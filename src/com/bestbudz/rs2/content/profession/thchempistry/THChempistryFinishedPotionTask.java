package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterXInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendItemOnInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMoveComponent;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class THChempistryFinishedPotionTask extends Task {

	public static final String THCHEMPISTRY_ITEM_1_KEY = "thchempistryitem1";
	public static final String THCHEMPISTRY_ITEM_2_KEY = "thchempistryitem2";
	private static final int[][] BUTTON_IDS = {{10239, 1}, {10238, 5}, {6211, 28}, {6212, 100}};
	private final Stoner stoner;
	private final FinishedPotionData data;
	private int amountToMake;

	public THChempistryFinishedPotionTask(Stoner stoner, FinishedPotionData data, int amount) {
		super(
			stoner,
			2,
			false,
			Task.StackType.NEVER_STACK,
			Task.BreakType.ON_MOVE,
			TaskIdentifier.CURRENT_ACTION);
		this.stoner = stoner;
		this.data = data;
		amountToMake = amount;
	}

	public static void attemptPotionMaking(Stoner stoner, int amount) {
		FinishedPotionData data =
			FinishedPotionData.forIds(
				((Item) stoner.getAttributes().get("thchempistryitem1")).getId(),
				((Item) stoner.getAttributes().get("thchempistryitem2")).getId());
		if (data == null) {
			data =
				FinishedPotionData.forId(
					((Item) stoner.getAttributes().get("thchempistryitem2")).getId());
		}
		if (data == null) {
			stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return;
		}
		if (!meetsRequirements(stoner, data, false)) {
			stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return;
		}
		createPotion(stoner, data);
		stoner.getUpdateFlags().sendAnimation(new Animation(363));
		amount--;
		TaskQueue.queue(new THChempistryFinishedPotionTask(stoner, data, amount));
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
	}

	private static void createPotion(Stoner stoner, FinishedPotionData data) {
		stoner.getBox().remove(new Item(data.getUnfinishedPotion(), 1));
		stoner.getBox().remove(new Item(data.getItemNeeded(), 1));
		stoner.getBox().add(new Item(data.getFinishedPotion(), 1));
		stoner.getProfession().addExperience(15, data.getExpGained());
	}

	// NEW: Auto-create finished potions when items are used together
	public static boolean autoCreateFinishedPotions(Stoner stoner, Item used, Item usedWith) {
		FinishedPotionData data = FinishedPotionData.forIds(used.getId(), usedWith.getId());
		if (data == null) {
			return false;
		}

		if (!meetsRequirements(stoner, data, false)) {
			return false;
		}

		// Check if player has multiple sets of ingredients
		int unfinishedAmount = stoner.getBox().getItemAmount(data.getUnfinishedPotion());
		int ingredientAmount = stoner.getBox().getItemAmount(data.getItemNeeded());
		int maxPotions = Math.min(unfinishedAmount, ingredientAmount);

		if (maxPotions > 1) {
			stoner.send(new SendMessage("You begin making all possible finished potions..."));
			TaskQueue.queue(new AutoFinishedPotionTask(stoner, data));
			return true;
		} else {
			// Single set - use normal interface
			return displayInterface(stoner, used, usedWith);
		}
	}

	public static boolean displayInterface(Stoner stoner, Item used, Item usedWith) {
		FinishedPotionData data = FinishedPotionData.forIds(used.getId(), usedWith.getId());

		if (data == null) {
			return false;
		}

		Item finishedPotion = new Item(data.getFinishedPotion(), 1);

		if (finishedPotion.getDefinition() == null) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("No can do."));
			return true;
		}
		stoner.getClient().queueOutgoingPacket(new SendChatBoxInterface(4429));
		stoner.getClient().queueOutgoingPacket(new SendMoveComponent(0, 25, 1746));
		stoner
			.getClient()
			.queueOutgoingPacket(new SendItemOnInterface(1746, 170, finishedPotion.getId()));

		if (finishedPotion.getDefinition() != null
			&& finishedPotion.getDefinition().getName() != null) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendString(
						"\\n \\n \\n \\n \\n \\n \\n" + finishedPotion.getDefinition() != null
							? finishedPotion.getDefinition().getName()
							: "null",
						2799));
		} else {
			stoner
				.getClient()
				.queueOutgoingPacket(new SendString("\\n \\n \\n \\n \\n \\n \\n Invalid Item", 2799));
		}

		stoner.getAttributes().set("thchempistryitem1", used);
		stoner.getAttributes().set("thchempistryitem2", usedWith);
		return true;
	}

	public static boolean handleTHChempistryButtons(Stoner stoner, int buttonId) {
		int amount = 0;
		for (int i = 0; i < BUTTON_IDS.length; i++) {
			if (BUTTON_IDS[i][0] == buttonId) {
				amount = BUTTON_IDS[i][1];
				break;
			}
		}
		if (amount == 0) {
			return false;
		}
		if (amount != 100) attemptPotionMaking(stoner, amount);
		else {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendEnterXInterface(
						4430, ((Item) stoner.getAttributes().get("thchempistryitem1")).getId()));
		}
		return true;
	}

	private static boolean meetsRequirements(
		Stoner stoner, FinishedPotionData data, boolean running) {
		if (stoner.getProfession().getGrades()[15] < data.getGradeReq()) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage(
						"You need an thc-hempistry grade of "
							+ data.getGradeReq()
							+ " to make this potion."));
			return false;
		}

		if ((!stoner.getBox().hasItemId(data.getUnfinishedPotion()))
			|| (!stoner.getBox().hasItemId(data.getItemNeeded()))) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage(
						!running
							? "You don't have the ingredients required to make this potion."
							: "You have run out of ingredients to make this potion."));
			return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (!meetsRequirements(stoner, data, true)) {
			stop();
			return;
		}

		if (amountToMake == 0) {
			stop();
			return;
		}

		stoner.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));

		stoner.getUpdateFlags().sendAnimation(new Animation(363));

		createPotion(stoner, data);

		amountToMake -= 1;
		if (amountToMake == 0) stop();
	}

	@Override
	public void onStop() {}

	// NEW: Auto-finished potion making task
	private static class AutoFinishedPotionTask extends Task {
		private final Stoner stoner;
		private final FinishedPotionData data;

		public AutoFinishedPotionTask(Stoner stoner, FinishedPotionData data) {
			super(stoner, 2, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
			this.data = data;
		}

		@Override
		public void execute() {
			if (stoner.getProfession().locked()) {
				return;
			}

			if (!stoner.getBox().hasItemId(data.getUnfinishedPotion()) ||
				!stoner.getBox().hasItemId(data.getItemNeeded())) {
				stoner.send(new SendMessage("You finish making all possible finished potions."));
				stop();
				return;
			}

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
		}

		@Override
		public void onStop() {}
	}
}