package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave06 extends WaveDefinition {
  public Wave06() {
    super(5);
  }

  @Override
  public void spawnNpcs(Stoner player) {

	  spawnNpc(player, BloodTrialNPCs.TOK_XIL);
	  spawnNpc(player, BloodTrialNPCs.LESSER_DEMON);
	  spawnNpc(player, BloodTrialNPCs.BLACK_DEMON);
	  spawnNpc(player, BloodTrialNPCs.ABYSSAL_DEMON);
	  spawnNpc(player, BloodTrialNPCs.WEREWOLF);
	  spawnNpc(player, BloodTrialNPCs.VAMPIRE);
	  spawnNpc(player, BloodTrialNPCs.CHAOS_DRUID);
  }
}
