package com.bestbudz.rs2.content.combat;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;

public interface CombatInterface {

	public abstract void afterCombatProcess(Entity paramEntity);

	public abstract boolean canAssault();

	public abstract void checkForDeath();

	public abstract int getCorrectedDamage(int paramInt);

	public abstract int getMaxHit(CombatTypes paramCombatTypes);

	public abstract void hit(Hit paramHit);

	public abstract boolean isIgnoreHitSuccess();

	public abstract void onAssault(Entity paramEntity, int paramInt, CombatTypes paramCombatTypes, boolean paramBoolean);

	public abstract void onCombatProcess(Entity paramEntity);

	public abstract void onHit(Entity paramEntity, Hit paramHit);

	public abstract void updateCombatType();

}
