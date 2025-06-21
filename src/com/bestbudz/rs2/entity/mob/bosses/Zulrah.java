package com.bestbudz.rs2.entity.mob.bosses;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.combat.CombatEffect;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProjectile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zulrah extends Mob implements CombatEffect {

  private final long TIME;
  private final List<Mob> SNAKELING = new ArrayList<>();
  private final Location[] LOCATIONS = {
    new Location(2266, 3072, getOwner().getZ()),
    new Location(2276, 3074, getOwner().getZ()),
    new Location(2273, 3064, getOwner().getZ())
  };

  public Zulrah(Stoner stoner, Location location) {
    super(stoner, 2042, false, false, false, location);
    TIME = System.currentTimeMillis();
    if (getOwner() != null) {
      getCombat().setAssault(getOwner());
      getOwner().setHitZulrah(true);
    }
  }

  @Override
  public void hit(Hit hit) {
    if (getOwner() == null) {
      return;
    }

    if (isDead()) {
      return;
    }

    super.hit(hit);
  }

  @Override
  public void onHit(Entity entity, Hit hit) {
    if (entity == null) {
      return;
    }

    int random = Utility.random(11);
    if (random == 0 && getOwner().isHitZulrah()) {
      move();
    } else if (random == 5) {
      snakes();
    }
  }

  @Override
  public void onDeath() {
    for (Mob snakes : SNAKELING) {
      if (!snakes.isDead()) {
        snakes.remove();
      }
    }
    SNAKELING.clear();
    getOwner()
        .send(
            new SendMessage(
                "Fight duration: @red@"
                    + new SimpleDateFormat("m:ss").format(System.currentTimeMillis() - TIME)
                    + "</col>."));
    getOwner().getProperties().addProperty(this, 1);
    AchievementHandler.activateAchievement(getOwner(), AchievementList.KILL_100_ZULRAHS, 1);
  }

  private int getNextForm() {
    List<Integer> possible = new ArrayList<>();
    possible.addAll(Arrays.asList(2042, 2043, 2044));
    possible.remove(Integer.valueOf(getId()));
    return Utility.randomElement(possible);
  }

  private void move() {
    setCanAssault(false);
    getOwner().setHitZulrah(false);
    getOwner().getCombat().reset();
    TaskQueue.queue(
        new Task(1) {
          @Override
          public void execute() {
            getUpdateFlags().sendAnimation(new Animation(5072));
            getOwner().send(new SendMessage("Zulrah dives into the swamp..."));
            getUpdateFlags().isUpdateRequired();
            stop();
          }

          @Override
          public void onStop() {}
        });
    TaskQueue.queue(
        new Task(3) {
          @Override
          public void execute() {
            transform(getNextForm());
            teleport(Utility.randomElement(LOCATIONS));
            getUpdateFlags().isUpdateRequired();
            getUpdateFlags().sendAnimation(new Animation(5071));
            getUpdateFlags().faceEntity(getOwner().getIndex());
            getUpdateFlags().isUpdateRequired();
            stop();
          }

          @Override
          public void onStop() {
            getCombat().setAssault(getOwner());
            setCanAssault(true);
            getOwner().setHitZulrah(true);
          }
        });
  }

  private void snakes() {
    List<Location> possibleLocations = new ArrayList<>();
    possibleLocations.addAll(
        Arrays.asList(
            new Location(2263, 3075),
            new Location(2263, 3071),
            new Location(2268, 3069),
            new Location(2273, 3071),
            new Location(2273, 3077)));
    TaskQueue.queue(
        new Task(3) {
          int cycles = -1;

          @Override
          public void execute() {
            if (++cycles == 3) {
              stop();
              return;
            }
            Location next = Utility.randomElement(possibleLocations);
            possibleLocations.remove(next);
            getUpdateFlags().sendFaceToDirection(next);
            getUpdateFlags().sendAnimation(new Animation(5068));
            final int offsetX = next.getX() - getX();
            final int offsetY = next.getY() - getY();
            Projectile projectile = new Projectile(1047, 1, 10, 85, 40, 10, 20);
            getOwner()
                .send(
                    new SendProjectile(
                        getOwner(), projectile, getLocation(), -1, (byte) offsetX, (byte) offsetY));
            spawn(next.getX(), next.getY());
            stop();
          }

          @Override
          public void onStop() {}
        });
  }

  private void spawn(int x, int y) {
    TaskQueue.queue(
        new Task(1) {
          @Override
          public void execute() {
            Mob m =
                new Mob(
                    getOwner(), 2045, false, false, false, new Location(x, y, getOwner().getZ()));
            m.getFollowing().setIgnoreDistance(true);
            m.getCombat().setAssault(getOwner());
            SNAKELING.add(m);
            stop();
          }

          @Override
          public void onStop() {}
        });
  }

  @Override
  public void execute(Entity paramEntity1, Entity paramEntity2) {}
}
