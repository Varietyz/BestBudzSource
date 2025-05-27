package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ClawsOfGuthixEffect implements CombatEffect {
	@Override
	public void execute(Stoner p, Entity e) {
	if ((Utility.randomNumber(4) == 0) && (p.getLastDamageDealt() > 0) && (!e.isNpc())) {
		Stoner other = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];

		if (other != null) {
			int tmp39_38 = 1;
			short[] tmp39_35 = other.getGrades();
			tmp39_35[tmp39_38] = ((short) (int) (tmp39_35[tmp39_38] - other.getGrades()[1] * 0.05D));

			if (other.getGrades()[1] < 0) {
				other.getGrades()[1] = 0;
			}

			other.getProfession().update(1);
		}
	}
	}
}
