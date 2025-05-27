package com.bestbudz.rs2.content;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateEnergy;

public class RunEnergy {

  public static final int RESTORE_TIMER = 2;
  public static final int REST_ANIMATION = 11786;
  public static final int STAND_UP_ANIMATION = 11788;
  private final int weight = 0;
  private final Stoner stoner;
  private double energy = 100.0D;
  private boolean allowed = true;
  private boolean running = false;
  private boolean resting = false;

  public RunEnergy(Stoner stoner) {
    this.stoner = stoner;
  }

  public void add(long amount) {
    energy += amount;
    if (energy > 100.0D) {
      energy = 100.0D;
    }
    update();
  }

  public boolean canRun() {
    return energy > 0.0D;
  }

  public void deduct(double percent) {
    energy -= (int) (energy * percent);
    if (energy < 0.0D) {
      energy = 0.0D;
    }
    update();
  }

  public void deduct(int amount) {
    energy -= amount;
    if (energy < 0.0D) {
      energy = 0.0D;
    }
    update();
  }

  public int getEnergy() {
    return (int) energy;
  }

  public void setEnergy(double energy) {
    this.energy = energy;
  }

  public boolean isAllowed() {
    return allowed;
  }

  public void setAllowed(boolean allowed) {
    this.allowed = allowed;
  }

  public boolean isResting() {
    return resting;
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

	public void onRun() {
		// Infinite run for everyone
		energy = 100.0D;
		update();
		allowed = false;
	}

	public void reset() {
    running = false;
    allowed = true;
  }

  public void restoreAll() {
    energy = 100.0D;
    update();
  }

  public void tick() {
    TaskQueue.queue(
        new Task(
            stoner,
            4,
            false,
            Task.StackType.STACK,
            Task.BreakType.NEVER,
            TaskIdentifier.RUN_ENERGY) {
          @Override
          public void execute() {
            if ((allowed) && (energy < 100D)) {
              RunEnergy en = RunEnergy.this;
              en.energy =
                  (en.energy + (resting ? 5.0D : (1.0D + stoner.getMaxGrades()[16] * 0.011D)));

              if (energy > 100.0D) {
                energy = 100.0D;
              }

              update();
            } else if (!allowed) {
              allowed = true;
            }
            if ((resting) && (energy == 100.0D)) toggleResting();
          }

          @Override
          public void onStop() {}
        });
  }

  public void toggleResting() {
    if (energy >= 100) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("Your energy is already full."));
      return;
    }

    resting = (!resting);

    if (!resting) {
      stoner.getUpdateFlags().sendAnimation(11788, 0);
      stoner.getEquipment().updateStonerAnimations();
      stoner.getClient().queueOutgoingPacket(new SendConfig(778, 0));
    } else {
      stoner.getUpdateFlags().sendAnimation(11786, 0);
      stoner.getAnimations().setStandEmote(11786);
      stoner.getMovementHandler().reset();
      stoner.getClient().queueOutgoingPacket(new SendConfig(778, 1));
    }

    stoner.setAppearanceUpdateRequired(true);
  }

  @Override
  public String toString() {
    return "RunEnergy [energy=" + energy + ", weight=" + weight + "]";
  }

  public void update() {
    stoner.getClient().queueOutgoingPacket(new SendUpdateEnergy(energy));
  }
}
