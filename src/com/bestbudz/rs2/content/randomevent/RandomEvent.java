package com.bestbudz.rs2.content.randomevent;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public enum RandomEvent {
	SINGLETON;

	public boolean testRandom(Stoner stoner) {
	if (Utility.random(100) == 0) {
		if (canStart(stoner)) {
			start(stoner);
		}
		return true;
	}

	return false;
	}

	private boolean canStart(Stoner stoner) {
	if (stoner.getCombat().inCombat() || stoner.getCombat().getAssaulting() != null) {
		return false;
	}

	if (stoner.inWilderness()) {
		return false;
	}

	if (stoner.getDueling().isDueling()) {
		return false;
	}

	if (stoner.getTrade().trading()) {
		return false;
	}

	return true;
	}

	private void start(Stoner stoner) {
	stoner.send(new SendInterface(16135));
	stoner.send(new SendString("", 16144));
	}

	public boolean clickButton(Stoner stoner, int button) {
	return false;
	}
}