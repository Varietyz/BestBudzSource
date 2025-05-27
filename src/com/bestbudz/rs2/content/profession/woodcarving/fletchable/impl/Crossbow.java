package com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl;

import com.bestbudz.rs2.content.profession.woodcarving.Woodcarving;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.Fletchable;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.FletchableItem;
import com.bestbudz.rs2.entity.item.Item;

public enum Crossbow implements Fletchable {
  BRONZE_CROSSBOW(new Item(9440), new Item(9420), new FletchableItem(new Item(9454), 1, 12.0)),
  IRON_CROSSBOW(new Item(9444), new Item(9423), new FletchableItem(new Item(9457), 1, 44.0)),
  STEEL_CROSSBOW(new Item(9446), new Item(9425), new FletchableItem(new Item(9459), 1, 54.0)),
  MITHRIL_CROSSBOW(new Item(9448), new Item(9427), new FletchableItem(new Item(9461), 1, 64.0)),
  ADAMANT_CROSSBOW(new Item(9450), new Item(9429), new FletchableItem(new Item(9463), 1, 82.0)),
  RUNE_CROSSBOW(new Item(9452), new Item(9431), new FletchableItem(new Item(9465), 1, 100.0));

  private final Item use;
  private final Item with;
  private final FletchableItem[] items;

  Crossbow(Item use, Item with, FletchableItem... items) {
    this.use = use;
    this.with = with;
    this.items = items;
  }

  public static void declare() {
    for (Crossbow cuttable : values()) {
      Woodcarving.SINGLETON.addFletchable(cuttable);
    }
  }

  @Override
  public int getAnimation() {
    switch (this) {
      case BRONZE_CROSSBOW:
        return 4436;
      case IRON_CROSSBOW:
        return 4438;
      case STEEL_CROSSBOW:
        return 4439;
      case MITHRIL_CROSSBOW:
        return 4440;
      case ADAMANT_CROSSBOW:
        return 4441;
      case RUNE_CROSSBOW:
        return 4442;
      default:
        return 4436;
    }
  }

  @Override
  public Item getUse() {
    return use;
  }

  @Override
  public Item getWith() {
    return with;
  }

  @Override
  public FletchableItem[] getFletchableItems() {
    return items;
  }

  @Override
  public Item[] getIngediants() {
    return new Item[] {use, with};
  }

  @Override
  public String getProductionMessage() {
    return null;
  }
}
