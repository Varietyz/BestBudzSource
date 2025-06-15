package com.bestbudz.rs2.content.minigames.fightcave;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;

public final class TzharrController extends Controller {

  public static final String MINIGAME = "Tzharr Fight Caves";

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
  public boolean canAssaultStoner(Stoner p, Stoner p2) {
    return false;
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
    return false;
  }

  @Override
  public boolean canTrade() {
    return false;
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
    return TzharrGame.LEAVE;
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return true;
  }

  @Override
  public void onControllerInit(Stoner p) {}

  @Override
  public void onDeath(Stoner p) {
    TzharrGame.finish(p, false);
  }

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner p) {
    TzharrGame.finish(p, false);
  }

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner p) {}

  @Override
  public String toString() {
    return "Tzharr Fight Caves";
  }

  @Override
  public boolean transitionOnWalk(Stoner p) {
    return false;
  }
}
