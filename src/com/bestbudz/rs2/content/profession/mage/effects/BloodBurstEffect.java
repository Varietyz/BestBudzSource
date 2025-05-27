package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class BloodBurstEffect implements CombatEffect {
  @Override
  public void execute(Stoner p, Entity e) {
    int dmg = p.getLastDamageDealt();
    if (dmg >= 4) {
      int heal = dmg / 4;
      int tmp20_19 = 3;
      long[] tmp20_16 = p.getGrades();
      tmp20_16[tmp20_19] = ((short) (tmp20_16[tmp20_19] + heal));
      if (p.getGrades()[3] > p.getMaxGrades()[3]) {
        p.getGrades()[3] = p.getMaxGrades()[3];
      }
      p.getProfession().update(3);
    }
  }
}
