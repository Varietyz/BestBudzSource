package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class TeleBlockEffect implements CombatEffect {

	@Override
	public void execute(Stoner p, Entity e) {
	if (!e.isNpc()) {
		Stoner p2 = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];
		if (p2 == null) {
			return;
		}
		if (p2.getNecromance().active(Necromance.PROTECT_FROM_MAGE)) {
			p2.getClient().queueOutgoingPacket(new SendMessage("@dre@You have been half teleblocked."));
			e.teleblock(150);
			return;
		} else {
			p2.getClient().queueOutgoingPacket(new SendMessage("@dre@You have been full teleblocked."));
			e.teleblock(300);
			return;
		}
	} else {
		e.teleblock(300);
		return;
	}
	}
}
