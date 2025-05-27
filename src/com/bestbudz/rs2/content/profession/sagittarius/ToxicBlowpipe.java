package com.bestbudz.rs2.content.profession.sagittarius;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ToxicBlowpipe {

  private static final int FULL = 16_383;

  private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");

  private Item blowpipeAmmo;
  private int blowpipeCharge;

  public ToxicBlowpipe(Item blowpipeAmmo, int blowpipeCharge) {
    this.blowpipeAmmo = blowpipeAmmo;
    this.blowpipeCharge = blowpipeCharge;
    FORMATTER.setRoundingMode(RoundingMode.FLOOR);
  }

  public static boolean itemOnItem(Stoner stoner, Item itemUsed, Item usedWith) {
    if (itemUsed.getId() == 12924
        || itemUsed.getId() == 12926
        || usedWith.getId() == 12924
        || usedWith.getId() == 12926) {
      if (usedWith.getId() == 12934) {
        if (stoner.getToxicBlowpipe().blowpipeCharge / 3 == FULL) {
          return true;
        }

        int added = 0;
        if (stoner.getToxicBlowpipe().blowpipeCharge / 3 + usedWith.getAmount() > FULL) {
          added = FULL - stoner.getToxicBlowpipe().blowpipeCharge / 3;
        } else {
          added = usedWith.getAmount();
        }
        stoner.getToxicBlowpipe().blowpipeCharge += added * 3;
        stoner.getBox().remove(usedWith.getId(), added);
        int slot = stoner.getBox().getItemSlot(itemUsed.getId());
        stoner.getBox().get(slot).setId(12926);
        check(stoner);
        return true;
      } else if (itemUsed.getId() == 12934) {
        if (stoner.getToxicBlowpipe().blowpipeCharge / 3 == FULL) {
          return true;
        }
        int added = 0;
        if (stoner.getToxicBlowpipe().blowpipeCharge / 3 + itemUsed.getAmount() > FULL) {
          added = FULL - stoner.getToxicBlowpipe().blowpipeCharge / 3;
        } else {
          added = itemUsed.getAmount();
        }
        stoner.getToxicBlowpipe().blowpipeCharge += added * 3;
        stoner.getBox().remove(itemUsed.getId(), added);
        int slot = stoner.getBox().getItemSlot(usedWith.getId());
        stoner.getBox().get(slot).setId(12926);
        check(stoner);
        return true;
      }
    }

    if (stoner.getToxicBlowpipe().blowpipeAmmo != null
        && stoner.getToxicBlowpipe().blowpipeAmmo.getAmount() == FULL) {
      return true;
    }

    Item dart = null;

    switch (itemUsed.getId()) {
      case 806:
      case 807:
      case 808:
      case 809:
      case 810:
      case 811:
      case 11230:
        dart = new Item(itemUsed);
    }

    if (dart == null) {
      switch (usedWith.getId()) {
        case 806:
        case 807:
        case 808:
        case 809:
        case 810:
        case 811:
        case 11230:
          dart = new Item(usedWith);
      }
    }

    if (dart == null) {
      return false;
    }

    if (usedWith.getId() == 12924) {
      int slot = stoner.getBox().getItemSlot(usedWith.getId());
      stoner.getBox().get(slot).setId(12926);
    } else if (itemUsed.getId() == 12924) {
      int slot = stoner.getBox().getItemSlot(itemUsed.getId());
      stoner.getBox().get(slot).setId(12926);
    }

    if (usedWith.getId() == 12924
        || usedWith.getId() == 12926
        || itemUsed.getId() == 12924
        || itemUsed.getId() == 12926) {
      if (stoner.getToxicBlowpipe().blowpipeAmmo != null) {
        if (dart.getAmount() + stoner.getToxicBlowpipe().blowpipeAmmo.getAmount() > FULL) {
          dart.setAmount(
              (dart.getAmount() + stoner.getToxicBlowpipe().blowpipeAmmo.getAmount()) - FULL);
        }
        stoner.getToxicBlowpipe().blowpipeAmmo.add(dart.getAmount());
      } else if (dart.getAmount() > FULL) {
        dart.setAmount(FULL);
        stoner.getToxicBlowpipe().blowpipeAmmo = dart;
      } else {
        stoner.getToxicBlowpipe().blowpipeAmmo = dart;
      }
    }

    stoner.getBox().remove(dart);
    check(stoner);
    return false;
  }

  public static void check(Stoner stoner) {
    String ammo = "None";
    if (stoner.getToxicBlowpipe().blowpipeAmmo != null) {
      ammo =
          stoner.getToxicBlowpipe().blowpipeAmmo.getDefinition().getName()
              + " x "
              + stoner.getToxicBlowpipe().blowpipeAmmo.getAmount();
    }
    String scales =
        FORMATTER.format((stoner.getToxicBlowpipe().blowpipeCharge / 3.0) * 100.0 / (double) FULL)
            + "%";
    stoner.send(
        new SendMessage("Darts: <col=007F00>" + ammo + "</col>. Scales: <col=007F00>" + scales));
  }

  public static boolean hasBlowpipe(Stoner stoner) {
    return stoner.getEquipment().isWearingItem(12926, EquipmentConstants.WEAPON_SLOT);
  }

  public static void unload(Stoner stoner) {
    if (stoner.getToxicBlowpipe().blowpipeCharge > 0) {
      stoner
          .getBox()
          .addOrCreateGroundItem(12934, stoner.getToxicBlowpipe().blowpipeCharge / 3, true);
    }
    if (stoner.getToxicBlowpipe().blowpipeAmmo != null) {
      stoner
          .getBox()
          .addOrCreateGroundItem(
              stoner.getToxicBlowpipe().blowpipeAmmo.getId(),
              stoner.getToxicBlowpipe().blowpipeAmmo.getAmount(),
              true);
    }
    stoner.getToxicBlowpipe().blowpipeCharge = 0;
    stoner.getToxicBlowpipe().blowpipeAmmo = null;
    stoner.getBox().get(stoner.getBox().getItemSlot(12926)).setId(12924);
  }

  public static boolean itemOption(Stoner stoner, int i, int itemId) {
    if (itemId != 12926) {
      return false;
    }
    switch (i) {
      case 1:
      case 2:
        check(stoner);
        return true;
      case 3:
        if (stoner.getToxicBlowpipe().blowpipeAmmo != null) {
          stoner
              .getBox()
              .addOrCreateGroundItem(
                  stoner.getToxicBlowpipe().blowpipeAmmo.getId(),
                  stoner.getToxicBlowpipe().blowpipeAmmo.getAmount(),
                  true);
        }
        stoner.getToxicBlowpipe().blowpipeAmmo = null;
        return true;
      case 4:
        ask(stoner, 12926);
        stoner.getAttributes().set("ASK_KEY", 0);
        return true;
    }
    return false;
  }

  public static void ask(Stoner stoner, int itemId) {
    ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);
    String[][] info = {
      {"Are you sure you want to destroy this object?", "14174"},
      {"Yes.", "14175"},
      {"No.", "14176"},
      {"", "14177"},
      {"", "14182"},
      {"If you uncharge the blowpipe, all scales and darts will fall out.", "14183"},
      {itemDef.getName(), "14184"}
    };
    stoner.send(new SendUpdateItemsAlt(14171, itemId, 1, 0));
    for (int i = 0; i < info.length; i++) {
      stoner.send(new SendString(info[i][0], Integer.parseInt(info[i][1])));
    }
    stoner.send(new SendChatBoxInterface(14170));
  }

  public static void degrade(Stoner stoner) {
    ToxicBlowpipe blowpipe = stoner.getToxicBlowpipe();
    blowpipe.blowpipeCharge -= 2;

    Item cape = stoner.getEquipment().getItems()[1];
    if (cape != null && (cape.getId() == 10499 || cape.getId() == 10498)) {
      if (Math.random() > 1 - 1 / 4.0) {
        if (stoner.getCombat().getAssaulting().getLocation() != null
            && Math.random() > 1 - 1 / 3.0) {
          stoner
              .getGroundItems()
              .drop(
                  blowpipe.blowpipeAmmo.getSingle(),
                  stoner.getCombat().getAssaulting().getLocation());
        }
        blowpipe.blowpipeAmmo.remove(1);
      }
    } else {
      if (stoner.getCombat().getAssaulting().getLocation() != null && Math.random() > 1 - 1 / 3.0) {
        stoner
            .getGroundItems()
            .drop(
                blowpipe.blowpipeAmmo.getSingle(),
                stoner.getCombat().getAssaulting().getLocation());
      }
      blowpipe.blowpipeAmmo.remove(1);
    }
    if (blowpipe.blowpipeCharge == 0 || blowpipe.blowpipeAmmo.getAmount() == 0) {
      if (blowpipe.blowpipeAmmo.getAmount() == 0) {
        blowpipe.blowpipeAmmo = null;
      }
      stoner.send(
          new SendMessage(
              "The blowpipe needs to be charged with Zulrah's scales and loaded with darts."));
    }
    if (blowpipe.blowpipeCharge == 0 && blowpipe.blowpipeAmmo == null) {
      stoner.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].setId(12924);
    }
  }

  public Item getBlowpipeAmmo() {
    return blowpipeAmmo;
  }

  public int getBlowpipeCharge() {
    return blowpipeCharge;
  }
}
