package com.bestbudz.core.task.impl;

import java.awt.Point;
import java.awt.Rectangle;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.Area;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Managment of the Wilderness Teleportation Obelisk
 */
public class ObeliskTick extends Task {

	private static final int TICKS = 6;
	private static final int ACTIVE = 14825;

	private Obelisk obelisk;

	public ObeliskTick(Obelisk obelisk) {
	super(TICKS, true);
	this.obelisk = obelisk;
	}

	@Override
	public void execute() {
	if (!obelisk.active) {
		obelisk.active = true;
		activateObelisk(obelisk);
		return;
	}
	stop();
	obelisk.active = false;
	Obelisk to;
	while (true) {
		int random = Utility.randomNumber(Obelisk.values().length);
		if (Obelisk.values()[random] == obelisk)
			continue;
		to = Obelisk.values()[random];
		break;
	}
	for (Stoner stoner : World.getStoners()) {
		if (stoner == null)
			continue;
		if (obelisk.getArea().contains(new Point(stoner.getLocation().getX(), stoner.getLocation().getY()))) {
			teleport(stoner, to);
		}
	}
	Location[] teleArea = obelisk.getGfxArea().calculateAllLocations();
	for (Location Location : teleArea) {
		World.sendStillGraphic(342, 0, Location);
	}
	}

	private void teleport(Stoner stoner, Obelisk to) {
	int deltaX = stoner.getLocation().getX() - obelisk.Location.getX();
	int deltaY = stoner.getLocation().getY() - obelisk.Location.getY();
	stoner.getMage().doWildernessTeleport(to.Location.getX() + deltaX, to.Location.getY() + deltaY, to.Location.getZ(), TeleportTypes.OBELISK);
	}

	private void activateObelisk(Obelisk obelisk) {
	for (Location pillar : obelisk.pillars) {
		new GameObject(ACTIVE, pillar.getX(), pillar.getY(), pillar.getZ(), 0, 10);
	}
	}

	private static Location[] getObeliskLocations(Obelisk obelisk) {
	Location location = obelisk.Location;
	int i = 0;
	Location[] locations = new Location[4];
	for (int xMod = 0; xMod <= 4; xMod += 4) {
		for (int yMod = 0; yMod <= 4; yMod += 4) {
			locations[i++] = new Location(location.getX() + xMod, location.getY() + yMod, location.getZ());
		}
	}
	return locations;
	}

	public static void main(String[] args) {
	getObeliskLocations(Obelisk.A);
	}

	public static boolean clickObelisk(int objectId) {
	for (Obelisk obelisk : Obelisk.values()) {
		if (obelisk.pillarId == objectId) {
			if (obelisk.active)
				return true;
			Task task = new ObeliskTick(obelisk);
			TaskQueue.queue(task);
			return true;
		}
	}
	return false;
	}

	public enum Obelisk {

		A(14829, new Location(3154, 3618)),
		B(14830, new Location(3225, 3665)),
		C(14827, new Location(3033, 3730)),
		D(14828, new Location(3104, 3792)),
		E(14826, new Location(2978, 3864)),
		F(14831, new Location(3305, 3914));

		Location Location;
		boolean active;
		Rectangle area;
		Area gfxArea;
		Location[] pillars;
		int pillarId;

		Obelisk(int pillarId, Location Location) {
		this.pillarId = pillarId;
		this.Location = Location;
		this.active = false;
		this.gfxArea = Area.areaFromCorner(new com.bestbudz.rs2.entity.Location(Location.getX() + 1, Location.getY() + 1, Location.getZ()), 2, 2);
		this.area = new Rectangle(Location.getX(), Location.getY(), 5, 5);
		this.pillars = getObeliskLocations(this);
		}

		public Rectangle getArea() {
		return area;
		}

		public Area getGfxArea() {
		return gfxArea;
		}
	}

	@Override
	public void onStop() {
	obelisk.active = false;
	}

}