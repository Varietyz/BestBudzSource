package com.bestbudz.rs2.content.minigames.plunder;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;

public class PlunderController extends Controller {

  @Override
  public boolean allowMultiSpells() {
    return true;
  }

  @Override
  public boolean allowPvPCombat() {
    return false;
  }

  @Override
  public boolean canAssaultNPC() {
    return true;
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
    return true;
  }

  @Override
  public boolean canEat(Stoner paramStoner) {
    return true;
  }

  @Override
  public boolean canEquip(Stoner paramStoner, int paramInt1, int paramInt2) {
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
  public boolean canMove(Stoner paramStoner) {
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
    return false;
  }

  @Override
  public boolean canTrade() {
    return false;
  }

  @Override
  public boolean canUseCombatType(Stoner paramStoner, CombatTypes paramCombatTypes) {
    return true;
  }

  @Override
  public boolean canUseNecromance(Stoner paramStoner, int id) {
    return true;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner paramStoner) {
    return true;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    return new Location(3443, 2915, 0);
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
  public void onDisconnect(Stoner paramStoner) {
    paramStoner.teleport(new Location(3443, 2915, 0));
  }

  @Override
  public void onTeleport(Stoner paramStoner) {}

  @Override
  public void tick(Stoner paramStoner) {}

  @Override
  public String toString() {
    return "Pyramid Plunder Controller";
  }

  @Override
  public boolean transitionOnWalk(Stoner paramStoner) {
    return false;
  }
}
