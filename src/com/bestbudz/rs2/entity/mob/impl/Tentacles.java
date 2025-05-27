package com.bestbudz.rs2.entity.mob.impl;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Tentacles extends Mob {

	public Tentacles(Stoner stoner, Location location) {
	super(stoner, 5535, false, false, false, location);
	getCombat().setAssault(getOwner());
	getOwner().tentacles.add(this);
	}

	@Override
	public void hit(Hit hit) {

	if (isDead() || getOwner() == null) {
		return;
	}

	super.hit(hit);

	}

}
