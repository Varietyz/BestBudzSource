package com.bestbudz.rs2.content.exercisement.obstacle.interaction;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.ForceMoveTask;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface ClimbOverInteraction extends ObstacleInteraction {

	@Override
	public default void start(Stoner stoner) {
	// Climbing has nothing special on start up.
	}

	@Override
	public default void onExecution(Stoner stoner, Location start, Location end) {
	stoner.getUpdateFlags().sendAnimation(new Animation(getAnimation()));
	TaskQueue.queue(new ForceMoveTask(stoner, 1, stoner.getLocation(), new Location(2, 0), 839, 0, 45, 1));
	}

	@Override
	public default void onCancellation(Stoner stoner) {
	// Climbing has nothing special on cancellation.
	}
}