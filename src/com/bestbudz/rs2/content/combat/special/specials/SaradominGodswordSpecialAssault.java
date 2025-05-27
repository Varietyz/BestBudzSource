package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles the Saradomin godsword special assault
 * 
 * @author Jaybane
 *
 */
public class SaradominGodswordSpecialAssault implements Special {

	/**
	 * Checks if stoner meets requirements
	 * 
	 * @param stoner
	 */
	@Override
	public boolean checkRequirements(Stoner stoner) {
	return true;
	}

	/**
	 * Special assault amount being used
	 */
	@Override
	public int getSpecialAmountRequired() {
	return 50;
	}

	/**
	 * Handles the assault special
	 * 
	 * @param stoner
	 */
	@Override
	public void handleAssault(Stoner stoner) {
	stoner.getCombat().getMelee().setAnimation(new Animation(7058, 0));
	stoner.getUpdateFlags().sendGraphic(Graphic.highGraphic(1209, 0));
	}
}
