package com.mayhem.rs2.entity.mob.impl;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;

/**
 * @author Andy || ReverendDread Mar 23, 2017
 */
public class Skotizo extends Mob {

	//Fields
	private Mob[] minions = new Mob[3]; //The minions
	private boolean spawnedMinions;

	/**
	 * Constructs a new Skotizo Entity
	 * @param location 
	 * @param player 
	 * @param loc {@link Location} Entity's location.
	 * @param target {@link Player} The target.
	 */
	public Skotizo(Player player, Location location) {
		super(player, 7286, false, false, false, location);
		if (getOwner() != null) {
			getCombat().setAttack(getOwner());
			getFollowing().setIgnoreDistance(true);
			getUpdateFlags().faceEntity(getOwner().getIndex());
		}
	}
	
	@Override
	public void hit(Hit hit) {
		if (!isDead() && getLevels()[3] <= 225 && !spawnedMinions) {
			getUpdateFlags().sendForceMessage("Gar mulno ful taglo!");
			spawnedMinions = true;
			spawnMinions();
		}
		super.hit(hit);
	}
	
	/**
	 * Handles minion spawning.
	 */
	private void spawnMinions() {
		if (!spawnedMinions) {
			System.err.println("Tried spawning skitzo minions again.");
			return;
		}
		if (Utility.random(2) == 1) {
			minions[0] = new ReanimatedDemonSpawn(getLocation(), getOwner(), this);
			minions[1] = new ReanimatedDemonSpawn(getLocation(), getOwner(), this);
			minions[2] = new ReanimatedDemonSpawn(getLocation(), getOwner(), this);
		} else {
			minions[0] = new DarkAnkou(getLocation(), getOwner(), this);
		}
	}
	
	@Override
	public void onDeath() {
		minions = null;
		spawnedMinions = false;
	}
	
}
