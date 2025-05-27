package com.mayhem.core.cache.map;

import com.mayhem.rs2.entity.object.GameObject;

public class RSObject {

	private short x;
	private short y;
	private byte z;
	protected int id;
	private byte type;
	private byte direction;

	public RSObject(int x, int y, int z) {
		this.x = (short) x;
		this.y = (short) y;
		this.z = (byte) z;
	}

	public RSObject(int x, int y, int z, int id, int type, int direction) {
		this.x = (short) x;
		this.y = (short) y;
		this.z = (byte) z;
		this.id = id;
		this.type = (byte) type;
		this.direction = (byte) direction;
	}

	/**
	 * By sk8r -- Could use this as a cheap hack to convert RSObjects into
	 * GameObjects.. Then you change mining/etc to check for gameobjects, and
	 * add something to get the custom spawned ones from the active list in
	 * ObjectManager.
	 * 
	 * @return a new GameObject based on the RSObject.
	 */
	public GameObject fromRSObject() {
		return new GameObject(this.id, this.x, this.y, this.z, this.type, this.direction);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RSObject) {
			RSObject l = (RSObject) o;

			return l.getX() == getX() && l.getY() == getY() && l.getZ() == getZ();
		}

		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public int getFace() {
		return direction;
	}
}
