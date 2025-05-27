package com.mayhem.rs2.content.minigames.miniraid2;

import java.util.List;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.content.minigames.miniraid2.MainMonster2;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.VirtualMobRegion;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.ControllerManager;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Mini Raid 2 game
 * @author Mod Divine
 *
 */
public class MiniRaid2Game {

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
	private final MainMonster2[] mainmonsters2;
	
	/**
	 * monster damage key
	 */
	public static final String MONSTER_DAMAGE_KEY = "monsterdamagekey";
	
	/** 
	 * mini game key
	 */
	public static final String MINIRAID2_GAME_KEY = "miniraid2gamekey";

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
	public MiniRaid2Game(List<Player> players, int count) {
		this.players = players;
		z = (count << 2);
		region = new VirtualMobRegion();
		mainmonsters2 = new MainMonster2[] { new MainMonster2(this, MiniRaid2Constants.MAIN_MONSTER2_IDS[0], MiniRaid2Constants.MAIN_MONSTER2_SPAWN_LOCATIONS[0], z) };
		init();
	}

	/**
	 * Ends the game
	 * @param success
	 */
	public void end(boolean success) {
		ended = true;
		
		if (success) {
			World.sendGlobalMessage("<img=8> @red@" + players.size() + " players have finished the Custom Raid 2!");
		} 

		for (MainMonster2 i : mainmonsters2) {
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
					DialogueManager.sendNpcChat(p, 306, Emotion.HAPPY_TALK, "You have managed to complete the Raid!", "Your Contribution: @red@" + p.getAttributes().getInt(MONSTER_DAMAGE_KEY) + "</col>.");
					p.getInventory().addOrCreateGroundItem(13307, p.getAttributes().getInt(MONSTER_DAMAGE_KEY) * 5, true);
					Rewards2.getRewards(p);
					
				} else {
					DialogueManager.sendNpcChat(p, 306, Emotion.CALM, "You were successful but did not contribute enough", "Try doing at least @red@50</col> damage.");
				}
			} 

			p.getAttributes().remove(MONSTER_DAMAGE_KEY);
			p.getAttributes().remove(MINIRAID2_GAME_KEY);
		}

		for (MainMonster2 i : mainmonsters2) {
			i.cleanup();
		}


		MiniRaid2.onGameEnd(this);
	}

	/**
	 * Gets the attackers
	 * @param p
	 * @return
	 */
	public int getAttackers(Player p) {
		int i = 0;

		for (MainMonster2 k : mainmonsters2) {
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
			p.teleport(new Location(1680 + Utility.randomNumber(1), 9901 + Utility.randomNumber(1), z));
			p.getAttributes().set(MONSTER_DAMAGE_KEY, 0);
			p.getAttributes().set(MINIRAID2_GAME_KEY, this);
			p.setController(ControllerManager.MINIRAID2_CONTROLLER);

			p.getSpecialAttack().setSpecialAmount(100);

			DialogueManager.sendNpcChat(p, 306, Emotion.CALM, "Let the second Trials begin!", "Goodluck adventurer!");
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


		if (!mainmonsters2[0].isActive()) {
			end(true);
		}
		
		
		for (Player p : players) {
			p.getClient().queueOutgoingPacket(new SendString(Utility.getFormattedTime(time) + "", 31117));

			for (int i = 0; i < 1; i++) {
				boolean dead = mainmonsters2[i].isDead();
				p.getClient().queueOutgoingPacket(new SendString((dead ? "@red@Dead" : "" + mainmonsters2[i].getLevels()[Skills.HITPOINTS]), 31111 + i));
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
			for (MainMonster2 i : mainmonsters2) {
				i.cleanup();
				MiniRaid2.onGameEnd(this);
			}


			MiniRaid2.onGameEnd(this);
		}
	}
	
}
