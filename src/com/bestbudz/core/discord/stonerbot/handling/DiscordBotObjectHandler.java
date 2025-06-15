package com.bestbudz.core.discord.stonerbot.handling;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple object detection for Discord bot
 */
public class DiscordBotObjectHandler {

	private final DiscordBotStoner bot;

	public DiscordBotObjectHandler(DiscordBotStoner bot) {
		this.bot = bot;
	}

	/**
	 * Find all objects within radius
	 */
	public Set<RSObject> findObjectsInRadius(Location center, int radius) {
		Set<RSObject> objects = new HashSet<>();

		int startX = center.getX() - radius;
		int endX = center.getX() + radius;
		int startY = center.getY() - radius;
		int endY = center.getY() + radius;

		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				if (calculateDistance(center, new Location(x, y, center.getZ())) <= radius) {
					RSObject object = Region.getObject(x, y, center.getZ());
					if (object != null) {
						objects.add(object);
					}
				}
			}
		}

		return objects;
	}

	/**
	 * Find specific object type in radius
	 */
	public Set<RSObject> findObjectsByType(Location center, int radius, int[] objectIds) {
		Set<RSObject> matchingObjects = new HashSet<>();
		Set<RSObject> allObjects = findObjectsInRadius(center, radius);

		for (RSObject object : allObjects) {
			for (int id : objectIds) {
				if (object.getId() == id) {
					matchingObjects.add(object);
					break;
				}
			}
		}

		return matchingObjects;
	}

	/**
	 * Calculate distance between locations
	 */
	private int calculateDistance(Location loc1, Location loc2) {
		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());
		return Math.max(deltaX, deltaY);
	}
}