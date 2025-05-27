package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.Advance;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for Advance
 * 
 * @author Jaybane
 *
 */
public class AdvanceDialogue extends Dialogue {

	public AdvanceDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case DialogueConstants.OPTIONS_4_1:
		stoner.send(new SendRemoveInterfaces());
		setNext(5);
		execute();
		break;
	case DialogueConstants.OPTIONS_4_2:
		stoner.send(new SendRemoveInterfaces());
		stoner.getShopping().open(93);
		break;
	case DialogueConstants.OPTIONS_4_3:
		Advance.update(stoner);
		stoner.send(new SendInterface(51000));
		break;
	case DialogueConstants.OPTIONS_4_4:
		break;

	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendNpcChat(stoner, 606, Emotion.HAPPY, "Hello " + stoner.getUsername() + ".", "I am the advance master.", "How may I help you?");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Tell me more about advances", "I would like to trade", "I would like to advance", "Nevermind");
		break;
	case 5:
		DialogueManager.sendNpcChat(stoner, 606, Emotion.HAPPY, "The prestiging system is quite simple.");
		next++;
		break;
	case 6:
		DialogueManager.sendNpcChat(stoner, 606, Emotion.HAPPY, "When you have reached the grade 99 in any profession", "you will have the ability to Advance it.");
		next++;
		break;
	case 7:
		DialogueManager.sendNpcChat(stoner, 606, Emotion.HAPPY, "By doing so you will reset your experience to 0.", "In return you will be rewarded with bestbucks as well as your", "profession being a different color representing the advance grade.");
		setNext(1);
		break;
	}
	}

}
