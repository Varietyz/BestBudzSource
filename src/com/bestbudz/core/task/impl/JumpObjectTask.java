package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;

public class JumpObjectTask extends Task {

	private final Stoner p;
	private final Location dest;
	private final Controller start;

	public JumpObjectTask(Stoner p, Location dest) {
	super(p, 1);
	this.p = p;
	this.dest = dest;
	this.start = p.getController();

	p.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	}

	@Override
	public void execute() {
	stop();
	}

	@Override
	public void onStop() {
	p.teleport(dest);
	p.setController(start);
	}

}
