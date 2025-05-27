package com.mayhem.core.task.impl;

import com.mayhem.core.task.Task;
import com.mayhem.rs2.entity.player.Player;

public class FinishTeleportingTask extends Task {

	private final Player player;

	public FinishTeleportingTask(Player player, int ticks) {
		super(player, ticks, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
	}

	@Override
	public void execute() {
		player.setTakeDamage(true);
		System.out.println("setting take damage..");
		stop();
	}

	@Override
	public void onStop() {
	}

}
