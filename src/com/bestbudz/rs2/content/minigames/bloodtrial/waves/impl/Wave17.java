package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave17 extends WaveDefinition {
  public Wave17() {
    super(16);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.GENERAL_GRAARDOR);
	  spawnNpc(player, BloodTrialNPCs.SERGEANT_GRIMSPIKE);
	  spawnNpc(player, BloodTrialNPCs.SERGEANT_STEELWILL);
	  spawnNpc(player, BloodTrialNPCs.SERGEANT_STRONGSTACK);
	  spawnNpc(player, BloodTrialNPCs.GOBLIN);
	  spawnNpc(player, BloodTrialNPCs.HOBGOBLIN);
	  spawnNpc(player, BloodTrialNPCs.ORK);
	  spawnNpc(player, BloodTrialNPCs.OGRE);
	  spawnNpc(player, BloodTrialNPCs.JOGRE);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.GENERAL_GRAARDOR) {
			splitNpc(player, mob, BloodTrialNPCs.GOBLIN, 5);
			return true;
		}
		return false;
	}
}
