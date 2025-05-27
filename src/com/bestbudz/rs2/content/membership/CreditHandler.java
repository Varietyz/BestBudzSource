package com.bestbudz.rs2.content.membership;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.CreditTab;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.HashMap;

public enum CreditHandler {
	CREDIT_SHOPS(205051, 1, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, null, 1)) {
			return;
		}
		stoner.start(new OptionDialogue("Credit Shop 1", p -> {
			stoner.setCredits(stoner.getCredits());
			stoner.getShopping().open(94);
			spent(stoner, 1);
		}, "Credit Shop 2", p -> {
			stoner.setCredits(stoner.getCredits());
			stoner.getShopping().open(90);
			spent(stoner, 1);
		}, "Credit Shop 3", p -> {
			stoner.setCredits(stoner.getCredits());
			stoner.getShopping().open(87);
			spent(stoner, 1);
		}));
		}
	}),

	SPECIAL_ASSAULT(205052, 1000000, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, CreditPurchase.SPECIAL_ASSAULT, 1000000)) {
			return;
		}
		stoner.unlockCredit(CreditPurchase.SPECIAL_ASSAULT);
		stoner.setCredits(stoner.getCredits() - 1000000);
		stoner.getSpecialAssault().setSpecialAmount(99950);
		stoner.getSpecialAssault().update();
		stoner.send(new SendMessage("@blu@You have spent 1.000.000 cannacredits; Remaining: " + stoner.getCredits() + "."));
		spent(stoner, 1000000);
		}
	}),
	TAP_TYCOON(205053, 1, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, null, 1)) {
			return;
		}
		stoner.setCredits(stoner.getCredits() + 1);
		stoner.send(new SendMessage("You got 1 CannaCredit!"));
		spent(stoner, 1);
		}
	}),
	OPEN_BANK(205054, 30, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, null, 30)) {
			return;
		}
		stoner.setCredits(stoner.getCredits() - 30);
		stoner.getBank().openBank();
		stoner.send(new SendMessage("@gre@You opened bank remotely."));
		stoner.send(new SendMessage("@blu@You have spent 30 cannacredits; Remaining: " + stoner.getCredits() + "."));
		spent(stoner, 30);
		}
	}),
	UNLOCK_FREE_TELEPORTS(205055, 350, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, CreditPurchase.FREE_TELEPORTS, 350)) {
			return;
		}
		stoner.setCredits(stoner.getCredits() - 350);
		stoner.unlockCredit(CreditPurchase.FREE_TELEPORTS);
		spent(stoner, 350);
		stoner.send(new SendMessage("@gre@You no longer have to pay for teleports!"));
		stoner.send(new SendMessage("@blu@You have spent 350 cannacredits; Remaining: " + stoner.getCredits() + "."));
		}
	}),
	UNLOCK_DISEASE_IMUNITY(205056, 270, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, CreditPurchase.DISEASE_IMUNITY, 270)) {
			return;
		}
		stoner.setCredits(stoner.getCredits() - 270);
		stoner.unlockCredit(CreditPurchase.DISEASE_IMUNITY);
		spent(stoner, 270);
		stoner.send(new SendMessage("@gre@You no longer have to deal with crops catching disease!"));
		stoner.send(new SendMessage("@blu@You have spent 270 cannacredits; Remaining: " + stoner.getCredits() + "."));
		}
	}),
	REMOVE_TELEBLOCK(205057, 200, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, null, 200)) {
			return;
		}
		stoner.setCredits(stoner.getCredits() - 200);
		stoner.teleblock(0);
		spent(stoner, 200);
		stoner.send(new SendMessage("@blu@You have spent 200 cannacredits; Remaining: " + stoner.getCredits() + "."));
		}
	}),
	EASY_SHOP(205058, 1, new Handle() {
		@Override
		public void handle(Stoner stoner) {
		if (!allowed(stoner, null, 1)) {
			return;
		}
		stoner.setCredits(stoner.getCredits());
		stoner.send(new SendMessage("@blu@COMING SOON, HOLD UR HORSES!"));
		}
	}),;

	public static HashMap<Integer, CreditHandler> cannacredits = new HashMap<Integer, CreditHandler>();

	static {
		for (final CreditHandler cannacredits : CreditHandler.values()) {
			CreditHandler.cannacredits.put(cannacredits.button, cannacredits);
		}
	}

	private final int button;
	private final int creditCost;
	private final Handle handle;

	CreditHandler(int button, int creditCost, Handle handle) {
	this.button = button;
	this.creditCost = creditCost;
	this.handle = handle;
	}

	public static boolean allowed(Stoner stoner, CreditPurchase credit, int amount) {
	if (stoner.isCreditUnlocked(credit)) {
		DialogueManager.sendStatement(stoner, "@red@You have this unlocked.");
		return false;
	}
	if (stoner.getCredits() < amount) {
		DialogueManager.sendStatement(stoner, "@red@You do not have enough cannacredits to do this!");
		return false;
	}
	if (stoner.inWilderness()) {
		DialogueManager.sendStatement(stoner, "You can not do this in the wilderness!");
		return false;
	}
	if (stoner.getCombat().inCombat()) {
		DialogueManager.sendStatement(stoner, "You can not do this while in combat!");
		return false;
	}
	return true;
	}

	public static void spent(Stoner stoner, int amount) {
	stoner.getClient().queueOutgoingPacket(new SendString("</col>CannaCredits: @gre@" + Utility.format(stoner.getCredits()), 52504));
	InterfaceHandler.writeText(new CreditTab(stoner));
	InterfaceHandler.writeText(new QuestTab(stoner));
	}

	public static boolean handleClicking(Stoner stoner, int buttonId) {
	CreditHandler cannacredits = CreditHandler.cannacredits.get(buttonId);

	if (cannacredits == null) {
		return false;
	}

	cannacredits.getHandle().handle(stoner);
	return false;
	}

	public int getButton() {
	return button;
	}

	public int getCost() {
	return creditCost;
	}

	public Handle getHandle() {
	return handle;
	}

}
