package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class THChempistryMasterProcessor {

	public static final THChempistryMasterProcessor SINGLETON = new THChempistryMasterProcessor();

	public boolean handleItemOnItem(Stoner stoner, Item used, Item usedWith) {

		if (!isTHChempistryItem(used.getId()) && !isTHChempistryItem(usedWith.getId())) {
			return false;
		}

		if (stoner.getProfession().locked()) {
			stoner.send(new SendMessage("@red@You cannot process while your profession is locked!"));
			return false;
		}

		if (!hasProcessableItems(stoner)) {
			stoner.send(new SendMessage("You don't have any THC-hempistry items to process."));
			return false;
		}

		stoner.send(new SendMessage("@gre@Starting master THC-hempistry processing..."));
		TaskQueue.queue(new MasterProcessingTask(stoner));
		return true;
	}

	private boolean isTHChempistryItem(int itemId) {

		if (UntrimmedWeedData.forId(itemId) != null) return true;

		if (UnfinishedPotionData.forId(itemId) != null) return true;

		if (GrindingData.forId(itemId) != null) return true;

		if (itemId == 233) return true;

		if (itemId == 227) return true;

		for (UnfinishedPotionData data : UnfinishedPotionData.values()) {
			if (itemId == data.getUnfPotion()) return true;
		}

		for (FinishedPotionData data : FinishedPotionData.values()) {
			if (itemId == data.getItemNeeded() ||
				itemId == data.getUnfinishedPotion() ||
				itemId == data.getFinishedPotion()) return true;
		}

		for (GrindingData data : GrindingData.values()) {
			if (itemId == data.getGroundId()) return true;
		}

		return false;
	}

	private boolean hasProcessableItems(Stoner stoner) {
		for (Item item : stoner.getBox().getItems()) {
			if (item != null) {

				if (UntrimmedWeedData.forId(item.getId()) != null) return true;

				if (GrindingData.forId(item.getId()) != null && stoner.getBox().hasItemId(233)) return true;

				if (UnfinishedPotionData.forId(item.getId()) != null && stoner.getBox().hasItemId(227)) return true;

				for (FinishedPotionData data : FinishedPotionData.values()) {
					if (item.getId() == data.getUnfinishedPotion() && stoner.getBox().hasItemId(data.getItemNeeded())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static class MasterProcessingTask extends Task {

		private final Stoner stoner;
		private ProcessingStage currentStage;
		private int cycleCount = 0;
		private static final int MAX_CYCLES = 15000;

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
				return;
			}

			if (cycleCount++ > MAX_CYCLES) {
				stoner.send(new SendMessage("@red@Processing stopped - maximum cycles reached."));
				stop();
				return;
			}

			switch (currentStage) {
				case CLEANING_WEED:
					if (processCleanWeed()) {
						return;
					}
					currentStage = ProcessingStage.GRINDING_ITEMS;
					stoner.send(new SendMessage("@gre@Weed cleaning complete! Starting grinding..."));
					break;

				case GRINDING_ITEMS:
					if (processGrinding()) {
						return;
					}
					currentStage = ProcessingStage.MAKING_UNFINISHED_POTIONS;
					stoner.send(new SendMessage("@gre@Grinding complete! Making unfinished potions..."));
					break;

				case MAKING_UNFINISHED_POTIONS:
					if (processUnfinishedPotions()) {
						return;
					}
					currentStage = ProcessingStage.MAKING_FINISHED_POTIONS;
					stoner.send(new SendMessage("@gre@Unfinished potions complete! Making finished potions..."));
					break;

				case MAKING_FINISHED_POTIONS:
					if (processFinishedPotions()) {
						return;
					}
					currentStage = ProcessingStage.CHECKING_FOR_MORE;
					break;

				case CHECKING_FOR_MORE:
					if (hasMoreToProcess()) {

						currentStage = ProcessingStage.CLEANING_WEED;
						stoner.send(new SendMessage("@gre@Starting another processing cycle..."));
					} else {

						stoner.send(new SendMessage("@gre@Master THC-hempistry processing complete!"));
						stop();
					}
					break;
			}
		}

		private boolean processCleanWeed() {
			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					UntrimmedWeedData data = UntrimmedWeedData.forId(item.getId());
					if (data != null && stoner.getProfession().getGrades()[15] >= data.getGradeReq()) {

						stoner.getUpdateFlags().sendAnimation(new Animation(363));
						stoner.getBox().remove(data.getUntrimmedWeed(), 1);
						stoner.getBox().add(new Item(data.getCleanWeed(), 1));
						stoner.getProfession().addExperience(15, data.getExp());
						stoner.send(new SendMessage("You clean the " + item.getDefinition().getName() + "."));
						stoner.getBox().update();
						return true;
					}
				}
			}
			return false;
		}

		private boolean processGrinding() {
			if (!stoner.getBox().hasItemId(233)) {
				return false;
			}

			for (Item item : stoner.getBox().getItems()) {
				if (item != null && item.getId() != 233) {
					GrindingData data = GrindingData.forId(item.getId());
					if (data != null) {

						stoner.getUpdateFlags().sendAnimation(new Animation(364));
						stoner.getBox().remove(data.getItemId(), 1);
						stoner.getBox().add(new Item(data.getGroundId(), 1));
						stoner.send(new SendMessage("You grind the " + item.getDefinition().getName() + "."));
						stoner.getBox().update();
						return true;
					}
				}
			}
			return false;
		}

		private boolean processUnfinishedPotions() {
			if (!stoner.getBox().hasItemId(227)) {
				return false;
			}

			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					UnfinishedPotionData data = UnfinishedPotionData.forId(item.getId());
					if (data != null && stoner.getProfession().getGrades()[15] >= data.getGradeReq()) {

						stoner.getUpdateFlags().sendAnimation(new Animation(363));
						stoner.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));
						stoner.getBox().remove(227, 1);
						stoner.getBox().remove(data.getWeedNeeded(), 1);
						stoner.getBox().add(new Item(data.getUnfPotion(), 1));
						stoner.send(new SendMessage("You put the " + item.getDefinition().getName() + " in the vial of water."));
						stoner.getBox().update();
						return true;
					}
				}
			}
			return false;
		}

		private boolean processFinishedPotions() {
			for (FinishedPotionData data : FinishedPotionData.values()) {
				if (stoner.getBox().hasItemId(data.getUnfinishedPotion()) &&
					stoner.getBox().hasItemId(data.getItemNeeded()) &&
					stoner.getProfession().getGrades()[15] >= data.getGradeReq()) {

					stoner.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));
					stoner.getUpdateFlags().sendAnimation(new Animation(363));
					stoner.getBox().remove(new Item(data.getUnfinishedPotion(), 1));
					stoner.getBox().remove(new Item(data.getItemNeeded(), 1));
					stoner.getBox().add(new Item(data.getFinishedPotion(), 1));
					stoner.getProfession().addExperience(15, data.getExpGained());

					Item finishedPotion = new Item(data.getFinishedPotion(), 1);
					stoner.send(new SendMessage("You finishedBloodTrial making the " + finishedPotion.getDefinition().getName() + "."));
					stoner.getBox().update();
					return true;
				}
			}
			return false;
		}

		private boolean hasMoreToProcess() {

			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {

					UntrimmedWeedData cleanData = UntrimmedWeedData.forId(item.getId());
					if (cleanData != null && stoner.getProfession().getGrades()[15] >= cleanData.getGradeReq()) {
						return true;
					}

					if (GrindingData.forId(item.getId()) != null && stoner.getBox().hasItemId(233)) {
						return true;
					}

					UnfinishedPotionData unfData = UnfinishedPotionData.forId(item.getId());
					if (unfData != null && stoner.getBox().hasItemId(227) &&
						stoner.getProfession().getGrades()[15] >= unfData.getGradeReq()) {
						return true;
					}
				}
			}

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

		}
	}
}
