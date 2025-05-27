package com.bestbudz.rs2.entity.stoner.controllers;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class Controller {

	public abstract boolean allowMultiSpells();

	public abstract boolean allowPvPCombat();

	public abstract boolean canAssaultNPC();

	public abstract boolean canAssaultStoner(Stoner stoner1, Stoner stoner2);

	public abstract boolean canClick();

	public abstract boolean canDrink(Stoner stoner);

	public abstract boolean canEat(Stoner stoner);

	public abstract boolean canEquip(Stoner stoner, int int1, int int2);

	public abstract boolean canUnequip(Stoner stoner);

	public abstract boolean canDrop(Stoner stoner);

	public abstract boolean canLogOut();

	public abstract boolean canMove(Stoner stoner);

	public abstract boolean canSave();

	public abstract boolean canTalk();

	public abstract boolean canTeleport();

	public abstract boolean canTrade();

	public abstract boolean canUseCombatType(Stoner stoner, CombatTypes paramCombatTypes);

	public abstract boolean canUseNecromance(Stoner stoner, int id);

	public abstract boolean canUseSpecialAssault(Stoner stoner);

	public abstract Location getRespawnLocation(Stoner stoner);

	public abstract boolean isSafe(Stoner stoner);

	public abstract void onControllerInit(Stoner stoner);

	public abstract void onDeath(Stoner stoner);

	public abstract void onKill(Stoner stoner, Entity killed);

	public abstract void onDisconnect(Stoner stoner);

	public abstract void onTeleport(Stoner stoner);

	public void throwException(Stoner stoner, String action) {
	System.out.println("||||||||||||||||||||||||||");
	System.out.println("UNABLE TO " + action + " FOR STONER " + stoner.getUsername() + "!");
	System.out.println("CONTROLLER: " + stoner.getController().toString());
	System.out.println("||||||||||||||||||||||||||");
	}

	public abstract void tick(Stoner stoner);

	@Override
	public abstract String toString();

	public abstract boolean transitionOnWalk(Stoner stoner);

}
