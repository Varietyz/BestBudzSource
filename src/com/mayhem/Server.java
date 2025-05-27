package com.mayhem;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.mayhem.core.GameThread;
import com.mayhem.core.util.logger.PlayerLogger;
import com.mayhem.rs2.content.clanchat.ClanManager;
import com.mayhem.rs2.content.io.PlayerSave;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;


/**
 * Initializes the server
 * 
 * @author Michael Sasse
 * 
 */
public class Server {

	/** 
	 * The logger for printing information.
	 */
	public static Logger logger = Logger.getLogger(Server.class.getSimpleName());

	/**
	 * Handles the clan chat.
	 */
	public static ClanManager clanManager = new ClanManager();

	/**
	 * Gets the Mayhem time
	 */
	public static String MayhemTime() {
		return new SimpleDateFormat("HH:mm aa").format(new Date());
	}

	/**
	 * Gets the server date
	 */
	public static String MayhemDate() {
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
	 *            The startup arguments
	 */
	public static void main(String[] args) {
		
		  com.everythingrs.playersonline.PlayersOnline.service.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					int online = 0;
					for (Player player : World.getPlayers()) {
						if (player != null) {
							online += 1;
						}
					}
					com.everythingrs.playersonline.PlayersOnline.insert("secret_key", online, false);
				}
			}, 0, 30, java.util.concurrent.TimeUnit.SECONDS);
                      
		
		if (args != null && args.length > 0) {
			Constants.DEV_MODE = Boolean.valueOf(args[0]);
		}
		
		logger.info("Development mode: " + (Constants.DEV_MODE ? "Online" : "Offline") + ".");
		logger.info("Staff mode: " + (Constants.STAFF_ONLY ? "Online" : "Offline") + ".");

		if (!Constants.DEV_MODE) {
			try {
			//	MembershipRewards.prepare();
			//	new Motivote<Reward>(new RewardHandler(), "http://www.mayhem.org/vote/", "60db6b12").start();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				for (Player players : World.getPlayers()) {
					if (players != null && players.isActive()) {
						PlayerSave.save(players);
					}
				}

			//	MembershipRewards.shutdown();

				PlayerLogger.SHUTDOWN_LOGGER.log("Logs", String.format("Server shutdown with %s online.", World.getActivePlayers()));
			}));
		}

		GameThread.init();
	}
}
