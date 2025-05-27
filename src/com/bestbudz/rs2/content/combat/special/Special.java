package com.bestbudz.rs2.content.combat.special;

import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract interface Special {

	public abstract boolean checkRequirements(Stoner stoner);

	public abstract int getSpecialAmountRequired();

	public abstract void handleAssault(Stoner stoner);
}
