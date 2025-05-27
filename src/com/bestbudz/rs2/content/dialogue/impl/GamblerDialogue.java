package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Handles Gambler dialogue
 * 
 * @author Jaybane
 *
 */
public class GamblerDialogue extends Dialogue {

	public GamblerDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int id) {

	switch (id) {

	case DialogueConstants.OPTIONS_4_1:
		stoner.start(new FlowerGameDialogue(stoner));
		break;

	case DialogueConstants.OPTIONS_4_2:
		setNext(4);
		execute();
		break;

	case DialogueConstants.OPTIONS_4_3:
		setNext(2);
		execute();
		break;

	case DialogueConstants.OPTIONS_4_4:
		stoner.send(new SendRemoveInterfaces());
		break;

	}

	return false;
	}

	@Override
	public void execute() {

	switch (next) {

	case 0:
		DialogueManager.sendNpcChat(stoner, 1011, Emotion.HAPPY_TALK, "Hello " + stoner.getUsername() + "!", "I am in charge of all the money wasting done in BestBudz.");
		next++;
		break;

	case 1:
		DialogueManager.sendOption(stoner, "Flower Game", "Lottery", "Play 55x2", "Nevermind");
		break;

	case 2:
		DialogueManager.sendNpcChat(stoner, 1011, Emotion.HAPPY_TALK, "How much would you like to bet?");
		next++;
		break;

	case 3:
		stoner.setEnterXInterfaceId(56000);
		stoner.getClient().queueOutgoingPacket(new SendEnterString());
		break;

	case 4:
		stoner.start(new LotteryDialogue(stoner));
		break;

	}

	}

}