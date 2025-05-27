package com.bestbudz.rs2.content.minigames.pestcontrol;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class PestWaitingRoomController extends Controller {

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
	return false;
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
	return true;
	}

	@Override
	public boolean canUseCombatType(Stoner p, CombatTypes type) {
	return false;
	}

	@Override
	public boolean canUseNecromance(Stoner p, int id) {
	return true;
	}

	@Override
	public boolean canUseSpecialAssault(Stoner p) {
	return false;
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
	p.getClient().queueOutgoingPacket(new SendString("Points: 0", 21123));
	}

	@Override
	public void onDeath(Stoner p) {
	}

	@Override
	public void onKill(Stoner stoner, Entity killed) {

	}

	@Override
	public void onDisconnect(Stoner p) {
	}

	@Override
	public void onTeleport(Stoner p) {
	}

	@Override
	public void tick(Stoner p) {
	p.getClient().queueOutgoingPacket(new SendString("Next Departure: " + Utility.getFormattedTime(PestControl.getMinutesTillDepart()), 21120));
	p.getClient().queueOutgoingPacket(new SendString("Stoners Ready: " + PestControl.getStonersReady(), 21121));
	p.getClient().queueOutgoingPacket(new SendString("(Need 2 to 25 stoners)", 21122));
	p.getClient().queueOutgoingPacket(new SendString("Points: " + Utility.format(p.getPestPoints()), 21123));
	}

	@Override
	public String toString() {
	return "PEST CONTROL";
	}

	@Override
	public boolean transitionOnWalk(Stoner p) {
	return false;
	}
}
