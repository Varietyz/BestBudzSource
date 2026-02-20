package com.bestbudz.core;

import com.bestbudz.core.discord.DiscordManager;
import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.content.io.sqlite.StonerSave;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChatBridgeManager;
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
		int processed = 0;
		final int MAX_LOGINS_PER_CYCLE = 3;

		while ((stoner = login.poll()) != null && processed < MAX_LOGINS_PER_CYCLE) {
			handleLogin(stoner);
			processed++;
		}

		if (processed == 0) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} else if (processed >= MAX_LOGINS_PER_CYCLE) {

			Thread.yield();
		}
	}

	private static void handleLogin(Stoner stoner) {

		if (isDiscordBot(stoner)) {
			handleDiscordBotLogin(stoner);
			return;
		}

		System.out.println("Logging in: " + stoner.getUsername());
		ChatBridgeManager.notifyPlayerJoin(stoner.getUsername());

		boolean starter;
		try {
			starter = !StonerSave.load(stoner);
		} catch (Exception e) {
			sendLoginError(stoner, 11);
			e.printStackTrace();
			return;
		}

		try {
			System.out.println("Login for " + stoner.getUsername() + " starter=" + starter);
			if (stoner.login(starter)) {
				stoner.getClient().setStage(Client.Stages.LOGGED_IN);

				if (!isDiscordBot(stoner)) {
					DiscordManager.getInstance().onPlayerCountChanged();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			stoner.logout(true);
		}
	}

	private static boolean isDiscordBot(Stoner stoner) {
		return DEFAULT_USERNAME.equals(stoner.getUsername()) ||
			stoner instanceof DiscordBotStoner;
	}

	private static void handleDiscordBotLogin(Stoner stoner) {
		try {
			System.out.println("Fast-track login for Discord bot");

			if (stoner.login(false)) {
				stoner.getClient().setStage(Client.Stages.LOGGED_IN);
				System.out.println("Discord bot logged in successfully with minimal overhead");
			}
		} catch (Exception e) {
			System.err.println("Discord bot login failed: " + e.getMessage());
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
			try {
				cycle();
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}
}
