package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.profession.resonance.Resonance;
import com.bestbudz.rs2.content.sounds.MobSounds;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.mob.MobDrops;
import com.bestbudz.rs2.entity.mob.Walking;
import com.bestbudz.rs2.entity.mob.impl.KalphiteQueen;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MobDeathTask extends Task {
  public static final int DEATH_DELAY = 2;
  public static final int DEFAULT_DISSAPEAR_DELAY = 5;
  private final Mob mob;

  public MobDeathTask(final Mob mob) {
    super(
        mob,
        mob.getRespawnTime() + 1 + MobConstants.MobDissapearDelay.getDelay(mob.getId()),
        false,
        Task.StackType.STACK,
        Task.BreakType.NEVER,
        TaskIdentifier.CURRENT_ACTION);
    this.mob = mob;
    mob.setDead(true);
    mob.getUpdateFlags().faceEntity(65535);
    mob.getCombat().reset();

    Task death =
        new Task(
            mob,
            2,
            false,
            Task.StackType.STACK,
            Task.BreakType.NEVER,
            TaskIdentifier.CURRENT_ACTION) {
          @Override
          public void execute() {
            Entity killer = mob.getCombat().getDamageTracker().getKiller();

            if ((killer != null) && (!killer.isNpc())) {
              MobSounds.sendDeathSound(
                  com.bestbudz.rs2.entity.World.getStoners()[killer.getIndex()], mob.getId());
            }

            mob.getUpdateFlags().sendAnimation(mob.getDeathAnimation());
            stop();
          }

          @Override
          public void onStop() {}
        };
	  Task dissapear =
		  new Task(
			  mob,
			  MobConstants.MobDissapearDelay.getDelay(mob.getId()),
			  false,
			  Task.StackType.STACK,
			  Task.BreakType.NEVER,
			  TaskIdentifier.CURRENT_ACTION) {
			  @Override
			  public void execute() {
				  mob.onDeath();
				  MobDrops.dropItems(mob.getCombat().getDamageTracker().getKiller(), mob);

				  // Flush any pending resonance messages before mob disappears
				  Entity killer = mob.getCombat().getDamageTracker().getKiller();
				  if (killer != null && !killer.isNpc()) {
					  Stoner stoner = (Stoner) killer; // You might need to cast appropriately
					  if (stoner.getResonance() != null) {
						  stoner.getResonance().flushPendingMessage();
					  }
				  }

				  mob.setVisible(false);
				  Walking.setNpcOnTile(mob, false);
				  mob.getUpdateFlags().setUpdateRequired(true);
				  mob.curePoison(0);
				  mob.unTransform();
				  stop();
			  }

			  @Override
			  public void onStop() {}
		  };
    TaskQueue.queue(death);
    TaskQueue.queue(dissapear);
  }

  @Override
  public void execute() {
    if (mob.shouldRespawn()) {
      mob.setVisible(true);
      mob.getLocation().setAs(mob.getNextSpawnLocation());
      mob.getUpdateFlags().setUpdateRequired(true);
      mob.getCombat().forRespawn();
      Walking.setNpcOnTile(mob, true);
      mob.resetGrades();
      mob.unfreeze();
      mob.getCombat().getDamageTracker().clear();

      if ((mob instanceof KalphiteQueen)) ((KalphiteQueen) mob).transform();
    } else {
      mob.remove();
    }
    stop();
  }

  @Override
  public void onStop() {}
}
