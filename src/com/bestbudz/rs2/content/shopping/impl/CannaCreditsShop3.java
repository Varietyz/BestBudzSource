package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class CannaCreditsShop3 extends Shop {

  public static final int SHOP_ID = 87;

  public CannaCreditsShop3() {
    super(
        SHOP_ID,
        new Item[] {
          new Item(10551),
        },
        false,
        "Being Reworked");
  }

  public static final int getPrice(int id) {
    switch (id) {
      case 11920:
      case 6739:
        return 10000;

      case 10551:
        return 4500;

      case 6916:
      case 6918:
      case 6920:
      case 6922:
      case 6924:
        return 2500;

      case 11943:
        return 25;
    }

    return 150;
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

    if (stoner.getCredits() < amount * getPrice(id)) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You do not have enough CannaCredits to buy that."));
      return;
    }

    stoner.setCredits(stoner.getCredits() - (amount * getPrice(id)));

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
    return "CannaCredits";
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
