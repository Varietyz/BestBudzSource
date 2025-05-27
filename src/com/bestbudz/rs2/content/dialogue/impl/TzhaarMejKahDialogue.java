package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.pets.BossPets;
import com.bestbudz.rs2.content.pets.BossPets.PetData;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Tzhaar Mej Kah Dialogue
 * 
 * @author Jaybane
 *
 */
public class TzhaarMejKahDialogue extends Dialogue {

	public TzhaarMejKahDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	public void handlePet() {
	PetData petDrop = PetData.forItem(4000);

	if (petDrop != null) {
		if (stoner.getBossPet() == null) {
			BossPets.spawnPet(stoner, petDrop.getItem(), true);
			stoner.send(new SendMessage("You feel a pressence following you; " + Utility.formatStonerName(GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName()) + " starts to follow you."));
		} else {
			stoner.getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
			stoner.send(new SendMessage("You feel a pressence added to your bank."));
		}
	}
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {

	case DialogueConstants.OPTIONS_2_1:
		if (!StonerConstants.isOwner(stoner)) {
			stoner.send(new SendMessage("Coming soon!"));
			stoner.send(new SendRemoveInterfaces());
			return false;
		}
		if (!stoner.getBox().hasItemId(6570)) {
			DialogueManager.sendItem1(stoner, "You don't have a Firecape to do this!", 6570);
			setNext(2);
			return false;
		}
		stoner.getBox().remove(6570, 1);
		if (Utility.random(200) == 0) {
			handlePet();
		} else {
			stoner.send(new SendMessage("@red@You have sacrificed a Fire cape... Nothing happens."));
		}
		stoner.send(new SendRemoveInterfaces());
		break;

	case DialogueConstants.OPTIONS_2_2:
		stoner.send(new SendRemoveInterfaces());
		break;

	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {

	case 0:
		DialogueManager.sendNpcChat(stoner, 2181, Emotion.HAPPY, "Hello there, " + stoner.getUsername() + ".", "Want to sacrifice a Firecape for a chance to", "Obtain the Jad pet?");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Yes", "No");
		break;
	case 2:
		stoner.send(new SendRemoveInterfaces());
		break;

	}
	}

}
