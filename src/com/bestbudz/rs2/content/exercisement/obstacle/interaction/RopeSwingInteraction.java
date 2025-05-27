package com.bestbudz.rs2.content.exercisement.obstacle.interaction;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.ForceMoveTask;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendAnimateObject;

public interface RopeSwingInteraction extends ObstacleInteraction {

  @Override
  default void start(Stoner stoner) {}

  @Override
  default void onExecution(Stoner stoner, Location start, Location end) {
    int modX = end.getX() - stoner.getLocation().getX();
    int modY = end.getY() - stoner.getLocation().getY();
    int xMod = Math.abs(modX) > Math.abs(modY) ? modX < 0 ? -1 : modX > 0 ? 1 : 0 : 0;
    int yMod = Math.abs(modY) > Math.abs(modY) ? modY < 0 ? -1 : modY > 0 ? 1 : 0 : 0;

    TaskQueue.queue(
        new Task(
            stoner,
            1,
            true,
            StackType.NEVER_STACK,
            BreakType.NEVER,
            TaskIdentifier.CURRENT_ACTION) {
          @Override
          public void execute() {
            if (stoner
                .getLocation()
                .equals(new Location(start.getX() + xMod, start.getY() + yMod))) {
              stop();
              return;
            }

            int dX = Integer.signum(start.getX() - stoner.getX());
            int dY = Integer.signum(start.getY() - stoner.getY());

            stoner.getMovementHandler().walkTo(dX + xMod, dY + yMod);
          }

          @Override
          public void onStop() {
            RSObject obj = (RSObject) stoner.getAttributes().get("EXERCISEMENT_OBJ");
            stoner.send(new SendAnimateObject(obj, 497));
            TaskQueue.queue(
                new ForceMoveTask(
                    stoner,
                    2,
                    new Location(stoner.getX() + xMod, stoner.getY() + yMod),
                    new Location(modX, modY),
                    751,
                    28,
                    66,
                    obj.getFace() == 0 ? 2 : 0));
          }
        });
  }

  @Override
  default void onCancellation(Stoner stoner) {}
}
