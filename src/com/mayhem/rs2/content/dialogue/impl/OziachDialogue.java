package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueConstants;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Oziach dialogue
 * @author Daniel
 *
 */
public class OziachDialogue extends Dialogue {
	
	public OziachDialogue(Player player) {
		this.player = player;
	}
	
	private int amount = 15_000_000;
	
	public void makeShield() {
		if (player.isPouchPayment()) {
			if (player.getMoneyPouch() < amount) {
				DialogueManager.sendNpcChat(player, 822, Emotion.DEFAULT, "You need 15,000,000 coins to do this!");
				return;
			}
		} else {
			if (!player.getInventory().hasItemAmount(995, amount)) {
				DialogueManager.sendNpcChat(player, 822, Emotion.DEFAULT, "You need 15,000,000 coins to do this!");
				return;
			}
		}

		if (!player.getInventory().hasItemAmount(11286, 1)) {
			DialogueManager.sendNpcChat(player, 822, Emotion.DEFAULT, "You need a " + GameDefinitionLoader.getItemDef(11286).getName() + ".");
			return;
		}
		
    	if (player.isPouchPayment()) {
    		player.setMoneyPouch(player.getMoneyPouch() - amount);
    		player.send(new SendString(player.getMoneyPouch() + "", 8135));
    	} else {
    		player.getInventory().remove(995, amount);    		
    	}
		
		
		player.getInventory().remove(11286, 1);
		player.getInventory().add(11283, 1);
		DialogueManager.sendNpcChat(player, 822, Emotion.DEFAULT, "Have fun with your new shield!");
		return;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		case DialogueConstants.OPTIONS_2_1:
			setNext(2);
			execute();
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
			DialogueManager.sendNpcChat(player, 822, Emotion.DEFAULT, "Hello there!", "How may I help you?");
			next ++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Can make me a dragonfire shield?", "Nothing.");
			break;
		case 2:
			DialogueManager.sendNpcChat(player, 822, Emotion.DEFAULT, "Yes of course!", "It will cost 2m coins.", "You also need a Dragonic visage.");
			next ++;
			break;
		case 3:
			makeShield();
			break;
			
		}
	}

}
