package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave14 extends WaveDefinition {
  public Wave14() {
    super(13);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.KBD);
	  spawnNpc(player, BloodTrialNPCs.BLACK_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.BLACK_DRAGON);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.KBD) {
			splitNpc(player, mob, BloodTrialNPCs.BLACK_DRAGON, 3);
			return true;
		}
		return false;
	}
}
