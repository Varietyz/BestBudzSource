package com.mayhem.rs2.content.dialogue;

import java.util.HashMap;

import com.mayhem.rs2.entity.player.Player;

public class OneLineDialogue {

	private static final HashMap<Integer, String> idsForChat = new HashMap<Integer, String>();

	public static void declare() {
		idsForChat.put(462, "Welcome to the Mages' guild!");
		idsForChat.put(553, "Hello there, I've got all kinds of magical supply!");
		idsForChat.put(7303, "Hello! I can sell you @blu@Recolored items</col>.");
	}

	public static boolean doOneLineChat(Player player, int id) {
		if (idsForChat.containsKey(id)) {
			DialogueManager.sendNpcChat(player, id, Emotion.HAPPY, idsForChat.get(id));
			return true;
		}
		return false;
	}

}
