package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave16 extends WaveDefinition {
  public Wave16() {
    super(15);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.DAGANNOTH_PRIME);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.DAGANNOTH_PRIME) {
			splitNpc(player, mob, BloodTrialNPCs.DAGANNOTH_REX, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.DAGANNOTH_REX) {
			splitNpc(player, mob, BloodTrialNPCs.DAGANNOTH_SUPREME, 1);
			return true;
		}
		return false;
	}
}
