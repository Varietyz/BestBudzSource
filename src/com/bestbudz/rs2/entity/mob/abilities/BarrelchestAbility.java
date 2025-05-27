package com.bestbudz.rs2.entity.mob.abilities;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.CombatEffect;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class BarrelchestAbility implements CombatEffect {

  @Override
  public void execute(Entity e1, Entity e2) {
    if (e1.getLastDamageDealt() <= 0) {
      return;
    }

    if ((e2.getGrades()[5] > 0) && (!e2.isNpc())) {
      Stoner p = World.getStoners()[e2.getIndex()];

      if (p != null) {
        p.getNecromance().drain(10 + Utility.randomNumber(10));
        if (p.getNecromance().active(Necromance.PROTECT_FROM_MELEE)) {
          p.getNecromance().disable(Necromance.PROTECT_FROM_MELEE);
        }
      }
    }
  }
}
