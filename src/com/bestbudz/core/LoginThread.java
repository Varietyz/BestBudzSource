package com.bestbudz.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.content.io.StonerSave;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;

public class LoginThread extends Thread {

	private static final Queue<Stoner> login = new ConcurrentLinkedQueue<Stoner>();

	public static void cycle() {
	long start = System.currentTimeMillis();

	Stoner stoner = null;

	if ((stoner = login.poll()) != null) {
		System.out.println("Logging in: " + stoner.getUsername());

		boolean starter = false;
		boolean wasLoaded = false;
		try {
			starter = !StonerSave.load(stoner);
			wasLoaded = true;
		} catch (Exception e) {
			if (stoner != null) {
				StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
				resp.writeByte(11);
				resp.writeByte(0);
				resp.writeByte(0);
				stoner.getClient().send(resp.getBuffer());
			}

			e.printStackTrace();
		}

		if (wasLoaded) {
			try {
				boolean login = stoner.login(starter);

				if (login) {
					stoner.getClient().setStage(Client.Stages.LOGGED_IN);
				}
			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

	}

	long elapsed = System.currentTimeMillis() - start;

	if (elapsed < 700L) {
		try {
			Thread.sleep(700L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} else {
		System.out.println("Login thread overflow: " + elapsed);
	}
	}

	public static void queueLogin(Stoner stoner) {
	login.add(stoner);
	}

	public LoginThread() {
	setName("Login Thread");

	setPriority(Thread.MAX_PRIORITY - 2);

	start();
	}

	@Override
	public void run() {
	while (!Thread.interrupted())
		cycle();
	}
}
