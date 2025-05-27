package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BarrelchestAnchorEffect implements CombatEffect {
	private static final int[] EFFECTED_PROFESSIONS = { 0, 1, 4, 6 };

	@Override
	public void execute(Stoner p, Entity e) {
	int eff = (int) Math.ceil(p.getLastDamageDealt() * 0.01D);
	Stoner p2 = null;

	if (eff == 0) {
		return;
	}

	if (!e.isNpc()) {
		p2 = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];
	}

	for (int i = 0; i < EFFECTED_PROFESSIONS.length; i++) {
		int tmp55_54 = EFFECTED_PROFESSIONS[i];
		short[] tmp55_46 = e.getGrades();
		tmp55_46[tmp55_54] = ((short) (tmp55_46[tmp55_54] - eff));
		if (e.getGrades()[EFFECTED_PROFESSIONS[i]] < 0) {
			e.getGrades()[EFFECTED_PROFESSIONS[i]] = 0;
		}
		if (p2 != null) {
			p2.getProfession().update(EFFECTED_PROFESSIONS[i]);
		}
	}

	p.getClient().queueOutgoingPacket(new SendMessage("You drain some of your opponents combat professions."));
	}
}
