package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DragonBattleaxeSpecialAssault implements Special {
  @Override
  public boolean checkRequirements(Stoner stoner) {
    return false;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 0;
  }

  @Override
  public void handleAssault(Stoner stoner) {}
}
