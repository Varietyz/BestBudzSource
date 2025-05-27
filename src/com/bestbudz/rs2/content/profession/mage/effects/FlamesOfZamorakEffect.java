package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class FlamesOfZamorakEffect implements CombatEffect {

	@Override
	public void execute(Stoner p, Entity e) {
	if ((Utility.randomNumber(4) == 0) && (p.getLastDamageDealt() > 0) && (!e.isNpc())) {
		Stoner other = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];

		if (other != null) {
			short[] tmp40_35 = other.getGrades();
			tmp40_35[6] = ((short) (int) (tmp40_35[6] - other.getGrades()[6] * 0.05D));

			if (other.getGrades()[6] < 0) {
				other.getGrades()[6] = 0;
			}

			other.getProfession().update(6);
		}
	}
	}
}
