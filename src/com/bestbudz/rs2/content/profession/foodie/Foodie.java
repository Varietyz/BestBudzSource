package com.bestbudz.rs2.content.profession.foodie;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Auto-cooking system that cooks all available raw food without interfaces
 */
public class Foodie extends Task {

	public static final Foodie SINGLETON = new Foodie();

	private Foodie() {
		super(null, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	}

	/**
	 * Handles object clicks for cooking activities
	 */
	public boolean handleObjectClick(Stoner stoner, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		// Check if it's a cooking object
		if (isCookingObject(objectId)) {
			return autoCookAllFood(stoner, objectId);
		}

		return false;
	}

	/**
	 * Handles item on object interactions for cooking activities
	 */
	public boolean itemOnObject(Stoner stoner, Item item, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		// Check if it's a cooking object
		if (isCookingObject(objectId)) {
			return autoCookAllFood(stoner, objectId);
		}

		return false;
	}

	/**
	 * Auto-cooks all available raw food in inventory
	 */
	private boolean autoCookAllFood(Stoner stoner, int objectId) {
		// Find all cookable raw food in inventory
		int totalItems = 0;
		for (Item item : stoner.getBox().getItems()) {
			if (item != null && FoodieData.forId(item.getId()) != null) {
				totalItems += item.getAmount();
			}
		}

		if (totalItems == 0) {
			stoner.send(new SendMessage("You do not have any raw food to cook!"));
			return false;
		}

		// Start auto-cooking task
		stoner.send(new SendMessage("You begin cooking all your raw food..."));
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

		TaskQueue.queue(new AutoCookingTask(stoner, objectId));
		return true;
	}

	/**
	 * Check if object is a cooking object (range, fire, oven, etc.)
	 */
	private boolean isCookingObject(int objectId) {
		// Common cooking object IDs - add more as needed
		switch (objectId) {
			case 114:   // Fire
			case 2728:  // Range
			case 2732:  // Range
			case 4172:  // Range
			case 8750:  // Range
			case 9682:  // Range
			case 21302: // Range
			case 26181: // Range
			case 26185: // Range
				return true;
			default:
				return false;
		}
	}

	@Override
	public void execute() {
		// Not used - this is a singleton for handling clicks
	}

	@Override
	public void onStop() {
		// Not used - this is a singleton for handling clicks
	}

	/**
	 * Auto-cooking task that processes all raw food
	 */
	private static class AutoCookingTask extends Task {

		private final Stoner stoner;
		private final int objectId;

		public AutoCookingTask(Stoner stoner, int objectId) {
			super(stoner, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
			this.objectId = objectId;
		}

		@Override
		public void execute() {
			// Find next cookable item
			FoodieData cookingData = null;
			Item rawFood = null;

			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					FoodieData data = FoodieData.forId(item.getId());
					if (data != null && meetsRequirements(stoner, data, item.getId())) {
						cookingData = data;
						rawFood = item;
						break;
					}
				}
			}

			// No more cookable items
			if (cookingData == null || rawFood == null) {
				stoner.send(new SendMessage("You finishedBloodTrial cooking."));
				stop();
				return;
			}

			// Cook the item
			stoner.getUpdateFlags().sendAnimation(883, 0);
			stoner.getBox().remove(new Item(rawFood.getId(), 1), false);

			if (successfulAttempt(stoner, cookingData)) {
				// Successfully cooked
				stoner.getBox().add(new Item(cookingData.getReplacement(), 1), true);
				stoner.send(new SendMessage("You cook the " + rawFood.getDefinition().getName() + "."));

				double experience = cookingData.getExperience();
				stoner.getProfession().addExperience(7, experience);

				AchievementHandler.activateAchievement(stoner, AchievementList.COOK_250_FOODS, 1);
				AchievementHandler.activateAchievement(stoner, AchievementList.COOK_10000_FOODS, 1);
			} else {
				// Burned the food
				stoner.getBox().add(new Item(cookingData.getBurnt(), 1), true);
				stoner.send(new SendMessage("You burn the " + rawFood.getDefinition().getName() + "."));
				stoner.send(new SendMessage("You have messed the fish up and got BestBucks."));
			}

			stoner.getBox().update();
		}

		@Override
		public void onStop() {
			// Task completed
		}

		/**
		 * Check if player meets requirements to cook this food
		 */
		private boolean meetsRequirements(Stoner stoner, FoodieData data, int itemId) {
			long foodieGrade = stoner.getProfession().getGrades()[7];
			if (foodieGrade < data.getGradeRequired()) {
				return false;
			}
			return stoner.getBox().hasItemId(itemId);
		}

		/**
		 * Determine if cooking attempt is successful or burns
		 */
		private boolean successfulAttempt(Stoner stoner, FoodieData data) {
			if (stoner.getProfession().getGrades()[7] > data.getNoBurnGrade()) {
				return true;
			}

			int boost = getFoodieGradeBoost(stoner);
			return Professions.isSuccess(
				stoner.getMaxGrades()[7] + boost,
				data.getGradeRequired() / 2 == 0 ? 1 : data.getGradeRequired() / 2);
		}

		/**
		 * Get cooking grade boost from equipment
		 */
		private int getFoodieGradeBoost(Stoner stoner) {
			Item gloves = stoner.getEquipment().getItems()[9];
			if ((gloves != null) && (gloves.getId() == 775)) {
				return 3;
			}
			return 0;
		}
	}
}