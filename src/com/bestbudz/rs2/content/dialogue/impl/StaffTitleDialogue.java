package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class StaffTitleDialogue extends Dialogue {

	public StaffTitleDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	private void title(int color) {
	stoner.setStonerTitle(StonerTitle.create(stoner.getStonerTitle().getTitle(), color, false));
	stoner.send(new SendMessage("Special title has been set!"));
	stoner.setAppearanceUpdateRequired(true);
	stoner.send(new SendRemoveInterfaces());
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {

	case DialogueConstants.OPTIONS_2_1:
		stoner.start(new OptionDialogue("Blue", p -> {
			title(0x3366FF);
		}, "Red", p -> {
			title(0xB80000);
		}, "Green", p -> {
			title(0x47B224);
		}, "Purple", p -> {
			title(0x8F24B2);
		}, "Orange", p -> {
			title(0xFF6600);
		}));
		break;

	case DialogueConstants.OPTIONS_2_2:
		stoner.setEnterXInterfaceId(56002);
		stoner.getClient().queueOutgoingPacket(new SendEnterString());
		break;

	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {

	case 0:
		if (!StonerConstants.isStaff(stoner)) {
			DialogueManager.sendStatement(stoner, "This is not for you!");
			return;
		}
		DialogueManager.sendNpcChat(stoner, 6749, Emotion.HAPPY_TALK, "Hello " + stoner.getUsername() + "!", "I can give you a special title.", "You must be privilaged enough of course!");
		next++;
		break;

	case 1:
		DialogueManager.sendOption(stoner, "Title color", "Select title");
		break;

	}
	}

}
