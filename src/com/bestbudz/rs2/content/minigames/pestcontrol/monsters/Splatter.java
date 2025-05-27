package com.bestbudz.rs2.content.minigames.pestcontrol.monsters;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.minigames.pestcontrol.Pest;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControlGame;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Splatter extends Pest {

	public Splatter(Location location, PestControlGame game) {
	super(game, PestControlConstants.SPLATTERS[Utility.randomNumber(PestControlConstants.SPLATTERS.length)], location);
	}

	@Override
	public void onDeath() {
	if (Utility.getManhattanDistance(getGame().getVoidKnight().getLocation(), getLocation()) <= 2) {
		getGame().getVoidKnight().hit(new Hit(1 + Utility.randomNumber(5)));
	}

	for (Stoner k : getGame().getStoners()) {
		if (Utility.getManhattanDistance(k.getLocation(), getLocation()) <= 2) {
			k.hit(new Hit(1 + Utility.randomNumber(5)));
		}
	}
	}

	@Override
	public void tick() {
	if (Utility.getManhattanDistance(getGame().getVoidKnight().getLocation(), getLocation()) <= 2) {
		getGrades()[3] = 0;
		checkForDeath();
	}
	}

}
