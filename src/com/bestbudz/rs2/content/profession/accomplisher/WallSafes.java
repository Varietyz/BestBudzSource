package com.bestbudz.rs2.content.profession.accomplisher;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class WallSafes extends Task {

	private Stoner stoner;

	public WallSafes(int delay, Stoner stoner) {
	super(stoner, delay, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	}

	public static Item ITEMS[] = { new Item(1617), new Item(1619), new Item(1621), new Item(1623), new Item(1623), new Item(995, 20), new Item(995, 40) };

	public static Item RANDOM() {
	return ITEMS[(int) (Math.random() * ITEMS.length)];
	}

	public static int timer(Stoner stoner) {
	if (stoner.getBox().hasItemAmount(new Item(5560))) {
		return (10 - (int) Math.floor(99 / 10) + Utility.random(5));
	} else {
		return (10 - (int) Math.floor(99 / 10) + Utility.random(11)) + 20;
	}
	}

	public static int chance(Stoner stoner) {
	return (Utility.random((int) Math.floor(99 / 10) + 1));
	}

	public static boolean can(Stoner stoner) {
	if (stoner.isCracking) {
		stoner.send(new SendMessage("You are currently cracking a safe!"));
		return false;
	}
	if (stoner.getBox().getFreeSlots() < 1) {
		stoner.send(new SendMessage("You do not have any space left in your box."));
		return false;
	}
	return true;
	}

	public static void crack(Stoner stoner) {
	if (!can(stoner) || stoner.getDelay().elapsed() < 1000) {
		return;
	}
	stoner.isCracking = true;
	stoner.send(new SendMessage("You attempt to crack the safe... "));
	stoner.getUpdateFlags().sendAnimation(new Animation(881));
	stoner.getMovementHandler().reset();
	TaskQueue.queue(new WallSafes(timer(stoner), stoner));

	}

	@Override
	public void execute() {
	if (chance(stoner) == 0) {
		stoner.send(new SendMessage("You slip and trigger a trap!"));
		if (stoner.getProfession().getGrades()[17] == 99) {
			stoner.hit(new Hit(1));
		} else if (stoner.getProfession().getGrades()[17] > 79) {
			stoner.hit(new Hit(2));
		} else if (stoner.getProfession().getGrades()[17] > 49) {
			stoner.hit(new Hit(3));
		} else {
			stoner.hit(new Hit(4));
		}
		this.stop();
		stoner.getUpdateFlags().sendAnimation(new Animation(404));
		stoner.isCracking = false;
		return;
	}
	stoner.send(new SendMessage("You get some loot."));
	stoner.getBox().add(RANDOM());
	stoner.isCracking = false;
	stoner.getDelay().reset();
	this.stop();
	}

	@Override
	public void onStop() {
	}

}
