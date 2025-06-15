package com.bestbudz.rs2.entity.stoner.controllers;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class GenericMinigameController extends Controller {

  @Override
  public abstract boolean allowMultiSpells();

  @Override
  public abstract boolean allowPvPCombat();

  @Override
  public abstract boolean canAssaultNPC();

  @Override
  public abstract boolean canAssaultStoner(Stoner paramStoner1, Stoner paramStoner2);

  @Override
  public boolean canClick() {
    return true;
  }

  @Override
  public abstract boolean canDrink(Stoner paramStoner);

  @Override
  public abstract boolean canEat(Stoner paramStoner);

  @Override
  public abstract boolean canEquip(Stoner paramStoner, int paramInt1, int paramInt2);

  @Override
  public abstract boolean canLogOut();

  @Override
  public abstract boolean canMove(Stoner p);

  @Override
  public abstract boolean canSave();

  @Override
  public abstract boolean canTalk();

  @Override
  public abstract boolean canTeleport();

  @Override
  public abstract boolean canTrade();

  @Override
  public abstract boolean canUseCombatType(Stoner paramStoner, CombatTypes paramCombatTypes);

  @Override
  public abstract boolean canUseResonance(Stoner paramStoner, int id);

  @Override
  public abstract boolean canUseSpecialAssault(Stoner paramStoner);

  @Override
  public abstract Location getRespawnLocation(Stoner stoner);

  @Override
  public abstract boolean isSafe(Stoner stoner);

  @Override
  public abstract void onControllerInit(Stoner paramStoner);

  @Override
  public abstract void onDeath(Stoner paramStoner);

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public abstract void onDisconnect(Stoner paramStoner);

  @Override
  public abstract void onTeleport(Stoner p);

  @Override
  public abstract void tick(Stoner paramStoner);

  @Override
  public abstract String toString();

  @Override
  public boolean transitionOnWalk(Stoner p) {
    return false;
  }
}
