package com.bestbudz.rs2.content.profession.summoning.impl;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.content.profession.summoning.FamiliarSpecial;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class SpiritJelly implements FamiliarSpecial {
  @Override
  public boolean execute(Stoner stoner, FamiliarMob mob) {
    Entity a = mob.getOwner().getCombat().getAssaulting();

    if (a != null) {
      mob.getAttributes().set("summonfammax", Integer.valueOf(13));
      mob.getCombat().setCombatType(CombatTypes.MAGE);

      if (a.getGrades()[0] > a.getMaxGrades()[0] - 5) {
        int tmp61_60 = 0;
        long[] tmp61_57 = a.getGrades();
        tmp61_57[tmp61_60] = ((short) (tmp61_57[tmp61_60] - 5));

        if (a.getGrades()[0] < a.getMaxGrades()[0] - 5) {
          a.getGrades()[0] = ((short) (a.getMaxGrades()[0] - 5));
        }

        if (!a.isNpc()) {
          Stoner p = com.bestbudz.rs2.entity.World.getStoners()[a.getIndex()];

          if (p != null) {
            p.getProfession().update(0);
          }
        }
      }
    }

    return true;
  }

  @Override
  public int getAmount() {
    return 6;
  }

  @Override
  public double getExperience() {
    return 1.0D;
  }

  @Override
  public FamiliarSpecial.SpecialType getSpecialType() {
    return FamiliarSpecial.SpecialType.COMBAT;
  }
}
