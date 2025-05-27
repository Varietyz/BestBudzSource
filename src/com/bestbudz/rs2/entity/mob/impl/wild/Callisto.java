package com.bestbudz.rs2.entity.mob.impl.wild;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles the Callisto boss
 * 
 * @author Jaybane
 *
 */
public class Callisto extends Mob {

	/**
	 * Callisto
	 */
	public Callisto() {
	super(6609, true, new Location(3295, 3851, 0));
	}

	/**
	 * Handles Callisto hitting entity
	 */
	@Override
	public void onHit(Entity entity, Hit hit) {
	if (entity != null && !entity.isNpc()) {
		if (entity.getStoner().isStunned()) {
			return;
		}
		int random = Utility.random(10);
		if (random == 1) {
			knockBack(entity.getStoner());
		}
	}
	}

	/**
	 * Handles Callisto's knock back effect
	 * 
	 * @param stoner
	 */
	private void knockBack(Entity stoner) {
	stoner.stun(2);
	stoner.hit(new Hit(2));
	stoner.getUpdateFlags().sendGraphic(new Graphic(80, true));
	stoner.getUpdateFlags().sendAnimation(new Animation(3170));
	stoner.getStoner().send(new SendMessage("Callisto's roar sends you backwards."));
	stoner.getStoner().teleport(new Location(stoner.getX() + Utility.random(3), stoner.getY() - Utility.random(5), 0));
	}

}
