package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.task.RunOnceTask;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.GraphicTask;
import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.CombatConstants;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.content.combat.formula.MageFormulas;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;

public class Mage {

	private final Entity entity;
	private Assault assault = new Assault(4, 5);
	private Animation animation = null;
	private Graphic start = null;
	private Graphic end = null;
	private Projectile projectile = null;
	private byte pDelay = 0;
	private int nextHit = 0;
	private boolean multi = false;

	public Mage(Entity entity) {
	this.entity = entity;
	}

	public void execute(Entity assaulting) {
	if (assault == null) {
		return;
	}

	entity.getCombat().updateTimers(assault.getAssaultDelay() + 1);

	if (animation != null) {
		entity.getUpdateFlags().sendAnimation(animation);
	}

	if (start != null && start.getId() != 0) {
		entity.getUpdateFlags().sendGraphic(start);
	}

	if (projectile != null) {
		final int lockon = assaulting.isNpc() ? assaulting.getIndex() + 1 : -assaulting.getIndex() - 1;
		final int offsetX = ((entity.getLocation().getY() - assaulting.getLocation().getY()) * -1) - 2;
		final int offsetY = ((entity.getLocation().getX() - assaulting.getLocation().getX()) * -1) - 3;
		if (pDelay > 0) {
			TaskQueue.queue(new RunOnceTask(entity, pDelay) {
				@Override
				public void onStop() {
				World.sendProjectile(projectile, CombatConstants.getOffsetProjectileLocation(entity), lockon, (byte) offsetX, (byte) offsetY);
				}
			});
		} else {
			World.sendProjectile(projectile, CombatConstants.getOffsetProjectileLocation(entity), lockon, (byte) offsetX, (byte) offsetY);
		}
	}

	entity.doConsecutiveAssaults(assaulting);
	finish(assaulting);
	}

	public void finish(Entity assaulting) {

	boolean success;

	double accuracy = MageFormulas.getMageAssaultRoll(entity);
	double aegis = MageFormulas.getMageAegisRoll(entity.getCombat().getAssaulting());
	double chance = FormulaData.getChance(accuracy, aegis);
	boolean accurate = FormulaData.isAccurateHit(chance);

	if (accurate) {
		success = true;
	} else {
		success = false;
	}

	if (nextHit > -1) {
		success = true;
	} else if (nextHit == -1) {
		success = false;
	}

	int damage = nextHit == -2 ? entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.MAGE) + 1)) : nextHit;

	if (nextHit != -2) {
		nextHit = -2;
	}

	Hit hit = new Hit(entity, (success || entity.isNpc()) || (entity.isIgnoreHitSuccess()) ? damage : -1, Hit.HitTypes.MAGE);

	entity.onAssault(assaulting, hit.getDamage(), CombatTypes.MAGE, success || entity.isNpc());

	entity.setLastDamageDealt(hit.getDamage());
	entity.setLastHitSuccess((success || entity.isNpc()) || (entity.isIgnoreHitSuccess()));

	if (hit.getDamage() > -1) {
		TaskQueue.queue(new HitTask(assault.getHitDelay(), false, hit, assaulting));
	}

	Graphic end = null;

	if ((success || entity.isNpc()) && (this.end != null))
		end = this.end;
	else if (!success && !entity.isNpc()) {
		end = new Graphic(85, 0, true);
	}

	if (end != null) {
		TaskQueue.queue(new GraphicTask(assault.getHitDelay(), false, end, assaulting));
	}
	assaulting.getCombat().setInCombat(entity);
	}

	public Assault getAssault() {
	return assault;
	}

	public byte getpDelay() {
	return pDelay;
	}

	public boolean isMulti() {
	return multi;
	}

	public void setAssault(Assault assault, Animation animation, Graphic start, Graphic end, Projectile projectile) {
	this.assault = assault;
	this.animation = animation;
	if (start != null) {
		this.start = new Graphic(start.getId(), start.getDelay(), true);
	} else {
		this.start = start;
	}
	if (end != null) {
		this.end = new Graphic(end.getId(), end.getDelay(), true);
	} else {
		this.end = end;
	}
	this.projectile = projectile;
	}

	public void setMulti(boolean multi) {
	this.multi = multi;
	}

	public void setNextHit(int hit) {
	nextHit = hit;
	}

	public void setpDelay(byte pDelay) {
	this.pDelay = pDelay;
	}
}
