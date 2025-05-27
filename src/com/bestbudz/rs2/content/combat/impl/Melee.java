package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.content.combat.formula.MeleeFormulas;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;

public class Melee {

	private final Entity entity;
	private Assault assault = new Assault(1, 5);
	private Animation animation = new Animation(422, 0);

	private int nextDamage = -1;
	private double damageBoost = 1.0D;

	public Melee(Entity entity) {
	this.entity = entity;
	}

	public void execute(Entity assaulting) {
	if (assault == null) {
		return;
	}

	double accuracy = MeleeFormulas.getAssaultRoll(entity);
	double aegis = MeleeFormulas.getAegisRoll(entity, entity.getCombat().getAssaulting());
	double chance = FormulaData.getChance(accuracy, aegis);
	boolean accurate = FormulaData.isAccurateHit(chance);

	boolean success;

	if (accurate) {
		success = true;
	} else {
		success = false;
	}

	int damage = (int) (entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.MELEE) + 1)) * damageBoost);

	if (nextDamage != -1) {
		damage = nextDamage;
		success = true;
	}

	Hit hit = new Hit(entity, (success) || (entity.isIgnoreHitSuccess()) ? damage : 0, Hit.HitTypes.MELEE);
	entity.setLastDamageDealt(!success ? 0 : hit.getDamage());

	entity.setLastHitSuccess((success) || (entity.isIgnoreHitSuccess()));

	entity.onAssault(assaulting, hit.getDamage(), CombatTypes.MELEE, success);

	entity.getCombat().updateTimers(assault.getAssaultDelay());

	if (animation != null) {
		entity.getUpdateFlags().sendAnimation(animation);
	}

	entity.doConsecutiveAssaults(assaulting);
	finish(assaulting, hit);
	}

	public void finish(Entity assaulting, Hit hit) {
	assaulting.getCombat().setInCombat(entity);
	TaskQueue.queue(new HitTask(assault.getHitDelay(), false, hit, assaulting));
	}

	public Animation getAnimation() {
	return animation;
	}

	public Assault getAssault() {
	return assault;
	}

	public void setAnimation(Animation animation) {
	this.animation = animation;
	}

	public void setAssault(Assault assault, Animation animation) {
	this.assault = assault;
	this.animation = animation;
	}

	public void setDamageBoost(double damageBoost) {
	this.damageBoost = damageBoost;
	}

	public void setNextDamage(int nextDamage) {
	this.nextDamage = nextDamage;
	}
}
