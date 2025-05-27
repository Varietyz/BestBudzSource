package com.bestbudz.rs2.entity.stoner.controllers;

import com.bestbudz.core.task.impl.ForceMovementController;
import com.bestbudz.rs2.content.minigames.clanwars.ClanWarsFFAController;
import com.bestbudz.rs2.content.minigames.duelarena.DuelArenaController;
import com.bestbudz.rs2.content.minigames.duelarena.DuelStakeController;
import com.bestbudz.rs2.content.minigames.duelarena.DuelingController;
import com.bestbudz.rs2.content.minigames.f2parena.F2PArenaController;
import com.bestbudz.rs2.content.minigames.fightcave.TzharrController;
import com.bestbudz.rs2.content.minigames.fightpits.FightPits;
import com.bestbudz.rs2.content.minigames.fightpits.FightPitsController;
import com.bestbudz.rs2.content.minigames.fightpits.FightPitsWaitingController;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsController;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControl;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControlController;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestWaitingRoomController;
import com.bestbudz.rs2.content.minigames.plunder.PlunderController;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGameController;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponLobbyController;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;

public class ControllerManager {

  public static final DefaultController DEFAULT_CONTROLLER = new DefaultController();
  public static final WildernessController WILDERNESS_CONTROLLER = new WildernessController();
  public static final DuelArenaController DUEL_ARENA_CONTROLLER = new DuelArenaController();
  public static final DuelingController DUELING_CONTROLLER = new DuelingController();
  public static final DuelStakeController DUEL_STAKE_CONTROLLER = new DuelStakeController();
  public static final FightPitsController FIGHT_PITS_CONTROLLER = new FightPitsController();
  public static final FightPitsWaitingController FIGHT_PITS_WAITING_CONTROLLER =
      new FightPitsWaitingController();
  public static final TzharrController TZHARR_CAVES_CONTROLLER = new TzharrController();
  public static final PestWaitingRoomController PEST_WAITING_ROOM_CONTROLLER =
      new PestWaitingRoomController();
  public static final ForceMovementController FORCE_MOVEMENT_CONTROLLER =
      new ForceMovementController();
  public static final GodWarsController GOD_WARS_CONTROLLER = new GodWarsController();
  public static final Controller PEST_CONTROLLER = new PestControlController();
  public static final PlunderController PLUNDER_CONTROLLER = new PlunderController();
  public static final WeaponGameController WEAPON_GAME_CONTROLLER = new WeaponGameController();
  public static final WeaponLobbyController WEAPON_LOBBY_CONTROLLER = new WeaponLobbyController();
  public static final F2PArenaController F2P_ARENA_CONTROLLER = new F2PArenaController();
  public static final ClanWarsFFAController CLAN_WARS_FFA_CONTROLLER = new ClanWarsFFAController();

  public static void onForceLogout(Stoner stoner) {
    Controller c = stoner.getController();
    if (c.equals(DUELING_CONTROLLER)) {
      stoner.getDueling().onForceLogout();
    } else if (c.equals(FIGHT_PITS_WAITING_CONTROLLER)) {
      stoner.teleport(new Location(2399, 5177));
      FightPits.removeFromWaitingRoom(stoner);
    } else if (c.equals(FIGHT_PITS_CONTROLLER)) {
      stoner.teleport(new Location(2399, 5177));
      FightPits.removeFromGame(stoner);
    } else if (c.equals(PEST_WAITING_ROOM_CONTROLLER)) {
      PestControl.clickObject(stoner, 14314);
    } else if (c.equals(FORCE_MOVEMENT_CONTROLLER) && !stoner.inWilderness()) {
      stoner.teleport(StonerConstants.HOME);
    } else if (c.equals(PLUNDER_CONTROLLER)) {
      c.onDisconnect(stoner);
    }
  }

  public static void setControllerOnWalk(Stoner stoner) {
    if ((stoner.getController() != null) && (!stoner.getController().transitionOnWalk(stoner))) {
      return;
    }

    Controller c = DEFAULT_CONTROLLER;

    if (stoner.inWilderness()) c = WILDERNESS_CONTROLLER;
    else if (stoner.inDuelArena()) c = DUEL_ARENA_CONTROLLER;
    else if (stoner.inGodwars()) {
      c = GOD_WARS_CONTROLLER;
    }

    if (c != stoner.getController()) {
      if (stoner.getController() == GOD_WARS_CONTROLLER) {
        stoner.getMinigames().resetGWD();
      }
    }

    if ((c == null) || (stoner.getController() == null) || (!stoner.getController().equals(c))) {
      stoner.setController(c);
    }
  }
}
