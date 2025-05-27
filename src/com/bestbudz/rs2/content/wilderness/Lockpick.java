package com.bestbudz.rs2.content.wilderness;

import com.bestbudz.core.cache.map.Door;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.WalkThroughDoorTask;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Lockpick extends Task {

	private Stoner stoner;
	private int x;
	private int y;
	private int z;
	private boolean success = false;
	private boolean insideExiting = false;

	public Lockpick(Stoner stoner, byte delay, int doorId, int x, int y, int z) {
	super(delay);
	this.stoner = stoner;
	this.x = x;
	this.y = y;
	this.z = z;
	}

	@Override
	public void execute() {
	stoner.getUpdateFlags().sendAnimation(new Animation(2246));
	Door door = Region.getDoor(x, y, z);
	if (door.getX() == 3041 && door.getY() == 3959) {
		if (stoner.getX() != door.getX()) {
			stoner.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
			return;
		}
	}
	if (door.getX() == 3191 && door.getY() == 3963) {
		if (stoner.getX() != door.getX()) {
			stoner.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
			return;
		}
	}
	if (door.getX() == 3190 && door.getY() == 3957) {
		if (stoner.getX() != door.getX()) {
			stoner.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
			return;
		}
	}
	if (door.getX() == 3038 || door.getX() == 3044 && door.getY() == 3956) {
		if (stoner.getY() != door.getY()) {
			stoner.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
			return;
		}
	}
	if (stoner.getX() == 3038 && stoner.getY() == 3956 || stoner.getX() == 3044 && stoner.getY() == 3956 || stoner.getX() == 3041 && stoner.getY() == 3959 || stoner.getX() == 3190 && stoner.getY() == 3957 || stoner.getX() == 3191 && stoner.getY() == 3963) {
		insideExiting = true;
		stop();
		return;
	}
	int chance = Utility.randomNumber(4);
	if (chance == 3 || chance == 1 || chance == 2) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You successfully pick the lock.."));
		success = true;
		stop();
		return;
	} else {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You fail to pick the lock on the door.."));
		stop();
		return;
	}
	}

	@Override
	public void onStop() {
	if (success) {
		Task task = new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y, z));
		stoner.getAttributes().set("lockPick", task);
		TaskQueue.queue(task);
		return;
	} else if (insideExiting && x == 3038) {
		Task task = new WalkThroughDoorTask(stoner, x, y, z, new Location(x - 1, y, z));
		stoner.getAttributes().set("lockPick", task);
		TaskQueue.queue(task);
	} else if (insideExiting && x == 3044) {
		Task task = new WalkThroughDoorTask(stoner, x, y, z, new Location(x + 1, y, z));
		stoner.getAttributes().set("lockPick", task);
		TaskQueue.queue(task);
	} else if (insideExiting && x == 3041) {
		Task task = new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y + 1, z));
		stoner.getAttributes().set("lockPick", task);
		TaskQueue.queue(task);
	} else if (insideExiting && x == 3190) {
		Task task = new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y + 1, z));
		stoner.getAttributes().set("lockPick", task);
		TaskQueue.queue(task);
	} else if (insideExiting && x == 3191) {
		Task task = new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y - 1, z));
		stoner.getAttributes().set("lockPick", task);
		TaskQueue.queue(task);
	}
	}

}