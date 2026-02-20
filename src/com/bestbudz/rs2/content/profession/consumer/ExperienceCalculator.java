package com.bestbudz.rs2.content.profession.consumer;

import com.bestbudz.rs2.entity.stoner.Stoner;

public class ExperienceCalculator {

	private final Stoner stoner;

	public ExperienceCalculator(Stoner stoner) {
		this.stoner = stoner;
	}

	public void addFoodExperience(int foodHealth) {
		stoner.getProfession().addExperience(20, 100 * foodHealth);
	}

	public void addPotionExperience(int potionId) {
		stoner.getProfession().addExperience(20, potionId + 1000);
	}

	public void addSpecialExperience(int amount) {
		stoner.getProfession().addExperience(20, amount);
	}
}