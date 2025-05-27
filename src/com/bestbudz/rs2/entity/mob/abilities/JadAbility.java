package com.bestbudz.rs2.entity.mob.abilities;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;

public class JadAbility implements CombatEffect {
  @Override
  public void execute(Entity e1, Entity e2) {
    if (e1.getCombat().getCombatType() == CombatTypes.SAGITTARIUS) {
      e2.getUpdateFlags().sendGraphic(new Graphic(451, 0, 0));
    }
  }
}
