package com.mayhem.rs2.content;

import java.util.HashMap;

import com.mayhem.core.cache.map.RSObject;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public enum Pickables {
	
	//Plant_Name, ObjectId, item array in new Item(1, 1); 
	
	//TODO Add more plants (thats on you travis <3)
	
	FLAX(14896, 500, new Item(1779, 1));
	
	private final int objectId;
	private final int respawnTime;
	private final Item harvest[];
	
	private Pickables(int objectId, int respawnTime, Item...harvest) {
		this.objectId = objectId;
		this.respawnTime = respawnTime;
		this.harvest = harvest;
	}
	
	public int getRespawnTime() {
		return respawnTime;
	}
	
	public int getObjectId() {
		return objectId;
	}
	
	private static final HashMap<Integer, Pickables> HARVEST = new HashMap<Integer, Pickables>();
	
	static {
		for (final Pickables harvest : Pickables.values()) {
			Pickables.HARVEST.put(harvest.objectId, harvest);
		}
	}
	
	public static boolean pickPlant(Player player, RSObject object) {
	
		Pickables data = Pickables.HARVEST.get(object.getId());
		
		// If object is null, return;
		if (data == null) {
			return false;
		}
	
		//Checks for free inventory spaces.
		if (player.getInventory().getFreeSlots() < data.harvest.length) {
			player.send(new SendMessage("You need at least " + data.harvest.length + " available inventory spaces to do this!"));
			return false;
		}
	
		//Add all the item pieces to inventory
		player.getInventory().addItems(data.harvest);	
		
		//Sends complete message
		player.send(new SendMessage("You successfully pick the plant."));
		
		//Sends picking animation
		player.getUpdateFlags().sendAnimation(new Animation(827));
		
		//TODO removing of object from game world and respawn task.
		
		return true;
	}
	
}
