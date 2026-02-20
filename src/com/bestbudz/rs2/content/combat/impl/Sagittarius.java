package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.GraphicTask;
import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.content.combat.formula.RangeFormulas;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Sagittarius {

	private final Entity entity;
	private Assault assault = null;
	private Animation animation = null;
	private Graphic start = null;
	private Graphic end = null;
	private Projectile projectile = null;

	private int pOffset = 0;
	private byte gOffset = 0;

	public Sagittarius(Entity entity) {
		this.entity = entity;
	}

	public void execute(Entity assaulting) {
		if ((assault == null) || (assaulting == null) || (assaulting.isDead())) {
			return;
		}

		boolean success;

		double accuracy = RangeFormulas.calculateRangeAssault(entity);
		double aegis = RangeFormulas.calculateRangeAegis(entity.getCombat().getAssaulting());
		double chance = FormulaData.getChance(accuracy, aegis, entity, entity.getCombat().getAssaulting());
		success = FormulaData.isAccurateHit(chance, entity, entity.getCombat().getAssaulting());

		int baseDamage = entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.SAGITTARIUS) + 1));
		int damage = (int)FormulaData.applyEmergentScaling(entity, baseDamage);

		Hit hit = new Hit(
			entity,
			(success) || (entity.isIgnoreHitSuccess()) ? damage : 0,
			Hit.HitTypes.SAGITTARIUS);

		entity.getCombat().updateHitChain(hit.getDamage());
		entity.setLastHitSuccess((success) || (entity.isIgnoreHitSuccess()));
		entity.getCombat().updateTimers(assault.getAssaultDelay());

		// Handle combat visuals based on entity type
		handleCombatVisuals(assaulting);

		TaskQueue.queue(new HitTask(assault.getHitDelay(), false, hit, assaulting));

		// Handle double hit
		if (FormulaData.isDoubleHit(entity.getCombat().getHitChance(), entity.getCombat().getHitChainStage(), entity, assaulting)) {
			handleDoubleHit(hit, assaulting);
		}

		// Handle end graphics
		handleEndGraphic(assaulting);

		assaulting.getCombat().setInCombat(entity);
		entity.doConsecutiveAssaults(assaulting);
		entity.onAssault(assaulting, hit.getDamage(), CombatTypes.SAGITTARIUS, success);
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
		Animation petAnimation = (Animation) entity.getAttributes().get("PET_SAGITTARIUS_ANIMATION");
		if (petAnimation != null) {
			entity.getUpdateFlags().sendAnimation(petAnimation);
			entity.setAnimationWithCombatLock(petAnimation, Combat.CombatTypes.SAGITTARIUS);
			entity.getCombat().setLastCombatActionTime(System.currentTimeMillis());
		}

		// Handle pet-specific start graphic
		Graphic petStartGraphic = (Graphic) entity.getAttributes().get("PET_SAGITTARIUS_START");
		Graphic petEndGraphic = (Graphic) entity.getAttributes().get("PET_SAGITTARIUS_END");
		if (petStartGraphic != null) {
			if (petStartGraphic.getId() == 451 || petEndGraphic == null) {
				executeStartGraphic(assaulting, petStartGraphic);
			} else {
				executeStartGraphic(petStartGraphic);
			}
		}

		// Handle pet-specific projectile
		Projectile petProjectile = (Projectile) entity.getAttributes().get("PET_SAGITTARIUS_PROJECTILE");
		if (petProjectile != null) {
			executeProjectile(assaulting, petProjectile);
		}
	}

	private void handleRegularVisuals(Entity assaulting) {
		// Handle regular animation (for stoners and other entities)
		if (animation != null) {
			entity.getUpdateFlags().sendAnimation(animation);
			entity.setAnimationWithCombatLock(animation, Combat.CombatTypes.SAGITTARIUS);
			entity.getCombat().setLastCombatActionTime(System.currentTimeMillis());
		}

		// Handle regular start graphic
		if (start != null) {
			executeStartGraphic();
		}

		// Handle regular projectile
		if (projectile != null) {
			executeProjectile(assaulting);
		}
	}

	private void handleEndGraphic(Entity assaulting) {
		Graphic endGraphic = null;

		if (entity instanceof Stoner && ((Stoner) entity).isPetStoner()) {
			// Pet-specific end graphic
			endGraphic = (Graphic) entity.getAttributes().get("PET_SAGITTARIUS_END");
		} else {
			// Regular end graphic (for stoners and other entities)
			endGraphic = this.end;
		}

		if (endGraphic != null) {
			TaskQueue.queue(new GraphicTask(assault.getHitDelay(), false, endGraphic, assaulting));
		}
	}

	private void handleDoubleHit(Hit originalHit, Entity assaulting) {
		long secondHitDamage = originalHit.getDamage() / 2;

		if (secondHitDamage > 0) {
			Hit secondHit = new Hit(entity, secondHitDamage, Hit.HitTypes.MELEE);
			final Entity target = assaulting;

			TaskQueue.queue(new Task(2) { // 1 tick = 300ms
				@Override
				public void execute() {
					Combat.applyHit(target, secondHit);
					if (entity instanceof Stoner) {
						((Stoner) entity).getClient().queueOutgoingPacket(
							new SendMessage("@gre@Double strike landed! Bonus: " + secondHitDamage));
					}
					FormulaData.updateCombatEvolution(entity, assaulting, originalHit.getDamage() > 0, (int)originalHit.getDamage());
					if (entity instanceof Stoner) {
						((Stoner) entity).getResonance().updateResonance(
							originalHit.getDamage() > 0,
							(int)originalHit.getDamage(),
							Combat.CombatTypes.SAGITTARIUS);
					}
					entity.getCombat().resetHitChain();
					stop();
				}

				@Override
				public void onStop() {}
			});
		}
	}

	public void executeProjectile(Entity target) {
		final int lockon = target.isNpc() ? target.getIndex() + 1 : -target.getIndex() - 1;
		final byte offsetX = (byte) ((entity.getLocation().getY() - target.getLocation().getY()) * -1);
		final byte offsetY = (byte) ((entity.getLocation().getX() - target.getLocation().getX()) * -1);

		if (pOffset > 0) {
			final Projectile p = new Projectile(projectile);
			TaskQueue.queue(new Task(pOffset) {
				@Override
				public void execute() {
					World.sendProjectile(p, entity.getLocation(), lockon, offsetX, offsetY);
					stop();
				}

				@Override
				public void onStop() {}
			});
		} else {
			World.sendProjectile(projectile, entity.getLocation(), lockon, offsetX, offsetY);
		}
	}

	public void executeStartGraphic() {
		if (gOffset > 0) {
			final Graphic g = new Graphic(start);
			TaskQueue.queue(new Task(gOffset) {
				@Override
				public void execute() {
					entity.getUpdateFlags().sendGraphic(g);
					stop();
				}

				@Override
				public void onStop() {}
			});
		} else {
			entity.getUpdateFlags().sendGraphic(start);
		}
	}

	private void executeProjectile(Entity target, Projectile petProjectile) {
		final int lockon = target.isNpc() ? target.getIndex() + 1 : -target.getIndex() - 1;
		final byte offsetX = (byte) ((entity.getLocation().getY() - target.getLocation().getY()) * -1);
		final byte offsetY = (byte) ((entity.getLocation().getX() - target.getLocation().getX()) * -1);

		if (pOffset > 0) {
			final Projectile p = new Projectile(petProjectile);
			TaskQueue.queue(new Task(pOffset) {
				@Override
				public void execute() {
					World.sendProjectile(p, entity.getLocation(), lockon, offsetX, offsetY);
					stop();
				}
				@Override
				public void onStop() {}
			});
		} else {
			World.sendProjectile(petProjectile, entity.getLocation(), lockon, offsetX, offsetY);
		}
	}

	private void executeStartGraphic(Graphic petStartGraphic) {
		if (gOffset > 0) {
			final Graphic g = new Graphic(petStartGraphic);
			TaskQueue.queue(new Task(gOffset) {
				@Override
				public void execute() {
					entity.getUpdateFlags().sendGraphic(g);
					stop();
				}
				@Override
				public void onStop() {}
			});
		} else {
			entity.getUpdateFlags().sendGraphic(petStartGraphic);
		}
	}

	private void executeStartGraphic(Entity target, Graphic petStartGraphic) {
		if (gOffset > 0) {
			final Graphic g = new Graphic(petStartGraphic);
			TaskQueue.queue(new Task(gOffset) {
				@Override
				public void execute() {
					target.getUpdateFlags().sendGraphic(new Graphic(
						petStartGraphic.getId(),
						petStartGraphic.getDelay(),
						petStartGraphic.getHeight()));
					stop();
				}
				@Override
				public void onStop() {}
			});
		} else {
			target.getUpdateFlags().sendGraphic(petStartGraphic);
		}
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public Assault getAssault() {
		return assault;
	}

	public void setAssault(Assault assault) {
		this.assault = assault;
	}

	public Projectile getProjectile() {
		return projectile;
	}

	public void setProjectile(Projectile projectile) {
		this.projectile = projectile;
	}

	public int getProjectileOffset() {
		return pOffset;
	}

	public void setProjectileOffset(int pOffset) {
		this.pOffset = pOffset;
	}

	public void setAssault(
		Assault assault, Animation animation, Graphic start, Graphic end, Projectile projectile) {
		this.assault = assault;
		this.animation = animation;
		this.start = start;
		this.end = end;
		this.projectile = projectile;
	}

	public void setEnd(Graphic end) {
		this.end = end;
	}

	public void setStart(Graphic start) {
		this.start = start;
	}

	public void setStartGfxOffset(byte gOffset) {
		this.gOffset = gOffset;
	}
}