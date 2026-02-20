package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave08 extends WaveDefinition {
  public Wave08() {
    super(7);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.BANSHEE);
    spawnNpc(player, BloodTrialNPCs.DARK_BEAST);
    spawnNpc(player, BloodTrialNPCs.HELLHOUND);
	  spawnNpc(player, BloodTrialNPCs.BABY_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.POISON_SCORPION);
	  spawnNpc(player, BloodTrialNPCs.SCORPION);
	  spawnNpc(player, BloodTrialNPCs.SCORPIA_OFFSPRING);
  }
}
