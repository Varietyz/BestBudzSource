package com.bestbudz.rs2.entity;

public class Location {
	public static final int REGION_OFFSET = 6;
	public static final int VIEW_DISTANCE = 15;
	private short x;
	private short y;
	private short z;

	public Location() {}

	public Location(int x, int y) {
		this(x, y, 0);
	}

	public Location(int x, int y, int z) {
		this.x = (short) x;
		this.y = (short) y;
		this.z = (short) z;
	}

	public Location(Location other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	public Location(Location other, int z) {
		this.x = other.x;
		this.y = other.y;
		this.z = (short) z;
	}

	@Override
	public int hashCode() {
		return (x << 16) | (y << 8) | z;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Location)) return false;
		Location p = (Location) other;
		return x == p.x && y == p.y && z == p.z;
	}

	@Override
	public String toString() {
		return "Location(" + x + ", " + y + ", " + z + ")";
	}

	// Optimized accessors
	public int getLocalX() {
		return x - ((x >> 3) - REGION_OFFSET) * 8;
	}

	public int getLocalX(Location base) {
		return x - base.getRegionX() * 8;
	}

	public int getLocalY() {
		return y - ((y >> 3) - REGION_OFFSET) * 8;
	}

	public int getLocalY(Location base) {
		return y - base.getRegionY() * 8;
	}

	public int getRegionX() {
		return (x >> 3) - REGION_OFFSET;
	}

	public int getRegionY() {
		return (y >> 3) - REGION_OFFSET;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = (short) x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = (short) y;
	}

	public int getZ() {
		return z;
	}

	public Location setZ(int z) {
		this.z = (short) z;
		return this;
	}

	public void setAs(Location other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	public void move(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	// Inline the view distance check – avoids Utility.delta(...) allocation
	public boolean isViewableFrom(Location other) {
		return this.z == other.z &&
			Math.abs(this.x - other.x) <= VIEW_DISTANCE &&
			Math.abs(this.y - other.y) <= VIEW_DISTANCE;
	}

	// Area flags – make static cached zones for better performance if needed
	public boolean inKingBlackDragonArea() {
		return x >= 2250 && x <= 2290 && y >= 4670 && y <= 4714;
	}

	// Optional: extract location check to utility for reuse
	public boolean inBounds(Location sw, Location ne, boolean inclusive) {
		if (inclusive) {
			return x >= sw.x && x <= ne.x && y >= sw.y && y <= ne.y;
		}
		return x > sw.x && x < ne.x && y > sw.y && y < ne.y;
	}
}
