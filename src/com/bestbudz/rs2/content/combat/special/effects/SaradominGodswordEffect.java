package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SaradominGodswordEffect implements CombatEffect {

	@Override
	public void execute(Stoner p, Entity e) {
	int dmg = p.getLastDamageDealt();
	if (dmg == 0) {
		return;
	}

	int hp = (int) (dmg * 0.5D);
	int pray = (int) (dmg * 0.25D);

	if ((hp > 9) && (p.getGrades()[3] < p.getMaxGrades()[3])) {
		int tmp55_54 = 3;
		short[] tmp55_51 = p.getGrades();
		tmp55_51[tmp55_54] = ((short) (tmp55_51[tmp55_54] + hp));
		if (p.getGrades()[3] > p.getMaxGrades()[3]) {
			hp = p.getMaxGrades()[3] - p.getGrades()[3];
			p.getGrades()[3] = p.getMaxGrades()[3];
		}
		p.getProfession().update(3);
	} else {
		hp = 0;
	}

	if ((pray > 4) && (p.getGrades()[5] < p.getMaxGrades()[5])) {
		int tmp144_143 = 5;
		short[] tmp144_140 = p.getGrades();
		tmp144_140[tmp144_143] = ((short) (tmp144_140[tmp144_143] + pray));
		if (p.getGrades()[5] > p.getMaxGrades()[5]) {
			pray = p.getMaxGrades()[5] - p.getGrades()[5];
			p.getGrades()[5] = p.getMaxGrades()[5];
		}
		p.getProfession().update(5);
	} else {
		pray = 0;
	}

	String message = "";

	if ((pray > 0) && (hp > 0))
		message = "You regenerate " + pray + " Necromance and " + hp + " Life.";
	else if ((pray == 0) && (hp > 0))
		message = "You regenerate " + hp + " Life.";
	else if ((hp == 0) && (pray > 0))
		message = "You regenerate " + pray + " Necromance.";
	else {
		return;
	}

	p.getClient().queueOutgoingPacket(new SendMessage(message));
	}

}
