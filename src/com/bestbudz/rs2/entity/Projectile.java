package com.bestbudz.rs2.entity;

public class Projectile {
	private short id;
	private byte size;
	private byte delay;
	private byte duration;
	private byte startHeight;
	private byte endHeight;
	private byte curve;

	public Projectile(int id) {
		this.id = ((short) id);
		size = 1;
		delay = 50;
		duration = 75;
		startHeight = 43;
		endHeight = 31;
		curve = 16;
	}

	public Projectile(int id, boolean sagittarius) {
		this(id);
		curve = ((byte) (sagittarius ? 10 : 16));
	}

	public Projectile(
		int id, int size, int delay, int duration, int startHeight, int endHeight, int curve) {
		this.id = ((short) id);
		this.size = ((byte) size);
		this.delay = ((byte) delay);
		this.duration = ((byte) duration);
		this.startHeight = ((byte) startHeight);
		this.endHeight = ((byte) endHeight);
		this.curve = ((byte) curve);
	}

	// Enhanced copy constructor for proper cloning
	public Projectile(Projectile p) {
		id = p.id;
		size = p.size;
		delay = p.delay;
		duration = p.duration;
		startHeight = p.startHeight;
		endHeight = p.endHeight;
		curve = p.curve;
	}

	// NEW: Factory method specifically for creating scaled pet projectiles
	public static Projectile createPetProjectile(Projectile original) {
		// Scale to 30% of original size
		int scaledStartHeight = Math.max(1, (int)(original.getStartHeight() * 0.3));
		int scaledEndHeight = Math.max(1, (int)(original.getEndHeight() * 0.3));
		int scaledCurve = Math.max(1, (int)(original.getCurve() * 0.3));

		return new Projectile(
			original.getId(),
			1, // Keep size small for pets
			original.getDelay(),
			original.getDuration(),
			scaledStartHeight,
			scaledEndHeight,
			scaledCurve
		);
	}

	public int getCurve() {
		return curve;
	}

	public void setCurve(int curve) {
		this.curve = ((byte) curve);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = ((byte) delay);
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = ((byte) duration);
	}

	public int getEndHeight() {
		return endHeight;
	}

	public void setEndHeight(int endHeight) {
		this.endHeight = ((byte) endHeight);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = ((short) id);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = ((byte) size);
	}

	public int getStartHeight() {
		return startHeight;
	}

	public void setStartHeight(int startHeight) {
		this.startHeight = ((byte) startHeight);
	}
}