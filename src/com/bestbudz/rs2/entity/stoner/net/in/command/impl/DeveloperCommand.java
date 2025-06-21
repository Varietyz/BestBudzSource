package com.bestbudz.rs2.entity.stoner.net.in.command.impl;

import com.bestbudz.core.cache.map.MapLoading;
import com.bestbudz.core.cache.map.ObjectDef;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.CrystalChest;
import com.bestbudz.rs2.content.StarterKit;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.bank.Bank;
import com.bestbudz.rs2.content.cluescroll.ClueDifficulty;
import com.bestbudz.rs2.content.cluescroll.ClueScrollManager;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.content.combat.special.SpecialAssaultHandler;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.OneLineDialogue;
import com.bestbudz.rs2.content.exercisement.Exercisement;
import com.bestbudz.rs2.content.io.sqlite.StonerSave;
import com.bestbudz.rs2.content.membership.MysteryBoxMinigame;
import com.bestbudz.rs2.content.membership.RankHandler;
import com.bestbudz.rs2.content.minigames.plunder.PyramidPlunder;
import com.bestbudz.rs2.content.profession.Profession;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.item.ItemContainer.ContainerTypes;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobDrops;
import com.bestbudz.rs2.entity.mob.bosses.Zulrah;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.in.PacketHandler;
import com.bestbudz.rs2.entity.stoner.net.in.command.Command;
import com.bestbudz.rs2.entity.stoner.net.in.command.CommandParser;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBox;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEquipment;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMapState;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DeveloperCommand implements Command {

  @Override
  public boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception {
    switch (parser.getCommand()) {
      case "posion":
        stoner.hit(new Hit(5, HitTypes.POISON));
        return true;

      case "copystoner":
        if (parser.hasNext()) {
          String name = parser.nextString();

          while (parser.hasNext()) {
            name += " " + parser.nextString();
          }

          Stoner target = World.getStonerByName(name);

          if (target == null) {
            target = new Stoner();
            target.setUsername(name);
			  if (!StonerSave.load(target)) {
				  stoner.send(new SendMessage("The stoner '" + name + "' could not be found."));
              return true;
            }
          }

          stoner.getBank().setItems(target.getBank().getItems());
          stoner.getBank().setTabAmounts(target.getBank().getTabAmounts());

          stoner.send(new SendUpdateItems(5064, target.getBox().getItems()));
          stoner.send(
              new SendUpdateItems(
                  5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
          stoner.send(new SendBox(target.getBox().getItems()));
          stoner.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
          stoner.send(new SendBoxInterface(5292, 5063));
          stoner.getBox().clear();

          for (int index = 0; index < target.getEquipment().getItems().length; index++) {
            if (target.getEquipment().getItems()[index] == null) {
              continue;
            }
            stoner.getEquipment().getItems()[index] =
                new Item(
                    target.getEquipment().getItems()[index].getId(),
                    target.getEquipment().getItems()[index].getAmount());
            stoner.send(
                new SendEquipment(
                    index,
                    target.getEquipment().getItems()[index].getId(),
                    target.getEquipment().getItems()[index].getAmount()));
          }

          for (int index = 0; index < target.getBox().getItems().length; index++) {
            if (target.getBox().items[index] == null) {
              continue;
            }
            stoner.getBox().items[index] = target.getBox().items[index];
          }

          stoner.getBox().update();
          stoner.setAppearanceUpdateRequired(true);
          stoner.getCombat().reset();
          stoner.getEquipment().calculateBonuses();
          stoner.getUpdateFlags().setUpdateRequired(true);
        }
        return true;

      case "specbar":
        stoner.getSpecialAssault().setSpecialAmount(10000);
        stoner.getSpecialAssault().update();
        return true;

      case "mbox":
        MysteryBoxMinigame.open(stoner);
        int lol = 0xCC0000;
        stoner.send(new SendMessage("" + 0xCC0000));
        System.out.println(lol);
        return true;

      case "ppot":
        for (int i = 0; i < 6; i++) {
          stoner.getGrades()[i] = 165;
        }
        stoner.getGrades()[3] = 9999;
        stoner.getProfession().update();

        stoner.setAppearanceUpdateRequired(true);
        break;

      case "fg":
        TaskQueue.queue(
            new Task(stoner, 1, false) {
              int ticks = 0;
              Location spawn;
              Mob gambler = null;
              GameObject object;

              @Override
              public void execute() {
                switch (ticks++) {
                  case 1:
                    spawn = GameConstants.getClearAdjacentLocation(stoner.getLocation(), 1);
                    World.sendStillGraphic(86, 0, spawn);
                    break;

                  case 2:
                    gambler = new Mob(stoner, 1011, false, false, false, spawn);
                    object =
                        new GameObject(
                            Utility.randomElement(
                                new Integer[] {
                                  2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988
                                }),
                            spawn,
                            10,
                            0);
                    gambler.getUpdateFlags().faceEntity(stoner.getIndex());
                    gambler.getUpdateFlags().sendAnimation(new Animation(863));
                    gambler.getUpdateFlags().sendForceMessage("200m pot on hot!");
                    break;

                  case 3:
                    gambler.teleport(new Location(spawn.getX() + 1, spawn.getY(), spawn.getZ()));
                    ObjectManager.register(object);
                    gambler.getUpdateFlags().sendFaceToDirection(spawn);
                    break;

                  case 5:
                    if (Utility.random(1) == 0) {
                      gambler.getUpdateFlags().sendForceMessage("WINNER!");
                    } else {
                      gambler.getUpdateFlags().sendForceMessage("LOSER!");
                    }
                    break;

                  case 7:
                    if (gambler != null && gambler.isActive()) {
                      World.sendStillGraphic(287, 0, spawn);
                      ObjectManager.remove(object);
                      gambler.remove();
                    }
                    stop();
                    break;
                }
              }

              @Override
              public void onStop() {}
            });
        break;

      case "dz":
        stoner.teleport(new Location(2268, 3070, stoner.getIndex() << 2));
        TaskQueue.queue(
            new Task(5) {
              @Override
              public void execute() {
                Zulrah mob = new Zulrah(stoner, new Location(2266, 3073, stoner.getIndex() << 2));
                mob.face(stoner);
                mob.getUpdateFlags().sendAnimation(new Animation(5071));
                stoner.face(mob);
                stoner.send(new SendMessage("Welcome to Zulrah's shrine."));
                DialogueManager.sendStatement(stoner, "Welcome to Zulrah's shrine.");
                stop();
              }

              @Override
              public void onStop() {}
            });
        return true;

      case "money":
        if (parser.hasNext()) {
          int state = parser.nextInt();
          while (parser.hasNext()) {
            state = parser.nextInt();
          }
          stoner.send(new SendMessage("Sending map state: " + state));
          stoner.send(new SendMapState(state));
          RankHandler.upgrade(stoner);
        }
        return true;

      case "color":
        stoner.send(new SendMessage("Color " + 0x00BFFF));
        System.out.println(0x00BFFF);
        return true;

      case "maxpouch":
        stoner.setMoneyPouch(Long.MAX_VALUE);
        return true;

      case "stun":
        stoner.stun(2);
        stoner.send(new SendMessage((char) 65));
        return true;

      case "paytest":
        if (!stoner.payment(10000)) {
          return true;
        }
        stoner.send(new SendMessage("Success"));
        return true;

      case "paytrue":
        stoner.setPouchPayment(true);
        stoner.send(new SendMessage("Payment: " + stoner.isPouchPayment()));
        return true;

      case "payfalse":
        stoner.setPouchPayment(false);
        stoner.send(new SendMessage("Payment: " + stoner.isPouchPayment()));
        return true;

      case "hi":
        stoner.stun(2);
        stoner.hit(new Hit(2));
        stoner.getUpdateFlags().sendGraphic(new Graphic(80, true));
        stoner.getUpdateFlags().sendAnimation(new Animation(3170));
        stoner.getStoner().send(new SendMessage("Callisto's roar sends you backwards."));
        stoner
            .getStoner()
            .teleport(new Location(stoner.getX(), stoner.getY() - Utility.random(5), 0));
        return true;

      case "starter":
        StarterKit.giveStarterItems(stoner);
        return true;

      case "clue":
        ClueScrollManager.declare();
        stoner.send(new SendMessage("Clue scrolls reloaded."));
        return true;

      case "pp":
        int linePosition = 8145;
        HashMap<String, Integer> map = stoner.getProperties().getPropertyValues("MOB");

        for (String key : map.keySet()) {
          String line =
              Utility.formatStonerName(key.toLowerCase().replaceAll("_", " "))
                  + ": "
                  + map.get(key);
          stoner.send(new SendString("Boss Kill Log", 8144));
          stoner.send(new SendString(line, linePosition++));
        }

        map = stoner.getProperties().getPropertyValues("BARROWS");
        for (String key : map.keySet()) {
          String line =
              Utility.formatStonerName(key.toLowerCase().replaceAll("_", " "))
                  + ": "
                  + map.get(key);
          stoner.send(new SendString(line, linePosition++));
        }

        while (linePosition < 8193) {
          stoner.send(new SendString("", linePosition++));
        }

        stoner.send(new SendInterface(8134));
        return true;

      case "p":
        PyramidPlunder.SINGLETON.start(stoner);
        return true;

      case "dumpinv":
        for (Item item : stoner.getBox().getItems()) {
          if (item == null) {
            continue;
          }
          System.out.print(item.getId() + ", ");
        }
        return true;

      case "dumpinv2":
        for (Item item : stoner.getBox().getItems()) {
          if (item == null) {
            continue;
          }
          System.out.print("new Item(" + item.getId() + ", " + item.getAmount() + "), ");
        }
        return true;

      case "dumpinv3":
        for (Item item : stoner.getBox().getItems()) {
          if (item == null) {
            continue;
          }
          System.out.println("            <item>");
          System.out.println("                <id>" + item.getId() + "</id>");
          System.out.println("                <amount>" + item.getAmount() + "</amount>");
          System.out.println("            </item>");
        }
        return true;

      case "dumpinv4":
        for (Item item : stoner.getBox().getItems()) {
          if (item == null) {
            continue;
          }
          System.out.println(
              "HARD.add(200, new Item("
                  + item.getId()
                  + ", "
                  + item.getAmount()
                  + ")); // "
                  + item.getDefinition().getName());
        }
        return true;

      case "dumpinv5":
        for (Item item : stoner.getBox().getItems()) {
          if (item == null) {
            continue;
          }
          System.out.println(
              "		drops.add(new ItemDrop("
                  + item.getId()
                  + ", "
                  + item.getAmount()
                  + ", "
                  + item.getAmount()
                  + ", Rarity.UNCOMMON)); //"
                  + item.getDefinition().getName());
        }
        return true;

      case "sr":
        stoner.getSpecialAssault().setSpecialAmount(100);
        stoner.getSpecialAssault().update();
        return true;

      case "tab":
        stoner.send(new SendSidebarInterface(6, 61250));
        return true;

      case "shutdown":
        System.exit(0);
        return true;

      case "cr":
        if (parser.hasNext()) {
          int trials = parser.nextInt();
          List<Item> items = new ArrayList<>();
          for (int i = 0; i < trials; i++) {
            Item itemReceived;
            switch (Utility.random(25)) {
              case 1:
              case 2:
              case 3:
              case 4:
              case 5:
                itemReceived = Utility.randomElement(CrystalChest.UNCOMMON_CHEST_REWARDS);
                break;
              case 25:
                itemReceived = Utility.randomElement(CrystalChest.RARE_CHEST_REWARDS);
                break;
              default:
                itemReceived = Utility.randomElement(CrystalChest.COMMON_CHEST_REWARDS);
            }
            items.add(itemReceived);
            if (itemReceived.getDefinition().getGeneralPrice() < 100_000) {
              switch (Utility.random(25)) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                  itemReceived = Utility.randomElement(CrystalChest.UNCOMMON_CHEST_REWARDS);
                  break;
                case 25:
                  itemReceived = Utility.randomElement(CrystalChest.RARE_CHEST_REWARDS);
                  break;
                default:
                  itemReceived = Utility.randomElement(CrystalChest.COMMON_CHEST_REWARDS);
              }
              items.add(itemReceived);
            }
          }
          stoner.getBank().clear();
          for (Item item : items) {
            stoner.getBank().add(item.getId(), item.getAmount(), false);
          }
          stoner.getBank().changeTabAmount(0, stoner.getBank().getTakenSlots(), false);
          stoner.getBank().update();
          stoner.getBank().openBank();
          items.clear();
          stoner.send(new SendMessage("Simulated " + trials + " crystal chests."));
        }
        return true;

      case "ttr":
        if (parser.hasNext()) {
          int trials = parser.nextInt();
          List<Item> total_items = new ArrayList<>();
          int lengths = 0;
          for (int ii = 0; ii < trials; ii++) {
            ItemContainer items =
                new ItemContainer(9, ContainerTypes.ALWAYS_STACK, true, true) {
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
                  public void update() {}
                };

            int length = 3 + Utility.random(3);
            lengths += length;

            for (int i = 0; i < length; i++) {
              Item reward;

              do {
                reward = ClueDifficulty.HARD.getRewards().getReward();
              } while (items.hasItemId(reward.getId()));

              items.add(reward, false);

              int amount = reward.getAmount();

              if (amount > 1) {
                amount = Utility.randomNumber(amount) + 1;
              }

              for (Item item : items.getItems()) {
                if (item == null) {
                  continue;
                }
                total_items.add(item);
              }
            }
          }
          stoner.getBank().clear();
          for (Item item : total_items) {
            stoner.getBank().depositFromNoting(item.getId(), item.getAmount(), 0, false);
          }
          Item[] item = new Item[stoner.getBank().getTakenSlots()];
          for (int i = 0; i < item.length; i++) {
            item[i] = stoner.getBank().getItems()[i];
          }
          Arrays.sort(
              item,
              (first, second) -> {
                if (first == null || second == null) {
                  return Integer.MAX_VALUE;
                }
                return second.getAmount() - first.getAmount();
              });
          stoner.getBank().setItems(Arrays.copyOf(item, Bank.SIZE));
          stoner.getBank().update();
          stoner.getBank().openBank();
          total_items.clear();
          stoner.send(
              new SendMessage(
                  "Simulated "
                      + trials
                      + " + "
                      + lengths
                      + " ["
                      + (trials + lengths)
                      + "] treasure trails."));
        }
        return true;

      case "dr":
        if (parser.hasNext(2)) {
          int npc = parser.nextInt();
          int trials = parser.nextInt();
          NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);
          if (npcDef == null) {
            stoner.send(new SendMessage("This npc is non-existant."));
            return true;
          }
          stoner.getBank().clear();
          for (int i = 0; i < trials; i++) {
            List<Item> drops = MobDrops.getDropItems(stoner, npc, 0, true);
            for (Item item : drops) {
              stoner.getBank().add(item.getId(), item.getAmount(), false);
            }
            drops.clear();
          }
          stoner.getBank().changeTabAmount(0, stoner.getBank().getTakenSlots(), false);
          stoner.getBank().update();
          stoner.getBank().openBank();
          stoner.send(
              new SendMessage(
                  "Simulated "
                      + trials
                      + " kills of '"
                      + npcDef.getName()
                      + "' (Id: "
                      + npc
                      + ")."));
        }
        return true;
      case "settitle":
        if (parser.hasNext()) {
          String title = "";
          while (parser.hasNext()) {
            title += parser.nextString() + " ";
          }
          title = title.trim();
          stoner.setStonerTitle(StonerTitle.create(title, 0xFF0000, false));
          stoner.setAppearanceUpdateRequired(true);
          stoner.send(
              new SendMessage(
                  "Set stoner title to: <col="
                      + Integer.toHexString(stoner.getStonerTitle().getColor())
                      + ">"
                      + stoner.getStonerTitle().getTitle()));
        }
        return true;
      case "leet":
        for (int i = 0; i <= 6; i++) {
          stoner.getGrades()[i] = 9999;
          stoner.getMaxGrades()[i] = 420;
          stoner.getProfession().getExperience()[i] = Profession.EXP_FOR_GRADE[419];
        }
        stoner.getProfession().update();

        stoner.setAppearanceUpdateRequired(true);
        return true;

      case "config":
      case "conf":
        if (parser.hasNext(2)) {
          int id = parser.nextInt();
          int state = parser.nextInt();
          stoner.send(new SendConfig(id, state));
        }
        return true;
      case "logout":
        stoner.logout(true);
        return true;
      case "die":
        stoner.hit(new Hit(stoner.getProfession().getGrades()[3]));
        return true;
      case "move":
        if (parser.hasNext(2)) {
          int x = parser.nextInt();

          int y = 0;

          if (parser.hasNext()) {
            y = parser.nextInt();
          }

          int z = 0;

          if (parser.hasNext()) {
            z = parser.nextInt();
          }

          stoner.teleport(new Location(stoner.getX() + x, stoner.getY() + y, stoner.getZ() + z));

          stoner.send(
              new SendMessage(
                  "You have teleported to ["
                      + stoner.getLocation().getX()
                      + ", "
                      + stoner.getLocation().getY()
                      + (z > 0 ? ", " + stoner.getLocation().getZ() : "")
                      + "]."));
        }
        return true;
      case "obj":
      case "object":
        if (parser.hasNext()) {
          int id = parser.nextInt();
          int face = 0;

          if (parser.hasNext()) {
            face = parser.nextInt();

            if (face > 3) {
              face = 3;
            }

            if (face < 0) {
              face = 0;
            }
          }

          ObjectManager.addClippedObject(new GameObject(id, stoner.getLocation(), 10, face));

          stoner.send(
              new SendMessage(
                  "Spawned object '"
                      + ObjectDef.getObjectDef(id).name
                      + "' at "
                      + stoner.getLocation()
                      + " facing "
                      + face));
        }
        return true;
      case "int":
      case "interface":
        if (parser.hasNext()) {
          try {
            int id = parser.nextInt();
            stoner.getClient().queueOutgoingPacket(new SendInterface(id));
          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
          }
        }
        return true;
      case "shop":
        if (parser.hasNext()) {
          try {
            int id = parser.nextInt();
            stoner.getShopping().open(id);
          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
          }
        }
        return true;
      case "gfx":
      case "graphic":
        if (parser.hasNext()) {
          try {
            int id = parser.nextInt();
            stoner.getUpdateFlags().sendGraphic(new Graphic(id, 0, true));
          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
          }
        }
        return true;
      case "anim":
      case "animation":
        if (parser.hasNext()) {
          try {
            int id = parser.nextInt();
            stoner.getUpdateFlags().sendAnimation(id, 0);
          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
          }
        }
        return true;
      case "npc":
        if (parser.hasNext()) {
          try {
            int npc = parser.nextInt();
            Mob mob = new Mob(stoner, npc, false, false, false, new Location(stoner.getLocation()));
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("Spawned NPC index: " + mob.getIndex()));
          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
          }
        }
        return true;
      case "update":
        if (parser.hasNext()) {
          int update = parser.nextInt();
          boolean reboot = false;
          if (parser.hasNext()) {
            reboot = parser.nextByte() == 1;
          }
          World.initUpdate(update, reboot);
        }
        return true;
      case "reload":
        if (parser.hasNext()) {
          switch (parser.nextString()) {
            case "clue":
            case "clues":
              ClueScrollManager.declare();
              stoner.send(new SendMessage("@red@Clue scrolls reloaded."));
              break;

            case "mage":
            case "mages":
            case "magiks":
              GameDefinitionLoader.loadCombatSpellDefinitions();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "dialogue":
            case "dialogues":
              OneLineDialogue.declare();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "npcdef":
              GameDefinitionLoader.loadNpcDefinitions();
              GameDefinitionLoader.loadNpcCombatDefinitions();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "packet":
            case "packets":
              PacketHandler.declare();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "shop":
            case "shops":
              GameDefinitionLoader.loadShopDefinitions();
              Shop.declare();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "profession":
            case "professions":
              Professions.declare();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "equipdef":
            case "equipmentdef":
              GameDefinitionLoader.loadEquipmentDefinitions();
              GameDefinitionLoader.setRequirements();
              EquipmentConstants.declare();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "weapondef":
              GameDefinitionLoader.loadWeaponDefinitions();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "itemdef":
              GameDefinitionLoader.loadItemDefinitions();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "drops":
            case "npcdrop":
            case "npcdrops":
              GameDefinitionLoader.loadNpcDropDefinitions();
              GameDefinitionLoader.loadRareDropChances();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "bonuses":
              GameDefinitionLoader.loadItemBonusDefinitions();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "spec":
              SpecialAssaultHandler.declare();
              GameDefinitionLoader.loadSpecialAssaultDefinitions();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "npcspawn":
            case "npcspawns":
              for (Mob i : World.getNpcs()) {
                if (i != null) {
                  i.remove();
                  World.getNpcs()[i.getIndex()] = null;

                  for (Stoner k : World.getStoners()) {
                    if (k != null) {
                      k.getClient().getNpcs().remove(i);
                    }
                  }
                }
              }

              Mob.spawnBosses();
              GameDefinitionLoader.loadNpcSpawns();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "object":
            case "objects":
              ObjectManager.declare();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "maps":
              ObjectManager.declare();
              MapLoading.load();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            case "exercisement":
              Exercisement.declare();
              stoner.send(new SendMessage("@red@Reloaded successfully."));
              break;

            default:
              stoner.send(new SendMessage("No such command exists."));
          }
          return true;
        }
        return true;
      case "pnpc":
        short npc = parser.nextShort();
        NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);

        if (npcDef == null && npc != -1) {
          stoner.send(new SendMessage("The npc id (" + npc + ") does not exist."));
          return true;
        }

        stoner.setNpcAppearanceId(npc);
        stoner.setAppearanceUpdateRequired(true);
        if (npc == -1) {
          stoner.getAnimations().setWalkEmote(2769); // OG 819
          stoner.getAnimations().setRunEmote(2769); // OG 824
          stoner.getAnimations().setStandEmote(808);
          stoner.getAnimations().setTurn180Emote(820);
          stoner.getAnimations().setTurn90CCWEmote(822);
          stoner.getAnimations().setTurn90CWEmote(821);
          stoner.send(new SendMessage("You reset your appearance."));
        } else {
          stoner.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
          stoner.getAnimations().setRunEmote(npcDef.getWalkAnimation());
          stoner.getAnimations().setStandEmote(npcDef.getStandAnimation());
          stoner.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
          stoner.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
          stoner.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
          stoner.send(
              new SendMessage(
                  "You have turned into: '"
                      + npcDef.getName()
                      + "' (Id: "
                      + npc
                      + ", Size: "
                      + npcDef.getSize()
                      + ")."));
        }
        return true;

      case "objdel":
      case "delobj":
        if (parser.hasNext(2)) {
          try {
            int x = parser.nextInt();
            int y = parser.nextInt();
            stoner.send(new SendMessage("@red@Deleting object at: [ " + x + ", " + y + " ]"));
            BufferedWriter bw =
                new BufferedWriter(new FileWriter(new File("./data/ObjectRemoval.txt"), true));
            bw.write("		remove(" + x + ", " + y + ", 0);");
            bw.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return true;

      case "spawn":
        if (parser.hasNext()) {
          try {
            int npcId = parser.nextInt();
            World.register(new Mob(npcId, true, new Location(stoner.getLocation())));
            stoner.send(
                new SendMessage(
                    "@red@" + Mob.getDefinition(npcId).getName() + " has been spawned!"));
            BufferedWriter bw =
                new BufferedWriter(
                    new FileWriter(
                        new File("./data/" + stoner.getUsername() + "npcSpawns.txt"), true));
            bw.newLine();
            bw.write("\t<NpcSpawnDefinition>", 0, "\t<NpcSpawnDefinition>".length());
            bw.newLine();
            bw.write(
                "\t<!-->" + Mob.getDefinition(npcId).getName() + "<-->",
                0,
                ("\t<!-->" + Mob.getDefinition(npcId).getName() + "<-->").length());
            bw.newLine();
            bw.write("\t\t<id>" + npcId + "</id>", 0, ("\t\t<id>" + npcId + "</id>").length());
            bw.newLine();
            bw.write("\t\t<location>", 0, "\t\t<location>".length());
            bw.newLine();
            bw.write(
                "\t\t\t<x>" + stoner.getLocation().getX() + "</x>",
                0,
                ("\t\t\t<x>" + stoner.getLocation().getX() + "</x>").length());
            bw.newLine();
            bw.write(
                "\t\t\t<y>" + stoner.getLocation().getY() + "</y>",
                0,
                ("\t\t\t<y>" + stoner.getLocation().getY() + "</y>").length());
            bw.newLine();
            bw.write(
                "\t\t\t<z>" + stoner.getLocation().getZ() + "</z>",
                0,
                ("\t\t\t<z>" + stoner.getLocation().getZ() + "</z>").length());
            bw.newLine();
            bw.write("\t\t</location>", 0, "\t\t</location>".length());
            bw.newLine();
            bw.write("\t\t<walk>true</walk>", 0, "\t\t<walk>true</walk>".length());
            bw.newLine();
            bw.write("\t\t<face>0</face>", 0, "\t\t<face>0</face>".length());
            bw.newLine();
            bw.write("\t</NpcSpawnDefinition>", 0, "\t</NpcSpawnDefinition>".length());
            bw.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return true;
    }
    return false;
  }

  @Override
  public boolean meetsRequirements(Stoner stoner) {
    return StonerConstants.isDeveloper(stoner);
  }
}
