package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave21 extends WaveDefinition {
  public Wave21() {
    super(20);
  }

  @Override
  public void spawnNpcs(Stoner player) {
	  spawnNpc(player, BloodTrialNPCs.GENERAL_GRAARDOR);
	  spawnNpc(player, BloodTrialNPCs.ZILYANA);
	  spawnNpc(player, BloodTrialNPCs.KREE_ARRA);
	  spawnNpc(player, BloodTrialNPCs.KRIL_TSUTSAROTH);
  }
}
