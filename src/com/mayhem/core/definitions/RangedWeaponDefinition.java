package com.mayhem.core.definitions;

import com.mayhem.rs2.entity.item.Item;

public class RangedWeaponDefinition {

	public enum RangedTypes {
		THROWN, SHOT
	}

	private short id;
	private RangedTypes type;
	private Item[] arrows;

	public Item[] getArrows() {
		return arrows;
	}

	public int getId() {
		return id;
	}

	public RangedTypes getType() {
		return type;
	}
}
