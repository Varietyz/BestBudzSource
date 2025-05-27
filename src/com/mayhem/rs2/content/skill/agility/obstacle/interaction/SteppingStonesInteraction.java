package com.mayhem.rs2.content.skill.agility.obstacle.interaction;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.ForceMoveTask;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;

public interface SteppingStonesInteraction extends ObstacleInteraction {

	@Override
	public default void start(Player player) {
	}

	@Override
	public default void onExecution(Player player, Location start, Location end) {
		int dX = end.getX() - player.getLocation().getX();
		int dY = end.getY() - player.getLocation().getY();
		int modX = Integer.signum(dX);
		int modY = Integer.signum(dY);

		int totalSteps = Math.abs(modX) > Math.abs(modY) ? Math.abs(dX) : Math.abs(dY);
		TaskQueue.queue(new Task(player, 2, true) {
			int steps = 0;

			@Override
			public void execute() {
				TaskQueue.queue(new ForceMoveTask(player, 0, player.getLocation(), new Location(modX, modY), getAnimation(), 10, 26, 3));
				
				if (++steps == totalSteps) {
					stop();
				}
			}

			@Override
			public void onStop() {
			}
		});
	}

	@Override
	public default void onCancellation(Player player) {
	}
}