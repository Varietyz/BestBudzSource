package com.bestbudz.rs2.entity.mob.abilities;

import com.bestbudz.rs2.content.combat.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import java.util.HashMap;
import java.util.Map;

public class MobAbilities {

  private static final Map<Integer, CombatEffect> abilities = new HashMap<Integer, CombatEffect>();

  public static final void declare() {
    abilities.put(Integer.valueOf(6342), new BarrelchestAbility());
    abilities.put(Integer.valueOf(319), new CorporealBeastAbility());
    abilities.put(Integer.valueOf(3127), new JadAbility());
	  abilities.put(Integer.valueOf(4010), new JadAbility());
    abilities.put(Integer.valueOf(7133), new BorkAbility());
  }

	public static final void executeAbility(int id, Entity attacker, Entity target) {
		CombatEffect e = abilities.get(Integer.valueOf(id));
		if (e != null) e.execute(attacker, target);
	}
}
