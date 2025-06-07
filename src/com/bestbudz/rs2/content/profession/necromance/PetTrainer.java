package com.bestbudz.rs2.content.profession.necromance;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class PetTrainer
{

	private final Stoner stoner;
	private final boolean[] activated;
	private final int[] drain;

	public PetTrainer(Stoner stoner) {
		this.stoner = stoner;
		// Keep minimal arrays for compatibility
		activated = new boolean[25]; // Enough for future expansion
		drain = new int[25];
	}

	// Pet trainer experience when any skill experience is gained
	public void onSkillExperienceGained(double baseExp) {
		if (stoner.getActivePets().isEmpty()) {
			return; // No pets out, no pet training exp
		}

		// Award pet training experience based on number of active pets
		int petCount = stoner.getActivePets().size();
		double petTrainerExp = baseExp * 0.1 * petCount; // 10% of base exp per pet

		stoner.getProfession().addExperience(5, petTrainerExp);
	}

	// No automatic drain - only manual drain when needed
	public void drain() {
		// Do nothing - no automatic draining
	}

	public void drain(long drainAmount) {
		long necromance = stoner.getProfession().getGrades()[5];
		if (drainAmount >= necromance) {
			stoner.getProfession().setGrade(5, 0);
			stoner.send(new SendMessage("Your pet training energy is exhausted; rest at home to recharge."));
			// Don't dismiss pets, just stop gaining experience
		} else {
			stoner.getProfession().deductFromGrade(5, drainAmount < 1 ? 1 : (int) Math.ceil(drainAmount));
		}
	}

	// Compatibility methods - keep existing interface
	public boolean clickButton(int button) {
		return false; // No special buttons yet
	}

	public void disable() {
		// Nothing to disable yet
	}

	public void forceToggle(Object necromance, boolean enabled) {
		// Compatibility method, does nothing for now
	}

	public boolean active(Object necromance) {
		return false; // No active behaviors yet
	}

	// Keep existing method signatures for compatibility
	public void doEffectOnHit(Object assaulted, Object hit) {
		// No special effects yet
	}

	public long getDamage(Object hit) {
		// No damage modification yet
		return 0;
	}

	public byte getHeadicon() {
		return -1; // No head icons yet
	}

	public boolean[] getQuickNecromances() {
		return new boolean[25]; // Empty array for compatibility
	}

	public void setQuickNecromances(boolean[] quickNecromances) {
		// Compatibility method
	}

	public boolean isQuickNecromance(Object necromance) {
		return false;
	}
}