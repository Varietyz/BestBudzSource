package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class CleanWeedTask extends Task {
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

	private static boolean meetsRequirements(Stoner stoner, UntrimmedWeedData data) {
	if (stoner.getProfession().getGrades()[15] < data.getGradeReq()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You need an THC-hempistry grade of " + data.getGradeReq() + " to trim this weed."));
		return false;
	}
	if (!stoner.getEquipment().isWearingItem(6575)) {
				DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
		return false;
	}
	return true;
	}

	private final Stoner stoner;

	// private int amount;

	private UntrimmedWeedData data;

	public CleanWeedTask(Stoner stoner, int amount, UntrimmedWeedData data) {
	super(stoner, 0, true, Task.StackType.STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	// this.amount = amount;
	this.data = data;
	}

	private void cleanWeed() {
	// stoner.getBox().getItems()[amount] = null;

	stoner.getBox().remove(data.getUntrimmedWeed(), 1);
	stoner.getBox().add(new Item(data.getCleanWeed(), 1));
	stoner.getBox().update();
	stoner.getProfession().addExperience(15, data.getExp());
	stoner.getClient().queueOutgoingPacket(new SendMessage("You carefully trimmed the weed, leaving u with some nice buds."));
	}

	@Override
	public void execute() {
	cleanWeed();
	stop();
	}

	@Override
	public void onStop() {
	}
}
