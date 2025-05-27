package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.membership.RankHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Handles the Membership dialogue
 * 
 * @author JayBane
 *
 */
public class MembershipDialogue extends Dialogue {

	public MembershipDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {

	case DialogueConstants.OPTIONS_5_1:
		setNext(2);
		execute();
		break;
	case DialogueConstants.OPTIONS_5_2:
		stoner.send(new SendString(" ", 12000));
		break;
	case DialogueConstants.OPTIONS_5_3:
		setNext(3);
		execute();
		break;
	case DialogueConstants.OPTIONS_5_4:
		RankHandler.upgrade(stoner);
		break;
	case DialogueConstants.OPTIONS_5_5:
		stoner.send(new SendRemoveInterfaces());
		break;

	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {

	case 0:
		DialogueManager.sendNpcChat(stoner, 5523, Emotion.DEFAULT, "Hello stoner. Let me tell you about CannaCredits.");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Okay!", "Coming soon", "I have purchased something", "Update rank", "Nevermind");
		break;
	case 2:
		DialogueManager.sendNpcChat(stoner, 5523, Emotion.DEFAULT, "BestBudz cannacredits can be purchased on our online store.", "They can be used for buying items from my store ", "and many other features in game.", "Including purchasing cannacredits in the cannacredits tab.");
		setNext(1);
		break;
	case 3:
		DialogueManager.sendNpcChat(stoner, 5523, Emotion.DEFAULT, "Alright, let me check out the database...");
		next++;
		break;

	}

	}

}
