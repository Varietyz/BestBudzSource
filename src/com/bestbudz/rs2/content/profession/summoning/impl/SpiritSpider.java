package com.bestbudz.rs2.content.profession.summoning.impl;

import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.content.profession.summoning.FamiliarSpecial;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.impl.GroundItemHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class SpiritSpider implements FamiliarSpecial {
	@Override
	public boolean execute(Stoner stoner, FamiliarMob mob) {
	mob.getUpdateFlags().sendForceMessage("Clicketyclack");

	Location a = new Location(stoner.getX() + 1, stoner.getY(), stoner.getZ());
	Location b = new Location(stoner.getX(), stoner.getY() + 1, stoner.getZ());

	GroundItemHandler.add(new Item(223), a, stoner);
	GroundItemHandler.add(new Item(223), b, stoner);

	World.sendStillGraphic(1342, 0, a);
	World.sendStillGraphic(1342, 0, b);
	return true;
	}

	@Override
	public int getAmount() {
	return 6;
	}

	@Override
	public double getExperience() {
	return 0.2D;
	}

	@Override
	public FamiliarSpecial.SpecialType getSpecialType() {
	return FamiliarSpecial.SpecialType.NONE;
	}
}
