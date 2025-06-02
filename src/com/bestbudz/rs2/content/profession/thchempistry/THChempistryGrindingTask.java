package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class THChempistryGrindingTask extends Task {
	private final Stoner stoner;
	private final GrindingData data;

	public THChempistryGrindingTask(Stoner stoner, GrindingData data) {
		super(
			stoner,
			1,
			true,
			Task.StackType.NEVER_STACK,
			Task.BreakType.ON_MOVE,
			TaskIdentifier.CURRENT_ACTION);
		this.stoner = stoner;
		this.data = data;
	}

	public static void handleGrindingIngredients(Stoner stoner, Item used, Item usedWith) {
		int itemId = used.getId() != 233 ? used.getId() : usedWith.getId();
		GrindingData data = GrindingData.forId(itemId);
		if (data == null) return;

		// Check if player has multiple grindable items for auto-grinding
		int totalGrindable = 0;
		for (Item item : stoner.getBox().getItems()) {
			if (item != null && GrindingData.forId(item.getId()) != null) {
				totalGrindable += item.getAmount();
			}
		}

		if (totalGrindable > 1) {
			// Start auto-grinding all items
			stoner.send(new SendMessage("You begin grinding all your ingredients..."));
			TaskQueue.queue(new AutoGrindingTask(stoner));
		} else {
			// Single item - use normal grinding
			stoner.getUpdateFlags().sendAnimation(new Animation(364));
			TaskQueue.queue(new THChempistryGrindingTask(stoner, data));
		}
	}

	private void createGroundItem() {
		stoner.getBox().remove(data.getItemId(), 1);
		stoner.getBox().add(new Item(data.getGroundId(), 1));
	}

	@Override
	public void execute() {
		createGroundItem();
		stop();
	}

	@Override
	public void onStop() {}

	// NEW: Auto-grinding task for all grindable items
	private static class AutoGrindingTask extends Task {
		private final Stoner stoner;

		public AutoGrindingTask(Stoner stoner) {
			super(stoner, 2, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
		}

		@Override
		public void execute() {
			if (stoner.getProfession().locked()) {
				return;
			}

			// Check if player still has pestle and mortar
			if (!stoner.getBox().hasItemId(233)) {
				stoner.send(new SendMessage("You can no longer grind - missing pestle and mortar."));
				stop();
				return;
			}

			// Find next grindable item
			GrindingData data = null;
			Item grindableItem = null;

			for (Item item : stoner.getBox().getItems()) {
				if (item != null && item.getId() != 233) { // Skip pestle and mortar
					GrindingData d = GrindingData.forId(item.getId());
					if (d != null) {
						data = d;
						grindableItem = item;
						break;
					}
				}
			}

			if (data == null || grindableItem == null) {
				stoner.send(new SendMessage("You finish grinding all your ingredients."));
				stop();
				return;
			}

			// Grind one item
			stoner.getUpdateFlags().sendAnimation(new Animation(364));
			stoner.getBox().remove(data.getItemId(), 1);
			stoner.getBox().add(new Item(data.getGroundId(), 1));
			stoner.send(new SendMessage("You grind the " + grindableItem.getDefinition().getName() + "."));
			stoner.getBox().update();
		}

		@Override
		public void onStop() {}
	}
}