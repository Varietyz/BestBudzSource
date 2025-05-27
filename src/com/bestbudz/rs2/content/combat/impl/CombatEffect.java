package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public interface CombatEffect {

  void execute(Stoner paramStoner, Entity paramEntity);
}
