package com.bestbudz.rs2.entity.mob.abilities;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class HobgoblinGeomancerAbility implements CombatEffect {
	@Override
	public void execute(Entity e1, Entity e2) {
	if (Utility.randomNumber(8) == 0) {
		Stoner p = null;

		if (!e2.isNpc()) {
			p = com.bestbudz.rs2.entity.World.getStoners()[e2.getIndex()];
			// if ((p != null) && (p.getNecromance().isProtectionActive())) {
			// p.getNecromance().disableProtection(27);
			// e2.getUpdateFlags().sendGraphic(new Graphic(2369, 0, 100));
			// int tmp74_73 = 3;
			// short[] tmp74_70 = e1.getGrades();
			// if ((tmp74_70[tmp74_73] = (short) (tmp74_70[tmp74_73] + 20)) > e1
			// .getMaxGrades()[3]) {
			// e1.getGrades()[3] = e1.getMaxGrades()[3];
			// }
			// }

		}

		for (int i = 0; i <= 6; i++)
			if (i != 3) {
				if (e2.getGrades()[i] > e2.getMaxGrades()[i] * 0.9D) {
					int tmp148_146 = i;
					short[] tmp148_143 = e2.getGrades();
					tmp148_143[tmp148_146] = ((short) (tmp148_143[tmp148_146] - 2));

					if (p != null)
						p.getProfession().update(i);
				}
			}
	}
	}
}
