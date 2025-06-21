package com.bestbudz.rs2.entity.mob;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.Animation;

public class MobDefinitionProvider {
	private final Mob mob;

	public MobDefinitionProvider(Mob mob) {
		this.mob = mob;
	}

	public static NpcDefinition getDefinition(int id) {
		return GameDefinitionLoader.getNpcDefinition(id);
	}

	public static NpcCombatDefinition getCombatDefinition(int id) {
		return GameDefinitionLoader.getNpcCombatDefinition(id);
	}

	public NpcDefinition getDefinition() {
		return getDefinition(mob.getId());
	}

	public NpcCombatDefinition getCombatDefinition() {
		return getCombatDefinition(mob.getId());
	}

	public Animation getDeathAnimation() {
		NpcCombatDefinition combatDef = getCombatDefinition();
		return combatDef != null ? combatDef.getDeath() : new Animation(0, 0);
	}

	public int getRespawnTime() {
		NpcCombatDefinition combatDef = getCombatDefinition();
		return combatDef != null ? combatDef.getRespawnTime() : 50;
	}
}