package com.bestbudz.rs2.content.pets;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayList;

public class BossPets {

  public static boolean spawnPet(Stoner stoner, int itemID, boolean loot) {
    PetData data = PetData.forItem(itemID);

    if (data == null) {
      return false;
    }

    if (stoner.getActivePets().size() == 5) {
      stoner.send(new SendMessage("Lets not make everyone jealous!"));
      return true;
    }

    stoner.getBox().remove(new Item(itemID, 1));

    final Mob mob = new Mob(stoner, data.npcID, false, false, true, stoner.getLocation());
    mob.getFollowing().setIgnoreDistance(true);
    mob.getFollowing().setFollow(stoner);
    mob.setCanAssault(false);
    mob.setPet(true);
    mob.getGrades()[3] = 420000;
    mob.getMaxGrades()[3] = 420000;

    stoner.addPet(mob);

    stoner.setBossID(data.npcID);
    stoner.getUpdateFlags().sendAnimation(new Animation(827));
    stoner.face(mob);

    if (loot) {
      AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_1_BOSS_PET, 1);
      AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_10_BOSS_PET, 1);
    } else {
      stoner.send(
          new SendMessage("You took out " + mob.getDefinition().getName() + " for a walk."));
    }
    return true;
  }

  public static boolean pickupPet(Stoner stoner, Mob mob) {
    if (mob == null || World.getNpcs()[mob.getIndex()] == null) {
      return false;
    }
    PetData data = PetData.forNPC(mob.getId());

    if (data == null) {
      return false;
    }

    if (stoner.getActivePets().isEmpty()) {
      return false;
    }

    if (!stoner.getActivePets().contains(mob) || mob.getOwner() != stoner) {
      DialogueManager.sendStatement(stoner, mob.getDefinition().getName() + " is not your pet!");
      return true;
    }

    if (stoner.getBox().hasSpaceFor(new Item(data.getItem()))) {
      stoner.getBox().add(new Item(data.getItem()));
    } else if (stoner.getBank().hasSpaceFor((new Item(data.getItem())))) {
      stoner.getBank().add((new Item(data.getItem())));
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(mob.getDefinition().getName() + " has been added to your bank."));
    } else {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "You must free some box space to pick up your " + mob.getDefinition().getName()));
      return false;
    }

    stoner.getUpdateFlags().sendAnimation(new Animation(827));
    stoner.face(mob);

    TaskQueue.queue(
        new Task(stoner, 1, true) {
          @Override
          public void execute() {
            mob.remove();
            stoner.removePet(mob);

            stop();
          }

          @Override
          public void onStop() {
            stoner.send(
                new SendMessage("You have picked up your " + mob.getDefinition().getName()));
          }
        });

    return true;
  }

  public static void onLogout(Stoner stoner) {
    for (Mob pet : new ArrayList<>(stoner.getActivePets())) {
      PetData data = PetData.forNPC(pet.getId());
      if (data == null) continue;

      Item petItem = new Item(data.getItem());

      if (stoner.getBox().hasSpaceFor(petItem)) {
        stoner.getBox().add(petItem);
      } else if (stoner.getBank().hasSpaceFor(petItem)) {
        stoner.getBank().add(petItem);
        stoner.send(
            new SendMessage("Your " + pet.getDefinition().getName() + " was added to your bank."));
      } else {
        stoner.send(new SendMessage("You have no space to save your pet. It was dismissed."));
      }

      pet.remove();
      stoner.removePet(pet);
    }
  }

  public static void onDeath(Stoner stoner) {
    for (Mob pet : new ArrayList<>(stoner.getActivePets())) {
      pet.remove();
      stoner.removePet(pet);
      stoner.send(new SendMessage("You got yourself and your pet killed, irresponsible douch!"));
    }
  }

  public enum PetData {
    KALPHITE_PRINCESS_FLY(12654, 6637),
    KALPHITE_PRINCESS_BUG(12647, 6638),
    SMOKE_DEVIL(12648, 6655),
    DARK_CORE(12816, 318),
    PRINCE_BLACK_DRAGON(12653, 4000),
    GREEN_SNAKELING(12921, 2130),
    RED_SNAKELING(12939, 2131),
    BLUE_SNAKELING(12940, 2132),
    CHAOS_ELEMENT(11995, 5907),
    KREE_ARRA(12649, 4003),
    CALLISTO(13178, 497),
    SCORPIAS_OFFSPRING(13181, 5547),
    VENENATIS(13177, 495),
    VETION_PURPLE(13179, 5559),
    VETION_ORANGE(13180, 5560),
    BABY_MOLE(12646, 6635),
    KRAKEN(12655, 6640),
    DAGANNOTH_SUPRIME(12643, 4006),
    DAGANNOTH_RIME(12644, 4007),
    DAGANNOTH_REX(12645, 4008),
    GENERAL_GRAARDOR(12650, 4001),
    COMMANDER_ZILYANA(12651, 4009),
    KRIL_TSUTSAROTH(12652, 4004),
    IMP(9952, 5008),
    KEBBIT(9953, 1347),
    BUTTERFLY(9970, 1854),
    GIANT_EAGLE(9974, 5317),
    BLACK_CHINCHOMPA(11959, 2912),
    GNOME(3257, 4233),
    CHICKEN(5609, 6367),
    HELLHOUND(8137, 3133),
    BABY_DRAGON(8134, 137),
    DEMON(8138, 142),
    ROCNAR(8305, 143),
    FLAMBEED(8304, 4881),
    TENTACLE(8303, 5535),
    DEATH(5567, 12840);

    private final int itemID;
    private final int npcID;

    PetData(int itemID, int npcID) {
      this.itemID = itemID;
      this.npcID = npcID;
    }

    public static PetData forItem(int id) {
      for (PetData data : PetData.values()) if (data.itemID == id) return data;
      return null;
    }

    public static PetData forNPC(int id) {
      for (PetData data : PetData.values()) if (data.npcID == id) return data;
      return null;
    }

    public int getItem() {
      return itemID;
    }

    public int getNPC() {
      return npcID;
    }
  }
}
