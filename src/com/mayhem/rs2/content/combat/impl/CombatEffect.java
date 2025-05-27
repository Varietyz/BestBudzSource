package com.mayhem.rs2.content.combat.impl;

import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.player.Player;

public abstract interface CombatEffect {

	public abstract void execute(Player paramPlayer, Entity paramEntity);
}
