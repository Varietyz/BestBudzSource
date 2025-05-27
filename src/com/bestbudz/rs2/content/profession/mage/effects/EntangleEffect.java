package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class EntangleEffect implements CombatEffect {
  @Override
  public void execute(Stoner p, Entity e) {
    e.freeze(15, 5);
  }
}
