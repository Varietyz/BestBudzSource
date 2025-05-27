package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.content.combat.impl.StonerDrops;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class StonerDeathTask extends Task {

	private final Stoner stoner;
	private final Controller c;

	public StonerDeathTask(final Stoner stoner) {
	super(stoner, 5, false, Task.StackType.NEVER_STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);

	this.stoner = stoner;
	c = stoner.getController();

	if (stoner.isDead()) {
		stop();
		return;
	}

	stoner.getUpdateFlags().faceEntity(65535);
	stoner.setDead(true);
	stoner.getMovementHandler().reset();

	TaskQueue.queue(new Task(stoner, 2, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
		@Override
		public void execute() {
		stoner.getUpdateFlags().sendGraphic(new Graphic(293));
		stoner.getUpdateFlags().sendAnimation(836, 0);

		Entity killer = stoner.getCombat().getDamageTracker().getKiller();

		if (stoner.getNecromance().active(Necromance.RETRIBUTION)) {
			stoner.getUpdateFlags().sendGraphic(new Graphic(437));

			if (killer != null && !killer.isNpc()) {
				int damage = Utility.random((int) (stoner.getMaxGrades()[5] * 0.25)) + 1;

				killer.hit(new Hit(stoner, damage, HitTypes.NONE));
			}
		}

		stop();
		}

		@Override
		public void onStop() {
		}
	});
	}

	@Override
	public void execute() {

	if (!c.isSafe(stoner) && !stoner.inZulrah()) {
		StonerDrops.dropItemsOnDeath(stoner);
	}

	if (stoner.isJailed()) {
		stoner.teleport(StonerConstants.JAILED_AREA);
	} else if (stoner.getController() != null) {
		stoner.teleport(stoner.getController().getRespawnLocation(stoner));
	} else {
		stoner.teleport(StonerConstants.HOME);
	}

	if (stoner.isPoisoned()) {
		stoner.curePoison(0);
	}

	if (stoner.getSkulling().isSkulled()) {
		stoner.getSkulling().unskull(stoner);
	}



	stoner.getNecromance().disable();

	stoner.getRunEnergy().setEnergy(100);

	stoner.getEquipment().onLogin();

	stoner.setFreeze(0);

	stoner.setTeleblockTime(0);

	if (stoner.inWilderness()) {
		stoner.setDeaths(stoner.getDeaths() + 1);
	}

	stoner.setHunterKills(0);
	stoner.setRogueKills(0);

	stoner.setAppearanceUpdateRequired(true);
	stoner.getClient().queueOutgoingPacket(new SendMessage("You have gotten yourself killed, good job!"));
	stoner.getUpdateFlags().sendAnimation(65535, 0);
	stoner.getCombat().forRespawn();

	AchievementHandler.activateAchievement(stoner, AchievementList.DIE_1_TIME, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.DIE_10_TIME, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.DIE_50_TIME, 1);

	c.onDeath(stoner);

	if (stoner.getCombat().getDamageTracker().getKiller() != null) {
		c.onKill(stoner, stoner.getCombat().getDamageTracker().getKiller());
	}

	stoner.getCombat().forRespawn();

	stop();
	}

	@Override
	public void onStop() {
	}
}