package com.bestbudz.rs2.content;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.content.membership.CreditPurchase;
import com.bestbudz.rs2.content.minigames.clanwars.ClanWarsConstants;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGameConstants;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.impl.Zulrah;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.text.NumberFormat;
import java.util.HashMap;

public class TeleportHandler {

  public static void teleport(Stoner stoner) {
    int buttonId = stoner.getTeleportTo();

    TeleportationData teleportation = TeleportationData.teleportation.get(buttonId);

    if (teleportation == null) {
      return;
    }

    if (teleportation.getSpecialCase()) {
      if (!specialCase(stoner, teleportation.getButton())) {
        return;
      }
    }

    int height =
        teleportation.isInstanced() ? stoner.getIndex() << 2 : teleportation.getPosition().getZ();

    stoner
        .getMage()
        .teleport(
            teleportation.getPosition().getX(),
            teleportation.getPosition().getY(),
            height,
            TeleportTypes.SPELL_BOOK);

    stoner.setTeleportTo(0);
  }

  public static boolean specialCase(Stoner stoner, int buttonId) {
    switch (buttonId) {
      case 254033:
        stoner.send(new SendMessage("Walk west to enter the FFA arena."));
        return true;

      case 250053:
        TaskQueue.queue(
            new Task(
                stoner,
                4,
                false,
                Task.StackType.STACK,
                Task.BreakType.NEVER,
                TaskIdentifier.CURRENT_ACTION) {
              @Override
              public void execute() {
                new Mob(
                    stoner,
                    6342,
                    true,
                    false,
                    false,
                    new Location(3815, 2844, stoner.getIndex() << 2));
                stop();
              }

              @Override
              public void onStop() {}
            });
        return true;

      case 242107:
        stoner.start(
            new OptionDialogue(
                "Catweedy",
                p -> {
                  p.teleport(new Location(2805, 3464, 0));
                  p.send(
                      new SendMessage(
                          "<col=482CB8>You have teleported to the Catweedy bankstanding area."));
                },
                "Ardougne",
                p -> {
                  p.teleport(new Location(2662, 3375, 0));
                  p.send(
                      new SendMessage(
                          "<col=482CB8>You have teleported to the Ardougne bankstanding area."));
                },
                "Falador",
                p -> {
                  p.teleport(new Location(3056, 3310, 0));
                  p.send(
                      new SendMessage(
                          "<col=482CB8>You have teleported to the Falador bankstanding area."));
                },
                "Phasmatys",
                p -> {
                  p.teleport(new Location(3600, 3524, 0));
                  p.send(
                      new SendMessage(
                          "<col=482CB8>You have teleported to the Phasmatys bankstanding area."));
                }));
        return false;

      case 242102:
        stoner.start(
            new OptionDialogue(
                "Gnome exercisement course",
                p -> {
                  p.teleport(new Location(2480, 3437, 0));
                  p.send(
                      new SendMessage(
                          "<col=482CB8>You have teleported to the Gnome exercisement course."));
                },
                "Barbarian exercisement course",
                p -> {
                  p.teleport(new Location(2546, 3551, 0));
                  p.send(
                      new SendMessage(
                          "<col=482CB8>You have teleported to the Barbarian exercisement course."));
                },
                "Wilderness exercisement course (50+ wild)",
                p -> {
                  p.teleport(new Location(2998, 3915, 0));
                  p.send(
                      new SendMessage(
                          "<col=482CB8>You have teleported to the Wilderness exercisement course."));
                },
                "Rooftop courses",
                p -> {
                  stoner.start(
                      new OptionDialogue(
                          "Seer's course",
                          p2 -> {
                            p.teleport(new Location(2729, 3488, 0));
                            p.send(
                                new SendMessage(
                                    "<col=482CB8>You have teleported to the Seer's Village rooftop exercisement course."));
                          },
                          "Ardougne course",
                          p2 -> {
                            p.teleport(new Location(2674, 3297, 0));
                            p.send(
                                new SendMessage(
                                    "<col=482CB8>You have teleported to the Ardougne rooftop exercisement course."));
                          }));
                }));
        return false;

      case 250058:
        TaskQueue.queue(
            new Task(5) {
              @Override
              public void execute() {
                int[][] DATA = {
                  {493, 3691, 5810},
                  {493, 3691, 5814},
                  {493, 3700, 5810},
                  {493, 3700, 5814},
                  {496, 3694, 5811}
                };
                for (int i = 0; i < DATA.length; i++) {
                  Mob mob =
                      new Mob(
                          stoner,
                          DATA[i][0],
                          false,
                          false,
                          false,
                          new Location(DATA[i][1], DATA[i][2], stoner.getZ()));
                  mob.setCanAssault(false);
                  stoner.face(mob);
                }
                stop();
              }

              @Override
              public void onStop() {
                stoner.whirlpoolsHit = 0;
                stoner.send(new SendMessage("Welcome to Kraken's cave."));
              }
            });
        return true;

      case 250057:
        TaskQueue.queue(
            new Task(5) {
              @Override
              public void execute() {
                Zulrah mob = new Zulrah(stoner, new Location(2266, 3073, stoner.getIndex() << 2));
                mob.face(stoner);
                mob.getUpdateFlags().sendAnimation(new Animation(5071));
                stoner.face(mob);
                stoner.send(new SendMessage("Welcome to Zulrah's shrine."));
                stop();
              }

              @Override
              public void onStop() {}
            });
        return true;

      case 240111:
        return true;

      case 240112:
        return true;
    }
    return false;
  }

