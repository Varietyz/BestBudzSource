package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class PestShop extends Shop {

  public static final int SHOP_ID = 5;

  public PestShop() {
    super(
        SHOP_ID,
        new Item[] {
          new Item(8841),
          new Item(11663),
          new Item(11664),
          new Item(11665),
          new Item(8839),
          new Item(8840),
          new Item(8842),
          new Item(13072),
          new Item(13073),
        },
        false,
        "Pest Control Store");
  }

  public static final int getPrice(int id) {
    switch (id) {
      case 8841:
        return 100;
      case 11663:
        return 100;
      case 11664:
        return 100;
      case 11665:
        return 100;
      case 8839:
        return 100;
      case 8840:
        return 100;
      case 8842:
        return 100;
      case 13072:
      case 13073:
        return 250;
    }

    return 2147483647;
  }

  @Override
  public void buy(Stoner stoner, int slot, int id, int amount) {
    if (!hasItem(slot, id)) return;
    if (get(slot).getAmount() == 0) return;
    if (amount > get(slot).getAmount()) {
      amount = get(slot).getAmount();
    }

    Item buying = new Item(id, amount);

    if (!stoner.getBox().hasSpaceFor(buying)) {
      if (!buying.getDefinition().isStackable()) {
        int slots = stoner.getBox().getFreeSlots();
        if (slots > 0) {
          buying.setAmount(slots);
          amount = slots;
        } else {
          stoner
              .getClient()
              .queueOutgoingPacket(
                  new SendMessage("You do not have enough box space to buy this item."));
        }
      } else {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage("You do not have enough box space to buy this item."));
        return;
      }
    }

    if (stoner.getPestPoints() < amount * getPrice(id)) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You do not have enough Pest points to buy that."));
      return;
    }

    stoner.setPestPoints(stoner.getPestPoints() - (amount * getPrice(id)));

    InterfaceHandler.writeText(new QuestTab(stoner));

    stoner.getBox().add(buying);
    update();
  }

  @Override
  public int getBuyPrice(int id) {
    return 0;
  }

  @Override
  public String getCurrencyName() {
    return "Pest points";
  }

  @Override
  public int getSellPrice(int id) {
    return getPrice(id);
  }

  @Override
  public boolean sell(Stoner stoner, int id, int amount) {
    stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot sell items to this shop."));
    return false;
  }
}
