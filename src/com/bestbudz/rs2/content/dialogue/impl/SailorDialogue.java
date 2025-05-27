package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class SailorDialogue extends Dialogue {

	public SailorDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	private final int COST = 2_500;

	public boolean can() {
	if (stoner.isPouchPayment()) {
		if (stoner.getMoneyPouch() < COST) {
			return false;
		} else {
			stoner.setMoneyPouch(stoner.getMoneyPouch() - COST);
			stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
			return true;
		}
	} else {
		if (!stoner.getBox().hasItemAmount(995, COST)) {
			return false;
		} else {
			stoner.getBox().remove(995, COST);
			return true;
		}
	}
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case DialogueConstants.OPTIONS_5_1:
		if (can()) {
			stoner.getMage().teleport(2948, 3147, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	case DialogueConstants.OPTIONS_5_2:
		if (can()) {
			stoner.getMage().teleport(2964, 3378, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	case DialogueConstants.OPTIONS_5_3:
		if (can()) {
			stoner.getMage().teleport(2662, 3305, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	case DialogueConstants.OPTIONS_5_4:
		if (can()) {
			stoner.getMage().teleport(2569, 3098, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	case DialogueConstants.OPTIONS_5_5:
		next = 2;
		execute();
		break;
	case DialogueConstants.OPTIONS_4_1:
		if (can()) {
			stoner.getMage().teleport(2708, 3492, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	case DialogueConstants.OPTIONS_4_2:
		if (can()) {
			stoner.getMage().teleport(3093, 3244, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	case DialogueConstants.OPTIONS_4_3:
		if (can()) {
			stoner.getMage().teleport(3210, 3424, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	case DialogueConstants.OPTIONS_4_4:
		if (can()) {
			stoner.getMage().teleport(2827, 2995, 0, TeleportTypes.SPELL_BOOK);
		} else {
			DialogueManager.sendNpcChat(stoner, 3936, Emotion.ANNOYED, "You do not have " + COST + " bestbucks for this!");
			next = 0;
		}
		break;
	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendNpcChat(stoner, 3936, Emotion.HAPPY_TALK, "Hello stoner!", "Are you interested in traveling the lands of BestBudz?", "I can take you places, for " + COST + " bestbucks!");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Karamja", "Falador", "Ardougne", "Yanille", "Next");
		break;
	case 2:
		DialogueManager.sendOption(stoner, "Seer's Village", "Draynor", "Varrock", "Shilo Village");
		break;
	}
	}

}
