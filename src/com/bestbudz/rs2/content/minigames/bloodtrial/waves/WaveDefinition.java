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

	public abstract void spawnNpcs(Stoner player);

	public boolean onNpcDeath(Stoner player, Mob mob) {
		return false;
	}

	public void onWaveStart(Stoner player) {

		player.getBloodTrialDetails().setWaveStartTime(System.currentTimeMillis());

		StringBuilder message = new StringBuilder(64);
		message.append("Wave ").append(waveNumber + 1).append(" begins!");
		player.send(new SendMessage(message.toString()));
	}

	public void onWaveComplete(Stoner player) {

		long startTime = player.getBloodTrialDetails().getWaveStartTime();
		long completionTime = System.currentTimeMillis() - startTime;
		String timeString = formatTime(completionTime);

		StringBuilder message = new StringBuilder(128);
		message.append("Wave ").append(waveNumber + 1).append(" completed in ").append(timeString);

		if (waveNumber < WaveRegistry.getMaxWaves() - 1) {
			message.append(" - Wave ").append(waveNumber + 2).append(" starting soon!");
		} else {
			message.append(" - Final wave completed!");
		}

		player.send(new SendMessage(message.toString()));
	}

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

	protected Mob spawnNpc(Stoner player, short npcId) {
		Location spawnLocation = BloodTrialSpawns.getRandomSpawnLocation(player.getBloodTrialDetails().getZ());
		Mob mob = new Mob(player, npcId, false, false, false, spawnLocation);
		mob.getFollowing().setIgnoreDistance(true);
		mob.getCombat().setAssault(player);
		player.getBloodTrialDetails().addNpc(mob);
		return mob;
	}

	protected Mob spawnNpcAt(Stoner player, short npcId, Location location) {
		Mob mob = new Mob(player, npcId, false, false, false, location);
		mob.getFollowing().setIgnoreDistance(true);
		mob.getCombat().setAssault(player);
		player.getBloodTrialDetails().addNpc(mob);
		return mob;
	}

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

	private Location findNearbySpawnLocation(Location center, int index) {

		int[][] offsets = {
			{0, 0},
			{1, 0},
			{-1, 0},
			{0, 1},
			{0, -1},
			{1, 1},
			{-1, -1},
			{1, -1},
			{-1, 1}
		};

		int patternIndex = index % offsets.length;
		int[] offset = offsets[patternIndex];

		return new Location(
			center.getX() + offset[0],
			center.getY() + offset[1],
			center.getZ()
		);
	}
}
