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

public class Fisher {

  private static final Fishable[] COMMON_DROPS = {
    Fishable.SHRIMP, Fishable.ANCHOVIES,
    Fishable.MACKEREL, Fishable.COD,
    Fishable.BASS, Fishable.TROUT,
    Fishable.SALMON, Fishable.PIKE,
    Fishable.SARDINE, Fishable.HERRING,
    Fishable.MONK_FISH, Fishable.LOBSTER,
    Fishable.TUNA, Fishable.SWORD_FISH,
    Fishable.SHARK, Fishable.DARK_CRAB,
    Fishable.MANTA_RAY, Fishable.CAVE_EEL,
    Fishable.SLIMY_EEL, Fishable.KARAMBWAN,
    Fishable.KARAMBWANJI, Fishable.LAVA_EEL
  };

  private static final Fishable[] PROFESSION_DROPS = {
    Fishable.FLAX, Fishable.FISH_BONES,
    Fishable.DRAGON_ARROWS
  };

  private static final Fishable[] RARE_DROPS = {
    Fishable.RARE_ARMADYL_GODSWORD,
    Fishable.RARE_BANDOS_GODSWORD,
    Fishable.RARE_SARADOMIN_GODSWORD,
    Fishable.RARE_ZAMORAK_GODSWORD,
    Fishable.RARE_GODSWORD_BLADE,
    Fishable.RARE_GODSWORD_SHARD_1,
    Fishable.RARE_GODSWORD_SHARD_2,
    Fishable.RARE_ARMADYL_HILT,
    Fishable.RARE_ARMADYL_PLATEBODY,
    Fishable.RARE_ARMADYL_PLATELEGS,
    Fishable.RARE_ARMADYL_PLATESKIRT,
    Fishable.RARE_ARMADYL_FULL_HELM,
    Fishable.RARE_ARMADYL_KITESHIELD,
    Fishable.RARE_ARMADYL_BRACERS,
    Fishable.RARE_ARMADYL_DHIDE,
    Fishable.RARE_ARMADYL_CHAPS,
    Fishable.RARE_ARMADYL_COIF,
    Fishable.RARE_ARMADYL_ROBE_LEGS,
    Fishable.RARE_ARMADYL_STOLE,
    Fishable.RARE_ARMADYL_MITRE,
    Fishable.RARE_ARMADYL_CLOAK,
    Fishable.RARE_BANDOS_PLATEBODY,
    Fishable.RARE_BANDOS_PLATELEGS,
    Fishable.RARE_BANDOS_PLATESKIRT,
    Fishable.RARE_BANDOS_FULL_HELM,
    Fishable.RARE_BANDOS_DHIDE,
    Fishable.RARE_BANDOS_CHAPS,
    Fishable.RARE_BANDOS_COIF,
    Fishable.RARE_BANDOS_ROBE_LEGS,
    Fishable.RARE_BANDOS_STOLE,
    Fishable.RARE_BANDOS_MITRE,
    Fishable.RARE_BANDOS_CLOAK,
    Fishable.RARE_KALPHITE_PRINCESS_FLY,
    Fishable.RARE_KALPHITE_PRINCESS_BUG,
    Fishable.RARE_SMOKE_DEVIL,
    Fishable.RARE_DARK_CORE,
    Fishable.RARE_PRINCE_BLACK_DRAGON,
    Fishable.RARE_GREEN_SNAKELING,
    Fishable.RARE_RED_SNAKELING,
    Fishable.RARE_BLUE_SNAKELING,
    Fishable.RARE_CHAOS_ELEMENT,
    Fishable.RARE_KREE_ARRA,
    Fishable.RARE_CALLISTO,
    Fishable.RARE_SCORPIAS_OFFSPRING,
    Fishable.RARE_VENENATIS,
    Fishable.RARE_VETION_PURPLE,
    Fishable.RARE_VETION_ORANGE,
    Fishable.RARE_BABY_MOLE,
    Fishable.RARE_KRAKEN,
    Fishable.RARE_DAGANNOTH_SUPREME,
    Fishable.RARE_DAGANNOTH_PRIME,
    Fishable.RARE_DAGANNOTH_REX,
    Fishable.RARE_GENERAL_GRAARDOR,
    Fishable.RARE_COMMANDER_ZILYANA,
    Fishable.RARE_KRIL_TSUTSAROTH,
    Fishable.RARE_BANDOS_HILT,
    Fishable.RARE_ZAMORAK_HILT,
    Fishable.RARE_SARADOMIN_HILT,
    Fishable.RARE_DRACONIC_VISAGE,
    Fishable.RARE_IMP,
    Fishable.RARE_KEBBIT,
    Fishable.RARE_BUTTERFLY,
    Fishable.RARE_GIANT_EAGLE,
    Fishable.RARE_BLACK_CHINCHOMPA,
    Fishable.RARE_GNOME,
    Fishable.RARE_CHICKEN,
    Fishable.RARE_HELLHOUND,
    Fishable.RARE_BABY_DRAGON,
    Fishable.RARE_DEMON,
    Fishable.RARE_ROCNAR,
    Fishable.RARE_FLAMBEED,
    Fishable.RARE_TENTACLE,
    Fishable.RARE_DEATH,
  };
  private final Stoner stoner;
  private Fishable[] fisher = null;

