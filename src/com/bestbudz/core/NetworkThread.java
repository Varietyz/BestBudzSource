package com.bestbudz.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class NetworkThread extends Thread {

  public static final String PACKET_LOG_DIR = "./data/logs/packets/";
  private static final Queue<PacketLog> packetLog = new ConcurrentLinkedQueue<PacketLog>();
  public static NetworkThread singleton;
  public static int cycles = 0;

  public NetworkThread() {
    singleton = this;

    setName("Network Thread");

    setPriority(Thread.MAX_PRIORITY - 1);

    start();
  }

  public static void createLog(String username, IncomingPacket packet, int opcode) {
    packetLog.add(new PacketLog(username, packet.getClass().getSimpleName() + " : " + opcode));
  }

  public static NetworkThread getSingleton() {
    return singleton;
  }

  private void processObjectQueue() {
    GameObject obj;
    while ((obj = ObjectManager.getSend().poll()) != null) {
      try {
        ObjectManager.send(obj);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void processStoners() {
    for (Stoner s : World.getStoners()) {
      if (s == null || !s.isActive()) continue;

      try {
        s.getGroundItems().process();
        s.getObjects().process();
        s.getClient().processOutgoingPackets();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void sleepIfNeeded(long start) {
    long elapsed = (System.nanoTime() - start) / 1_000_000;
    if (elapsed < 200) {
      try {
        Thread.sleep(200 - elapsed);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Correct way to restore interrupt flag
      }
    } else {
      System.out.println("Network thread overflow: " + elapsed + "ms");
    }
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      long start = System.nanoTime();

      processObjectQueue();
      processStoners();

      sleepIfNeeded(start);
    }
  }

  public static class PacketLog {
    public final String username;
    public final String packet;

    public PacketLog(String username, String packet) {
      this.username = username;
      this.packet = packet;
    }
  }
}
