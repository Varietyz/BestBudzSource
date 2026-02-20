package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave25 extends WaveDefinition {
  public Wave25() {
    super(24);
  }

  @Override
  public void spawnNpcs(Stoner player) {
	  spawnNpc(player, BloodTrialNPCs.TZTOK_JAD);
	  spawnNpc(player, BloodTrialNPCs.CORP);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH_PRIME);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH_REX);
	  spawnNpc(player, BloodTrialNPCs.DAGANNOTH_SUPREME);
	  spawnNpc(player, BloodTrialNPCs.GENERAL_GRAARDOR);
	  spawnNpc(player, BloodTrialNPCs.ZILYANA);
	  spawnNpc(player, BloodTrialNPCs.KREE_ARRA);
	  spawnNpc(player, BloodTrialNPCs.KRIL_TSUTSAROTH);
	  spawnNpc(player, BloodTrialNPCs.KBD);
  }
}
