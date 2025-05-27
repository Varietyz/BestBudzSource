package com.bestbudz.core.task;

import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Entity;

public abstract class Task {

  private final TaskIdentifier taskId;
  private final boolean immediate;
  private final Entity entity;
  private final StackType stackType;
  private final BreakType breakType;
  private short delay;
  private short position = 0;
  private boolean stopped = false;

  public Task(Entity entity, int delay) {
    this.entity = entity;
    this.delay = (short) delay;
    this.immediate = false;
    breakType = BreakType.NEVER;
    stackType = StackType.STACK;
    taskId = TaskIdentifier.CURRENT_ACTION;
  }

  public Task(Entity entity, int delay, boolean immediate) {
    this.entity = entity;
    this.delay = (short) delay;
    this.immediate = immediate;
    breakType = BreakType.NEVER;
    stackType = StackType.STACK;
    taskId = TaskIdentifier.CURRENT_ACTION;
  }

  public Task(
      Entity entity,
      int delay,
      boolean immediate,
      StackType stackType,
      BreakType breakType,
      TaskIdentifier taskId) {
    this.delay = (short) delay;
    this.immediate = immediate;
    this.entity = entity;
    this.breakType = breakType;
    this.stackType = stackType;
    this.taskId = taskId;
  }

  public Task(int delay) {
    entity = null;
    this.delay = (short) delay;
    this.immediate = false;
    breakType = BreakType.NEVER;
    stackType = StackType.STACK;
    taskId = TaskIdentifier.CURRENT_ACTION;
  }

  public Task(int delay, boolean immediate) {
    entity = null;
    this.delay = (short) delay;
    this.immediate = immediate;
    breakType = BreakType.NEVER;
    stackType = StackType.STACK;
    taskId = TaskIdentifier.CURRENT_ACTION;
  }

  public Task(
      int delay,
      boolean immediate,
      StackType stackType,
      BreakType breakType,
      TaskIdentifier taskId) {
    entity = null;
    this.delay = (short) delay;
    this.immediate = immediate;
    this.breakType = breakType;
    this.stackType = stackType;
    this.taskId = taskId;
  }

  public abstract void execute();

  public BreakType getBreakType() {
    return breakType;
  }

  public Entity getEntity() {
    return entity;
  }

  public int getPosition() {
    return position;
  }

  public StackType getStackType() {
    return stackType;
  }

  public TaskIdentifier getTaskId() {
    return taskId;
  }

  public boolean immediate() {
    return immediate;
  }

  public boolean isAssociateActive() {
    return entity != null && entity.isActive();
  }

  public boolean isAssociated() {
    return entity != null;
  }

  public abstract void onStop();

  public void reset() {
    position = 0;
  }

  public void run() {
    position++;
    if (position >= delay) {
      execute();
      reset();
    }
  }

  public void setTaskDelay(int ticks) {
    if (ticks < 0) {
      throw new IllegalArgumentException("Tick amount must be positive.");
    }

    this.delay = (short) ticks;
  }

  public void stop() {
    stopped = true;
  }

  public boolean stopped() {
    if (stopped) return true;
    if (entity == null) return false;
    if (!entity.isActive()) return true;
    if (breakType == BreakType.ON_MOVE) {
      var move = entity.getMovementHandler();
      return move.isFlagged() && !move.isForced();
    }
    return false;
  }

  public void onStart() {}

  public enum BreakType {
    NEVER,
    ON_MOVE
  }

  public enum StackType {
    STACK,
    NEVER_STACK
  }
}
