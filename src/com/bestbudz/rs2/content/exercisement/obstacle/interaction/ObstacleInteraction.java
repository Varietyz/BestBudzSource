package com.bestbudz.rs2.content.exercisement.obstacle.interaction;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.exercisement.obstacle.Obstacle;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.impl.GroundItemHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerAnimations;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public interface ObstacleInteraction {

	int getAnimation();

	String getPreMessage();

	String getPostMessage();

	void start(Stoner stoner);

	void onExecution(Stoner stoner, Location start, Location end);

	void onCancellation(Stoner stoner);

	default void execute(Stoner stoner, Obstacle next, Location start, Location end, int ordinal) {

	stoner.getAttributes().set("TEMP_CONTROLLER", stoner.getController());
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	stoner.getMovementHandler().setForceMove(true);
	stoner.getMovementHandler().reset();
	stoner.getCombat().reset();

	TaskQueue.queue(new Task(stoner, 1, false) {
		private final StonerAnimations ANIMATION = stoner.getAnimations().copy();
		private final boolean RUNNING = stoner.getRunEnergy().isRunning();
		private boolean started = false;
		private Obstacle nextObstacle = next;

		@Override
		public void onStart() {
		stoner.getProfession().lock(4);
		stoner.getRunEnergy().setRunning(false);
		if (getPreMessage() != null) {
			stoner.send(new SendMessage(getPreMessage()));
		}
		start(stoner);

		}

		@Override
		public void execute() {
		if (nextObstacle != null && stoner.getLocation().equals(nextObstacle.getEnd())) {
			Location delta = Utility.delta(nextObstacle == null ? start : nextObstacle.getStart(), nextObstacle == null ? end : nextObstacle.getEnd());
			stoner.getUpdateFlags().sendFaceToDirection(nextObstacle.getStart().getX() + delta.getX(), nextObstacle.getStart().getY() + delta.getY());
				stoner.getProfession().lock(4);
				nextObstacle.getType().getInteraction().start(stoner);
				nextObstacle.getType().getInteraction().onExecution(stoner, nextObstacle.getStart(), nextObstacle.getEnd());
				if (nextObstacle.getType().getInteraction().getPreMessage() != null) {
					stoner.send(new SendMessage(nextObstacle.getType().getInteraction().getPreMessage()));
	
			}
			if (nextObstacle.getNext() != null) {
				nextObstacle.getType().getInteraction().onCancellation(stoner);
				nextObstacle = nextObstacle.getNext();
			} else {
				this.stop();
				return;
			}
		} else if (stoner.getLocation().equals(end)) {
			Location delta = Utility.delta(nextObstacle == null ? start : nextObstacle.getStart(), nextObstacle == null ? end : nextObstacle.getEnd());
			stoner.getUpdateFlags().sendFaceToDirection((nextObstacle == null ? start.getX() : nextObstacle.getStart().getX()) + delta.getX(), (nextObstacle == null ? start.getY() : nextObstacle.getStart().getY()) + delta.getY());
			if (nextObstacle != null) {
					stoner.getProfession().lock(4);
					nextObstacle.getType().getInteraction().start(stoner);
					nextObstacle.getType().getInteraction().onExecution(stoner, nextObstacle.getStart(), nextObstacle.getEnd());
					if (nextObstacle.getType().getInteraction().getPreMessage() != null) {
						stoner.send(new SendMessage(nextObstacle.getType().getInteraction().getPreMessage()));
					
				}
				if (nextObstacle.getNext() != null) {
					nextObstacle.getType().getInteraction().onCancellation(stoner);
					nextObstacle = nextObstacle.getNext();
				} else {
					this.stop();
					return;
				}
			} else {
				this.stop();
				return;
			}
		}

		if (!started) {
			started = true;
			Location delta = Utility.delta(nextObstacle == null ? start : nextObstacle.getStart(), nextObstacle == null ? end : nextObstacle.getEnd());
			stoner.getUpdateFlags().sendFaceToDirection((nextObstacle == null ? start.getX() : nextObstacle.getStart().getX()) + delta.getX(), (nextObstacle == null ? start.getY() : nextObstacle.getStart().getY()) + delta.getY());
			onExecution(stoner, start, end);

			if (ordinal > -1 && Utility.random(45) == 0) {
				GroundItemHandler.add(new Item(11849, 1), end, stoner);
				stoner.send(new SendMessage("<col=C60DDE>There appears to be a wild Grace mark near you."));
			}

		}

		stoner.getMovementHandler().setForceMove(true);
		stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		}

		@Override
		public void onStop() {
		if (getPostMessage() != null) {
			stoner.send(new SendMessage(getPostMessage()));
		}


		if (ordinal > -1) {
			if (ordinal == 0) {
				stoner.getAttributes().set("EXERCISEMENT_FLAGS", 1 << ordinal);
			} else {
				stoner.getAttributes().set("EXERCISEMENT_FLAGS", stoner.getAttributes().getInt("EXERCISEMENT_FLAGS") | (1 << ordinal));
			}
		}


		stoner.teleport(nextObstacle != null ? nextObstacle.getEnd() : end);
		stoner.getRunEnergy().setRunning(RUNNING);
		stoner.getAnimations().set(ANIMATION);
		stoner.setAppearanceUpdateRequired(true);
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController((Controller) stoner.getAttributes().get("TEMP_CONTROLLER"));
		stoner.getMovementHandler().setForceMove(false);
		stoner.getMovementHandler().reset();
		stoner.getCombat().reset();
		onCancellation(stoner);
		}
	});
	}

	default boolean canExecute(Stoner stoner) {
	if (stoner.getProfession().locked()) {
		return false;
	}


	return true;
	}

	default boolean courseRewards(Stoner stoner, String course, int ordinal, int flags) {
	if (((int) stoner.getAttributes().get("EXERCISEMENT_FLAGS") & flags) != flags) {
		return false;
	}

	stoner.getAttributes().set("EXERCISEMENT_FLAGS", 0);
	return true;
	}
}