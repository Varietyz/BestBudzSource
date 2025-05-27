package com.bestbudz.rs2.content.exercisement.obstacle.interaction;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.ForceMoveTask;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface ClimbOverInteraction extends ObstacleInteraction {

  @Override
  default void start(Stoner stoner) {}

  @Override
  default void onExecution(Stoner stoner, Location start, Location end) {
    stoner.getUpdateFlags().sendAnimation(new Animation(getAnimation()));
    TaskQueue.queue(
        new ForceMoveTask(stoner, 1, stoner.getLocation(), new Location(2, 0), 839, 0, 45, 1));
  }

  @Override
  default void onCancellation(Stoner stoner) {}
}
