package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public final class SmokeBlitzEffect implements CombatEffect {
	@Override
	public void execute(Stoner p, Entity e) {
	if ((p.getLastDamageDealt() >= 0) && (Utility.randomNumber(2) == 0))
		e.poison(4);
	}
}
