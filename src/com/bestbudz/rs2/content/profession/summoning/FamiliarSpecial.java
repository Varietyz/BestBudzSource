package com.bestbudz.rs2.content.profession.summoning;

import com.bestbudz.rs2.entity.stoner.Stoner;

public interface FamiliarSpecial {
  boolean execute(Stoner paramStoner, FamiliarMob paramFamiliarMob);

  int getAmount();

  double getExperience();

  SpecialType getSpecialType();

  enum SpecialType {
    COMBAT,
    NONE
  }
}
