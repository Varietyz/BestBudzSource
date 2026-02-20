package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.io.sqlite.SaveCache;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBox;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.Arrays;
import java.util.Objects;

public class Box extends ItemContainer {

  private final Stoner stoner;

  public Box(Stoner stoner) {
    super(28, ItemContainer.ContainerTypes.STACK, false, true);
    this.stoner = stoner;
  }

  public void addOnLogin(Item item, int slot) {
    if (item == null) {
      return;
    }

    getItems()[slot] = item;
    onAdd(item);
  }

  public void addOrCreateGroundItem(Item item) {
    if (stoner.getBox().hasSpaceFor(item)) {
      stoner.getBox().insert(item);
    }
    update();
  }

  public void addOrCreateGroundItem(int id, int amount, boolean update) {
    if (stoner.getBox().hasSpaceFor(new Item(id, amount))) {
      stoner.getBox().insert(id, amount);
    }
    if (update) update();
  }

  @Override
  public boolean allowZero(int id) {
    return false;
  }

  @Override
  public void setItems(Item[] items) {
    super.setItems(items);
    update();
  }

  @Override
  public void onAdd(Item item) {
	  SaveCache.markDirty(stoner);
  }

  @Override
  public void onFillContainer() {
    stoner
        .getClient()
        .queueOutgoingPacket(new SendMessage("You do not have enough box space to carry that."));
  }

  @Override
  public void onMaxStack() {
    stoner.getClient().queueOutgoingPacket(new SendMessage("You won't be able to carry all that!"));
  }

  @Override
  public void onRemove(Item item) {SaveCache.markDirty(stoner);
  }

  @Override
  public void update() {
    for (int i = 0; i < getItems().length; i++) {
      if ((getItems()[i] != null)
          && (getItems()[i].getAmount() >= 1000000000)
          && (!StonerConstants.isOwner(stoner))) {
        stoner.getClient().setLogStoner(true);
        break;
      }
    }

    stoner.getClient().queueOutgoingPacket(new SendBox(getItems()));
  }

  public boolean contains(Object o) {
    if (!(o instanceof Item)) return false;
    Item item = (Item) o;
    return Arrays.stream(items)
        .filter(Objects::nonNull)
        .anyMatch(i -> i.getId() == item.getId() && totalAmount(item.getId()) >= item.getAmount());
  }

  public int totalAmount(int itemId) {
    return Arrays.stream(items)
        .filter(item -> item != null && item.getId() == itemId)
        .mapToInt(item -> item.getAmount())
        .sum();
  }
}
