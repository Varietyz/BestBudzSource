package com.bestbudz.rs2.content.minigames.weapongame;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class WeaponLobbyController extends Controller {

  @Override
  public boolean allowMultiSpells() {
    return false;
  }

  @Override
  public boolean allowPvPCombat() {
    return false;
  }

  @Override
  public boolean canAssaultNPC() {
    return false;
  }

  @Override
  public boolean canAssaultStoner(Stoner paramStoner1, Stoner paramStoner2) {
    return false;
  }

  @Override
  public boolean canClick() {
    return true;
  }

  @Override
  public boolean canDrink(Stoner paramStoner) {
    return false;
  }

  @Override
  public boolean canEat(Stoner paramStoner) {
    return false;
  }

  @Override
  public boolean canEquip(Stoner paramStoner, int paramInt1, int paramInt2) {
    return false;
  }

  @Override
  public boolean canUnequip(Stoner stoner) {
    return false;
  }

  @Override
  public boolean canDrop(Stoner stoner) {
    return false;
  }

  @Override
  public boolean canLogOut() {
    return true;
  }

  @Override
  public boolean canMove(Stoner paramStoner) {
    return true;
  }

  @Override
  public boolean canSave() {
    return false;
  }

  @Override
  public boolean canTalk() {
    return true;
  }

  @Override
  public boolean canTeleport() {
    return false;
  }

  @Override
  public boolean canTrade() {
    return false;
  }

  @Override
  public boolean canUseCombatType(Stoner paramStoner, CombatTypes paramCombatTypes) {
    return false;
  }

  @Override
  public boolean canUseNecromance(Stoner paramStoner, int id) {
    return false;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner paramStoner) {
    return false;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    return null;
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return true;
  }

  @Override
  public void onControllerInit(Stoner stoner) {
    TaskQueue.queue(
        new Task(stoner, 1) {
          @Override
          public void execute() {
            DialogueManager.sendStatement(
                stoner,
                "Welcome to the @dre@Weapon Game Lobby@bla@!",
                "Game will start when there are a minimum of @dre@5@bla@ stoners.",
                "Click on the @dre@portal@bla@ to @dre@exit@bla@.");
            stop();
          }

          @Override
          public void onStop() {}
        });
  }

  @Override
  public void onDeath(Stoner stoner) {}

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner stoner) {
    WeaponGame.leaveLobby(stoner, false);
  }

  @Override
  public void onTeleport(Stoner stoner) {
    stoner.send(new SendMessage("@dre@If you would like to exit, please enter the portal."));
  }

  @Override
  public void tick(Stoner stoner) {
    stoner.send(new SendString("Stoners ready: " + WeaponGame.lobbyCount(), 41252));
    stoner.send(
        new SendString(
            "(Need "
                + WeaponGameConstants.MINIMUM_STONERS
                + " to "
                + WeaponGameConstants.MAXIMUM_STONERS
                + ")",
            41253));
    stoner.send(
        new SendString(
            "Next Departure: " + Utility.getFormattedTime(WeaponGameConstants.LOBBY_TIME), 41254));
  }

  @Override
  public String toString() {
    return "WEAPON_GAME_LOBBY_CONTROLLER";
  }

  @Override
  public boolean transitionOnWalk(Stoner paramStoner) {
    return false;
  }
}
