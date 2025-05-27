package com.mayhem.rs2.content.minigames.miniraid;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.combat.Combat.CombatTypes;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.controllers.Controller;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Pest Control waiting room controller
 * @author Daniel
 *
 */
public class MiniRaidWaitingRoomController extends Controller {
	
	@Override
	public boolean allowMultiSpells() {
		return false;
	}

	@Override
	public boolean allowPvPCombat() {
		return false;
	}

	@Override
	public boolean canAttackNPC() {
		return false;
	}

	@Override
	public boolean canAttackPlayer(Player p, Player p2) {
		return false;
	}

	@Override
	public boolean canClick() {
		return true;
	}
	
	@Override
	public boolean canUnequip(Player player) {		
		return true;
	}

	@Override
	public boolean canDrop(Player player) {
		return true;
	}

	@Override
	public boolean canDrink(Player p) {
		return true;
	}

	@Override
	public boolean canEat(Player p) {
		return true;
	}

	@Override
	public boolean canEquip(Player p, int id, int slot) {
		return true;
	}

	@Override
	public boolean canLogOut() {
		return false;
	}

	@Override
	public boolean canMove(Player p) {
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
	public boolean canUseCombatType(Player p, CombatTypes type) {
		return false;
	}

	@Override
	public boolean canUsePrayer(Player p, int id) {
		return true;
	}

	@Override
	public boolean canUseSpecialAttack(Player p) {
		return false;
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return new Location(PlayerConstants.EDGEVILLE);
	}

	@Override
	public boolean isSafe(Player player) {
		return true;
	}

	@Override
	public void onControllerInit(Player p) {
	}

	@Override
	public void onDeath(Player p) {
	}

	@Override
	public void onDisconnect(Player p) {
	}

	@Override
	public void onTeleport(Player p) {
	}

	@Override
	public void tick(Player p) {
		p.getClient().queueOutgoingPacket(new SendString("Next Raid in: " + Utility.getFormattedTime(MiniRaid.getMinutesTillDepart())  + "", 31120));
		p.getClient().queueOutgoingPacket(new SendString("Players Ready: " + MiniRaid.getPlayersReady(), 31121));
		p.getClient().queueOutgoingPacket(new SendString("(Need 2 to 25 players)", 31122));
		//p.getClient().queueOutgoingPacket(new SendString("Points: " + Utility.format(p.getRaidPoints()), 21123));
	}

	@Override
	public String toString() {
		return "MINI RAID";
	}

	@Override
	public boolean transitionOnWalk(Player p) {
		return false;
	}

	@Override
	public void onKill(Player player, Entity killed) {
	// TODO Auto-generated method stub
	
	}
}
