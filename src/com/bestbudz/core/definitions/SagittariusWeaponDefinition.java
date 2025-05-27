package com.bestbudz.core.definitions;

import com.bestbudz.rs2.entity.item.Item;

public class SagittariusWeaponDefinition {

	public enum SagittariusTypes {
		THROWN,
		SHOT
	}

	private short id;
	private SagittariusTypes type;
	private Item[] arrows;

	public Item[] getArrows() {
	return arrows;
	}

	public int getId() {
	return id;
	}

	public SagittariusTypes getType() {
	return type;
	}
}
