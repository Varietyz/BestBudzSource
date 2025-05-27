package com.bestbudz.core.task.impl;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;

public class HopDitchTask extends ForceMovementTask {

	public static Location getLocation(Location loc) {
	if (loc.getY() < 3522) {
		return new Location(loc.getX(), loc.getY() + 3);
	} else {
		return new Location(loc.getX(), loc.getY() - 3);
	}
	}

	private byte stage = 0;

	public HopDitchTask(Stoner stoner) {
	super(stoner, getLocation(stoner.getLocation()), ControllerManager.DEFAULT_CONTROLLER);
	}

	@Override
	public void execute() {
	if (stage == 0) {
		stoner.getUpdateFlags().sendAnimation(6132, 0);
	} else if (stage == 1) {
		stoner.teleport(dest);
		stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
		stoner.getUpdateFlags().sendFaceToDirection(stoner.getLocation().getX() - xMod, stoner.getLocation().getY() - yMod);
		stop();
	}

	stage++;
	}

	@Override
	public void onDestination() {
	}
}
