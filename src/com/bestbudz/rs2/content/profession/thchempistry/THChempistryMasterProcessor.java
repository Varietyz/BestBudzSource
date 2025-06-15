package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

/**
 * Master THC-hempistry auto-processor that chains all activities:
 * Clean Weed → Grind Items → Make Unfinished Potions → Make Finished Potions
 */
public class THChempistryMasterProcessor {

	public static final THChempistryMasterProcessor SINGLETON = new THChempistryMasterProcessor();

	/**
	 * Handles any THC-hempistry item-on-item interaction to start master processing
	 */
	public boolean handleItemOnItem(Stoner stoner, Item used, Item usedWith) {
		// Check if either item is a THC-hempistry item
		if (!isTHChempistryItem(used.getId()) && !isTHChempistryItem(usedWith.getId())) {
			return false; // Not THC-hempistry related
		}


		if (stoner.getProfession().locked()) {
			stoner.send(new SendMessage("@red@You cannot process while your profession is locked!"));
			return false;
		}

		// Check if there's anything to process
		if (!hasProcessableItems(stoner)) {
			stoner.send(new SendMessage("You don't have any THC-hempistry items to process."));
			return false;
		}

		// Start master processing
		stoner.send(new SendMessage("@gre@Starting master THC-hempistry processing..."));
		TaskQueue.queue(new MasterProcessingTask(stoner));
		return true;
	}

	/**
	 * Check if an item ID is related to THC-hempistry
	 */
	private boolean isTHChempistryItem(int itemId) {
		// Check untrimmed weed
		if (UntrimmedWeedData.forId(itemId) != null) return true;

		// Check clean weed (for unfinished potions)
		if (UnfinishedPotionData.forId(itemId) != null) return true;

		// Check grindable items
		if (GrindingData.forId(itemId) != null) return true;

		// Check pestle and mortar
		if (itemId == 233) return true;

		// Check vial of water
		if (itemId == 227) return true;

		// Check unfinished potions
		for (UnfinishedPotionData data : UnfinishedPotionData.values()) {
			if (itemId == data.getUnfPotion()) return true;
		}

		// Check finished potion ingredients and results
		for (FinishedPotionData data : FinishedPotionData.values()) {
			if (itemId == data.getItemNeeded() ||
				itemId == data.getUnfinishedPotion() ||
				itemId == data.getFinishedPotion()) return true;
		}

		// Check ground items
		for (GrindingData data : GrindingData.values()) {
			if (itemId == data.getGroundId()) return true;
		}

		return false;
	}

