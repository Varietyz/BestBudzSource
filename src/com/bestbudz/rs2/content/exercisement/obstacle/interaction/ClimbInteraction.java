package com.bestbudz.rs2.content.exercisement.obstacle.interaction;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface ClimbInteraction extends ObstacleInteraction {

	@Override
	public default void start(Stoner stoner) {
	// Climbing has nothing special on start up.
	}

	@Override
	public default void onExecution(Stoner stoner, Location start, Location end) {
	stoner.getUpdateFlags().sendAnimation(new Animation(getAnimation()));
	TaskQueue.queue(new Task(stoner, 3, false) {
		@Override
		public void execute() {
		stoner.teleport(end);
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		this.stop();
		}

		@Override
		public void onStop() {
		}
	});
	}

	@Override
	public default void onCancellation(Stoner stoner) {
	// Climbing has nothing special on cancellation.
	}
}