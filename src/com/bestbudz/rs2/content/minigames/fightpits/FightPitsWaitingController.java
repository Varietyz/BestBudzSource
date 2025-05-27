package com.bestbudz.rs2.content.minigames.fightpits;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendWalkableInterface;

public class FightPitsWaitingController extends Controller {
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
	public boolean canUnequip(Stoner stoner) {
	return true;
	}

	@Override
	public boolean canDrop(Stoner stoner) {
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
	public boolean canLogOut() {
	return true;
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
	return true;
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
	return new Location(StonerConstants.EDGEVILLE);
	}

	@Override
	public boolean isSafe(Stoner stoner) {
	return true;
	}

	@Override
	public void onControllerInit(Stoner p) {
	FightPits.updateInterface(p);
	p.getClient().queueOutgoingPacket(new SendStonerOption("null", 3));
	for (int i = 2; i < FightPitsConstants.FIGHT_PITS_INTERFACE_STRINGS.length; i++) {
		p.getClient().queueOutgoingPacket(new SendString("", FightPitsConstants.FIGHT_PITS_INTERFACE_STRINGS[i]));
	}
	p.getClient().queueOutgoingPacket(new SendWalkableInterface(17600));
	}

	@Override
	public void onDeath(Stoner p) {
	p.setController(ControllerManager.DEFAULT_CONTROLLER);
	FightPits.removeFromWaitingRoom(p);
	}

	@Override
	public void onDisconnect(Stoner p) {
	FightPits.removeFromWaitingRoom(p);
	}

	@Override
	public void onTeleport(Stoner p) {
	FightPits.removeFromWaitingRoom(p);
	}

	@Override
	public void tick(Stoner p) {
	FightPits.updateInterface(p);
	}

	@Override
	public String toString() {
	return "FIGHT PITS WAITING ROOM";
	}

	@Override
	public boolean transitionOnWalk(Stoner p) {
	return false;
	}

	@Override
	public void onKill(Stoner stoner, Entity killed) {
	}
}
