package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.GraphicTask;
import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.core.util.Utility;
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

    success =
        Utility.randomNumber(RangeFormulas.calculateRangeAegis(entity.getCombat().getAssaulting()))
            <= Utility.randomNumber(RangeFormulas.calculateRangeAssault(entity));

    int damage =
        entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.SAGITTARIUS) + 1));

    Hit hit =
        new Hit(
            entity,
            (success) || (entity.isIgnoreHitSuccess()) ? damage : 0,
            Hit.HitTypes.SAGITTARIUS);

    entity.getCombat().updateHitChain(hit.getDamage());

    entity.setLastHitSuccess((success) || (entity.isIgnoreHitSuccess()));

    entity.getCombat().updateTimers(assault.getAssaultDelay());

    if (animation != null) {
      entity.getUpdateFlags().sendAnimation(animation);
    }

    if (start != null) {
      executeStartGraphic();
    }

    if (projectile != null) {
      executeProjectile(assaulting);
    }

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

    if (end != null) {
      TaskQueue.queue(new GraphicTask(assault.getHitDelay(), false, end, assaulting));
    }

    assaulting.getCombat().setInCombat(entity);
    entity.doConsecutiveAssaults(assaulting);
    entity.onAssault(assaulting, hit.getDamage(), CombatTypes.SAGITTARIUS, success);
  }

  public void executeProjectile(Entity target) {
    final int lockon = target.isNpc() ? target.getIndex() + 1 : -target.getIndex() - 1;
    final byte offsetX = (byte) ((entity.getLocation().getY() - target.getLocation().getY()) * -1);
    final byte offsetY = (byte) ((entity.getLocation().getX() - target.getLocation().getX()) * -1);

    if (pOffset > 0) {
      final Projectile p = new Projectile(projectile);
      TaskQueue.queue(
          new Task(pOffset) {
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

      TaskQueue.queue(
          new Task(gOffset) {
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

  public int getProjectileOffset() {
    return pOffset;
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

  public void setProjectile(Projectile projectile) {
    this.projectile = projectile;
  }

  public void setProjectileOffset(int pOffset) {
    this.pOffset = pOffset;
  }

  public void setStart(Graphic start) {
    this.start = start;
  }

  public void setStartGfxOffset(byte gOffset) {
    this.gOffset = gOffset;
  }
}
