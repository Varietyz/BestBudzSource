package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave05 extends WaveDefinition {
  public Wave05() {
    super(4);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.HILL_GIANT);
    spawnNpc(player, BloodTrialNPCs.CYCLOPES);
	  spawnNpc(player, BloodTrialNPCs.FIRE_GIANT);
	  spawnNpc(player, BloodTrialNPCs.MOSS_GIANT);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.FIRE_GIANT) {
			splitNpc(player, mob, BloodTrialNPCs.PYREFIEND, 3);
			return true;
		}
		return false;
	}
}
