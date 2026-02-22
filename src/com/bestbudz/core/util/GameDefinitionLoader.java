package com.bestbudz.core.util;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.definitions.CombatSpellDefinition;
import com.bestbudz.core.definitions.EquipmentDefinition;
import com.bestbudz.core.definitions.FoodDefinition;
import com.bestbudz.core.definitions.ItemBonusDefinition;
import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.definitions.ItemDropDefinition;
import com.bestbudz.core.definitions.ItemDropDefinition.ItemDrop;
import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.definitions.NpcSpawnDefinition;
import com.bestbudz.core.definitions.PotionDefinition;
import com.bestbudz.core.definitions.SagittariusVigourDefinition;
import com.bestbudz.core.definitions.SagittariusWeaponDefinition;
import com.bestbudz.core.definitions.ShopDefinition;
import com.bestbudz.core.definitions.SpecialAssaultDefinition;
import com.bestbudz.core.definitions.WeaponDefinition;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Logger;

public class GameDefinitionLoader {

  private static final Logger logger = Logger.getLogger(GameDefinitionLoader.class.getSimpleName());

  private static final Gson gson = new Gson();
  private static final Map<Integer, Integer> rareDropChances = new HashMap<Integer, Integer>();
  private static final Map<Integer, byte[][]> itemRequirements = new HashMap<Integer, byte[][]>();
  private static final Map<Integer, ItemDefinition> itemDefinitions =
      new HashMap<Integer, ItemDefinition>();
  private static final Map<Integer, NpcDefinition> npcDefinitions =
      new HashMap<Integer, NpcDefinition>();
  private static final Map<Integer, SpecialAssaultDefinition> specialAssaultDefinitions =
      new HashMap<Integer, SpecialAssaultDefinition>();
  private static final Map<Integer, SagittariusWeaponDefinition> sagittariusWeaponDefinitions =
      new HashMap<Integer, SagittariusWeaponDefinition>();
  private static final Map<Integer, WeaponDefinition> weaponDefinitions =
      new HashMap<Integer, WeaponDefinition>();
  private static final Map<Integer, FoodDefinition> foodDefinitions =
      new HashMap<Integer, FoodDefinition>();
  private static final Map<Integer, PotionDefinition> potionDefinitions =
      new HashMap<Integer, PotionDefinition>();
  private static final Map<Integer, EquipmentDefinition> equipmentDefinitions =
      new HashMap<Integer, EquipmentDefinition>();
  private static final Map<Integer, ItemBonusDefinition> itemBonusDefinitions =
      new HashMap<Integer, ItemBonusDefinition>();
  private static final Map<Integer, CombatSpellDefinition> combatSpellDefinitions =
      new HashMap<Integer, CombatSpellDefinition>();
  private static final Map<Integer, NpcCombatDefinition> npcCombatDefinitions =
      new HashMap<Integer, NpcCombatDefinition>();
  private static final Map<Integer, SagittariusVigourDefinition> sagittariusVigourDefinitions =
      new HashMap<Integer, SagittariusVigourDefinition>();
  private static final Map<Integer, ItemDropDefinition> mobDropDefinitions =
      new HashMap<Integer, ItemDropDefinition>();
  private static int[][] alternates;

  private GameDefinitionLoader() {}

  private static <T> List<T> loadJsonList(String path, Type type) throws IOException {
    try (FileReader reader = new FileReader(path)) {
      return gson.fromJson(reader, type);
    }
  }

  public static final void clearAlternates() {
    alternates = null;
  }

  public static final void declare() {
    logger.info("All GameDefinitions have been loaded.");
  }

  public static void dumpSizes() {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("./NPCSizes.txt"));

