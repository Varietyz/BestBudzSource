package com.bestbudz.rs2.entity.mob;

import com.bestbudz.rs2.content.combat.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.abilities.BarrelchestAbility;
import com.bestbudz.rs2.entity.mob.abilities.BorkAbility;
import com.bestbudz.rs2.entity.mob.abilities.CorporealBeastAbility;
import com.bestbudz.rs2.entity.mob.abilities.JadAbility;
import java.util.HashMap;
import java.util.Map;

public class MobAbilities {

	private static final Map<Integer, CombatEffect> abilities = new HashMap<Integer, CombatEffect>();

	public static final void declare() {
	abilities.put(Integer.valueOf(6342), new BarrelchestAbility());
	abilities.put(Integer.valueOf(319), new CorporealBeastAbility());
	abilities.put(Integer.valueOf(2745), new JadAbility());
	abilities.put(Integer.valueOf(7133), new BorkAbility());
	}

	public static final void executeAbility(int id, Mob mob, Entity a) {
	CombatEffect e = abilities.get(Integer.valueOf(id));

	if (e != null)
		e.execute(mob, a);
	}
}
