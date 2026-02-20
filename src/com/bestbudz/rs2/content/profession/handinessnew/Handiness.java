package com.bestbudz.rs2.content.profession.handinessnew;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.handinessnew.craftable.Craftable;
import com.bestbudz.rs2.content.profession.handinessnew.craftable.CraftableItem;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public enum Handiness {
	SINGLETON;

	private final HashMap<Integer, Craftable> CRAFTABLES = new HashMap<>();

	/**
	 * Handles item on item interactions - automatically crafts all eligible items
	 */
	public boolean itemOnItem(Stoner stoner, Item use, Item with) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		// Special case for needle and thread
		if (isNeedleAndThread(use, with)) {
			stoner.send(new SendMessage("@red@This combination requires the old crafting interface."));
			return true;
		}

		final Craftable craftable = getCraftable(use.getId(), with.getId());
		if (craftable == null) {
			return false;
		}

		// For gem cutting, find all available gems in inventory
		if ("Gem".equals(craftable.getName())) {
			return autoCraftAllAvailableGems(stoner);
		}

		// Auto-craft all eligible items for other craftables
		return autoCraftAll(stoner, craftable);
	}

	/**
	 * Automatically crafts all items the player can make with available resources
	 */
	private boolean autoCraftAll(Stoner stoner, Craftable craftable) {
		List<Integer> eligibleItems = new ArrayList<>();

		// Add all craftable items (no grade requirement check)
		for (int i = 0; i < craftable.getCraftableItems().length; i++) {
			eligibleItems.add(i);
		}

		if (eligibleItems.isEmpty()) {
			stoner.send(new SendMessage("@red@No craftable items found."));
			return true;
		}

		// Start continuous crafting
		startContinuousCrafting(stoner, craftable, eligibleItems);
		return true;
	}

	/**
	 * Starts continuous crafting for all eligible items
	 */
	private void startContinuousCrafting(Stoner stoner, Craftable craftable, List<Integer> eligibleItems) {
		stoner.send(new SendMessage("@gre@Auto-crafting started - will craft until materials run out..."));

		TaskQueue.queue(new ContinuousCraftingTask(stoner, craftable, eligibleItems));
	}

	/**
	 * Automatically finds and crafts all available gems in inventory
	 */
	private boolean autoCraftAllAvailableGems(Stoner stoner) {
		// List of all gem item IDs that can be cut
		int[] gemIds = {1625, 1627, 1629, 1623, 1621, 1619, 1617, 1631, 6571};
		List<Craftable> availableGemCraftables = new ArrayList<>();

		// Find all gems in inventory and their corresponding craftables
		for (int gemId : gemIds) {
			if (stoner.getBox().contains(new Item(gemId))) {
				Craftable gemCraftable = getCraftable(1755, gemId); // 1755 is chisel
				if (gemCraftable != null) {
					availableGemCraftables.add(gemCraftable);
				}
			}
		}

		if (availableGemCraftables.isEmpty()) {
			stoner.send(new SendMessage("@red@No gems found in inventory to cut."));
			return true;
		}

		// Start multi-gem crafting
		startMultiGemCrafting(stoner, availableGemCraftables);
		return true;
	}

	/**
	 * Starts crafting for multiple gem types
	 */
	private void startMultiGemCrafting(Stoner stoner, List<Craftable> gemCraftables) {
		stoner.send(new SendMessage("@gre@Auto gem-cutting started - found " + gemCraftables.size() + " gem types!"));
		TaskQueue.queue(new MultiGemCraftingTask(stoner, gemCraftables));
	}
	private boolean isNeedleAndThread(Item use, Item with) {
		return (use.getId() == 1733 && with.getId() == 1741) ||
			(use.getId() == 1741 && with.getId() == 1733);
	}

	/**
	 * Easy method to add new craftables
	 */
	public void registerCraftable(Craftable craftable) {
		if (CRAFTABLES.put(craftable.getWith().getId(), craftable) != null) {
			System.out.println(
				"[Handiness] Warning: Overriding existing craftable for item ID: "
					+ craftable.getWith().getId() + " (" + craftable.getName() + ")");
		} else {
			System.out.println(
				"[Handiness] Registered craftable: " + craftable.getName()
					+ " (Item ID: " + craftable.getWith().getId() + ")");
		}
	}

	/**
	 * Easy method to add multiple craftables at once
	 */
	public void registerCraftables(Craftable... craftables) {
		for (Craftable craftable : craftables) {
			registerCraftable(craftable);
		}
	}

	/**
	 * Get craftable by item combination
	 */
	public Craftable getCraftable(int use, int with) {
		return CRAFTABLES.get(use) == null ? CRAFTABLES.get(with) : CRAFTABLES.get(use);
	}

	/**
	 * Legacy method for manual crafting (kept for compatibility)
	 */
	public void addCraftable(Craftable craftable) {
		registerCraftable(craftable);
	}

	/**
	 * Inner class to represent eligible items (simplified)
	 */
	private static class EligibleItem {
		final int index;
		final CraftableItem item;

		EligibleItem(int index, CraftableItem item) {
			this.index = index;
			this.item = item;
		}
	}

	/**
	 * Multi-gem crafting task that cycles through all available gem types
	 */
	private static class MultiGemCraftingTask extends Task {
		private final Stoner stoner;
		private final List<Craftable> gemCraftables;
		private int currentGemIndex = 0;

		public MultiGemCraftingTask(Stoner stoner, List<Craftable> gemCraftables) {
			super(stoner, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.PROFESSION_CREATING);
			this.stoner = stoner;
			this.gemCraftables = gemCraftables;
		}

		@Override
		public void execute() {
			// Find next gem type we can craft
			Craftable nextGem = findNextCraftableGem();

			if (nextGem == null) {
				// No more gems can be crafted
				stoner.send(new SendMessage("@gre@Auto gem-cutting completed - no more gems available!"));
				stop();
				return;
			}

			// Get the first (and only) craftable item for this gem
			CraftableItem gemItem = nextGem.getCraftableItems()[0];
			stoner.getProfession().lock(2);

			// Perform the crafting
			stoner.getUpdateFlags().sendAnimation(new Animation(nextGem.getAnimation()));
			stoner.getProfession().addExperience(Professions.HANDINESS, gemItem.getExperience());
			stoner.getBox().remove(nextGem.getIngediants(0), true);
			stoner.getBox().add(gemItem.getProduct());

			// Send production message if available
			if (nextGem.getProductionMessage() != null) {
				stoner.send(new SendMessage(nextGem.getProductionMessage()));
			}

			// Handle achievements
			AchievementHandler.activateAchievement(stoner, AchievementList.CUT_2500_GEMS, 1);
		}

		/**
		 * Finds the next gem type that can be crafted with current materials
		 */
		private Craftable findNextCraftableGem() {
			// Start from current gem and cycle through all available gem types
			for (int i = 0; i < gemCraftables.size(); i++) {
				int gemIndex = (currentGemIndex + i) % gemCraftables.size();
				Craftable gem = gemCraftables.get(gemIndex);

				// Check if we have materials for this gem
				Item[] requiredItems = gem.getIngediants(0);
				boolean hasAllMaterials = true;

				for (Item requiredItem : requiredItems) {
					if (!stoner.getBox().contains(requiredItem)) {
						hasAllMaterials = false;
						break;
					}
				}

				if (hasAllMaterials) {
					currentGemIndex = (currentGemIndex + i + 1) % gemCraftables.size(); // Move to next for next time
					return gem;
				}
			}

			return null; // No gems can be crafted
		}

		@Override
		public void onStop() {
			stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		}
	}

	/**
	 * Continuous crafting task that keeps making items until materials run out
	 */
	private static class ContinuousCraftingTask extends Task {
		private final Stoner stoner;
		private final Craftable craftable;
		private final List<Integer> eligibleItems;
		private int currentItemIndex = 0;

		public ContinuousCraftingTask(Stoner stoner, Craftable craftable, List<Integer> eligibleItems) {
			super(stoner, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.PROFESSION_CREATING);
			this.stoner = stoner;
			this.craftable = craftable;
			this.eligibleItems = eligibleItems;
		}

		@Override
		public void execute() {
			// Find next item we can craft
			int itemIndex = findNextCraftableItem();

			if (itemIndex == -1) {
				// No more items can be crafted
				stoner.send(new SendMessage("@gre@Auto-crafting completed - no more materials available!"));
				stop();
				return;
			}

			CraftableItem currentItem = craftable.getCraftableItems()[itemIndex];
			stoner.getProfession().lock(2);

			// Perform the crafting
			stoner.getUpdateFlags().sendAnimation(new Animation(craftable.getAnimation()));
			stoner.getProfession().addExperience(Professions.HANDINESS, currentItem.getExperience());
			stoner.getBox().remove(craftable.getIngediants(itemIndex), true);
			stoner.getBox().add(currentItem.getProduct());

			// Send production message if available
			if (craftable.getProductionMessage() != null) {
				stoner.send(new SendMessage(craftable.getProductionMessage()));
			}

			// Handle achievements
			if ("Gem".equals(craftable.getName())) {
				AchievementHandler.activateAchievement(stoner, AchievementList.CUT_2500_GEMS, 1);
			}
		}

		/**
		 * Finds the next item that can be crafted with current materials
		 */
		private int findNextCraftableItem() {
			// Start from current item and cycle through all eligible items
			for (int i = 0; i < eligibleItems.size(); i++) {
				int itemIndex = eligibleItems.get((currentItemIndex + i) % eligibleItems.size());

				// Check if we have materials for this item
				Item[] requiredItems = craftable.getIngediants(itemIndex);
				boolean hasAllMaterials = true;

				for (Item requiredItem : requiredItems) {
					if (!stoner.getBox().contains(requiredItem)) {
						hasAllMaterials = false;
						break;
					}
				}

				if (hasAllMaterials) {
					currentItemIndex = (currentItemIndex + i + 1) % eligibleItems.size(); // Move to next for next time
					return itemIndex;
				}
			}

			return -1; // Nothing can be crafted
		}

		@Override
		public void onStop() {
			stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		}
	}

	public boolean clickButton(Stoner stoner, int button) {
		stoner.send(new SendMessage("@red@Interface crafting is disabled. Use item-on-item for auto-crafting."));
		return false;
	}
}