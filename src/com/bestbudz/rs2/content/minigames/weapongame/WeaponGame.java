package com.bestbudz.rs2.content.minigames.weapongame;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEquipment;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerHint;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class WeaponGame {

  private static final List<GameObject> crates = new ArrayList<>();
  public static Queue<Stoner> lobbyStoners = new ArrayDeque<Stoner>();
  public static Queue<Stoner> gameStoners = new ArrayDeque<Stoner>();
  public static Stoner leader;

  private static boolean started = false;

  public static void joinLobby(Stoner stoner) {

    if (stoner.getProfession().getCombatGrade() < WeaponGameConstants.COMBAT_REQUIRED) {
      DialogueManager.sendStatement(
          stoner, "Combat grade of " + WeaponGameConstants.COMBAT_REQUIRED + " is required!");
      return;
    }
    if (stoner.getBox().getFreeSlots() != 28) {
      DialogueManager.sendStatement(stoner, "You can't bring any items into the game!");
      return;
    }
    if (stoner.getEquipment().getEquipmentCount() != 0) {
      DialogueManager.sendStatement(stoner, "You can't be wearing any items!");
      return;
    }
    if (!stoner.getActivePets().isEmpty()) {
      DialogueManager.sendStatement(stoner, "You can't bring a pet into this game!");
      return;
    }
    if (!stoner.getController().equals(ControllerManager.WEAPON_LOBBY_CONTROLLER)) {
      stoner.teleport(new Location(stoner.getX(), stoner.getY() - 2, stoner.getZ()));
      stoner.setController(ControllerManager.WEAPON_LOBBY_CONTROLLER);
      if (!lobbyStoners.contains(stoner)) {
        lobbyStoners.add(stoner);
      }
      stoner.send(
          new SendMessage(
              "@dre@There are currently " + lobbyStoners.size() + " stoners in the lobby."));
    }
  }

  public static void leaveLobby(Stoner stoner, boolean barrier) {
    if (lobbyStoners.contains(stoner)) {
      lobbyStoners.remove(stoner);
      stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
    }
    if (barrier) {
      stoner.send(new SendMessage("@dre@You have left the Weapon Game Lobby."));
      stoner.teleport(new Location(stoner.getX(), stoner.getY() + 2, stoner.getZ()));
    }
  }

  public static void startGame() {
    if (lobbyStoners.size() < WeaponGameConstants.MINIMUM_STONERS) {
      messageStoners("@dre@There were not enough stoners to start the game!", true);
      return;
    }
    if (started) {
      messageStoners("@dre@There is currently a game in session.", true);
      return;
    }
    World.sendGlobalMessage("[ <col=0079AD>Weapon Game</col> ] Game has just begun!");
    WeaponGameConstants.METEOR_TIME = 120;
    WeaponGameConstants.CRATE_TIME = 90;
    WeaponGameConstants.GAME_TIME = 900;
    for (Stoner stoners : lobbyStoners) {
      stoners.getProfession().resetCombatStats();
      stoners.getNecromance().disable();
      stoners.setWeaponKills(0);
      stoners.teleport(Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS));
      DialogueManager.sendInformationBox(
          stoners,
          "@dre@Weapon Game",
          "@dre@Objective: @bla@Be the first to reach 10 kills",
          "@dre@Note: @bla@Each kill will upgrade your weapon",
          "@dre@Hint: @bla@Click on scattered crates for supplies ",
          "Good luck!");
      stoners.send(new SendStonerOption("Assault", 3));
      lobbyStoners.remove(stoners);
      gameStoners.add(stoners);
      started = true;
      stoners.setController(ControllerManager.WEAPON_GAME_CONTROLLER);
    }
  }

  public static void meteors() {
    messageStoners("@dre@Beware meteors are inbound!", false);
    for (int i = 0; i < Utility.random(10); i++) {
      Location location = Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS);
      World.sendStillGraphic(659, 100, location);
      for (Stoner stoners : gameStoners) {
        TaskQueue.queue(
            new Task(stoners, 3, false) {
              @Override
              public void execute() {
                stop();
              }

              @Override
              public void onStop() {
                if (stoners.getLocation() == location) {
                  stoners.hit(new Hit(Utility.random(35)));
                  stoners.send(new SendMessage("@dre@A meteor came crashing down on your head!"));
                }
              }
            });
      }
    }
    WeaponGameConstants.METEOR_TIME = 120;
  }

  public static void spawnCrates() {
    messageStoners("@dre@A crate has spawned! Find it for some gear and supplies!", false);
    for (int index = 0; index < 5; index++) {
      Location location = Utility.randomElement(WeaponGameConstants.CRATE_LOCATIONS);
      GameObject object = new GameObject(2072, location, 10, 0);
      ObjectManager.register(object);
      crates.add(object);
      World.sendStillGraphic(776, 10, location);
    }
    WeaponGameConstants.CRATE_TIME = 60;
  }

  public static void handleCrate(Stoner stoner, int x, int y, int z) {
    if (gameStoners.contains(stoner)) {
      stoner.send(new SendMessage("You start searching the crate..."));
      stoner.getUpdateFlags().sendAnimation(new Animation(832));
      TaskQueue.queue(
          new Task(3) {
            @Override
            public void execute() {
              crateLoot(stoner, x, y, z);
              stop();
            }

            @Override
            public void onStop() {}
          });
    }
  }

  public static void crateLoot(Stoner stoner, int x, int y, int z) {
    int random = Utility.random(5);
    GameObject object = ObjectManager.getGameObject(x, y, z);

    if (object == null) {
      return;
    }

    switch (random) {
      case 0:
      case 1:
      case 2:
      case 3:
        ObjectManager.remove(object);
        crates.remove(object);
        Item weapon = Utility.randomElement(WeaponGameConstants.CRATE_DATA);
        stoner.getBox().addItems(weapon);
        stoner.send(
            new SendMessage(
                "@dre@...You have found some "
                    + weapon.getDefinition().getName()
                    + " inside the chest!"));
        break;

      case 4:
        stoner.hit(new Hit(Utility.random(10)));
        stoner.send(
            new SendMessage("@dre@...While searching you cut your hand on a sharp object!"));
        ObjectManager.remove(object);
        crates.remove(object);
        break;

      case 5:
        stoner.teleport(Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS));
        stoner.send(
            new SendMessage("@dre@...While searching you feel a mysterious force move you!"));
        ObjectManager.remove(object);
        crates.remove(object);
        break;
    }
  }

  public static void upgrade(Stoner stoner) {
    if (stoner.getWeaponKills() >= 10) {
      leader = stoner;
      endGame(false);
      return;
    }
    if (!stoner.inWGGame() || !gameStoners.contains(stoner)) {
      return;
    }
    for (int index = 0; index < WeaponGameConstants.TIER_DATA.length; index++) {
      if (stoner.getWeaponKills() != index) {
        continue;
      }
      Item weapon = Utility.randomElement(WeaponGameConstants.TIER_DATA[index]);
      stoner.getEquipment().getItems()[3] = weapon;
      stoner.send(new SendEquipment(3, weapon.getId(), weapon.getAmount()));
      stoner.setAppearanceUpdateRequired(true);
      stoner.getCombat().reset();
      stoner.getUpdateFlags().setUpdateRequired(true);
      stoner.getEquipment().onLogin();
      DialogueManager.sendItem1(stoner, "You have advanced to the next tier!", weapon.getId());
    }
    for (Stoner stoners : gameStoners) {
      if (stoner.getWeaponKills() > stoners.getWeaponKills()) {
        leader = stoner;
        stoners.send(new SendStonerHint(true, stoner.getIndex()));
      }
    }
  }

  public static void endGame(boolean force) {
    for (GameObject object : crates) {
      ObjectManager.remove(object);
    }
    crates.clear();
    if (force) {
      World.sendGlobalMessage(
          "[ <col=0079AD>Weapon Game</col> ] Game has just ended with no victor!");
    } else {
      World.sendGlobalMessage(
          "[ <col=0079AD>Weapon Game</col> ] Game has been won by <col=0079AD>"
              + leader.deterquarryIcon(leader)
              + " "
              + leader.getUsername()
              + " </col>!");
      for (Stoner stoners : gameStoners) {
        leaveGame(stoners, true);
      }
    }

    leader = null;
    started = false;
  }

  public static void leaveGame(Stoner stoner, boolean game) {
    stoner.getBox().clear();
    stoner.getEquipment().clear();
    stoner.teleport(WeaponGameConstants.LOBBY_COODINATES);
    stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
    stoner.send(new SendStonerHint(true, -1));
    stoner.getProfession().restore();
    if (game) {
      if (leader != null) {
        if (leader == stoner) {
          stoner.getBox().add(995, 500_000);
          stoner.setWeaponPoints(stoner.getWeaponPoints() + WeaponGameConstants.LEADER_REWARD);
          DialogueManager.sendStatement(
              leader, "Congratulations! You have won the Weapon Game and was rewarded.");
        } else {
          if (stoner.getWeaponKills() < WeaponGameConstants.KILLS_FOR_REWARD) {
            DialogueManager.sendStatement(
                leader,
                "You needed a minimum of "
                    + WeaponGameConstants.KILLS_FOR_REWARD
                    + " kills to get a reward.");
          } else {
            stoner.setWeaponPoints(stoner.getWeaponPoints() + WeaponGameConstants.STONER_REWARD);
            DialogueManager.sendStatement(leader, "You lost! But was rewarded for your efforts!");
          }
        }
      }
    }
    gameStoners.remove(stoner);
  }

  public static int lobbyCount() {
    return lobbyStoners.size();
  }

  public static int gameCount() {
    return gameStoners.size();
  }

  public static String messageStoners(String message, boolean lobby) {
    if (lobby) {
      for (Stoner stoner : lobbyStoners) {
        stoner.send(new SendMessage(message));
      }
    } else {
      for (Stoner stoner : gameStoners) {
        stoner.send(new SendMessage(message));
      }
    }
    return null;
  }

  public static boolean objectClick(Stoner stoner, int id, int x, int y, int z) {
    switch (id) {
      case 2072:
        handleCrate(stoner, x, y, z);
        break;
      case 11005:
        if (!stoner.inWGLobby()) {
          joinLobby(stoner);
        } else {
          leaveLobby(stoner, true);
        }
        break;
    }
    return false;
  }

  public static void tick() {
    if (lobbyCount() > 0) {
      WeaponGameConstants.LOBBY_TIME--;
      if (WeaponGameConstants.LOBBY_TIME == 0
          || lobbyCount() == WeaponGameConstants.MAXIMUM_STONERS) {
        startGame();
        WeaponGameConstants.LOBBY_TIME = 180;
      }
    }
    if (started) {
      WeaponGameConstants.GAME_TIME--;
      if (WeaponGameConstants.GAME_TIME == 0
          || gameStoners.size() == 1
          || gameStoners.size() == 0) {
        endGame(true);
        return;
      }
      WeaponGameConstants.CRATE_TIME--;
      if (WeaponGameConstants.CRATE_TIME == 0) {
        spawnCrates();
      }
      WeaponGameConstants.METEOR_TIME--;
      if (WeaponGameConstants.METEOR_TIME == 0) {
        meteors();
      }
    }
  }
}
