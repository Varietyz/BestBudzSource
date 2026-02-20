package com.bestbudz.rs2.entity;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProjectile;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStillGraphic;

public class WorldMessageManager {

	public void sendGlobalMessage(Stoner[] stoners, String message, boolean format) {
		message = (format ? "<col=255>" : "") + message + (format ? "</col>" : "");

		for (Stoner p : stoners)
			if ((p != null) && (p.isActive()))
				p.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	public void sendGlobalMessage(Stoner[] stoners, String message) {
		for (Stoner i : stoners) {
			if (i != null) {
				i.getClient().queueOutgoingPacket(new SendMessage(message));
			}
		}
	}

	public void sendGlobalMessage(Stoner[] stoners, String message, Stoner exceptions) {
		for (Stoner i : stoners) {
			if (i != null) {
				if (i != exceptions) i.getClient().queueOutgoingPacket(new SendMessage(message));
			}
		}
	}

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

	public void sendStillGraphic(Stoner[] stoners, int id, int delay, Location location) {
		for (Stoner stoner : stoners)
			if ((stoner != null) && (location.isViewableFrom(stoner.getLocation())))
				stoner.getClient().queueOutgoingPacket(new SendStillGraphic(id, location, delay));
	}

	public void sendRegionMessage(Stoner[] stoners, String message, Location location) {
		for (Stoner stoner : stoners) {
			if (stoner != null && location.isViewableFrom(stoner.getLocation())) {
				stoner.send(new SendMessage(message));
			}
		}
	}
}
