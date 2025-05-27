package com.bestbudz;

import com.bestbudz.core.GameThread;
import com.bestbudz.core.util.logger.StonerLogger;
import com.bestbudz.rs2.content.clanchat.ClanManager;
import com.bestbudz.rs2.content.io.StonerSave;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Initializes the server
 * 
 * @author Jaybane
 * 
 */
public class Server {

	/**
	 * The logger for printing information.
	 */
	private static Logger logger = Logger.getLogger(Server.class.getSimpleName());

	/**
	 * Handles the clan chat.
	 */
	public static ClanManager clanManager = new ClanManager();

	/**
	 * Gets the BestBudz time
	 */
	public static String bestbudzTime() {
	return new SimpleDateFormat("HH:mm aa").format(new Date());
	}

	/**
	 * Gets the server date
	 */
	public static String bestbudzDate() {
	return new SimpleDateFormat("EEEE MMM dd yyyy ").format(new Date());
	}

	/**
	 * Gets the server uptime
	 * 
	 * @return
	 */
	public static String getUptime() {
	RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
	DateFormat df = new SimpleDateFormat("DD 'D', HH 'H', mm 'M'");
	df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
	return "" + df.format(new Date(mx.getUptime()));
	}

	/**
	 * The main method of the server that initializes everything
	 * 
	 * @param args
	 *                 The startup arguments
	 */
	public static void main(String[] args) {
	if (args != null && args.length > 0) {
		BestbudzConstants.DEV_MODE = Boolean.valueOf(args[0]);
	}

	logger.info("Development mode: " + (BestbudzConstants.DEV_MODE ? "Online" : "Offline") + ".");
	logger.info("Staff mode: " + (BestbudzConstants.STAFF_ONLY ? "Online" : "Offline") + ".");

	if (!BestbudzConstants.DEV_MODE) {
		try {
		} catch (Exception ex) {
			ex.printStackTrace();
		}


		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			for (Stoner stoners : World.getStoners()) {
				if (stoners != null && stoners.isActive()) {
					StonerSave.save(stoners);
				}
			}

			StonerLogger.SHUTDOWN_LOGGER.log("Logs", String.format("Server shutdown with %s online.", World.getActiveStoners()));
		}));
	}

	GameThread.init();
	}
}