  public Fisher(Stoner stoner) {
    this.stoner = stoner;
  }

  public static Fishable[] combined() {
    return combine(COMMON_DROPS, PROFESSION_DROPS, RARE_DROPS);
  }

  private static Fishable[] combine(Fishable[]... arrays) {
    int total = 0;
    for (Fishable[] arr : arrays) total += arr.length;

    Fishable[] result = new Fishable[total];
    int index = 0;
    for (Fishable[] arr : arrays) {
      System.arraycopy(arr, 0, result, index, arr.length);
      index += arr.length;
    }
    return result;
  }

  public static boolean canFish(Stoner p, Fishable fish, boolean message) {

    return true;
  }

  public boolean clickNpc(Mob mob, int id, int option) {
    if (FisherSpots.forId(id) == null) {
      return false;
    }

    FisherSpots spot = FisherSpots.forId(id);

    Fishable[] fish = spot.option_1;
    Fishable[] valid = new Fishable[fish.length];
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

    Fishable[] fisher = new Fishable[amount];
    System.arraycopy(valid, 0, fisher, 0, amount);

    start(mob, fisher, 0);
    return true;
  }

  public boolean fish() {
    if (fisher == null) {
      return false;
    }

    Fishable[] valid = new Fishable[fisher.length];
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

    Fishable[] fish = new Fishable[count];
    System.arraycopy(valid, 0, fish, 0, count);

    Fishable f = fish[Utility.randomNumber(count)];

    if (stoner.getBox().getFreeSlots() == 0) {
      DialogueManager.sendStatement(
          stoner, "U can start a fisher stall with all these fish, now get.");
      return false;
    }

    double roll = Math.random();
    if (success(f) && roll <= f.getDropRate()) {

      stoner.getClient().queueOutgoingPacket(new SendSound(378, 0, 0));

      int id = f.getRawFishId();
      String name = Item.getDefinition(id).getName();

      int amount = determineRollAmount();
      stoner.getBox().add(new Item(id, amount));

      stoner.getProfession().addExperience(10, (amount * f.getExperience()));
      double rarestDropRate = 1.0D;
      for (Fishable candidate : fisher) {
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

    if (roll <= 0.000001) return 1000;

    if (roll <= 0.00005) return 100;

    if (roll <= 0.0001) return 20;

    if (roll <= 0.0005) return 10;

    if (roll <= 0.001) return 5;

    if (roll <= 0.005) return 4;

    if (roll <= 0.01) return 3;

    if (roll <= 0.05) return 2;

    return 1;
  }

  public void reset() {
    fisher = null;
  }

  public void start(final Mob mob, Fishable[] fisher, int option) {
    if ((fisher == null) || (fisher[option] == null)) {
      return;
    }

    this.fisher = fisher;

    stoner.getClient().queueOutgoingPacket(new SendSound(289, 0, 0));

    stoner.getUpdateFlags().sendAnimation(1768, 0);

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
            stoner.getUpdateFlags().sendAnimation(1768, 0);

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

  public boolean success(Fishable fish) {
    return Professions.isSuccess(stoner, 10, fish.getRequiredGrade());
  }

}
