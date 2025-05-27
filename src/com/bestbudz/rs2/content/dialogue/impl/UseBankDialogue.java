package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class UseBankDialogue extends Dialogue {

	public UseBankDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int button) {
	switch (button) {
	case DialogueConstants.OPTIONS_2_1:
		stoner.getBank().openBank();
		return true;
	case DialogueConstants.OPTIONS_2_2:
		stoner.start(new ShopExchangeDialogue(stoner));
		return true;
	}
	return false;
	}

	@Override
	public void execute() {
	DialogueManager.sendOption(stoner, "Open bank", "POS Options");
	}

}
