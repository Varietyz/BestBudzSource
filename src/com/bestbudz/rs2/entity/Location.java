package com.bestbudz.rs2.entity;

import com.bestbudz.core.util.Utility;

public class Location {
	private short x = 0;

	private short y = 0;

	private short z = 0;

	public Location() {
	}

	public Location(int x, int y) {
	this(x, y, 0);
	}

	public Location(int x, int y, int z) {
	this.x = ((short) x);
	this.y = ((short) y);
	this.z = ((short) z);
	}

	public Location(Location other) {
	x = ((short) other.getX());
	y = ((short) other.getY());
	z = ((short) other.getZ());
	}

	public Location(Location other, int z) {
	x = ((short) other.getX());
	y = ((short) other.getY());
	this.z = ((short) z);
	}

	public static boolean inGnomeCourse(Entity entity) {
	return entity.getLocation().inLocation(new Location(2469, 3414), new Location(2490, 3440), true) && entity.getLocation().getZ() == 0;
	}

	public static boolean inBarbarianCourse(Entity entity) {
	return entity.getLocation().inLocation(new Location(2530, 3543), new Location(2553, 3556), true) && entity.getLocation().getZ() == 0;
	}

	public static boolean inWildernessCourse(Entity entity) {
	return entity.getLocation().inLocation(new Location(2992, 3931), new Location(3007, 3961), true) && entity.getLocation().getZ() == 0;
	}

	@Override
	public int hashCode() {
	return x << 16 | y << 8 | z;
	}

	@Override
	public boolean equals(Object other) {
	if ((other instanceof Location)) {
		Location p = (Location) other;
		return (x == p.x) && (y == p.y) && (z == p.z);
	}
	return false;
	}

	@Override
	public String toString() {
	return "Location(" + x + ", " + y + ", " + z + ")";
	}

	public int getLocalX() {
	return getLocalX(this);
	}

	public int getLocalX(Location base) {
	return x - 8 * base.getRegionX();
	}

	public int getLocalY() {
	return getLocalY(this);
	}

	public int getLocalY(Location base) {
	return y - 8 * base.getRegionY();
	}

	public int getRegionX() {
	return (x >> 3) - 6;
	}

	public int getRegionY() {
	return (y >> 3) - 6;
	}

	public int getX() {
	return x;
	}

	public void setX(int x) {
	this.x = ((short) x);
	}

	public int getY() {
	return y;
	}

	public void setY(int y) {
	this.y = ((short) y);
	}

	public int getZ() {
	return z;
	}

	public Location setZ(int z) {
	this.z = ((short) z);
	return this;
	}

	public boolean inKingBlackDragonArea() {
	return x >= 2250 && x <= 2290 && y >= 4670 && y <= 4714;
	}

	public boolean isViewableFrom(Location other) {
	Location p = Utility.delta(this, other);
	return (other.z == z) && (p.x <= 14) && (p.x >= -15) && (p.y <= 14) && (p.y >= -15);
	}

	public void move(int amountX, int amountY) {
	x = ((short) (x + amountX));
	y = ((short) (y + amountY));
	}

	public void setAs(Location other) {
	x = other.x;
	y = other.y;
	z = other.z;
	}

	public boolean inLocation(Location southWest, Location northEast, boolean inclusive) {
	return !inclusive ? this.x > southWest.getX() && this.x < northEast.getX() && this.y > southWest.getY() && this.y < northEast.getY() : this.x >= southWest.getX() && this.x <= northEast.getX() && this.y >= southWest.getY() && this.y <= northEast.getY();
	}

	public final boolean inBarrows() {
	return (x > 3539 && x < 3582 && y >= 9675 && y < 9722);
	}
}
