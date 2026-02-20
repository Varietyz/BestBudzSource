package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave07 extends WaveDefinition {
  public Wave07() {
    super(6);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.HOBGOBLIN);
    spawnNpc(player, BloodTrialNPCs.GOBLIN);
	  spawnNpc(player, BloodTrialNPCs.HOBGOBLIN);
	  spawnNpc(player, BloodTrialNPCs.GOBLIN);
	  spawnNpc(player, BloodTrialNPCs.HOBGOBLIN);
	  spawnNpc(player, BloodTrialNPCs.GOBLIN);
	  spawnNpc(player, BloodTrialNPCs.HOBGOBLIN);
	  spawnNpc(player, BloodTrialNPCs.GOBLIN);
	  spawnNpc(player, BloodTrialNPCs.HOBGOBLIN);
	  spawnNpc(player, BloodTrialNPCs.GOBLIN);
  }
}
