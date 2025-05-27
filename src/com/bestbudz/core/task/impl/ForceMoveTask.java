package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ForceMoveTask extends Task {

	private final Stoner stoner;
	private final Location start;
	private final Location dest;

	public ForceMoveTask(Stoner stoner, int delay, Location start, Location dest, int animation, int speed1, int speed2, int direction) {
	super(stoner, delay, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	this.start = start;
	this.dest = dest;
	stoner.getUpdateFlags().sendAnimation(new Animation(animation));
	stoner.getMovementHandler().setForceStart(start);
	stoner.getMovementHandler().setForceEnd(dest);
	stoner.getMovementHandler().setForceSpeed1((short) speed1);
	stoner.getMovementHandler().setForceSpeed2((short) speed2);
	stoner.getMovementHandler().setForceDirection((byte) direction);
	stoner.getMovementHandler().setForceMove(true);
	stoner.getUpdateFlags().setForceMovement(true);
	}

	@Override
	public void execute() {
	int x = start.getX() + dest.getX();
	int y = start.getY() + dest.getY();
	stoner.teleport(new Location(x, y, stoner.getZ()));
	stop();
	}

	@Override
	public void onStop() {
	stoner.getMovementHandler().setForceMove(false);
	}
}