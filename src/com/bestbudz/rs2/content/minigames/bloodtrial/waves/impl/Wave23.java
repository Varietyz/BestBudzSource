package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave23 extends WaveDefinition {
  public Wave23() {
    super(22);
  }

  @Override
  public void spawnNpcs(Stoner player) {
	  spawnNpc(player, BloodTrialNPCs.TZTOK_JAD);
	  spawnNpc(player, BloodTrialNPCs.TZTOK_JAD);
	  spawnNpc(player, BloodTrialNPCs.TZTOK_JAD);
  }
}
