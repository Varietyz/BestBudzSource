package com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl;

import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialNPCs;
import com.bestbudz.rs2.content.minigames.bloodtrial.waves.WaveDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Wave09 extends WaveDefinition {
  public Wave09() {
    super(8);
  }

  @Override
  public void spawnNpcs(Stoner player) {
    spawnNpc(player, BloodTrialNPCs.SARADOMIN_WIZARD);
    spawnNpc(player, BloodTrialNPCs.ZAMORAK_WIZARD);
	  spawnNpc(player, BloodTrialNPCs.BLACK_DRAGON);
	  spawnNpc(player, BloodTrialNPCs.BLUE_DRAGON);
  }

	@Override
	public boolean onNpcDeath(Stoner player, Mob mob) {
		if (mob.getId() == BloodTrialNPCs.ZAMORAK_WIZARD) {
			splitNpc(player, mob, BloodTrialNPCs.INFERNAL_MAGE, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.INFERNAL_MAGE) {
			splitNpc(player, mob, BloodTrialNPCs.CHAOS_DRUID, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.CHAOS_DRUID) {
			splitNpc(player, mob, BloodTrialNPCs.SKELETON, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.SKELETON) {
			splitNpc(player, mob, BloodTrialNPCs.GHOST, 1);
			return true;
		}
		if (mob.getId() == BloodTrialNPCs.GHOST) {
			splitNpc(player, mob, BloodTrialNPCs.LESSER_DEMON, 1);
			return true;
		}
		return false;
	}
}
