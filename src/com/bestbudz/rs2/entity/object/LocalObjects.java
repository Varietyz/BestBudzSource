package com.bestbudz.rs2.entity.object;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendObject;
import java.util.Deque;
import java.util.LinkedList;

public class LocalObjects {

	private final Stoner stoner;
	private final Deque<GameObject> adding = new LinkedList<GameObject>();
	private boolean load = false;

	public LocalObjects(Stoner stoner) {
	this.stoner = stoner;
	}

	public void add(GameObject o) {
	synchronized (adding) {
		adding.add(o);
	}
	}

	private void load() {
	if (ObjectManager.getActive() == null) {
		return;
	}

	synchronized (ObjectManager.getActive()) {
		if (ObjectManager.getActive() == null) {
			return;
		}

		for (GameObject o : ObjectManager.getActive()) {
			if (stoner.withinRegion(o.getLocation()) && stoner.getLocation().getZ() % 4 == o.getLocation().getZ() % 4) {
				stoner.getClient().queueOutgoingPacket(new SendObject(stoner, o));
			}
		}
	}

	load = false;
	}

	public void onRegionChange() {
	synchronized (adding) {
		adding.clear();
	}

	load = true;
	}

	public void process() {
	if (load) {
		load();
	}

	synchronized (adding) {
		GameObject g = null;
		while ((g = adding.poll()) != null) {
			stoner.getClient().queueOutgoingPacket(new SendObject(stoner, g));
		}
	}
	}
}
