package com.mayhem.rs2.content.minigames.miniraid;

import java.util.List;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.content.minigames.miniraid.MainMonster;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.VirtualMobRegion;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.ControllerManager;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Mini Raid game
 * @author Mod Divine
 *
 */
public class MiniRaidGame {

	/**
	 * List of players
	 */
	private final List<Player> players;
	
	/**
	 * Height
	 */
	private final int z;
	
	/**
	 * Region
	 */
	private final VirtualMobRegion region;

	/**
	 * Array of Main Monsters
	 */
	private final MainMonster[] mainmonsters;
	
	/**
	 * monster damage key
	 */
	public static final String MONSTER_DAMAGE_KEY = "monsterdamagekey";
	
	/** 
	 * mini game key
	 */
	public static final String MINIRAID_GAME_KEY = "miniraidgamekey";

	/**
	 * Game time
	 */
	private int time = 2500;

	/**
	 * Check if game has ended
	 */
	private boolean ended = false;

	/**
	 * Mini Raid game
	 * @param players
	 * @param count
	 */
	public MiniRaidGame(List<Player> players, int count) {
		this.players = players;
		z = (count << 2);
		region = new VirtualMobRegion();
		mainmonsters = new MainMonster[] { new MainMonster(this, MiniRaidConstants.MAIN_MONSTER_IDS[0], MiniRaidConstants.MAIN_MONSTER_SPAWN_LOCATIONS[0], z) };
		//spawn a vespula without this, just spawn it normally and see if its agro
		init();
	}

	/**
	 * Ends the game
	 * @param success
	 */
	public void end(boolean success) {
		ended = true;
		
		if (success) {
			World.sendGlobalMessage("<img=8> @red@" + players.size() + " players have finished the Trial of Flames!");
		} 

		for (MainMonster i : mainmonsters) {
			i.remove();
		}


		for (Player p : players) {
			p.teleport(new Location(3086, 3493));

			p.setController(ControllerManager.DEFAULT_CONTROLLER);

			p.getCombat().reset();
			p.getMagic().setVengeanceActive(false);
			p.resetLevels();
			p.curePoison(0);

			if (success) {
				if (p.getAttributes().get(MONSTER_DAMAGE_KEY) != null && p.getAttributes().getInt(MONSTER_DAMAGE_KEY) >= 50) {
					DialogueManager.sendNpcChat(p, 306, Emotion.HAPPY_TALK, "You have managed to complete the Trial!", "Your Contribution: @red@" + p.getAttributes().getInt(MONSTER_DAMAGE_KEY) + "</col>.");
					p.getInventory().addOrCreateGroundItem(13307, p.getAttributes().getInt(MONSTER_DAMAGE_KEY) * 5, true);
					Rewards.getRewards(p);
					
				} else {
					DialogueManager.sendNpcChat(p, 306, Emotion.CALM, "You were successful but did not contribute enough", "Try doing at least @red@50</col> damage.");
				}
			} 

			p.getAttributes().remove(MONSTER_DAMAGE_KEY);
			p.getAttributes().remove(MINIRAID_GAME_KEY);
		}

		for (MainMonster i : mainmonsters) {
			i.cleanup();
		}


		MiniRaid.onGameEnd(this);
	}

	/**
	 * Gets the attackers
	 * @param p
	 * @return
	 */
	public int getAttackers(Player p) {
		int i = 0;

		for (MainMonster k : mainmonsters) {
			for (Mob j : k.getMonsters()) {
				if (j.getCombat().getAttacking() != null && j.getCombat().getAttacking().equals(p)) {
					i++;
				}
			}
		}

		return i;
	}

	/**
	 * Gets the players
	 * @return
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Gets the region
	 * @return
	 */
	public VirtualMobRegion getVirtualRegion() {
		return region;
	}
	
	/**
	 * Gets the height
	 * @return
	 */
	public int getZ() {
		return z;
	}
	
	/**
	 * Gets if ended
	 * @return
	 */
	public boolean hasEnded() {
		return ended;
	}

	/**
	 * Initialize
	 */
	public void init() {
		for (Player p : players) {
			p.teleport(new Location(3014 + Utility.randomNumber(1), 5242 + Utility.randomNumber(1), z));
			p.getAttributes().set(MONSTER_DAMAGE_KEY, 0);
			p.getAttributes().set(MINIRAID_GAME_KEY, this);
			p.setController(ControllerManager.MINIRAID_CONTROLLER);

			p.getSpecialAttack().setSpecialAmount(100);

			DialogueManager.sendNpcChat(p, 306, Emotion.CALM, "Let the first Trials begin!", "Goodluck adventurer!");
		}
		
		time = 2500;
	}

	/**
	 * Game process
	 */
	public void process() {
		
		time--;

		if (time <= 0) {
			end(false);
			return;
		}

		if (!mainmonsters[0].isActive()) {
			end(true);
		}
		
		
		for (Player p : players) {
			p.getClient().queueOutgoingPacket(new SendString(Utility.getFormattedTime(time) + "", 31117));

			for (int i = 0; i < 1; i++) {
				boolean dead = mainmonsters[i].isDead();
				p.getClient().queueOutgoingPacket(new SendString((dead ? "@red@Dead" : "" + mainmonsters[i].getLevels()[Skills.HITPOINTS]), 31111 + i));
			}

			if (p.getAttributes().get(MONSTER_DAMAGE_KEY) != null) {
				int damage = p.getAttributes().getInt(MONSTER_DAMAGE_KEY);
				p.getClient().queueOutgoingPacket(new SendString((damage >= 50 ? "" : "@red@") + p.getAttributes().getInt(MONSTER_DAMAGE_KEY), 31116));
			}
		}
	}

	/**
	 * Removes player from game
	 * @param p
	 */
	public void remove(Player p) {
		players.remove(p);

		if (players.size() == 0) {
			for (MainMonster i : mainmonsters) {
				i.cleanup();
				MiniRaid.onGameEnd(this);
			}


			MiniRaid.onGameEnd(this);
		}
	}
	
}
