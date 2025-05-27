package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class StaffOfDeadSpecialAssault implements Special {
  @Override
  public boolean checkRequirements(Stoner stoner) {
    return true;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 100;
  }

  @Override
  public void handleAssault(Stoner stoner) {
    stoner.getCombat().getAssaulting().getUpdateFlags().sendGraphic(Graphic.highGraphic(1958, 0));
    stoner.getCombat().getAssaulting().getUpdateFlags().sendAnimation(new Animation(10516, 0));
  }
}
