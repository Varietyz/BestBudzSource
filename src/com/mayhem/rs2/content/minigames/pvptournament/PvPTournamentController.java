/**
 * 
 */
package com.mayhem.rs2.content.minigames.pvptournament;

import com.mayhem.rs2.content.combat.Combat.CombatTypes;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.EquipmentConstants;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.Controller;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendPlayerOption;

/**
 * @author ReverendDread
 * Jul 29, 2018
 */
public class PvPTournamentController extends Controller {
	
	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#allowMultiSpells()
	 */
	@Override
	public boolean allowMultiSpells() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#allowPvPCombat()
	 */
	@Override
	public boolean allowPvPCombat() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canAttackNPC()
	 */
	@Override
	public boolean canAttackNPC() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canAttackPlayer(com.mayhem.rs2.entity.player.Player, com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean canAttackPlayer(Player player1, Player player2) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canClick()
	 */
	@Override
	public boolean canClick() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canDrink(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean canDrink(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canEat(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean canEat(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canEquip(com.mayhem.rs2.entity.player.Player, int, int)
	 */
	@Override
	public boolean canEquip(Player player, int int1, int int2) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canUnequip(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean canUnequip(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canDrop(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean canDrop(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canLogOut()
	 */
	@Override
	public boolean canLogOut() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canMove(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean canMove(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canSave()
	 */
	@Override
	public boolean canSave() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canTalk()
	 */
	@Override
	public boolean canTalk() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canTeleport()
	 */
	@Override
	public boolean canTeleport() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canTrade()
	 */
	@Override
	public boolean canTrade() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canUseCombatType(com.mayhem.rs2.entity.player.Player, com.mayhem.rs2.content.combat.Combat.CombatTypes)
	 */
	@Override
	public boolean canUseCombatType(Player player, CombatTypes paramCombatTypes) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canUsePrayer(com.mayhem.rs2.entity.player.Player, int)
	 */
	@Override
	public boolean canUsePrayer(Player player, int id) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#canUseSpecialAttack(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean canUseSpecialAttack(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#getRespawnLocation(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public Location getRespawnLocation(Player player) {
		return new Location(3087, 3490, 0);
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#isSafe(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean isSafe(Player player) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#onControllerInit(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public void onControllerInit(Player player) {
		player.getClient().queueOutgoingPacket(new SendPlayerOption("Attack", 3));
		player.teleport(World.getTournament().getRandomSpawn());
		World.getTournament().restockGear(player);
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#onDeath(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public void onDeath(Player player) {
		player.getClient().queueOutgoingPacket(new SendPlayerOption("null", 3));
		World.getTournament().exit(player, false);
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#onKill(com.mayhem.rs2.entity.player.Player, com.mayhem.rs2.entity.Entity)
	 */
	@Override
	public void onKill(Player player, Entity killed) {
		Player kill = (Player) killed;
		World.getTournament().handleKill(kill, player);
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#onDisconnect(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public void onDisconnect(Player player) {
		World.getTournament().exit(player, false);
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#onTeleport(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public void onTeleport(Player player) {
		World.getTournament().exit(player, false);
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#tick(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public void tick(Player player) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.player.controllers.Controller#transitionOnWalk(com.mayhem.rs2.entity.player.Player)
	 */
	@Override
	public boolean transitionOnWalk(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

}
