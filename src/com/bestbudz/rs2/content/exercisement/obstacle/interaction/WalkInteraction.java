package com.bestbudz.rs2.content.exercisement.obstacle.interaction;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface WalkInteraction extends ObstacleInteraction {

	@Override
	public default void start(Stoner stoner) {
	stoner.getAnimations().setRunEmote(getAnimation());
	stoner.getAnimations().setWalkEmote(getAnimation());
	stoner.getAnimations().setStandEmote(getAnimation());
	}

	@Override
	public default void onExecution(Stoner stoner, Location start, Location end) {
	int xDiff = -stoner.getLocation().getX() + end.getX();
	int yDiff = -stoner.getLocation().getY() + end.getY();

	stoner.getMovementHandler().reset();
	stoner.getCombat().reset();
	stoner.getMovementHandler().walkTo(xDiff, yDiff);
	stoner.setAppearanceUpdateRequired(true);
	}

	@Override
	public default void onCancellation(Stoner stoner) {
	}
}