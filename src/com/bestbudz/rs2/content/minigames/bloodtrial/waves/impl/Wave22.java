package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave22 extends WaveDefinition {
  public Wave22() {
    super(21);
  }

  @Override
  public void spawnNpcs(Stoner player) {
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH_PRIME);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH_REX);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH_SUPREME);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH);
  }
}
