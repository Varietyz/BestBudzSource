package com.bestbudz.core.definitions;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;

public class WeaponDefinition {

	private short id;
	private boolean twoHanded;
	private CombatTypes type;
	private short[] assaultAnimations;
	private byte[] assaultSpeeds;
	private short block;
	private short stand;
	private short walk;
	private short run;
	private int sidebarId;
	private int standTurn;
	private int turn180;
	private int turn90CW;
	private int turn90CCW;

	public short[] getAssaultAnimations() {
	return assaultAnimations;
	}

	public byte[] getAssaultSpeeds() {
	return assaultSpeeds;
	}

	public int getBlock() {
	return block;
	}

	public int getId() {
	return id;
	}

	public int getRun() {
	return run;
	}

	public int getSidebarId() {
	return sidebarId;
	}

	public int getStand() {
	return stand;
	}

	public int getStandTurn() {
	return standTurn;
	}

	public int getTurn180() {
	return turn180;
	}

	public int getTurn90CCW() {
	return turn90CCW;
	}

	public int getTurn90CW() {
	return turn90CW;
	}

	public CombatTypes getType() {
	return type;
	}

	public int getWalk() {
	return walk;
	}

	public boolean isTwoHanded() {
	return twoHanded;
	}
}
