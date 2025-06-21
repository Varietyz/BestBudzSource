package com.bestbudz.rs2.entity.mob.bosses.wild;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import java.util.ArrayList;
import java.util.List;

public class Scorpia extends Mob {

  private final List<Mob> guardians = new ArrayList<>();
  private boolean spawnGuardians = false;

  public Scorpia() {
    super(6615, true, new Location(3232, 10341, 0));
  }

  @Override
  public void hit(Hit hit) {

    if (isDead()) {
      return;
    }

    super.hit(hit);

    if (getGrades()[3] < 100 && !isSpawnGuardians()) {
      spawnGuardians();
    }
  }

  @Override
  public void onAssault(Entity assault, long hit, CombatTypes type, boolean success) {
    super.onAssault(assault, hit, type, success);
    if (success) {
      if (Utility.random(10) == 0) {
        assault.poison(20);
      }
    }
  }

  @Override
  public void doAliveMobProcessing() {
    if (isSpawnGuardians()) {
      boolean clear = false;
      if (!guardians.isEmpty()) {
        for (Mob guardian : guardians) {
          if (!guardian.isDead()) {
            if (getGrades()[3] >= 200) {
              clear = true;
              guardian.remove();
              continue;
            }

            if (Utility.getExactDistance(guardian.getLocation(), getLocation()) <= 5) {
              if (Utility.randomNumber(10) == 1) {
                getGrades()[3] += 1;
              }
            } else {
              clear = true;
              guardian.remove();
            }
          }
        }

        if (clear) {
          guardians.clear();
        }
      }
    }
  }

  @Override
  public int getRespawnTime() {
    return 60;
  }

  @Override
  public void onDeath() {
    setSpawnGuardians(false);
    for (Mob guardians : guardians) {
      if (!guardians.isDead()) {
        guardians.remove();
      }
    }
    guardians.clear();
  }

  private void spawnGuardians() {
    setSpawnGuardians(true);
    for (int index = 0; index < 2; index++) {
      Mob mob = new Mob(6617, true, new Location(getX() + index, getY(), getZ()));
      mob.getFollowing().setFollow(this);
      mob.getUpdateFlags().faceEntity(getIndex());
      guardians.add(mob);
    }
  }

  public boolean isSpawnGuardians() {
    return spawnGuardians;
  }

  public void setSpawnGuardians(boolean spawnGuardians) {
    this.spawnGuardians = spawnGuardians;
  }
}
