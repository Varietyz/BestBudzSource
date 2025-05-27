package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ZamorakianHastaSpecialAssault implements Special {

	@Override
	public boolean checkRequirements(Stoner stoner) {
	return true;
	}

	@Override
	public int getSpecialAmountRequired() {
	return 25;
	}

	@Override
	public void handleAssault(Stoner stoner) {
	stoner.getCombat().getAssaulting().getUpdateFlags().sendGraphic(Graphic.highGraphic(80, 0));
	stoner.getCombat().getMelee().setAnimation(new Animation(1064, 0));
	stoner.getUpdateFlags().sendGraphic(Graphic.highGraphic(253, 0));
	}
}
