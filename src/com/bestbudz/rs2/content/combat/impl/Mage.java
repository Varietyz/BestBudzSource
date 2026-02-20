package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.task.RunOnceTask;
import com.bestbudz.core.task.Task;
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
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Mage {

	private final Entity entity;
	private Assault assault = new Assault(2, 2);
	private Animation animation = null;
	private Graphic start = null;
	private Graphic end = null;
	private Projectile projectile = null;
	private byte pDelay = 0;
	private int nextHit = 0;
	private boolean multi = true;

	public Mage(Entity entity) {
		this.entity = entity;
	}

	public void execute(Entity assaulting) {
		if (assault == null) {
			return;
		}

		entity.getCombat().updateTimers(assault.getAssaultDelay() + 1);

		// Handle animations, graphics, and projectiles based on entity type
		handleCombatVisuals(assaulting);

		entity.doConsecutiveAssaults(assaulting);
		finish(assaulting);
	}

	private void handleCombatVisuals(Entity assaulting) {
		if (entity instanceof Stoner && ((Stoner) entity).isPetStoner()) {
			handlePetVisuals(assaulting);
		} else {
			handleRegularVisuals(assaulting);
		}
	}

	private void handlePetVisuals(Entity assaulting) {
		// Handle pet-specific animation
		Animation petAnimation = (Animation) entity.getAttributes().get("PET_MAGE_ANIMATION");
		if (petAnimation != null) {
			entity.getUpdateFlags().sendAnimation(petAnimation);
			entity.setAnimationWithCombatLock(petAnimation, Combat.CombatTypes.MAGE);
			entity.getCombat().setLastCombatActionTime(System.currentTimeMillis());
		}

		// Handle pet-specific start graphic
		Graphic petStartGraphic = (Graphic) entity.getAttributes().get("PET_MAGE_START");
		if (petStartGraphic != null && petStartGraphic.getId() != 0 && petStartGraphic.getId() != 448) {
			entity.getUpdateFlags().sendGraphic(petStartGraphic);
		}

		// Handle pet-specific projectile
		Projectile petProjectile = (Projectile) entity.getAttributes().get("PET_MAGE_PROJECTILE");
		if (petProjectile != null) {
			sendProjectile(petProjectile, assaulting);
		}
	}

	private void handleRegularVisuals(Entity assaulting) {
		// Handle regular animation (for stoners and other entities)
		if (animation != null) {
			entity.getUpdateFlags().sendAnimation(animation);
			entity.setAnimationWithCombatLock(animation, Combat.CombatTypes.MAGE);
			entity.getCombat().setLastCombatActionTime(System.currentTimeMillis());
		}

		// Handle regular start graphic
		if (start != null && start.getId() != 0) {
			entity.getUpdateFlags().sendGraphic(start);
		}

		// Handle regular projectile
		if (projectile != null) {
			sendProjectile(projectile, assaulting);
		}
	}

	private void sendProjectile(Projectile projectileToSend, Entity assaulting) {
		final int lockon = assaulting.isNpc() ? assaulting.getIndex() + 1 : -assaulting.getIndex() - 1;
		final int offsetX = ((entity.getLocation().getY() - assaulting.getLocation().getY()) * -1) - 2;
		final int offsetY = ((entity.getLocation().getX() - assaulting.getLocation().getX()) * -1) - 3;

		if (pDelay > 0) {
			TaskQueue.queue(new RunOnceTask(entity, pDelay) {
				@Override
				public void onStop() {
					World.sendProjectile(
						projectileToSend,
						CombatConstants.getOffsetProjectileLocation(entity),
						lockon,
						(byte) offsetX,
						(byte) offsetY);
				}
			});
		} else {
			World.sendProjectile(
				projectileToSend,
				CombatConstants.getOffsetProjectileLocation(entity),
				lockon,
				(byte) offsetX,
				(byte) offsetY);
		}
	}

	public void finish(Entity assaulting) {
		boolean success;

		double accuracy = MageFormulas.getMageAssaultRoll(entity);
		double aegis = MageFormulas.getMageAegisRoll(entity.getCombat().getAssaulting());
		double chance = FormulaData.getChance(accuracy, aegis, entity, entity.getCombat().getAssaulting());
		boolean accurate = FormulaData.isAccurateHit(chance, entity, entity.getCombat().getAssaulting());
		entity.getCombat().setHitChance(chance);

		success = accurate;

		if (nextHit > -1) {
			success = true;
		} else if (nextHit == -1) {
			success = false;
		}

		int damage = nextHit == -2
			? entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.MAGE) + 1))
			: nextHit;

		if (nextHit != -2) {
			nextHit = -2;
		}

		Hit hit = new Hit(
			entity,
			(success || entity.isNpc()) || (entity.isIgnoreHitSuccess()) ? damage : -1,
			Hit.HitTypes.MAGE);

		entity.onAssault(assaulting, hit.getDamage(), CombatTypes.MAGE, success || entity.isNpc());
		entity.getCombat().updateHitChain(hit.getDamage());
		entity.setLastHitSuccess((success || entity.isNpc()) || (entity.isIgnoreHitSuccess()));

		if (hit.getDamage() > -1) {
			TaskQueue.queue(new HitTask(assault.getHitDelay(), false, hit, assaulting));
		}

		// Handle end graphics separately for pets vs regular entities
		Graphic endGraphic = getEndGraphic(success);

		if (endGraphic != null) {
			TaskQueue.queue(new GraphicTask(assault.getHitDelay(), false, endGraphic, assaulting));

			// Handle double hit logic
			if (FormulaData.isDoubleHit(entity.getCombat().getHitChance(),
				entity.getCombat().getHitChainStage(), entity, assaulting)) {
				handleDoubleHit(hit, assaulting);
			}
		}

		assaulting.getCombat().setInCombat(entity);
	}

	private Graphic getEndGraphic(boolean success) {
		if (success || entity.isNpc()) {
			if (entity instanceof Stoner && ((Stoner) entity).isPetStoner()) {
				// Pet-specific end graphic
				Graphic petEndGraphic = (Graphic) entity.getAttributes().get("PET_MAGE_END");
				return petEndGraphic; // No fallback for pets - returns null if not set
			} else {
				// Regular end graphic (for stoners and other entities)
				return this.end;
			}
		} else if (!entity.isNpc()) {
			// Miss graphic for non-NPCs
			return new Graphic(85, 0, true);
		}

		return null;
	}

	private void handleDoubleHit(Hit originalHit, Entity assaulting) {
		long secondHitDamage = originalHit.getDamage() / 2;

		if (secondHitDamage > 0) {
			Hit secondHit = new Hit(entity, secondHitDamage, Hit.HitTypes.DEFLECT);
			final Entity target = assaulting;

			TaskQueue.queue(new Task(2) { // 1 tick = 300ms
				@Override
				public void execute() {
					Combat.applyHit(target, secondHit);
					if (entity instanceof Stoner) {
						((Stoner) entity).getClient().queueOutgoingPacket(
							new SendMessage("@gre@Double strike landed! Bonus: " + secondHitDamage));
					}
					FormulaData.updateCombatEvolution(entity, assaulting,
						originalHit.getDamage() > -1, Math.max(0, (int)originalHit.getDamage()));
					if (entity instanceof Stoner) {
						((Stoner) entity).getResonance().updateResonance(
							originalHit.getDamage() > -1,
							Math.max(0, (int)originalHit.getDamage()),
							Combat.CombatTypes.MAGE);
					}
					entity.getCombat().resetHitChain();
					stop();
				}

				@Override
				public void onStop() {}
			});
		}
	}

	public Assault getAssault() {
		return assault;
	}

	public byte getpDelay() {
		return pDelay;
	}

	public void setpDelay(byte pDelay) {
		this.pDelay = pDelay;
	}

	public boolean isMulti() {
		return multi;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

	public void setAssault(
		Assault assault, Animation animation, Graphic start, Graphic end, Projectile projectile) {
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

	public void setNextHit(int hit) {
		nextHit = hit;
	}
}