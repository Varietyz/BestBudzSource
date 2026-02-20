package com.bestbudz.rs2.entity.pathfinding;

import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.Walking;

public class SimplePathWalker {

	public static boolean walkable(Entity entity, int dir) {
		return entity.getMovementHandler().canMoveTo(dir);
	}

	public static void walkToNextTile(Mob mob, Location target) {
		Location loc = mob.getLocation();

		if (loc.equals(target)) {
			return;
		}

		int x = loc.getX();
		int y = loc.getY();
		int dx = Integer.compare(target.getX(), x);
		int dy = Integer.compare(target.getY(), y);

		int toDir = GameConstants.getDirection(x, y, x + dx, y + dy);

		if (toDir != -1 && walkable(mob, toDir)) {
			Walking.walk(mob, toDir);
			return;
		}

		int horDir = GameConstants.getDirection(x, y, x + dx, y);
		int verDir = GameConstants.getDirection(x, y, x, y + dy);

		if (verDir != -1 && walkable(mob, verDir)) {
			Walking.walk(mob, verDir);
		} else if (horDir != -1 && walkable(mob, horDir)) {
			Walking.walk(mob, horDir);
		}

	}
}
