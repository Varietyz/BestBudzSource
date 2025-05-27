package com.bestbudz.rs2.content.profession.melee;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SerpentineHelmet {

  private static final int FULL = 11_000;

  private int charges;

  public SerpentineHelmet(int charges) {
    this.charges = charges;
  }

  public static boolean hasHelmet(Stoner stoner) {
    return stoner.getEquipment().isWearingItem(12931, EquipmentConstants.HELM_SLOT);
  }

  public static boolean itemOnItem(Stoner stoner, Item itemUsed, Item usedWith) {
    if ((itemUsed.getId() == 12929 && usedWith.getId() == 12934)
        || (usedWith.getId() == 12929 && itemUsed.getId() == 12934)) {
      int amount = stoner.getBox().getItemAmount(12934);
      if (amount >= FULL) {
        amount = FULL;
      }
      stoner.getBox().remove(12934, amount);
      stoner.getSerpentineHelment().charges += amount;
      if (stoner.getSerpentineHelment().getCharges() > 0) {
        stoner.getBox().remove(12929, 1);
        stoner.getBox().add(12931, 1);
      }
      check(stoner);
      return true;
    }

    if ((itemUsed.getId() == 12931 && usedWith.getId() == 12934)
        || (usedWith.getId() == 12931 && itemUsed.getId() == 12934)) {
      int amount = stoner.getBox().getItemAmount(12934);
      if (stoner.getSerpentineHelment().getCharges() >= FULL
          || stoner.getSerpentineHelment().getCharges() + amount >= FULL) {
        amount = FULL - stoner.getSerpentineHelment().getCharges();
      }

      if (amount <= 0) {
        return true;
      }

      stoner.getBox().remove(12934, amount);
      stoner.getSerpentineHelment().charges += amount;
      check(stoner);
      return true;
    }

    return false;
  }

  public static boolean itemOption(Stoner stoner, int i, int itemId) {
    if (itemId != 12931) {
      return false;
    }

    switch (i) {
      case 1:
        check(stoner);
        return true;

      case 4:
        uncharge(stoner);
        return true;
    }

    return true;
  }

  public static void check(Stoner stoner) {
    int percent = stoner.getSerpentineHelment().getCharges() * 100 / FULL;
    stoner.send(new SendMessage("Please not the helment effect has not been added yet!"));
    stoner.send(
        new SendMessage(
            "Charges: <col=007F00>"
                + Utility.format(stoner.getSerpentineHelment().getCharges())
                + " </col>(<col=007F00>"
                + percent
                + "%</col>)"));
  }

  public static void uncharge(Stoner stoner) {
    if (stoner.getSerpentineHelment().getCharges() == 0) {
      stoner.send(new SendMessage("You do not have any charges!"));
      return;
    }
    int amount = stoner.getSerpentineHelment().getCharges();

    stoner.getSerpentineHelment().charges = 0;
    stoner.getBox().remove(12931, 1);
    stoner.getBox().addOrCreateGroundItem(12934, amount, true);
    stoner.getBox().addOrCreateGroundItem(12929, 1, true);
  }

  public int getCharges() {
    return charges;
  }
}
