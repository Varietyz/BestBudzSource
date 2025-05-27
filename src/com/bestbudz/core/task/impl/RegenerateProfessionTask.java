package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.profession.Profession;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class RegenerateProfessionTask extends Task {

	private final Entity entity;
	private Profession profession = null;
	// private int iterations = 0;

	public static final String EXRTA_HP_REGEN_TASK = "extrahpregentask";

	public RegenerateProfessionTask(Entity entity, int delay) {
	super(entity, delay, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.PROFESSION_RESTORE);

	if (entity == null) {
		stop();
	}

	if (!entity.isNpc()) {
		Stoner p = World.getStoners()[entity.getIndex()];
		if (p != null) {
			profession = p.getProfession();
		}
	}

	this.entity = entity;
	}

	@Override
	public void execute() {
	if (entity == null) {
		stop();
		return;
	}

	// iterations++;

	/**
	 * Check for the Regen bracelet
	 */
	if (!entity.isNpc()) {
		final Stoner p = World.getStoners()[entity.getIndex()];
		if (p != null) {
			if (p.getAttributes().get(EXRTA_HP_REGEN_TASK) == null/* && iterations % 2 == 0 */) {
				Item gl = p.getEquipment().getItems()[EquipmentConstants.GLOVES_SLOT];

				if (gl != null && gl.getId() == 11133) {
					p.getAttributes().set(EXRTA_HP_REGEN_TASK, (byte) 0);
					Task t = new Task(p, 25) {

						@Override
						public void execute() {
						Item gl = p.getEquipment().getItems()[EquipmentConstants.GLOVES_SLOT];

						if (gl == null || gl != null && gl.getId() != 11133) {
							p.getAttributes().remove(EXRTA_HP_REGEN_TASK);
							stop();
							return;
						}

						if (p.getGrades()[3] < p.getMaxGrades()[3]) {
							p.getGrades()[3] += 1;
							p.getProfession().update(3);
						}
						}

						@Override
						public void onStop() {
						}

					};

					TaskQueue.queue(t);
				}
			}
		}
	}

	for (int i = 0; i < (!entity.isNpc() ? Professions.PROFESSION_COUNT : 7); i++) {
		if (i > 7 && entity.isNpc()) {
			break;
		}

		if (i == Professions.NECROMANCE || i == Professions.LIFE && entity.getGrades()[Professions.LIFE] > entity.getMaxGrades()[Professions.LIFE]) {
			continue;
		}

		int lvl = entity.getGrades()[i];
		int max = entity.getMaxGrades()[i];

		// boolean restore = iterations % 2 == 0;
		//
		// if (!entity.isNpc()) {
		// if (entity.getStoner().getNecromance().active(8)) {
		// if (i == 3) {
		// continue;
		// }
		//
		// entity.getStoner().send(new SendMessage("Rapid Restore"));
		//
		// restore = true;
		// }
		//
		// if (entity.getStoner().getNecromance().active(9)) {
		// if (i != 3) {
		// continue;
		// }
		// entity.getStoner().send(new SendMessage("Rapid Heal"));
		//
		// restore = true;
		// }
		// }

		// if (!restore) {
		// return;
		// }
		//
		// if (lvl > max && restore) {
		// continue;
		// }

		if (lvl != max) {
			entity.getGrades()[i] += (lvl < max ? 1 : -1);
			if (profession != null) {
				profession.update(i);
			}
		}
	}
	}

	@Override
	public void onStop() {
	}
}
