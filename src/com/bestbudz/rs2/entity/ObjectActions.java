package com.bestbudz.rs2.entity;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.cache.map.Door;
import com.bestbudz.core.cache.map.Doors;
import com.bestbudz.core.cache.map.DoubleDoor;
import com.bestbudz.core.cache.map.ObjectDef;
import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.FollowToEntityTask;
import com.bestbudz.core.task.impl.HarvestTask;
import com.bestbudz.core.task.impl.HopDitchTask;
import com.bestbudz.core.task.impl.ObeliskTick;
import com.bestbudz.core.task.impl.PullLeverTask;
import com.bestbudz.core.task.impl.ShearingTask;
import com.bestbudz.core.task.impl.TeleOtherTask;
import com.bestbudz.core.task.impl.WalkThroughDoorTask;
import com.bestbudz.core.task.impl.WalkThroughDoubleDoorTask;
import com.bestbudz.core.task.impl.WalkToTask;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.Advance;
import com.bestbudz.rs2.content.CrystalChest;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.FountainOfRune;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.OneLineDialogue;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.content.dialogue.impl.AchievementDialogue;
import com.bestbudz.rs2.content.dialogue.impl.AdvanceDialogue;
import com.bestbudz.rs2.content.dialogue.impl.BestBudzDialogue;
import com.bestbudz.rs2.content.dialogue.impl.ConsumerTeleport;
import com.bestbudz.rs2.content.dialogue.impl.DecantingDialogue;
import com.bestbudz.rs2.content.dialogue.impl.DunceDialogue;
import com.bestbudz.rs2.content.dialogue.impl.EmblemDialogue;
import com.bestbudz.rs2.content.dialogue.impl.GenieResetDialogue;
import com.bestbudz.rs2.content.dialogue.impl.HariDialogue;
import com.bestbudz.rs2.content.dialogue.impl.KamfreeDialogue;
import com.bestbudz.rs2.content.dialogue.impl.KolodionDialogue;
import com.bestbudz.rs2.content.dialogue.impl.MakeoverMage;
import com.bestbudz.rs2.content.dialogue.impl.MembershipDialogue;
import com.bestbudz.rs2.content.dialogue.impl.NeiveDialogue;
import com.bestbudz.rs2.content.dialogue.impl.OttoGodblessed;
import com.bestbudz.rs2.content.dialogue.impl.OziachDialogue;
import com.bestbudz.rs2.content.dialogue.impl.PilesDialogue;
import com.bestbudz.rs2.content.dialogue.impl.SailorDialogue;
import com.bestbudz.rs2.content.dialogue.impl.StaffTitleDialogue;
import com.bestbudz.rs2.content.dialogue.impl.TzhaarMejKahDialogue;
import com.bestbudz.rs2.content.dialogue.impl.VannakaDialogue;
import com.bestbudz.rs2.content.dialogue.impl.WeaponGameDialogue;
import com.bestbudz.rs2.content.dialogue.impl.teleport.SpiritTree;
import com.bestbudz.rs2.content.dialogue.impl.teleport.WildernessLever;
import com.bestbudz.rs2.content.dwarfcannon.DwarfMultiCannon;
import com.bestbudz.rs2.content.exercisement.Exercisement;
import com.bestbudz.rs2.content.exercisement.obstacle.interaction.WalkInteraction;
import com.bestbudz.rs2.content.membership.MysteryBoxMinigame;
import com.bestbudz.rs2.content.minigames.barrows.Barrows;
import com.bestbudz.rs2.content.minigames.clanwars.ClanWarsFFA;
import com.bestbudz.rs2.content.minigames.fightcave.TzharrGame;
import com.bestbudz.rs2.content.minigames.fightpits.FightPits;
import com.bestbudz.rs2.content.minigames.godwars.GodWars;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControl;
import com.bestbudz.rs2.content.minigames.plunder.PyramidPlunder;
import com.bestbudz.rs2.content.minigames.warriorsguild.ArmourAnimator;
import com.bestbudz.rs2.content.minigames.warriorsguild.CyclopsRoom;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGame;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGameStore;
import com.bestbudz.rs2.content.pets.BossPets;
import com.bestbudz.rs2.content.pets.BossPets.PetData;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.accomplisher.HomeStalls;
import com.bestbudz.rs2.content.profession.accomplisher.WallSafes;
import com.bestbudz.rs2.content.profession.foodie.Foodie;
import com.bestbudz.rs2.content.profession.foodie.FoodieData;
import com.bestbudz.rs2.content.profession.forging.Forging;
import com.bestbudz.rs2.content.profession.handiness.Flax;
import com.bestbudz.rs2.content.profession.handiness.Handiness;
import com.bestbudz.rs2.content.profession.handiness.HideTanning;
import com.bestbudz.rs2.content.profession.handiness.JewelryCreationTask;
import com.bestbudz.rs2.content.profession.handiness.Spinnable;
import com.bestbudz.rs2.content.profession.hunter.Impling.ImplingRewards.Implings;
import com.bestbudz.rs2.content.profession.lumbering.LumberingTask;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.content.profession.mage.MageProfession.SpellBookTypes;
import com.bestbudz.rs2.content.profession.necromance.PetInteraction;
import com.bestbudz.rs2.content.profession.pyromaniac.PyroAutoBurn;
import com.bestbudz.rs2.content.profession.pyromaniac.Pyromaniac;
import com.bestbudz.rs2.content.profession.quarrying.Quarrying;
import com.bestbudz.rs2.content.profession.thchempistry.PotionDecanting;
import com.bestbudz.rs2.content.shopping.impl.BountyShop;
import com.bestbudz.rs2.content.shopping.impl.PestShop;
import com.bestbudz.rs2.content.wilderness.Lockpick;
import com.bestbudz.rs2.content.wilderness.TargetSystem;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.item.impl.GroundItemHandler;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectConstants;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObjectActions
{

	private static boolean hasSmithableBars(int itemId) {
		// Check if item is a bar that can be smithed
		int[] bars = {2349, 2351, 2353, 2359, 2361, 2363}; // Bronze, Iron, Steel, Mith, Addy, Rune
		for (int barId : bars) {
			if (itemId == barId) return true;
		}
		return false;
	}

	private boolean hasSmeltableOres(int itemId) {
		// Check if item is an ore that can be smelted
		int[] ores = {438, 436, 440, 453, 447, 449, 451}; // Tin, Copper, Iron, Coal, Gold, Mith, Addy
		for (int oreId : ores) {
			if (itemId == oreId) return true;
		}
		return false;
	}

  public static final int[][] NPC_STORE_DATA = {
    {519, 0},
    {518, 0},
    {225, 15},
    {527, 16},
    {505, 17},
    {528, 18},
    {536, 25},
    {5314, 26},
    {603, 27},
    {4306, 20},
    {5919, 3},
    {22, 29},
    {1758, 5},
    {3984, 31},
    {532, 45},
  };

  public static final String[] DEFAULT_DIALOGUES = {
    "You should listen to SoundSystems while playing BestBudz.",
    "So what you smoking on?!",
    "Did you know nixon lied to the whole world?!",
    "High, how doing you are?",
    "I'm in the mood to spark a bowl!",
    "Don't let the weedman rip your ass.",
    "Bong, check! Weed, Check! Lighter, ... FUCK!",
    "Get yo weed, fool.",
    "Did u steal my lighter?",
    "You got a lighter bro?"
  };

  public static void clickNpc(final Stoner stoner, final int option, int slot) {
    if (stoner.getMage().isTeleporting()) {
      return;
    }

    if (stoner.getTrade().trading()) {
      stoner.getTrade().end(false);
    }

    if (stoner.getDueling().isDueling()) {
      stoner.getDueling().decline();
    }

    if ((slot > World.getNpcs().length) || (slot < 0)) {
      return;
    }

    stoner.getMovementHandler().reset();

    final Mob mob = World.getNpcs()[slot];

    if (mob == null) {
      stoner.getMovementHandler().reset();
      return;
    }

    if (BestbudzConstants.DEV_MODE) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("option: " + option));
    }


    TaskQueue.queue(
        new FollowToEntityTask(stoner, mob) {
          @Override
          public void onDestination() {
            if (mob.face()) {
              mob.face(stoner);
            }

            stoner.face(mob);

            if (mob.getSize() > 1) {
              stoner.getMovementHandler().reset();
            }

            ObjectActions.finishClickNpc(stoner, option, mob);
          }
        });
  }

  public static void clickObject(
      final Stoner stoner, final int option, final int id, final int x, final int y) {
    if (stoner.getMage().isTeleporting()) {
      return;
    }

    if (stoner.getTrade().trading()) {
      stoner.getTrade().end(false);
    }

    if (stoner.getDueling().isDueling()) {
      stoner.getDueling().decline();
    }

    int z = stoner.getLocation().getZ();

    RSObject object = Region.getObject(x, y, z);

    if ((object == null) && (!StonerConstants.isOverrideObjectExistance(stoner, id, x, y, z))) {
      return;
    }

    final int[] length = ObjectConstants.getObjectLength(id, object == null ? 0 : object.getFace());

    if ((id == 1815) && x == 3098 && y == 3499 && stoner.getX() == x && stoner.getY() == y) {
      stoner.start(new WildernessLever(stoner));
      return;
    }
    if ((id == 1815)) {
      TaskQueue.queue(
          new PullLeverTask(stoner, x, y, length[0], length[1]) {
            @Override
            public void onDestination() {
              stoner
                  .getUpdateFlags()
                  .sendFaceToDirection(
                      length[0] >= 2 ? x + length[0] / 2 : x,
                      length[1] >= 2 ? y + length[1] / 2 : y);
              stoner
                  .getMage()
                  .teleportNoWildernessRequirement(
                      2561, 3311, 0, MageProfession.TeleportTypes.SPELL_BOOK);
            }
          });
      return;
    } else if ((id == 1814)) {
      TaskQueue.queue(
          new PullLeverTask(stoner, x, y, length[0], length[1]) {

            @Override
            public void onDestination() {
              stoner
                  .getUpdateFlags()
                  .sendFaceToDirection(
                      length[0] >= 2 ? x + length[0] / 2 : x,
                      length[1] >= 2 ? y + length[1] / 2 : y);
              stoner
                  .getMage()
                  .teleportNoWildernessRequirement(
                      3153, 3923, 0, MageProfession.TeleportTypes.SPELL_BOOK);
            }
          });
      return;
    } else if ((id == 5960) || (id == 5959)) {
      TaskQueue.queue(
          new PullLeverTask(stoner, x, y, length[0], length[1]) {
            @Override
            public void onDestination() {
              stoner
                  .getUpdateFlags()
                  .sendFaceToDirection(
                      length[0] >= 2 ? x + length[0] / 2 : x,
                      length[1] >= 2 ? y + length[1] / 2 : y);
              stoner
                  .getMage()
                  .teleportNoWildernessRequirement(
                      id == 5960 ? 3090 : 2539,
                      id == 5960 ? 3956 : 4712,
                      0,
                      MageProfession.TeleportTypes.SPELL_BOOK);
            }
          });
      return;
    } else if ((id == 9706)) {
      TaskQueue.queue(
          new PullLeverTask(stoner, x, y, length[0], length[1]) {
            @Override
            public void onDestination() {
              stoner
                  .getUpdateFlags()
                  .sendFaceToDirection(
                      length[0] >= 2 ? x + length[0] / 2 : x,
                      length[1] >= 2 ? y + length[1] / 2 : y);
              stoner
                  .getMage()
                  .teleportNoWildernessRequirement(
                      3105, 3951, 0, MageProfession.TeleportTypes.SPELL_BOOK);
            }
          });
      return;
    } else if ((id == 9707)) {
      TaskQueue.queue(
          new PullLeverTask(stoner, x, y, length[0], length[1]) {
            @Override
            public void onDestination() {
              stoner
                  .getUpdateFlags()
                  .sendFaceToDirection(
                      length[0] >= 2 ? x + length[0] / 2 : x,
                      length[1] >= 2 ? y + length[1] / 2 : y);
              stoner
                  .getMage()
                  .teleportNoWildernessRequirement(
                      3105, 3956, 0, MageProfession.TeleportTypes.SPELL_BOOK);
            }
          });
      return;
    }

    TaskQueue.queue(
        new WalkToTask(stoner, x, y, length[0], length[1]) {

          @Override
          public void onDestination() {
            stoner
                .getUpdateFlags()
                .sendFaceToDirection(
                    length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);

            ObjectActions.finishObjectClick(stoner, id, option, x, y);
          }
        });
  }

  @SuppressWarnings("unchecked")
  public static final void finishClickNpc(Stoner stoner, int option, Mob mob) {
    int id = mob.getId();

    if (stoner.getSummoning().interact(mob, option)) {
      return;
    }

    if (Implings.implings.containsKey(mob.getId())) {
      Implings.catchImp(stoner, mob);
    }
    switch (option) {
      case 1:
        if (stoner.getFisher().clickNpc(mob, id, 1)) {
          return;
        }

        for (int i = 0; i < NPC_STORE_DATA.length; i++) {
          if (NPC_STORE_DATA[i][0] == id) {
            stoner.getShopping().open(NPC_STORE_DATA[i][1]);
            return;
          }
        }

        if (BossPets.pickupPet(stoner, mob)) {
          return;
        }

        switch (id) {
          case 3936:
            stoner.start(new SailorDialogue(stoner));
            break;
          case 2914:
            stoner.start(new OttoGodblessed(stoner));
            break;
          case 2801:
            if (!stoner.getEquipment().isWearingItem(6575)) {
				stoner.send(new SendMessage("You must be wearing a tool ring to do this!"));
              return;
            }
            if (stoner.getBox().getFreeSlots() == 0) {
				stoner.send(new SendMessage("You do not have any free box spaces to do this!"));
              return;
            }
            if (stoner.getDelay().elapsed() < 5000) {
              return;
            }
            stoner.send(new SendMessage("You manage to get some wool."));
            stoner.getUpdateFlags().sendAnimation(new Animation(893));
            stoner.getBox().add(1737, 1);
            mob.transform(2697);
            stoner.getDelay().reset();
            TaskQueue.queue(new ShearingTask(mob, 15));
            AchievementHandler.activateAchievement(stoner, AchievementList.SHEAR_10_SHEEPS, 1);
            AchievementHandler.activateAchievement(stoner, AchievementList.SHEAR_150_SHEEPS, 1);
            break;
          case 1011:
          case 1103:
            stoner.start(new WeaponGameDialogue(stoner));
            break;
          case 534:
            stoner.start(
                new OptionDialogue(
                    "Clothing shop 1",
                    p -> {
                      stoner.getShopping().open(28);
                    },
                    "Clothing shop 2",
                    p -> {
                      stoner.getShopping().open(40);
                    }));
            break;
          case 6749:
            if (stoner.inMemberZone()) {
              stoner.start(new DunceDialogue(stoner));
            } else {
              stoner.start(new StaffTitleDialogue(stoner));
            }
            break;
          case 2181:
            stoner.start(new TzhaarMejKahDialogue(stoner));
            break;
          case 2195:
			  stoner.send(new SendMessage("Help me..."));
            break;
          case 490:
            stoner.start(new NeiveDialogue(stoner));
            break;
          case 1603:
            stoner.start(new KolodionDialogue(stoner));
            break;

          case 5811:
            stoner.getShopping().open(36);
            break;
          case 822:
            stoner.start(new OziachDialogue(stoner));
            break;
          case 954:
			  stoner.send(new SendMessage("Fix items for 250,000 bestbucks each. Simply use your item on me."));
            break;
          case 5787:
            stoner.send(new SendString("Exercisement Ticket Exchange", 8383));
            stoner.send(new SendInterface(8292));
            break;
          case 732:

                      stoner.getShopping().open(33);
            break;
          case 5523:
            stoner.start(new MembershipDialogue(stoner));
            break;
          case 3231:
            HideTanning.sendTanningInterface(stoner);
            break;
          case 394:
          case 395:
          case 2182:
            stoner.getBank().openBank();
            break;
          case 1558:
            stoner.start(new BestBudzDialogue(stoner));
            break;
          case 13:
            stoner.start(new PilesDialogue(stoner));
            break;
          case 403:
            stoner.start(new VannakaDialogue(stoner));
            break;
          case 1306:
            stoner.start(new MakeoverMage(stoner));
            break;
          case 315:
            stoner.start(new EmblemDialogue(stoner));
            break;
          case 4936:
            stoner.start(new ConsumerTeleport(stoner, mob));
            break;
          case 5419:
          case 326:
            stoner.start(new GenieResetDialogue(stoner));
            break;
          case 1325:
            stoner.start(new HariDialogue(stoner));
            break;
          case 606:
            stoner.start(new AdvanceDialogue(stoner));
            break;
          case 2461:
            stoner.start(new KamfreeDialogue());
            break;
          case 1756:
			  stoner.send(new SendMessage("HABALALAAAAAA SHAAAHAAAABBAAALLAAAA!"));
            stoner.send(new SendMessage("Maybe I should focus on defeating the portals..."));
            break;
          case 1771:
			  stoner.send(new SendMessage("Welcome to Pest Control " + stoner.getUsername() + "!"));
            break;
          case 5527:
            stoner.start(new AchievementDialogue(stoner));
            break;
          case 6524:
            stoner.start(new DecantingDialogue(stoner));
            break;

          default:
            if (stoner.getUsername().equalsIgnoreCase(StonerConstants.OWNER_USERNAME[0])) {
              stoner.getClient().queueOutgoingPacket(new SendMessage("Mob id: " + mob.getId()));
            }

            if (OneLineDialogue.doOneLineChat(stoner, id)) {
              return;
            }
			  stoner.send(new SendMessage(Utility.randomElement(DEFAULT_DIALOGUES)));

            break;
        }
        break;

      case 2:
        for (int i = 0; i < NPC_STORE_DATA.length; i++) {
          if (NPC_STORE_DATA[i][0] == id) {
            stoner.getShopping().open(NPC_STORE_DATA[i][1]);
            return;
          }
        }

        if (stoner.getFisher().clickNpc(mob, id, 2)) {
          return;
        }

        switch (id) {
          case 2195:
            stoner.getShopping().open(37);
            break;
          case 5527:
            stoner.getShopping().open(89);
            break;
          case 1603:
            stoner.getShopping().open(95);
            break;
          case 2130:
          case 2131:
          case 2132:
            if (!stoner.getActivePets().contains(mob) || mob.getOwner() != stoner) {
              stoner
                  .getClient()
                  .queueOutgoingPacket(new SendMessage("Please try again, it might just work."));
              return;
            }

            List<Object[]> dialogues = new ArrayList<>();
            if (id != 2130) {
              dialogues.add(
                  new Object[] {
                    "Green",
                    (Consumer<Stoner>)
                        p -> {
                          mob.transform(2130);
                          p.send(new SendRemoveInterfaces());
                        }
                  });
            }
            if (id != 2131) {
              dialogues.add(
                  new Object[] {
                    "Red",
                    (Consumer<Stoner>)
                        p -> {
                          mob.transform(2131);
                          p.send(new SendRemoveInterfaces());
                        }
                  });
            }
            if (id != 2132) {
              dialogues.add(
                  new Object[] {
                    "Blue",
                    (Consumer<Stoner>)
                        p -> {
                          mob.transform(2132);
                          p.send(new SendRemoveInterfaces());
                        }
                  });
            }
            stoner.start(
                new OptionDialogue(
                    (String) dialogues.get(0)[0],
                    (Consumer<Stoner>) dialogues.get(0)[1],
                    (String) dialogues.get(1)[0],
                    (Consumer<Stoner>) dialogues.get(1)[1]));
            break;

          case 822:
            stoner.getShopping().open(34);
            break;
          case 5787:
            stoner.send(new SendString("Exercisement Ticket Exchange", 8383));
            stoner.send(new SendInterface(8292));
            break;
          case 5523:
            stoner.start(
                new OptionDialogue(
                    "Credit Shop 1",
                    p -> {
                      stoner.getShopping().open(94);
                    },
                    "Credit Store 2",
                    p -> {
                      stoner.getShopping().open(90);
                    },
                    "Credit Store 3",
                    p -> {
                      stoner.getShopping().open(87);
                    }));

            break;
          case 606:
            stoner.getShopping().open(93);
            break;
          case 395:
            stoner.getBank().openBank();
            break;
          case 3951:
            stoner.send(new SendInterface(55000));
            break;
          case 403:
          case 490:
            stoner.getShopping().open(6);
            break;
          case 315:
            stoner.getShopping().open(BountyShop.SHOP_ID);
            break;
          case 5419:
            break;
          case 326:
            stoner.send(new SendInterface(59500));
            break;
          case 3789:
          case 3788:
            stoner.getShopping().open(PestShop.SHOP_ID);
            break;
          case 494:
          case 902:
          case 6538:
          case 2271:
            stoner.getBank().openBank();
            break;
          case 6524:
			  stoner.send(new SendMessage("I can decant your potions for 300 gp each."));
            break;
          case 4936:
            stoner.getShopping().open(30);
            break;

          default:
            if (BestbudzConstants.DEV_MODE) {
              stoner.getClient().queueOutgoingPacket(new SendMessage("Mob id: " + mob.getId()));
            }
			  stoner.send(new SendMessage( Utility.randomElement(DEFAULT_DIALOGUES)));
            break;
        }
        break;

      case 3:
        for (int i = 0; i < NPC_STORE_DATA.length; i++) {
          if (NPC_STORE_DATA[i][0] == id) {
            stoner.getShopping().open(NPC_STORE_DATA[i][1]);
            return;
          }
        }
        switch (id) {
          case 1103:
            WeaponGameStore.open(stoner);
            break;
          case 1306:
            break;
          case 5523:
            if (StonerConstants.isStoner(stoner)) {
				stoner.send(new SendMessage("You need to be a <img=4>@red@member </col>to do this!"));
              return;
            }
            TaskQueue.queue(new TeleOtherTask(mob, stoner, StonerConstants.MEMEBER_AREA));
            break;
          case 4936:

                      stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
                      TaskQueue.queue(new TeleOtherTask(mob, stoner, new Location(3039, 4834)));

            break;
          case 1325:
            DropTable.open(stoner);
            break;
          case 606:
            Advance.update(stoner);
            stoner.send(new SendInterface(51000));
            break;
          case 315:
            if (stoner.getSkulling().isSkulled()) {
				stoner.send(new SendMessage("You already have a wilderness skull!"));
              return;
            }
            stoner.getSkulling().skull(stoner, stoner);
			  stoner.send(new SendMessage("You have been skulled."));
            break;
          case 6524:
            PotionDecanting.decantAll(stoner);
            break;
          case 5419:
            break;

          default:
			  stoner.send(new SendMessage(Utility.randomElement(DEFAULT_DIALOGUES)));
        }
        break;

      case 4:
        switch (id) {
          case 315:
            if (stoner.getSkulling().isSkulled()) {
				stoner.send(new SendMessage("You already have a wilderness skull!"));
              return;
            }

            stoner.getSkulling().skull(stoner, stoner);
			  stoner.send(new SendMessage("You have been skulled."));
            break;
        }

      default:
		  stoner.send(new SendMessage( Utility.randomElement(DEFAULT_DIALOGUES)));
        break;
    }
  }

  public static final void finishItemOnNpc(Stoner stoner, int item, Mob mob) {
    switch (mob.getId()) {
      case 2181:
        if (!StonerConstants.isOwner(stoner)) {
          stoner.send(new SendMessage("Coming soon!"));
          stoner.send(new SendRemoveInterfaces());
          return;
        }
        if (!stoner.getBox().hasItemId(6570)) {
			stoner.send(new SendMessage("You don't have a Firecape to do this!"));
          return;
        }
        stoner.getBox().remove(6570, 1);
        if (Utility.random(200) == 0) {
          PetData petDrop = PetData.forItem(13178);

          if (petDrop != null) {
            if (stoner.getActivePets().size() < 5) {
              BossPets.spawnPet(stoner, petDrop.getItem(), true);
              stoner.send(
                  new SendMessage(
                      "You feel a pressence following you; "
                          + Utility.formatStonerName(
                              GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName())
                          + " starts to follow you."));
            } else {
              stoner.getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
              stoner.send(new SendMessage("You feel a pressence added to your bank."));
            }
          } else {
            GroundItemHandler.add(new Item(13178, 1), stoner.getLocation(), stoner);
          }
        } else {
          stoner.send(new SendMessage("@red@You have sacrificed a Fire cape... Nothing happens."));
        }
        stoner.send(new SendRemoveInterfaces());
        break;

      case 954:
        for (int[] id : Barrows.BROKEN_BARROWS) {
          if (item == id[1]) {
            if (stoner.getBox().contains(new Item(995, 250_000))) {
              stoner.getBox().remove(item, 1);
              stoner.getBox().remove(new Item(995, 250_000));
              stoner.getBox().add(new Item(id[0]));
				stoner.send(new SendMessage("Your "
                      + GameDefinitionLoader.getItemDef(id[0]).getName()
                      + " has been repaired."));
            } else {
				stoner.send(new SendMessage("You need 250k to repair your barrows piece!"));
            }
            break;
          }
        }
        break;
    }
  }

  public static void finishObjectClick(Stoner stoner, int id, int option, int x, int y) {
    int z = stoner.getLocation().getZ();

    if (stoner.getMage().isTeleporting()) {
      return;
    }

    if (StonerConstants.isOwner(stoner)) {
      RSObject o = Region.getObject(x, y, stoner.getLocation().getZ());
      if (o != null) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "Object option: "
                        + option
                        + " id: "
                        + id
                        + " x: "
                        + x
                        + " y: "
                        + y
                        + " face: "
                        + o.getFace()
                        + " type: "
                        + o.getType()));
      } else {
        stoner
            .getClient()
            .queueOutgoingPacket(new SendMessage("clicking id with no reference: " + id));
      }
    }

    ObjectDef def = ObjectDef.getObjectDef(id);

    if (def != null && def.name != null && def.name.toLowerCase().contains("vines")) {
      RSObject o = Region.getObject(x, y, stoner.getLocation().getZ());
      if (o != null) {
        if (o.getFace() == 1 || o.getFace() == 3) {
          if (stoner.getX() > x) {
            stoner.teleport(new Location(stoner.getX() - 2, stoner.getY()));
          } else {
            stoner.teleport(new Location(stoner.getX() + 2, stoner.getY()));
          }
        } else {
          if (stoner.getY() > y) {
            stoner.teleport(new Location(stoner.getX(), stoner.getY() - 2));
          } else {
            stoner.teleport(new Location(stoner.getX(), stoner.getY() + 2));
          }
        }
      }
    }

    if (option == 1) {
      RSObject o = Region.getObject(x, y, stoner.getLocation().getZ());

      if (o == null) {
        o = new RSObject(x, y, z, id, 10, 0);
      }

      if (PyramidPlunder.SINGLETON.clickObject(stoner, o)) {
        return;
      }
    }

    if (Doors.isDoorJammed(stoner, x, y, z)) {
      return;
    }

    if ((id == 1738) && (x == 2839) && (y == 3537)) {
      stoner.teleport(new Location(2839, 3537, 2));
      return;
    }
    if ((id == 1742) && (x == 2445) && (y == 3434)) {
      stoner.teleport(new Location(2445, 3433, 1));
      return;
    }
    if ((id == 1742) && (x == 2444) && (y == 3414)) {
      stoner.teleport(new Location(2445, 3416, 1));
      return;
    }
    if ((id == 1744) && (x == 2445) && (y == 3434)) {
      stoner.teleport(new Location(2446, 3436));
      return;
    }
    if ((id == 1744) && (x == 2445) && (y == 3415)) {
      stoner.teleport(new Location(2444, 3413));
      return;
    }

    if ((id == 15638) && (x == 2840) && (y == 3538)) {
      stoner.teleport(new Location(2840, 3538));
      return;
    }
    if ((id == 1738) && (x == 2853) && (y == 3535)) {
      stoner.teleport(new Location(2840, 3539, 2));
      return;
    }

    if (id == 11834) {
      TzharrGame.finish(stoner, false);
      return;
    }

    if (PestControl.clickObject(stoner, id)) {
      return;
    }

    if (WeaponGame.objectClick(stoner, id, x, y, z)) {
      return;
    }

    switch (option) {
      case 1:
        if (stoner.getDueling().clickForfeitTrapDoor(id)) {
          return;
        }

        if (GodWars.clickObject(stoner, id, x, y, z)) {
          return;
        }

        if (FightPits.clickObject(stoner, id)) {
          return;
        }

        if (PestControl.clickObject(stoner, id)) {
          return;
        }

        if ((id >= 7) && (id <= 9)) {
          if (DwarfMultiCannon.hasCannon(stoner)) {
            DwarfMultiCannon.getCannon(stoner).pickup(stoner, x, y);
          }
          return;
        }

        if ((id == 5264) && (x == 3161) && (y == 4236)) {
          stoner.teleport(new Location(3085, 3493));
          return;
        }
        if (ObeliskTick.clickObelisk(id)) {
          return;
        }
        if (Doors.clickDoor(stoner, id, x, y, stoner.getLocation().getZ())) {
          return;
        }

        if (Exercisement.SINGLETON.fireObjectClick(
            stoner, stoner.getLocation(), Region.getObject(x, y, stoner.getLocation().getZ()))) {
          return;
        }

        if (ClanWarsFFA.clickObject(stoner, id)) {
          return;
        }

        if (ObjectDef.getObjectDef(id) != null
            && ObjectDef.getObjectDef(id).name != null
            && ObjectDef.getObjectDef(id).name.toLowerCase().contains("bank booth")) {
          stoner.getBank().openBank();
          return;
        }

        if (ObjectConstants.isSummoningObelisk(id)) {
          if (stoner.getGrades()[21] == stoner.getMaxGrades()[21]) {
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("You already have full Summoning points."));
          } else {
            stoner.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
            stoner
                .getClient()
                .queueOutgoingPacket(
                    new SendMessage("You recharge your Summoning points at the obelisk."));
            stoner.getUpdateFlags().sendAnimation(8502, 0);
            stoner.getUpdateFlags().sendGraphic(new Graphic(1308, 0, false));
            stoner.getGrades()[21] = stoner.getMaxGrades()[21];
            stoner.getProfession().update(21);
          }
          return;
        }

        if (Quarrying.clickRock(stoner, Region.getObject(x, y, stoner.getLocation().getZ()))) {
          return;
        }
        LumberingTask.attemptLumbering(stoner, id, x, y);
        switch (id) {
          case 22472:
            stoner.send(new SendString("Please click on a tab to create it", 26707));
            stoner.send(new SendString("Click on 'info' to get requirements.", 26708));
            int[] tabs = {8007, 8008, 8009, 8010, 8011, 8012, 8013, 8014, 8015};
            for (int i = 0; i < tabs.length; i++) {
              stoner.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(26706, tabs[i], 1, i));
            }
            stoner.send(new SendInterface(26700));
            break;
          case 3193:
          case 26707:
            stoner.getBank().openBank();
            break;

          case 26762:
            stoner.getUpdateFlags().sendFaceToDirection(x, y);
            stoner.getUpdateFlags().sendAnimation(new Animation(844));
            TaskQueue.queue(
                new Task(stoner, 1, false) {
                  @Override
                  public void execute() {
                    stop();
                  }

                  @Override
                  public void onStop() {
                    if (stoner.getLocation().equals(new Location(3233, 3938))
                        || stoner.getLocation().equals(new Location(3232, 3938))) {
                      stoner.teleport(new Location(3233, 10332));
                    } else if (stoner.getLocation().equals(new Location(3233, 3950))
                        || stoner.getLocation().equals(new Location(3232, 3950))) {
                      stoner.teleport(new Location(3232, 10351));
                    } else if (stoner.getLocation().equals(new Location(3242, 3948))
                        || stoner.getLocation().equals(new Location(3243, 3948))) {
                      stoner.teleport(new Location(3243, 10351));
                    }
                  }
                });
            break;

          case 26763:
            stoner.getUpdateFlags().sendFaceToDirection(x, y);
            stoner.getUpdateFlags().sendAnimation(new Animation(844));
            TaskQueue.queue(
                new Task(stoner, 1, false) {
                  @Override
                  public void execute() {
                    stop();
                  }

                  @Override
                  public void onStop() {
                    if (stoner.getLocation().equals(new Location(3233, 10332))) {
                      stoner.teleport(new Location(3233, 3938));
                    } else if (stoner.getLocation().equals(new Location(3232, 10351))) {
                      stoner.teleport(new Location(3233, 3950));
                    } else if (stoner.getLocation().equals(new Location(3243, 10351))) {
                      stoner.teleport(new Location(3242, 3948));
                    }
                  }
                });
            break;

          case 2878:
            TaskQueue.queue(
                new Task(stoner, 1) {
                  @Override
                  public void execute() {
                    stoner.getUpdateFlags().sendAnimation(new Animation(7133));
                    stop();
                  }

                  @Override
                  public void onStop() {
                    stoner.teleport(new Location(2509, 4689, 0));
                  }
                });
            break;
          case 2879:
            TaskQueue.queue(
                new Task(stoner, 1) {
                  @Override
                  public void execute() {
                    stoner.getUpdateFlags().sendAnimation(new Animation(7133));
                    stop();
                  }

                  @Override
                  public void onStop() {
                    stoner.teleport(new Location(2542, 4718, 0));
                  }
                });
            break;
          case 2873:
            if (ItemCheck.hasGodCape(stoner)) {
				stoner.send(new SendMessage("It appears you already have a god cape!"));
              return;
            }
            if (stoner.getProfession().getGrades()[Professions.MAGE] < 60) {
				stoner.send(new SendMessage("You need a Mage grade of 60 to do this!"));
              return;
            }
            stoner.getUpdateFlags().sendAnimation(new Animation(645));
            stoner.getBox().add(new Item(2412));
            stoner.send(new SendMessage("You have obtained a Saradomin cape."));
            break;
          case 2875:
            if (ItemCheck.hasGodCape(stoner)) {
				stoner.send(new SendMessage("It appears you already have a god cape!"));
              return;
            }
            if (stoner.getProfession().getGrades()[Professions.MAGE] < 60) {
				stoner.send(new SendMessage("You need a Mage grade of 60 to do this!"));
              return;
            }
            stoner.getUpdateFlags().sendAnimation(new Animation(645));
            stoner.getBox().add(new Item(2413));
            stoner.send(new SendMessage("You have obtained a Guthix cape."));
            break;
          case 2874:
            if (ItemCheck.hasGodCape(stoner)) {
				stoner.send(new SendMessage("It appears you already have a god cape!"));
              return;
            }
            if (stoner.getProfession().getGrades()[Professions.MAGE] < 60) {
				stoner.send(new SendMessage("You need a Mage grade of 60 to do this!"));
              return;
            }
            stoner.getUpdateFlags().sendAnimation(new Animation(645));
            stoner.getBox().add(new Item(2414));
            stoner.send(new SendMessage("You have obtained a Zamorak cape."));
            break;
          case 13618:

                      stoner.teleport(new Location(3056, 9555, 0));
                      stoner.send(
                          new SendMessage("You have been teleported to the Skeletal Wyverns."));

            break;
          case 13619:

                      stoner
                          .getMage()
                          .teleport(3372, 3894, 0, MageProfession.TeleportTypes.FOUNTAIN_OF_RUNE);
                      stoner.send(
                          new SendMessage("You have been teleported to the Fountain of Rune."));
            break;

          case 10596:
            stoner.teleport(new Location(3056, 9555, 0));
            break;
          case 12941:
            break;
          case 26760:
            if (stoner.getLocation().getX() == 3184 && stoner.getLocation().getY() == 3945) {
              if (stoner.isPouchPayment()) {
                if (stoner.getMoneyPouch() < 7500) {
					stoner.send(new SendMessage("You need 7,500 bestbucks to enter the arena."));
                  return;
                }
              } else {
                if (!stoner.getBox().hasItemAmount(995, 7500)) {
					stoner.send(new SendMessage("You need 7,500 bestbucks to enter the arena."));
                  return;
                }
              }
              if (stoner.isPouchPayment()) {
                stoner.setMoneyPouch(stoner.getMoneyPouch() - 7500);
                stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
              } else {
                stoner.getBox().remove(995, 7500);
              }
              stoner.send(
                  new SendMessage("You have paid 7,500 bestbucks and entered the resource arena."));
              TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y - 1, z)));
            } else {
              stoner.getAttributes().set("stopMovement", Boolean.valueOf(true));
              TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y + 1, z)));
            }
            break;
          case 10230:
            stoner.teleport(new Location(2900, 4449, 0));
            break;
          case 9472:
          case 9741:
          case 9371:
          case 24306:
          case 24309:
            CyclopsRoom.handleDoor(stoner, x, y);
            break;
          case 16671:
            stoner.teleport(new Location(2841, 3538, 2));
            stoner.send(new SendMessage("You climb up the stairs to the next floor."));
            break;
          case 24303:
            stoner.teleport(new Location(2841, 3538, 0));
            stoner.send(new SendMessage("You climb down the stairs to the previous floor."));
            break;
          case 7236:
            WallSafes.crack(stoner);
            break;
          case 18987:
            stoner.send(new SendMessage("You have climbed down the ladder."));
            stoner.teleport(new Location(2271, 4680));
            break;
          case 677:
            if (stoner.getX() == 2974) {
              stoner.teleport(new Location(stoner.getX() - 3, stoner.getY(), stoner.getZ()));
            } else {
              stoner.teleport(new Location(stoner.getX() + 3, stoner.getY(), stoner.getZ()));
            }
            break;
          case 11183:
          case 11185:
          case 11184:
            if (stoner.getBox().hasSpaceFor(new Item(444))) {
              stoner.getBox().add(new Item(444));
              stoner.getClient().queueOutgoingPacket(new SendMessage("You get some gold ore."));
            }
            break;
          case 11188:
          case 11186:
          case 11187:
            if (stoner.getBox().hasSpaceFor(new Item(442))) {
              stoner.getBox().add(new Item(442));
              stoner.getClient().queueOutgoingPacket(new SendMessage("You get some silver ore."));
            }
            break;
          case 11191:
          case 11189:
          case 11190:
            if (stoner.getBox().hasSpaceFor(new Item(434))) {
              stoner.getBox().add(new Item(434));
              stoner.getClient().queueOutgoingPacket(new SendMessage("You get some clay."));
            }
            break;
          case 2073:
            if (stoner.getBox().hasSpaceFor(new Item(1963))) {
              stoner.getBox().add(new Item(1963));
              stoner
                  .getClient()
                  .queueOutgoingPacket(new SendMessage("You pick the banana from the tree."));
            }
            break;
          case 2557:
          case 2558:
            if (stoner.getX() == 3038 && stoner.getY() == 3956
                || stoner.getX() == 3044 && stoner.getY() == 3956
                || stoner.getX() == 3041 && stoner.getY() == 3959
                || stoner.getX() == 3190 && stoner.getY() == 3957
                || stoner.getX() == 3191 && stoner.getY() == 3963) {
              stoner.getUpdateFlags().sendAnimation(new Animation(2246));
              Task task = new Lockpick(stoner, (byte) 2, id, x, y, z);
              stoner.getAttributes().set("lockPick", task);
              TaskQueue.queue(task);
            } else {
              stoner.getClient().queueOutgoingPacket(new SendMessage("This door is locked.."));
            }
            break;
          case 2147:
            if (x == 2324 && y == 3809) {
              stoner.teleport(new Location(3577, 9927));
            }
            if (x == 2317 && y == 3807) {
              stoner.teleport(new Location(2884, 9798));
            }
            if (x == 2538 && y == 3895) {
              stoner.teleport(new Location(1764, 5365, 1));
            }
            break;
          case 1568:
            if (x == 3097 && y == 3468) {
              stoner.teleport(new Location(3096, 9867));
            }
            break;
          case 29370:
            if (x == 3150 && y == 9906) {
              stoner.teleport(new Location(3155, 9906));
            }
            if (x == 3153 && y == 9906) {
              stoner.teleport(new Location(3149, 9906));
            }
            break;
          case 1755:
            if (x == 2884 && y == 9797) {
              stoner.teleport(new Location(2318, 3807));
            }
            if (x == 3008 && y == 9550) {
              stoner.teleport(new Location(2364, 3893));
            }
            if (x == 3097 && y == 9867) {
              stoner.teleport(new Location(3096, 3468));
            }
            break;
          case 1757:
            if (x == 3578 && y == 9927) {
              stoner.teleport(new Location(2324, 3808));
            }
            break;
          case 23555:
            if (stoner.getProfession().locked()) {
              return;
            }
            stoner.getProfession().lock(4);

            if (stoner.getLocation().getY() < 3917) {
              TaskQueue.queue(
                  new WalkThroughDoorTask(stoner, x, y, z, new Location(2998, 3931, z)) {
                    final DoubleDoor normalDoor = Region.getDoubleDoor(2998, 3931, 0);
                    boolean stopBalance = false;
                    WalkInteraction balance;

                    @Override
                    public void execute() {
                      if (stage == 0) {
                        p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
                        ObjectManager.remove2(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                door.getX(),
                                door.getY(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace()));
                        door.append();
                        ObjectManager.send(
                            new GameObject(
                                door.getCurrentId(),
                                door.getX(),
                                door.getY(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace()));
                        stage++;
                      } else if (stage == 1) {
                        p.getMovementHandler().walkTo(xMod, yMod);
                        stage++;
                      } else if (stage == 2) {
                        ObjectManager.send(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                door.getX(),
                                door.getY(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace()));
                        door.append();
                        ObjectManager.send(
                            new GameObject(
                                door.getCurrentId(),
                                door.getX(),
                                door.getY(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace()));
                        stage++;
                      } else if (stage == 3) {
                        stage++;
                      } else if (stage == 4 && balance == null) {
                        balance =
                            new WalkInteraction() {
                              @Override
                              public int getAnimation() {
                                return 762;
                              }

                              @Override
                              public String getPreMessage() {
                                return null;
                              }

                              @Override
                              public String getPostMessage() {
                                return null;
                              }

                              @Override
                              public void onCancellation(Stoner stoner) {
                                WalkInteraction.super.onCancellation(stoner);
                                stopBalance = true;
                              }
                            };

                        balance.execute(
                            stoner, null, stoner.getLocation(), new Location(2998, 3930), 0);
                      } else if (stage == 5) {
                        p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
                        ObjectManager.remove2(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                normalDoor.getX1(),
                                normalDoor.getY1(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace1()));
                        ObjectManager.remove2(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                normalDoor.getX2(),
                                normalDoor.getY2(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace2()));
                        normalDoor.append();
                        ObjectManager.send(
                            new GameObject(
                                normalDoor.getCurrentId1(),
                                normalDoor.getX1(),
                                normalDoor.getY1(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace1()));
                        ObjectManager.send(
                            new GameObject(
                                normalDoor.getCurrentId2(),
                                normalDoor.getX2(),
                                normalDoor.getY2(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace2()));
                        stage++;
                      } else if (stage == 6) {
                        p.getMovementHandler().walkTo(0, 1);
                        stage++;
                      } else if (stage == 7) {
                        ObjectManager.send(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                normalDoor.getX1(),
                                normalDoor.getY1(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace1()));
                        ObjectManager.send(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                normalDoor.getX2(),
                                normalDoor.getY2(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace2()));
                        normalDoor.append();
                        ObjectManager.send(
                            new GameObject(
                                normalDoor.getCurrentId1(),
                                normalDoor.getX1(),
                                normalDoor.getY1(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace1()));
                        ObjectManager.send(
                            new GameObject(
                                normalDoor.getCurrentId2(),
                                normalDoor.getX2(),
                                normalDoor.getY2(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace2()));
                        stop();
                        return;
                      }

                      if (stopBalance) {
                        stage++;
                        stopBalance = false;
                      }
                    }
                  });
            }
            break;
          case 23552:
          case 23554:
            if (stoner.getProfession().locked()) {
              return;
            }

            stoner.getProfession().lock(4);

            if (stoner.getLocation().getY() >= 3931) {
              TaskQueue.queue(
                  new WalkThroughDoubleDoorTask(stoner, x, y, z, new Location(2998, 3931, z)) {
                    final Door normalDoor = Region.getDoor(2998, 3917, 0);
                    boolean stopBalance = false;
                    WalkInteraction balance;

                    @Override
                    public void execute() {
                      if (stage == 0) {
                        p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
                        ObjectManager.remove2(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                door.getX1(),
                                door.getY1(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace1()));
                        ObjectManager.remove2(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                door.getX2(),
                                door.getY2(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace2()));
                        door.append();
                        ObjectManager.send(
                            new GameObject(
                                door.getCurrentId1(),
                                door.getX1(),
                                door.getY1(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace1()));
                        ObjectManager.send(
                            new GameObject(
                                door.getCurrentId2(),
                                door.getX2(),
                                door.getY2(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace2()));
                        stage++;
                      } else if (stage == 1) {
                        p.getMovementHandler().walkTo(xMod, yMod);
                        stage++;
                      } else if (stage == 2) {
                        ObjectManager.send(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                door.getX1(),
                                door.getY1(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace1()));
                        ObjectManager.send(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                door.getX2(),
                                door.getY2(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace2()));
                        door.append();
                        ObjectManager.send(
                            new GameObject(
                                door.getCurrentId1(),
                                door.getX1(),
                                door.getY1(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace1()));
                        ObjectManager.send(
                            new GameObject(
                                door.getCurrentId2(),
                                door.getX2(),
                                door.getY2(),
                                door.getZ(),
                                door.getType(),
                                door.getCurrentFace2()));
                        stage++;
                      } else if (stage == 3) {
                        stage++;
                      } else if (stage == 4 && balance == null) {
                        balance =
                            new WalkInteraction() {
                              @Override
                              public int getAnimation() {
                                return 762;
                              }

                              @Override
                              public String getPreMessage() {
                                return null;
                              }

                              @Override
                              public String getPostMessage() {
                                return null;
                              }

                              @Override
                              public void onCancellation(Stoner stoner) {
                                WalkInteraction.super.onCancellation(stoner);
                                stopBalance = true;
                              }
                            };

                        balance.execute(
                            stoner, null, stoner.getLocation(), new Location(2998, 3916), 0);
                      } else if (stage == 5) {
                        p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
                        ObjectManager.remove2(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                normalDoor.getX(),
                                normalDoor.getY(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace()));
                        normalDoor.append();
                        ObjectManager.send(
                            new GameObject(
                                normalDoor.getCurrentId(),
                                normalDoor.getX(),
                                normalDoor.getY(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace()));
                        stage++;
                      } else if (stage == 6) {
                        p.getMovementHandler().walkTo(0, -1);
                        stage++;
                      } else if (stage == 7) {
                        ObjectManager.send(
                            new GameObject(
                                ObjectManager.BLANK_OBJECT_ID,
                                normalDoor.getX(),
                                normalDoor.getY(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace()));
                        normalDoor.append();
                        ObjectManager.send(
                            new GameObject(
                                normalDoor.getCurrentId(),
                                normalDoor.getX(),
                                normalDoor.getY(),
                                normalDoor.getZ(),
                                normalDoor.getType(),
                                normalDoor.getCurrentFace()));
                        stop();
                        return;
                      }

                      if (stopBalance) {
                        stage++;
                        stopBalance = false;
                      }
                    }
                  });
            }
            break;
          case 26933:
            stoner.teleport(new Location(3096, 9867));
            break;
          case 5084:
            stoner.teleport(new Location(2401, 3888));
            break;
          case 21584:
            stoner.teleport(new Location(2712, 9564));
            break;
          case 10595:
            stoner.teleport(new Location(3056, 9562));
            break;
          case 1733:
            stoner.teleport(new Location(stoner.getLocation().getX(), 10322, 0));
            break;
          case 1734:
            stoner.teleport(new Location(stoner.getLocation().getX(), 3927, 0));
            break;
          case 25338:
            stoner.teleport(new Location(1772, 5366, 0));
            break;
          case 25336:
            stoner.teleport(new Location(1768, 5366, 1));
            break;
          case 21311:
            stoner.teleport(new Location(2314, 3839));
            break;
          case 21310:
            stoner.teleport(new Location(2314, 3848));
            break;
          case 21585:
            stoner.teleport(new Location(2882, 5310, 2));
            break;
          case 21586:
            stoner.teleport(new Location(3007, 9550));
            break;
          case 5083:
            stoner.teleport(new Location(2712, 9564));
            break;
          case 10041:
			  stoner.send(new SendMessage("Woah Woah! Watch out!"));
            break;
          case 21309:
            stoner.getMovementHandler().addToPath(new Location(2343, 3820));
            stoner.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
            break;
          case 21308:
            stoner.getMovementHandler().addToPath(new Location(2343, 3829));
            stoner.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
            break;
          case 21306:
            stoner.getMovementHandler().addToPath(new Location(2317, 3832));
            stoner.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
            break;
          case 21307:
            stoner.getMovementHandler().addToPath(new Location(2317, 3823));
            stoner.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
            break;
          case 272:
            stoner.getUpdateFlags().sendAnimation(new Animation(828));
            stoner.teleport(new Location(3018, 3956, 1));
            break;
          case 273:
            stoner.getUpdateFlags().sendAnimation(new Animation(828));
            stoner.teleport(new Location(3018, 3958, 0));
            break;

          case 245:
            if (x == 3017 && y == 3959) {
              stoner.teleport(new Location(3017, 3960, 2));
            } else if (x == 3019 && y == 3959) {
              stoner.teleport(new Location(3019, 3960, 2));
            }
            break;

          case 246:
            stoner.teleport(new Location(stoner.getX(), stoner.getY() - 2, 1));
            break;
          case 9358:
            stoner.teleport(new Location(2480, 5175));
            break;
          case 492:
            stoner.teleport(new Location(2856, 9570));
            break;

          case 25339:
            stoner.teleport(new Location(1778, 5343, 1));
            break;

          case 25340:
            stoner.teleport(new Location(1778, 5346, 0));
            break;

          case 37928:
            stoner.teleport(new Location(3404, 3090));
            break;

          case 10229:
            stoner.teleport(new Location(2488, 10151));
            break;

          case 8929:
            stoner.teleport(new Location(2442, 10146));
            break;

          case 8966:
            stoner.teleport(new Location(2523, 3739));
            break;

          case 41077:
            stoner.teleport(new Location(3106, 3955));
            break;

          case 3832:
            stoner.teleport(new Location(3509, 9499, 2));
            break;
          case 3829:
            stoner.teleport(new Location(3228, 3110));
            break;

          case 412:
			  stoner.send(new SendMessage("Use the dock to swap spellbooks!"));
            break;

          case 6552:
            stoner
                .getMage()
                .setSpellBookType(
                    stoner.getMage().getSpellBookType() == SpellBookTypes.MODERN
                        ? SpellBookTypes.ANCIENT
                        : SpellBookTypes.MODERN);
            stoner
                .getMage()
                .setMageBook(
                    stoner.getMage().getSpellBookType() == SpellBookTypes.MODERN ? 1151 : 12855);
            stoner.getUpdateFlags().sendAnimation(new Animation(645));
            break;
          case 409:
          case 10638:
          case 19145:
            if (stoner.getProfession().getGrades()[Professions.NECROMANCE]
                < stoner.getMaxGrades()[Professions.NECROMANCE]) {
              stoner
                  .getProfession()
                  .setGrade(Professions.NECROMANCE, stoner.getMaxGrades()[Professions.NECROMANCE]);
              stoner
                  .getClient()
                  .queueOutgoingPacket(new SendMessage("You recharge your necromance points."));
              stoner.getUpdateFlags().sendAnimation(new Animation(645));
            } else {
              stoner
                  .getClient()
                  .queueOutgoingPacket(new SendMessage("Your necromance is already full."));
            }
            break;
          case 18772:
            MysteryBoxMinigame.open(stoner);
            break;

          case 2113:
            stoner.teleport(new Location(3021, 9739));
            break;
          case 30941:
            stoner.teleport(new Location(3019, 3337));
            break;

          case 7257:
            stoner.teleport(new Location(3061, 4985, 1));
            break;
          case 7258:
            stoner.teleport(new Location(2906, 3537));
            break;

          case 5008:
            stoner.teleport(new Location(3206, 9379, 0));
            break;
          case 4499:
            stoner.teleport(new Location(3206, 9379, 0));
            stoner
                .getClient()
                .queueOutgoingPacket(
                    new SendMessage(
                        "This is an alternative entrance, use the rope to find the shorter cave entrance."));
            break;
          case 6439:
            stoner.teleport(new Location(2730, 3713, 0));
            break;
          case 1765:
            if (x == 3017 && y == 3849) {
              stoner.teleport(new Location(3069, 10255));
            }
            break;
          case 1766:
            if (x == 3069 && y == 10256) {
              stoner.teleport(new Location(3017, 3850));
            }
            break;
          case 1817:
            stoner.teleport(new Location(3017, 3848));
            break;
          case 36687:
            stoner.teleport(new Location(3210, 9616, 0));
            break;
          case 29355:
            stoner.teleport(new Location(3210, 3216, 0));
            break;
          case 2492:
            stoner.getMage().teleport(2809, 3436, 0, MageProfession.TeleportTypes.SPELL_BOOK);
            break;
          case 2191:
            if (stoner.getBox().contains(new Item(989))) {
              CrystalChest.searchChest(stoner, x, y);
            } else {
              stoner.send(new SendMessage("You need a key to open this chest."));
            }
            break;
          case 9319:
            stoner.changeZ(1);
            break;
          case 9320:
            stoner.changeZ(0);
            break;
          case 11601:
            JewelryCreationTask.sendInterface(stoner);
            break;
          case 2806:
            stoner.teleport(new Location(2885, 4372));
            break;
          case 37929:
            if (stoner.getLocation().getX() < 2926) {
              if (stoner.getLocation().getX() <= 2918) stoner.teleport(new Location(2921, 4384));
              else {
                stoner.teleport(new Location(2917, 4384));
              }
            } else if (stoner.getLocation().getX() <= 2971)
              stoner.teleport(new Location(2974, 3484));
            else {
              stoner.teleport(new Location(2970, 4384));
            }

            break;
          case 1293:
            stoner.start(new SpiritTree(stoner));
            break;
          case 2640:
          case 4859:
          case 27661:
            if (stoner.getGrades()[5] == stoner.getMaxGrades()[5]) {
              stoner
                  .getClient()
                  .queueOutgoingPacket(new SendMessage("You already have full Necromance."));
            } else {
              stoner.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
              stoner
                  .getClient()
                  .queueOutgoingPacket(
                      new SendMessage("You recharge your Necromance points at the altar."));
              stoner.getUpdateFlags().sendAnimation(645, 5);
              stoner.getGrades()[5] = stoner.getMaxGrades()[5];
              stoner.getProfession().update(5);
            }
            break;
          case 23271:
            TaskQueue.queue(new HopDitchTask(stoner));
            if (TargetSystem.getInstance().stonerHasTarget(stoner)) {
              TargetSystem.getInstance().resetTarget(stoner, false);
            }
            stoner.send(new SendString("---", 25351));
            stoner.send(new SendString("---", 25353));
            stoner.send(new SendString("---", 25355));
            break;
          case 11833:
            TzharrGame.init(stoner, false);
            break;
          case 2114:
            stoner.teleport(new Location(3433, 3537, 1));
            break;
          case 2118:
            stoner.teleport(new Location(3438, 3538, 0));
            break;
          case 2119:
            stoner.teleport(new Location(3417, 3541, 2));
            break;
          case 2120:
            stoner.teleport(new Location(3412, 3541, 1));
            break;
          case 2102:
          case 2104:
            if (stoner.getProfession().locked()) {
              return;
            }
            stoner.getProfession().lock(4);
            if (stoner.getLocation().getY() >= 3556) {
              TaskQueue.queue(
                  new WalkThroughDoubleDoorTask(stoner, x, y, z, new Location(x, y - 1, z)));
            } else {
              TaskQueue.queue(
                  new WalkThroughDoubleDoorTask(stoner, x, y, z, new Location(x, y + 1, z)));
            }

            break;
          case 2100:
            if (stoner.getProfession().locked()) {
              return;
            }
            stoner.getProfession().lock(4);
            if (stoner.getLocation().getY() <= 3554) {
              TaskQueue.queue(
                  new WalkThroughDoorTask(stoner, x, y, z, new Location(3445, 3555, z)));
            } else {
              TaskQueue.queue(
                  new WalkThroughDoorTask(stoner, x, y, z, new Location(3445, 3554, z)));
            }
            break;
          case 1596:
          case 1597:
            if (stoner.getLocation().getY() <= 9917) stoner.teleport(new Location(3131, 9918, 0));
            else {
              stoner.teleport(new Location(3131, 9917, 0));
            }
            break;
          case 1557:
          case 1558:
            if (stoner.getLocation().getY() == 9944) stoner.teleport(new Location(3105, 9945, 0));
            else if (stoner.getLocation().getY() == 9945)
              stoner.teleport(new Location(3105, 9944, 0));
            else if (stoner.getLocation().getX() == 3146)
              stoner.teleport(new Location(3145, 9871, 0));
            else if (stoner.getLocation().getX() == 3145) {
              stoner.teleport(new Location(3146, 9871, 0));
            }
            break;
          case 2623:
            if (stoner.getLocation().getX() >= 2924) stoner.teleport(new Location(2923, 9803, 0));
            else {
              stoner.teleport(new Location(2924, 9803, 0));
            }
            break;
          case 8960:
            if (stoner.getLocation().getX() <= 2490) stoner.teleport(new Location(2491, 10131, 0));
            else {
              stoner.teleport(new Location(2490, 10131, 0));
            }
            break;
          case 8959:
            if (stoner.getLocation().getX() <= 2490) stoner.teleport(new Location(2491, 10146, 0));
            else {
              stoner.teleport(new Location(2490, 10146, 0));
            }
            break;
          case 8958:
            if (stoner.getLocation().getX() <= 2490) stoner.teleport(new Location(2491, 10163, 0));
            else {
              stoner.teleport(new Location(2490, 10163, 0));
            }
            break;
          case 5103:
            if (stoner.getLocation().getX() >= 2691) stoner.teleport(new Location(2689, 9564, 0));
            else {
              stoner.teleport(new Location(2691, 9564, 0));
            }
            break;
          case 5104:
            if (stoner.getLocation().getY() <= 9568) stoner.teleport(new Location(2683, 9570, 0));
            else {
              stoner.teleport(new Location(2683, 9568, 0));
            }
            break;
          case 5110:
            stoner.teleport(new Location(2647, 9557, 0));
            break;
          case 5111:
            stoner.teleport(new Location(2649, 9562, 0));
            break;
          case 5106:
            if (stoner.getLocation().getX() <= 2674) stoner.teleport(new Location(2676, 9479, 0));
            else {
              stoner.teleport(new Location(2674, 9479, 0));
            }
            break;
          case 5107:
            if (stoner.getLocation().getX() <= 2693) stoner.teleport(new Location(2695, 9482, 0));
            else {
              stoner.teleport(new Location(2693, 9482, 0));
            }
            break;
          case 5105:
            if (stoner.getLocation().getX() <= 2672) stoner.teleport(new Location(2674, 9499, 0));
            else {
              stoner.teleport(new Location(2672, 9499, 0));
            }
            break;
          case 5088:
            stoner.teleport(new Location(2687, 9506, 0));
            break;
          case 5090:
            stoner.teleport(new Location(2682, 9506, 0));
            break;
          case 5097:
            stoner.teleport(new Location(2636, 9510, 2));
            break;
          case 5098:
            stoner.teleport(new Location(2636, 9517, 0));
            break;
          case 5096:
            stoner.teleport(new Location(2649, 9591, 0));
            break;
          case 5094:
            stoner.teleport(new Location(2643, 9594, 2));
            break;
          case 4309:
            for (Item i : stoner.getBox().getItems()) {
              if (i != null && Spinnable.forId(i.getId()) != null) {
				  Handiness.SINGLETON.handleObjectClick(stoner, 2644);
				  return;
              }
            }
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("You do not have anything to spin!"));
            break;

			case 2097: // Anvil
				for (Item i : stoner.getBox().getItems()) {
					if (i != null && hasSmithableBars(i.getId())) {
						Forging.SINGLETON.handleObjectClick(stoner, id);
						return;
					}
				}
				stoner
					.getClient()
					.queueOutgoingPacket(new SendMessage("You do not have any bars to hammer!"));
				break;

			case 26181: // Range
				for (Item i : stoner.getBox().getItems()) {
					if (i != null && FoodieData.forId(i.getId()) != null) {
						Foodie.SINGLETON.handleObjectClick(stoner, id);
						return;
					}
				}
				stoner
					.getClient()
					.queueOutgoingPacket(new SendMessage("You do not have any raw food to cook!"));
				break;

			case 5249:  // Fyah (Fire Altar)
			case 114:   // Fire
			case 2732:  // Range (can burn on)
			case 26185: // Other fire objects
			case 14901: // Fire Altar
				for (Item i : stoner.getBox().getItems()) {
					if (i != null && Pyromaniac.Wood.forId(i.getId()) != null) {
						PyroAutoBurn.SINGLETON.handleObjectClick(stoner, id);
						return;
					}
				}
				stoner
					.getClient()
					.queueOutgoingPacket(new SendMessage("You do not have any wood to burn!"));
				break;
          default:
            break;
        }

        break;
      case 2:
        if (id == 12309) {
          stoner.getShopping().open(10);
          return;
        }

        Location location = new Location(x, y, z);
        HomeStalls.attempt(stoner, id, location);
        if (DwarfMultiCannon.hasCannon(stoner)) {
          DwarfMultiCannon.getCannon(stoner).pickup(stoner, x, y);
          return;
        }

        if (ObjectDef.getObjectDef(id) != null
            && ObjectDef.getObjectDef(id).name != null
            && ObjectDef.getObjectDef(id).name.toLowerCase().contains("bank booth")) {
          stoner.getBank().openBank();
          return;
        }

        switch (id) {
          case 8720:
          case 26820:
          case 26813:
            stoner.getShopping().open(92);
            break;
          case 7134:
            Flax.pickFlax(stoner, x, y);
            break;
          case 9472:
          case 9741:
          case 9371:
          case 2030:
          case 21303:
          case 24009:
          case 26814:
			  Forging.SINGLETON.handleObjectClick(stoner, id);
			  return;

          case 2557:
          case 2558:
            if (stoner.getProfession().getGrades()[17] < 52) {
              stoner
                  .getClient()
                  .queueOutgoingPacket(
                      new SendMessage(
                          "You need a accomplisher grade of 52 to pick the lock on this door."));
              return;
            }
            if (!stoner.getEquipment().isWearingItem(6575)) {
				stoner.send(new SendMessage("You must be wearing a tool ring to be able to open this door."));
              return;
            }
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("You attempt to pick the lock..."));
            Task task = new Lockpick(stoner, (byte) 2, id, x, y, z);
            stoner.getAttributes().set("lockPick", task);
            TaskQueue.queue(task);
            break;

          case 4309:
            for (Item i : stoner.getBox().getItems()) {
              if (i != null && Spinnable.forId(i.getId()) != null) {
				  Handiness.SINGLETON.handleObjectClick(stoner, 2644);
				  return;
              }
            }

            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("You do not have anything to spin!"));
            break;

          case 3044:
          case 11666:
          case 45310:
			  Forging.SINGLETON.handleObjectClick(stoner, id);
			  return;
          case 2646:
            TaskQueue.queue(new HarvestTask(stoner, id, 1779, x, y, z));
            break;
          case 1293:
            stoner.teleport(new Location(2461, 3434, 0));
            break;

          default:
            break;
        }

        break;
      case 3:
        switch (id) {
          case 8720:
          case 26820:
          case 26813:
			  stoner.send(new SendMessage("You have @blu@"
                    + Utility.format(stoner.getChillPoints())
                    + " </col>chill points."));
            break;
          case 9371:
          case 9472:
          case 9741:

          default:
            return;
        }
        break;
      case 4:
        switch (id) {

        }
        break;
    }
  }

  public static void itemOnObject(
      final Stoner stoner, final int itemId, final int objectId, final int x, final int y) {
    if (BestbudzConstants.DEV_MODE) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("Using item " + itemId + " on object " + objectId));
    }

    if (stoner.getMage().isTeleporting()) {
      return;
    }

    RSObject object = Region.getObject(x, y, stoner.getLocation().getZ());

    int z = stoner.getLocation().getZ();

    if ((object == null)
        && (!StonerConstants.isOverrideObjectExistance(stoner, objectId, x, y, z))) {
      return;
    }

    final int[] length =
        ObjectConstants.getObjectLength(objectId, object != null ? object.getFace() : 0);

    TaskQueue.queue(
        new WalkToTask(stoner, x, y, length[0], length[1]) {
          @Override
          public void onDestination() {
            stoner
                .getUpdateFlags()
                .sendFaceToDirection(
                    length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);

            if (objectId == 884 && itemId == 5331) {
              int slot = stoner.getBox().getItemSlot(itemId);
              stoner.getBox().get(slot).setId(5340);
              stoner.send(new SendUpdateItemsAlt(3214, 5340, 1, slot));
              stoner.send(new SendMessage("You fill your Watering can."));
              return;
            }

            if (FountainOfRune.itemOnObject(stoner, objectId, itemId)) {
              return;
            }

			  if (Forging.SINGLETON.itemOnObject(stoner, new Item(itemId), objectId)) {
				  return;
			  }


            if (GodWars.useItemOnObject(stoner, itemId, objectId)) {
              return;
            }

            if (objectId == 3044 || objectId == 45310 || objectId == 2097) {
					Forging.SINGLETON.handleObjectClick(stoner, objectId);
					return;
            }

            if (objectId == 11744) {

              ItemDefinition def = GameDefinitionLoader.getItemDef(itemId);

              if (def == null) {
                return;
              }

              if (!def.isTradable()) {
                stoner.send(new SendMessage(def.getName() + " cannot be noted!"));
                return;
              }

              if (def.getNoteId() == -1) {
                stoner.send(
                    new SendMessage(
                        def.getName()
                            + " cannot be "
                            + (def.isNote() ? "un-noted" : "noted")
                            + "."));
                return;
              }

              int space = stoner.getBox().getFreeSlots();

              int amount = stoner.getBox().getItemAmount(def.getId());

              if (def.isNote()) {

                if (space == 0) {
                  stoner.send(new SendMessage("You have no free box spaces to do this!"));
                  return;
                }

                if (amount > space) {
                  amount = space;
                  stoner.send(new SendMessage("You can't un-note that many items!"));
                }
                stoner.getBox().remove(def.getId(), amount);
                stoner.getBox().add(def.getNoteId(), amount);
                stoner.send(
                    new SendMessage("You have un-noted " + amount + " of " + def.getName() + "."));
                return;
              }

              stoner.getBox().remove(def.getId(), amount);
              stoner.getBox().add(def.getNoteId(), amount);
              stoner.send(
                  new SendMessage("You have noted " + amount + " of " + def.getName() + "."));
              return;
            }

            if (objectId == 48802 && itemId == 954) {
              stoner.teleport(new Location(3484, 9510, 2));
              return;
            }

            if (objectId == 48803 && itemId == 954) {
              stoner.teleport(new Location(3507, 9494));
              return;
            }


            if (objectId == 4309) {
              if (Spinnable.forId(itemId) != null) {
				  Handiness.SINGLETON.handleObjectClick(stoner, 2644);
				  return;
              } else {
                stoner.getClient().queueOutgoingPacket(new SendMessage("You cant spin this!"));
              }

              return;
            }

            GameObject object = new GameObject(objectId, x, y, stoner.getLocation().getZ(), 10, 0);

            ArmourAnimator.armorOnAnimator(stoner, itemId, object, x, y);

            if ((DwarfMultiCannon.hasCannon(stoner))
                && (!DwarfMultiCannon.getCannon(stoner).construct(itemId)))
              DwarfMultiCannon.getCannon(stoner).load(stoner, itemId, objectId);
          }
        });
  }

  public static void useItemOnNpc(final Stoner stoner, final int item, int slot) {
    if (stoner.getMage().isTeleporting()) {
      return;
    }

    if ((slot > World.getNpcs().length) || (slot < 0)) {
      return;
    }

    final Mob mob = World.getNpcs()[slot];

    if (mob == null) {
      stoner.getMovementHandler().reset();
      return;
    }

    TaskQueue.queue(
        new FollowToEntityTask(stoner, mob) {
          @Override
          public void onDestination() {
            if (mob.face()) {
              mob.face(stoner);
            }

            stoner.face(mob);

            if (mob.getSize() > 1) {
              stoner.getMovementHandler().reset();
            }

            ObjectActions.finishItemOnNpc(stoner, item, mob);
          }
        });
  }
}
