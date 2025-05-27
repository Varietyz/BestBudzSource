package com.bestbudz.rs2.content.minigames.clanwars;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.GenericMinigameController;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;

public class ClanWarsFFAController extends GenericMinigameController {

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
	return false;
	}

	@Override
	public boolean canAssaultStoner(Stoner paramStoner1, Stoner paramStoner2) {
	if (!paramStoner1.inClanWarsFFA() || !paramStoner2.inClanWarsFFA()) {
		return false;
	}
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
	public boolean canUseCombatType(Stoner paramStoner, CombatTypes paramCombatTypes) {
	return true;
	}

	@Override
	public boolean canUseNecromance(Stoner paramStoner, int id) {
	return true;
	}

	@Override
	public boolean canUseSpecialAssault(Stoner paramStoner) {
	return true;
	}

	@Override
	public Location getRespawnLocation(Stoner stoner) {
	return ClanWarsConstants.FFA_PORTAL;
	}

	@Override
	public boolean isSafe(Stoner stoner) {
	return true;
	}

	@Override
	public void onControllerInit(Stoner stoner) {
	stoner.send(new SendMessage("@dre@Pass the line enter combat zone."));
	}

	@Override
	public void onDeath(Stoner paramStoner) {
	paramStoner.teleport(new Location(3327, 4754, paramStoner.getZ()));
	}

	@Override
	public void onDisconnect(Stoner paramStoner) {
	paramStoner.teleport(ClanWarsConstants.CLAN_WARS_ARENA);
	}

	@Override
	public void tick(Stoner paramStoner) {
	if (paramStoner.inClanWarsFFA()) {
		paramStoner.getClient().queueOutgoingPacket(new SendStonerOption("Assault", 3));
	} else {
		paramStoner.getClient().queueOutgoingPacket(new SendStonerOption("null", 3));
	}
	}

	@Override
	public String toString() {
	return "Clan Wars FFA";
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
	return true;
	}

	@Override
	public boolean canTrade() {
	return true;
	}

	@Override
	public void onTeleport(Stoner p) {
	}

}
