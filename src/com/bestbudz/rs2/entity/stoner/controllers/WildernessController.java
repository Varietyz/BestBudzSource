package com.bestbudz.rs2.entity.stoner.controllers;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;

public class WildernessController extends Controller {

  public static final String GRADE_ATTRIBUTE = "wildlvlattr";

  @Override
  public boolean allowMultiSpells() {
    return true;
  }

  @Override
  public boolean allowPvPCombat() {
    return true;
  }

  @Override
  public boolean canAssaultNPC() {
    return true;
  }

  @Override
  public boolean canAssaultStoner(Stoner p, Stoner p2) {
    if (p2.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)) {
      int difference =
          Math.abs(p.getProfession().getCombatGrade() - p2.getProfession().getCombatGrade());

      if (difference > p.getWildernessGrade()) {
        p.getClient()
            .queueOutgoingPacket(
                new SendMessage("You must move deeper in the Wilderness to assault this stoner."));
        return false;
      }
      if (difference > p2.getWildernessGrade()) {
        p.getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "This stoner must move deeper in the Wilderness for you to assault him."));
        return false;
      }
    } else {
      p.getClient()
          .queueOutgoingPacket(
              new SendMessage("This stoner is busy or they are not in the Wilderness."));
      return false;
    }

    return true;
  }

  @Override
  public boolean canClick() {
    return true;
  }

  @Override
  public boolean canDrink(Stoner p) {
    return true;
  }

  @Override
  public boolean canEat(Stoner p) {
    return true;
  }

  @Override
  public boolean canEquip(Stoner p, int id, int slot) {
    return true;
  }

  @Override
  public boolean canUnequip(Stoner stoner) {
    return true;
  }

  @Override
  public boolean canDrop(Stoner stoner) {
    return true;
  }

  @Override
  public boolean canLogOut() {
    return true;
  }

  @Override
  public boolean canMove(Stoner p) {
    return true;
  }

  @Override
  public boolean canSave() {
    return true;
  }

  @Override
  public boolean canTalk() {
    return true;
  }

  @Override
  public boolean canTeleport() {
    return true;
  }

  @Override
  public boolean canTrade() {
    return true;
  }

  @Override
  public boolean canUseCombatType(Stoner p, CombatTypes type) {
    return true;
  }

  @Override
  public boolean canUseResonance(Stoner p, int id) {
    return true;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner p) {
    return true;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    return new Location(StonerConstants.HOME);
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return false;
  }

  @Override
  public void onControllerInit(Stoner stoner) {
    stoner.getClient().queueOutgoingPacket(new SendStonerOption("Assault", 3));

  }

  @Override
  public void onDeath(Stoner p) {
    p.getAttributes().remove("gainTarget");
  }

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner p) {}

  @Override
  public void onTeleport(Stoner p) {
    if (p.getAttributes().get("gainTarget") != null) {
      ((Task) p.getAttributes().get("gainTarget")).stop();
    }

  }

  @Override
  public void tick(Stoner stoner) {

  }

  @Override
  public String toString() {
    return "WILDERNESS";
  }

  @Override
  public boolean transitionOnWalk(Stoner p) {
    return true;
  }
}
