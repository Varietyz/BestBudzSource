package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave11 extends WaveDefinition {
  public Wave11() {
    super(10);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.YT_MEJKOT);
    spawnNpc(player, BloodTrialNPCs.BRONZE_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.IRON_DRAGON);
  }
}
