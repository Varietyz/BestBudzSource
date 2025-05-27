/**
 * 
 */
package com.mayhem.rs2.content.minigames.pvptournament;

import java.util.ArrayList;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.EquipmentConstants;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.ControllerManager;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles PvP Tournament related functions.
 * @author ReverendDread
 * Jul 29, 2018
 */
public class PvPTournament {

	/** Players in the lobby */
	private ArrayList<Player> players;
	
	/** The host of the tournament */
	private Player host;
	
	/** If the tournament has started */
	private boolean started;
	
	/** If the tournament has finished */
	private boolean finished;
	
	/** Type of mode the tournament is in */
	private int mode;
	
	/** Delay till tournament starts. */
	private int delay;
	
	/** Spawn locations */
	private Location[] spawns = new Location[] {
		new Location(3480, 3240, 4), //add more spawns here.
		new Location(3524, 3240, 4),
		new Location(3509, 3224, 4),
		new Location(3524, 3218, 4),
		new Location(3510, 3188, 4),
		new Location(3484, 3195, 4),
		new Location(3483, 3233, 4),
		new Location(3481, 3223, 4),
		new Location(3489, 3222, 4),
		new Location(3499, 3223, 4),
		new Location(3511, 3214, 4),
		new Location(3524, 3214, 4),
		new Location(3530, 3209, 4),
		new Location(3533, 3217, 4),
		new Location(3494, 3218, 4),
		new Location(3532, 3234, 4),
		new Location(3482, 3217, 4),
		new Location(3483, 3211, 4),
		new Location(3489, 3211, 4),
		new Location(3497, 3212, 4),
	};
	
	/**
	 * Constructor for PvPTournament
	 * @param mode
	 * 			the mode.
	 */
	public PvPTournament(final Player host, int mode, int delay) {
		this.mode = mode;
		this.delay = delay;
		this.host = host;
		this.players = new ArrayList<Player>();
		startLobbyTask();
	}
	
	/**
	 * Processes lobby shit.
	 */
	private final void startLobbyTask() {
		TaskQueue.queue(new Task(delay) {
			
			@Override
			public void execute() {
				if (players.size() < 2) {
					World.sendGlobalMessage("<img=1>[PvP Tournament] @red@The Tournament has been canceled due to not enough players.");
					players.forEach((player) -> {
						exit(player, false);
					});
					stop();
					return;
				}
				players.forEach((player) -> {
					player.setController(new PvPTournamentController());
				});
				started = true;
				stop();
			}

			@Override
			public void onStop() {}
			
		});
	}
	
	/**
	 * Handles giving the winner their reward/rewards.
	 * @param player
	 */
	public final void reward(final Player player) {
		player.getInventory().addOrCreateGroundItem(6199, 1, true);
	}
	
	/**
	 * Called when a player is attempting to join the tournament lobby.
	 * @param player
	 * 				the player joining.
	 */
	public static final void join(final Player player) {
		if (World.getTournament() == null) {
			player.send(new SendMessage("There is currently no active tournament."));
			return;
		}
		if (player.getEquipment().wearingGear()) {
			player.send(new SendMessage("You can't bring your own gear into this minigame."));
			return;
		}
		if (player.getInventory().containsItems()) {
			player.send(new SendMessage("You can't bring your own items into this minigame."));
			return;
		}
		if (player.getSkill().getCombatLevel() < 100) {
			player.send(new SendMessage("You can't join the tournament unless you're 100 combat."));
			return;
		}
		if (World.getTournament().hasStarted()) {
			player.send(new SendMessage("You can't join the tournament because it's already started."));
			return;
		}
		player.teleport(new Location(3345, 3213, 4));
		player.setController(new TrounamentLobbyController());
		World.getTournament().getPlayers().add(player);
		player.send(new SendMessage("You enter the Tournament Queue."));
	}
	
	/**
	 * Handles the event of a player killing another player.
	 * @param player
	 * 				the player.
	 */
	public final void handleKill(final Player killed, final Player player) {
		player.send(new SendMessage("You've killed " + killed.getDisplay() + ", they didn't stand a chance."));
		killed.send(new SendMessage("You've been slayen by " + player.getDisplay() + ", and lost the tournament."));
		restockGear(player);
		exit(killed, false);
		if (players.size() == 1) {
			World.sendGlobalMessage("<img=1>[PvP Tournament] @red@" + player.getDisplay() + " has won the Tournament!");
			exit(player, false);
			reward(player);
			finished = true;
			return;
		}
		player.send(new SendMessage("Your gear has been restocked for getting a kill."));
		World.sendRegionMessage("<img=1>[PvP Tournament]@red@ Theres now " + (players.size()) + " players left alive!", player.getCurrentRegion());
	}
	
