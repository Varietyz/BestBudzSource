package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public abstract class ConfirmDialogue extends Dialogue {

	private final String[] confirm;

	public ConfirmDialogue(Stoner stoner) {
	this.stoner = stoner;
	confirm = null;
	}

	public ConfirmDialogue(Stoner stoner, String[] confirm) {
	this.stoner = stoner;
	this.confirm = confirm;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case 9157:
		end();
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		onConfirm();
		return true;
	case 9158:
		end();
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		return true;
	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		if (confirm == null)
			DialogueManager.sendStatement(stoner, new String[] { "Are you sure?" });
		else {
			DialogueManager.sendStatement(stoner, confirm);
		}
		next += 1;
		break;
	case 1:
		DialogueManager.sendOption(stoner, new String[] { "Yes.", "No." });
	}
	}

	public abstract void onConfirm();
}
