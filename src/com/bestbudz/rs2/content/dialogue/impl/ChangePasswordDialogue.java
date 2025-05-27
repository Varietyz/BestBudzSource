package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class ChangePasswordDialogue extends Dialogue {
	private final String password;

	public ChangePasswordDialogue(Stoner stoner, String password) {
	this.stoner = stoner;
	this.password = password;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case 9157:
		stoner.setPassword(password);
		DialogueManager.sendStatement(stoner, new String[] { "Your password will now be:", "'" + password + "'" });
		end();
		return true;
	case 9158:
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		return true;
	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendStatement(stoner, new String[] { "Your new password will be:", "'" + password + "'", "Are you sure you want to make this change?" });
		next += 1;
		break;
	case 1:
		DialogueManager.sendOption(stoner, new String[] { "Yes.", "No." });
	}
	}
}
