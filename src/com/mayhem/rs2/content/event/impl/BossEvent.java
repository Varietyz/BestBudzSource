package com.mayhem.rs2.content.event.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.event.Event;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.impl.EnragedCerberus;
import com.mayhem.rs2.entity.mob.impl.EnragedGeneralGraador;
import com.mayhem.rs2.entity.mob.impl.EnragedSkitzo;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * @author Andy || ReverendDread Mar 29, 2017
 */
public class BossEvent extends Event {
					
	/**
	 * Enum containing spawn locations for event bosses.
	 * @author Andy || ReverendDread Mar 29, 2017
	 */
	private enum SpawnLocations {
		
		FALADOR_FARMING_PLOT("Falador Farming plot.", new Location(3029, 3301)),
		CRAFTING_AREA("the Crafting area.", new Location(2749, 3418)),
		LUMBRIDGE_SWAMP_MINE("Lumbridge swamp mine.", new Location(3188, 3146)),
		;
		
		private String locationName;
		private Location spawnLocation;
		
		private SpawnLocations(String locationName, Location spawnLocation) {
			this.locationName = locationName;
			this.spawnLocation = spawnLocation;
		}

		/**
		 * Gets the locationName.
		 * @return the locationName
		 */
		public String getLocationName() {
			return locationName;
		}

		/**
		 * Gets the spawnLocation.
		 * @return the spawnLocation
		 */
		public Location getSpawnLocation() {
			return spawnLocation;
		}
		
		private static final List<SpawnLocations> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		/**
		 * Gets a random location.
		 * @return the location data.
		 */
		public static SpawnLocations getRandomLocation()  {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
		
	}
	
	//Fields.
	private Mob boss;
	SpawnLocations location = SpawnLocations.getRandomLocation();
	
	@Override
	public boolean start() {	
		int random = Utility.random(3);	
		switch (random) {
			//Cerberus
			case 1:
				boss = new EnragedCerberus(location.getSpawnLocation());
				break;
			//Skitzo	
			case 2:
				boss = new EnragedSkitzo(location.getSpawnLocation());
				break;
			//General Graador
			case 3:
				boss = new EnragedGeneralGraador(location.getSpawnLocation());
				break;
				
			default:
				boss = new EnragedGeneralGraador(location.getSpawnLocation());
		}
		sendMessage("an " + boss.getDefinition().getName() + " has spawned near " + location.getLocationName() + "!");
		return true;
	}

	@Override
	public boolean preStartupCheck() {
		if (boss != null)
			return true;
		System.err.println("---[EVENT]--- Boss has spawned null.");
		return false;
	}

	private int duration;
	
	@Override
	public int process() {
		duration++;
		if (boss.isDead()) {
			sendMessage("The event is now over, the boss has been killed.");
			//ObjectManager.spawnWithObject(new BossChest(boss.getX(), boss.getY(), boss.getZ()));
			for (Player player : World.getPlayers()) {
				if (player == null)
					continue;
				if (player.getLocation().inBossEvent()) {
					GroundItemHandler.add(new Item(20526, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
					player.send(new SendMessage("Use the Key on the Event Chest at home to receive your rewards!"));
				}
			}
			return -1; //Ends event.
		}
		if (duration >= 3000) {
			sendMessage("The event is now over, the boss has disappeared.");
			stop();
			return -1;
		}
		return 1;
	}

	@Override
	public void stop() {
		if (!boss.isDead()) {
			boss.remove();
		}	
	}

}
