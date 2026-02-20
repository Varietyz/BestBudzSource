package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave18 extends WaveDefinition {
  public Wave18() {
    super(17);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.KREE_ARRA);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.KREE_ARRA) {
			splitNpc(player, mob, BloodTrialNPCs.KRIL_TSUTSAROTH, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.KRIL_TSUTSAROTH) {
			splitNpc(player, mob, BloodTrialNPCs.ZILYANA, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.ZILYANA) {
			splitNpc(player, mob, BloodTrialNPCs.GENERAL_GRAARDOR, 1);
			return true;
		}
		return false;
	}
}
