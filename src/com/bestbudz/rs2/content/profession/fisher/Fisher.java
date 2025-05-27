package com.bestbudz.rs2.content.profession.fisher;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;
import java.util.HashMap;
import java.util.Map;

public class Fisher {

  private static final FishableData.Fishable[] COMMON_DROPS = {
    FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES,
    FishableData.Fishable.MACKEREL, FishableData.Fishable.COD,
    FishableData.Fishable.BASS, FishableData.Fishable.TROUT,
    FishableData.Fishable.SALMON, FishableData.Fishable.PIKE,
    FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING,
    FishableData.Fishable.MONK_FISH, FishableData.Fishable.LOBSTER,
    FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH,
    FishableData.Fishable.SHARK, FishableData.Fishable.DARK_CRAB,
    FishableData.Fishable.MANTA_RAY, FishableData.Fishable.CAVE_EEL,
    FishableData.Fishable.SLIMY_EEL, FishableData.Fishable.KARAMBWAN,
    FishableData.Fishable.KARAMBWANJI, FishableData.Fishable.LAVA_EEL
  };

  private static final FishableData.Fishable[] PROFESSION_DROPS = {
    FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES,
    FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.EXPLOSIVE
  };

  private static final FishableData.Fishable[] RARE_DROPS = {
    FishableData.Fishable.RARE_ARMADYL_GODSWORD,
    FishableData.Fishable.RARE_BANDOS_GODSWORD,
    FishableData.Fishable.RARE_SARADOMIN_GODSWORD,
    FishableData.Fishable.RARE_ZAMORAK_GODSWORD,
    FishableData.Fishable.RARE_GODSWORD_BLADE,
    FishableData.Fishable.RARE_GODSWORD_SHARD_1,
    FishableData.Fishable.RARE_GODSWORD_SHARD_2,
    FishableData.Fishable.RARE_ARMADYL_HILT,
    FishableData.Fishable.RARE_ARMADYL_PLATEBODY,
    FishableData.Fishable.RARE_ARMADYL_PLATELEGS,
    FishableData.Fishable.RARE_ARMADYL_PLATESKIRT,
    FishableData.Fishable.RARE_ARMADYL_FULL_HELM,
    FishableData.Fishable.RARE_ARMADYL_KITESHIELD,
    FishableData.Fishable.RARE_ARMADYL_BRACERS,
    FishableData.Fishable.RARE_ARMADYL_DHIDE,
    FishableData.Fishable.RARE_ARMADYL_CHAPS,
    FishableData.Fishable.RARE_ARMADYL_COIF,
    FishableData.Fishable.RARE_ARMADYL_ROBE_LEGS,
    FishableData.Fishable.RARE_ARMADYL_STOLE,
    FishableData.Fishable.RARE_ARMADYL_MITRE,
    FishableData.Fishable.RARE_ARMADYL_CLOAK,
    FishableData.Fishable.RARE_BANDOS_PLATEBODY,
    FishableData.Fishable.RARE_BANDOS_PLATELEGS,
    FishableData.Fishable.RARE_BANDOS_PLATESKIRT,
    FishableData.Fishable.RARE_BANDOS_FULL_HELM,
    FishableData.Fishable.RARE_BANDOS_DHIDE,
    FishableData.Fishable.RARE_BANDOS_CHAPS,
    FishableData.Fishable.RARE_BANDOS_COIF,
    FishableData.Fishable.RARE_BANDOS_ROBE_LEGS,
    FishableData.Fishable.RARE_BANDOS_STOLE,
    FishableData.Fishable.RARE_BANDOS_MITRE,
    FishableData.Fishable.RARE_BANDOS_CLOAK,
    FishableData.Fishable.RARE_KALPHITE_PRINCESS_FLY,
    FishableData.Fishable.RARE_KALPHITE_PRINCESS_BUG,
    FishableData.Fishable.RARE_SMOKE_DEVIL,
    FishableData.Fishable.RARE_DARK_CORE,
    FishableData.Fishable.RARE_PRINCE_BLACK_DRAGON,
    FishableData.Fishable.RARE_GREEN_SNAKELING,
    FishableData.Fishable.RARE_RED_SNAKELING,
    FishableData.Fishable.RARE_BLUE_SNAKELING,
    FishableData.Fishable.RARE_CHAOS_ELEMENT,
    FishableData.Fishable.RARE_KREE_ARRA,
    FishableData.Fishable.RARE_CALLISTO,
    FishableData.Fishable.RARE_SCORPIAS_OFFSPRING,
    FishableData.Fishable.RARE_VENENATIS,
    FishableData.Fishable.RARE_VETION_PURPLE,
    FishableData.Fishable.RARE_VETION_ORANGE,
    FishableData.Fishable.RARE_BABY_MOLE,
    FishableData.Fishable.RARE_KRAKEN,
    FishableData.Fishable.RARE_DAGANNOTH_SUPREME,
    FishableData.Fishable.RARE_DAGANNOTH_PRIME,
    FishableData.Fishable.RARE_DAGANNOTH_REX,
    FishableData.Fishable.RARE_GENERAL_GRAARDOR,
    FishableData.Fishable.RARE_COMMANDER_ZILYANA,
    FishableData.Fishable.RARE_KRIL_TSUTSAROTH,
    FishableData.Fishable.RARE_BANDOS_HILT,
    FishableData.Fishable.RARE_ZAMORAK_HILT,
    FishableData.Fishable.RARE_SARADOMIN_HILT,
    FishableData.Fishable.RARE_DRACONIC_VISAGE,
    FishableData.Fishable.RARE_IMP,
    FishableData.Fishable.RARE_KEBBIT,
    FishableData.Fishable.RARE_BUTTERFLY,
    FishableData.Fishable.RARE_GIANT_EAGLE,
    FishableData.Fishable.RARE_BLACK_CHINCHOMPA,
    FishableData.Fishable.RARE_GNOME,
    FishableData.Fishable.RARE_CHICKEN,
    FishableData.Fishable.RARE_HELLHOUND,
    FishableData.Fishable.RARE_BABY_DRAGON,
    FishableData.Fishable.RARE_DEMON,
    FishableData.Fishable.RARE_ROCNAR,
    FishableData.Fishable.RARE_FLAMBEED,
    FishableData.Fishable.RARE_TENTACLE,
    FishableData.Fishable.RARE_DEATH,
  };
  private final Stoner stoner;
  private FishableData.Fishable[] fisher = null;
  private ToolData.Tools tool = null;

