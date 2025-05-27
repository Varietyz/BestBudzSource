package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.impl.Sagittarius;
import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles the Armadyl crossbow special assault
 * 
 * @author Jaybane
 *
 */
public class ArmadylCrossbowSpecialAssault implements Special {

	@Override
	public boolean checkRequirements(Stoner stoner) {
	return true;
	}

	@Override
	public int getSpecialAmountRequired() {
	return 40;
	}

	@Override
	public void handleAssault(Stoner stoner) {
	Sagittarius range = stoner.getCombat().getSagittarius();

	range.setAnimation(new Animation(4230, 0));
	range.setProjectile(new Projectile(301));
	range.setStartGfxOffset((byte) 1);
	range.getProjectile().setDelay(35);
	}

}
