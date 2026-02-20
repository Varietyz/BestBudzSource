package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave10 extends WaveDefinition {
  public Wave10() {
    super(9);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.GREEN_DRAGON);
    spawnNpc(player, BloodTrialNPCs.RED_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.LAVA_DRAGON);
  }
}
