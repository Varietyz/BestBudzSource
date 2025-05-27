package com.bestbudz.rs2.content.profession.summoning.impl;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.content.profession.summoning.FamiliarSpecial;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Minotaur implements FamiliarSpecial {
  @Override
  public boolean execute(Stoner stoner, FamiliarMob mob) {
    int max = 13;

    switch (mob.getData().ordinal()) {
      case 63:
        max = 19;
        break;
      case 52:
        max = 16;
        break;
    }

    mob.getCombat().setCombatType(CombatTypes.MAGE);
    mob.getAttributes().set("summonfammax", Integer.valueOf(max));

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
