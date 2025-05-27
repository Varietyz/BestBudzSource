package com.bestbudz.rs2.entity.mob.impl.wild;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class CrazyArchaeologist extends Mob {

  private final String[] messages = {
    "I'm Bellock - respect me!",
    "Get off my site!",
    "No-one messes with Bellock's dig!",
    "These ruins are quarry!",
    "Taste my knowledge!",
    "You belong in a museum!",
  };
  private final String specialMessage = "Rain of knowledge!";
  private final String deathMessage = "Ow!";
  private boolean usingSpecial = false;

  public CrazyArchaeologist() {
    super(6618, true, new Location(2969, 3708, 0));
  }

  @Override
  public void onDeath() {
    getUpdateFlags().sendForceMessage(deathMessage);
  }

  @Override
  public void onHit(Entity entity, Hit hit) {
    if (entity != null && !entity.isNpc()) {
      int random = Utility.random(10);
      if (random == 1) {
        usingSpecial = true;
        getUpdateFlags().sendForceMessage(specialMessage);
        special(entity.getStoner());
      } else if (!isDead() || !usingSpecial) {
        getUpdateFlags().sendForceMessage(Utility.randomElement(messages));
      }
    }
  }

  public void special(Stoner stoner) {
    for (int i = 0; i < 3; i++) {
      int offsetX = stoner.getX() - getX();
      int offsetY = stoner.getY() - getY();
      if (i == 0 || i == 2) {
        offsetX += i == 0 ? -1 : 1;
        offsetY++;
      }
      Location end = new Location(getX() + offsetX, getY() + offsetY, 0);
      World.sendProjectile(
          new Projectile(551, 1, 10, 100, 65, 10, 20),
          getLocation(),
          -1,
          (byte) offsetX,
          (byte) offsetY);
      World.sendStillGraphic(659, 100, end);
      TaskQueue.queue(
          new Task(stoner, 3, false) {
            @Override
            public void execute() {
              stop();
            }

            @Override
            public void onStop() {
              if (stoner.getLocation().equals(end)) {
                int damage = Utility.random(15) + Utility.random(15) + 12;
                if (damage > 23) {
                  damage = 23;
                }
                stoner.hit(new Hit(damage, HitTypes.MAGE));
                usingSpecial = false;
              }
            }
          });
    }
  }
}
