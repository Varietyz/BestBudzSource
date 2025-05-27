package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for Makeover Mage
 * 
 * @author Jaybane
 *
 */
public class MakeoverMage extends Dialogue {

	public MakeoverMage(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case 9157:
		if (!stoner.getBox().hasItemAmount(new Item(995, 10000))) {
			DialogueManager.sendNpcChat(stoner, 1306, Emotion.ANNOYED, "You don't have 10,000 bestbucks!");
			return false;
		}
		stoner.getBox().remove(new Item(995, 10000));
		stoner.send(new SendInterface(3559));
		break;
	case 9158:
		stoner.send(new SendRemoveInterfaces());
		return false;
	}
	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendNpcChat(stoner, 1306, Emotion.HAPPY, "Hello " + stoner.getUsername() + ".", "Would you care for a make over?", "Only 10,000 bestbucks!");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Yes, take my money!", "10,000!? Scam much? Bye.");
		break;
	}
	}

}
