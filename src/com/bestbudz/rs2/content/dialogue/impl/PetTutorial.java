package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class PetTutorial extends Dialogue {
	public PetTutorial(Stoner stoner) {
	this.stoner = stoner;
	stoner.setController(Tutorial.TUTORIAL_CONTROLLER);
	}

	@Override
	public boolean clickButton(int id) {
	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendNpcChat(stoner, 6750, Emotion.CALM, new String[] { "Your pet will grow over time.", "But you must take care of it.", "Your pet will run away if you do not", "feed it every 45 minutes." });
		next += 1;
		break;
	case 1:
		stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		end();
	}
	}
}
