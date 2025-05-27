package com.bestbudz.rs2.content.profession.summoning.impl;

import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.content.profession.summoning.FamiliarSpecial;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SpiritTerrorBird implements FamiliarSpecial {
	@Override
	public boolean execute(Stoner stoner, FamiliarMob mob) {
	stoner.getRunEnergy().add(stoner.getGrades()[16] / 2);

	if (stoner.getGrades()[16] < stoner.getMaxGrades()[16] + 1) {
		short[] tmp41_36 = stoner.getGrades();
		tmp41_36[16] = ((short) (tmp41_36[16] + 1));
	}

	mob.getUpdateFlags().sendGraphic(new Graphic(1521, 0, true));

	stoner.getUpdateFlags().sendGraphic(new Graphic(1306, 0, true));

	stoner.getClient().queueOutgoingPacket(new SendMessage("Your Spririt Terrorbird restores your run energy."));
	return true;
	}

	@Override
	public int getAmount() {
	return 8;
	}

	@Override
	public double getExperience() {
	return 2.0D;
	}

	@Override
	public FamiliarSpecial.SpecialType getSpecialType() {
	return FamiliarSpecial.SpecialType.NONE;
	}
}
