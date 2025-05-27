package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ToxicBlowpipeEffect implements CombatEffect {

	@Override
	public void execute(Stoner stoner, Entity entity) {
	int damage = stoner.getLastDamageDealt();

	if (damage == 0) {
		return;
	}

	int hp = (int) (damage * 0.5D);

	if ((hp > 9) && (stoner.getGrades()[3] < stoner.getMaxGrades()[3])) {
		short[] tempHp = stoner.getGrades();
		tempHp[Professions.LIFE] = ((short) (tempHp[Professions.LIFE] + hp));
		if (stoner.getGrades()[3] > stoner.getMaxGrades()[3]) {
			hp = stoner.getMaxGrades()[3] - stoner.getGrades()[3];
			stoner.getGrades()[3] = stoner.getMaxGrades()[3];
		}
		stoner.getProfession().update(3);
	} else {
		hp = 0;
	}

	String message = "";

	if (hp > 0)
		message = "You regenerate " + hp + " Life.";
	else {
		return;
	}

	stoner.getClient().queueOutgoingPacket(new SendMessage(message));
	}
}
