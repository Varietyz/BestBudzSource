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
		this.allergySystem = stoner.getAllergySystem();
	}

	public boolean consumeFood(int id, int slot, Item consumable) {

		specialEffects.applySpecialFoodEffects(consumable);

		FoodDefinition food = Item.getFoodDefinition(id);
		if (food == null) {
			return false;
		}

		if (!consumables.canEat() || !stoner.getController().canEat(stoner)) {
			return true;
		}

		AllergySystem.AllergyType allergy = allergySystem.getAllergyFor(id);
		if (allergy != null && allergySystem.shouldTriggerAllergy(allergy)) {
			int foodHealth = calculateFoodHealth(id, food);

			handleFoodConsumption(slot, consumable, food);

			allergySystem.applyAllergyReaction(allergy, foodHealth);

			stoner.getClient().queueOutgoingPacket(new SendSound(317, 1, 2));
			stoner.getUpdateFlags().sendAnimation(829, 0);

			expCalculator.addFoodExperience(Math.max(1, foodHealth / 2));

			scheduleFoodCooldown(id, food);
			return true;
		}

		int foodHealth = calculateFoodHealth(id, food);
		long targetHeal = calculateTargetHeal(id, foodHealth);

		boolean preserveItem = allergySystem.shouldPreserveItem(allergySystem.getConsumerLevel());

		if (!preserveItem) {
			handleFoodConsumption(slot, consumable, food);
		} else {

			stoner.send(new SendMessage("@gre@Your Consumer mastery preserves the " + food.getName() + "!"));
		}

		applyFoodEffects(food, foodHealth, targetHeal);

		allergySystem.applyConsumerMastery(foodHealth, allergySystem.getConsumerLevel());

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

		if (id == 15272) {
			baseHeal = (int) Math.round(stoner.getMaxGrades()[3] * 0.23D);
		}

		boolean canEnhance = (advancements >= 1) || (consumerLevel >= 105);

		if (canEnhance) {
			double enhancement = 1.0;

			if (advancements >= 1) enhancement += 0.05;
			if (advancements >= 2) enhancement += 0.05;
			if (advancements >= 3) enhancement += 0.05;
			if (advancements >= 4) enhancement += 0.05;
			if (advancements >= 5) enhancement += 0.05;

			enhancement += (consumerLevel / 2100.0);

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
