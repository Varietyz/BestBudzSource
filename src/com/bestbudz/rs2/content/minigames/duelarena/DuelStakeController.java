package com.bestbudz.rs2.content.minigames.duelarena;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;

public class DuelStakeController extends Controller {
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
  public boolean canAssaultStoner(Stoner p, Stoner p2) {
    return false;
  }

  @Override
  public boolean canClick() {
    return true;
  }

  @Override
  public boolean canDrink(Stoner p) {
    return false;
  }

  @Override
  public boolean canEat(Stoner p) {
    return false;
  }

  @Override
  public boolean canEquip(Stoner p, int id, int slot) {
    return false;
  }

  @Override
  public boolean canUnequip(Stoner stoner) {
    return true;
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
  public boolean canMove(Stoner p) {
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
  public boolean canUseCombatType(Stoner p, CombatTypes type) {
    return false;
  }

  @Override
  public boolean canUseNecromance(Stoner p, int id) {
    return false;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner p) {
    return false;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    return StonerConstants.HOME;
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return false;
  }

  @Override
  public void onControllerInit(Stoner p) {}

  @Override
  public void onDeath(Stoner p) {}

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner p) {}

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner p) {}

  @Override
  public String toString() {
    return "Duel Stake";
  }

  @Override
  public boolean transitionOnWalk(Stoner p) {
    return false;
  }
}
