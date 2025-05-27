package com.bestbudz.rs2.content.shopping;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.profession.summoning.Pouch;
import com.bestbudz.rs2.content.shopping.Shopping.ShopType;
import com.bestbudz.rs2.content.shopping.impl.AchievementShop;
import com.bestbudz.rs2.content.shopping.impl.AdvanceShop;
import com.bestbudz.rs2.content.shopping.impl.BountyShop;
import com.bestbudz.rs2.content.shopping.impl.CannaCreditsShop;
import com.bestbudz.rs2.content.shopping.impl.CannaCreditsShop2;
import com.bestbudz.rs2.content.shopping.impl.CannaCreditsShop3;
import com.bestbudz.rs2.content.shopping.impl.ChillShop;
import com.bestbudz.rs2.content.shopping.impl.ExerciseShop;
import com.bestbudz.rs2.content.shopping.impl.GracefulShop;
import com.bestbudz.rs2.content.shopping.impl.MageArenaShop;
import com.bestbudz.rs2.content.shopping.impl.MasterCapeShop;
import com.bestbudz.rs2.content.shopping.impl.MercenaryShop;
import com.bestbudz.rs2.content.shopping.impl.PestShop;
import com.bestbudz.rs2.content.shopping.impl.ProfessioncapeShop;
import com.bestbudz.rs2.content.shopping.impl.TokkulShop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class Shop extends ItemContainer {

  public static final int SHOP_SIZE = 36;
  private static final Shop[] shops = new Shop[100];
  private final int store;

  private final Item[] defaultItems;
  private final int restock = 50;
  protected ShopType type;
  private boolean general = false;
  private String name;
  private long update = System.currentTimeMillis();

  public Shop(int id, Item[] stock, boolean general, String name, ShopType type) {
    super(SHOP_SIZE, ItemContainer.ContainerTypes.ALWAYS_STACK, true, false);
    this.general = general;
    this.name = name;
    this.store = id;
    this.type = type;
    shops[id] = this;

    defaultItems = (stock.clone());
    for (int i = 0; i < stock.length; i++) {
      if (stock[i] != null) {
        setSlot(new Item(stock[i]), i);
      }
    }

    shift();

    TaskQueue.queue(
        new Task(restock) {
          @Override
          public void execute() {
            refreshContainers();
          }

          @Override
          public void onStop() {}
        });
  }

  public Shop(Item[] stock, String name) {
    super(SHOP_SIZE, ItemContainer.ContainerTypes.ALWAYS_STACK, true, true);
    general = false;
    this.name = name;
    store = -1;
    defaultItems = (stock.clone());
  }

  public Shop(int id, Item[] stock, boolean general, String name) {
    this(id, stock, general, name, ShopType.DEFAULT);
  }

  public static void declare() {
    shops[TokkulShop.SHOP_ID] = new TokkulShop();
    shops[PestShop.SHOP_ID] = new PestShop();
    shops[MercenaryShop.SHOP_ID] = new MercenaryShop();
    shops[BountyShop.SHOP_ID] = new BountyShop();
    shops[GracefulShop.SHOP_ID] = new GracefulShop();
    shops[ProfessioncapeShop.SHOP_ID] = new ProfessioncapeShop();
    shops[ChillShop.SHOP_ID] = new ChillShop();
    shops[CannaCreditsShop.SHOP_ID] = new CannaCreditsShop();
    shops[CannaCreditsShop2.SHOP_ID] = new CannaCreditsShop2();
    shops[CannaCreditsShop3.SHOP_ID] = new CannaCreditsShop3();
    shops[AdvanceShop.SHOP_ID] = new AdvanceShop();
    shops[MageArenaShop.SHOP_ID] = new MageArenaShop();
    shops[AchievementShop.SHOP_ID] = new AchievementShop();
    shops[MasterCapeShop.SHOP_ID] = new MasterCapeShop();

    shops[91] = new ExerciseShop();

    Item[] stock = new Item[SHOP_SIZE];
    Item[] stock2 = new Item[SHOP_SIZE];

    stock[0] = new Item(18016, 800000);
    stock[1] = new Item(12525, 100000);

    for (int i = 2; i < stock.length; i++) {
      int id = Pouch.values()[(i - 2)].secondIngredientId;

      if ((id != 1635)
          && (id != 440)
          && (id != 1519)
          && (id != 2349)
          && (id != 249)
          && (id != 590)
          && (id != 2351)
          && (id != 3095)) {
        stock[i] = new Item(Pouch.values()[(i - 2)].secondIngredientId, 50000);
      }
    }
    for (int i = 0; i < stock.length; i++) {
      if (i + 50 >= Pouch.values().length) {
        break;
      }
      int id = Pouch.values()[(i + 50)].secondIngredientId;

      if ((id != 383)
          && (id != 2363)
          && (id != 2361)
          && (id != 1635)
          && (id != 6155)
          && (id != 1119)
          && (id != 1115)) {
        stock2[i] = new Item(id, 50000);
      }
    }
    new Shop(50, stock, false, "Summoning Shop");
    new Shop(51, stock2, false, "Summoning Shop");
  }

  public static Shop[] getShops() {
    return shops;
  }

  public ShopType getShopType() {
    return type;
  }

  @Override
  public boolean allowZero(int id) {
    return isDefaultItem(id);
  }

  @Override
  public void onAdd(Item item) {}

  @Override
  public void onFillContainer() {}

  @Override
  public void onMaxStack() {}

  @Override
  public void onRemove(Item item) {}

  @Override
  public void update() {
    update = System.currentTimeMillis();
  }

  public void buy(Stoner stoner, int slot, int id, int amount) {

    if (System.currentTimeMillis() - stoner.shopDelay < 1000) {
      stoner.send(
          new SendMessage(
              "[" + ShopConstants.COLOUR + "*</col>] Please wait before doing this again!"));
      return;
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
    if (amount > get(slot).getAmount()) {
      amount = get(slot).getAmount();
    }

    Item buying = new Item(id, amount);

    Item gold = new Item(995, getSellPrice(id) * amount);

    if (!stoner.getBox().hasSpaceOnRemove(gold, buying)) {
      if (!buying.getDefinition().isStackable()) {
        int slots = stoner.getBox().getFreeSlots();
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
      if (stoner.isPouchPayment()) {
        if (stoner.getMoneyPouch() < gold.getAmount()) {
          stoner.send(
              new SendMessage(
                  "[" + ShopConstants.COLOUR + "*</col>] Insufficient funds on your Debit card!"));
          return;
        }
      } else {
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
    }

    if (this.store != 21) {
      int newAmount = get(slot).getAmount() - amount;

      if (newAmount < 1) {
        if (isDefaultItem(id)) get(slot).setAmount(0);
        else remove(get(slot));
      } else {
        get(slot).setAmount(newAmount);
      }
    }

    if (gold.getAmount() > 0) {
      if (stoner.isPouchPayment()) {
        stoner.setMoneyPouch(stoner.getMoneyPouch() - gold.getAmount());
        stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
      } else {
        stoner.getBox().remove(gold, false);
      }
    }

    if (this.store == 13) {
      buying.setId(
          buying.getDefinition().getNoteId() > -1
              ? buying.getDefinition().getNoteId()
              : buying.getId());
    }

    stoner.getBox().add(buying);
    update();
    stoner.shopDelay = System.currentTimeMillis();
  }

  public boolean empty(int slot) {
    return (get(slot) == null) || (get(slot).getAmount() == 0);
  }

  public int getBuyPrice(int id) {
    if (this.store == 21) {
      return 0;
    }

    return GameDefinitionLoader.getStoreSellToValue(id);
  }

  public String getCurrencyName() {
    return null;
  }

  public Item getDefaultItem(int id) {
    for (Item i : defaultItems) {
      if ((i != null) && (i.getId() == id)) {
        return i;
      }
    }

    return null;
  }

  public Item[] getDefaultItems() {
    return defaultItems;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getSellPrice(int id) {
    if (this.store == 21) {
      return 0;
    }

    return GameDefinitionLoader.getStoreBuyFromValue(id);
  }

  public boolean hasItem(int slot, int id) {
    return (get(slot) != null) && (get(slot).getId() == id);
  }

  public boolean isDefaultItem(int id) {
    for (Item i : defaultItems) {
      if ((i != null) && (i.getId() == id)) {
        return true;
      }
    }

    return false;
  }

  public boolean isGeneral() {
    return general;
  }

  public boolean isUpdate() {
    return System.currentTimeMillis() - update < 1000L;
  }

  public void refreshContainers() {
    Item[] items = getItems();

    for (int j = 0; j < SHOP_SIZE; j++) {
      if (items[j] == null) {
        break;
      }
      Item stock = getDefaultItem(items[j].getId());

      if (stock != null) {
        if (items[j].getAmount() < stock.getAmount()) items[j].add(1);
        else if (items[j].getAmount() > stock.getAmount()) {
          items[j].remove(1);
        }
      } else if (items[j].getAmount() > 1) items[j].remove(1);
      else {
        remove(getItems()[j]);
      }
    }

    update();
  }

  public boolean sell(Stoner stoner, int id, int amount) {
    if (id == 995) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "[" + ShopConstants.COLOUR + "*</col>] You cannot sell BestBucks to a shop."));
      return false;
    }
    for (int i = 0; i < ShopConstants.NO_SELL.length; i++) {
      if (id == ShopConstants.NO_SELL[i]) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "["
                        + ShopConstants.COLOUR
                        + "*</col>] You may not sell this item! Try selling it to a stoner."));
        return false;
      }
    }
    if (!Item.getDefinition(id).isTradable()) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item."));
      return false;
    }

    if ((this.store == 21) || ((!general) && (!isDefaultItem(id)))) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item to this shop."));
      return false;
    }

    if (amount > 50000) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "["
                      + ShopConstants.COLOUR
                      + "*</col>] You can only sell 50,000 at a time to these shops."));
      amount = 50000;
    }

    int invAmount = stoner.getBox().getItemAmount(id);

    if (invAmount == 0) return false;
    if (invAmount < amount) {
      amount = invAmount;
    }

    Item item = new Item(id, amount);

    if (!hasSpaceFor(item)) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "["
                      + ShopConstants.COLOUR
                      + "*</col>] The shop does not have enough space to buy this item."));
      return false;
    }

    Item gold = new Item(995, getBuyPrice(id) * amount);

    if (!stoner.getBox().hasSpaceOnRemove(item, gold)) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "["
                      + ShopConstants.COLOUR
                      + "*</col>] You do not have enough box space to sell this item."));
      return false;
    }

    stoner.getBox().remove(item);

    if (gold.getAmount() > 0) {
      stoner.getBox().add(gold);
    }

    add(item);
    update();
    return true;
  }

  public void onOpen(Stoner stoner) {}
}
