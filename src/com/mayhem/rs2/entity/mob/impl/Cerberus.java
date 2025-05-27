package com.mayhem.rs2.entity.mob.impl;

import java.util.ArrayList;
import java.util.List;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;

public class Cerberus extends Mob {
	

	/**
	 * Start of Cerberus
	 * @param player
	 * @param location
	 */
	public Cerberus(Player player, Location location) {
		super(player, 5862, false, false, false, location);
		if (getOwner() != null) {
			getCombat().setAttack(getOwner());
			getFollowing().setIgnoreDistance(true);
			getUpdateFlags().faceEntity(getOwner().getIndex());
		}
	}

	/**
	 * Cerberus' GFX
	 */
	private List<Mob> ghosts = new ArrayList<>();
	public static final int CERBERUS = 5862;
	public static final int SPECIAL_CHANCE = 20;
	private boolean rocks = false;
	
	private boolean spawnGhosts = true;
	
	@Override
	public int getRespawnTime() {
		return 5;
	}
	
	@Override
	public void onHit(Entity entity, Hit hit) {
	if (entity == null) {
		return;
	}
	int random = Utility.random(15);
	if (random == 1) {
		initRocks();
	}
	}
	@Override
	public void hit(Hit hit) {
	if (getOwner() == null) {
		return;
	}
		if (isDead()) {
			ghosts.clear();
			return;
		}
		super.hit(hit);
	}
	/***
	 * 
	 * @param Checks For Damage inside the Location
	 */
	public void checkForDamage(Location a) {
		for (Player player : getCombatants()) {
			Location b = player.getLocation();
			player.getLocation();
			player.getLocation();
			if ((Math.abs(a.getX() - b.getX()) <= 1) && (Math.abs(a.getY() - b.getY()) <= 1))
				player.hit(new Hit(Utility.randomNumber(20)));
		}
	}

	@Override
	public void doAliveMobProcessing() {
		if ((getCombat().getAttacking() != null) && (!rocks) && (Utility.randomNumber(20) == 0)) {
			rocks = true;
			initRocks();
		}
			if ((getCombat().getAttacking() != null) && (!spawnGhosts) && (Utility.randomNumber(20) == 0)) {
				spawnGhosts = true;
				isSpawnGhosts();
			}
		}
	
	
	@Override
	public void onDeath() {
		setSpawnGhosts(false);
		for (Mob ghosts : ghosts) {
			if (!ghosts.isDead()) {
				ghosts.remove();
			}
		}
		ghosts.clear();
	}
	
	public boolean isSpawnGhosts() {
	return spawnGhosts;
	}	
	
	public void setSpawnGhosts(boolean spawnGhosts) {
	this.spawnGhosts = spawnGhosts;
	}
	
	public void spawnGhosts() {
	setSpawnGhosts(true);
	for (int index = 0; index < 2; index++) {
		Mob mob = new Mob(5867, true, new Location(1239, 1256, getOwner().getZ()));
		Mob mob1 = new Mob(5869, true, new Location(1240, 1256, getOwner().getZ()));
		Mob mob2 = new Mob(5868, true, new Location(1241, 1256, getOwner().getZ()));
		mob.getUpdateFlags().faceEntity(getIndex());
		mob.setFreeze(9999);
		
		getUpdateFlags().sendForceMessage("Aaaarrrroooo");
		
		TaskQueue.queue(new Task(1) {
		byte Stage = 0;
		
		@Override
		public void execute() {
			if (Stage == 0) {
				if (!mob.isDead()) {
					ghosts.add(mob);
					mob.getCombat().setAttack(getOwner());
					mob1.getAttackType();
				}
			} else if (Stage == 1) {
				if (!mob1.isDead()) {
					ghosts.add(mob1);
					mob1.getCombat().setAttack(getOwner());
					mob1.getAttackType();
				}
				
			} else if (Stage == 1) {
				if (!mob2.isDead()) {
					ghosts.add(mob2);
					mob2.getCombat().setAttack(getOwner());
					mob2.getAttackType();
				}
			}
			if ((this.Stage = (byte) (Stage + 1)) == 3) {
				spawnGhosts = false;
				ghosts.clear();
				stop();
			}
		}
			@Override
			public void onStop() {
			}
			});
		}
	}
	
			
	public void initRocks() {
		final Location a = new Location(1237, 1253, getOwner().getZ());
		final Location b = new Location(1243, 1253, getOwner().getZ());
		final Location c = new Location(1242, 1250, getOwner().getZ());
		final Location d = new Location(1238, 1250, getOwner().getZ());

		TaskQueue.queue(new Task(1) {
		byte stage = 0;

		
		@Override
		public void execute() {
			if (stage == 0) {
				World.sendStillGraphic(1247, 0, a);
				checkForDamage(a);
			} else if (stage == 1) {
				World.sendStillGraphic(1247, 0, b);
				checkForDamage(b);
			} else if (stage == 2) {
				World.sendStillGraphic(1247, 0, c);
				checkForDamage(c);
			} else if (stage == 4) {
				World.sendStillGraphic(1247, 0, d);
				checkForDamage(d);
			}
			if ((this.stage = (byte) (stage + 1)) == 5) {
				rocks = false;
				stop();
			}
		}
			@Override
			public void onStop() {
			}
			});
		}
}