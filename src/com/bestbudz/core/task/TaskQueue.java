package com.bestbudz.core.task;

import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.rs2.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue {

  private static final Queue<Task> adding = new ConcurrentLinkedQueue<>();
  private static final Deque<Task> tasks = new ArrayDeque<>(256); // Better locality

  /** Cancels all HitTasks on a specific entity */
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

  /** Handles movement-sensitive tasks */
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

  /** Processes all pending and active tasks each tick */
  public static void process() {
    // Phase 1: Move new tasks into main task pool
    Task task;
    while ((task = adding.poll()) != null) {
      tasks.add(task);
    }

    // Phase 2: Execute or discard tasks
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

  /** Queues a new task for execution */
  public static Task queue(Task task) {
    if (task.stopped()) return task;

    if (task.isAssociated()) {
      Entity e = task.getEntity();

      // Deduplication for NEVER_STACK tasks
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
