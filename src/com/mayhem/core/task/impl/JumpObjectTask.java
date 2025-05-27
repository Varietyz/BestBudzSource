package com.mayhem.core.task.impl;

import com.mayhem.core.task.Task;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.Controller;
import com.mayhem.rs2.entity.player.controllers.ControllerManager;

public class JumpObjectTask extends Task {

	private final Player p;
	private final Location dest;
	private final Controller start;

	public JumpObjectTask(Player p, Location dest) {
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
