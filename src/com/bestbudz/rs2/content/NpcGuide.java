package com.bestbudz.rs2.content;

import com.bestbudz.core.definitions.ItemDropDefinition;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNpcDisplay;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

public class NpcGuide {

  private static final int MONSTER_GUIDE_INTERFACE_ID = 59800;

  private static final int INTERFACE_ITEM_CONTAINER = 59806;

  private static final int INTERFACE_TITLE_ID = 59805;

  private static final int INTERFACE_STRING_CONTAINER = 59821;

  public static void open(Stoner stoner, final int npcId) {

    NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

    if (npcDef == null) {
      stoner.send(
          new SendMessage(
              "@red@The ID "
                  + Utility.format(npcId)
                  + " does not exist in our database! Please check the NPC list."));
      return;
    }

    ItemDropDefinition table = GameDefinitionLoader.getItemDropDefinition(npcId);
    if (table == null) {
      clear(stoner);

      stoner.send(new SendInterface(MONSTER_GUIDE_INTERFACE_ID));
      stoner.send(new SendNpcDisplay(npcId, npcDef.getSize() > 1 ? 40 : 100));
      stoner.send(
          new SendString(
              "@or1@Monster Information | @gre@" + npcDef.getName(), INTERFACE_TITLE_ID));
      for (int i = 0; i <= 3; i++) {
        stoner.send(new SendString(getInfo(npcDef, i), INTERFACE_STRING_CONTAINER + i));
      }
      return;
    }

    Item[] drops = table.getMostExpensiveDrops(8);

    if (drops == null) {
      DialogueManager.sendStatement(stoner, "Comparable returned null array.");
      return;
    }

    clear(stoner);

    stoner.send(new SendInterface(MONSTER_GUIDE_INTERFACE_ID));
    stoner.send(new SendNpcDisplay(npcId, npcDef.getSize() > 1 ? 40 : 100));
    stoner.send(
        new SendString("@or1@Monster Information | @gre@" + npcDef.getName(), INTERFACE_TITLE_ID));

    stoner.getClient().queueOutgoingPacket(new SendUpdateItems(INTERFACE_ITEM_CONTAINER, drops));

    for (int i = 0; i <= 3; i++) {
      stoner.send(new SendString(getInfo(npcDef, i), INTERFACE_STRING_CONTAINER + i));
    }
  }

  private static void clear(Stoner stoner) {
    for (int loop = 0; loop < 3; loop++) {
      stoner.send(new SendString("", INTERFACE_STRING_CONTAINER + loop));
    }
    stoner.getClient().queueOutgoingPacket(new SendUpdateItems(INTERFACE_ITEM_CONTAINER, null));
  }

  private static String getInfo(NpcDefinition npcDef, int index) {
    switch (index) {
      case 0:
        return "@or1@".concat(npcDef.getName() + ":");
      case 1:
        return "@or1@ID: @gre@".concat(String.valueOf(npcDef.getId()));
      case 2:
        return "@or1@Grade: @gre@".concat(String.valueOf(npcDef.getGrade()));
      case 3:
        return "@or1@Can assault: @gre@".concat(String.valueOf(npcDef.isAssaultable()));
      default:
        return "Error";
    }
  }
}
