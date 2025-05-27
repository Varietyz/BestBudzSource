package com.bestbudz.rs2.content.exercisement.obstacle.interaction.rooftop.ardougne;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.exercisement.obstacle.interaction.ObstacleInteraction;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface ArdougneRoofJumpInteraction extends ObstacleInteraction {

  @Override
  default void start(Stoner stoner) {}

  @Override
  default void onExecution(Stoner stoner, Location start, Location end) {
    TaskQueue.queue(
        new Task(stoner, 1, true) {
          int ticks = 0;

          @Override
          public void execute() {
            switch (ticks++) {
              case 1:
                stoner.getUpdateFlags().sendFaceToDirection(2667, 3311);
                stoner.getUpdateFlags().sendAnimation(new Animation(2586));
                break;

              case 2:
                stoner.teleport(new Location(2667, 3311, 1));
                stoner.getUpdateFlags().sendAnimation(new Animation(2588));
                break;

              case 3:
                stoner.getUpdateFlags().sendFaceToDirection(2665, 3315);
                break;

              case 4:
                stoner.getUpdateFlags().sendAnimation(new Animation(2588));
                stoner.teleport(new Location(2665, 3315, 1));
                break;

              case 6:
                stoner.teleport(end);
                stoner.getUpdateFlags().sendAnimation(new Animation(2588));
                stop();
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
