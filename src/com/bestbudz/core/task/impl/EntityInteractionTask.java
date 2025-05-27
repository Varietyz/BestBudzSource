package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Used to handle npc interactivity such as pickpocketing.
 * 
 * 
 */
public abstract class EntityInteractionTask extends Task {

	public EntityInteractionTask(Stoner entity, int ticks) {
	super(entity, ticks);
	}

	/**
	 * 
	 * @return
	 */
	public abstract Item[] getConsumedItems();

	/**
	 * The message when you do not have the grade required to interact with the
	 * entity.
	 * 
	 * @return the message to display.
	 */
	public abstract String getInsufficentGradeMessage();

	/**
	 * 
	 * @return
	 */
	public abstract Mob getInteractingMob();

	/**
	 * The message when you begin to interact with the entity.
	 * 
	 * @return the message to display.
	 */
	public abstract String getInteractionMessage();

	/**
	 * 
	 * @return
	 */
	public abstract short getRequiredGrade();

	/**
	 * 
	 * @return
	 */
	public abstract Item[] getRewards();

	/**
	 * The message when you succeed to interact as planned with the entity.
	 * 
	 * @return the message to display.
	 */
	public abstract String getSuccessfulInteractionMessage();

	/**
	 * The message when you fail to interact as planned with the entity.
	 * 
	 * @return the message to display.
	 */
	public abstract String getUnsuccessfulInteractionMessage();

}
