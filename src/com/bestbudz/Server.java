package com.bestbudz;

import com.bestbudz.core.GameThread;
import com.bestbudz.rs2.content.clanchat.ClanManager;
import com.bestbudz.rs2.content.io.sqlite.AntiRollbackManager;
import com.bestbudz.rs2.content.io.sqlite.GracefulShutdownHook;

import com.bestbudz.rs2.content.io.sqlite.SQLiteDB;
import io.netty.channel.EventLoopGroup;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class Server {

	public static final Logger logger = Logger.getLogger(Server.class.getSimpleName());
	public static EventLoopGroup bossGroup;
	public static EventLoopGroup workerGroup;
	public static final ClanManager clanManager = new ClanManager();

	public static String bestbudzDate() {
		return new SimpleDateFormat("EEEE MMM dd yyyy ").format(new Date());
	}

	public static void main(String[] args) throws SQLException, IOException
	{
		System.out.println(">>> [DEBUG] Server.main() has started");
		SQLiteDB.init(); // must run before any save/load
		AntiRollbackManager.readSnapshot(); // ✅ Load rollback token cache before anything else
		Runtime.getRuntime().addShutdownHook(new GracefulShutdownHook());

		if (args != null && args.length > 0) {
			BestbudzConstants.DEV_MODE = Boolean.parseBoolean(args[0]);
		}

		logger.info("Development mode: " + (BestbudzConstants.DEV_MODE ? "Online" : "Offline") + ".");
		logger.info("Staff mode: " + (BestbudzConstants.STAFF_ONLY ? "Online" : "Offline") + ".");

		GameThread.init();
	}
}
