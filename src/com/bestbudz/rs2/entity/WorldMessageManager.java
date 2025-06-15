package com.bestbudz.rs2.entity;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProjectile;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStillGraphic;

/**
 * Handles all messaging and communication operations for the World
 * Extracted from World.java with 1:1 logic preservation
 */
public class WorldMessageManager {

	/**
	 * Send global message with formatting option
	 */
	public void sendGlobalMessage(Stoner[] stoners, String message, boolean format) {
		message = (format ? "<col=255>" : "") + message + (format ? "</col>" : "");

		for (Stoner p : stoners)
			if ((p != null) && (p.isActive()))
				p.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	/**
	 * Send global message to all players
	 */
	public void sendGlobalMessage(Stoner[] stoners, String message) {
		for (Stoner i : stoners) {
			if (i != null) {
				i.getClient().queueOutgoingPacket(new SendMessage(message));
			}
		}
	}

	/**
	 * Send global message with exceptions
	 */
	public void sendGlobalMessage(Stoner[] stoners, String message, Stoner exceptions) {
		for (Stoner i : stoners) {
			if (i != null) {
				if (i != exceptions) i.getClient().queueOutgoingPacket(new SendMessage(message));
			}
		}
	}

	/**
	 * Send projectile to all players in range
	 */
	public void sendProjectile(Stoner[] stoners, Projectile projectile, Location pLocation,
							   int lockon, byte offsetX, byte offsetY) {
		for (Stoner stoner : stoners)
			if (stoner != null) {
				if (pLocation.isViewableFrom(stoner.getLocation()))
					stoner
						.getClient()
						.queueOutgoingPacket(
							new SendProjectile(stoner, projectile, pLocation, lockon, offsetX, offsetY));
			}
	}

	/**
	 * Send still graphic to all players in range
	 */
	public void sendStillGraphic(Stoner[] stoners, int id, int delay, Location location) {
		for (Stoner stoner : stoners)
			if ((stoner != null) && (location.isViewableFrom(stoner.getLocation())))
				stoner.getClient().queueOutgoingPacket(new SendStillGraphic(id, location, delay));
	}

	/**
	 * Send message to all players in a specific region
	 */
	public void sendRegionMessage(Stoner[] stoners, String message, Location location) {
		for (Stoner stoner : stoners) {
			if (stoner != null && location.isViewableFrom(stoner.getLocation())) {
				stoner.send(new SendMessage(message));
			}
		}
	}
}