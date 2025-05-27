package com.mayhem.rs2.entity.mob.impl;

import java.util.ArrayList;
import java.util.List;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.content.combat.Hit.HitTypes;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;


/**
 * Handles the Callisto boss
 * 
 *
 */
public class Shaman extends Mob {
	
	private final Player bind;
	
	/**
	 * Shaman
	 */
		public Shaman(Player player, Location location, Player bind) {
			super(player, 6766, false, false, false, location);
			this.bind = bind;
		if (getOwner() != null) {
			getCombat().setAttack(getOwner());
			getFollowing().setIgnoreDistance(true);
			getUpdateFlags().faceEntity(getOwner().getIndex());
	}
}

		
		public static long timer;
		
		private List<Mob> spawn = new ArrayList<>();
		

	/**
	 * Handles Spinning attack
	 */
		@Override
		public void onHit(Entity entity, Hit hit) {
		if (entity == null) {
			return;
		}
			int random = Utility.random(5);
			if (random == 1) {
			SpinAttack(entity.getPlayer());
			}
			if (random == 2) {
				SpitAttack(entity.getPlayer());

			}
			if (random == 3) {
				jumpAttack(entity.getPlayer());
			}
			if (random == 4) {
				spawnPets(entity.getPlayer(), getPlayer());
			}
			if(isDead()) {
				spawn.remove(spawn);
			}
			}
			
		
		public Entity getAttacking() {
		return attacking;
	}


	public void setAttacking(Entity attacking) {
		this.attacking = attacking;
	}


		@Override
		public int getRespawnTime() {
			return 60;
		}
	
		@Override
		public void onDeath() {
			for (Mob spawn : spawn) {
				if (!spawn.isDead()) {
					spawn.remove();
				}
			}
			spawn.clear();
		}
		
		public boolean isSpawnSpawns() {
		return spawnSpawns;
	}

	public void setSpawnSpawns(boolean spawnGuardians) {
		this.spawnSpawns = spawnGuardians;
	}
		
		private boolean spawnSpawns = false;

		private Entity attacking;
		
		public boolean areCoresDead() {
		if (spawn == null) {
			return true;
		}

		for (Mob mob : spawn) {
			if (!mob.isDead()) {
				return false;
			}
		}

		return true;
	}
				
	
	/**
	 * Handles Shamans spin
	 * @param player
	 */
	private void SpinAttack(Entity player) {
		getUpdateFlags().sendAnimation(new Animation(7192));						
	}

	
	private void SpitAttack(Entity player) {
			int success = Utility.random(3);
			getUpdateFlags().sendAnimation(new Animation(7193));	
			player.getUpdateFlags().sendGraphic(new Graphic(1294));
			if (success == 1) {
					player.hit(new Hit(30, HitTypes.POISON));
				}
			}

	
		private void jumpAttack(Entity player) {
			setAttacking(getCombat().getAttacking());
			getUpdateFlags().sendAnimation(new Animation(7152));
		}
		
		
		private void spawnPets(Entity entity, Player player) {
		getUpdateFlags().sendAnimation(new Animation(7157));
		setTakeDamage(true);
		for (int index = 0; index < 1; index++) {
		Mob mob = new Mob(6768, true, new Location(getX() + index, getY(), getZ()));
		mob.getFollowing().setFollow(bind);
		mob.getUpdateFlags().faceEntity(getIndex());
		spawn.add(mob);
		
		timer++;
		if (timer == 20) {
			mob.getUpdateFlags().sendGraphic(new Graphic (1227));
			//setVisible(false);

			}
		
		}
		}
}

		
	

