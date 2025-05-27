package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class AbyssalTentacleSpecialAssault implements Special {

  @Override
  public boolean checkRequirements(Stoner stoner) {
    return true;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 50;
  }

  @Override
  public void handleAssault(Stoner stoner) {
    stoner.getCombat().getAssaulting().getUpdateFlags().sendGraphic(Graphic.highGraphic(341, 0));
    stoner.getCombat().getMelee().setAnimation(new Animation(1658, 0));
  }
}
