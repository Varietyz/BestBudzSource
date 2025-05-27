package com.mayhem.rs2.content.minigames.miniraid;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.ControllerManager;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Mini Raid
 * @author Mod Divine
 *
 */
public class MiniRaid {
	
	/**
	 * List of games
	 */
	private static final List<MiniRaidGame> games = new LinkedList<MiniRaidGame>();

	/**
	 * List of players waiting
	 */
	private static final Queue<Player> waiting = new ArrayDeque<Player>();

	/**
	 * Time
	 */
	private static short time = 120;

	/**
	 * Handles object clicking
	 * @param player
	 * @param id
	 * @return
	 */
	public static boolean clickObject(Player player, int id) {
		switch (id) {
		case 7322:
			if (!player.getController().equals(ControllerManager.MINI_RAID_WAITING_ROOM_CONTROLLER)) {
				player.setController(ControllerManager.MINI_RAID_WAITING_ROOM_CONTROLLER);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Welcome to the Trial of Flames!"));
				player.teleport(new Location(2799, 5166));

				if (!waiting.contains(player)) {
					waiting.add(player);
				}
			}
			return true;
		case 7324:
			if (!player.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
				player.setController(ControllerManager.DEFAULT_CONTROLLER);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You have left the Queue for the Trials."));
				player.teleport(new Location(3086, 3493, 0));
				waiting.remove(player);
			}
			return true;
			
		case 13878:
			if (!player.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
				player.setController(ControllerManager.DEFAULT_CONTROLLER);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You have left the Trial of Flames."));
				player.teleport(new Location(3086, 3493, 0));
				waiting.remove(player);
			} return true;
		}

		return false;
	}

	/**
	 * Get the minutes till raid starts
	 * @return
	 */
	public static int getMinutesTillDepart() {
		return time;
	}

	/**
	 * Gets the ready players
	 * @return
	 */
	public static int getPlayersReady() {
		return waiting.size();
	}

	/**
	 * Game ending
	 * @param game
	 */
	public static void onGameEnd(MiniRaidGame game) {
		games.remove(game);
	}

	/**
	 * Sends message to players waiting
	 * @param message
	 */
	public static void sendMessageToWaiting(String message) {
		for (Player p : waiting) {
			p.getClient().queueOutgoingPacket(new SendMessage(message));
		}
	}

	/**
	 * Starts the mini raid
	 */
	public static void startGame() {

		if (waiting.size() < 2) {
			sendMessageToWaiting("There are not enough players to start.");
			return;
		}

		if (games.size() == 100) {
			sendMessageToWaiting("There are too many active Trials right now, Please wait.");
			return;
		}

		List<Player> toPlay = new LinkedList<Player>();

		int playing = 0;
		Player p;

		while ((playing < 10) && ((p = waiting.poll()) != null)) {
			toPlay.add(p);
			playing++;
		}

		if (waiting.size() > 0) {
			for (Player k : waiting) {
				k.getClient().queueOutgoingPacket(new SendMessage("You couldn't be added to the last Trial of Flames raid, you've moved up in priority for the next one."));
			}
		}

		games.add(new MiniRaidGame(toPlay, toPlay.get(0).getIndex() << 2));
	}

	/**
	 * Process
	 */
	public static void tick() {
		if (waiting.size() > 0) {
			time--;

			if (time == 0 || waiting.size() == 10) {
				startGame();
				time = 120;
			}
		} else if (time != 120) {
			time = 120;
		}

		if (games.size() > 0) {
			for (Iterator<MiniRaidGame> i = games.iterator(); i.hasNext();) {
				i.next().process();
			}
		}
	}
	
}
