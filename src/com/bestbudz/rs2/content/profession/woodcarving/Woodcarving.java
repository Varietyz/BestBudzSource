package com.bestbudz.rs2.content.profession.woodcarving;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.Fletchable;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.FletchableItem;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendItemOnInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
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


		// Auto-craft all available woodcarving items
		return autoCraftAllAvailableItems(stoner);
	}

	/**
	 * Automatically finds and crafts all available woodcarving items in inventory
	 */
	private boolean autoCraftAllAvailableItems(Stoner stoner) {
		List<FletchableTask> availableFletchables = new ArrayList<>();

		// Check all registered fletchables for items in inventory
		for (Fletchable fletchable : FLETCHABLES.values()) {
			// For each fletchable, check all possible items it can make
			for (int i = 0; i < fletchable.getFletchableItems().length; i++) {
				// Check if we have materials for this specific item
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

		// Start multi-item crafting
		startMultiItemCrafting(stoner, availableFletchables);
		return true;
	}

	/**
	 * Starts crafting for multiple item types
	 */
	private void startMultiItemCrafting(Stoner stoner, List<FletchableTask> fletchableTasks) {
		stoner.send(new SendMessage("@gre@Auto-crafting started - found " + fletchableTasks.size() + " craftable items!"));
		TaskQueue.queue(new MultiItemCraftingTask(stoner, fletchableTasks));
	}

	/**
	 * Helper class to store fletchable and item index
	 */
	private static class FletchableTask {
		final Fletchable fletchable;
		final int itemIndex;

		FletchableTask(Fletchable fletchable, int itemIndex) {
			this.fletchable = fletchable;
			this.itemIndex = itemIndex;
		}
	}

	/**
	 * Multi-item crafting task that cycles through all available item types
	 */
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
			// Check if inventory is full
			if (!stoner.getBox().hasSpaceFor(new Item(1, 1))) {
				stoner.send(new SendMessage("@gre@Auto-crafting stopped - inventory is full!"));
				stop();
				return;
			}

			// Find next item we can craft
			FletchableTask nextTask = findNextCraftableItem();

			if (nextTask == null) {
				// No more items can be crafted
				stoner.send(new SendMessage("@gre@Auto-crafting completed - no more materials available!"));
				stop();
				return;
			}

			Fletchable fletchable = nextTask.fletchable;
			FletchableItem craftableItem = fletchable.getFletchableItems()[nextTask.itemIndex];
			stoner.getProfession().lock(2);

			// Perform the crafting
			stoner.getUpdateFlags().sendAnimation(new Animation(fletchable.getAnimation()));
			stoner.getProfession().addExperience(Professions.WOODCARVING, craftableItem.getExperience());
			stoner.getBox().remove(fletchable.getIngediants(), true);
			stoner.getBox().add(craftableItem.getProduct());

			// Send production message if available
			if (fletchable.getProductionMessage() != null) {
				stoner.send(new SendMessage(fletchable.getProductionMessage()));
			}
		}

		/**
		 * Finds the next item that can be crafted with current materials
		 */
		private FletchableTask findNextCraftableItem() {
			// Start from current task and cycle through all available tasks
			for (int i = 0; i < fletchableTasks.size(); i++) {
				int taskIndex = (currentTaskIndex + i) % fletchableTasks.size();
				FletchableTask task = fletchableTasks.get(taskIndex);

				// Check if we have materials for this item
				Item[] requiredItems = task.fletchable.getIngediants();
				boolean hasAllMaterials = true;

				for (Item requiredItem : requiredItems) {
					if (!stoner.getBox().contains(requiredItem)) {
						hasAllMaterials = false;
						break;
					}
				}

				if (hasAllMaterials) {
					currentTaskIndex = (currentTaskIndex + i + 1) % fletchableTasks.size(); // Move to next for next time
					return task;
				}
			}

			return null; // No items can be crafted
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