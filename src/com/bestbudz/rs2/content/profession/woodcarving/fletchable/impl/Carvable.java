package com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl;

import com.bestbudz.rs2.content.profession.woodcarving.Woodcarving;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.Fletchable;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.FletchableItem;
import com.bestbudz.rs2.entity.item.Item;

public enum Carvable implements Fletchable {
  LOG(
      new Item(946),
      new Item(1511),
      new FletchableItem(new Item(53, 5), 1, 5.0),
      new FletchableItem(new Item(50), 1, 5.0),
      new FletchableItem(new Item(48), 1, 10.0),
      new FletchableItem(new Item(9440), 1, 6.0)),
  OAK_LOG(
      new Item(946),
      new Item(1521),
      new FletchableItem(new Item(54), 1, 16.5),
      new FletchableItem(new Item(56), 1, 25.0),
      new FletchableItem(new Item(9442), 1, 16.0)),
  WILLOW_LOG(
      new Item(946),
      new Item(1519),
      new FletchableItem(new Item(60), 1, 33.3),
      new FletchableItem(new Item(58), 1, 41.5),
      new FletchableItem(new Item(9444), 1, 22.0)),
  TEAK_LOG(new Item(946), new Item(6333), new FletchableItem(new Item(9446), 1, 27.0)),
  MAPLE_LOG(
      new Item(946),
      new Item(1517),
      new FletchableItem(new Item(64), 1, 50.0),
      new FletchableItem(new Item(62), 1, 51.3),
      new FletchableItem(new Item(9448), 1, 32.0)),
  MAHOGANY_LOG(new Item(946), new Item(6332), new FletchableItem(new Item(9450), 1, 41.0)),
  YEW_LOG(
      new Item(946),
      new Item(1515),
      new FletchableItem(new Item(68), 1, 67.5),
      new FletchableItem(new Item(66), 1, 75.0),
      new FletchableItem(new Item(9452), 1, 50.0)),
  MAGE_LOG(
      new Item(946),
      new Item(1513),
      new FletchableItem(new Item(72), 1, 83.3),
      new FletchableItem(new Item(70), 1, 91.5)),

  OPAL_BOLT_TIP(new Item(1755), new Item(1609), new FletchableItem(new Item(45, 12), 1, 1.5)),
  JADE_BOLT_TIP(new Item(1755), new Item(1611), new FletchableItem(new Item(9187, 12), 1, 2.0)),
  PEARL_BOLT_TIP(new Item(1755), new Item(411), new FletchableItem(new Item(46, 24), 1, 3.2)),
  PEARLS_BOLT_TIP(new Item(1755), new Item(413), new FletchableItem(new Item(46, 6), 1, 3.2)),
  TOPAZ_BOLT_TIP(new Item(1755), new Item(1613), new FletchableItem(new Item(9188, 12), 1, 3.9)),
  SAPPHIRE_BOLT_TIP(new Item(1755), new Item(1607), new FletchableItem(new Item(9189, 12), 1, 4.7)),
  EMERALD_BOLT_TIP(new Item(1755), new Item(1605), new FletchableItem(new Item(9190, 12), 1, 5.5)),
  RUBY_BOLT_TIP(new Item(1755), new Item(1603), new FletchableItem(new Item(9191, 12), 1, 6.3)),
  DIAMOND_BOLT_TIP(new Item(1755), new Item(1601), new FletchableItem(new Item(9192, 12), 1, 7.0)),
  DRAGONSTONE_BOLT_TIP(
      new Item(1755), new Item(1615), new FletchableItem(new Item(9193, 12), 1, 8.2)),
  ONYX_BOLT_TIP(new Item(1755), new Item(6573), new FletchableItem(new Item(9194, 12), 1, 9.4));

  private final Item use;
  private final Item with;
  private final FletchableItem[] items;

  Carvable(Item use, Item with, FletchableItem... items) {
    this.use = use;
    this.with = with;
    this.items = items;
  }

  public static void declare() {
    for (Carvable cuttable : values()) {
      Woodcarving.SINGLETON.addFletchable(cuttable);
    }
  }

  @Override
  public int getAnimation() {
    switch (this) {
      case OPAL_BOLT_TIP:
      case PEARL_BOLT_TIP:
        return 891;
      case TOPAZ_BOLT_TIP:
        return 892;
      case SAPPHIRE_BOLT_TIP:
        return 888;
      case EMERALD_BOLT_TIP:
        return 889;
      case RUBY_BOLT_TIP:
        return 887;
      case DIAMOND_BOLT_TIP:
        return 890;
      case DRAGONSTONE_BOLT_TIP:
        return 890;
      case ONYX_BOLT_TIP:
        return 2717;
      default:
        return 1248;
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
  public String getProductionMessage() {
    return null;
  }

  @Override
  public Item[] getIngediants() {
    return new Item[] {with};
  }
}
