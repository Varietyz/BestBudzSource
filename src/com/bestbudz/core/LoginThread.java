package com.bestbudz.core;

import com.bestbudz.rs2.content.io.sqlite.StonerSave;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;

public class LoginThread extends Thread {

  private static final Queue<Stoner> login = new ConcurrentLinkedQueue<>();

  public LoginThread() {
    setName("Login Thread");
    setPriority(Thread.MAX_PRIORITY - 2);
    start();
  }

  public static void queueLogin(Stoner stoner) {
    login.add(stoner);
  }

  public static void cycle() {
    Stoner stoner;
    boolean processed = false;

    while ((stoner = login.poll()) != null) {
      handleLogin(stoner);
      processed = true;
    }

    if (!processed) {
      try {
        Thread.sleep(50); // Sleep briefly when idle
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

	private static void handleLogin(Stoner stoner) {
		System.out.println("Logging in: " + stoner.getUsername());

		boolean starter;
		try {
			starter = !StonerSave.load(stoner); // true if new player
		} catch (Exception e) {
			sendLoginError(stoner, 11);
			e.printStackTrace();
			return;
		}

		try {
			System.out.println("Login for " + stoner.getUsername() + " starter=" + starter);
			if (stoner.login(starter)) {
				stoner.getClient().setStage(Client.Stages.LOGGED_IN);
			}
		} catch (Exception e) {
			e.printStackTrace();
			stoner.logout(true);
		}
	}


  private static void sendLoginError(Stoner stoner, int code) {
    if (stoner == null || stoner.getClient() == null) return;

    StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
    resp.writeByte(code);
    resp.writeByte(0);
    resp.writeByte(0);
    stoner.getClient().send(resp.getBuffer());
  }

  @Override
  public void run() {
    while (!isInterrupted()) {
      cycle();
    }
  }
}
