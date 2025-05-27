package com.mayhem.rs2.entity.mob.impl;

import com.mayhem.core.definitions.NpcCombatDefinition;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.content.combat.Combat.CombatTypes;
import com.mayhem.rs2.content.skill.magic.MagicConstants;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;

public class GelatinnothMother extends Mob {
	public static final int[] STAGES = { 4884, 4885, 4886, 4887, 4888, 4889 };

	private byte stage = 0;

	public GelatinnothMother(Player player, Location location) {
		super(player, 4884, false, false, false, location);
		if (getOwner() != null) {
			getCombat().setAttack(getOwner());
			getFollowing().setIgnoreDistance(true);
			getUpdateFlags().faceEntity(getOwner().getIndex());
		}
	

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
	public int getRespawnTime() {
		return 5;
	}

	@Override
	public int getAffectedDamage(Hit hit) {
		if (hit.getAttacker() != null && !hit.getAttacker().isNpc()) {
			Player p = World.getPlayers()[hit.getAttacker().getIndex()];

			if (p != null && PlayerConstants.isOwner(p)) {
				return hit.getDamage();
			}
		}

		if ((hit.getAttacker() != null) && (!hit.getAttacker().isNpc())) {
			Player p = com.mayhem.rs2.entity.World.getPlayers()[hit.getAttacker().getIndex()];
			if (p != null) {
				if (getId() == STAGES[0]) {
					if ((p.getCombat().getCombatType() == CombatTypes.MAGIC) && (MagicConstants.getSpellTypeForId(p.getMagic().getSpellCasting().getCurrentSpellId()) == MagicConstants.SpellType.WIND)) {
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
					if ((p.getCombat().getCombatType() == CombatTypes.MAGIC) && (MagicConstants.getSpellTypeForId(p.getMagic().getSpellCasting().getCurrentSpellId()) == MagicConstants.SpellType.WATER)) {
						return hit.getDamage();
					}

					return 0;
				}
				if (getId() == STAGES[3]) {
					if ((p.getCombat().getCombatType() == CombatTypes.MAGIC) && (MagicConstants.getSpellTypeForId(p.getMagic().getSpellCasting().getCurrentSpellId()) == MagicConstants.SpellType.FIRE)) {
						return hit.getDamage();
					}

					return 0;
				}
				if (getId() == STAGES[4]) {
					if (p.getCombat().getCombatType() == CombatTypes.RANGED) {
						return hit.getDamage();
					}

					return 0;
				}
				if (getId() == STAGES[5]) {
					if ((p.getCombat().getCombatType() == CombatTypes.MAGIC) && (MagicConstants.getSpellTypeForId(p.getMagic().getSpellCasting().getCurrentSpellId()) == MagicConstants.SpellType.EARTH)) {
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
	
	public void onStop() {
	
}
}
