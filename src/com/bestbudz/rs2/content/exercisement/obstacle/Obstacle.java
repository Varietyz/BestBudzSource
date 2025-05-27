package com.bestbudz.rs2.content.exercisement.obstacle;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public final class Obstacle {

	private final ObstacleType type;
	private final Location start;
	private final Location end;
	private final int ordinal;
	private final Obstacle next;

	private Obstacle(ObstacleBuilder builder) {
	type = builder.type;
	start = builder.start;
	end = builder.end;
	ordinal = builder.ordinal;
	next = builder.next;
	}

	public Location getStart() {
	return start;
	}

	public Location getEnd() {
	return end;
	}

	public Obstacle getNext() {
	return next;
	}

	public int getOrdinal() {
	return ordinal;
	}

	public ObstacleType getType() {
	return type;
	}

	@Override
	public String toString() {
	return "OBSTACLE [Type: " + type + ", Start: " + start + ", End: " + end + ", Ordinal: " + ordinal + "]";
	}

	public void execute(Stoner stoner) {
	type.execute(stoner, next, start, end, ordinal);
	}

	public static class ObstacleBuilder {
		// Required parameters
		private final ObstacleType type;
		private final Location start;
		private final Location end;

		// Optional parameters
		private int ordinal;
		private Obstacle next;

		public ObstacleBuilder(ObstacleType type, Location start, Location end) {
		this.type = type;
		this.end = end;
		this.start = start;
		ordinal = -1;
		}


		public ObstacleBuilder setOrdinal(int ordinal) {
		this.ordinal = ordinal;
		return this;
		}

		public ObstacleBuilder setNext(Obstacle next) {
		this.next = next;
		return this;
		}

		public Obstacle build() {
		return new Obstacle(this);
		}
	}
}