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

public class THChempistryUnfinishedPotionTask extends Task {
	public static final String THCHEMPISTRY_ITEM_1_KEY = "thchempistryitem1";
	public static final String THCHEMPISTRY_ITEM_2_KEY = "thchempistryitem2";
	private static final int[][] BUTTON_IDS = {{10239, 1}, {10238, 5}, {6211, 28}, {6212, 100}};
	private final Stoner stoner;
	private final UnfinishedPotionData data;
	private final Item used;
	private final Item usedWith;
	private int amountToMake;

	public THChempistryUnfinishedPotionTask(
		Stoner stoner, UnfinishedPotionData data, Item used, Item usedWith, int amountToMake) {
		super(
			stoner,
			3,
			false,
			Task.StackType.NEVER_STACK,
			Task.BreakType.ON_MOVE,
			TaskIdentifier.CURRENT_ACTION);
		this.stoner = stoner;
		this.data = data;
		this.used = used;
		this.usedWith = usedWith;
		this.amountToMake = amountToMake;
	}

	public static boolean attemptToCreateUnfinishedPotion(
		Stoner stoner, int amount, Item used, Item usedWith) {
		Item[] items = {
			(Item) stoner.getAttributes().get("thchempistryitem1"),
			(Item) stoner.getAttributes().get("thchempistryitem2")
		};

		UnfinishedPotionData data =
			UnfinishedPotionData.forId(items[0].getId() == 227 ? items[1].getId() : items[0].getId());

		if (data == null) {
			return false;
		}

		if (!meetsRequirements(stoner, data, used, usedWith)) {
			return false;
		}

		TaskQueue.queue(
			new THChempistryUnfinishedPotionTask(
				stoner, data, new Item(items[0]), new Item(items[1]), amount));
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		return true;
	}

	// NEW: Auto-create unfinished potions when items are used together
	public static boolean autoCreateUnfinishedPotions(Stoner stoner, Item used, Item usedWith) {
		UnfinishedPotionData data = UnfinishedPotionData.forId(used.getId() == 227 ? usedWith.getId() : used.getId());
		if (data == null) {
			return false;
		}

		if (!meetsRequirements(stoner, data, used, usedWith)) {
			return false;
		}

		// Check if player has multiple sets of ingredients
		int waterVials = stoner.getBox().getItemAmount(227);
		int weedAmount = stoner.getBox().getItemAmount(data.getWeedNeeded());
		int maxPotions = Math.min(waterVials, weedAmount);

		if (maxPotions > 1) {
			stoner.send(new SendMessage("You begin making all possible unfinished potions..."));
			TaskQueue.queue(new AutoUnfinishedPotionTask(stoner, data));
			return true;
		} else {
			// Single set - use normal interface
			displayInterface(stoner, used, usedWith);
			return true;
		}
	}

	public static void displayInterface(Stoner stoner, Item used, Item usedWith) {
		UnfinishedPotionData data =
			UnfinishedPotionData.forId(used.getId() == 227 ? usedWith.getId() : used.getId());
		if (data == null) {
			return;
		}

		stoner.getClient().queueOutgoingPacket(new SendChatBoxInterface(4429));
		Item unfinishedPotion = new Item(data.getUnfPotion(), 1);
		stoner.getClient().queueOutgoingPacket(new SendMoveComponent(0, 25, 1746));
		stoner
			.getClient()
			.queueOutgoingPacket(new SendItemOnInterface(1746, 170, unfinishedPotion.getId()));
		stoner
			.getClient()
			.queueOutgoingPacket(
				new SendString(
					"\\n \\n \\n \\n " + unfinishedPotion.getDefinition().getName(), 2799));

		stoner.getAttributes().set("thchempistryitem1", used);
		stoner.getAttributes().set("thchempistryitem2", usedWith);
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

		if (amount != 100)
			attemptToCreateUnfinishedPotion(
				stoner,
				amount,
				(Item) stoner.getAttributes().get("thchempistryitem1"),
				(Item) stoner.getAttributes().get("thchempistryitem2"));
		else {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendEnterXInterface(
						4429, ((Item) stoner.getAttributes().get("thchempistryitem1")).getId()));
		}
		return true;
	}

	private static boolean meetsRequirements(
		Stoner stoner, UnfinishedPotionData data, Item used, Item used2) {
		if (stoner.getProfession().getGrades()[15] < data.getGradeReq()) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage(
						"You need an thc-hempistry grade of " + data.getGradeReq() + " to make this."));
			stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return false;
		}

		if ((!stoner.getBox().hasItemId(used.getId())) || (!stoner.getBox().hasItemId(used2.getId()))) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage("You don't have the ingredients required to make this potion."));
			return false;
		}

		return true;
	}

	private void createUnfinishedPotion() {
		stoner.getBox().add(new Item(data.getUnfPotion(), 1));
		Item weed = used.getId() == 227 ? usedWith : used;
		stoner
			.getClient()
			.queueOutgoingPacket(
				new SendMessage(
					"You put the " + weed.getDefinition().getName() + " in the vial of water."));
	}

	@Override
	public void execute() {
		if (!meetsRequirements(stoner, data, used, usedWith)) {
			stop();
			return;
		}

		stoner.getUpdateFlags().sendAnimation(new Animation(363));
		stoner.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));

		stoner.getBox().remove(used.getId(), 1);
		stoner.getBox().remove(usedWith.getId(), 1);
		createUnfinishedPotion();

		if (--amountToMake == 0) stop();
	}

	@Override
	public void onStop() {}

	// NEW: Auto-unfinished potion making task
	private static class AutoUnfinishedPotionTask extends Task {
		private final Stoner stoner;
		private final UnfinishedPotionData data;

		public AutoUnfinishedPotionTask(Stoner stoner, UnfinishedPotionData data) {
			super(stoner, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
			this.data = data;
		}

		@Override
		public void execute() {
			if (stoner.getProfession().locked()) {
				return;
			}

			if (!stoner.getBox().hasItemId(227) || !stoner.getBox().hasItemId(data.getWeedNeeded())) {
				stoner.send(new SendMessage("You finish making all possible unfinished potions."));
				stop();
				return;
			}

			// Make one unfinished potion
			stoner.getUpdateFlags().sendAnimation(new Animation(363));
			stoner.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));

			stoner.getBox().remove(227, 1); // Vial of water
			stoner.getBox().remove(data.getWeedNeeded(), 1);
			stoner.getBox().add(new Item(data.getUnfPotion(), 1));

			Item weedItem = new Item(data.getWeedNeeded(), 1);
			stoner.send(new SendMessage("You put the " + weedItem.getDefinition().getName() + " in the vial of water."));
			stoner.getBox().update();
		}

		@Override
		public void onStop() {}
	}
}