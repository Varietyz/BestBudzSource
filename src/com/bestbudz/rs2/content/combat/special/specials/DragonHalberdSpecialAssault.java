package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DragonHalberdSpecialAssault implements Special {

  @Override
  public boolean checkRequirements(Stoner stoner) {
    return false;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 55;
  }

  @Override
  public void handleAssault(Stoner stoner) {
    stoner.getCombat().getMelee().setAnimation(new Animation(1203, 0));
    stoner.getUpdateFlags().sendGraphic(Graphic.lowGraphic(282, 0));
    stoner.getCombat().getAssaulting().getUpdateFlags().sendGraphic(Graphic.lowGraphic(282, 0));
  }
}
