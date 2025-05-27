package com.bestbudz.core.task.impl;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;

public abstract class CrossGangPlankTask extends ForceMovementTask {

	private byte steps = 0;
	private final boolean on;

	public CrossGangPlankTask(Stoner stoner, Location dest, Controller to, boolean on) {
	super(stoner, new Location(dest.getX(), dest.getY(), 1), to);
	this.on = on;
	}

	@Override
	public void execute() {
	if (++steps == 2) {
		stoner.teleport(new Location(stoner.getLocation().getX(), stoner.getLocation().getY(), on ? 1 : 0));
	} else if (steps > 6) {// just in case
		stoner.getMovementHandler().reset();
		stoner.teleport(dest);
		onDestination();
		stoner.setController(to);
		stoner.getUpdateFlags().sendFaceToDirection(stoner.getLocation().getX() - xMod, stoner.getLocation().getY() - yMod);
		stop();
		return;
	}

	stoner.getMovementHandler().walkTo(xMod, yMod);
	if (stoner.getLocation().getX() + xMod == dest.getX() && stoner.getLocation().getY() + yMod == dest.getY()) {
		onDestination();
		stoner.setController(to);
		stoner.getUpdateFlags().sendFaceToDirection(stoner.getLocation().getX() - xMod, stoner.getLocation().getY() - yMod);
		stop();
	}
	}
}
