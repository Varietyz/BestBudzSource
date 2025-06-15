package com.bestbudz.rs2.content.minigames.pestcontrol;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.GenericMinigameController;

public class PestControlController extends GenericMinigameController {

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
  public boolean canAssaultStoner(Stoner paramStoner1, Stoner paramStoner2) {
    return false;
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
  public boolean canLogOut() {
    return false;
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
  public boolean canUseCombatType(Stoner paramStoner, CombatTypes paramCombatTypes) {
    return true;
  }

  @Override
  public boolean canUseResonance(Stoner paramStoner, int id) {
    return true;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner paramStoner) {
    return true;
  }

  @Override
  public Location getRespawnLocation(Stoner p) {
    if (p.getAttributes().get(PestControlGame.PEST_GAME_KEY) != null) {
      if (((PestControlGame) p.getAttributes().get(PestControlGame.PEST_GAME_KEY)).hasEnded()) {
        return new Location(2657, 2639, p.getZ());
      }
    }

    return PestControlConstants.getRandomBoatLocation(p.getZ());
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return true;
  }

  @Override
  public void onControllerInit(Stoner paramStoner) {}

  @Override
  public void onDeath(Stoner paramStoner) {}

  @Override
  public void onDisconnect(Stoner p) {
    p.teleport(new Location(2657, 2639, 0));
    ((PestControlGame) p.getAttributes().get(PestControlGame.PEST_GAME_KEY)).remove(p);
  }

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner paramStoner) {}

  @Override
  public String toString() {
    return "Pest Control";
  }

  @Override
  public boolean canUnequip(Stoner stoner) {
    return true;
  }

  @Override
  public boolean canDrop(Stoner stoner) {
    return true;
  }
}
