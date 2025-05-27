package com.bestbudz.rs2.content.profession;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterXInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProfessionGoal;
import java.util.HashMap;

public enum ProfessionGoal {
	ASSAULT(0, 94100, 94099, 33206),
	VIGOUR(2, 94106, 94105, 33209),
	AEGIS(1, 94112, 94111, 33212),
	RANGE(4, 94118, 94117, 33215),
	NECROMANCE(5, 94124, 94123, 33218),
	MAGE(6, 94130, 94129, 33221),
	CONSUMER(20, 94136, 94135, 33224),
	LIFE(3, 94102, 94101, 33207),
	WEEDSMOKING(16, 94108, 94107, 33210),
	THCHEMPISTRY(15, 94114, 94113, 33213),
	ACCOMPLISHER(17, 94120, 94119, 33216),
	HANDINESS(12, 94126, 94125, 33219),
	WOODCARVING(9, 94132, 94131, 33222),
	MERCENARY(18, 94138, 94137, 47130),
	TOTAL_GRADE(25, -1, 94143, 94098),
	QUARRYING(14, 94104, 94103, 33208),
	FORGING(13, 94110, 94109, 33211),
	FISHER(10, 94116, 94115, 33214),
	FOODIE(7, 94122, 94121, 33217),
	PYROMANIAC(11, 94128, 94127, 33220),
	LUMBERING(8, 94134, 94133, 33223),
	CULTIVATION(19, 94140, 94139, 54104);

	public static HashMap<Integer, ProfessionGoal> professions = new HashMap<Integer, ProfessionGoal>();

	static {
		for (ProfessionGoal profession : values()) {
			professions.put(profession.gradeId, profession);
			professions.put(profession.expId, profession);
			professions.put(profession.clearId, profession);
		}
	}

	public final int profession, gradeId, expId, clearId;

	ProfessionGoal(int profession, int gradeId, int expId, int clearId) {
	this.profession = profession;
	this.gradeId = gradeId;
	this.expId = expId;
	this.clearId = clearId;
	}

	public static boolean handle(Stoner stoner, int buttonId) {
	ProfessionGoal professionGoal = professions.get(buttonId);

	if (professionGoal == null) {
		return false;
	}

	if (buttonId == professionGoal.clearId) {
		stoner.send(new SendProfessionGoal(professionGoal.profession, 0, 0, 0));
		return true;
	}

	stoner.send(new SendEnterXInterface(3917, professionGoal.gradeId == buttonId ? 1 : professionGoal.expId == buttonId ? 2 : 0));
	stoner.setEnterXSlot(professionGoal.profession);
	return true;
	}
}