package com.bestbudz.core.task;

import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.rs2.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue {

  private static final Queue<Task> adding = new ConcurrentLinkedQueue<>();
  private static final Deque<Task> tasks = new ArrayDeque<>(256);

  public static void cancelHitsOnEntity(Entity e) {
    for (Task t : tasks) {
      if (t instanceof HitTask) {
        HitTask hit = (HitTask) t;
        if (hit.getEntity() == e) {
          hit.stop();
        }
      }
    }
  }

  public static void onMovement(Entity e) {
    LinkedList<Task> active = e.getTasks();
    if (active == null) return;

    for (Iterator<Task> it = active.iterator(); it.hasNext(); ) {
      Task t = it.next();
      if (t.getBreakType() == BreakType.ON_MOVE) {
        t.stop();
        it.remove();
      }
    }
  }

  public static void process() {

    Task task;
    while ((task = adding.poll()) != null) {
      tasks.add(task);
    }

    Iterator<Task> it = tasks.iterator();
    while (it.hasNext()) {
      Task t = it.next();
      try {
        if (t.stopped()) {
          if (t.isAssociated()) {
            t.getEntity().getTasks().remove(t);
          }
          t.onStop();
          it.remove();
          continue;
        }

        if (t.isAssociated()) {
          Entity e = t.getEntity();
          if (e == null || !e.isActive() || !e.getTasks().contains(t)) {
            t.onStop();
            it.remove();
            continue;
          }
        }

        t.run();

      } catch (Exception e) {
        e.printStackTrace();
        it.remove();
      }
    }
  }

  public static Task queue(Task task) {
    if (task.stopped()) return task;

    if (task.isAssociated()) {
      Entity e = task.getEntity();

      if (task.getStackType() == StackType.NEVER_STACK) {
        Iterator<Task> it = e.getTasks().iterator();
        while (it.hasNext()) {
          Task t = it.next();
          if (t.getStackType() == StackType.NEVER_STACK && t.getTaskId() == task.getTaskId()) {
            it.remove();
          }
        }
      }

      e.getTasks().add(task);
    }

    task.onStart();

    if (task.immediate()) {
      task.execute();
    }

    adding.add(task);
    return task;
  }
}
