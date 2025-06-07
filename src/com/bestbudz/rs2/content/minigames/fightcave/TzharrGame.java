package com.bestbudz.rs2.content.minigames.fightcave;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.minigames.fightcave.TzharrData.NPCS_DETAILS;
import com.bestbudz.rs2.content.pets.BossPets;
import com.bestbudz.rs2.content.pets.BossPets.PetData;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import java.util.ArrayList;
import java.util.Collections;

public final class TzharrGame {

  public static final TzharrController CONTROLLER = new TzharrController();

  public static final Location LEAVE = new Location(2438, 5168, 0);
  public static final String FIGHT_CAVE_NPCS_KEY = "fightcavesnpcs";
  public static final Location[] SPAWN_LOCATIONS = {
    new Location(2411, 5109),
    new Location(2413, 5105),
    new Location(2385, 5106),
    new Location(2380, 5102),
    new Location(2380, 5073),
    new Location(2387, 5071),
    new Location(2420, 5082),
    new Location(2416, 5107),
    new Location(2412, 5111),
    new Location(2382, 5108),
    new Location(2378, 5103)
  };

  public static void checkForFightCave(Stoner p, Mob mob) {
    if (p.getController().equals(CONTROLLER)) {

      p.getJadDetails().removeNpc(mob);

      if (mob.getId() == NPCS_DETAILS.TZ_KEK) {

        short[] ids = new short[] {NPCS_DETAILS.TZ_KEK_SPAWN, NPCS_DETAILS.TZ_KEK_SPAWN};
        for (short i : ids) {
          Mob m = new Mob(p, i, false, false, false, mob.getLocation());
          m.getFollowing().setIgnoreDistance(true);
          m.getCombat().setAssault(p);
          p.getJadDetails().addNpc(m);
        }
      }

      if (p.getJadDetails().getKillAmount() == 0) {
        if (p.getJadDetails().getStage() == 14) {
          finish(p, true);
          return;
        }
        p.getJadDetails().increaseStage();
        startNextWave(p);
      }
    }
  }

  public static void finish(Stoner stoner, boolean reward) {
    if (reward) {
      stoner.getBox().addOrCreateGroundItem(6570, 1, true);
      stoner.getBox().addOrCreateGroundItem(6529, 16064, true);
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("Congratulations, you have completed The Fight Caves"));
      DialogueManager.sendStatement(stoner, "Congratulations, you have completed The Fight Caves");
      World.sendGlobalMessage(
          "<img=8> <col=C42BAD>"
              + stoner.getUsername()
              + " has just completed the Fight Caves minigame!");
      AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_10_FIRECAPES, 1);
      AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_50_FIRECAPES, 1);
      if (Utility.random(150) == 0) {
        handlePet(stoner);
      }
    } else {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You did not make it far enough to receive a reward."));
    }

    stoner.teleport(LEAVE);
    onLeaveGame(stoner);
    stoner.getJadDetails().reset();
  }

  public static void handlePet(Stoner stoner) {
    PetData petDrop = PetData.forItem(4000);

    if (petDrop != null) {
      if (stoner.getActivePets().size() < 5) {
        BossPets.spawnPet(stoner, petDrop.getItem(), true);
        stoner.send(
            new SendMessage(
                "You feel a presence following you; "
                    + Utility.formatStonerName(
                        GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName())
                    + " starts to follow you."));
      } else {
        stoner.getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
        stoner.send(new SendMessage("You feel a presence added to your bank."));
      }
    }
  }

  public static void init(Stoner p, boolean kiln) {
    p.send(new SendRemoveInterfaces());
    p.setController(CONTROLLER);

    if (p.getJadDetails().getZ() == 0) {
      p.getJadDetails().setZ(p);
    }

    p.teleport(new Location(2413, 5117, p.getJadDetails().getZ()));
    startNextWave(p);
  }

  public static void loadGame(Stoner stoner) {
    stoner.setController(CONTROLLER);

    if (stoner.getJadDetails().getStage() != 0) startNextWave(stoner);
  }

  public static void onLeaveGame(Stoner stoner) {
    for (Mob i : stoner.getJadDetails().getMobs()) {
      if (i != null) {
        i.remove();
      }
    }

    stoner.getJadDetails().getMobs().clear();

    stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
  }

  public static void startNextWave(final Stoner p) {
    p.getClient()
        .queueOutgoingPacket(new SendMessage("The next wave will start in a few seconds."));
    if (p.getJadDetails().getZ() == 0) {
      p.getJadDetails().setZ(p);
      p.changeZ(p.getJadDetails().getZ());
    }
    TaskQueue.queue(
        new Task(p, 20, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.TZHAAR) {
          @Override
          public void execute() {
            final ArrayList<Location> randomizedSpawns = new ArrayList<Location>();

            Collections.addAll(randomizedSpawns, SPAWN_LOCATIONS);

            int c;
            for (short i : TzharrData.values()[p.getJadDetails().getStage()].getNpcs()) {
              c = Utility.randomNumber(randomizedSpawns.size());
              Location l = new Location(randomizedSpawns.get(c));
              randomizedSpawns.remove(c);
              l.setZ(p.getJadDetails().getZ());
              Mob mob = new Mob(p, i, false, false, false, l);
              mob.getFollowing().setIgnoreDistance(true);
              mob.getCombat().setAssault(p);
              p.getJadDetails().addNpc(mob);
            }
            p.getClient()
                .queueOutgoingPacket(
                    new SendMessage("Wave: " + (p.getJadDetails().getStage() + 1)));
            stop();
          }

          @Override
          public void onStop() {}
        });
  }
}
