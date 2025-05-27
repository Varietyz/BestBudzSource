package com.bestbudz.rs2.content.randomevent;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;

public class RandomEventController extends Controller {

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
    return false;
  }

  @Override
  public boolean canMove(Stoner paramStoner) {
    return false;
  }

  @Override
  public boolean canSave() {
    return true;
  }

  @Override
  public boolean canTalk() {
    return false;
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
    return false;
  }

  @Override
  public void onControllerInit(Stoner paramStoner) {}

  @Override
  public void onDeath(Stoner paramStoner) {}

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner paramStoner) {}

  @Override
  public void onTeleport(Stoner paramStoner) {}

  @Override
  public void tick(Stoner paramStoner) {}

  @Override
  public String toString() {
    return "Random Event Controller";
  }

  @Override
  public boolean transitionOnWalk(Stoner paramStoner) {
    return false;
  }
}
