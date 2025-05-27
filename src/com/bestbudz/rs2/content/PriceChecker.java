package com.bestbudz.rs2.content;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGame;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;
import java.text.NumberFormat;

public class PriceChecker extends ItemContainer {

  private final Stoner stoner;
  private final int[][] ITEM_STRINGS = {
    {0, 48550},
    {1, 48551},
    {2, 48552},
    {3, 48553},
    {4, 48554},
    {5, 48555},
    {6, 48556},
    {7, 48557},
    {8, 48558},
    {9, 48559},
    {10, 48560},
    {11, 48561},
    {12, 48562},
    {13, 48563},
    {14, 48564},
    {15, 48565},
    {16, 48566},
    {17, 48567},
    {18, 48568},
    {19, 48569},
    {20, 48570},
    {21, 48571},
    {22, 48572},
    {23, 48573},
    {24, 48574},
    {25, 48575},
    {26, 48576},
    {27, 48577},
  };

  public PriceChecker(Stoner stoner, int size) {
    super(size, ItemContainer.ContainerTypes.STACK, true, true);
    this.stoner = stoner;
  }

  @Override
  public boolean allowZero(int paramInt) {
    return false;
  }

  @Override
  public void onAdd(Item paramItem) {}

  @Override
  public void onFillContainer() {}

  @Override
  public void onMaxStack() {}

  @Override
  public void onRemove(Item paramItem) {}

  @Override
  public void update() {
    stoner.getClient().queueOutgoingPacket(new SendUpdateItems(48542, getItems()));
    stoner.getClient().queueOutgoingPacket(new SendUpdateItems(5064, stoner.getBox().getItems()));
    stoner.send(new SendString(NumberFormat.getInstance().format(getContainerNet()), 48513));
  }

  public void open() {
    if (WeaponGame.gameStoners.contains(stoner)
        || stoner.getCombat().inCombat()
        || stoner.isDead()
        || stoner.getMage().isTeleporting()) {
      return;
    }
    update();
    for (int i = 0; i < 27; i++) {
      stoner.send(new SendString("", ITEM_STRINGS[i][1]));
      stoner.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(48542, 0, 0, i));
    }
    stoner.getClient().queueOutgoingPacket(new SendBoxInterface(48500, 5063));
  }

  public void store(Item item) {
    if (item != null) {
      store(item.getId(), item.getAmount());
    }
  }

  public void store(int id, int amount) {

    if (WeaponGame.gameStoners.contains(stoner)) {
      return;
    }
    if (!Item.getDefinition(id).isTradable()
        || Item.getDefinition(id).getName().contains("Clue scroll")) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("This item is untradeable!"));
      return;
    }

    if (stoner.getBox().getItemAmount(id) < amount) {
      amount = stoner.getBox().getItemAmount(id);
    }

    int slot = getItemSlot(id);
    Item item = new Item(id, amount);

    if (slot == -1 || !item.getDefinition().isStackable()) {
      for (int i = 0; i < getSize(); i++) {
        if (getItems()[i] == null) {
          slot = i;
          break;
        }
      }
    }

    add(item, true);
    stoner.send(
        new SendString(
            NumberFormat.getInstance().format(GameDefinitionLoader.getStoreSellToValue(id))
                + "x"
                + NumberFormat.getInstance().format(amount),
            ITEM_STRINGS[slot][1]));
    stoner.getBox().remove(new Item(id, amount), false);
    update();
  }

  public void withdraw(int itemId, int slot, int amount) {
    Item item = get(slot);
    if (item == null || item.getId() != itemId) {
      return;
    }

    amount = removeFromSlot(slot, itemId, amount);
    if (amount <= 0) {
      return;
    }
    stoner.getBox().add(new Item(itemId, amount));

    shift();

    for (int i = 0; i < getSize(); i++) {
      if (getItems()[i] != null) {
        stoner.send(
            new SendString(
                NumberFormat.getInstance()
                    .format(GameDefinitionLoader.getStoreSellToValue(getItems()[i].getId())),
                ITEM_STRINGS[i][1]));
      } else {
        stoner.send(new SendString("", ITEM_STRINGS[i][1]));
      }
    }
    update();
  }

  public void depositeAll() {
    for (Item item : stoner.getBox().getItems()) {
      store(item);
    }
  }

  public void withdrawAll() {
    for (int i = 0; i < getSize(); i++) {
      Item item = get(i);

      if (item == null) {
        continue;
      }
      stoner.getBox().add(item);
      stoner.send(new SendString("", ITEM_STRINGS[i][1]));
    }

    clear();
    update();
  }
}
