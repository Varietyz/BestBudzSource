package com.bestbudz.rs2.content.combat.special;

import com.bestbudz.rs2.entity.stoner.Stoner;

public interface Special {

  boolean checkRequirements(Stoner stoner);

  int getSpecialAmountRequired();

  void handleAssault(Stoner stoner);
}
