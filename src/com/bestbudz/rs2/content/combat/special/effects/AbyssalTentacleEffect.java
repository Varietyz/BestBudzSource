package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class AbyssalTentacleEffect implements CombatEffect {

  @Override
  public void execute(Stoner stoner, Entity entity) {
    if (!entity.isNpc()) {
      Stoner p2 = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
      if (p2 == null) {
        return;
      }
      p2.freeze(10, 5);
      p2.getUpdateFlags().sendGraphic(new Graphic(181));
      if (Utility.random(100) < 50) {
        p2.poison(4);
      }
    }
  }
}
