package com.bestbudz.rs2.content.profession.accomplisher;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles stalls for accomplisher class
 * 
 * @author Jaybane
 *
 */
public class HomeStalls extends Task {

	private Stoner stoner;

	private stallData data;

	public HomeStalls(int delay, Stoner stoner, stallData data) {
	super(stoner, delay, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	this.data = data;
	}

	@Override
	public void execute() {
	if (!meetsRequirements(stoner, data)) {
		stop();
		return;
	}
	successfull(stoner, data);
	stop();
	}

	@Override
	public void onStop() {
	}

	private enum stallData {
		FOOD("food", 4875, 3162, 1500),
		GENERAL("general", 4876, 1887, 2500),
		CRAFT("handiness", 4874, 1635, 5500),
		MAGE("mage", 4877, 8788, 1),
		SCIMITAR("scimitar", 4878, 6721, 12000);

		private String name;
		private int objectId;
		private int itemId;

		private stallData(String name, int objectId, int itemId, int itemAmount) {
		this.name = name;
		this.objectId = objectId;
		this.itemId = itemId;
		}

		public static stallData getObjectById(int id) {
		for (stallData data : stallData.values())
			if (data.objectId == id)
				return data;
		return null;
		}
	}

	public static void attempt(Stoner stoner, int id, Location location) {
	stallData data = stallData.getObjectById(id);

	if (data == null) {
		return;
	}

	if (stoner.getProfession().locked()) {
		return;
	}
	if (!meetsRequirements(stoner, data)) {
		return;
	}
	stoner.getProfession().lock(3);
	stoner.getUpdateFlags().sendAnimation(new Animation(832));

	TaskQueue.queue(new HomeStalls(4, stoner, data));
	}

	public static boolean meetsRequirements(Stoner stoner, stallData stall) {
	if (stall == null) {
		return false;
	}
	if (stoner.getBox().getFreeSlots() == 0) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You don't have enough box spaces left to hold this."));
		return false;
	}
	return true;
	}

	public static void successfull(Stoner stoner, stallData stall) {
	stoner.getUpdateFlags().sendAnimation(new Animation(832));
	stoner.getBox().add(new Item(stall.itemId, 1));
	stoner.getClient().queueOutgoingPacket(new SendMessage("" + stall.name + " stall keeps generating new free junk."));
	}



}