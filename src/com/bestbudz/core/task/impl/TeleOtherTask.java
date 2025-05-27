package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class TeleOtherTask extends Task {
	private final Stoner stoner;
	private final Location to;

	public TeleOtherTask(Entity entity, Stoner stoner, Location to) {
	super(entity, 2);
	this.to = to;
	this.stoner = stoner;

	entity.getUpdateFlags().sendAnimation(1818, 0);
	entity.getUpdateFlags().sendGraphic(new Graphic(343, 15, true));
	}

	@Override
	public void execute() {
	stoner.getMage().teleport(to.getX(), to.getY(), to.getZ(), MageProfession.TeleportTypes.TELE_OTHER);
	stop();
	}

	@Override
	public void onStop() {
	}
}
