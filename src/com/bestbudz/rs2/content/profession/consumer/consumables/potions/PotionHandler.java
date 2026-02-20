package com.bestbudz.rs2.content.profession.consumer.consumables.potions;

import com.bestbudz.core.definitions.PotionDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.profession.consumer.ExperienceCalculator;
import com.bestbudz.rs2.content.profession.consumer.consumables.SpecialEffects;
import com.bestbudz.rs2.content.profession.consumer.consumables.Consumables;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class PotionHandler {

	private final Stoner stoner;
	private final Consumables consumables;
	private final ExperienceCalculator expCalculator;
	private final SpecialEffects specialEffects;

	public PotionHandler(Stoner stoner, Consumables consumables) {
		this.stoner = stoner;
		this.consumables = consumables;
		this.expCalculator = new ExperienceCalculator(stoner);
		this.specialEffects = new SpecialEffects(stoner);
	}

	public boolean consumePotion(int id, int slot, Item consumable) {
		PotionDefinition potions = Item.getPotionDefinition(id);
		if (potions == null) {
			return false;
		}

		if (!stoner.getController().canDrink(stoner) || !consumables.canDrink()) {
			return true;
		}

		consumables.setCanDrink(false);

		applyPotionEffects(potions);
		handlePotionConsumption(slot, consumable, potions);
		specialEffects.applySpecialPotionEffects(id);
		applyProfessionEffects(potions);
		schedulePotionCooldown();

		return true;
	}

	private void applyPotionEffects(PotionDefinition potions) {
		String name = potions.getName();

		stoner.getUpdateFlags().sendAnimation(829, 0);
		stoner.getClient().queueOutgoingPacket(new SendSound(334, 1, 0));

		String message = "You gulp some " + name + ".";
		expCalculator.addPotionExperience(potions.getId());
		stoner.getClient().queueOutgoingPacket(new SendMessage(message));
		stoner.getCombat().reset();
	}

	private void handlePotionConsumption(int slot, Item consumable, PotionDefinition potions) {
		if (potions.getReplaceId() == -1 && consumable.getAmount() <= 1) {
			stoner.getBox().clear(slot);
		}
		stoner.getBox().setId(slot, potions.getReplaceId());
	}

	private void applyProfessionEffects(PotionDefinition potions) {
		PotionDefinition.ProfessionData[] professionData = potions.getProfessionData();

		if (professionData != null && professionData.length > 0) {
			for (PotionDefinition.ProfessionData data : professionData) {
				applyProfessionEffect(data, potions);
			}
		}
	}

	private void applyProfessionEffect(PotionDefinition.ProfessionData data, PotionDefinition potions) {
		int professionId = data.getProfessionId();
		int add = data.getAdd();
		double modifier = data.getModifier();

		long currentGrade = stoner.getProfession().getGrades()[professionId];
		long maxGrade = stoner.getMaxGrades()[professionId];

		if (modifier < 0.0D) {
			applyNegativeEffect(professionId, currentGrade, maxGrade, modifier, add);
		} else {
			applyPositiveEffect(professionId, currentGrade, maxGrade, modifier, add, potions);
		}
	}

	private void applyNegativeEffect(int professionId, long currentGrade, long maxGrade, double modifier, int add) {
		long affectedGrade = currentGrade + (int) (maxGrade * modifier) + add;

		if (affectedGrade < 1) {
			affectedGrade = 1;
		}

		stoner.getProfession().setGrade(professionId, affectedGrade);
	}

	private void applyPositiveEffect(int professionId, long currentGrade, long maxGrade, double modifier, int add, PotionDefinition potions) {
		long maxLvl = calculateMaxLevel(professionId, maxGrade, modifier, add, potions);
		long affectedGrade = currentGrade + (int) (maxGrade * modifier) + add;

		if (maxLvl > currentGrade) {
			if (affectedGrade > maxLvl) {
				affectedGrade = maxLvl;
			}
			stoner.getProfession().setGrade(professionId, affectedGrade);
		}
	}

	private long calculateMaxLevel(int professionId, long maxGrade, double modifier, int add, PotionDefinition potions) {
		long maxLvl = potions.getPotionType() == PotionDefinition.PotionTypes.RESTORE
			? maxGrade
			: maxGrade + (int) (maxGrade * modifier) + add;

		// Special cases
		if (professionId == 3 && potions.getName().contains("Healing ale of the gods")) {
			maxLvl = 432;
		}

		if (professionId == 5 && potions.getName().contains("Zamorak brew")) {
			maxLvl = 420;
		}

		return maxLvl;
	}

	private void schedulePotionCooldown() {
		TaskQueue.queue(
			new Task(
				stoner,
				3,
				false,
				Task.StackType.STACK,
				Task.BreakType.NEVER,
				TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					consumables.setCanDrink(true);
					stop();
				}

				@Override
				public void onStop() {}
			});
	}
}