package com.bestbudz.rs2.entity.mob.impl;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.profession.mage.MageConstants;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;

public class GelatinnothMother extends Mob {
	public static final int[] STAGES = { 3497, 3498, 3499, 3500, 3501, 3502 };

	private byte stage = 0;

	public GelatinnothMother(Location location, Stoner owner) {
	super(3497, false, location, owner, false, false, null);

	TaskQueue.queue(new Task(this, 20) {
		@Override
		public void execute() {
		GelatinnothMother tmp4_1 = GelatinnothMother.this;
		tmp4_1.stage = ((byte) (tmp4_1.stage + 1));

		if (stage == GelatinnothMother.STAGES.length) {
			stage = 0;
		}

		transform(GelatinnothMother.STAGES[stage]);
		}

		@Override
		public void onStop() {
		}
	});
	}

	@Override
	public int getAffectedDamage(Hit hit) {
	if (hit.getAssaulter() != null && !hit.getAssaulter().isNpc()) {
		Stoner p = World.getStoners()[hit.getAssaulter().getIndex()];

		if (p != null && StonerConstants.isOwner(p)) {
			return hit.getDamage();
		}
	}

	if ((hit.getAssaulter() != null) && (!hit.getAssaulter().isNpc())) {
		Stoner p = com.bestbudz.rs2.entity.World.getStoners()[hit.getAssaulter().getIndex()];
		if (p != null) {
			if (getId() == STAGES[0]) {
				if ((p.getCombat().getCombatType() == CombatTypes.MAGE) && (MageConstants.getSpellTypeForId(p.getMage().getSpellCasting().getCurrentSpellId()) == MageConstants.SpellType.WIND)) {
					return hit.getDamage();
				}

				return 0;
			}
			if (getId() == STAGES[1]) {
				if (p.getCombat().getCombatType() == CombatTypes.MELEE) {
					return hit.getDamage();
				}

				return 0;
			}
			if (getId() == STAGES[2]) {
				if ((p.getCombat().getCombatType() == CombatTypes.MAGE) && (MageConstants.getSpellTypeForId(p.getMage().getSpellCasting().getCurrentSpellId()) == MageConstants.SpellType.WATER)) {
					return hit.getDamage();
				}

				return 0;
			}
			if (getId() == STAGES[3]) {
				if ((p.getCombat().getCombatType() == CombatTypes.MAGE) && (MageConstants.getSpellTypeForId(p.getMage().getSpellCasting().getCurrentSpellId()) == MageConstants.SpellType.FIRE)) {
					return hit.getDamage();
				}

				return 0;
			}
			if (getId() == STAGES[4]) {
				if (p.getCombat().getCombatType() == CombatTypes.SAGITTARIUS) {
					return hit.getDamage();
				}

				return 0;
			}
			if (getId() == STAGES[5]) {
				if ((p.getCombat().getCombatType() == CombatTypes.MAGE) && (MageConstants.getSpellTypeForId(p.getMage().getSpellCasting().getCurrentSpellId()) == MageConstants.SpellType.EARTH)) {
					return hit.getDamage();
				}

				return 0;
			}
		}
	}

	return hit.getDamage();
	}

	@Override
	public NpcCombatDefinition getCombatDefinition() {
	return GameDefinitionLoader.getNpcCombatDefinition(STAGES[0]);
	}
}