	/**
	 * Called when a player exits the lobby.
	 * @param force TODO
	 */
	public final void exit(final Player player, boolean force) {
		if (!force)
			players.remove(player);
		player.getInventory().clear();
		player.getEquipment().clear();
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		player.teleport(new Location(3086, 3493, 0));
		player.getSkill().restore();
	}
	
	/**
	 * Forces the tournament to end.
	 * @param player
	 * 				the player ending the tournament.
	 */
	public final void forceEnd(final Player player) {
		if (World.getTournament().isFinished()) {
			player.send(new SendMessage("The tournament has already finished."));
			return;
		}
		players.forEach((p) -> {
			exit(p, true);
		});
		players.clear();
		finished = true;
		World.sendGlobalMessage("<img=1>[PvP Tournament]@red@ The Tournament has been forcefully ended by the host.");
	}
	
	/**
	 * Restocks a players gear.
	 * @param player
	 */
	public final void restockGear(final Player player) {
		player.getSkill().restore();
		switch (World.getTournament().getMode()) {
			case 0: //rune
				//Clear the equipment for the new items.
				player.getEquipment().clear();
				player.getEquipment().setSlot(new Item(4587), EquipmentConstants.WEAPON_SLOT, false); //dragon scimi
				player.getEquipment().setSlot(new Item(1127), EquipmentConstants.TORSO_SLOT, false); //rune platebody
				player.getEquipment().setSlot(new Item(1079), EquipmentConstants.LEGS_SLOT, false); //rune platelegs
				player.getEquipment().setSlot(new Item(11840), EquipmentConstants.BOOTS_SLOT, false); //dragon boots
				player.getEquipment().setSlot(new Item(3751), EquipmentConstants.HELM_SLOT, false); //berserker helm
				player.getEquipment().setSlot(new Item(8850), EquipmentConstants.SHIELD_SLOT, false); //rune defender
				player.getEquipment().setSlot(new Item(6570), EquipmentConstants.CAPE_SLOT, false); //fire cape
				player.getEquipment().setSlot(new Item(1704), EquipmentConstants.NECKLACE_SLOT, false); //amulet of glory
				player.getEquipment().setSlot(new Item(2550), EquipmentConstants.RING_SLOT, false); //ring of recoil
				player.getEquipment().setSlot(new Item(7462), EquipmentConstants.GLOVES_SLOT, true); //barrows glove, update last item
				//Clear the inventory for new items.
				player.getInventory().clear();	
				player.getInventory().add(new Item(2436)); //super attack
				player.getInventory().add(new Item(2440)); //super strength
				player.getInventory().add(new Item(2442)); //super defence
				player.getInventory().add(new Item(5698)); //dds p++
				for (int i = 0; i < 5; i++)
					player.getInventory().add(new Item(6685)); //saradomin brew
				for (int i = 0; i < 5; i++)
					player.getInventory().add(new Item(3024)); //super restore
				for (int i = 0; i < 14; i++) 
					player.getInventory().add(new Item(385)); //sharks
				break;
			case 1: //dh	
				break;
			case 2: //tribrid		
				break;
		}
	}
	
	/**
	 * Gets the tournament mode.
	 * @return
	 */
	public int getMode() {
		return mode;
	}
	
	/**
	 * Gets the host of the tournament.
	 * @return
	 */
	public Player getHost() {
		return host;
	}
	
	/**
	 * Checks if the tournament is finished.
	 * @return
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Checks if the tournament has started.
	 * @return
	 */
	public boolean hasStarted() {
		return started;
	}
	
	/**
	 * Gets a random location for a player to spawn.
	 * @return
	 */
	public Location getRandomSpawn() {
		return Utility.randomElement(spawns);
	}
	
	/**
	 * Gets the players list.
	 * @return
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Launches a PvP Tournament.
	 * @param player
	 * 			the player launching it.
	 * @param mode
	 * 			the mode for the tournament.
	 */
	public static void launch(final Player player, int mode, int delay) {
		if (mode < 0 || mode > 2) //change greater than value if more modes are added.
			mode = 0;
		if (delay < 30)
			delay = 30;
		if (World.getTournament() != null && !World.getTournament().isFinished()) {
			player.send(new SendMessage("You can't start a tournament when one is already in progress."));
			return;
		}
		World.setTournament(new PvPTournament(player, mode, delay));
		PvPTournament.join(player);
		World.sendGlobalMessage("<img=1>[PvP Tournament] A Tournament is starting soon! Join through the red portal at home.");
	}
	
}
