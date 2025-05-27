package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles ticking down the Resist Brisingr effect
 * 
 * @author Arithium
 * 
 */
public class AntifireTask extends Task {

	/**
	 * The cycles before it ends
	 */
	private int cycles;

	/**
	 * The stoner who drank the Resist Brisingr
	 */
	private final Stoner stoner;

	/**
	 * The potion is a super potion
	 */
	private final boolean isSuper;

	/**
	 * The potion is successfull
	 */
	private boolean success;

	/**
	 * Constructs a new AntiFireTask for the stoner
	 * 
	 * @param stoner
	 *                    The stoner who drank the Resist Brisingr
	 * @param isSuper
	 *                    If the potion is a super potion or not
	 */
	public AntifireTask(Stoner stoner, boolean isSuper) {
	super(stoner, 1, false, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
	this.cycles = 600;
	this.stoner = stoner;
	this.isSuper = isSuper;
	this.success = true;

	if (stoner.getAttributes().get("fire_potion_task") != null) {
		// cancels this task when starting another one
		((AntifireTask) stoner.getAttributes().get("fire_potion_task")).cycles = 600;
		success = false;
		return;
	}

	/**
	 * To cancel the previous task
	 */
	stoner.getAttributes().set("fire_resist", Boolean.FALSE);
	stoner.getAttributes().set("super_fire_resist", Boolean.FALSE);
	stoner.getAttributes().set("fire_potion_task", this);

	stoner.getAttributes().set(isSuper ? "super_fire_resist" : "fire_resist", Boolean.TRUE);
	}

	@Override
	public void execute() {
	if (stoner.isDead() || !success) {
		this.stop();
		return;
	}

	if ((!isSuper && !stoner.getAttributes().is("fire_resist")) || (isSuper && !stoner.getAttributes().is("super_fire_resist"))) {
		// cancels this task when starting another one
		this.stop();
		return;
	}
	if (cycles > 0) {
		cycles--;

		if (cycles == 100) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("@red@Your resistance to dragonfire is about to run out."));
		}

		if (cycles == 0) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("@red@Your resistance to dragonfire has run out."));
			this.stop();
			return;
		}
	}
	}

	@Override
	public void onStop() {
	if (success) {
		stoner.getAttributes().set(isSuper ? "super_fire_resist" : "fire_resist", Boolean.FALSE);
		stoner.getAttributes().remove("fire_potion_task");
	}
	}

}
