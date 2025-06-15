package com.bestbudz.rs2.content.minigames.weapongame;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.GenericMinigameController;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;

public class WeaponGameController extends GenericMinigameController {

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
    return false;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner paramStoner) {
    return true;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    return Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS);
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return true;
  }

  @Override
  public void onControllerInit(Stoner paramStoner) {}

  @Override
  public void onDeath(Stoner paramStoner) {

    Entity killer = paramStoner.getCombat().getDamageTracker().getKiller();

    if (killer != null && !killer.isNpc()) {
      killer.getStoner().setWeaponKills(killer.getStoner().getWeaponKills() + 1);
      WeaponGame.upgrade(killer.getStoner());
    }
  }

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner paramStoner) {
    WeaponGame.leaveGame(paramStoner, false);
  }

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner paramStoner) {
    paramStoner.send(
        new SendString(Utility.getFormattedTime(WeaponGameConstants.GAME_TIME), 41274));
    paramStoner.send(new SendString("" + WeaponGame.gameCount(), 41276));
    if (WeaponGame.leader != null) {
      paramStoner.send(new SendString(WeaponGame.leader.getUsername(), 41278));
    } else {
      paramStoner.send(new SendString("None", 41278));
    }
    paramStoner.send(
        new SendString(Utility.getFormattedTime(WeaponGameConstants.CRATE_TIME), 41280));

    paramStoner.send(new SendString("" + paramStoner.getWeaponKills(), 41282));
    if (paramStoner.getWeaponKills() == 0) {
      paramStoner.send(new SendUpdateItemsAlt(41283, 0, 0, 0));
    } else {
      paramStoner.send(
          new SendUpdateItemsAlt(
              41283, WeaponGameConstants.TIER_DATA[paramStoner.getWeaponKills()][0].getId(), 1, 0));
    }
    if (paramStoner.getWeaponKills() != 9) {
      paramStoner.send(
          new SendUpdateItemsAlt(
              41283,
              WeaponGameConstants.TIER_DATA[paramStoner.getWeaponKills() + 1][0].getId(),
              1,
              1));
    } else {
      paramStoner.send(new SendUpdateItemsAlt(41283, 995, 500_000, 1));
    }
  }

  @Override
  public String toString() {
    return "WEAPON_GAME_CONTROLLER";
  }

  @Override
  public boolean canUnequip(Stoner stoner) {
    return true;
  }

  @Override
  public boolean canDrop(Stoner stoner) {
    return false;
  }
}
