package com.mayhem.core.definitions;

import com.mayhem.rs2.entity.item.Item;

public class ShopDefinition {

	private short id;
	private String name;
	private boolean general;
	private Item[] items;

	public int getId() {
		return id;
	}

	public Item[] getItems() {
		return items;
	}

	public String getName() {
		return name;
	}

	public boolean isGeneral() {
		return general;
	}
}
