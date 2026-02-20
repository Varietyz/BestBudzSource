package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave03 extends WaveDefinition {
  public Wave03() {
    super(2);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.GOBLIN);
    spawnNpc(player, BloodTrialNPCs.ROCK_CRAB);
	  spawnNpc(player, BloodTrialNPCs.BLOODVELD);
	  spawnNpc(player, BloodTrialNPCs.BLACK_KNIGHT);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.BLACK_KNIGHT) {
			splitNpc(player, mob, BloodTrialNPCs.SKELETON, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.SKELETON) {
			splitNpc(player, mob, BloodTrialNPCs.GHOST, 1);
			return true;
		}
		return false;
	}
}
