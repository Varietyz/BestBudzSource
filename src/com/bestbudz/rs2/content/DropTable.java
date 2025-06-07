package com.bestbudz.rs2.content;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.definitions.ItemDropDefinition;
import com.bestbudz.core.definitions.ItemDropDefinition.ItemDrop;
import com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DropTable {

  private static final int STRING = 59901;

  public static void open(Stoner stoner) {

    clear(stoner);

    for (int i = 0; i < 50; i++) {
      stoner.send(new SendString("", STRING + i));
    }

    stoner.send(new SendString("", 59818));

    stoner.send(new SendInterface(59800));
  }

  public static void searchNpc(Stoner stoner, String name) {
    name = name.trim().toLowerCase();

    List<NpcDefinition> npcdefs =
        GameDefinitionLoader.getNpcDefinitions().values().stream()
            .filter(
                npcdef -> {
                  if (npcdef == null
                      || !GameDefinitionLoader.getMobDropDefinitions()
                          .containsKey(npcdef.getId())) {
                    return false;
                  }
                  return npcdef.isAssaultable();
                })
            .collect(Collectors.toList());

    HashMap<Integer, Integer> toAdd = new HashMap<>();

    for (NpcDefinition def : npcdefs) {
      if (def.getName().toLowerCase().contains(name)) {
        if (toAdd.size() < 3) {
          toAdd.put(233253 + toAdd.size(), def.getId());
        } else {
          toAdd.put(233997 + toAdd.size(), def.getId());
        }
        stoner.send(new SendString("</col> " + def.getName(), STRING + toAdd.size() - 1));
      }
    }

    for (int i = toAdd.size(); i < 50; i++) {
      stoner.send(new SendString("", STRING + i));
    }

    stoner.send(new SendString("</col>Monster Drop Guide - Results @red@" + toAdd.size(), 59805));

    stoner.getAttributes().set("DROPTABLE_SEARCH", toAdd);
  }

  public static void searchItem(Stoner stoner, String name) {
    name = name.trim().toLowerCase();

    HashMap<Integer, Integer> toAdd = new HashMap<>();

    List<NpcDefinition> npcdefs =
        GameDefinitionLoader.getNpcDefinitions().values().stream()
            .filter(
                npcdef -> {
                  if (npcdef == null
                      || !GameDefinitionLoader.getMobDropDefinitions()
                          .containsKey(npcdef.getId())) {
                    return false;
                  }
                  return npcdef.isAssaultable();
                })
            .collect(Collectors.toList());

    for (NpcDefinition def : npcdefs) {
      ItemDropDefinition dropDef = GameDefinitionLoader.getMobDropDefinitions().get(def.getId());

      boolean found = false;

      if (dropDef.getConstant() != null && dropDef.getConstant().getDrops() != null) {
        for (ItemDrop drop : dropDef.getConstant().getDrops()) {
          if (GameDefinitionLoader.getItemDef(drop.getId())
              .getName()
              .toLowerCase()
              .contains(name)) {
            found = true;
          }
        }
      }

      if (!found && dropDef.getCommon() != null && dropDef.getCommon().getDrops() != null) {
        for (ItemDrop drop : dropDef.getCommon().getDrops()) {
          if (GameDefinitionLoader.getItemDef(drop.getId())
              .getName()
              .toLowerCase()
              .contains(name)) {
            found = true;
          }
        }
      }

      if (!found && dropDef.getUncommon() != null && dropDef.getUncommon().getDrops() != null) {
        for (ItemDrop drop : dropDef.getUncommon().getDrops()) {
          if (GameDefinitionLoader.getItemDef(drop.getId())
              .getName()
              .toLowerCase()
              .contains(name)) {
            found = true;
          }
        }
      }

      if (!found && dropDef.getRare() != null && dropDef.getRare().getDrops() != null) {
        for (ItemDrop drop : dropDef.getRare().getDrops()) {
          if (GameDefinitionLoader.getItemDef(drop.getId())
              .getName()
              .toLowerCase()
              .contains(name)) {
            found = true;
          }
        }
      }

      if (found) {
        if (toAdd.size() < 3) {
          toAdd.put(233253 + toAdd.size(), def.getId());
        } else {
          toAdd.put(233997 + toAdd.size(), def.getId());
        }
        stoner.send(new SendString("</col> " + def.getName(), STRING + toAdd.size() - 1));
      }
    }

    stoner.send(new SendString("</col>Monster Drop Guide - Results @red@" + toAdd.size(), 59805));

    for (int i = toAdd.size(); i < 50; i++) {
      stoner.send(new SendString("", STRING + i));
    }

    stoner.getAttributes().set("DROPTABLE_SEARCH", toAdd);
  }

  public static void displayNpc(Stoner stoner, int npcId) {

    NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

    if (npcDef == null) {
      stoner.send(
          new SendMessage("It appears this NPC is nulled! Please report it to a staff member."));
      return;
    }

    clear(stoner);

    ItemDropDefinition table = GameDefinitionLoader.getItemDropDefinition(npcId);

    if (table == null) {
      return;
    }

    stoner.monsterSelected = npcId;

    stoner.send(new SendString("</col>Name: @bla@" + npcDef.getName(), 59806));
    stoner.send(new SendString("</col>Grade: @bla@" + npcDef.getGrade(), 59807));
    stoner.send(new SendString("", 59818));

    stoner.send(new SendMessage("Now displaying drop tables of " + npcDef.getName() + "."));

    Item[] items = new Item[250];

    sendBest(stoner, table.getConstant(), items, 0);
    sendBest(stoner, table.getCommon(), items, 1);
    sendBest(stoner, table.getUncommon(), items, 2);

    List<Item> rares = new ArrayList<>();
    List<Item> v_rares = new ArrayList<>();

    for (ItemDrop drop : table.getRare().getDrops()) {
      if (GameDefinitionLoader.getRareDropChance(drop.getId()) <= 50) {
        v_rares.add(new Item(drop.getId(), drop.getMax()));
      } else {
        rares.add(new Item(drop.getId(), drop.getMax()));
      }
    }

    Collections.sort(
        rares,
        (first, second) ->
            second.getAmount() * second.getDefinition().getGeneralPrice()
                - first.getAmount() * first.getDefinition().getGeneralPrice());

    for (int i = 0; i < 250; i += 5) {
      items[3 + i] = i / 5 < rares.size() ? rares.get(i / 5) : null;
    }

    Collections.sort(
        v_rares,
        (first, second) ->
            GameDefinitionLoader.getRareDropChance(first.getId())
                - GameDefinitionLoader.getRareDropChance(second.getId()));

    for (int i = 0; i < 250; i += 5) {
      items[4 + i] = i / 5 < v_rares.size() ? v_rares.get(i / 5) : null;
    }

    stoner.send(new SendUpdateItems(59813, items));

    stoner.send(new SendInterface(59800));
  }

  private static void sendBest(Stoner stoner, ItemDropTable itemDropTable, Item[] items, int slot) {
    if (itemDropTable == null || itemDropTable.getDrops() == null) {
      for (int i = 0; i < 250; i += 5) {
        items[slot + i] = null;
      }
      return;
    }

    List<Item> itemList = new ArrayList<>();

    for (ItemDrop drop : itemDropTable.getDrops()) {
      if (drop != null) {
        itemList.add(new Item(drop.getId(), drop.getMax()));
      }
    }

    Collections.sort(
        itemList,
        (first, second) ->
            second.getAmount() * second.getDefinition().getGeneralPrice()
                - first.getAmount() * first.getDefinition().getGeneralPrice());

    for (int i = 0; i < 250; i += 5) {
      items[slot + i] = i / 5 < itemList.size() ? itemList.get(i / 5) : null;
    }
  }

  private static void clear(Stoner stoner) {

    stoner.send(new SendString("</col>Name: ", 59806));
    stoner.send(new SendString("</col>Grade: ", 59807));
    stoner.send(new SendString("", 59818));

    stoner.send(new SendUpdateItems(59813, null));
  }

  public static void itemDetails(Stoner stoner, int itemId) {

    ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);

    if (itemDef == null) {
      stoner.send(
          new SendMessage("It appears this item is nulled! Please report it to a staff member."));
      return;
    }

    stoner.send(new SendUpdateItemsAlt(59757, itemDef.getId(), 1, 0));
    stoner.send(new SendString("</col>Item: @gre@" + itemDef.getName(), 59753));
    stoner.send(
        new SendString(
            "</col>Price: @gre@" + Utility.formatPrice(itemDef.getGeneralPrice()), 59754));
    stoner.send(
        new SendString(
            "</col>Tradeable: @gre@" + Utility.formatBoolean(itemDef.isTradable()), 59755));
    stoner.send(
        new SendString("</col>Noted: @gre@" + Utility.formatBoolean(itemDef.isNote()), 59756));

    stoner.send(new SendInterface(59750));
  }
}
