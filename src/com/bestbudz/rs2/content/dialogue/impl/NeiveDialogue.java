package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class NeiveDialogue extends Dialogue {

	public NeiveDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {

	case DialogueConstants.OPTIONS_3_1:
		if (stoner.getCredits() < 2) {
			DialogueManager.sendNpcChat(stoner, 490, Emotion.DEFAULT, "You do not have enough cannacredits to do this!");
			end();
			return false;
		}
		if (stoner.getMercenary().hasTask()) {
			DialogueManager.sendNpcChat(stoner, 490, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
			return false;
		}
		stoner.setCredits(stoner.getCredits() - 2);
		stoner.getMercenary().assign(Mercenary.MercenaryDifficulty.BOSS);
		next = 2;
		execute();
		break;

	case DialogueConstants.OPTIONS_3_2:
		stoner.send(new SendRemoveInterfaces());
		break;

	case DialogueConstants.OPTIONS_3_3:
		if (!stoner.getMercenary().hasTask()) {
			DialogueManager.sendNpcChat(stoner, 490, Emotion.DEFAULT, "A mercenary task is required to reset it!");
			end();
			return false;
		}
		if (!stoner.getBox().hasItemAmount(995, 250_000)) {
			DialogueManager.sendNpcChat(stoner, 490, Emotion.DEFAULT, "You do not have enough BestBucks to do this!");
			end();
			return false;
		}
		stoner.getBox().remove(995, 250_000);
		stoner.getMercenary().reset();
		DialogueManager.sendNpcChat(stoner, 490, Emotion.DEFAULT, "Your current task has been reset!");
		end();
		break;

	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {

	case 0:
		DialogueManager.sendNpcChat(stoner, 490, Emotion.DEFAULT, "Hello my elite friend.", "I can give you a boss task.", "It will cost you 2 cannacredits.", "Are you interested?");
		next++;
		break;

	case 1:
		DialogueManager.sendOption(stoner, "Yes", "No", "Reset current task (@dre@250k</col>)");
		break;

	case 2:
		String task = stoner.getMercenary().getTask();
		byte am = stoner.getMercenary().getAmount();
		DialogueManager.sendNpcChat(stoner, 490, Emotion.CALM, new String[] { "You have been assigned the task of killing:", "@dre@" + am + " " + task, });
		end();
		break;

	}
	}

}
