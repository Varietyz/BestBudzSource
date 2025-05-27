package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueConstants;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * 
 * @author Faz
 *
 */
public class MacDialogue extends Dialogue {

	public MacDialogue(Player player) {
	this.player = player;
	}

	public final int MAX_CAPE_ID = 13280;
	public final int MAX_HOOD_ID = 13281;

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case DialogueConstants.OPTIONS_4_1:
		player.send(new SendRemoveInterfaces());
		setNext(3);
		execute();
		break;
	case DialogueConstants.OPTIONS_4_2:
		setNext(6);
		execute();
		break;
	case DialogueConstants.OPTIONS_4_3:
		setNext(16);
		execute();
		break;

	case DialogueConstants.OPTIONS_4_4:
		player.send(new SendRemoveInterfaces());
		break;

	case DialogueConstants.OPTIONS_2_1:
		
		if (player.getSkill().getTotalLevel() != 2178) {
			DialogueManager.sendNpcChat(player, 6481, Emotion.DEFAULT, "You don't have the requirements to buy this item!");
			end();
			return false;
		}
		if (!player.getInventory().hasItemAmount(995, 5000000)) {
			DialogueManager.sendNpcChat(player, 6481, Emotion.DEFAULT, "You do not have enough money to buy the cape");
			end();
			return false;
		}

		
		player.getInventory().remove(995, 5000000);
		player.getInventory().add(MAX_CAPE_ID, 1);
		player.getInventory().add(MAX_HOOD_ID, 1);
		player.send(new SendRemoveInterfaces());
		break;

	case DialogueConstants.OPTIONS_2_2:
		player.send(new SendRemoveInterfaces());
		break;

	}

	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendPlayerChat(player, Emotion.CALM, "Hello");
		next++;
		break;
	case 1:
		DialogueManager.sendStatement(player, new String[] { "The man glances at you and grunts something unintelligent" });
		next++;
		break;
	case 2:
		DialogueManager.sendOption(player, "Who are you?", "What do you have in your sack?", "Why are you so dirty?", "Bye.");
		break;
	case 3:
		DialogueManager.sendPlayerChat(player, Emotion.CALM, "Who are you?");
		next++;
		break;
	case 4:
		DialogueManager.sendNpcChat(player, 6481, Emotion.ANGRY_1, "Mac. What's it to you?");
		next++;
		break;
	case 5:
		DialogueManager.sendPlayerChat(player, Emotion.CALM, "Only trying to be friendly.");
		setNext(2);
		break;
	case 6:	
		DialogueManager.sendPlayerChat(player, Emotion.CALM, "What do you have in your sack?");
		next++;
		break;
	case 7:
		DialogueManager.sendNpcChat(player, 6481, Emotion.CALM, "S'me cape.");
		next++;
		break;
	case 8:
		DialogueManager.sendPlayerChat(player, Emotion.CALM, "Your cape?");
		next++;
		break;
	case 9:
		DialogueManager.sendPlayerChat(player, Emotion.CALM, "Can I have it?");
		next++;
		break;
	case 10:
		DialogueManager.sendNpcChat(player, 6481, Emotion.CALM, "Mebe");
		next++;
		break;
	case 11:
		DialogueManager.sendPlayerChat(player, Emotion.HAPPY, "I'm sure I could make it worth your while.");
		next++;
		break;
	case 12:
		DialogueManager.sendNpcChat(player, 6481, Emotion.HAPPY, "How much?");
		next++;
		break;
	case 13:
		DialogueManager.sendPlayerChat(player, Emotion.HAPPY, "How about 5,000,000 gold?");
		next++;
		break;
	case 14:
		DialogueManager.sendOption(player, "Yes, pay the man.", "No");
		break;
	case 15:
		DialogueManager.sendPlayerChat(player, Emotion.CALM, "Why are you so dirty?");
		next++;
		break;
	case 16:
		DialogueManager.sendNpcChat(player, 6481, Emotion.CALM, "Bath XP waste.");
		setNext(2);
		break;

	}

	}

	private static final int[][] MAXCAPE_ITEM_IDS = { { 13280, 13281, 6570, 13329, 13330 }, { 13280, 13281, 2412, 13331, 13332 }, { 13280, 13281, 2414, 13333, 13334 }, { 13280, 13281, 2413, 13335, 13336 }, { 13280, 13281, 10499, 13337, 13338 } };

	public static void maxCapeCombining(Player client, int itemUsed, int useWith) {
	for (int m = 0; m < MAXCAPE_ITEM_IDS.length; m++) {
		if (itemUsed == MAXCAPE_ITEM_IDS[m][0] && useWith == MAXCAPE_ITEM_IDS[m][2] || itemUsed == MAXCAPE_ITEM_IDS[m][1] && useWith == MAXCAPE_ITEM_IDS[m][2] || itemUsed == MAXCAPE_ITEM_IDS[m][2] && useWith == MAXCAPE_ITEM_IDS[m][0] || itemUsed == MAXCAPE_ITEM_IDS[m][2] && useWith == MAXCAPE_ITEM_IDS[m][1]) {
			if (!client.getInventory().hasItemAmount(MAXCAPE_ITEM_IDS[m][0], 1) || !client.getInventory().hasItemAmount(MAXCAPE_ITEM_IDS[m][1], 1) || !client.getInventory().hasItemAmount(MAXCAPE_ITEM_IDS[m][2], 1)) {
				client.getClient().queueOutgoingPacket(new SendMessage("Make sure you have both 'Max cape' and 'Max hood' in your inventory."));
				return;
			}
			int item3 = MAXCAPE_ITEM_IDS[m][0];
			int item4 = MAXCAPE_ITEM_IDS[m][1];
			int item5 = MAXCAPE_ITEM_IDS[m][2];
			client.getInventory().remove(item3, client.getInventory().getItemSlot(item3));
			client.getInventory().remove(item4, client.getInventory().getItemSlot(item4));
			client.getInventory().remove(item5, client.getInventory().getItemSlot(item5));
			int item6 = MAXCAPE_ITEM_IDS[m][3];
			int item7 = MAXCAPE_ITEM_IDS[m][4];
			client.getInventory().add(item6, 1);
			client.getInventory().add(item7, 1);
			return;
		}
	}
	}
}