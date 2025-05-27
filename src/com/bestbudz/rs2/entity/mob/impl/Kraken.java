package com.bestbudz.rs2.entity.mob.impl;

import java.text.SimpleDateFormat;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles the Kraken boss
 * 
 * @author Jaybane
 *
 */
public class Kraken extends Mob {

	public Kraken(Stoner stoner, Location location) {
	super(stoner, 494, false, false, false, location);
	getCombat().setAssault(getOwner());
	TIME = System.currentTimeMillis();
	}

	private long TIME;

	@Override
	public void hit(Hit hit) {

	if (isDead() || getOwner() == null) {
		return;
	}

	super.hit(hit);

	}

	@Override
	public void onDeath() {
	for (Mob mobs : getOwner().tentacles) {
		if (!mobs.isDead()) {
			mobs.remove();
		}
	}
	getOwner().tentacles.clear();
	getOwner().whirlpoolsHit = 0;
	getOwner().send(new SendMessage("Fight duration: @red@" + new SimpleDateFormat("m:ss").format(System.currentTimeMillis() - TIME) + "</col>."));
	}

}