  public static boolean selection(Stoner stoner, int buttonId) {
    TeleportationData teleportation = TeleportationData.teleportation.get(buttonId);

    if (teleportation == null) {
      return false;
    }

    stoner.setTeleportTo(buttonId);
    stoner.send(new SendString("Grade: @red@0", 64039));
    switch (stoner.getInterfaceManager().getMain()) {
      case 61000:
        stoner.send(
            new SendString(
                "Selected: @red@"
                    + Utility.formatStonerName(
                        teleportation.name().replaceAll("_", " ").toLowerCase()),
                61031));
        stoner.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 61033));
        stoner.send(new SendString("Other: @red@" + teleportation.getOther(), 61034));
        break;
      case 62000:
        stoner.send(
            new SendString(
                "Selected: @red@"
                    + Utility.formatStonerName(
                        teleportation.name().replaceAll("_", " ").toLowerCase()),
                62031));
        stoner.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 62033));
        stoner.send(new SendString("Other: @red@" + teleportation.getOther(), 62034));
        break;
      case 63000:
        stoner.send(
            new SendString(
                "Selected: @red@"
                    + Utility.formatStonerName(
                        teleportation.name().replaceAll("_", " ").toLowerCase()),
                63031));
        stoner.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 63033));
        stoner.send(new SendString("Other: @red@" + teleportation.getOther(), 63034));
        break;
      case 64000:
        stoner.send(
            new SendString(
                "Selected: @red@"
                    + Utility.formatStonerName(
                        teleportation.name().replaceAll("_", " ").toLowerCase()),
                64031));
        stoner.send(new SendString("Cost: @red@" + format(teleportation.getCost()), 64032));
        stoner.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 64033));
        stoner.send(new SendString("Other: @red@" + teleportation.getOther(), 64034));
        stoner.send(new SendString("Grade: @red@" + teleportation.getGrade(), 64039));
        break;
      case 65000:
        stoner.send(
            new SendString(
                "Selected: @red@"
                    + Utility.formatStonerName(
                        teleportation.name().replaceAll("_", " ").toLowerCase()),
                65031));
        stoner.send(new SendString("Cost: @red@" + format(teleportation.getCost()), 65032));
        stoner.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 65033));
        stoner.send(new SendString("Other: @red@" + teleportation.getOther(), 65034));
        break;
      case 61500:
        stoner.send(
            new SendString(
                "Selected: @red@"
                    + Utility.formatStonerName(
                        teleportation.name().replaceAll("_", " ").toLowerCase()),
                61531));
        stoner.send(new SendString("Cost: @red@" + format(teleportation.getCost()), 61532));
        stoner.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 61533));
        stoner.send(new SendString("Other: @red@" + teleportation.getOther(), 61534));
        break;
    }
    return true;
  }

  public static String format(long num) {
    if (num == 0) {
      return "Free";
    }
    return NumberFormat.getInstance().format(num);
  }

  public enum TeleportationData {
    ROCK_CRABS(238123, 0, "None", "None", 0, new Location(3417, 2923, 0), false, false),
    HILL_GIANTS(238124, 0, "None", "None", 0, new Location(3117, 9856, 0), false, false),
    AL_KAHID(238125, 0, "None", "None", 0, new Location(3293, 3182, 0), false, false),
    COWS(238126, 0, "None", "None", 0, new Location(3362, 2889, 0), false, false),
    YAKS(238127, 0, "None", "None", 0, new Location(2321, 3804, 0), false, false),
    BRIMHAVEN_DUNG(238128, 1000, "None", "None", 0, new Location(2710, 9466, 0), false, false),
    TAVERLY_DUNG(238129, 1500, "None", "None", 0, new Location(2884, 9798, 0), false, false),
    MERCENARY_TOWER(238130, 2000, "None", "None", 0, new Location(3428, 3538, 0), false, false),
    LAVA_DRAGONS(238131, 2750, "40+ Wild!", "None", 0, new Location(3202, 3860, 0), false, false),
    MITHRIL_DRAGONS(238132, 3000, "None", "None", 0, new Location(1747, 5324, 0), false, false),

    WILD_RESOURCE(242099, 3000, "54 Wild!", "None", 0, new Location(3184, 3947, 0), false, false),
    ACCOMPLISHER(242100, 3000, "None", "None", 0, new Location(3047, 4976, 1), false, false),
    HANDINESS(242101, 2000, "None", "None", 0, new Location(2747, 3444, 0), false, false),
    QUARRYING(242103, 3000, "None", "None", 0, new Location(3044, 9785, 0), false, false),
    FORGING(242104, 2000, "None", "None", 0, new Location(3186, 3425, 0), false, false),
    FISHER(242105, 4000, "None", "None", 0, new Location(2840, 3437, 0), false, false),
    LUMBERING(242106, 3000, "None", "None", 0, new Location(2722, 3473, 0), false, false),
    BANKSTANDING(242107, 3000, "None", "None", 0, new Location(2806, 3463, 0), false, true),

    EDGEVILLE(246075, 0, "None", "None", 0, new Location(3087, 3515, 0), false, false),
    VARROCK(246076, 0, "None", "None", 0, new Location(3244, 3512, 0), false, false),
    EAST_DRAGONS(246077, 0, "None", "None", 0, new Location(3333, 3666, 0), false, false),
    CASTLE(246078, 0, "None", "None", 0, new Location(3002, 3626, 0), false, false),
    MAGE_BANK(246079, 0, "None", "None", 0, new Location(2540, 4717, 0), false, false),

    KING_BLACK_DRAGON(
        250051, 2500, "High combat", "40+ Wild", 276, new Location(2997, 3849, 0), false, false),
    SEA_TROLL_QUEEN(
        250052, 2500, "High combat", "Multi", 170, new Location(2336, 3692, 0), false, false),
    BARRELCHEST(
        250053, 4500, "High combat", "Instanced", 190, new Location(3806, 2844, 0), true, true),
    CORPOREAL_BEAST(
        250054, 6000, "High combat", "Team Based", 785, new Location(2948, 4385, 2), false, false),
    DAGGANNOTH_KINGS(
        250055, 6000, "High combat", "", 303, new Location(1909, 4367, 0), false, false),
    GOD_WARS(
        250056, 8000, "High combat", "Team Based", 0, new Location(2882, 5308, 2), false, false),
    ZULRAH(250057, 10000, "High combat", "Hard", 725, new Location(2268, 3070, 0), true, true),
    KRAKEN(250058, 7500, "High combat", "", 291, new Location(3696, 5807, 0), true, true),
    GIANT_MOLE(250059, 1500, "High combat", "", 230, new Location(1760, 5163, 0), false, false),
    CHAOS_ELEMENTAL(
        250060, 3500, "High combat", "50+ Wild", 305, new Location(3284, 3913, 0), false, false),
    CALLISTO(
        250061, 5750, "High combat", "40+ Wild", 470, new Location(3283, 3853, 0), false, false),
    SCORPIA(
        250062, 8750, "High combat", "50+ Wild", 225, new Location(3233, 3943, 0), false, false),
    VETION(250063, 8000, "High combat", "30+ Wild", 454, new Location(3210, 3780, 0), false, false),
    CHAOS_FANATIC(
        250065, 3750, "High combat", "40+ Wild", 202, new Location(2981, 3837, 0), false, false),
    CRAZY_ARCHAEOLOGIST(
        250066, 4500, "High combat", "20+ Wild", 204, new Location(2975, 3715, 0), false, false),

    BARROWS(254027, 0, "None", "None", 0, new Location(3565, 3315, 0), false, false),
    WARRIORS_GUILD(254028, 0, "None", "None", 0, new Location(2869, 3544, 0), false, false),
    DUEL_ARENA(254029, 0, "None", "None", 0, new Location(3365, 3265, 0), false, false),
    PEST_CONTROL(254030, 0, "None", "None", 0, new Location(2662, 2655, 0), false, false),
    FIGHT_CAVES(254031, 0, "None", "None", 0, new Location(2439, 5171, 0), false, false),
    WEAPON_GAME(254032, 0, "None", "None", 0, WeaponGameConstants.LOBBY_COODINATES, false, false),
    CLAN_WARS(254033, 0, "None", "None", 0, ClanWarsConstants.CLAN_WARS_ARENA, false, true),

    MEMBERSHIP(240111, 0, "Members only", "None", 0, StonerConstants.MEMEBER_AREA, false, true),
    STAFFZONE(240112, 0, "Staff only", "None", 0, StonerConstants.STAFF_AREA, false, true);

    public static HashMap<Integer, TeleportationData> teleportation =
        new HashMap<Integer, TeleportationData>();

    static {
      for (final TeleportationData teleportation : TeleportationData.values()) {
        TeleportationData.teleportation.put(teleportation.button, teleportation);
      }
    }

    private final int button;
    private final int cost;
    private final String requirement;
    private final String other;
    private final int grade;
    private final Location position;
    private final boolean instanced;
    private final boolean specialCase;

    TeleportationData(
        int button,
        int cost,
        String requirement,
        String other,
        int grade,
        Location position,
        boolean instanced,
        boolean specialCase) {
      this.button = button;
      this.cost = cost;
      this.requirement = requirement;
      this.other = other;
      this.grade = grade;
      this.position = position;
      this.instanced = instanced;
      this.specialCase = specialCase;
    }

    public int getButton() {
      return button;
    }

    public int getCost() {
      return cost;
    }

    public String getRequirement() {
      return requirement;
    }

    public String getOther() {
      return other;
    }

    public int getGrade() {
      return grade;
    }

    public Location getPosition() {
      return position;
    }

    public boolean isInstanced() {
      return instanced;
    }

    public boolean getSpecialCase() {
      return specialCase;
    }
  }
}
