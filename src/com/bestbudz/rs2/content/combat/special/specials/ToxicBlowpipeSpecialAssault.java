package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles the Toxic blowpipe special assault
 * 
 * @author Jaybane
 *
 */
public class ToxicBlowpipeSpecialAssault implements Special {

	@Override
	public boolean checkRequirements(Stoner stoner) {
	if (stoner.getToxicBlowpipe().getBlowpipeAmmo() == null) {
		return false;
	}
	if (stoner.getToxicBlowpipe().getBlowpipeAmmo().getAmount() <= 0) {
		return false;
	}
	if (stoner.getToxicBlowpipe().getBlowpipeCharge() <= 0) {
		return false;
	}
	return true;
	}

	@Override
	public int getSpecialAmountRequired() {
	return 50;
	}

	@Override
	public void handleAssault(Stoner stoner) {

	}
}