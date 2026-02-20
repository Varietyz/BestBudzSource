package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.content.shopping.ShopConstants;
import com.bestbudz.rs2.content.shopping.Shopping.ShopType;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class MasterCapeShop extends Shop {

  public static final int SHOP_ID = 45;

  public MasterCapeShop() {
    super(new Item[Professions.PROFESSION_COUNT], "Mastercape Shop");
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
    if (stoner.getProfession().isAdvanceFive()) {
      stoner.getBox().add(buying);
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

    for (int index = 0; index < Professions.PROFESSION_COUNT; index++) {

      int advancegrade = stoner.getProfessionAdvances()[index];

        if (advancegrade >= 5) {
			add(new Item(Professioncape.values()[index].getMasterCape()));
          if (stoner.getTotalAdvances() >= 21) {
            add(new Item(Professioncape.HUNTER.getMasterCape()));
            add(new Item(Professioncape.CONSTRUCTION.getMasterCape()));
          }
        }

    }
  }

  enum Professioncape {
    ASSAULT(
        new int[] {
          13200,
        }),
    AEGIS(
        new int[] {
          13201,
        }),
    VIGOUR(
        new int[] {
          13202,
        }),
    CONSTITUTION(
        new int[] {
          13203,
        }),
    SAGITTARIUS(
        new int[] {
          13204,
        }),
    RESONANCE(
        new int[] {
          13205,
        }),
    MAGE(
        new int[] {
          13206,
        }),
    FOODIE(
        new int[] {
          13207,
        }),
    LUMBERING(
        new int[] {
          13208,
        }),
    WOODCARVING(
        new int[] {
          13209,
        }),
    FISHER(
        new int[] {
          13210,
        }),
    PYROMANIAC(
        new int[] {
          13211,
        }),
    HANDINESS(
        new int[] {
          13212,
        }),
    FORGING(
        new int[] {
          13213,
        }),
    QUARRYING(
        new int[] {
          13214,
        }),
    THCHEMPISTRY(
        new int[] {
          13215,
        }),
    WEEDSMOKING(
        new int[] {
          13216,
        }),
    STARTER(
        new int[] {
          13217,
        }),
    MERCENARY(
        new int[] {
          13218,
        }),
    BANKSTANDING(
        new int[] {
          13219,
        }),
    CONSUMER(
        new int[] {
          13220,
        }),
    CONSTRUCTION(
        new int[] {
          13221,
        }),
    HUNTER(
        new int[] {
          13222,
        }),
	  PET_MASTER(
		  new int[] {
			  13223,
		  });

    private final int[] items;

    Professioncape(int[] items) {
      this.items = items;
    }

    public Item getMasterCape() {
      return new Item(items[0]);
    }
  }
}
