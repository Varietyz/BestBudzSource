package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave01 extends WaveDefinition {
	public Wave01() {
		super(0); // Zero-indexed to match existing stage system
	}

	@Override
	public void spawnNpcs(Stoner player) {
		spawnNpc(player, BloodTrialNPCs.TZ_KIH);
		spawnNpc(player, BloodTrialNPCs.CHICKEN);
		spawnNpc(player, BloodTrialNPCs.CRAWLING_HAND);
	}
}

