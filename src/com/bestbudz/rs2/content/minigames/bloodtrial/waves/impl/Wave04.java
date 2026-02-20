package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave04 extends WaveDefinition {
  public Wave04() {
    super(3);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.JOGRE);
    spawnNpc(player, BloodTrialNPCs.OGRE);
    spawnNpc(player, BloodTrialNPCs.ORK);
	  spawnNpc(player, BloodTrialNPCs.GOBLIN);
	  spawnNpc(player, BloodTrialNPCs.SERGEANT_STEELWILL);
  }
}
