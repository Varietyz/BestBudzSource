package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;

public class GraphicTask extends Task {

  private final Graphic graphic;
  private final Entity entity;

  public GraphicTask(int delay, boolean immediate, Graphic graphic, Entity entity) {
    super(
        entity, delay, immediate, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
    this.graphic = graphic;
    this.entity = entity;
  }

	public GraphicTask(Entity taskOwner, int delay, boolean immediate, Graphic graphic, Entity targetEntity) {
		super(taskOwner, delay, immediate, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.graphic = graphic;
		this.entity = targetEntity; // This is the entity that gets the graphic
	}

  @Override
  public void execute() {

    entity.getUpdateFlags().sendGraphic(graphic);
    stop();
  }

  @Override
  public void onStop() {}
}
