package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class CannaCreditsShop extends Shop {

  public static final int SHOP_ID = 94;

  public CannaCreditsShop() {
    super(
        SHOP_ID,
        new Item[] {
          new Item(2528),
        },
        false,
        "Being reworked");
  }

  public static final int getPrice(int id) {
    switch (id) {
      case 2528:
        return 100;
      case 12439:
      case 12397:
      case 12393:
      case 12395:
      case 12319:
      case 12351:
      case 12441:
      case 12443:
        return 250;
      case 4566:
      case 4565:
        return 500;
      case 11990:
      case 7144:
        return 150;

      case 12363:
        return 150;
      case 12365:
        return 200;
      case 12367:
        return 250;
      case 12369:
        return 300;
      case 12518:
        return 350;
      case 12520:
        return 400;
      case 12522:
        return 450;
      case 12371:
      case 12524:
      case 12373:
      case 12335:
      case 12337:
      case 12432:
        return 500;
      case 12357:
        return 1000;

      case 9472:
      case 9945:
        return 350;
      case 9946:
        return 650;
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
