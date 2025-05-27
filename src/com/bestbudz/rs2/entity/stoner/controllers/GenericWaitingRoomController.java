package com.bestbudz.rs2.entity.stoner.controllers;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class GenericWaitingRoomController extends GenericMinigameController {
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
  public boolean canSave() {
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
    return null;
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return true;
  }

  @Override
  public abstract void onControllerInit(Stoner paramStoner);

  @Override
  public void onDeath(Stoner p) {}

  @Override
  public abstract void onDisconnect(Stoner paramStoner);

  @Override
  public abstract String toString();
}
