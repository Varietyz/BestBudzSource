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

    if (animation != null) {
      entity.getUpdateFlags().sendAnimation(animation);
    }

    if (start != null && start.getId() != 0) {
      entity.getUpdateFlags().sendGraphic(start);
    }

    if (projectile != null) {
      final int lockon =
          assaulting.isNpc() ? assaulting.getIndex() + 1 : -assaulting.getIndex() - 1;
      final int offsetX =
          ((entity.getLocation().getY() - assaulting.getLocation().getY()) * -1) - 2;
      final int offsetY =
          ((entity.getLocation().getX() - assaulting.getLocation().getX()) * -1) - 3;
      if (pDelay > 0) {
        TaskQueue.queue(
            new RunOnceTask(entity, pDelay) {
              @Override
              public void onStop() {
                World.sendProjectile(
                    projectile,
                    CombatConstants.getOffsetProjectileLocation(entity),
                    lockon,
                    (byte) offsetX,
                    (byte) offsetY);
              }
            });
      } else {
        World.sendProjectile(
            projectile,
            CombatConstants.getOffsetProjectileLocation(entity),
            lockon,
            (byte) offsetX,
            (byte) offsetY);
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
	  entity.getCombat().setHitChance(chance);

    success = accurate;

    if (nextHit > -1) {
      success = true;
    } else if (nextHit == -1) {
      success = false;
    }

    int damage =
        nextHit == -2
            ? entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.MAGE) + 1))
            : nextHit;


    if (nextHit != -2) {
      nextHit = -2;
    }

    Hit hit =
        new Hit(
            entity,
            (success || entity.isNpc()) || (entity.isIgnoreHitSuccess()) ? damage : -1,
            Hit.HitTypes.MAGE);

    entity.onAssault(assaulting, hit.getDamage(), CombatTypes.MAGE, success || entity.isNpc());

    entity.getCombat().updateHitChain(hit.getDamage());

    entity.setLastHitSuccess((success || entity.isNpc()) || (entity.isIgnoreHitSuccess()));

    if (hit.getDamage() > -1) {
      TaskQueue.queue(new HitTask(assault.getHitDelay(), false, hit, assaulting));

    }

    Graphic end = null;

    if ((success || entity.isNpc()) && (this.end != null)) end = this.end;
    else if (!success && !entity.isNpc()) {
      end = new Graphic(85, 0, true);
    }

    if (end != null) {
      TaskQueue.queue(new GraphicTask(assault.getHitDelay(), false, end, assaulting));
		if (FormulaData.isDoubleHit(entity.getCombat().getHitChance(), entity.getCombat().getHitChainStage())) {
			long secondHitDamage = hit.getDamage() / 2;

			if (secondHitDamage > 0) {
				Hit secondHit = new Hit(entity, secondHitDamage, Hit.HitTypes.MELEE);

				// Copy of 'assaulting' must be made effectively final for the inner class
				final Entity target = assaulting;

				TaskQueue.queue(new Task(1) { // 1 tick = 300ms
					@Override
					public void execute() {
						Combat.applyHit(target, secondHit);
						if (entity instanceof Stoner) {
							((Stoner) entity).getClient().queueOutgoingPacket(
								new SendMessage("@gre@Double strike landed! Bonus: " + secondHitDamage)
							);
						}
						entity.getCombat().resetHitChain();
						stop();
					}

					@Override
					public void onStop() {}
				});
			}
		}
    }
    assaulting.getCombat().setInCombat(entity);
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
