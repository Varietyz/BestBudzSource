package com.mayhem.core.task.impl;

import com.mayhem.core.cache.map.Door;
import com.mayhem.core.task.Task;

public class TickDoorTask extends Task {

	public TickDoorTask(Door door) {
		super(null, 1);
		if (door.original()) {
			stop();
			return;
		}
	}

	@Override
	public void execute() {

		stop();
	}

	@Override
	public void onStop() {
	}

}
