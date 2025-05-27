package com.mayhem.rs2.content.quest;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.player.Player;

public class StarterMiniQuest extends Dialogue {
	
	public StarterMiniQuest(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 2914, Emotion.HAPPY_TALK, "Hello @blu@+ player.getUsername() +</col>.", "Thank Saradomin you're here!", "We're in need of an Adenturer.");
			next++;
			break;
			
		case 1:
			DialogueManager.sendNpcChat(player, 2914, Emotion.HAPPY_TALK, "I will reward you for helping me.");
			next++;
			break;
			
		case 2:
			DialogueManager.sendOption(player, "Alright, what do you need done?", "I don't have time to help right now.");
			break;
		
		}
		
	}

}
