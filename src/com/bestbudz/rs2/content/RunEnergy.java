package com.bestbudz.rs2.content;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateEnergy;

/**
 * Handles resting and restoring energy
 * 
 * @author Jaybane
 * 
 */
public class RunEnergy {

	public static final int RESTORE_TIMER = 2;
	public static final int REST_ANIMATION = 11786;
	public static final int STAND_UP_ANIMATION = 11788;
	private double energy = 100.0D;
	private int weight = 0;
	private boolean allowed = true;
	private boolean running = false;
	private boolean resting = false;
	private final Stoner stoner;

	public RunEnergy(Stoner stoner) {
	this.stoner = stoner;
	}

	/**
	 * Adds energy to the stoners current energy
	 * 
	 * @param amount
	 *                   The amount to add
	 */
	public void add(int amount) {
	energy += amount;
	if (energy > 100.0D) {
		energy = 100.0D;
	}
	update();
	}

	/**
	 * Gets if a stoner can run
	 * 
	 * @return
	 */
	public boolean canRun() {
	return energy > 0.0D;
	}

	/**
	 * Deducts energy from the stoner by percentage
	 * 
	 * @param percent
	 *                    The percentage to remove
	 */
	public void deduct(double percent) {
	energy -= (int) (energy * percent);
	if (energy < 0.0D) {
		energy = 0.0D;
	}
	update();
	}

	/**
	 * Deducts energy from the stoners energy
	 * 
	 * @param amount
	 *                   The amount to deduct
	 */
	public void deduct(int amount) {
	energy -= amount;
	if (energy < 0.0D) {
		energy = 0.0D;
	}
	update();
	}

	public int getEnergy() {
	return (int) energy;
	}

	public boolean isAllowed() {
	return allowed;
	}

	public boolean isResting() {
	return resting;
	}

	public boolean isRunning() {
	return running;
	}

	/**
	 * Handles what happens when a stoner is running
	 */
	public void onRun() {
	if ((StonerConstants.isOwner(stoner)) || ((stoner.getMaxGrades()[16] == 99) && (!stoner.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)))) {
		return;
	}

	energy -= (1.0D - stoner.getMaxGrades()[16] * 0.005D) * 1.0D * (stoner.getEquipment().getItems()[EquipmentConstants.BOOTS_SLOT] != null && stoner.getEquipment().getItems()[EquipmentConstants.BOOTS_SLOT].getId() == 88 ? 0.8 : 1.0);
	update();
	allowed = false;

	if (energy == 0.0D) {
		stoner.getClient().queueOutgoingPacket(new SendConfig(173, 0));
		stoner.getClient().queueOutgoingPacket(new SendMessage("You have run out of energy."));
		running = false;
	}
	}

	/**
	 * Resets the stoner running
	 */
	public void reset() {
	running = false;
	allowed = true;
	}

	/**
	 * Restores the stoners energy
	 */
	public void restoreAll() {
	energy = 100.0D;
	update();
	}

	public void setAllowed(boolean allowed) {
	this.allowed = allowed;
	}

	public void setEnergy(double energy) {
	this.energy = energy;
	}

	public void setRunning(boolean running) {
	this.running = running;
	}

	/**
	 * Handles the energy tick as well as resting
	 */
	public void tick() {
	TaskQueue.queue(new Task(stoner, 4, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.RUN_ENERGY) {
		@Override
		public void execute() {
		if ((allowed) && (energy < 100D)) {
			RunEnergy en = RunEnergy.this;
			en.energy = (en.energy + (resting ? 5.0D : (1.0D + stoner.getMaxGrades()[16] * 0.011D) * 1.0D));

			if (energy > 100.0D) {
				energy = 100.0D;
			}

			update();
		} else if (!allowed) {
			allowed = true;
		}
		if ((resting) && (energy == 100.0D))
			toggleResting();
		}

		@Override
		public void onStop() {
		}
	});
	}

	/**
	 * Toggles the resting to start the process
	 */
	public void toggleResting() {
	if (energy >= 100) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("Your energy is already full."));
		return;
	}

	resting = (!resting);

	if (!resting) {
		stoner.getUpdateFlags().sendAnimation(11788, 0);
		stoner.getEquipment().updateStonerAnimations();
		stoner.getClient().queueOutgoingPacket(new SendConfig(778, 0));
	} else {
		stoner.getUpdateFlags().sendAnimation(11786, 0);
		stoner.getAnimations().setStandEmote(11786);
		stoner.getMovementHandler().reset();
		stoner.getClient().queueOutgoingPacket(new SendConfig(778, 1));
	}

	stoner.setAppearanceUpdateRequired(true);
	}

	@Override
	public String toString() {
	return "RunEnergy [energy=" + energy + ", weight=" + weight + "]";
	}

	/**
	 * Updates the stoners energy client sided
	 */
	public void update() {
	stoner.getClient().queueOutgoingPacket(new SendUpdateEnergy(energy));
	}
}
