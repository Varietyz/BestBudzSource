package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave02 extends WaveDefinition
{
	public Wave02() {
		super(1);
	}

	@Override
	public void spawnNpcs(Stoner player) {
		spawnNpc(player, BloodTrialNPCs.YAK);
		spawnNpc(player, BloodTrialNPCs.TZ_KEK);
	}

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.TZ_KEK) {
			splitNpc(player, mob, BloodTrialNPCs.TZ_KEK_SPAWN, 2);
			return true; // Special handling occurred
		}
		if (mob.getId() == BloodTrialNPCs.YAK) {
			splitNpc(player, mob, BloodTrialNPCs.COW, 1);
			return true; // Special handling occurred
		}
		return false;
	}
}
