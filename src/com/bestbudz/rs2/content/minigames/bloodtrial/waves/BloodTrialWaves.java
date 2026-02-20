package com.bestbudz.rs2.content.minigames.bloodtrial.waves;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialConfig;
import com.bestbudz.rs2.content.minigames.bloodtrial.core.BloodTrialSpawns;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BloodTrialWaves
{
	private static final StringBuilder messageBuilder = new StringBuilder(32);

	public static void startNextWave(final Stoner p) {
		if (p == null || p.getBloodTrialDetails() == null) {
			return;
		}

		p.getClient().queueOutgoingPacket(new SendMessage("The next wave will start in a few seconds."));

		if (p.getBloodTrialDetails().getZ() == 0) {
			p.getBloodTrialDetails().setZ(p);
			p.changeZ(p.getBloodTrialDetails().getZ());
		}

		TaskQueue.queue(new Task(p, BloodTrialConfig.WAVE_START_DELAY, false,
			Task.StackType.NEVER_STACK, Task.BreakType.NEVER, TaskIdentifier.TZHAAR) {
			@Override
			public void execute() {
				if (p == null || p.getBloodTrialDetails() == null) {
					stop();
					return;
				}

				try {
					executeWave(p);
					sendWaveMessage(p);
				} catch (Exception e) {

					stop();
				} finally {
					stop();
				}
			}

			@Override
			public void onStop() {

				BloodTrialSpawns.resetUsedSpawns();
			}
		});
	}

	private static void executeWave(Stoner p) {
		int stage = p.getBloodTrialDetails().getStage();
		WaveDefinition wave = WaveRegistry.getWave(stage);

		wave.onWaveStart(p);

		wave.spawnNpcs(p);
	}

	private static void sendWaveMessage(Stoner p) {
		messageBuilder.setLength(0);
		p.getClient().queueOutgoingPacket(new SendMessage(messageBuilder.toString()));
	}
}
