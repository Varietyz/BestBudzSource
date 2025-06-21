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
import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Logger;

public class GameDefinitionLoader {

  private static final Logger logger = Logger.getLogger(GameDefinitionLoader.class.getSimpleName());

  private static final XStream xStream = new XStream();
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
  private static int[][] alternates = new int[53000][1];

  private GameDefinitionLoader() {}

  public static final void clearAlternates() {
    alternates = null;
  }

  public static final void declare() {
    xStream.allowTypes(
        new Class[] {
          com.bestbudz.core.definitions.ItemDropDefinition.class,
          com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.class,
          com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.class,
          com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.class,
          com.bestbudz.core.definitions.ItemDropDefinition.ItemDrop.class,
          com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.ScrollTypes.class,
          com.bestbudz.rs2.entity.Location.class,
          com.bestbudz.rs2.entity.item.Item.class,
          com.bestbudz.rs2.entity.Projectile.class,
          com.bestbudz.rs2.entity.Graphic.class,
          com.bestbudz.rs2.entity.Animation.class,
          com.bestbudz.core.definitions.NpcCombatDefinition.class,
          com.bestbudz.core.definitions.NpcCombatDefinition.Profession.class,
          com.bestbudz.core.definitions.NpcCombatDefinition.Melee.class,
          com.bestbudz.core.definitions.NpcCombatDefinition.Mage.class,
          com.bestbudz.core.definitions.NpcCombatDefinition.Sagittarius.class,
          com.bestbudz.core.definitions.ItemDefinition.class,
          com.bestbudz.core.definitions.ShopDefinition.class,
          com.bestbudz.core.definitions.WeaponDefinition.class,
          com.bestbudz.core.definitions.SpecialAssaultDefinition.class,
          com.bestbudz.core.definitions.SagittariusWeaponDefinition.class,
          com.bestbudz.core.definitions.SagittariusVigourDefinition.class,
          com.bestbudz.core.definitions.FoodDefinition.class,
          com.bestbudz.core.definitions.PotionDefinition.class,
          com.bestbudz.core.definitions.PotionDefinition.ProfessionData.class,
          com.bestbudz.core.definitions.ItemBonusDefinition.class,
          com.bestbudz.core.definitions.CombatSpellDefinition.class,
          com.bestbudz.core.definitions.NpcDefinition.class,
          com.bestbudz.core.definitions.NpcSpawnDefinition.class,
          com.bestbudz.core.definitions.EquipmentDefinition.class,
        });

    xStream.alias("ItemDropDefinition", com.bestbudz.core.definitions.ItemDropDefinition.class);
    xStream.alias("constant", com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.class);
    xStream.alias("common", com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.class);
    xStream.alias("uncommon", com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.class);
    xStream.alias("itemDrop", com.bestbudz.core.definitions.ItemDropDefinition.ItemDrop.class);
    xStream.alias(
        "scroll", com.bestbudz.core.definitions.ItemDropDefinition.ItemDropTable.ScrollTypes.class);

    xStream.alias("location", com.bestbudz.rs2.entity.Location.class);
    xStream.alias("item", com.bestbudz.rs2.entity.item.Item.class);
    xStream.alias("projectile", com.bestbudz.rs2.entity.Projectile.class);
    xStream.alias("graphic", com.bestbudz.rs2.entity.Graphic.class);
    xStream.alias("animation", com.bestbudz.rs2.entity.Animation.class);

    xStream.alias("NpcCombatDefinition", com.bestbudz.core.definitions.NpcCombatDefinition.class);
    xStream.alias("profession", com.bestbudz.core.definitions.NpcCombatDefinition.Profession.class);
    xStream.alias("melee", com.bestbudz.core.definitions.NpcCombatDefinition.Melee.class);
    xStream.alias("mage", com.bestbudz.core.definitions.NpcCombatDefinition.Mage.class);
    xStream.alias(
        "sagittarius", com.bestbudz.core.definitions.NpcCombatDefinition.Sagittarius.class);

    xStream.alias("ItemDefinition", com.bestbudz.core.definitions.ItemDefinition.class);
    xStream.alias("ShopDefinition", com.bestbudz.core.definitions.ShopDefinition.class);
    xStream.alias("WeaponDefinition", com.bestbudz.core.definitions.WeaponDefinition.class);
    xStream.alias(
        "SpecialAssaultDefinition", com.bestbudz.core.definitions.SpecialAssaultDefinition.class);
    xStream.alias(
        "SagittariusWeaponDefinition",
        com.bestbudz.core.definitions.SagittariusWeaponDefinition.class);
    xStream.alias(
        "SagittariusVigourDefinition",
        com.bestbudz.core.definitions.SagittariusVigourDefinition.class);
    xStream.alias("FoodDefinition", com.bestbudz.core.definitions.FoodDefinition.class);
    xStream.alias("PotionDefinition", com.bestbudz.core.definitions.PotionDefinition.class);
    xStream.alias(
        "professionData", com.bestbudz.core.definitions.PotionDefinition.ProfessionData.class);
    xStream.alias("ItemBonusDefinition", com.bestbudz.core.definitions.ItemBonusDefinition.class);
    xStream.alias(
        "CombatSpellDefinition", com.bestbudz.core.definitions.CombatSpellDefinition.class);
    xStream.alias("NpcDefinition", com.bestbudz.core.definitions.NpcDefinition.class);
    xStream.alias("NpcSpawnDefinition", com.bestbudz.core.definitions.NpcSpawnDefinition.class);
    xStream.alias("EquipmentDefinition", com.bestbudz.core.definitions.EquipmentDefinition.class);
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

  public static XStream getxStream() {
    return xStream;
  }

	public static final void loadAlternateIds() {
		try (BufferedReader reader = new BufferedReader(new FileReader("./data/def/ObjectAlternates.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || !line.contains(":")) {
					System.err.println("[AltID] Skipping malformed line: " + line);
					continue;
				}
				try {
					int id = Integer.parseInt(line.substring(0, line.indexOf(":")));
					int alt = Integer.parseInt(line.substring(line.indexOf(":") + 1));
					alternates[id][0] = alt;
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					System.err.println("[AltID] Invalid format: " + line);
				}
			}
		} catch (Exception e) {
			System.err.println("[AltID] Failed to load alternate IDs:");
			e.printStackTrace();
		}

		logger.info("All alternative objects have been loaded.");
	}

  @SuppressWarnings("unchecked")
  public static void loadCombatSpellDefinitions() throws IOException {
    List<CombatSpellDefinition> list =
        (List<CombatSpellDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/mage/CombatSpellDefinitions.xml"));
    for (CombatSpellDefinition definition : list) {
      combatSpellDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " combat spell definitions.");
  }

  @SuppressWarnings("unchecked")
  public static void loadEquipmentDefinitions() throws IOException {
    List<EquipmentDefinition> list =
        (List<EquipmentDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/EquipmentDefinitions.xml"));

    for (EquipmentDefinition definition : list) {

      equipmentDefinitions.put(definition.getId(), definition);
      int size = 0;

      if (definition.getRequirements() != null) {
        byte[] array = definition.getRequirements();

        for (int i = 0; i < array.length; i++) {
          if (array[i] > 1) {
            size++;
          }
        }
      }

      if (size == 0) {
        continue;
      }

      // System.out.println("  {");
      // System.out.println("    \"id\": " + definition.getId() + ",");
      // System.out.println("    \"requirements\": [");
      byte[] array = definition.getRequirements();

      for (int i = 0, complete = 0; i < array.length; i++) {
        if (array[i] == 1) {
          continue;
        }

        // System.out.println("      {");
        // System.out.println("        \"grade\": " + array[i] + ",");

        String name = "";

        switch (i) {
          case 0:
            name = "ASSAULT";
            break;
          case 1:
            name = "AEGIS";
            break;
          case 2:
            name = "VIGOUR";
            break;
          case 3:
            name = "LIFE";
            break;
          case 4:
            name = "SAGITTARIUS";
            break;
          case 5:
            name = "RESONANCE";
            break;
          case 6:
            name = "MAGE";
            break;
          case 7:
            name = "FOODIE";
            break;
          case 8:
            name = "LUMBERING";
            break;
          case 9:
            name = "WOODCARVING";
            break;
          case 10:
            name = "FISHER";
            break;
          case 11:
            name = "PYROMANIAC";
            break;
          case 12:
            name = "HANDINESS";
            break;
          case 13:
            name = "FORGING";
            break;
          case 14:
            name = "QUARRYING";
            break;
          case 15:
            name = "THC-HEMPISTRY";
            break;
          case 16:
            name = "WEEDSMOKING";
            break;
          case 17:
            name = "PET_MASTER";
            break;
          case 18:
            name = "MERCENARY";
            break;
          case 19:
            name = "BANKSTANDING";
            break;
          case 20:
            name = "HUNTER";
            break;
          case 21:
            name = "CONSTRUCTION";
            break;
        }

        complete++;
      //  System.out.println("        \"profession\": \"" + name + "\"");
      //  System.out.println("      }" + (complete >= size ? "" : ","));
      }

     // System.out.println("    ]");
     // System.out.println("  },");
    }

    logger.info("Loaded " + Utility.format(list.size()) + " equipment definitions.");
  }

  public static void main(String[] args) throws IOException {
    declare();
    loadNpcDropDefinitions();
  }

  @SuppressWarnings("unchecked")
  public static void loadFoodDefinitions() throws IOException {
    List<FoodDefinition> list =
        (List<FoodDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/FoodDefinitions.xml"));
    for (FoodDefinition definition : list) {
      foodDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " food definitions.");
  }

  @SuppressWarnings("unchecked")
  public static void loadItemBonusDefinitions() throws IOException {
    List<ItemBonusDefinition> list =
        (List<ItemBonusDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/ItemBonusDefinitions.xml"));
    for (ItemBonusDefinition definition : list) {
      itemBonusDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " item bonus definitions.");
  }

  @SuppressWarnings("unchecked")
  public static void loadItemDefinitions() throws IOException {
    List<ItemDefinition> list =
        (List<ItemDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/ItemDefinitions.xml"));
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

  @SuppressWarnings("unchecked")
  public static void loadNpcCombatDefinitions() throws IOException {
    List<NpcCombatDefinition> list =
        (List<NpcCombatDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/npcs/NpcCombatDefinitions.xml"));
    for (NpcCombatDefinition definition : list) {
      npcCombatDefinitions.put(definition.getId(), definition);
    }

    logger.info("Loaded " + Utility.format(list.size()) + " npc combat definitions.");
  }

  @SuppressWarnings("unchecked")
  public static void loadNpcDefinitions() throws IOException {
    List<NpcDefinition> list =
        (List<NpcDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/npcs/NpcDefinitions.xml"));
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

  @SuppressWarnings("unchecked")
  public static void loadNpcDropDefinitions() throws IOException {
    List<ItemDropDefinition> list =
        (List<ItemDropDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/npcs/ItemDropDefinitions.xml"));
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

      // System.out.println("  {");
      // System.out.println("    \"id\": " + i.getId() + ",");

      if (always) {
        // System.out.println("	\"always\": [");

        for (int index = 0; index < i.getConstant().getDrops().length; index++) {
          ItemDrop drop = i.getConstant().getDrops()[index];
          // System.out.println("      {");
          // System.out.println("        \"itemId\": " + drop.getId() + ",");
          // System.out.println("        \"minAmount\": " + drop.getMin() + ",");
          // System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
          // System.out.println("        \"chance\": ALWAYS");

          if (index + 1 == i.getConstant().getDrops().length) {
            // System.out.println("      }");
          } else {
            // System.out.println("      },");
          }
        }

        // System.out.println("    ],");
      }

      // System.out.println("	\"drops\": [");

      if (common) {
        for (int index = 0; index < i.getCommon().getDrops().length; index++) {
          ItemDrop drop = i.getCommon().getDrops()[index];
          // System.out.println("      {");
          // System.out.println("        \"itemId\": " + drop.getId() + ",");
          // System.out.println("        \"minAmount\": " + drop.getMin() + ",");
          // System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
          // System.out.println("        \"chance\": COMMON");

          if (index + 1 == i.getCommon().getDrops().length && !uncommon && !rare) {
            // System.out.println("      }");
          } else {
            // System.out.println("      },");
          }
        }
      }

      if (uncommon) {
        for (int index = 0; index < i.getUncommon().getDrops().length; index++) {
          ItemDrop drop = i.getUncommon().getDrops()[index];
          // System.out.println("      {");
          // System.out.println("        \"itemId\": " + drop.getId() + ",");
          // System.out.println("        \"minAmount\": " + drop.getMin() + ",");
          // System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
          // System.out.println("        \"chance\": UNCOMMON");

          if (index + 1 == i.getUncommon().getDrops().length && !rare) {
            // System.out.println("      }");
          } else {
            // System.out.println("      },");
          }
        }
      }

      if (rare) {
        for (int index = 0; index < i.getRare().getDrops().length; index++) {
          ItemDrop drop = i.getRare().getDrops()[index];
          // System.out.println("      {");
          // System.out.println("        \"itemId\": " + drop.getId() + ",");
          // System.out.println("        \"minAmount\": " + drop.getMin() + ",");
          // System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
          // System.out.println("        \"chance\": RARE");

          if (index + 1 == i.getRare().getDrops().length) {
            // System.out.println("      }");
          } else {
            // System.out.println("      },");
          }
        }
      }

      // System.out.println("	]");
      // System.out.println("  },");
    }

    logger.info("Loaded " + Utility.format(list.size()) + " npc drops.");
  }
	@SuppressWarnings("unchecked")
  public static void loadNpcSpawns() throws IOException {
    List<NpcSpawnDefinition> list =
        (List<NpcSpawnDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/npcs/NpcSpawnDefinitions.xml"));
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
	@SuppressWarnings("unchecked")
  public static void loadPotionDefinitions() throws IOException {
    List<PotionDefinition> list =
        (List<PotionDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/PotionDefinitions.xml"));
    for (PotionDefinition definition : list) {
      if (definition.getName() == null) {
        definition.setName(itemDefinitions.get(definition.getId()).getName());
      }
      potionDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " potion definitions.");
  }
	@SuppressWarnings("unchecked")
  public static void loadSagittariusVigourDefinitions() throws IOException {
    List<SagittariusVigourDefinition> list =
        (List<SagittariusVigourDefinition>)
            xStream.fromXML(
                new FileInputStream("./data/def/items/SagittariusVigourDefinitions.xml"));
    for (SagittariusVigourDefinition definition : list) {
      sagittariusVigourDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " sagittarius vigour bonus definitions.");
  }
	@SuppressWarnings("unchecked")
  public static void loadSagittariusWeaponDefinitions() throws IOException {
    List<SagittariusWeaponDefinition> list =
        (List<SagittariusWeaponDefinition>)
            xStream.fromXML(
                new FileInputStream("./data/def/items/SagittariusWeaponDefinitions.xml"));
    for (SagittariusWeaponDefinition definition : list) {
      sagittariusWeaponDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " sagittarius weapon definitions.");
  }

	public static final void loadRareDropChances() {
		try (BufferedReader reader = new BufferedReader(new FileReader("./data/def/npcs/DropChances.txt"))) {
			String line;

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("//")) {
					continue;
				}

				try {
					if (!line.contains(":") || !line.contains("/")) {
						System.err.println("[RareDropChances] Skipping malformed line: " + line);
						continue;
					}

					String[] parts = line.split(":");
					int id = Integer.parseInt(parts[0].trim());

					String dropPart = parts[1].split("/")[0].trim();
					int chance = Integer.parseInt(dropPart);

					rareDropChances.put(id, chance);

				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					System.err.println("[RareDropChances] Failed to parse line: " + line);
				}
			}
		} catch (Exception e) {
			System.err.println("[RareDropChances] Fatal error loading file:");
			e.printStackTrace();
		}

		logger.info("Successfully loaded all rare drops.");
	}


  @SuppressWarnings("unchecked")
  public static void loadShopDefinitions() throws IOException {
    List<ShopDefinition> list =
        (List<ShopDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/ShopDefinitions.xml"));
    for (ShopDefinition def : list) {
      Shop.getShops()[def.getId()] =
          new Shop(def.getId(), def.getItems(), def.isGeneral(), def.getName());
    }
    logger.info("Loaded " + Utility.format(list.size()) + " shops.");
  }

  @SuppressWarnings("unchecked")
  public static void loadSpecialAssaultDefinitions() throws IOException {
    List<SpecialAssaultDefinition> list =
        (List<SpecialAssaultDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/SpecialAssaultDefinitions.xml"));
    for (SpecialAssaultDefinition definition : list) {
      specialAssaultDefinitions.put(definition.getId(), definition);
    }
    logger.info("Loaded " + Utility.format(list.size()) + " special assault definitions.");
  }

  @SuppressWarnings("unchecked")
  public static void loadWeaponDefinitions() throws IOException {
    List<WeaponDefinition> list =
        (List<WeaponDefinition>)
            xStream.fromXML(new FileInputStream("./data/def/items/WeaponDefinitions.xml"));
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
