package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.io.sqlite.SaveCache;
import java.util.HashMap;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendColor;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;

public class StarterKit {

  private static int selected = 202051;

  public static boolean handle(Stoner stoner, int buttonId) {

    StarterData data = StarterData.starterKits.get(buttonId);

    if (data == null) {
      if (extraButton(stoner, buttonId)) {
        return false;
      }
      return false;
    }

    int color = 0xBA640D;

    if (buttonId == 202051) {
      stoner.send(new SendColor(51766, color));
      stoner.send(new SendColor(51767, 0xF7AA25));
      stoner.send(new SendColor(51768, 0xF7AA25));
    } else if (buttonId == 202052) {
      stoner.send(new SendColor(51766, 0xF7AA25));
      stoner.send(new SendColor(51767, color));
      stoner.send(new SendColor(51768, 0xF7AA25));
    } else if (buttonId == 202053) {
      stoner.send(new SendColor(51766, 0xF7AA25));
      stoner.send(new SendColor(51767, 0xF7AA25));
      stoner.send(new SendColor(51768, color));
    }

    selected = buttonId;
    stoner.send(new SendColor(data.getString(), 0xC71C1C));
    String name = Utility.capitalize(data.name().toLowerCase().replaceAll("_", " "));
    stoner.send(new SendString("Stash (@red@" + name + "</col>):", 51757));

    for (int i = 0; i < 30; i++) {
      stoner.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(51758, 0, 0, i));
    }

    for (int i = 0; i < data.getItems().length; i++) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendUpdateItemsAlt(
                  51758, data.getItems()[i].getId(), data.getItems()[i].getAmount(), i));
    }

    for (int i = 0; i < data.getDescription().length; i++) {
      stoner.send(new SendString(data.getDescription()[i], 51760 + i));
    }

    return true;
  }

  public static boolean extraButton(Stoner stoner, int button) {
    if (stoner.getInterfaceManager().main != 51750) {
      return false;
    }
    switch (button) {
      case 202054:
        stoner.send(new SendConfig(1085, 0));
        StarterKit.handle(stoner, 202051);
        return true;
      case 202055:
        stoner.send(new SendConfig(1085, 1));
        StarterKit.handle(stoner, 202052);
        return true;
      case 202056:
        stoner.send(new SendConfig(1085, 2));
        StarterKit.handle(stoner, 202053);
        return true;
      case 202057:
        StarterKit.confirm(stoner);
        return true;
    }
    return false;
  }

  public static void confirm(Stoner stoner) {

    StarterData data = StarterData.starterKits.get(selected);

    if (data == null || stoner.getDelay().elapsed() < 1000) {
      return;
    }

    if (stoner.getInterfaceManager().main != 51750) {
      stoner.send(new SendRemoveInterfaces());
      return;
    }

    stoner.getDelay().reset();
    String name = Utility.capitalize(data.name().toLowerCase().replaceAll("_", " "));
    stoner.send(new SendRemoveInterfaces());
    stoner.send(
        new SendMessage(
            "@red@You will be playing as " + Utility.getAOrAn(name) + " " + name + "."));
    stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
    stoner.setStarter(false);
    stoner.send(new SendInterface(3559));

    for (int i = 0; i < StonerConstants.SIDEBAR_INTERFACE_IDS.length; i++) {
      stoner.send(new SendSidebarInterface(i, StonerConstants.SIDEBAR_INTERFACE_IDS[i]));
    }

    stoner.send(new SendSidebarInterface(5, 5608));
    stoner.send(new SendSidebarInterface(6, 1151));
    stoner.setRights(data.getRights());
    stoner.getUpdateFlags().setUpdateRequired(true);

    switch (selected) {
      case 202052:
      case 202053:
      case 202051:
		  if (!Boolean.TRUE.equals(stoner.getAttributes().get("starter_ip_logged"))) {
			  stoner.getBox().addItems(data.getItems());
			  stoner.getAttributes().set("starter_ip_logged", true);
			  SaveCache.markDirty(stoner);
		  }

		  stoner.setRights(0);
        break;
    }
  }

  public enum StarterData {
    STONER(
        202051,
        51766,
        0,
        new Item[] {
          new Item(995, 5000000),
          new Item(7945, 100),
          new Item(11879, 1),
          new Item(11881, 1),
          new Item(11883, 1),
          new Item(12009, 1),
          new Item(11885, 1),
          new Item(12859, 1),
          new Item(11738, 1),
          new Item(1511, 100),
          new Item(1521, 100),
          new Item(1519, 100),
          new Item(1517, 100),
          new Item(1515, 100),
          new Item(1513, 100),
          new Item(6199, 1),
          new Item(6857, 1),
          new Item(775, 1),
          new Item(1837, 1),
          new Item(13223, 1),
          new Item(6575, 1),
          new Item(6577, 1),
          new Item(12647, 1)
        },
        "",
        "",
        ""),

    DEALER(
        202052,
        51767,
        11,
        new Item[] {
          new Item(995, 5000000),
          new Item(7947, 100),
          new Item(2437, 10),
          new Item(2441, 10),
          new Item(2443, 10),
          new Item(3025, 10),
          new Item(12414, 1),
          new Item(12415, 1),
          new Item(12416, 1),
          new Item(12417, 1),
          new Item(12418, 1),
          new Item(4587, 1),
          new Item(6585, 1),
          new Item(7461, 1),
          new Item(11840, 1),
          new Item(2675, 1),
          new Item(2673, 1),
          new Item(2671, 1),
          new Item(2669, 1),
          new Item(13223, 1),
          new Item(12654, 1)
        },
        "",
        "",
        ""),

    GROWER(
        202053,
        51768,
        12,
        new Item[] {
          new Item(995, 5000000),
          new Item(7947, 100),
          new Item(2445, 10),
          new Item(3041, 10),
          new Item(3025, 10),
          new Item(892, 500),
          new Item(4675, 1),
          new Item(2579, 1),
          new Item(2577, 1),
          new Item(4214, 1),
          new Item(861, 1),
          new Item(10376, 1),
          new Item(10380, 1),
          new Item(6585, 1),
          new Item(13223, 1),
          new Item(12648, 1)
        },
        "",
        "",
        "");

    private static final HashMap<Integer, StarterData> starterKits =
        new HashMap<Integer, StarterData>();

    static {
      for (final StarterData starters : StarterData.values()) {
        StarterData.starterKits.put(starters.button, starters);
      }
    }

    private final int button;
    private final int stringId;
    private final int rights;
    private final Item[] items;
    private final String[] descriptions;

    StarterData(int button, int stringId, int rights, Item[] items, String... descriptions) {
      this.button = button;
      this.stringId = stringId;
      this.rights = rights;
      this.items = items;
      this.descriptions = descriptions;
    }

    public int getButton() {
      return button;
    }

    public int getString() {
      return stringId;
    }

    public int getRights() {
      return rights;
    }

    public Item[] getItems() {
      return items;
    }

    public String[] getDescription() {
      return descriptions;
    }
  }
}