	/**
	 * Check if player has any processable THC-hempistry items
	 */
	private boolean hasProcessableItems(Stoner stoner) {
		for (Item item : stoner.getBox().getItems()) {
			if (item != null) {
				// Check for untrimmed weed
				if (UntrimmedWeedData.forId(item.getId()) != null) return true;

				// Check for grindable items
				if (GrindingData.forId(item.getId()) != null && stoner.getBox().hasItemId(233)) return true;

				// Check for unfinished potion ingredients (weed + water vials)
				if (UnfinishedPotionData.forId(item.getId()) != null && stoner.getBox().hasItemId(227)) return true;

				// Check for finished potion ingredients
				for (FinishedPotionData data : FinishedPotionData.values()) {
					if (item.getId() == data.getUnfinishedPotion() && stoner.getBox().hasItemId(data.getItemNeeded())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Master processing task that handles all THC-hempistry activities in sequence
	 */
	private static class MasterProcessingTask extends Task {

		private final Stoner stoner;
		private ProcessingStage currentStage;
		private int cycleCount = 0;
		private static final int MAX_CYCLES = 15000; // Prevent infinite loops

		private enum ProcessingStage {
			CLEANING_WEED,
			GRINDING_ITEMS,
			MAKING_UNFINISHED_POTIONS,
			MAKING_FINISHED_POTIONS,
			CHECKING_FOR_MORE
		}

		public MasterProcessingTask(Stoner stoner) {
			super(stoner, 2, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
			this.currentStage = ProcessingStage.CLEANING_WEED;
		}

		@Override
		public void execute() {
			if (stoner.getProfession().locked()) {
				return; // Wait for profession to unlock
			}

			if (cycleCount++ > MAX_CYCLES) {
				stoner.send(new SendMessage("@red@Processing stopped - maximum cycles reached."));
				stop();
				return;
			}

			switch (currentStage) {
				case CLEANING_WEED:
					if (processCleanWeed()) {
						return; // Still cleaning
					}
					currentStage = ProcessingStage.GRINDING_ITEMS;
					stoner.send(new SendMessage("@gre@Weed cleaning complete! Starting grinding..."));
					break;

				case GRINDING_ITEMS:
					if (processGrinding()) {
						return; // Still grinding
					}
					currentStage = ProcessingStage.MAKING_UNFINISHED_POTIONS;
					stoner.send(new SendMessage("@gre@Grinding complete! Making unfinished potions..."));
					break;

				case MAKING_UNFINISHED_POTIONS:
					if (processUnfinishedPotions()) {
						return; // Still making unfinished
					}
					currentStage = ProcessingStage.MAKING_FINISHED_POTIONS;
					stoner.send(new SendMessage("@gre@Unfinished potions complete! Making finished potions..."));
					break;

				case MAKING_FINISHED_POTIONS:
					if (processFinishedPotions()) {
						return; // Still making finished
					}
					currentStage = ProcessingStage.CHECKING_FOR_MORE;
					break;

				case CHECKING_FOR_MORE:
					if (hasMoreToProcess()) {
						// Start another cycle
						currentStage = ProcessingStage.CLEANING_WEED;
						stoner.send(new SendMessage("@gre@Starting another processing cycle..."));
					} else {
						// All done!
						stoner.send(new SendMessage("@gre@Master THC-hempistry processing complete!"));
						stop();
					}
					break;
			}
		}

		/**
		 * Process cleaning weed - returns true if still working
		 */
		private boolean processCleanWeed() {
			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					UntrimmedWeedData data = UntrimmedWeedData.forId(item.getId());
					if (data != null && stoner.getProfession().getGrades()[15] >= data.getGradeReq()) {
						// Clean one piece
						stoner.getUpdateFlags().sendAnimation(new Animation(363));
						stoner.getBox().remove(data.getUntrimmedWeed(), 1);
						stoner.getBox().add(new Item(data.getCleanWeed(), 1));
						stoner.getProfession().addExperience(15, data.getExp());
						stoner.send(new SendMessage("You clean the " + item.getDefinition().getName() + "."));
						stoner.getBox().update();
						return true; // Still cleaning
					}
				}
			}
			return false; // No more to clean
		}

		/**
		 * Process grinding items - returns true if still working
		 */
		private boolean processGrinding() {
			if (!stoner.getBox().hasItemId(233)) {
				return false; // No pestle and mortar
			}

			for (Item item : stoner.getBox().getItems()) {
				if (item != null && item.getId() != 233) { // Skip pestle and mortar
					GrindingData data = GrindingData.forId(item.getId());
					if (data != null) {
						// Grind one item
						stoner.getUpdateFlags().sendAnimation(new Animation(364));
						stoner.getBox().remove(data.getItemId(), 1);
						stoner.getBox().add(new Item(data.getGroundId(), 1));
						stoner.send(new SendMessage("You grind the " + item.getDefinition().getName() + "."));
						stoner.getBox().update();
						return true; // Still grinding
					}
				}
			}
			return false; // No more to grind
		}

		/**
		 * Process making unfinished potions - returns true if still working
		 */
		private boolean processUnfinishedPotions() {
			if (!stoner.getBox().hasItemId(227)) {
				return false; // No vials of water
			}

			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					UnfinishedPotionData data = UnfinishedPotionData.forId(item.getId());
					if (data != null && stoner.getProfession().getGrades()[15] >= data.getGradeReq()) {
						// Make one unfinished potion
						stoner.getUpdateFlags().sendAnimation(new Animation(363));
						stoner.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));
						stoner.getBox().remove(227, 1); // Vial of water
						stoner.getBox().remove(data.getWeedNeeded(), 1);
						stoner.getBox().add(new Item(data.getUnfPotion(), 1));
						stoner.send(new SendMessage("You put the " + item.getDefinition().getName() + " in the vial of water."));
						stoner.getBox().update();
						return true; // Still making unfinished
					}
				}
			}
			return false; // No more to make
		}

		/**
		 * Process making finished potions - returns true if still working
		 */
		private boolean processFinishedPotions() {
			for (FinishedPotionData data : FinishedPotionData.values()) {
				if (stoner.getBox().hasItemId(data.getUnfinishedPotion()) &&
					stoner.getBox().hasItemId(data.getItemNeeded()) &&
					stoner.getProfession().getGrades()[15] >= data.getGradeReq()) {

					// Make one finished potion
					stoner.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));
					stoner.getUpdateFlags().sendAnimation(new Animation(363));
					stoner.getBox().remove(new Item(data.getUnfinishedPotion(), 1));
					stoner.getBox().remove(new Item(data.getItemNeeded(), 1));
					stoner.getBox().add(new Item(data.getFinishedPotion(), 1));
					stoner.getProfession().addExperience(15, data.getExpGained());

					Item finishedPotion = new Item(data.getFinishedPotion(), 1);
					stoner.send(new SendMessage("You finish making the " + finishedPotion.getDefinition().getName() + "."));
					stoner.getBox().update();
					return true; // Still making finished
				}
			}
			return false; // No more to make
		}

		/**
		 * Check if there's more processing to do
		 */
		private boolean hasMoreToProcess() {
			// Check for any processable items
			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					// Untrimmed weed
					UntrimmedWeedData cleanData = UntrimmedWeedData.forId(item.getId());
					if (cleanData != null && stoner.getProfession().getGrades()[15] >= cleanData.getGradeReq()) {
						return true;
					}

					// Grindable items
					if (GrindingData.forId(item.getId()) != null && stoner.getBox().hasItemId(233)) {
						return true;
					}

					// Unfinished potion ingredients
					UnfinishedPotionData unfData = UnfinishedPotionData.forId(item.getId());
					if (unfData != null && stoner.getBox().hasItemId(227) &&
						stoner.getProfession().getGrades()[15] >= unfData.getGradeReq()) {
						return true;
					}
				}
			}

			// Finished potion ingredients
			for (FinishedPotionData data : FinishedPotionData.values()) {
				if (stoner.getBox().hasItemId(data.getUnfinishedPotion()) &&
					stoner.getBox().hasItemId(data.getItemNeeded()) &&
					stoner.getProfession().getGrades()[15] >= data.getGradeReq()) {
					return true;
				}
			}

			return false;
		}

		@Override
		public void onStop() {
			// Processing completed or stopped
		}
	}
}