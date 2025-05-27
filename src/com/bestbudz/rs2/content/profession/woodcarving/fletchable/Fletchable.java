package com.bestbudz.rs2.content.profession.woodcarving.fletchable;

import com.bestbudz.rs2.entity.item.Item;

public interface Fletchable {

  int getAnimation();

  Item getUse();

  Item getWith();

  FletchableItem[] getFletchableItems();

  Item[] getIngediants();

  String getProductionMessage();
}
