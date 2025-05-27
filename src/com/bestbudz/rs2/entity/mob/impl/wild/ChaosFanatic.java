package com.bestbudz.rs2.entity.mob.impl.wild;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ChaosFanatic extends Mob {

  private final String[] messages = {
    "Burn!",
    "WEUGH!",
    "Develish Oxen Roll!",
    "All your wilderness are belong to them!",
    "AhehHeheuhHhahueHuUEehEahAH",
    "I shall call him squidgy and he shall be my squidgy!",
  };

  public ChaosFanatic() {
    super(6619, true, new Location(2980, 3846, 0));
  }

  @Override
  public void onHit(Entity entity, Hit hit) {
    if (entity != null && !entity.isNpc()) {
      getUpdateFlags().sendForceMessage(Utility.randomElement(messages));
      int random = Utility.random(10);
      if (random == 1) {
        castOrbs(entity.getStoner());
      }
    }
  }

  private void castOrbs(Stoner stoner) {
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
                stoner.hit(new Hit(Utility.random(15) + Utility.random(15) + 10, HitTypes.MAGE));
                if (stoner.getEquipment().getItems()[3] != null) {
                  if (stoner.getBox().getFreeSlots() == 0) {
                    int id = stoner.getBox().getSlotId(0);
                    stoner.getGroundItems().dropFull(id, 0);
                  }
                  stoner.getEquipment().unequip(3);
                  stoner.send(
                      new SendMessage(
                          "The Chaos Fanatic has removed some of your worn equipment."));
                  stoner.getUpdateFlags().sendGraphic(new Graphic(557));
                }
              }
            }
          });
    }
  }
}
