package com.bestbudz.rs2.content.exercisement.obstacle.interaction.rooftop.seers;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.ForceMoveTask;
import com.bestbudz.rs2.content.exercisement.obstacle.interaction.ObstacleInteraction;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface SeersJumpGapInteraction2 extends ObstacleInteraction {

	@Override
	public default void start(Stoner stoner) {
	stoner.getUpdateFlags().sendFaceToDirection(stoner.getX(), stoner.getY() - 1);
	}

	@Override
	public default void onExecution(Stoner stoner, Location start, Location end) {
	stoner.teleport(new Location(stoner.getX(), stoner.getY(), stoner.getZ() - 1));
	TaskQueue.queue(new ForceMoveTask(stoner, 0, stoner.getLocation(), new Location(0, -3), 5043, 0, 12, 2) {
		@Override
		public void onStop() {
		super.onStop();
		TaskQueue.queue(new Task(stoner, 1, true) {
			int ticks = 0;

			@Override
			public void execute() {
			switch (ticks++) {
			case 3:
				stoner.teleport(new Location(stoner.getX(), stoner.getY(), stoner.getZ() + 1));
				break;

			case 4:
				stoner.teleport(end);
				stoner.getUpdateFlags().sendAnimation(new Animation(65535));
				stop();
				break;
			}
			}

			@Override
			public void onStop() {
			}
		});
		}
	});
	}

	@Override
	public default void onCancellation(Stoner stoner) {
	// Climbing has nothing special on cancellation.
	}
}