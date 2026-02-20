package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave12 extends WaveDefinition {
  public Wave12() {
    super(11);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.STEEL_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.STEEL_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.STEEL_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.STEEL_DRAGON);
  }
}
