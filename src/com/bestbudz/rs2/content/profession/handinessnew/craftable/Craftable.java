package com.bestbudz.rs2.content.profession.handinessnew.craftable;

import com.bestbudz.rs2.entity.item.Item;

public interface Craftable {

  String getName();

  int getAnimation();

  Item getUse();

  Item getWith();

  CraftableItem[] getCraftableItems();

  Item[] getIngediants(int index);

  String getProductionMessage();
}
