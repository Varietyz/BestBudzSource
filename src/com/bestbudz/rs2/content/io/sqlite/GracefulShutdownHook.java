package com.bestbudz.rs2.content.io.sqlite;

import static com.bestbudz.Server.bossGroup;
import static com.bestbudz.Server.workerGroup;

import com.bestbudz.core.discord.DiscordManager;
import com.bestbudz.core.discord.core.DiscordServerIntegration;
import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import com.bestbudz.core.util.logger.StonerLogger;
import com.bestbudz.rs2.content.profession.petmaster.db.PetMasterDatabase;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.World;

public final class GracefulShutdownHook extends Thread {

	@Override
	public void run() {
		System.out.println("[Shutdown] Saving active players...");

		// Send shutdown notification FIRST (before anything else)
		// This ensures Discord gets the message before services shut down
		try {
			DiscordMessageManager.announceServerShutdown();
			System.out.println("[Shutdown] Server shutdown notification sent to Discord");

			// Give Discord time to send the message
			Thread.sleep(2000);
		} catch (Exception e) {
			System.err.println("[Shutdown] Failed to send shutdown notification: " + e.getMessage());
		}

		for (Stoner stoner : World.getStoners()) {
			if (stoner != null && stoner.isActive()) {
				StonerSave.save(stoner);
			}
		}

		// IMPORTANT: Save Discord bot state before shutdown
		System.out.println("[Shutdown] Saving Discord bot state...");
		try {
			if (DiscordManager.getInstance().isActive() && DiscordManager.getInstance().getBotPlayer() != null) {
				DiscordManager.getInstance().getBotPlayer().getBotPersistence().forceSaveBotState();
				System.out.println("[Shutdown] Discord bot state saved successfully");
			}
		} catch (Exception e) {
			System.err.println("[Shutdown] Failed to save Discord bot state: " + e.getMessage());
			e.printStackTrace();
		}

		SaveWorker.shutdownAndFlush();

		if (!SQLiteDB.isClosed()) {
			AntiRollbackManager.writeSnapshot();
			SQLiteDB.close();
			PetMasterDatabase.getInstance().shutdown();
		}

		StonerLogger.SHUTDOWN_LOGGER.log("Logs", String.format(
			"Server shutdown with %s online.", World.getActiveStoners()));

		// Shutdown Discord services in proper order
		try {
			System.out.println("[Shutdown] Shutting down Discord services...");

			// Shutdown Discord manager first
			DiscordManager.getInstance().shutdown();

			// Then shutdown the integration (this will handle any remaining cleanup)
			DiscordServerIntegration.shutdownDiscordBot();

			System.out.println("[Shutdown] Discord services shut down successfully");
		} catch (Exception e) {
			System.err.println("[Shutdown] Error during Discord shutdown: " + e.getMessage());
			e.printStackTrace();
		}

		try {
			if (bossGroup != null) bossGroup.shutdownGracefully().sync();
			if (workerGroup != null) workerGroup.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("[Shutdown] Player data, Discord bot data, rollback, and DB flushed.");
	}
}