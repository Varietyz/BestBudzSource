package com.bestbudz.rs2.content.profession.summoning;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

public class BOBContainer extends ItemContainer {
  private final Stoner stoner;

  public BOBContainer(Stoner stoner, int size) {
    super(size, ItemContainer.ContainerTypes.STACK, true, true);
    this.stoner = stoner;
  }

  @Override
  public boolean allowZero(int id) {
    return false;
  }

  @Override
  public void onAdd(Item item) {}

  @Override
  public void onFillContainer() {
    stoner.getClient().queueOutgoingPacket(new SendMessage("Your familiar's box is full."));
  }

  @Override
  public void onMaxStack() {}

  @Override
  public void onRemove(Item item) {}

  @Override
  public void update() {
    stoner.getBox().update();
    stoner.getClient().queueOutgoingPacket(new SendUpdateItems(5064, stoner.getBox().getItems()));
    stoner.getClient().queueOutgoingPacket(new SendUpdateItems(2702, getItems()));
  }

  public void open() {
    stoner.getClient().queueOutgoingPacket(new SendBoxInterface(2700, 5063));
    update();
  }

  public void store(int id, int amount, int slot) {
    if (!stoner.getInterfaceManager().hasBOBBoxOpen()) {
      return;
    }

    if (!stoner.getBox().slotContainsItem(slot, id)) {
      return;
    }

    if (GameDefinitionLoader.getHighAlchemyValue(id) > 50000) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You cannot store an item this valuable in your familiar's box."));
      return;
    }
    int value = 0;

    for (Item i : getItems()) {
      if (i != null) {
        value += GameDefinitionLoader.getHighAlchemyValue(i.getId()) * i.getAmount();
      }
    }

    if (value + GameDefinitionLoader.getHighAlchemyValue(id) > 175000) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("Your familiars maximum carried value limit has been reached!"));
      return;
    }

    if ((!Item.getDefinition(id).isTradable()) || (id == 7936)) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You cannot store this item in your familiar's box."));
      return;
    }

    int invAmount = stoner.getBox().getItemAmount(id);

    if (invAmount < amount) {
      amount = invAmount;
    }

    int added = add(new Item(id, amount), true);

    if (added > 0)
      if (added == 1) stoner.getBox().removeFromSlot(slot, id, added);
      else stoner.getBox().remove(id, added, false);
  }

  public void withdraw(int slot, int amount) {
    if (!stoner.getInterfaceManager().hasBOBBoxOpen()) {
      return;
    }

    if (!slotHasItem(slot)) {
      return;
    }

    int id = getSlotId(slot);
    int bankAmount = getItemAmount(id);

    if (bankAmount < amount) {
      amount = bankAmount;
    }

    int added = stoner.getBox().add(id, amount, false);

    if (added > 0) remove(new Item(id, added), true);
  }
}
