package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.content.shopping.ShopConstants;
import com.bestbudz.rs2.content.shopping.Shopping.ShopType;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ProfessioncapeShop extends Shop {

  public static final int SHOP_ID = 20;

  public ProfessioncapeShop() {
    super(new Item[Professions.PROFESSION_COUNT], "Professioncape Shop");
    type = ShopType.INSTANCE;
  }

  @Override
  public void buy(Stoner stoner, int slot, int id, int amount) {
    if (amount > 500) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "["
                      + ShopConstants.COLOUR
                      + "*</col>] You can only buy 500 maximum at a time from these shops."));
      amount = 500;
    }

    if (!hasItem(slot, id)) return;

    if (get(slot).getAmount() == 0) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "[" + ShopConstants.COLOUR + "*</col>] The shop is out of stock on that item."));
      return;
    }

    Item buying = new Item(id, amount);

    Item gold = new Item(995, getSellPrice(id) * amount);

    if (!(stoner.getBox().getFreeSlots() > amount * 3)) {
      if (!buying.getDefinition().isStackable()) {
        int slots = stoner.getBox().getFreeSlots() / 3;
        if (slots > 0) {
          buying.setAmount(slots);
          amount = slots;
          gold.setAmount(getSellPrice(id) * amount);
        } else {
          stoner
              .getClient()
              .queueOutgoingPacket(
                  new SendMessage(
                      "["
                          + ShopConstants.COLOUR
                          + "*</col>] You do not have enough box space to buy this item."));
          return;
        }
      } else {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "["
                        + ShopConstants.COLOUR
                        + "*</col>] You do not have enough box space to buy this item."));
        return;
      }
    }

    if (gold.getAmount() > 0) {
      if (!stoner.getBox().hasItemAmount(gold)) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "["
                        + ShopConstants.COLOUR
                        + "*</col>] You do not have enough BestBucks to buy that."));
        return;
      }
    }

    if (gold.getAmount() > 0) {
      stoner.getBox().remove(gold, false);
    }
    if (stoner.getProfession().hasTwo99s()) {
      stoner.getBox().add(buying.getId() + 1, amount);
      stoner.getBox().add(buying.getId(), amount);

    } else {
      stoner.getBox().add(buying);
      stoner.getBox().add(buying.getId() + 2, amount);
    }
  }

  @Override
  public int getSellPrice(int id) {
    return 0;
  }

  @Override
  public boolean sell(Stoner stoner, int id, int amount) {
    stoner.send(new SendMessage("You cannot sell items to this store."));
    return false;
  }

  @Override
  public void onOpen(Stoner stoner) {
    clear();

    boolean trimmed = stoner.getProfession().hasTwo99s();

    for (int index = 0; index < Professions.PROFESSION_COUNT; index++) {

      long grade = stoner.getMaxGrades()[index];
		add(new Item(9777), false);
		add(new Item(9778), false);
      if (grade >= 420) {
        if (stoner.getTotalAdvances() >= 10) {
          add(new Item(Professioncape.HUNTER.getTrimmedCape()), false);
			add(new Item(Professioncape.HUNTER.getCape()), false);
          add(new Item(Professioncape.CONSTRUCTION.getCape()), false);
			add(new Item(Professioncape.CONSTRUCTION.getTrimmedCape()), false);

        } else {
          add(
              new Item(
                  trimmed
                      ? Professioncape.values()[index].getTrimmedCape()
                      : Professioncape.values()[index].getCape()),
              false);
        }
      }
    }
  }

  enum Professioncape {
    ASSAULT(
        new int[] {
          9747, 9748, 9749,
        }),
    AEGIS(
        new int[] {
          9753, 9754, 9755,
        }),
    VIGOUR(
        new int[] {
          9750, 9751, 9752,
        }),
    CONSTITUTION(
        new int[] {
          9768, 9769, 9770,
        }),
    SAGITTARIUS(
        new int[] {
          9756, 9757, 9758,
        }),
    RESONANCE(
        new int[] {
          9759, 9760, 9761,
        }),
    MAGE(
        new int[] {
          9762, 9763, 9764,
        }),
    FOODIE(
        new int[] {
          9801, 9802, 9803,
        }),
    LUMBERING(
        new int[] {
          9807, 9808, 9809,
        }),
    WOODCARVING(
        new int[] {
          9783, 9784, 9785,
        }),
    FISHER(
        new int[] {
          9798, 9799, 9800,
        }),
    PYROMANIAC(
        new int[] {
          9804, 9805, 9806,
        }),
    HANDINESS(
        new int[] {
          9780, 9781, 9782,
        }),
    FORGING(
        new int[] {
          9795, 9796, 9797,
        }),
    QUARRYING(
        new int[] {
          9792, 9793, 9794,
        }),
    THCHEMPISTRY(
        new int[] {
          9774, 9775, 9776,
        }),
    WEEDSMOKING(
        new int[] {
          9771, 9772, 9773,
        }),
    MERCENARY(
        new int[] {
          9786, 9787, 9788,
        }),
    BANKSTANDING(
        new int[] {
          9810, 9811, 9812,
        }),
    CONSUMER(
        new int[] {
          9765, 9766, 9767,
        }),
    CONSTRUCTION(
        new int[] {
          9789, 9790, 9791,
        }),
    HUNTER(
        new int[] {
          9948, 9949, 9950,
        });

    private final int[] items;

    Professioncape(int[] items) {
      this.items = items;
    }

    public static Item forCape(int cape) {
      for (Professioncape sc : values()) {
        if (sc.getCape().getId() == cape || sc.getTrimmedCape().getId() == cape) {
          return new Item(sc.items[2]);
        }
      }
      return null;
    }

    public Item getCape() {
      return new Item(items[0]);
    }

    public Item getTrimmedCape() {
      return new Item(items[1]);
    }
  }
}
