package com.bestbudz.rs2.content.profession.woodcarving;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.Fletchable;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.FletchableItem;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public enum Woodcarving {
	SINGLETON;

	public static final String FLETCHABLE_KEY = "FLETCHABLE_KEY";

	private final HashMap<Integer, Fletchable> FLETCHABLES = new HashMap<>();

	public boolean itemOnItem(Stoner stoner, Item use, Item with) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		final Fletchable fletchable = getFletchable(use.getId(), with.getId());

		if (fletchable == null || use.getId() == 590 || with.getId() == 590) {
			return false;
		}

		return autoCraftAllAvailableItems(stoner);
	}

	private boolean autoCraftAllAvailableItems(Stoner stoner) {
		List<FletchableTask> availableFletchables = new ArrayList<>();

		for (Fletchable fletchable : FLETCHABLES.values()) {

			for (int i = 0; i < fletchable.getFletchableItems().length; i++) {

				Item[] requiredItems = fletchable.getIngediants();
				boolean hasAllMaterials = true;

				for (Item requiredItem : requiredItems) {
					if (!stoner.getBox().contains(requiredItem)) {
						hasAllMaterials = false;
						break;
					}
				}

				if (hasAllMaterials) {
					availableFletchables.add(new FletchableTask(fletchable, i));
				}
			}
		}

		if (availableFletchables.isEmpty()) {
			stoner.send(new SendMessage("@red@No woodcarving materials found in inventory."));
			return true;
		}

		startMultiItemCrafting(stoner, availableFletchables);
		return true;
	}

	private void startMultiItemCrafting(Stoner stoner, List<FletchableTask> fletchableTasks) {
		stoner.send(new SendMessage("@gre@Auto-crafting started - found " + fletchableTasks.size() + " craftable items!"));
		TaskQueue.queue(new MultiItemCraftingTask(stoner, fletchableTasks));
	}

	private static class FletchableTask {
		final Fletchable fletchable;
		final int itemIndex;

		FletchableTask(Fletchable fletchable, int itemIndex) {
			this.fletchable = fletchable;
			this.itemIndex = itemIndex;
		}
	}

	private static class MultiItemCraftingTask extends Task {
		private final Stoner stoner;
		private final List<FletchableTask> fletchableTasks;
		private int currentTaskIndex = 0;

		public MultiItemCraftingTask(Stoner stoner, List<FletchableTask> fletchableTasks) {
			super(stoner, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.PROFESSION_CREATING);
			this.stoner = stoner;
			this.fletchableTasks = fletchableTasks;
		}

		@Override
		public void execute() {

			if (!stoner.getBox().hasSpaceFor(new Item(1, 1))) {
				stoner.send(new SendMessage("@gre@Auto-crafting stopped - inventory is full!"));
				stop();
				return;
			}

			FletchableTask nextTask = findNextCraftableItem();

			if (nextTask == null) {

				stoner.send(new SendMessage("@gre@Auto-crafting completed - no more materials available!"));
				stop();
				return;
			}

			Fletchable fletchable = nextTask.fletchable;
			FletchableItem craftableItem = fletchable.getFletchableItems()[nextTask.itemIndex];
			stoner.getProfession().lock(2);

			stoner.getUpdateFlags().sendAnimation(new Animation(fletchable.getAnimation()));
			stoner.getProfession().addExperience(Professions.WOODCARVING, craftableItem.getExperience());
			stoner.getBox().remove(fletchable.getIngediants(), true);
			stoner.getBox().add(craftableItem.getProduct());

			if (fletchable.getProductionMessage() != null) {
				stoner.send(new SendMessage(fletchable.getProductionMessage()));
			}
		}

		private FletchableTask findNextCraftableItem() {

			for (int i = 0; i < fletchableTasks.size(); i++) {
				int taskIndex = (currentTaskIndex + i) % fletchableTasks.size();
				FletchableTask task = fletchableTasks.get(taskIndex);

				Item[] requiredItems = task.fletchable.getIngediants();
				boolean hasAllMaterials = true;

				for (Item requiredItem : requiredItems) {
					if (!stoner.getBox().contains(requiredItem)) {
						hasAllMaterials = false;
						break;
					}
				}

				if (hasAllMaterials) {
					currentTaskIndex = (currentTaskIndex + i + 1) % fletchableTasks.size();
					return task;
				}
			}

			return null;
		}

		@Override
		public void onStop() {
			stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		}
	}

	public boolean fletch(Stoner stoner, int index, int amount) {
		stoner.send(new SendMessage("@red@Manual crafting is disabled. Use item-on-item for auto-crafting."));
		return false;
	}

	public void addFletchable(Fletchable fletchable) {
		if (FLETCHABLES.put(fletchable.getWith().getId(), fletchable) != null) {
			System.out.println(
				"[Woodcarving] Conflicting item values: "
					+ fletchable.getWith().getId()
					+ " Type: "
					+ fletchable.getClass().getSimpleName());
		}
	}

	public Fletchable getFletchable(int use, int with) {
		return FLETCHABLES.get(use) == null ? FLETCHABLES.get(with) : FLETCHABLES.get(use);
	}

	public boolean clickButton(Stoner stoner, int button) {
		stoner.send(new SendMessage("@red@Interface crafting is disabled. Use item-on-item for auto-crafting."));
		return false;
	}

	public boolean start(Stoner stoner, Fletchable fletchable, int index, int amount) {
		stoner.send(new SendMessage("@red@Manual crafting is disabled. Use item-on-item for auto-crafting."));
		return false;
	}
}
