package com.bestbudz.rs2.entity.movement;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class MovementHandler {
	protected final Entity entity;
	protected int primaryDirection = -1;
	protected int secondaryDirection = -1;
	protected Location lastLocation = new Location(0, 0);
	protected Deque<Point> waypoints = new ConcurrentLinkedDeque<Point>();
	protected boolean flag = false;

	protected Location forceStart;
	protected Location forceEnd;
	protected short forceSpeed1;
	protected short forceSpeed2;
	protected byte forceDirection;

	protected boolean forceMove = false;

	private boolean forced = false;

	public MovementHandler(Entity entity) {
		this.entity = entity;
	}

	private void addStep(int x, int y) {
		if (waypoints.size() >= 100) {
			return;
		}
		Point last = waypoints.peekLast();
		int deltaX = x - last.getX();
		int deltaY = y - last.getY();
		int direction = Utility.direction(deltaX, deltaY);
		if (direction > -1) waypoints.add(new Point(x, y, direction));
	}

	public void addToPath(Location location) {
		if (waypoints.size() == 0) {
			reset();
		}

		// CRITICAL FIX: Reset combat target when manually moving (not combat following)
		if (entity.getCombat().getAssaulting() != null &&
			entity.getFollowing().getInteracting() == null) {
			entity.getCombat().setAssaulting(null);
			// Player is manually moving while having a combat target - clear it
			entity.getCombat().reset();
		}

		Point last = waypoints.peekLast();
		int deltaX = location.getX() - last.getX();
		int deltaY = location.getY() - last.getY();
		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		for (int i = 0; i < max; i++) {
			if (deltaX < 0) deltaX++;
			else if (deltaX > 0) {
				deltaX--;
			}
			if (deltaY < 0) deltaY++;
			else if (deltaY > 0) {
				deltaY--;
			}
			addStep(location.getX() - deltaX, location.getY() - deltaY);
		}
	}

	public abstract boolean canMoveTo(int paramInt);

	public abstract boolean canMoveTo(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

	public void finish() {
		waypoints.removeFirst();
	}

	public void flag() {
		flag = true;
		forced = false;
	}

	/**
	 * Checks if the entity is locked by combat animations
	 */
	private boolean isCombatAnimationLocked() {
		// Check if entity is performing a combat animation that should lock movement
		if (entity.getCombat().isPerformingCombatAction()) {
			return true;
		}

		// Check if entity has an animation lock timer
		if (entity.hasAnimationLock()) {
			return true;
		}

		// Check if current animation prevents movement (for specific combat animations)
		if (entity.getAnimation() != null && isCombatAnimation(entity.getAnimation().getId())) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if an animation ID is a combat animation that should lock movement
	 */
	private boolean isCombatAnimation(int animationId) {
		// Add your combat animation IDs here
		// These are common RuneScape combat animation IDs, adjust for your server
		switch (animationId) {
			case 422: // Basic melee attack
			case 423: // Melee attack variation
			case 1162: // Magic spell casting
			case 1161: // Magic spell casting variation
			case 426: // Ranged attack
			case 427: // Ranged attack variation
				// Add more combat animation IDs as needed
				return true;
			default:
				return false;
		}
	}

	public Location getForceStart() {
		return forceStart;
	}

	public void setForceStart(Location forceStart) {
		this.forceStart = forceStart;
	}

	public Location getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Location forceEnd) {
		this.forceEnd = forceEnd;
	}

	public short getForceSpeed1() {
		return forceSpeed1;
	}

	public void setForceSpeed1(short forceSpeed1) {
		this.forceSpeed1 = forceSpeed1;
	}

	public short getForceSpeed2() {
		return forceSpeed2;
	}

	public void setForceSpeed2(short forceSpeed2) {
		this.forceSpeed2 = forceSpeed2;
	}

	public byte getForceDirection() {
		return forceDirection;
	}

	public void setForceDirection(byte forceDirection) {
		this.forceDirection = forceDirection;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public int getPrimaryDirection() {
		return primaryDirection;
	}

	public void setPrimaryDirection(int primaryDirection) {
		this.primaryDirection = primaryDirection;
	}

	public int getSecondaryDirection() {
		return secondaryDirection;
	}

	public void setSecondaryDirection(int secondaryDirection) {
		this.secondaryDirection = secondaryDirection;
	}

	public boolean hasDirection() {
		return primaryDirection != -1;
	}

	public boolean isFlagged() {
		return flag;
	}

	public boolean isForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public boolean isForceMove() {
		return forceMove;
	}

	public void setForceMove(boolean forceMove) {
		this.forceMove = forceMove;
	}

	/**
	 * Enhanced moving() method that includes combat animation locks
	 */
	public boolean moving() {
		return (waypoints.size() > 0)
			&& (!entity.isFrozen())
			&& (!entity.isStunned())
			&& (!isCombatAnimationLocked()); // NEW: Check for combat animation locks
	}

	public abstract void process();

	public void reset() {
		waypoints.clear();

		Location p = entity.getLocation();
		waypoints.add(new Point(p.getX(), p.getY(), -1));
	}

	public void resetMoveDirections() {
		primaryDirection = -1;
		secondaryDirection = -1;
	}

	public void setPath(Deque<Point> path) {
		waypoints = path;
	}

	public void walkTo(int x, int y) {
		Location location = entity.getLocation();
		int newX = location.getX() + x;
		int newY = location.getY() + y;

		// CRITICAL FIX: Reset combat when manually walking
		if (!entity.getCombat().inCombat()) {
			entity.getCombat().setAssaulting(null);
			entity.getCombat().reset();
		}

		reset();
		addToPath(new Location(newX, newY));
		finish();
	}
}