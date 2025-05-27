package com.bestbudz.rs2.content.profession.mage.spells;

import com.bestbudz.rs2.content.profession.mage.Spell;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
//import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Charge extends Spell {

	public static boolean isChargeActive(Stoner p) {
	return (p.getAttributes().get("charge") != null) && (System.currentTimeMillis() - ((Long) p.getAttributes().get("charge")).longValue() < 420000L);
	}

	@Override
	public boolean execute(Stoner stoner) {
	if (stoner.getAttributes().get("charge") != null) {
		if (System.currentTimeMillis() - ((Long) stoner.getAttributes().get("charge")).longValue() < 420000L) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You are still affected by a mage charge."));
			return false;
		}
		stoner.getAttributes().remove("charge");
	}

	if (stoner.getAttributes().get("chargeCoolDown") != null) {
		if (System.currentTimeMillis() - ((Long) stoner.getAttributes().get("chargeCoolDown")).longValue() < 60000L) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You must wait atleast 1 minute to cast this spell again."));
			return false;
		}
	} else
		stoner.getAttributes().remove("chargeCoolDown");

	stoner.getAttributes().set("charge", Long.valueOf(System.currentTimeMillis()));
	stoner.getAttributes().set("chargeCoolDown", Long.valueOf(System.currentTimeMillis()));

	stoner.getUpdateFlags().sendGraphic(Graphic.highGraphic(308, 5));
	stoner.getUpdateFlags().sendAnimation(new Animation(811));
	stoner.getClient().queueOutgoingPacket(new SendMessage("You feel charged with mage power."));
	return false;
	}

	@Override
	public double getExperience() {
	return 180.0D;
	}

	@Override
	public int getGrade() {
	return 80;
	}

	@Override
	public String getName() {
	return "Charge";
	}

	// @Override
	// public Item[] getRunes() {
	// return new Item[] { new Item(565, 3), new Item(554, 3), new Item(556, 3) };
	// }
}
