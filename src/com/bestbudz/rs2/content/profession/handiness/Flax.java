package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles flax picking
 * 
 * @author Jaybane
 *
 */
public class Flax {

	/**
	 * Picks flax
	 * 
	 * @param stoner
	 * @param x
	 * @param y
	 */
	public static void pickFlax(final Stoner stoner, final int x, final int y) {
	// final WorldObject flax = new WorldObject(2646, new Position(x, y),
	// Direction.NORTH);
	// final WorldObject flaxPicked = new WorldObject(-1, new Position(x, y),
	// Direction.NORTH);
	if (stoner.getBox().getFreeSlots() < 1) {
		stoner.send(new SendMessage("Check yo box fool."));
		return;
	}
	stoner.getBox().addItems(new Item(1779, 3));
	stoner.getUpdateFlags().sendAnimation((new Animation(827)));
	stoner.send(new SendMessage("You grab a handful of flax."));
	/*
	 * if (Utility.random(3) == 1) { WorldObjectManager.register(flaxPicked);
	 * TaskManager.submit(new FlaxEvent(flax, flaxPicked, 15)); }
	 */
	}

}
