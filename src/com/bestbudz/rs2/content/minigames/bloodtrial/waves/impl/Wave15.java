package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave15 extends WaveDefinition {
  public Wave15() {
    super(14);
  }

  @Override
  public void spawnNpcs(Stoner player) {
	  spawnNpc(player, BloodTrialNPCs.GUTHAN);
	  spawnNpc(player, BloodTrialNPCs.AHRIM);
	  spawnNpc(player, BloodTrialNPCs.DHAROK);
	  spawnNpc(player, BloodTrialNPCs.KARIL);
	  spawnNpc(player, BloodTrialNPCs.TORAG);
	  spawnNpc(player, BloodTrialNPCs.VERAC);
  }
}
