package com.bestbudz.rs2.content.minigames.duelarena;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendWalkableInterface;

public class DuelArenaController extends Controller {
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
    return false;
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
    return new Location(StonerConstants.EDGEVILLE);
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return false;
  }

  @Override
  public void onControllerInit(Stoner stoner) {
    stoner.getClient().queueOutgoingPacket(new SendStonerOption("Challenge", 3));
    stoner.getClient().queueOutgoingPacket(new SendWalkableInterface(201));
  }

  @Override
  public void onDeath(Stoner p) {}

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner p) {}

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner stoner) {}

  @Override
  public String toString() {
    return "DUEL ARENA";
  }

  @Override
  public boolean transitionOnWalk(Stoner p) {
    return true;
  }
}
