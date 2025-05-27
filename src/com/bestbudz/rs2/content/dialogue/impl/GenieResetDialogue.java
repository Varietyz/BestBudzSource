package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue that handles genie profession reset
 * 
 * @author Jaybane
 */
public class GenieResetDialogue extends Dialogue {

	public GenieResetDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case 9157:
		stoner.send(new SendInterface(59500));
		break;
	case 9158:
		stoner.send(new SendRemoveInterfaces());
		break;
	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendNpcChat(stoner, 326, Emotion.HAPPY_TALK, "Waddup stoner.", "Wanne go back on those combat professions?", "Are you interested?");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Ya man", "No man");
		break;
	}
	}

}