  public Fisher(Stoner stoner) {
    this.stoner = stoner;
  }

  private static FishableData.Fishable[] combined() {
    return combine(COMMON_DROPS, PROFESSION_DROPS, RARE_DROPS);
  }

  private static FishableData.Fishable[] combine(FishableData.Fishable[]... arrays) {
    int total = 0;
    for (FishableData.Fishable[] arr : arrays) total += arr.length;

    FishableData.Fishable[] result = new FishableData.Fishable[total];
    int index = 0;
    for (FishableData.Fishable[] arr : arrays) {
      System.arraycopy(arr, 0, result, index, arr.length);
      index += arr.length;
    }
    return result;
  }

  public static boolean canFish(Stoner p, FishableData.Fishable fish, boolean message) {

    return true;
  }

  public static boolean hasFisherItems(Stoner stoner, FishableData.Fishable fish, boolean message) {
    int tool = fish.getToolId();
    int bait = fish.getBaitRequired();

    if (tool == 6577) {
      if (!stoner.getBox().hasItemAmount(new Item(tool, 1))) {

        Item necklace = stoner.getEquipment().getItems()[2];
        if ((necklace != null) && (necklace.getId() == 6577)) {
          return true;
        }
      }
      if (message) {
        DialogueManager.sendItem1(
            stoner, "You must be wearing a fisher necklace to splash at the fishes!", 6577);
      }
      return false;

    } else if ((!stoner.getBox().hasItemAmount(new Item(tool, 1))) && (message)) {
      String name = Item.getDefinition(tool).getName();
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "BUG WHILE CHECKING FOR TOOL "
                      + Utility.getAOrAn(name)
                      + " "
                      + name
                      + ", PLEASE SHARE SCREENSHOT."));
      return false;
    }

    if ((bait > -1) && (!stoner.getBox().hasItemAmount(new Item(bait, 1)))) {
      String name = Item.getDefinition(bait).getName();
      if (message) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "BUG WHILE CHECKING FOR BAIT "
                        + Utility.getAOrAn(name)
                        + " "
                        + name
                        + ", PLEASE SHARE SCREENSHOT."));
      }
      return false;
    }

    return true;
  }

  public boolean clickNpc(Mob mob, int id, int option) {
    if (FisherSpots.forId(id) == null) {
      return false;
    }

    FisherSpots spot = FisherSpots.forId(id);

    FishableData.Fishable[] fish = spot.option_1;
    FishableData.Fishable[] valid = new FishableData.Fishable[fish.length];
    int amount = 0;

    switch (option) {
      case 1:
        for (int i = 0; i < fish.length; i++) {
          if (canFish(stoner, fish[i], i == 0)) {
            valid[amount] = fish[i];
            amount++;
          }
        }
        break;
      default:
        return false;
    }

    if (amount == 0) {
      return true;
    }

    FishableData.Fishable[] fisher = new FishableData.Fishable[amount];
    System.arraycopy(valid, 0, fisher, 0, amount);

    start(mob, fisher, 0);
    return true;
  }

  public boolean fish() {
    if (fisher == null) {
      return false;
    }

    FishableData.Fishable[] valid = new FishableData.Fishable[fisher.length];
    int count = 0;

    for (int i = 0; i < fisher.length; i++) {
      if (canFish(stoner, fisher[i], false)) {
        valid[count] = fisher[i];
        count++;
      }
    }

    if (count == 0) {
      return false;
    }

    FishableData.Fishable[] fish = new FishableData.Fishable[count];
    System.arraycopy(valid, 0, fish, 0, count);

    FishableData.Fishable f = fish[Utility.randomNumber(count)];

    if (stoner.getBox().getFreeSlots() == 0) {
      DialogueManager.sendStatement(
          stoner, "U can start a fisher stall with all these fish, now get.");
      return false;
    }

    double roll = Math.random();
    if (success(f) && roll <= f.getDropRate()) {
      if (f.getBaitRequired() != -1) {
        stoner.getBox().remove(new Item(f.getBaitRequired(), 0));
      }

      stoner.getClient().queueOutgoingPacket(new SendSound(378, 0, 0));

      int id = f.getRawFishId();
      String name = Item.getDefinition(id).getName();

      int amount = determineRollAmount();
      stoner.getBox().add(new Item(id, amount));

      stoner.getProfession().addExperience(10, (amount * f.getExperience()));
      double rarestDropRate = 1.0D;
      for (FishableData.Fishable candidate : fisher) {
        if (candidate.getDropRate() < rarestDropRate) {
          rarestDropRate = candidate.getDropRate();
        }
      }

      System.out.println("Rolled: " + roll + " (Need ≤ " + f.getDropRate() + ")");
      String percentRoll = String.format("%.4f", roll * 100.0D);

      stoner.getClient().queueOutgoingPacket(new SendMessage("Rolled " + percentRoll + "%"));

      if (amount > 1000) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "HOLY FUCK BALLS, You hit 0.000001 droprate! x" + amount + " " + name + "."));
      } else if (amount > 100) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage("You are insanely lucky! x" + amount + " " + name + "."));
      } else if (amount > 20) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You just rolled the pet drop table! but got x"
                        + amount
                        + " "
                        + name
                        + " instead."));
      } else if (amount > 1) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(getFishStringMod(name) + "lucky! x" + amount + " " + name + "."));
      } else {
        stoner
            .getClient()
            .queueOutgoingPacket(new SendMessage(getFishStringMod(name) + name + "."));
      }
    }

    stoner.getProfession().lock(4);
    return true;
  }

  public String getFishStringMod(String name) {
    return "You splashed the water and got ";
  }

  private int determineRollAmount() {
    double roll = Math.random();

    // 0.0001% chance (1 in 1,000,000)
    if (roll <= 0.000001) return 1000;

    // 0.005% chance (1 in 20,000)
    if (roll <= 0.00005) return 100;

    // 0.01% chance (1 in 10,000)
    if (roll <= 0.0001) return 20;

    // 0.05% chance (1 in 2,000)
    if (roll <= 0.0005) return 10;

    // 0.1% chance (1 in 1,000)
    if (roll <= 0.001) return 5;

    // 0.5% chance (1 in 200)
    if (roll <= 0.005) return 4;

    // 1% chance (1 in 100)
    if (roll <= 0.01) return 3;

    // 5% chance (1 in 20)
    if (roll <= 0.05) return 2;

    // 95% fallback
    return 1;
  }

  public enum FisherSpots {
    SMALL_NET_OR_BAIT(1518, combined()),
    LURE_OR_BAIT(1526, combined()),
    CAGE_OR_HARPOON(1519, combined()),
    LARGE_NET_OR_HARPOON(1520, combined()),
    HARPOON_OR_SMALL_NET(1534, combined()),
    MANTA_RAY(3019, combined()),
    DARK_CRAB(1536, combined());

    private static final Map<Integer, FisherSpots> fisherSpots =
        new HashMap<Integer, FisherSpots>();
    private final int id;
    private final FishableData.Fishable[] option_1;

    FisherSpots(int id, FishableData.Fishable[] option_1) {
      this.id = id;
      this.option_1 = option_1;
    }

    public static final void declare() {
      for (FisherSpots spots : values()) fisherSpots.put(Integer.valueOf(spots.getId()), spots);
    }

    public static FisherSpots forId(int id) {
      return fisherSpots.get(Integer.valueOf(id));
    }

    public int getId() {
      return id;
    }

    public FishableData.Fishable[] getOption_1() {
      return option_1;
    }
  }

  public void reset() {
    fisher = null;
    tool = null;
  }

  public void start(final Mob mob, FishableData.Fishable[] fisher, int option) {
    if ((fisher == null) || (fisher[option] == null) || (fisher[option].getToolId() == -1)) {
      return;
    }

    this.fisher = fisher;

    tool = ToolData.Tools.forId(fisher[option].getToolId());

    if (!hasFisherItems(stoner, fisher[option], true)) {
      return;
    }

    stoner.getClient().queueOutgoingPacket(new SendSound(289, 0, 0));

    stoner.getUpdateFlags().sendAnimation(tool.getAnimationId(), 0);

    Task profession =
        new Task(
            stoner,
            4,
            false,
            Task.StackType.NEVER_STACK,
            Task.BreakType.ON_MOVE,
            TaskIdentifier.FISHER) {
          @Override
          public void execute() {
            stoner.face(mob);
            stoner.getUpdateFlags().sendAnimation(tool.getAnimationId(), 0);

            if (!fish()) {
              stop();
              reset();
            }
          }

          @Override
          public void onStop() {}
        };
    TaskQueue.queue(profession);
  }

  public boolean success(FishableData.Fishable fish) {
    return Professions.isSuccess(stoner, 10, fish.getRequiredGrade());
  }
}
