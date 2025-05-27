package com.bestbudz.rs2.content.minigames.f2parena;

import java.util.ArrayDeque;
import java.util.Queue;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles the F2P Arena Minigame Arena where stoners may only use F2P weapon,
 * armour, and gear.
 * 
 * @author Jaybane
 *
 */
public class F2PArena {
	/**
	 * Holds the game stoners
	 */
	public static Queue<Stoner> gameStoners = new ArrayDeque<Stoner>();

	/**
	 * Handles entering minigame
	 * 
	 * @param stoner
	 */
	public static void enterGame(Stoner stoner) {
	if (stoner.getBossPet() != null) {
		DialogueManager.sendStatement(stoner, "You can't bring a pet into this game!");
		return;
	}
	if (gameStoners.contains(stoner)) {
		return;
	}
	DialogueManager.sendInformationBox(stoner, "F2P Arena", "Welcome to the @blu@F2P Arena@bla@!", "There are currently @blu@" + gameStoners.size() + " @bla@members playing.", "Objective: @blu@Kill as many stoners as possible@bla@.", "To leave click on the @blu@portal@bla@.");
	stoner.setController(ControllerManager.F2P_ARENA_CONTROLLER);
	stoner.teleport(Utility.randomElement(F2PArenaConstants.RESPAWN_LOCATIONS));
	gameStoners.add(stoner);
	}

	/**
	 * Handles leaving minigame
	 * 
	 * @param stoner
	 */
	public static void leaveGame(Stoner stoner) {
	if (!gameStoners.contains(stoner)) {
		return;
	}
	gameStoners.remove(stoner);
	}

	/**
	 * Sends message to all game stoners
	 * 
	 * @param message
	 */
	public static void messageStoners(String message) {
	for (Stoner stoners : gameStoners) {
		stoners.send(new SendMessage(message));
	}
	}

}
