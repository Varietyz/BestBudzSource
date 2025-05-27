package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ShadowBlitzEffect implements CombatEffect {
	@Override
	public void execute(Stoner p, Entity e) {
	if (p.getLastDamageDealt() > -1) {
		int tmp13_12 = 0;
		short[] tmp13_9 = e.getGrades();
		tmp13_9[tmp13_12] = ((short) (int) (tmp13_9[tmp13_12] - e.getGrades()[0] * 0.5D));
		if (e.getGrades()[0] < 0) {
			e.getGrades()[0] = 0;
		}

		if (!e.isNpc()) {
			Stoner p2 = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];

			if (p2 == null) {
				return;
			}

			p2.getProfession().update(0);
		}
	}
	}
}
