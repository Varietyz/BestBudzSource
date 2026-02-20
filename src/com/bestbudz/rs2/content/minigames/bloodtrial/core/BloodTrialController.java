package com.bestbudz.rs2.content.minigames.bloodtrial.core;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.minigames.bloodtrial.BloodTrial;
import com.bestbudz.rs2.content.minigames.bloodtrial.finish.BloodTrialFinish;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;

public final class BloodTrialController extends Controller {
	private static final String MINIGAME_NAME = "Blood Trial";

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
		return BloodTrial.LEAVE;
	}

	@Override
	public boolean isSafe(Stoner stoner) {
		return true;
	}

	@Override
	public void onControllerInit(Stoner p) {
		// No initialization needed
	}

	@Override
	public void onDeath(Stoner p) {
		BloodTrialFinish.finishedBloodTrial(p, false);
	}

	@Override
	public void onKill(Stoner stoner, Entity killed) {
	}

	@Override
	public void onDisconnect(Stoner p) {
		BloodTrialFinish.finishedBloodTrial(p, false);
	}

	@Override
	public void onTeleport(Stoner p) {
		// No action needed
	}

	@Override
	public void tick(Stoner p) {
		// No regular ticking needed
	}

	@Override
	public String toString() {
		return MINIGAME_NAME;
	}

	@Override
	public boolean transitionOnWalk(Stoner p) {
		return false;
	}
}