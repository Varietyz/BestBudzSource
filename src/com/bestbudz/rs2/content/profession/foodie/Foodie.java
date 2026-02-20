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

public class Foodie extends Task {

	public static final Foodie SINGLETON = new Foodie();

	private Foodie() {
		super(null, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	}

	public boolean handleObjectClick(Stoner stoner, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		if (isCookingObject(objectId)) {
			return autoCookAllFood(stoner, objectId);
		}

		return false;
	}

	public boolean itemOnObject(Stoner stoner, Item item, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		if (isCookingObject(objectId)) {
			return autoCookAllFood(stoner, objectId);
		}

		return false;
	}

	private boolean autoCookAllFood(Stoner stoner, int objectId) {

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

		stoner.send(new SendMessage("You begin cooking all your raw food..."));
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

		TaskQueue.queue(new AutoCookingTask(stoner, objectId));
		return true;
	}

	private boolean isCookingObject(int objectId) {

		switch (objectId) {
			case 114:
			case 2728:
			case 2732:
			case 4172:
			case 8750:
			case 9682:
			case 21302:
			case 26181:
			case 26185:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void execute() {

	}

	@Override
	public void onStop() {

	}

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

			if (cookingData == null || rawFood == null) {
				stoner.send(new SendMessage("You finishedBloodTrial cooking."));
				stop();
				return;
			}

			stoner.getUpdateFlags().sendAnimation(883, 0);
			stoner.getBox().remove(new Item(rawFood.getId(), 1), false);

			if (successfulAttempt(stoner, cookingData)) {

				stoner.getBox().add(new Item(cookingData.getReplacement(), 1), true);
				stoner.send(new SendMessage("You cook the " + rawFood.getDefinition().getName() + "."));

				double experience = cookingData.getExperience();
				stoner.getProfession().addExperience(7, experience);

				AchievementHandler.activateAchievement(stoner, AchievementList.COOK_250_FOODS, 1);
				AchievementHandler.activateAchievement(stoner, AchievementList.COOK_10000_FOODS, 1);
			} else {

				stoner.getBox().add(new Item(cookingData.getBurnt(), 1), true);
				stoner.send(new SendMessage("You burn the " + rawFood.getDefinition().getName() + "."));
				stoner.send(new SendMessage("You have messed the fish up and got BestBucks."));
			}

			stoner.getBox().update();
		}

		@Override
		public void onStop() {

		}

		private boolean meetsRequirements(Stoner stoner, FoodieData data, int itemId) {
			long foodieGrade = stoner.getProfession().getGrades()[7];
			if (foodieGrade < data.getGradeRequired()) {
				return false;
			}
			return stoner.getBox().hasItemId(itemId);
		}

		private boolean successfulAttempt(Stoner stoner, FoodieData data) {
			if (stoner.getProfession().getGrades()[7] > data.getNoBurnGrade()) {
				return true;
			}

			int boost = getFoodieGradeBoost(stoner);
			return Professions.isSuccess(
				stoner.getMaxGrades()[7] + boost,
				data.getGradeRequired() / 2 == 0 ? 1 : data.getGradeRequired() / 2);
		}

		private int getFoodieGradeBoost(Stoner stoner) {
			Item gloves = stoner.getEquipment().getItems()[9];
			if ((gloves != null) && (gloves.getId() == 775)) {
				return 3;
			}
			return 0;
		}
	}
}
