package com.bestbudz.rs2.content.exercisement.obstacle.interaction.rooftop.ardougne;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.exercisement.obstacle.interaction.ObstacleInteraction;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface ArdougneRoofJumpInteraction2 extends ObstacleInteraction {

	@Override
	public default void start(Stoner stoner) {
	stoner.getUpdateFlags().sendFaceToDirection(2658, 3298);
	}

	@Override
	public default void onExecution(Stoner stoner, Location start, Location end) {
	TaskQueue.queue(new Task(stoner, 1, true) {
		int ticks = 0;

		@Override
		public void execute() {
		switch (ticks++) {
		case 1:
			stoner.getUpdateFlags().sendAnimation(new Animation(2586));
			stoner.teleport(new Location(2658, 3298, 1));
			break;

		case 2:
			stoner.getUpdateFlags().sendFaceToDirection(2658, 3298);
			stoner.getUpdateFlags().sendAnimation(new Animation(2588));
			break;

		case 3:
			stoner.getMovementHandler().walkTo(3, 0);
			break;

		case 7:
			stoner.getUpdateFlags().sendFaceToDirection(2663, 3296);
			stoner.getUpdateFlags().sendAnimation(new Animation(2586));
			break;

		case 8:
			stoner.getUpdateFlags().sendAnimation(new Animation(2588));
			stoner.teleport(new Location(2663, 3297, 1));
			break;

		case 9:
			stoner.getMovementHandler().walkTo(3, 0);
			break;

		case 13:
			stoner.getUpdateFlags().sendAnimation(new Animation(2586));
			break;

		case 14:
			stoner.teleport(end);
			break;
		}
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