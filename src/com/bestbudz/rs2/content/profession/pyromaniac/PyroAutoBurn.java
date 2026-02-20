package com.bestbudz.rs2.content.profession.pyromaniac;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class PyroAutoBurn extends Task {

	public static final PyroAutoBurn SINGLETON = new PyroAutoBurn();

	private PyroAutoBurn() {
		super(null, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	}

	public boolean handleObjectClick(Stoner stoner, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		if (isBurningObject(objectId)) {
			return autoBurnAllLogs(stoner, objectId);
		}

		return false;
	}

	public boolean itemOnObject(Stoner stoner, Item item, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		if (isBurningObject(objectId)) {
			return autoBurnAllLogs(stoner, objectId);
		}

		return false;
	}

	private boolean autoBurnAllLogs(Stoner stoner, int objectId) {

		int totalLogs = 0;
		for (Item item : stoner.getBox().getItems()) {
			if (item != null && Pyromaniac.Wood.forId(item.getId()) != null) {
				totalLogs += item.getAmount();
			}
		}

		if (totalLogs == 0) {
			stoner.send(new SendMessage("You do not have any logs to burn!"));
			return false;
		}

		stoner.send(new SendMessage("You begin burning all your logs..."));
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

		TaskQueue.queue(new AutoBurningTask(stoner, objectId));
		return true;
	}

	private boolean isBurningObject(int objectId) {
		switch (objectId) {
			case 5249:
			case 114:
			case 2732:
			case 26185:
			case 14901:
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

	private static class AutoBurningTask extends Task {

		private final Stoner stoner;
		private final int objectId;

		public AutoBurningTask(Stoner stoner, int objectId) {
			super(stoner, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
			this.objectId = objectId;
		}

		@Override
		public void execute() {

			if (System.currentTimeMillis() - stoner.getCurrentStunDelay() < stoner.getSetStunDelay()) {
				return;
			}

			if (stoner.getProfession().locked()) {
				return;
			}

			Pyromaniac.Wood wood = null;
			Item logItem = null;

			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					Pyromaniac.Wood w = Pyromaniac.Wood.forId(item.getId());
					if (w != null) {
						wood = w;
						logItem = item;
						break;
					}
				}
			}

			if (wood == null || logItem == null) {
				stoner.send(new SendMessage("You finishedBloodTrial burning all your logs."));
				stop();
				return;
			}

			burnLog(stoner, wood, logItem, objectId);
		}

		@Override
		public void onStop() {

		}

		private void burnLog(Stoner stoner, Pyromaniac.Wood wood, Item logItem, int objectId) {

			stoner.freeze(5, 5);
			stoner.setCurrentStunDelay(System.currentTimeMillis() + wood.getStunTime() * 1000L);
			stoner.getProfession().lock(3);

			stoner.getUpdateFlags().sendAnimation(new Animation(Pyromaniac.BURNING_ANIMATION));
			stoner.getUpdateFlags().sendGraphic(new Graphic(831));

			stoner.getBox().remove(wood.getId(), 1);

			if (objectId == 5249) {

				stoner.getBox().add(995, 100);
				stoner.getProfession().addExperience(11, wood.getExperience() / 4.0);
				stoner.send(new SendMessage("You burn the " + logItem.getDefinition().getName() + " on the altar."));
			} if (objectId == 14901) {

				stoner.getBox().add(995, 500);
				stoner.getProfession().addExperience(11, wood.getExperience() / 2.0 );
				stoner.send(new SendMessage("You magically burn the " + logItem.getDefinition().getName() + " in the presence of the altar."));
			} else {

				stoner.getBox().add(995, 1000);
				stoner.getProfession().addExperience(11, wood.getExperience());
				stoner.send(new SendMessage("You burn the " + logItem.getDefinition().getName() + "."));
			}

			AchievementHandler.activateAchievement(stoner, AchievementList.BURN_1500_WOOD, 1);
			AchievementHandler.activateAchievement(stoner, AchievementList.BURN_12500_WOOD, 1);

			stoner.getBox().update();
		}
	}
}
