package com.bestbudz.rs2.content.minigames.bloodtrial.core;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialConfig;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveRegistry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Iterator;

public final class BloodTrialDetails
{

	private final Set<Mob> mobs = ConcurrentHashMap.newKeySet();
	private long waveStartTime = 0;
	private int stage = 0;
	private int z = 0;

	public void addNpc(Mob mob) {
		if (mob != null && mob.isActive() && !mob.isDead()) {
			mobs.add(mob);
			System.out.println("DEBUG: Added mob " + mob.getId() + " to wave " + (stage + 1) + ". Total mobs: " + mobs.size());
		}
	}

	public int getKillAmount() {
		cleanupDeadMobs();
		return mobs.size();
	}

	public int getKillAmountWithDebug() {
		cleanupDeadMobs();
		int aliveCount = 0;
		System.out.println("DEBUG: Checking mob status for wave " + (stage + 1) + ":");

		for (Mob mob : mobs) {
			if (mob != null && mob.isActive() && !mob.isDead()) {
				long currentHp = mob.getGrades()[0];
				aliveCount++;
				System.out.println("  - Mob " + mob.getId() + " is ALIVE (HP: " + currentHp + ")");
			} else {
				System.out.println("  - Mob " + (mob != null ? mob.getId() : "null") + " is DEAD/INACTIVE");
			}
		}

		System.out.println("DEBUG: Total alive mobs: " + aliveCount + ", Collection size: " + mobs.size());
		return aliveCount;
	}

	private void cleanupDeadMobs() {
		Iterator<Mob> iterator = mobs.iterator();
		while (iterator.hasNext()) {
			Mob mob = iterator.next();
			if (mob == null || !mob.isActive() || mob.isDead()) {
				iterator.remove();
			}
		}
	}

	public boolean isWaveComplete() {
		return getKillAmount() == 0;
	}

	public boolean forceCheckWaveComplete() {
		System.out.println("DEBUG: Force checking wave completion for wave " + (stage + 1));
		cleanupDeadMobs();

		int aliveCount = 0;
		for (Mob mob : mobs) {
			if (mob != null && mob.isActive() && !mob.isDead()) {
				long currentHp = mob.getGrades()[0];
				aliveCount++;
				System.out.println("DEBUG: Still alive - Mob " + mob.getId() + " at " + mob.getLocation() + " (HP: " + currentHp + ")");
			}
		}

		boolean complete = aliveCount == 0;
		System.out.println("DEBUG: Wave complete: " + complete + " (alive: " + aliveCount + ")");
		return complete;
	}

	public Set<Mob> getMobs() {
		cleanupDeadMobs();
		return mobs;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		if (stage >= 0 && stage < WaveRegistry.getMaxWaves()) {
			this.stage = stage;
		}
	}

	public int getZ() {
		return z;
	}

	public void setZ(Stoner p) {
		this.z = p.getIndex() * BloodTrialConfig.Z_MULTIPLIER;
	}

	public void increaseStage() {
		if (stage < WaveRegistry.getMaxWaves() - 1) {
			stage++;
		}
	}

	public boolean removeNpc(Mob mob) {
		boolean removed = mob != null && mobs.remove(mob);
		if (removed) {
			System.out.println("DEBUG: Removed mob " + mob.getId() + " from wave " + (stage + 1) + ". Remaining: " + mobs.size());
		}
		return removed;
	}

	public void forceRemoveNpc(Mob mob) {
		if (mob != null) {
			boolean wasInCollection = mobs.remove(mob);
			System.out.println("DEBUG: Force removed mob " + mob.getId() + " (was tracked: " + wasInCollection + "). Remaining: " + mobs.size());
		}
	}

	public void reset() {
		stage = 0;
		z = 0;
		clearMobs();
	}

	public void clearMobs() {
		System.out.println("DEBUG: Clearing " + mobs.size() + " mobs from tracking");

		for (Mob mob : mobs) {
			if (mob != null && mob.isActive()) {
				mob.remove();
			}
		}
		mobs.clear();
	}

	public void setWaveStartTime(long time) {
		this.waveStartTime = time;
	}

	public long getWaveStartTime() {
		return waveStartTime;
	}
}
