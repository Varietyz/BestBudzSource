package com.bestbudz.rs2.content.minigames.bloodtrial;

import com.bestbudz.rs2.content.minigames.bloodtrial.core.BloodTrialController;
import com.bestbudz.rs2.content.minigames.bloodtrial.core.BloodTrialSpawns;
import com.bestbudz.rs2.content.minigames.bloodtrial.finish.BloodTrialFinish;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.BloodTrialWaves;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveRegistry;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;

public final class BloodTrial
{
	public static final BloodTrialController CONTROLLER = new BloodTrialController();
	public static final Location LEAVE = new Location(3428, 2916, 0);
	public static final Location ENTRANCE = new Location(2036, 4529, 0);

	public static void checkForBloodTrial(Stoner p, Mob mob) {
		if (mob == null) {
			return;
		}

		// Check ALL Blood Trial players to see if this mob is in their list
		Stoner[] players = com.bestbudz.rs2.entity.World.getStoners();

		for (Stoner player : players) {
			if (player != null && player.getController().equals(CONTROLLER)) {
				if (player.getBloodTrialDetails().removeNpc(mob)) {
					// Mob was in this player's list and got removed
					System.out.println("DEBUG: Removed mob " + mob.getId() + " from " + player.getUsername());

					// Check if wave complete
					if (player.getBloodTrialDetails().getKillAmount() == 0) {
						handleWaveComplete(player);
					}
					return; // Found it, done
				}
			}
		}
	}

	private static void handleWaveComplete(Stoner p) {
		WaveDefinition currentWave = WaveRegistry.getWave(p.getBloodTrialDetails().getStage());
		currentWave.onWaveComplete(p);

		if (p.getBloodTrialDetails().getStage() == WaveRegistry.getMaxWaves() - 1) {
			BloodTrialFinish.finishedBloodTrial(p, true);
			return;
		}

		p.getBloodTrialDetails().increaseStage();
		BloodTrialWaves.startNextWave(p);
	}

	public static void init(Stoner p, boolean kiln) {
		if (p == null) {
			return;
		}

		p.send(new SendRemoveInterfaces());
		p.setController(CONTROLLER);

		// Initialize Z coordinate if needed
		if (p.getBloodTrialDetails().getZ() == 0) {
			p.getBloodTrialDetails().setZ(p);
		}

		// Teleport to entrance with player's Z coordinate
		Location entrance = new Location(ENTRANCE.getX(), ENTRANCE.getY(), p.getBloodTrialDetails().getZ());
		p.teleport(entrance);

		BloodTrialWaves.startNextWave(p);
	}

	public static void loadGame(Stoner stoner) {
		if (stoner == null) {
			return;
		}

		stoner.setController(CONTROLLER);

		if (stoner.getBloodTrialDetails().getStage() != 0) {
			BloodTrialWaves.startNextWave(stoner);
		}
	}

	public static void onLeaveGame(Stoner stoner) {
		if (stoner == null) {
			return;
		}

		// Clean up all NPCs
		stoner.getBloodTrialDetails().clearMobs();

		// Reset controller
		stoner.setController(ControllerManager.DEFAULT_CONTROLLER);

		// Reset spawn locations
		BloodTrialSpawns.resetUsedSpawns();
	}
}