package com.bestbudz.rs2.content.combat;

import com.bestbudz.rs2.entity.Entity;

public abstract interface CombatEffect {
	
	public abstract void execute(Entity paramEntity1, Entity paramEntity2);
}
