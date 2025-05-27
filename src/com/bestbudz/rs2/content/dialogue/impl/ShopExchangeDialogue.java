package com.bestbudz.rs2.content.dialogue.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Dialogue for the Stoner Shop Exchange
 * 
 * @author Jaybane
 *
 */
public class ShopExchangeDialogue extends Dialogue {

	public ShopExchangeDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	@Override
	public boolean clickButton(int button) {
	switch (button) {

	/* Opens stoners shop */
	case DialogueConstants.OPTIONS_3_1:
		stoner.getShopping().open(stoner);
		break;

	/* Setting motto */
	case DialogueConstants.OPTIONS_3_2:
		if (stoner.getCredits() < 10) {
			DialogueManager.sendStatement(stoner, "You do not have enough cannacredits to do this!");
			return false;
		}
		stoner.setEnterXInterfaceId(55776);
		stoner.getClient().queueOutgoingPacket(new SendEnterString());
		break;

	/* Setting Color */
	case DialogueConstants.OPTIONS_3_3:
		if (stoner.getCredits() < 10) {
			DialogueManager.sendStatement(stoner, "You do not have enough cannacredits to do this!");
			return false;
		}
		stoner.start(new OptionDialogue("Red", p -> {
			stoner.setShopColor("@red@");
			stoner.setCredits(stoner.getCredits() - 10);
			stoner.send(new SendRemoveInterfaces());
			DialogueManager.sendInformationBox(stoner, "Stoner Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Red", "", "");
		}, "Blue", p -> {
			stoner.setShopColor("@blu@");
			stoner.setCredits(stoner.getCredits() - 10);
			stoner.send(new SendRemoveInterfaces());
			DialogueManager.sendInformationBox(stoner, "Stoner Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Blue", "", "");
		}, "Green", p -> {
			stoner.setShopColor("@gre@");
			stoner.setCredits(stoner.getCredits() - 10);
			stoner.send(new SendRemoveInterfaces());
			DialogueManager.sendInformationBox(stoner, "Stoner Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Green", "", "");
		}, "Cyan", p -> {
			stoner.setShopColor("@cya@");
			stoner.setCredits(stoner.getCredits() - 10);
			stoner.send(new SendRemoveInterfaces());
			DialogueManager.sendInformationBox(stoner, "Stoner Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Cyan", "", "");
		}, "Default", p -> {
			stoner.setShopColor("</col>");
			stoner.setCredits(stoner.getCredits() - 10);
			stoner.send(new SendRemoveInterfaces());
			DialogueManager.sendInformationBox(stoner, "Stoner Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Default	", "", "");
		}));
		break;

	/* Shop collecting */
	case DialogueConstants.OPTIONS_5_5:
		if (stoner.getShopCollection() == 0) {
			DialogueManager.sendStatement(stoner, "You do not have any bestbucks to collect!");
			return true;
		}
		if (stoner.getBox().getFreeSlots() == 0) {
			DialogueManager.sendStatement(stoner, "Please free up some space first.");
			return true;
		}
		if (stoner.getBox().hasItemId(995)) {
			DialogueManager.sendStatement(stoner, "Please remove all bestbucks from your box.");
			return true;
		}
		stoner.getBox().add(new Item(995, (int) stoner.getShopCollection()));
		stoner.setShopCollection(0);
		return true;

	/* Searching stoner */
	case DialogueConstants.OPTIONS_5_3:
		stoner.setEnterXInterfaceId(55777);
		stoner.getClient().queueOutgoingPacket(new SendEnterString());
		return true;

	/* Searching item */
	case DialogueConstants.OPTIONS_5_4:
		stoner.setEnterXInterfaceId(55778);
		stoner.getClient().queueOutgoingPacket(new SendEnterString());
		return true;

	/* Editing */
	case DialogueConstants.OPTIONS_5_2:
		DialogueManager.sendOption(stoner, "Edit shop", "Edit shop motto (10 cannacredits)", "Edit shop color (10 cannacredits)");
		return true;

	/* Show all active shops */
	case DialogueConstants.OPTIONS_5_1:
		stoner.getClient().queueOutgoingPacket(new SendInterface(53500));
		List<Stoner> available = Arrays.stream(World.getStoners()).filter(p -> p != null && p.isActive() && p.getStonerShop().hasAnyItems()).collect(Collectors.toList());
		for (int i = 53516; i < 53716; i++) {
			Stoner p = null;
			if (i - 53516 < available.size()) {
				p = available.get(i - 53516);
				String color = "";
				if (p.getShopColor() == null) {
					color = "</col>" + p.deterquarryIcon(p);
				} else {
					color = p.getShopColor();
				}

				stoner.getClient().queueOutgoingPacket(new SendString(p.deterquarryIcon(p) + p.getUsername(), i));

				if (stoner.getShopMotto() != null) {
					stoner.getClient().queueOutgoingPacket(new SendString(color + p.getShopMotto(), i + 200));
				} else {
					stoner.getClient().queueOutgoingPacket(new SendString(color + "No shop description set.", i + 200));
				}
			} else {
				stoner.getClient().queueOutgoingPacket(new SendString("", i));
				stoner.getClient().queueOutgoingPacket(new SendString("", i + 200));
			}
		}
		return true;
	}

	return false;
	}

	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendOption(stoner, "View all shops", "Edit your Shop", "Search for stoner's shop", "Search for specific item", "Collect bestbucks");
		break;
	}
	}

}
