package com.bestbudz.rs2.content.io.sqlite;

import static com.bestbudz.Server.bossGroup;
import static com.bestbudz.Server.workerGroup;

import com.bestbudz.core.util.logger.StonerLogger;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.World;

public final class GracefulShutdownHook extends Thread {

	@Override
	public void run() {
		System.out.println("[Shutdown] Saving active players...");
		for (Stoner stoner : World.getStoners()) {
			if (stoner != null && stoner.isActive()) {
				StonerSave.save(stoner);
			}
		}

		SaveWorker.shutdownAndFlush();

		if (!SQLiteDB.isClosed()) {
			AntiRollbackManager.writeSnapshot();
			SQLiteDB.close();
		}

		StonerLogger.SHUTDOWN_LOGGER.log("Logs", String.format(
			"Server shutdown with %s online.", World.getActiveStoners()));

		try {
			if (bossGroup != null) bossGroup.shutdownGracefully().sync();
			if (workerGroup != null) workerGroup.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("[Shutdown] Player data, rollback, and DB flushed.");
	}

}