      for (NpcDefinition i : npcDefinitions.values()) {
        if (i != null) {
          writer.write(i.getId() + ":" + i.getSize());
          writer.newLine();
        }
      }

      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Sizes dumped.");
  }

  public static int getAlternate(int id) {
    if (alternates == null || id < 0 || id >= alternates.length) {
      return 0;
    }
    return alternates[id][0];
  }

  public static CombatSpellDefinition getCombatSpellDefinition(int id) {
    return combatSpellDefinitions.get(id);
  }

  public static EquipmentDefinition getEquipmentDefinition(int id) {
    return equipmentDefinitions.get(id);
  }

  public static FoodDefinition getFoodDefinition(int id) {
    return foodDefinitions.get(id);
  }

  public static int getHighAlchemyValue(int id) {
    ItemDefinition def = getItemDef(id);

    if (def == null) {
      return 1;
    }

    Item item = new Item(id);

    if (def.isNote()) {
      item.unNote();
    }

    return item.getDefinition().getHighAlch();
  }

  public static ItemBonusDefinition getItemBonusDefinition(int i) {
    return itemBonusDefinitions.get(i);
  }

  public static ItemDefinition getItemDef(int i) {
    return itemDefinitions.get(i);
  }

  public static ItemDropDefinition getItemDropDefinition(int id) {
    return mobDropDefinitions.get(id);
  }

  public static byte[][] getItemRequirements(int id) {
    return itemRequirements.get(id);
  }

  public static int getLowAlchemyValue(int id) {
    ItemDefinition def = getItemDef(id);

    if (def == null) {
      return 1;
    }

    Item item = new Item(id);

    if (def.isNote()) {
      item.unNote();
    }

    return item.getDefinition().getLowAlch();
  }

  public static NpcCombatDefinition getNpcCombatDefinition(int id) {
    return npcCombatDefinitions.get(id);
  }

  public static NpcDefinition getNpcDefinition(int id) {
    return npcDefinitions.get(id);
  }

  public static PotionDefinition getPotionDefinition(int id) {
    return potionDefinitions.get(id);
  }

  public static SagittariusVigourDefinition getSagittariusVigourDefinition(int id) {
    return sagittariusVigourDefinitions.get(id);
  }

  public static SagittariusWeaponDefinition getSagittariusWeaponDefinition(int id) {
    return sagittariusWeaponDefinitions.get(id);
  }

  public static int getRareDropChance(int id) {
    if (!rareDropChances.containsKey(id)) {
      return 80;
    }

    return rareDropChances.get(id).intValue();
  }

  public static SpecialAssaultDefinition getSpecialDefinition(int id) {
    return specialAssaultDefinitions.get(id);
  }

  public static int getStoreBuyFromValue(int id) {
    ItemDefinition def = getItemDef(id);

    if (def == null) {
      return 1;
    }

    if (def.isNote()) {
      Item item = new Item(id);
      item.unNote();
      def = item.getDefinition();
    }

    double ratio = 0;

    if (def.getLowAlch() == 0 || (def.getHighAlch() == 0 && def.getLowAlch() == 0)) {
      ratio = 1;
    } else {
      ratio = def.getHighAlch() / (double) def.getLowAlch();
    }

    return (int) Math.ceil(def.getGeneralPrice() * ratio);
  }

  public static int getStoreSellToValue(int id) {
    ItemDefinition def = getItemDef(id);

    if (def == null) {
      return 1;
    }

    Item item = new Item(id);

    if (item.getDefinition().isNote()) {
      item.unNote();
    }

    return item.getDefinition().getGeneralPrice();
  }

  public static WeaponDefinition getWeaponDefinition(int id) {
    return weaponDefinitions.get(id);
  }

  public static final void loadAlternateIds() throws IOException {
    Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
    List<Map<String, Object>> list = loadJsonList("./data/def/ObjectAlternates.json", type);
    int maxId = 0;
    for (Map<String, Object> entry : list) {
      int id = ((Number) entry.get("id")).intValue();
      if (id > maxId) maxId = id;
    }
    alternates = new int[maxId + 1][1];
    for (Map<String, Object> entry : list) {
      int id = ((Number) entry.get("id")).intValue();
      int alt = ((Number) entry.get("alt")).intValue();
      alternates[id][0] = alt;
    }
    logger.info("Loaded " + Utility.format(list.size()) + " alternative object IDs.");
  }

  public static void loadCombatSpellDefinitions() throws IOException {
    List<CombatSpellDefinition> list =
        loadJsonList(
            "./data/def/mage/CombatSpellDefinitions.json",
            new TypeToken<List<CombatSpellDefinition>>() {}.getType());
    for (CombatSpellDefinition definition : list) {
      combatSpellDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " combat spell definitions.");
  }

  public static void loadEquipmentDefinitions() throws IOException {
    List<EquipmentDefinition> list =
        loadJsonList(
            "./data/def/items/EquipmentDefinitions.json",
            new TypeToken<List<EquipmentDefinition>>() {}.getType());

    for (EquipmentDefinition definition : list) {
      equipmentDefinitions.put(definition.getId(), definition);
    }

    logger.info("Loaded " + Utility.format(list.size()) + " equipment definitions.");
  }

  public static void loadFoodDefinitions() throws IOException {
    List<FoodDefinition> list =
        loadJsonList(
            "./data/def/items/FoodDefinitions.json",
            new TypeToken<List<FoodDefinition>>() {}.getType());
    for (FoodDefinition definition : list) {
      foodDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " food definitions.");
  }

  public static void loadItemBonusDefinitions() throws IOException {
    List<ItemBonusDefinition> list =
        loadJsonList(
            "./data/def/items/ItemBonusDefinitions.json",
            new TypeToken<List<ItemBonusDefinition>>() {}.getType());
    for (ItemBonusDefinition definition : list) {
      itemBonusDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " item bonus definitions.");
  }

  public static void loadItemDefinitions() throws IOException {
    List<ItemDefinition> list =
        loadJsonList(
            "./data/def/items/ItemDefinitions.json",
            new TypeToken<List<ItemDefinition>>() {}.getType());
    for (ItemDefinition definition : list) {
      itemDefinitions.put(definition.getId(), definition);
    }

    logger.info("Loaded " + Utility.format(list.size()) + " item definitions.");
  }

  public static Map<Integer, ItemDefinition> getItemDefinitions() {
    return itemDefinitions;
  }

  public static Map<Integer, ItemBonusDefinition> getItemBonusDefinitions() {
    return itemBonusDefinitions;
  }

  public static void loadNpcCombatDefinitions() throws IOException {
    List<NpcCombatDefinition> list =
        loadJsonList(
            "./data/def/npcs/NpcCombatDefinitions.json",
            new TypeToken<List<NpcCombatDefinition>>() {}.getType());
    for (NpcCombatDefinition definition : list) {
      npcCombatDefinitions.put(definition.getId(), definition);
    }

    logger.info("Loaded " + Utility.format(list.size()) + " npc combat definitions.");
  }

  public static void loadNpcDefinitions() throws IOException {
    List<NpcDefinition> list =
        loadJsonList(
            "./data/def/npcs/NpcDefinitions.json",
            new TypeToken<List<NpcDefinition>>() {}.getType());
    for (NpcDefinition definition : list) {
      npcDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " NPC definitions.");
  }

  public static Map<Integer, NpcDefinition> getNpcDefinitions() {
    return npcDefinitions;
  }

  public static Map<Integer, ItemDropDefinition> getMobDropDefinitions() {
    return mobDropDefinitions;
  }

  public static void loadNpcDropDefinitions() throws IOException {
    List<ItemDropDefinition> list =
        loadJsonList(
            "./data/def/npcs/ItemDropDefinitions.json",
            new TypeToken<List<ItemDropDefinition>>() {}.getType());
    for (ItemDropDefinition def : list) {
      mobDropDefinitions.put(def.getId(), def);
    }

    for (ItemDropDefinition i : mobDropDefinitions.values()) {
      if (npcCombatDefinitions.get(i.getId()) == null) {
        mobDropDefinitions.remove(0);
      }
    }

    for (ItemDropDefinition i : mobDropDefinitions.values()) {
      boolean always = false;
      boolean common = false;
      boolean uncommon = false;
      boolean rare = false;

      if (i.getConstant() != null && i.getConstant().getDrops() != null) {
        always = true;
      }

      if (i.getCommon() != null && i.getCommon().getDrops() != null) {
        common = true;
      }

      if (i.getUncommon() != null && i.getUncommon().getDrops() != null) {
        uncommon = true;
      }

      if (i.getRare() != null && i.getRare().getDrops() != null) {
        rare = true;
      }

      if (!always && !common && !uncommon && !rare) {
        continue;
      }

      if (always) {

        for (int index = 0; index < i.getConstant().getDrops().length; index++) {
          ItemDrop drop = i.getConstant().getDrops()[index];

          if (index + 1 == i.getConstant().getDrops().length) {

          } else {

          }
        }

      }

      if (common) {
        for (int index = 0; index < i.getCommon().getDrops().length; index++) {
          ItemDrop drop = i.getCommon().getDrops()[index];

          if (index + 1 == i.getCommon().getDrops().length && !uncommon && !rare) {

          } else {

          }
        }
      }

      if (uncommon) {
        for (int index = 0; index < i.getUncommon().getDrops().length; index++) {
          ItemDrop drop = i.getUncommon().getDrops()[index];

          if (index + 1 == i.getUncommon().getDrops().length && !rare) {

          } else {

          }
        }
      }

      if (rare) {
        for (int index = 0; index < i.getRare().getDrops().length; index++) {
          ItemDrop drop = i.getRare().getDrops()[index];

          if (index + 1 == i.getRare().getDrops().length) {

          } else {

          }
        }
      }

    }

    logger.info("Loaded " + Utility.format(list.size()) + " npc drops.");
  }

  public static void loadNpcSpawns() throws IOException {
    List<NpcSpawnDefinition> list =
        loadJsonList(
            "./data/def/npcs/NpcSpawnDefinitions.json",
            new TypeToken<List<NpcSpawnDefinition>>() {}.getType());
    for (NpcSpawnDefinition def : list) {
      if (Region.getRegion(def.getLocation().getX(), def.getLocation().getY()) == null) {
        continue;
      }

      if (npcDefinitions.get(def.getId()).isAssaultable()
          && npcCombatDefinitions.get(def.getId()) == null) {
        continue;
      }

      Mob m = new Mob(def.getId(), def.isWalk(), def.getLocation());

      if (def.getFace() > 0) {
        m.setFaceDir(def.getFace());
      } else {
        m.setFaceDir(-1);
      }
    }
    logger.info("Loaded " + Utility.format(list.size()) + " NPC spawns.");
  }

  public static void loadPotionDefinitions() throws IOException {
    List<PotionDefinition> list =
        loadJsonList(
            "./data/def/items/PotionDefinitions.json",
            new TypeToken<List<PotionDefinition>>() {}.getType());
    for (PotionDefinition definition : list) {
      if (definition.getName() == null) {
        definition.setName(itemDefinitions.get(definition.getId()).getName());
      }
      potionDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " potion definitions.");
  }

  public static void loadSagittariusVigourDefinitions() throws IOException {
    List<SagittariusVigourDefinition> list =
        loadJsonList(
            "./data/def/items/SagittariusVigourDefinitions.json",
            new TypeToken<List<SagittariusVigourDefinition>>() {}.getType());
    for (SagittariusVigourDefinition definition : list) {
      sagittariusVigourDefinitions.put(definition.getId(), definition);
    }
    logger.info(
        "Loaded " + Utility.format(list.size()) + " sagittarius vigour bonus definitions.");
  }

  public static void loadSagittariusWeaponDefinitions() throws IOException {
    List<SagittariusWeaponDefinition> list =
        loadJsonList(
            "./data/def/items/SagittariusWeaponDefinitions.json",
            new TypeToken<List<SagittariusWeaponDefinition>>() {}.getType());
    for (SagittariusWeaponDefinition definition : list) {
      sagittariusWeaponDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " sagittarius weapon definitions.");
  }

  public static final void loadRareDropChances() throws IOException {
    Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
    List<Map<String, Object>> list = loadJsonList("./data/def/npcs/DropChances.json", type);
    for (Map<String, Object> entry : list) {
      int id = ((Number) entry.get("id")).intValue();
      int chance = ((Number) entry.get("chance")).intValue();
      rareDropChances.put(id, chance);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " rare drop chances.");
  }

  public static void loadShopDefinitions() throws IOException {
    List<ShopDefinition> list =
        loadJsonList(
            "./data/def/items/ShopDefinitions.json",
            new TypeToken<List<ShopDefinition>>() {}.getType());
    for (ShopDefinition def : list) {
      Shop.getShops()[def.getId()] =
          new Shop(def.getId(), def.getItems(), def.isGeneral(), def.getName());
    }
    logger.info("Loaded " + Utility.format(list.size()) + " shops.");
  }

  public static void loadSpecialAssaultDefinitions() throws IOException {
    List<SpecialAssaultDefinition> list =
        loadJsonList(
            "./data/def/items/SpecialAssaultDefinitions.json",
            new TypeToken<List<SpecialAssaultDefinition>>() {}.getType());
    for (SpecialAssaultDefinition definition : list) {
      specialAssaultDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " special assault definitions.");
  }

  public static void loadWeaponDefinitions() throws IOException {
    List<WeaponDefinition> list =
        loadJsonList(
            "./data/def/items/WeaponDefinitions.json",
            new TypeToken<List<WeaponDefinition>>() {}.getType());
    for (WeaponDefinition definition : list) {
      weaponDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " weapon definitions.");
  }

  public static Map<Integer, WeaponDefinition> getWeaponDefinitions() {
    return weaponDefinitions;
  }

  public static void setNotTradable(int id) {
    itemDefinitions.get(id).setUntradable();
  }

  public static void setRequirements() {
    for (Object def : equipmentDefinitions.values().toArray()) {
      EquipmentDefinition definition = (EquipmentDefinition) def;

      if (definition == null || definition.getRequirements() == null) {
        continue;
      }

      byte[][] requirements = new byte[Professions.PROFESSION_COUNT][2];
      int count = 0;

      for (int i = 0; i < definition.getRequirements().length; i++) {
        if (definition.getRequirements()[i] == 1) {
          continue;
        } else {
          if (count < Professions.PROFESSION_COUNT) {
            requirements[count][0] = (byte) i;
            requirements[count][1] = definition.getRequirements()[i];
          }
          count++;
        }
      }

      byte[][] set = new byte[count][2];

      for (int i = 0; i < count; i++) {
        if (count < Professions.PROFESSION_COUNT) {
          set[i][0] = requirements[i][0];
          set[i][1] = requirements[i][1];
        }
      }

      itemRequirements.put(((EquipmentDefinition) def).getId(), set);

      ((EquipmentDefinition) def).setRequirements(null);
    }
  }

  public static void writeDropPreference() {
    try {
      Queue<Item> items = new PriorityQueue<Item>(42);

      for (ItemDefinition i : itemDefinitions.values()) {
        if (!i.isTradable() || i.getNoteId() != -1 && items.contains(new Item(i.getNoteId()))) {
          continue;
        }

        items.add(new Item(i.getId()));
      }

      BufferedWriter writer = new BufferedWriter(new FileWriter("./DropSettings.txt"));

      Item item = null;

      while ((item = items.poll()) != null) {
        writer.write(item.getId() + ":" + item.getDefinition().getName());
        writer.newLine();
      }

      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
