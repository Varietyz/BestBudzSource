package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave19 extends WaveDefinition {
  public Wave19() {
    super(18);
  }

  @Override
  public void spawnNpcs(Stoner player) {
	  spawnNpc(player, BloodTrialNPCs.TOK_XIL);
	  spawnNpc(player, BloodTrialNPCs.TZ_KEK);
	  spawnNpc(player, BloodTrialNPCs.YT_MEJKOT);
	  spawnNpc(player, BloodTrialNPCs.TZ_KIH);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.TZ_KEK) {
			splitNpc(player, mob, BloodTrialNPCs.TZ_KEK_SPAWN, 2);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.YT_MEJKOT) {
			splitNpc(player, mob, BloodTrialNPCs.TZTOK_JAD, 1);
			return true;
		}
		return false;
	}
}
