package com.bestbudz.rs2.content.minigames.bloodtrial.waves;

import com.bestbudz.rs2.content.minigames.bloodtrial.core.BloodTrialSpawns;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;


public abstract class WaveDefinition {
	private final int waveNumber;

	protected WaveDefinition(int waveNumber) {
		this.waveNumber = waveNumber;
	}

	public int getWaveNumber() {
		return waveNumber;
	}

	/**
	 * Called when the wave starts - spawn all NPCs and setup wave mechanics
	 */
	public abstract void spawnNpcs(Stoner player);

	/**
	 * Called when an NPC dies - handle any special death mechanics
	 * @param player The player
	 * @param mob The mob that died
	 * @return true if special handling occurred, false for default behavior
	 */
	public boolean onNpcDeath(Stoner player, Mob mob) {
		return false; // Default: no special handling
	}

	/**
	 * Called when wave starts - setup any special mechanics
	 */
	public void onWaveStart(Stoner player) {
		// Record wave start time for completion tracking
		player.getBloodTrialDetails().setWaveStartTime(System.currentTimeMillis());

		// Send wave start message
		StringBuilder message = new StringBuilder(64);
		message.append("Wave ").append(waveNumber + 1).append(" begins!");
		player.send(new SendMessage(message.toString()));
	}

	/**
	 * Called when wave completes - cleanup and rewards
	 */
	public void onWaveComplete(Stoner player) {
		// Calculate completion time
		long startTime = player.getBloodTrialDetails().getWaveStartTime();
		long completionTime = System.currentTimeMillis() - startTime;
		String timeString = formatTime(completionTime);

		// Build completion message
		StringBuilder message = new StringBuilder(128);
		message.append("Wave ").append(waveNumber + 1).append(" completed in ").append(timeString);

		// Add next wave info (if not final wave)
		if (waveNumber < WaveRegistry.getMaxWaves() - 1) {
			message.append(" - Wave ").append(waveNumber + 2).append(" starting soon!");
		} else {
			message.append(" - Final wave completed!");
		}

		player.send(new SendMessage(message.toString()));
	}

	/**
	 * Formats milliseconds into a readable time string
	 */
	private String formatTime(long milliseconds) {
		if (milliseconds < 1000) {
			return "< 1 second";
		}

		long seconds = milliseconds / 1000;
		if (seconds < 60) {
			return seconds + " second" + (seconds == 1 ? "" : "s");
		}

		long minutes = seconds / 60;
		seconds = seconds % 60;

		StringBuilder time = new StringBuilder(16);
		time.append(minutes).append(" minute").append(minutes == 1 ? "" : "s");
		if (seconds > 0) {
			time.append(" and ").append(seconds).append(" second").append(seconds == 1 ? "" : "s");
		}

		return time.toString();
	}

	/**
	 * Utility method for spawning a single NPC at random location
	 */
	protected Mob spawnNpc(Stoner player, short npcId) {
		Location spawnLocation = BloodTrialSpawns.getRandomSpawnLocation(player.getBloodTrialDetails().getZ());
		Mob mob = new Mob(player, npcId, false, false, false, spawnLocation);
		mob.getFollowing().setIgnoreDistance(true);
		mob.getCombat().setAssault(player);
		player.getBloodTrialDetails().addNpc(mob);
		return mob;
	}

	/**
	 * Utility method for spawning NPC at specific location
	 */
	protected Mob spawnNpcAt(Stoner player, short npcId, Location location) {
		Mob mob = new Mob(player, npcId, false, false, false, location);
		mob.getFollowing().setIgnoreDistance(true);
		mob.getCombat().setAssault(player);
		player.getBloodTrialDetails().addNpc(mob);
		return mob;
	}

	/**
	 * Centralized split mechanic - spawns multiple NPCs at the death location
	 * @param player The player
	 * @param deadMob The mob that died
	 * @param spawnId The NPC ID to spawn
	 * @param count How many to spawn
	 */
	protected void splitNpc(Stoner player, Mob deadMob, short spawnId, int count) {
		Location deathLocation = deadMob.getLocation();

		for (int i = 0; i < count; i++) {
			Location spawnLocation = findNearbySpawnLocation(deathLocation, i);
			Mob spawn = new Mob(player, spawnId, false, false, false, spawnLocation);
			spawn.getFollowing().setIgnoreDistance(true);
			spawn.getCombat().setAssault(player);
			player.getBloodTrialDetails().addNpc(spawn);
		}
	}

	/**
	 * Finds a nearby location for split spawns to avoid stacking
	 * @param center The center location (where original mob died)
	 * @param index The spawn index (0, 1, 2, etc.)
	 * @return A location near the center but offset
	 */
	private Location findNearbySpawnLocation(Location center, int index) {
		// Define offset patterns around the death location
		int[][] offsets = {
			{0, 0},   // First spawn at exact location
			{1, 0},   // Second spawn 1 tile east
			{-1, 0},  // Third spawn 1 tile west
			{0, 1},   // Fourth spawn 1 tile north
			{0, -1},  // Fifth spawn 1 tile south
			{1, 1},   // Sixth spawn northeast
			{-1, -1}, // Seventh spawn southwest
			{1, -1},  // Eighth spawn southeast
			{-1, 1}   // Ninth spawn northwest
		};

		// Use modulo to cycle through patterns if more spawns than patterns
		int patternIndex = index % offsets.length;
		int[] offset = offsets[patternIndex];

		return new Location(
			center.getX() + offset[0],
			center.getY() + offset[1],
			center.getZ()
		);
	}
}