package com.bestbudz.rs2.content.combat.impl;

public class Assault {

	private int hitDelay;
	private int assaultDelay;

	public Assault(int hitDelay, int assaultDelay) {
	this.hitDelay = hitDelay;
	this.assaultDelay = assaultDelay;
	}

	public int getAssaultDelay() {
	return assaultDelay;
	}

	public int getHitDelay() {
	return hitDelay;
	}

	public void setAssaultDelay(int assaultDelay) {
	this.assaultDelay = assaultDelay;
	}

	public void setHitDelay(int hitDelay) {
	this.hitDelay = hitDelay;
	}
}
