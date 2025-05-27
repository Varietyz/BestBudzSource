package com.bestbudz.rs2.content.minigames.fightpits;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendWalkableInterface;

public class FightPitsController extends Controller {
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
    return (p.getController().equals(ControllerManager.FIGHT_PITS_CONTROLLER))
        && (p2.getController().equals(ControllerManager.FIGHT_PITS_CONTROLLER));
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
    return true;
  }

  @Override
  public boolean canUseNecromance(Stoner p, int id) {
    return true;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner p) {
    return true;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    return new Location(2399, 5169);
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return true;
  }

  @Override
  public void onControllerInit(Stoner p) {
    p.getClient().queueOutgoingPacket(new SendStonerOption("Assault", 3));
    p.getClient().queueOutgoingPacket(new SendWalkableInterface(17600));
  }

  @Override
  public void onDeath(Stoner p) {
    FightPits.onStonerDeath(p);
  }

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner p) {}

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner p) {
    FightPits.updateInterface(p);
  }

  @Override
  public String toString() {
    return "FIGHT PITS GAME";
  }

  @Override
  public boolean transitionOnWalk(Stoner p) {
    return false;
  }
}
