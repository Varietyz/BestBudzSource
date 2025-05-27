package com.bestbudz.rs2.content.profession.woodcarving.fletchable;

import com.bestbudz.rs2.entity.item.Item;

public interface Fletchable {

	public int getAnimation();

	public Item getUse();

	public Item getWith();

	public FletchableItem[] getFletchableItems();

	public Item[] getIngediants();

	public String getProductionMessage();
}