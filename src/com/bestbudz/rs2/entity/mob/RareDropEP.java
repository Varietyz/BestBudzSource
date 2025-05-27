package com.bestbudz.rs2.entity.mob;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;

public class RareDropEP {

	private double ep = 0;
	private byte received = 0;

	public RareDropEP() {
	}

	public void addReceived() {
	received++;
	}

	public void forHitOnMob(Stoner stoner, Mob mob, Hit hit) {
	if (hit.getDamage() > 0) {
		if (stoner.getController().equals(ControllerManager.DEFAULT_CONTROLLER) || stoner.getController().equals(ControllerManager.GOD_WARS_CONTROLLER) || stoner.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)) {

			ep += ((hit.getDamage()) / 5000.0) + ((mob.getGrades()[Professions.AEGIS]) / 4000.0);
		}
	}
	}

	public double getEp() {
	return ep;
	}

	public int getEpAddon() {
	return (int) ep;
	}

	public int getReceived() {
	return received;
	}

	public void reset() {
	ep = 0;
	}

	public void setEp(double ep) {
	this.ep = ep;
	}

	public void setReceived(int received) {
	this.received = (byte) received;
	}

}
