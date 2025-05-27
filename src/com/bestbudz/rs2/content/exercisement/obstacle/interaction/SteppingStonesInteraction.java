package com.bestbudz.rs2.content.exercisement.obstacle.interaction;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.ForceMoveTask;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface SteppingStonesInteraction extends ObstacleInteraction {

  @Override
  default void start(Stoner stoner) {}

  @Override
  default void onExecution(Stoner stoner, Location start, Location end) {
    int dX = end.getX() - stoner.getLocation().getX();
    int dY = end.getY() - stoner.getLocation().getY();
    int modX = Integer.signum(dX);
    int modY = Integer.signum(dY);

    int totalSteps = Math.abs(modX) > Math.abs(modY) ? Math.abs(dX) : Math.abs(dY);
    TaskQueue.queue(
        new Task(stoner, 2, true) {
          int steps = 0;

          @Override
          public void execute() {
            TaskQueue.queue(
                new ForceMoveTask(
                    stoner,
                    0,
                    stoner.getLocation(),
                    new Location(modX, modY),
                    getAnimation(),
                    10,
                    26,
                    3));

            if (++steps == totalSteps) {
              stop();
            }
          }

          @Override
          public void onStop() {}
        });
  }

  @Override
  default void onCancellation(Stoner stoner) {}
}
