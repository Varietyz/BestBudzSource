package com.bestbudz.rs2.entity.item.impl;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.WalkToTask;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.pathfinding.StraightPathFinder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendGroundItem;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveGroundItem;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class LocalGroundItems {
	private final Stoner stoner;
	private final Deque<GroundItem> loaded = new ArrayDeque<GroundItem>();
	private final Deque<GroundItem> adding = new ArrayDeque<GroundItem>();
	private final Deque<GroundItem> removing = new ArrayDeque<GroundItem>();
	private boolean hasLoaded = true;

	public LocalGroundItems(Stoner stoner) {
	this.stoner = stoner;
	}

	public void add(GroundItem groundItem) {
	synchronized (adding) {
		adding.add(groundItem);
	}
	}

	public void dropFull(int id, int slot) {
	Item drop = stoner.getBox().get(slot);
	GroundItemHandler.add(drop, new Location(stoner.getLocation()), stoner);
	stoner.getBox().clear(slot);
	}

	public void drop(int id, int slot) {
	if ((stoner.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)) && (stoner.getCombat().inCombat())) {
		if (GameDefinitionLoader.getHighAlchemyValue(id) > 1000) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot drop this item while in combat here."));
		}
	} else if ((stoner.getController().equals(ControllerManager.DUELING_CONTROLLER))) {
		return;
	}

	Item drop = stoner.getBox().get(slot);

	GroundItemHandler.add(drop, new Location(stoner.getLocation()), stoner);

	stoner.getBox().clear(slot);
	stoner.getCombat().reset();
	if (stoner.getInterfaceManager().getMain() != -1) {
		stoner.send(new SendRemoveInterfaces());
	}
	}

	public boolean drop(Item item, Location location) {
	return GroundItemHandler.add(item, location, stoner);
	}

	private void load() {
	synchronized (GroundItemHandler.getActive()) {
		for (Iterator<?> g = GroundItemHandler.getActive().iterator(); g.hasNext();) {
			GroundItem i = (GroundItem) g.next();

			if (GroundItemHandler.visible(stoner, i)) {
				synchronized (adding) {
					adding.add(i);
				}
			}
		}
	}

	hasLoaded = true;
	}

	public void onRegionChange() {
	hasLoaded = false;

	synchronized (adding) {
		adding.clear();
	}

	synchronized (loaded) {
		GroundItem g;
		while ((g = loaded.poll()) != null) {
			stoner.getClient().queueOutgoingPacket(new SendRemoveGroundItem(stoner, g));
		}
	}
	}

	public void pickup(final int x, final int y, final int id) {
	GroundItem g = GroundItemHandler.getGroundItem(id, x, y, stoner.getLocation().getZ(), stoner.getUsername(), false);
	if ((g == null) || (!GroundItemHandler.exists(g))) {
		stoner.getMovementHandler().reset();
		return;
	}
	TaskQueue.queue(new WalkToTask(stoner, g) {
		@Override
		public void onDestination() {
		GroundItem g = GroundItemHandler.getGroundItem(id, x, y, stoner.getLocation().getZ(), stoner.getUsername(), false);

		if ((g == null) || (!g.exists())) {
			stoner.getMovementHandler().reset();
			stop();
			return;
		}

		if (!StraightPathFinder.isInteractionPathClear(stoner.getLocation(), g.getLocation())) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("I can't reach that!"));
			stop();
			return;
		}

		if ((stoner.getBox().hasSpaceFor(g.getItem())) && (GroundItemHandler.remove(g))) {
			stoner.getBox().add(g.getItem());
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough box space to pick that up."));
		}
		}
	});
	}

	public void process() {
	if (!hasLoaded) {
		load();
	}

	GroundItem g = null;

	synchronized (adding) {
		while ((g = adding.poll()) != null) {
			stoner.getClient().queueOutgoingPacket(new SendGroundItem(stoner, g));
			loaded.add(g);
		}
	}

	synchronized (removing) {
		while ((g = removing.poll()) != null)
			stoner.getClient().queueOutgoingPacket(new SendRemoveGroundItem(stoner, g));
	}
	}

	public void remove(GroundItem groundItem) {
	synchronized (removing) {
		removing.add(groundItem);
	}
	}

	public boolean stack(GroundItem g) {
	if (!g.getItem().getDefinition().isStackable()) {
		return false;
	}

	GroundItem onGround = GroundItemHandler.getNonGlobalGroundItem(g.getItem().getId(), g.getLocation().getX(), g.getLocation().getY(), g.getLocation().getZ(), g.getLongOwnerName());

	if (onGround == null) {
		return false;
	}

	if (onGround.isGlobal()) {
		return false;
	}
	stoner.getClient().queueOutgoingPacket(new SendRemoveGroundItem(stoner, onGround));
	onGround.getItem().add(g.getItem().getAmount());
	stoner.getClient().queueOutgoingPacket(new SendGroundItem(stoner, onGround));
	return true;
	}
}
