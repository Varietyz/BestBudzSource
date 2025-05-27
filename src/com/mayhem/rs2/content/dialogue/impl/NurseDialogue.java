package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.player.Player;

public class NurseDialogue extends Dialogue {
	
	public NurseDialogue(Player player) {
	this.player = player;
	}
	
	@Override
	public boolean clickButton(int id) {
		return false;
	}

	@Override
	public void execute() {
		switch(next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 6875, Emotion.HAPPY_TALK, "Hello Honey, What can I do for you?");
			next ++;
			break;
		case 1:
			DialogueManager.sendPlayerChat(player, Emotion.LONGER_LAUGHING_2, "Heal me please.");
			next ++;
			break;
		case 2:
			DialogueManager.sendNpcChat(player, 6875, Emotion.ANGRY_1, "Right click on me to heal yourself instantly");
			next ++;
			break;
		case 3:
			DialogueManager.sendPlayerChat(player, Emotion.SAD, "Alright, thanks!");
			end();
			break;
			
		}
	}

}
