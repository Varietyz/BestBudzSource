package com.bestbudz.rs2.content.profession.summoning;

import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract interface FamiliarSpecial {
	public static enum SpecialType {
		COMBAT,
		NONE;
	}

	public abstract boolean execute(Stoner paramStoner, FamiliarMob paramFamiliarMob);

	public abstract int getAmount();

	public abstract double getExperience();

	public abstract SpecialType getSpecialType();
}
