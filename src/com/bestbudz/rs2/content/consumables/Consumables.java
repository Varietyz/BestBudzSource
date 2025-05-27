package com.bestbudz.rs2.content.consumables;

import com.bestbudz.core.definitions.FoodDefinition;
import com.bestbudz.core.definitions.PotionDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.AntifireTask;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

/**
 * Handles consumables (Potions & Foods)
 *
 */
public final class Consumables {

	/**
	 * Stoner
	 */
	private final Stoner stoner;

	/**
	 * Consumables
	 * 
	 * @param stoner
	 */
	public Consumables(Stoner stoner) {
	this.stoner = stoner;
	}

	/**
	 * Checks if item is potion
	 * 
	 * @param i
	 * @return
	 */
	public static boolean isPotion(Item i) {
	return i != null && GameDefinitionLoader.getPotionDefinition(i.getId()) != null;
	}

	/**
	 * Check if stoner can eat
	 */
	private boolean canEat = true;

	/**
	 * Check if stoner can drink
	 */
	private boolean canDrink = true;

	/**
	 * Consumes the item
	 * 
	 * @param id
	 * @param slot
	 * @param type
	 * @return
	 */
	public final boolean consume(int id, int slot, ConsumableType type) {
	Item consumable = stoner.getBox().get(slot);

	if (consumable == null || stoner.getMage().isTeleporting() || stoner.isStunned()) {
		return false;
	}

	PotionDefinition potions = Item.getPotionDefinition(id);
	FoodDefinition food = Item.getFoodDefinition(id);
	switch (type) {

	case FOOD:
		SpecialConsumables.specialFood(stoner, consumable);

		if (food == null) {
			return false;
		}

		if ((!canEat) || (!stoner.getController().canEat(stoner))) {
			return true;
		}

		int foodHealth = food.getHeal();

		if (id == 15272) {
			foodHealth = (int) Math.round(stoner.getMaxGrades()[3] * 0.23D);
		}

		int heal = stoner.getProfession().getGrades()[3] + foodHealth;

		if (heal > stoner.getMaxGrades()[3]) {
			if (id != 15272)
				heal = stoner.getMaxGrades()[3];
			else {
				heal = stoner.getMaxGrades()[3] + 10;
			}
		}
		if ((food.getReplaceId() == -1) && (consumable.getAmount() <= 1)) {
			stoner.getBox().clear(slot);
		} else if ((food.getReplaceId() == -1) && (consumable.getAmount() > 1)) {
			consumable.remove(1);
			stoner.getBox().update();
		} else {
			stoner.getBox().setId(slot, food.getReplaceId());
		}
		stoner.getClient().queueOutgoingPacket(new SendSound(317, 1, 2));
		stoner.getUpdateFlags().sendAnimation(829, 0);
		stoner.getProfession().addExperience(20, 100 * foodHealth);

		if (stoner.getProfession().getGrades()[3] < heal) {
			stoner.getProfession().setGrade(3, heal);
		}

		stoner.getClient().queueOutgoingPacket(new SendMessage(food.getMessage()));


		stoner.getCombat().reset();

		if (stoner.getCombat().getAssaultTimer() > 0) {
			stoner.getCombat().increaseAssaultTimer(food.getDelay());
		}

		if (id != 3144) {
			canEat = false;
		}

		TaskQueue.queue(new Task(stoner, food.getDelay(), false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
			canEat = true;
			stop();
			}

			@Override
			public void onStop() {
			}
		});
		break;

	case POTION:
		if (potions == null) {
			return false;
		}

		if ((!stoner.getController().canDrink(stoner)) || (!canDrink)) {
			return true;
		}

		canDrink = false;
		PotionDefinition.ProfessionData[] professionData = potions.getProfessionData();
		String name = potions.getName();

		stoner.getUpdateFlags().sendAnimation(829, 0);
		stoner.getClient().queueOutgoingPacket(new SendSound(334, 1, 0));
		String message = "You gulp some " + name + ".";
		stoner.getProfession().addExperience(20, potions.getId() + 1000);
		stoner.getClient().queueOutgoingPacket(new SendMessage(message));
		if ((potions.getReplaceId() == -1) && (consumable.getAmount() <= 1)) {
			stoner.getBox().clear(slot);
		}
		stoner.getBox().setId(slot, potions.getReplaceId());
		stoner.getCombat().reset();

		useSpecialCasePotion(id);

		if ((professionData != null) && (professionData.length > 0)) {
			for (int i = 0; i < professionData.length; i++) {
				int professionId = professionData[i].getProfessionId();
				int add = professionData[i].getAdd();
				double modifier = professionData[i].getModifier();

				int grade = stoner.getProfession().getGrades()[professionId];
				int gradeForExp = stoner.getMaxGrades()[professionId];

				if (modifier < 0.0D) {
					int affectedGrade = grade + (int) (gradeForExp * modifier) + add;

					if (affectedGrade < 1) {
						affectedGrade = 1;
					}

					stoner.getProfession().setGrade(professionId, affectedGrade);
				} else {
					int maxLvl = potions.getPotionType() == PotionDefinition.PotionTypes.RESTORE ? gradeForExp : gradeForExp + (int) (gradeForExp * modifier) + add;
					int affectedGrade = grade + (int) (gradeForExp * modifier) + add;

					if ((professionId == 3) && (potions.getName().contains("Healing ale of the gods"))) {
						maxLvl = 111;
					}

					if ((professionId == 5) && (potions.getName().contains("Zamorak brew"))) {
						maxLvl = 99;
					}

					if (maxLvl > grade) {
						if (affectedGrade > maxLvl) {
							affectedGrade = maxLvl;
						}
						stoner.getProfession().setGrade(professionId, affectedGrade);
					}
				}
			}
		}

		TaskQueue.queue(new Task(stoner, 3, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
			canDrink = true;
			stop();
			}

			@Override
			public void onStop() {
			}
		});
		break;
	default:
		System.out.print("[ERROR] - CONSUMABLES");
	}

	return true;
	}

	/**
	 * Special potion case
	 * 
	 * @param id
	 * @return
	 */
	public boolean useSpecialCasePotion(int id) {
	switch (id) {
	case 2452:
	case 2454:
	case 2456:
	case 2458:
	case 2488:
		TaskQueue.queue(new AntifireTask(stoner, false));
		break;
	case 15304:
	case 15305:
	case 15306:
	case 15307:
		TaskQueue.queue(new AntifireTask(stoner, true));
		break;
	case 3008:
	case 3010:
	case 3012:
	case 3014:
		stoner.getRunEnergy().add(20);
		return true;
	case 175:
	case 177:
	case 179:
	case 2446:
		stoner.curePoison(100);
		return true;

	case 12695:
	case 12697:
	case 12699:
	case 12701:
		this.superCombatEffect(stoner);
		return true;

	}
	return false;
	}

	/**
	 * Jah Powa effect
	 * 
	 * @param stoner2
	 */
	private void superCombatEffect(Stoner stoner2) {
		for (int i = 0; i < 7; i++) {
			stoner.getGrades()[i] = 150;
		}
		stoner.getRunEnergy().setEnergy(420);
		stoner.getGrades()[3] = 250;
		stoner.getGrades()[18] = 100;
		stoner.getProfession().update();
		stoner.getProfession().addExperience(20, 250000);

		stoner.setAppearanceUpdateRequired(true);
	}
}

