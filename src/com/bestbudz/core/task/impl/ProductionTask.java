package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * <p>
 * A producing action is an action where on item is transformed into another,
 * typically this is in professions such as forging and handiness.
 * </p>
 * 
 * <p>
 * This class implements code related to all production-type professions, such
 * as dealing with the action itself, replacing the items and checking grades.
 * </p>
 * 
 * <p>
 * The individual handiness, forging, and other professions implement
 * functionality specific to them such as random events.
 * </p>
 * 
 * @author Graham Edgecombe
 * @author Jaybane <Scu11>
 */
public abstract class ProductionTask extends Task {

	/**
	 * Stoner instance because this is for a stoner
	 */
	protected Stoner stoner;

	/**
	 * This starts the actions animation and requirement checks, but prevents the
	 * production from immediately executing.
	 */
	protected boolean started = false;

	/**
	 * The cycle count.
	 */
	protected int cycleCount = 0;

	/**
	 * The amount of items to produce.
	 */
	private int productionCount = 0;

	public ProductionTask(Stoner stoner, int delay) {
	this(stoner, delay, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	}

	public ProductionTask(Stoner stoner, int delay, boolean immediate, StackType stackType, BreakType breakType, TaskIdentifier taskId) {
	super(stoner, delay, immediate, stackType, breakType, taskId);
	this.stoner = stoner;
	}

	/**
	 * Performs extra checks that a specific production event independently uses,
	 * e.g. checking for ingredients in thc-hempistry.
	 */
	public abstract boolean canProduce();

	@Override
	public void execute() {
	if (stoner.getProfession().getGrades()[getProfession()] < getRequiredGrade()) {
		DialogueManager.sendStatement(stoner, getInsufficentGradeMessage());
		stoner.getUpdateFlags().sendAnimation(new Animation(-1));
		this.stop();
		return;
	}
	for (Item productionItem : getConsumedItems()) {
		if (productionItem != null) {
			if (stoner.getBox().getItemAmount(productionItem.getId()) < productionItem.getAmount()) {
				if (noIngredients(productionItem) != null)
					stoner.getClient().queueOutgoingPacket(new SendMessage(noIngredients(productionItem)));
				stoner.getUpdateFlags().sendAnimation(new Animation(-1));
				this.stop();
				return;
			}
		}
	}
	if (!canProduce()) {
		this.stop();
		return;
	}
	if (!started) {
		started = true;
		if (getAnimation() != null) {
			stoner.getUpdateFlags().sendAnimation(getAnimation());
		}
		if (getGraphic() != null) {
			stoner.getUpdateFlags().sendGraphic(getGraphic());
		}

		productionCount = getProductionCount();
		cycleCount = getCycleCount();
		return;
	}

	if (cycleCount > 1) {
		cycleCount--;
	} else {

		if (getAnimation() != null && getAnimation().getId() > 0) {
			stoner.getUpdateFlags().sendAnimation(getAnimation());
		}
		if (getGraphic() != null && getGraphic().getId() > 0) {
			stoner.getUpdateFlags().sendGraphic(getGraphic());
		}

		cycleCount = getCycleCount();

		productionCount--;

		for (Item item : getConsumedItems()) {
			stoner.getBox().remove(item);
		}
		for (Item item : getRewards()) {
			stoner.getBox().add(item);
		}
		stoner.getClient().queueOutgoingPacket(new SendMessage(getSuccessfulProductionMessage()));
		stoner.getProfession().addExperience(getProfession(), getExperience());

		if (productionCount < 1) {
			stoner.getUpdateFlags().sendAnimation(new Animation(-1));
			this.stop();
			return;
		}
		for (Item item : getConsumedItems()) {
			if (stoner.getBox().getItemAmount(item.getId()) < item.getAmount()) {
				stoner.getUpdateFlags().sendAnimation(new Animation(-1));
				this.stop();
				return;
			}
		}
	}
	}

	/**
	 * Gets the animation played whilst producing the item.
	 * 
	 * @return The animation played whilst producing the item.
	 */
	public abstract Animation getAnimation();

	/**
	 * Gets the consumed item in the production of this item.
	 * 
	 * @return The consumed item in the production of this item.
	 */
	public abstract Item[] getConsumedItems();

	/**
	 * Gets the amount of cycles before the item is produced.
	 * 
	 * @return The amount of cycles before the item is produced.
	 */
	public abstract int getCycleCount();

	/**
	 * Gets the experience granted for each item that is successfully produced.
	 * 
	 * @return The experience granted for each item that is successfully produced.
	 */
	public abstract double getExperience();

	/**
	 * Gets the graphic played whilst producing the item.
	 * 
	 * @return The graphic played whilst producing the item.
	 */
	public abstract Graphic getGraphic();

	/**
	 * Gets the message sent when the Entity's grade is too low to produce this
	 * item.
	 * 
	 * @return The message sent when the Entity's grade is too low to produce this
	 *         item.
	 */
	public abstract String getInsufficentGradeMessage();

	/**
	 * Gets the amount of times an item is produced.
	 * 
	 * @return The amount of times an item is produced.
	 */
	public abstract int getProductionCount();

	/**
	 * Gets the required grade to produce this item.
	 * 
	 * @return The required grade to produce this item.
	 */
	public abstract int getRequiredGrade();

	/**
	 * Gets the rewarded items from production.
	 * 
	 * @return The rewarded items from production.
	 */
	public abstract Item[] getRewards();

	/**
	 * Gets the profession we are using to produce.
	 * 
	 * @return The profession we are using to produce.
	 */
	public abstract int getProfession();

	/**
	 * Gets the message sent when the Entity successfully produces an item.
	 * 
	 * @return The message sent when the Entity successfully produce an item.
	 */
	public abstract String getSuccessfulProductionMessage();

	public boolean isStarted() {
	return started;
	}

	/**
	 * Creates the production action for the specified mob.
	 * 
	 * @param mob
	 *                The mob to create the action for.
	 */

	public abstract String noIngredients(Item item);

	public void setCycleCount(int cycleCount) {
	this.cycleCount = cycleCount;
	}

	public void setProductionCount(int productionCount) {
	this.productionCount = productionCount;
	}

	public void setStarted(boolean started) {
	this.started = started;
	}
}
