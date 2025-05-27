package com.bestbudz.rs2.content.profession.handinessnew.craftable;

import com.bestbudz.rs2.entity.item.Item;

public interface Craftable {

	public String getName();

	public int getAnimation();

	public Item getUse();

	public Item getWith();

	public CraftableItem[] getCraftableItems();

	public Item[] getIngediants(int index);

	public String getProductionMessage();
}