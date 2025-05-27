package com.bestbudz.rs2.entity.item.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class GroundItemHandler {

	public static final int SHOW_GROUND_ITEM = 100;
	public static final int REMOVE_GROUND_ITEM = 350;
	public static final int MAX_GLOBALIZATION = 10;
	public static final int MAX_REMOVAL = 10;
	private static final List<GroundItem> active = new LinkedList<GroundItem>();
	private static final List<GroundItem> globalizeQueue = new LinkedList<GroundItem>();

	public static boolean add(GroundItem groundItem) {
	Stoner owner = groundItem.getOwner();

	if ((owner != null) && (owner.getGroundItems().stack(groundItem))) {
		return true;
	}

	if ((owner != null) && (visible(owner, groundItem))) {
		owner.getGroundItems().add(groundItem);
	}

	active.add(groundItem);

	return true;
	}

	public static boolean add(Item item, Location location, Stoner stoner) {
	GroundItem groundItem = new GroundItem(item, new Location(location), stoner == null ? null : stoner.getUsername());
	add(groundItem);
	return true;
	}

	public static boolean add(Item item, Location location, Stoner stoner, Stoner include) {
	GroundItem groundItem = new GroundItem(item, new Location(location), stoner == null ? null : stoner.getUsername());
	groundItem.include(stoner == null ? null : stoner.getUsername());
	add(groundItem);
	return true;
	}

	public static boolean add(Item item, Location location, Stoner stoner, int time) {
	GroundItem groundItem = new GroundItem(item, new Location(location), stoner == null ? null : stoner.getUsername());

	if (time >= 0) {
		groundItem.setTime(time);
	}

	return add(groundItem);
	}

	public static boolean exists(GroundItem g) {
	return active.contains(g);
	}

	public static List<GroundItem> getActive() {
	return active;
	}

	/**
	 * name = stoner username your searching for specific = if this item is owned by
	 * this stoner
	 */
	public static GroundItem getGroundItem(int id, int x, int y, int z, String name, boolean specific) {
	long longAsName = name == null ? -1 : Utility.nameToLong(name);

	Location l = new Location(x, y, z);

	for (Iterator<GroundItem> i = active.iterator(); i.hasNext();) {
		GroundItem g = i.next();

		if (g.getLocation().equals(l) && g.exists()) {
			if (longAsName != -1 && longAsName == g.getLongOwnerName() && g.getItem().getId() == id || !specific && g.isGlobal() && g.getItem().getId() == id) {
				return g;
			}
		}
	}

	return null;
	}

	public static GroundItem getNonGlobalGroundItem(int id, int x, int y, int z, long name) {
	Location l = new Location(x, y, z);

	for (Iterator<GroundItem> i = active.iterator(); i.hasNext();) {
		GroundItem g = i.next();

		if ((g.getLocation().equals(l)) && (!g.isGlobal()) && (g.exists())) {
			if ((g.getLongOwnerName() == name) && (g.getItem().getId() == id)) {
				return g;
			}
		}
	}
	return null;
	}

	public static Region getRegion(GroundItem groundItem) {
	return Region.getRegion(groundItem.getLocation().getX(), groundItem.getLocation().getY());
	}

	public static void globalize(GroundItem groundItem) {
	globalizeQueue.add(groundItem);
	}

	public static void process() {
	synchronized (active) {
		for (Iterator<GroundItem> i = active.iterator(); i.hasNext();) {
			GroundItem groundItem = i.next();

			groundItem.countdown();

			if (groundItem.globalize()) {
				globalize(groundItem);
			}

			if (groundItem.remove()) {
				groundItem.erase();

				if (!groundItem.isGlobal()) {
					Stoner owner = groundItem.getOwner();

					if ((owner != null) && (visible(owner, groundItem)))
						owner.getGroundItems().remove(groundItem);
				} else {
					for (int k = 1; k < World.getStoners().length; k++) {
						Stoner stoner = World.getStoners()[k];

						if (stoner != null) {
							if (visible(stoner, groundItem)) {
								stoner.getGroundItems().remove(groundItem);
							}
						}
					}
				}
				i.remove();
			}
		}
	}

	for (Iterator<GroundItem> i = globalizeQueue.iterator(); i.hasNext();) {
		GroundItem groundItem = i.next();

		if (!groundItem.exists()) {
			i.remove();
		} else {
			groundItem.setGlobal(true);

			Stoner owner = groundItem.getOwner();

			for (int k = 1; k < World.getStoners().length; k++) {
				Stoner stoner = World.getStoners()[k];

				if ((stoner != null) && ((owner == null) || (!stoner.equals(owner)))) {
					if (visible(stoner, groundItem)) {
						stoner.getGroundItems().add(groundItem);
					}
				}
			}
			i.remove();
		}
	}
	}

	public static boolean remove(GroundItem groundItem) {
	if (groundItem.isGlobal) {
		GlobalItemHandler.createRespawnTask(groundItem);
	}
	groundItem.erase();

	if (!groundItem.isGlobal()) {
		Stoner owner = groundItem.getOwner();

		if ((owner != null) && (visible(owner, groundItem)))
			owner.getGroundItems().remove(groundItem);
	} else {
		for (int k = 1; k < World.getStoners().length; k++) {
			Stoner stoner = World.getStoners()[k];

			if (stoner != null) {
				if (visible(stoner, groundItem)) {
					stoner.getGroundItems().remove(groundItem);
				}
			}
		}
	}
	active.remove(groundItem);

	return true;
	}

	public static boolean visible(Stoner stoner, GroundItem groundItem) {
	Stoner owner = groundItem.getOwner();

	if ((stoner.withinRegion(groundItem.getLocation())) && (stoner.getLocation().getZ() == groundItem.getLocation().getZ()) && ((groundItem.isGlobal()) || ((owner != null) && (stoner.equals(owner))))) {
		return true;
	}

	return false;
	}
}
