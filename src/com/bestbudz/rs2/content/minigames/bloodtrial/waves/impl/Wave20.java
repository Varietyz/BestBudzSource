package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave20 extends WaveDefinition {
  public Wave20() {
    super(19);
  }

  @Override
  public void spawnNpcs(Stoner player) {
	  spawnNpc(player, BloodTrialNPCs.CORP);
  }
}
