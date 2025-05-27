package com.bestbudz.rs2.content.dwarfcannon;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.ForceMovementTask;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DwarfMultiCannon {

	public static final int CANNONBALL_ITEM_ID = 2;
	public static final int BASE_ITEM_ID = 6;
	public static final int STAND_ITEM_ID = 8;
	public static final int BARRELS_ITEM_ID = 10;
	public static final int FURNACE_ITEM_ID = 12;
	public static final int DWARF_MULTI_CANNON_OBJECT_ID = 6;
	public static final int CANNON_BASE_OBJECT_ID = 7;
	public static final int CANNON_STAND_OBJECT_ID = 8;
	public static final int CANNON_BARRELS_OBJECT_ID = 9;
	public static final String CANNON_ATTRIBUTE_KEY = "dwarfmulticannon";

	public static DwarfCannon getCannon(Stoner stoner) {
	return (DwarfCannon) stoner.getAttributes().get("dwarfmulticannon");
	}

	public static boolean hasCannon(Stoner stoner) {
	return stoner.getAttributes().get("dwarfmulticannon") != null;
	}

	public static final boolean isCannonSetupClear(Stoner stoner) {
	if (!stoner.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
		return false;
	}

	int x = stoner.getLocation().getX();
	int y = stoner.getLocation().getY();
	int z = stoner.getLocation().getZ();

	Region r = Region.getRegion(x, y);

	for (int i = 0; i < 8; i++) {
		if (!r.canMove(x, y, z, i)) {
			return false;
		}
	}

	int x2 = x + com.bestbudz.rs2.GameConstants.DIR[3][0];
	int y2 = y + com.bestbudz.rs2.GameConstants.DIR[3][1];

	if (!Region.getRegion(x2, y2).canMove(x2, y2, z, 3)) {
		return false;
	}

	return true;
	}

	public static final boolean setCannonBase(Stoner stoner, int id) {
	if (id == 6) {
		if (isCannonSetupClear(stoner)) {
			final Location l = new Location(stoner.getLocation());
			TaskQueue.queue(new ForceMovementTask(stoner, new Location(l.getX() - 2, l.getY()), ControllerManager.DEFAULT_CONTROLLER) {
				@Override
				public void onDestination() {
				if (stoner.getBox().hasItemId(6)) {
					stoner.getBox().remove(6);
					stoner.getAttributes().set("dwarfmulticannon", new DwarfCannon(stoner, l.getX(), l.getY(), l.getZ()));
				}
				}
			});
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot setup your cannon here."));
		}

		return true;
	}

	return false;
	}
}
