package com.bestbudz.rs2.content.minigames.f2parena;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.GenericMinigameController;

public class F2PArenaController extends GenericMinigameController {
  @Override
  public boolean allowMultiSpells() {
    return false;
  }

  @Override
  public boolean allowPvPCombat() {
    return true;
  }

  @Override
  public boolean canAssaultNPC() {
    return false;
  }

  @Override
  public boolean canAssaultStoner(Stoner paramStoner1, Stoner paramStoner2) {
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
    for (int i = 0; i < F2PArenaConstants.ALLOWED_RESONANCES.length; i++) {
      if (id == F2PArenaConstants.ALLOWED_RESONANCES[i]) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner paramStoner) {
    return false;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    return Utility.randomElement(F2PArenaConstants.RESPAWN_LOCATIONS);
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
  public void onDisconnect(Stoner paramStoner) {}

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner paramStoner) {}

  @Override
  public String toString() {
    return "F2P Arena";
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
