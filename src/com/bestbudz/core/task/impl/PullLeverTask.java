package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class PullLeverTask extends Task {

	private final Stoner stoner;
	private final int minX;
	private final int maxX;
	private final int minY;
	private final int maxY;
	private Location location;
	private byte wait = 0;

	public PullLeverTask(Stoner stoner, int x, int y, int xLength, int yLength) {
	super(stoner, 1, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	location = stoner.getLocation();
	minX = (x - 1);
	maxX = (minX + xLength + 1);
	minY = (y - 1);
	maxY = (minY + yLength + 1);
	}

	@Override
	public void execute() {
	int pX = location.getX();
	int pY = location.getY();

	if ((pX >= minX) && (pX <= maxX) && (pY >= minY) && (pY <= maxY)) {
		if (wait == 1) {
			stoner.getUpdateFlags().sendAnimation(2140, 0);
		}

		if (wait == 3) {
			onDestination();
			stop();
		}

		wait = ((byte) (wait + 1));
	}
	}

	public abstract void onDestination();

	@Override
	public void onStop() {
	}
}
