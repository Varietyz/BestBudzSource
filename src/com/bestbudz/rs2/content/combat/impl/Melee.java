package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.content.combat.formula.MeleeFormulas;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Melee {

  private final Entity entity;
  private Assault assault = new Assault(1, 2);
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
    entity.getCombat().setHitChance(chance);

    boolean success;

    success = accurate;

    int damage =
        (int)
            (entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.MELEE) + 1))
                * damageBoost);

    if (nextDamage != -1) {
      damage = nextDamage;
      success = true;
    }

    Hit hit =
        new Hit(
            entity, (success) || (entity.isIgnoreHitSuccess()) ? damage : 0, Hit.HitTypes.MELEE);
    long dealt = !success ? 0 : hit.getDamage();
    entity.getCombat().updateHitChain(dealt);

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
    if (FormulaData.isDoubleHit(
        entity.getCombat().getHitChance(), entity.getCombat().getHitChainStage())) {
      long secondHitDamage = hit.getDamage() / 2;

      if (secondHitDamage > 0) {
        Hit secondHit = new Hit(entity, secondHitDamage, Hit.HitTypes.MELEE);

        // Copy of 'assaulting' must be made effectively final for the inner class
        final Entity target = assaulting;

        TaskQueue.queue(
            new Task(1) { // 1 tick = 300ms
              @Override
              public void execute() {
                Combat.applyHit(target, secondHit);
                if (entity instanceof Stoner) {
                  ((Stoner) entity)
                      .getClient()
                      .queueOutgoingPacket(
                          new SendMessage("@gre@Double strike landed! Bonus: " + secondHitDamage));
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

  public Animation getAnimation() {
    return animation;
  }

  public void setAnimation(Animation animation) {
    this.animation = animation;
  }

  public Assault getAssault() {
    return assault;
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
