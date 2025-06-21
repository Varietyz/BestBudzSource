package com.bestbudz.rs2.entity.mob;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.rs2.content.profession.Professions;

public class MobTransformationHandler {
	private final Mob mob;
	private final short originalNpcId;
	private short transformId = -1;
	private boolean transformUpdate = false;

	public MobTransformationHandler(Mob mob, int originalNpcId) {
		this.mob = mob;
		this.originalNpcId = (short) originalNpcId;
	}

	public void transform(int id) {
		transformUpdate = true;
		transformId = (short) id;
		mob.setNpcId((short) id);
		mob.getCombatHandler().updateCombatType();
		mob.getUpdateFlags().setUpdateRequired(true);

		if (mob.isAssaultable()) {
			NpcCombatDefinition combatDef = mob.getDefinitionProvider().getCombatDefinition();
			if (combatDef != null) {
				mob.setBonuses(combatDef.getBonuses().clone());
				NpcCombatDefinition.Profession[] professions = combatDef.getProfessions();
				if (professions != null) {
					long[] profession = new long[Professions.PROFESSION_COUNT];
					long[] professionMax = new long[Professions.PROFESSION_COUNT];

					for (int i = 0; i < professions.length; i++) {
						if (i == 3) {
							profession[3] = mob.getGrades()[3];
							professionMax[3] = mob.getMaxGrades()[3];
							continue;
						}
						profession[professions[i].getId()] = professions[i].getGrade();
						professionMax[professions[i].getId()] = professions[i].getGrade();
					}

					mob.setGrades(profession.clone());
					mob.setMaxGrades(professionMax.clone());
				}
			}
		}
	}

	public void unTransform() {
		if (originalNpcId != mob.getId()) {
			transform(originalNpcId);
		}
	}

	// Getters and setters
	public int getTransformId() { return transformId; }
	public void setTransformId(int transformId) { this.transformId = (short) transformId; }
	public boolean isTransformUpdate() { return transformUpdate; }
	public void setTransformUpdate(boolean transformUpdate) { this.transformUpdate = transformUpdate; }
	public short getOriginalNpcId() { return originalNpcId; }
}