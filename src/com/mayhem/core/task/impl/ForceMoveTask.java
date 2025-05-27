package com.mayhem.core.task.impl;

import com.mayhem.core.task.Task;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;

public class ForceMoveTask extends Task {

	private final Player player;
	private final Location start;
	private final Location dest;

	public ForceMoveTask(Player player, int delay, Location start, Location dest, int animation, int speed1, int speed2, int direction) {
		super(player, delay, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.start = start;
		this.dest = dest;
		player.getUpdateFlags().sendAnimation(new Animation(animation));
		player.getMovementHandler().setForceStart(start);
		player.getMovementHandler().setForceEnd(dest);
		player.getMovementHandler().setForceSpeed1((short) speed1);
		player.getMovementHandler().setForceSpeed2((short) speed2);
		player.getMovementHandler().setForceDirection((byte) direction);
		player.getMovementHandler().setForceMove(true);
		player.getUpdateFlags().setForceMovement(true);
	}

	@Override
	public void execute() {
		int x = start.getX() + dest.getX();
		int y = start.getY() + dest.getY();
		player.teleport(new Location(x, y, player.getZ()));
		stop();
	}

	@Override
	public void onStop() {
		player.getMovementHandler().setForceMove(false);
	}
}