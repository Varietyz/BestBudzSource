package com.bestbudz.rs2.content.exercisement.obstacle.interaction.rooftop.ardougne;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.exercisement.obstacle.interaction.ObstacleInteraction;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface ArdougneWallClimbInteraction extends ObstacleInteraction {

  @Override
  default void start(Stoner stoner) {
    stoner.getUpdateFlags().sendFaceToDirection(stoner.getX(), stoner.getY() + 1);
  }

  @Override
  default void onExecution(Stoner stoner, Location start, Location end) {
    TaskQueue.queue(
        new Task(stoner, 1, true) {
          int ticks = 0;

          @Override
          public void execute() {
            switch (ticks++) {
              case 1:
                stoner.getUpdateFlags().sendAnimation(new Animation(737));
                break;

              case 2:
                stoner.getUpdateFlags().sendAnimation(new Animation(737));
                stoner.teleport(new Location(start.getX(), start.getY(), 1));
                break;

              case 3:
                stoner.getUpdateFlags().sendAnimation(new Animation(737));
                stoner.teleport(new Location(start.getX(), start.getY(), 2));
                break;

              case 4:
                stoner.getUpdateFlags().sendAnimation(new Animation(2588));
                stoner.teleport(end);
                break;
            }
          }

          @Override
          public void onStop() {}
        });
  }

  @Override
  default void onCancellation(Stoner stoner) {}
}
