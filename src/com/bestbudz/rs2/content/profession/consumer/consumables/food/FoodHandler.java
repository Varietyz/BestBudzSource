package com.bestbudz.rs2.content.profession.consumer.consumables.food;

import com.bestbudz.core.definitions.FoodDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.profession.consumer.ExperienceCalculator;
import com.bestbudz.rs2.content.profession.consumer.consumables.SpecialEffects;
import com.bestbudz.rs2.content.profession.consumer.consumables.Consumables;
import com.bestbudz.rs2.content.profession.consumer.allergies.AllergySystem;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class FoodHandler {

	private final Stoner stoner;
	private final Consumables consumables;
	private final ExperienceCalculator expCalculator;
	private final SpecialEffects specialEffects;
	private final AllergySystem allergySystem;

	public FoodHandler(Stoner stoner, Consumables consumables) {
		this.stoner = stoner;
		this.consumables = consumables;
		this.expCalculator = new ExperienceCalculator(stoner);
		this.specialEffects = new SpecialEffects(stoner);
		this.allergySystem = stoner.getAllergySystem(); // Assume this exists on Stoner
	}

	public boolean consumeFood(int id, int slot, Item consumable) {
		// Apply special effects first
		specialEffects.applySpecialFoodEffects(consumable);

		FoodDefinition food = Item.getFoodDefinition(id);
		if (food == null) {
			return false;
		}

		if (!consumables.canEat() || !stoner.getController().canEat(stoner)) {
			return true;
		}

		// Check for allergies BEFORE consumption
		AllergySystem.AllergyType allergy = allergySystem.getAllergyFor(id);
		if (allergy != null && allergySystem.shouldTriggerAllergy(allergy)) {
			int foodHealth = calculateFoodHealth(id, food);

			// Handle item consumption (still consume the item)
			handleFoodConsumption(slot, consumable, food);

			// Apply allergic reaction instead of normal effects
			allergySystem.applyAllergyReaction(allergy, foodHealth);

			// Still play eating animation/sound but with different message
			stoner.getClient().queueOutgoingPacket(new SendSound(317, 1, 2));
			stoner.getUpdateFlags().sendAnimation(829, 0);

			// Add Consumer experience for attempting consumption
			expCalculator.addFoodExperience(Math.max(1, foodHealth / 2));

			scheduleFoodCooldown(id, food);
			return true;
		}

		// Normal consumption path
		int foodHealth = calculateFoodHealth(id, food);
		long targetHeal = calculateTargetHeal(id, foodHealth);

		// Check for item preservation (Consumer mastery)
		boolean preserveItem = allergySystem.shouldPreserveItem(allergySystem.getConsumerLevel());

		if (!preserveItem) {
			handleFoodConsumption(slot, consumable, food);
		} else {
			// Item preserved, just play effects
			stoner.send(new SendMessage("@gre@Your Consumer mastery preserves the " + food.getName() + "!"));
		}

		applyFoodEffects(food, foodHealth, targetHeal);

		// Apply Consumer mastery bonuses
		allergySystem.applyConsumerMastery(foodHealth, allergySystem.getConsumerLevel());

		// Build resistance if player has this allergy
		if (allergy != null) {
			allergySystem.handleAllergyExposure(allergy);
		}

		scheduleFoodCooldown(id, food);

		return true;
	}

	private int calculateFoodHealth(int id, FoodDefinition food) {
		int consumerLevel = allergySystem.getConsumerLevel();
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];
		int baseHeal = food.getHeal();

		// Special case for Saradomin brew equivalent
		if (id == 15272) {
			baseHeal = (int) Math.round(stoner.getMaxGrades()[3] * 0.23D);
		}

		// Consumer skill enhances food effectiveness - level OR advancement based
		boolean canEnhance = (advancements >= 1) || (consumerLevel >= 105); // Tier 1 advancement OR 25% of 420

		if (canEnhance) {
			double enhancement = 1.0;

			// Advancement-based enhancement (permanent)
			if (advancements >= 1) enhancement += 0.05; // Tier 1: 5% bonus
			if (advancements >= 2) enhancement += 0.05; // Tier 2: +5% more (10% total)
			if (advancements >= 3) enhancement += 0.05; // Tier 3: +5% more (15% total)
			if (advancements >= 4) enhancement += 0.05; // Tier 4: +5% more (20% total)
			if (advancements >= 5) enhancement += 0.05; // Tier 5: +5% more (25% total, max)

			// Level-based enhancement (still scales with current level)
			enhancement += (consumerLevel / 2100.0); // Up to 20% bonus at level 420

			baseHeal = (int) (baseHeal * enhancement);
		}

		return baseHeal;
	}

	private long calculateTargetHeal(int id, int foodHealth) {
		long heal = stoner.getProfession().getGrades()[3] + foodHealth;

		if (heal > stoner.getMaxGrades()[3]) {
			if (id != 15272) {
				heal = stoner.getMaxGrades()[3];
			} else {
				heal = stoner.getMaxGrades()[3] + 10;
			}
		}
		return heal;
	}

	private void handleFoodConsumption(int slot, Item consumable, FoodDefinition food) {
		if (food.getReplaceId() == -1 && consumable.getAmount() <= 1) {
			stoner.getBox().clear(slot);
		} else if (food.getReplaceId() == -1 && consumable.getAmount() > 1) {
			consumable.remove(1);
			stoner.getBox().update();
		} else {
			stoner.getBox().setId(slot, food.getReplaceId());
		}
	}

	private void applyFoodEffects(FoodDefinition food, int foodHealth, long targetHeal) {
		stoner.getClient().queueOutgoingPacket(new SendSound(317, 1, 2));
		stoner.getUpdateFlags().sendAnimation(829, 0);

		expCalculator.addFoodExperience(foodHealth);

		if (stoner.getProfession().getGrades()[3] < targetHeal) {
			stoner.getProfession().setGrade(3, targetHeal);
		}

		stoner.getClient().queueOutgoingPacket(new SendMessage(food.getMessage()));
		stoner.getCombat().reset();

		if (stoner.getCombat().getAssaultTimer() > 0) {
			stoner.getCombat().increaseAssaultTimer(food.getDelay());
		}
	}

	private void scheduleFoodCooldown(int id, FoodDefinition food) {
		if (id != 3144) {
			consumables.setCanEat(false);
		}

		TaskQueue.queue(
			new Task(
				stoner,
				food.getDelay(),
				false,
				Task.StackType.STACK,
				Task.BreakType.NEVER,
				TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					consumables.setCanEat(true);
					stop();
				}

				@Override
				public void onStop() {}
			});
	}
}