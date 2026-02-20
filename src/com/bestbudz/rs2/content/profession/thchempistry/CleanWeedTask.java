package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class CleanWeedTask extends Task {
	private final Stoner stoner;
	private final UntrimmedWeedData data;

	public CleanWeedTask(Stoner stoner, int amount, UntrimmedWeedData data) {
		super(
			stoner,
			0,
			true,
			Task.StackType.STACK,
			Task.BreakType.ON_MOVE,
			TaskIdentifier.CURRENT_ACTION);
		this.stoner = stoner;
		this.data = data;
	}

	public static void attemptWeedCleaning(Stoner stoner, int amount) {
		if (stoner.getBox().get(amount) == null) {
			return;
		}

		UntrimmedWeedData data = UntrimmedWeedData.forId(stoner.getBox().get(amount).getId());

		if (data == null) {
			return;
		}
		if (!meetsRequirements(stoner, data)) {
			return;
		}

		TaskQueue.queue(new CleanWeedTask(stoner, amount, data));
	}

	public static boolean autoCleanAllWeed(Stoner stoner, int itemId) {
		UntrimmedWeedData data = UntrimmedWeedData.forId(itemId);
		if (data == null) {
			return false;
		}

		if (!meetsRequirements(stoner, data)) {
			return false;
		}

		int totalUntrimmed = 0;
		for (Item item : stoner.getBox().getItems()) {
			if (item != null && UntrimmedWeedData.forId(item.getId()) != null) {
				totalUntrimmed += item.getAmount();
			}
		}

		if (totalUntrimmed > 1) {
			stoner.send(new SendMessage("You begin cleaning all your untrimmed weed..."));
			TaskQueue.queue(new AutoCleanWeedTask(stoner));
			return true;
		} else {

			TaskQueue.queue(new CleanWeedTask(stoner, 0, data));
			return true;
		}
	}

	private static boolean meetsRequirements(Stoner stoner, UntrimmedWeedData data) {
		if (stoner.getProfession().getGrades()[15] < data.getGradeReq()) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage(
						"You need an THC-hempistry grade of "
							+ data.getGradeReq()
							+ " to trim this weed."));
			return false;
		}
		return true;
	}

	private void cleanWeed() {
		stoner.getBox().remove(data.getUntrimmedWeed(), 1);
		stoner.getBox().add(new Item(data.getCleanWeed(), 1));
		stoner.getBox().update();
		stoner.getProfession().addExperience(15, data.getExp());
		stoner
			.getClient()
			.queueOutgoingPacket(
				new SendMessage("You carefully trimmed the weed, leaving u with some nice buds."));
	}

	@Override
	public void execute() {
		cleanWeed();
		stop();
	}

	@Override
	public void onStop() {}

	private static class AutoCleanWeedTask extends Task {
		private final Stoner stoner;

		public AutoCleanWeedTask(Stoner stoner) {
			super(stoner, 2, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
		}

		@Override
		public void execute() {
			if (stoner.getProfession().locked()) {
				return;
			}

			UntrimmedWeedData data = null;
			Item weedItem = null;

			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					UntrimmedWeedData d = UntrimmedWeedData.forId(item.getId());
					if (d != null && stoner.getProfession().getGrades()[15] >= d.getGradeReq()) {
						data = d;
						weedItem = item;
						break;
					}
				}
			}

			if (data == null || weedItem == null) {
				stoner.send(new SendMessage("You finishedBloodTrial cleaning all your weed."));
				stop();
				return;
			}

			stoner.getBox().remove(data.getUntrimmedWeed(), 1);
			stoner.getBox().add(new Item(data.getCleanWeed(), 1));
			stoner.getProfession().addExperience(15, data.getExp());
			stoner.send(new SendMessage("You clean the " + weedItem.getDefinition().getName() + "."));
			stoner.getBox().update();
		}

		@Override
		public void onStop() {}
	}
}
