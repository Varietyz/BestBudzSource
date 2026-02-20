package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave13 extends WaveDefinition {
  public Wave13() {
    super(12);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.MITHRIL_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.SKELETAL_WYVERN);
  }
}
