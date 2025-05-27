package com.bestbudz.rs2.content.combat;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;

public interface CombatInterface {

  void afterCombatProcess(Entity paramEntity);

  boolean canAssault();

  void checkForDeath();

  int getCorrectedDamage(int paramInt);

  int getMaxHit(CombatTypes paramCombatTypes);

  void hit(Hit paramHit);

  boolean isIgnoreHitSuccess();

  void onAssault(
      Entity paramEntity, long paramInt, CombatTypes paramCombatTypes, boolean paramBoolean);

  void onCombatProcess(Entity paramEntity);

  void onHit(Entity paramEntity, Hit paramHit);

  void updateCombatType();
}
