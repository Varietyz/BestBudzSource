package com.mayhem.rs2.entity.player.controllers;

import com.mayhem.core.task.impl.ForceMovementController;
import com.mayhem.rs2.content.minigames.clanwars.ClanWarsFFAController;
import com.mayhem.rs2.content.minigames.duelarena.DuelArenaController;
import com.mayhem.rs2.content.minigames.duelarena.DuelStakeController;
import com.mayhem.rs2.content.minigames.duelarena.DuelingController;
import com.mayhem.rs2.content.minigames.f2parena.F2PArenaController;
import com.mayhem.rs2.content.minigames.fightcave.TzharrController;
import com.mayhem.rs2.content.minigames.fightpits.FightPits;
import com.mayhem.rs2.content.minigames.fightpits.FightPitsController;
import com.mayhem.rs2.content.minigames.fightpits.FightPitsWaitingController;
import com.mayhem.rs2.content.minigames.godwars.GodWarsController;
import com.mayhem.rs2.content.minigames.pestcontrol.PestControl;
import com.mayhem.rs2.content.minigames.pestcontrol.PestControlController;
import com.mayhem.rs2.content.minigames.pestcontrol.PestWaitingRoomController;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaid;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidController;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidWaitingRoomController;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Controller;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2WaitingRoomController;
import com.mayhem.rs2.content.minigames.plunder.PlunderController;
import com.mayhem.rs2.content.minigames.weapongame.WeaponGameController;
import com.mayhem.rs2.content.minigames.weapongame.WeaponLobbyController;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;

public class ControllerManager {

	public static final DefaultController DEFAULT_CONTROLLER = new DefaultController();
	public static final WildernessController WILDERNESS_CONTROLLER = new WildernessController();
	public static final DuelArenaController DUEL_ARENA_CONTROLLER = new DuelArenaController();
	public static final DuelingController DUELING_CONTROLLER = new DuelingController();
	public static final DuelStakeController DUEL_STAKE_CONTROLLER = new DuelStakeController();
	public static final FightPitsController FIGHT_PITS_CONTROLLER = new FightPitsController();
	public static final FightPitsWaitingController FIGHT_PITS_WAITING_CONTROLLER = new FightPitsWaitingController();
	public static final TzharrController TZHARR_CAVES_CONTROLLER = new TzharrController();
	public static final PestWaitingRoomController PEST_WAITING_ROOM_CONTROLLER = new PestWaitingRoomController();
	public static final MiniRaidWaitingRoomController MINI_RAID_WAITING_ROOM_CONTROLLER = new MiniRaidWaitingRoomController();
	public static final MiniRaid2WaitingRoomController MINI_RAID2_WAITING_ROOM_CONTROLLER = new MiniRaid2WaitingRoomController();
	public static final ForceMovementController FORCE_MOVEMENT_CONTROLLER = new ForceMovementController();
	public static final GodWarsController GOD_WARS_CONTROLLER = new GodWarsController();
	public static final Controller PEST_CONTROLLER = new PestControlController();
	public static final Controller MINIRAID_CONTROLLER = new MiniRaidController();
	public static final Controller MINIRAID2_CONTROLLER = new MiniRaid2Controller();
	public static final PlunderController PLUNDER_CONTROLLER = new PlunderController();
	public static final WeaponGameController WEAPON_GAME_CONTROLLER = new WeaponGameController();
	public static final WeaponLobbyController WEAPON_LOBBY_CONTROLLER = new WeaponLobbyController();
	public static final F2PArenaController F2P_ARENA_CONTROLLER = new F2PArenaController();
	public static final ClanWarsFFAController CLAN_WARS_FFA_CONTROLLER = new ClanWarsFFAController();

	public static void onForceLogout(Player player) {
		Controller c = player.getController();
		if (c.equals(DUELING_CONTROLLER)) {
			player.getDueling().onForceLogout();
		} else if (c.equals(FIGHT_PITS_WAITING_CONTROLLER)) {
			player.teleport(new Location(2399, 5177));
			FightPits.removeFromWaitingRoom(player);
		} else if (c.equals(FIGHT_PITS_CONTROLLER)) {
			player.teleport(new Location(2399, 5177));
			FightPits.removeFromGame(player);
		} else if (c.equals(PEST_WAITING_ROOM_CONTROLLER)) {
			PestControl.clickObject(player, 14314);
		} else if (c.equals(FORCE_MOVEMENT_CONTROLLER) && !player.inWilderness()) {
			player.teleport(PlayerConstants.HOME);
		} else if (c.equals(PLUNDER_CONTROLLER)) {
			c.onDisconnect(player);
		} else if (c.equals(MINI_RAID_WAITING_ROOM_CONTROLLER)) {
			MiniRaid.clickObject(player, 7322);
		} else if (c.equals(MINI_RAID2_WAITING_ROOM_CONTROLLER)) {
			MiniRaid2.clickObject(player, 14846);
		}
		
		
	}

	public static void setControllerOnWalk(Player player) {
		if ((player.getController() != null) && (!player.getController().transitionOnWalk(player))) {
			return;
		}

		Controller c = DEFAULT_CONTROLLER;

		if (player.inWilderness())
			c = WILDERNESS_CONTROLLER;
		else if (player.inDuelArena())
			c = DUEL_ARENA_CONTROLLER;
		else if (player.inGodwars()) {
			c = GOD_WARS_CONTROLLER;
		}
		
		if (c != player.getController()) {
			if (player.getController() == GOD_WARS_CONTROLLER) {
				player.getMinigames().resetGWD();
			}
		}

		if ((c == null) || (player.getController() == null) || (!player.getController().equals(c))) {
			player.setController(c);
		}
	}
}
