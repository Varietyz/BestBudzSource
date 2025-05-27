package com.bestbudz.rs2.entity.mob.abilities;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class BorkAbility implements CombatEffect {
	@Override
	public void execute(Entity e1, Entity e2) {
	if (e1.isNpc()) {
		Mob m = com.bestbudz.rs2.entity.World.getNpcs()[e1.getIndex()];

		if ((m != null) && (m.getCombat().getCombatType() == CombatTypes.MAGE) && (m.getCombatants() != null) && (m.getCombatants().size() > 0))
			for (Stoner p : m.getCombatants())
				if (!p.equals(e1.getCombat().getAssaulting()))
					m.getCombat().getMage().finish(p);
	}
	}
}
