package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class SaradominStrikeEffect implements CombatEffect {
	@Override
	public void execute(Stoner p, Entity e) {
	if ((p.getLastDamageDealt() > 0) && (!e.isNpc())) {
		Stoner other = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];

		if (other != null) {
			int tmp32_31 = 5;
			short[] tmp32_28 = other.getGrades();
			tmp32_28[tmp32_31] = ((short) (tmp32_28[tmp32_31] - 1));

			if (other.getGrades()[5] < 0) {
				other.getGrades()[5] = 0;
			}

			other.getProfession().update(5);
		}
	}
	}
}
