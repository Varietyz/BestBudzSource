package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Handles the Mercenary dialogue
 * 
 * @author Jaybane
 *
 */
public class VannakaDialogue extends Dialogue {

	/**
	 * Vannaka Dialogue
	 * 
	 * @param stoner
	 */
	public VannakaDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {

	case DialogueConstants.OPTIONS_3_1:
		if (stoner.getMercenary().hasTask()) {
			DialogueManager.sendNpcChat(stoner, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
			break;
		}
		stoner.getMercenary().assign(Mercenary.MercenaryDifficulty.LOW);
		next = 6;
		execute();
		break;

	case DialogueConstants.OPTIONS_3_2:
		if (stoner.getMercenary().hasTask()) {
			DialogueManager.sendNpcChat(stoner, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
			break;
		}
		stoner.getMercenary().assign(Mercenary.MercenaryDifficulty.MEDIUM);
		next = 6;
		execute();
		break;

	case DialogueConstants.OPTIONS_3_3:
		if (stoner.getMercenary().hasTask()) {
			DialogueManager.sendNpcChat(stoner, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
			break;
		}
		stoner.getMercenary().assign(Mercenary.MercenaryDifficulty.HIGH);
		next = 6;
		execute();
		break;

	case DialogueConstants.OPTIONS_4_1:
		if (stoner.getMercenary().hasTask()) {
			DialogueManager.sendNpcChat(stoner, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
		} else {
			setNext(4);
			execute();
		}
		break;

	case DialogueConstants.OPTIONS_4_2:
		stoner.send(new SendRemoveInterfaces());
		setNext(2);
		execute();
		break;

	case DialogueConstants.OPTIONS_4_3:
		if (!stoner.getMercenary().hasTask()) {
			DialogueManager.sendStatement(stoner, new String[] { "A mercenary task is required to reset it!" });
			end();
		} else if (!stoner.getBox().hasItemAmount(995, 250000)) {
			DialogueManager.sendStatement(stoner, new String[] { "@blu@250,000</col> bestbucks is required to do this;", "which you do not have!" });
			end();
		} else {
			stoner.getMercenary().reset();
			DialogueManager.sendStatement(stoner, new String[] { "You have reset your task for @blu@250,000 </col>bestbucks." });
			stoner.getBox().remove(995, 250000, true);
			InterfaceHandler.writeText(new QuestTab(stoner));
			end();
		}
		break;

	case DialogueConstants.OPTIONS_4_4:
		stoner.getShopping().open(6);
		break;

	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {

	case 0:
		DialogueManager.sendNpcChat(stoner, 403, Emotion.HAPPY_TALK, "Hello stoner!", "I am Vannaka, master of mercenary.", "How may I assist you today?");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Obtain task", "Set co-op mercenary partner", "Reset my task", "Trade");
		break;
	case 2:
		DialogueManager.sendNpcChat(stoner, 403, Emotion.HAPPY_TALK, "Coming soon!");
		// DialogueManager.sendNpcChat(stoner, 403, Emotion.HAPPY_TALK, "Please enter
		// the stoner's username you want to pair up with.");
		// next ++;
		break;
	case 3:
		end();
		stoner.setEnterXInterfaceId(100);
		stoner.getClient().queueOutgoingPacket(new SendEnterString());
		break;
	case 4:
		DialogueManager.sendNpcChat(stoner, 403, Emotion.HAPPY, "Please select the grade of difficulty.");
		next++;
		break;
	case 5:
		DialogueManager.sendOption(stoner, "Easy", "Medium", "Hard");
		break;
	case 6:
		String task = stoner.getMercenary().getTask();
		byte am = stoner.getMercenary().getAmount();
		DialogueManager.sendNpcChat(stoner, 403, Emotion.CALM, new String[] { "You have been assigned the task of killing:", "@blu@" + am + " " + task, });
		end();
		break;

	}
	}

}
