package com.bestbudz.core;

import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
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
		int processed = 0;

		while ((obj = ObjectManager.getSend().poll()) != null && processed < 50) {
			try {
				ObjectManager.send(obj);
				processed++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processStoners() {
		int processed = 0;

		for (Stoner s : World.getStoners()) {
			if (s == null || !s.isActive()) continue;

			try {

				if (isDiscordBot(s)) {

					continue;
				}

				s.getGroundItems().process();
				s.getObjects().process();
				s.getClient().processOutgoingPackets();

				processed++;

				if (processed % 10 == 0) {
					Thread.yield();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isDiscordBot(Stoner stoner) {
		return DEFAULT_USERNAME.equals(stoner.getUsername()) ||
			stoner instanceof DiscordBotStoner;
	}

	private void sleepIfNeeded(long start) {
		long elapsed = (System.nanoTime() - start) / 1_000_000;

		int realPlayerCount = World.getRealStonerCount();
		long targetCycleTime = realPlayerCount > 50 ? 150 : 200;

		if (elapsed < targetCycleTime) {
			try {
				long sleepTime = targetCycleTime - elapsed;

				if (sleepTime <= 5) {
					Thread.yield();
				} else {
					Thread.sleep(sleepTime);
				}

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} else if (elapsed > targetCycleTime * 2) {

			System.out.println("Network thread overflow: " + elapsed + "ms (target: " + targetCycleTime + "ms, real players: " + realPlayerCount + ")");
		}
	}

	@Override
	public void run() {
		long lastOptimizationCheck = System.currentTimeMillis();

		while (!Thread.interrupted()) {
			long start = System.nanoTime();

			try {
				processObjectQueue();
				processStoners();

				long currentTime = System.currentTimeMillis();
				if (currentTime - lastOptimizationCheck > 30000) {
					optimizeIfNeeded();
					lastOptimizationCheck = currentTime;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			sleepIfNeeded(start);
			cycles++;
		}
	}

	private void optimizeIfNeeded() {
		int realPlayerCount = World.getRealStonerCount();

		if (realPlayerCount > 100) {
			System.gc();
		}

		if (cycles % 1000 == 0) {
			System.out.println("Network thread: " + cycles + " cycles, " + realPlayerCount + " real players, " + World.getStonerCount() + " total entities");
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
