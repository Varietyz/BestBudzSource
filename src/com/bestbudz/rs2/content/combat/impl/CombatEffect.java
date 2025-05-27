package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract interface CombatEffect {

	public abstract void execute(Stoner paramStoner, Entity paramEntity);
}
