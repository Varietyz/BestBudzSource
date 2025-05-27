package com.bestbudz.rs2.entity.mob.impl.wild;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ChaosElemental extends Mob {

  public ChaosElemental() {
    super(2054, true, new Location(3277, 3921, 0));
  }

  @Override
  public void hit(Hit hit) {

    if (isDead()) {
      return;
    }

    super.hit(hit);

    int random = Utility.random(20);

    if (random == 5) {
      teleportSpecial();
    } else if (random == 10) {
      equipmentSpecial();
    }
  }

  @Override
  public int getRespawnTime() {
    return 60;
  }

  private Projectile getProjectile(int id) {
    return new Projectile(id, 1, 40, 70, 43, 31, 16);
  }

  private boolean assaulting() {
    if (getCombat().getAssaulting() != null) {
      return !getCombat().getAssaulting().isNpc();
    }
    return false;
  }

  private void teleportSpecial() {
    if (assaulting()) {
      Stoner stoner = (Stoner) getCombat().getAssaulting();
      World.sendProjectile(getProjectile(553), stoner, this);
      stoner.teleport(
          new Location(stoner.getX() - Utility.random(3), stoner.getY() - Utility.random(3), 0));
      stoner.send(new SendMessage("The Chaos Elemental has teleported you away from it."));
      stoner.getUpdateFlags().sendGraphic(new Graphic(554));
    }
  }

  private void equipmentSpecial() {
    if (assaulting()) {
      Stoner stoner = (Stoner) getCombat().getAssaulting();
      World.sendProjectile(getProjectile(556), stoner, this);
      if (stoner.getEquipment().getItems()[3] != null) {
        if (stoner.getBox().getFreeSlots() == 0) {
          int id = stoner.getBox().getSlotId(0);
          stoner.getGroundItems().dropFull(id, 0);
        }
        stoner.getEquipment().unequip(3);
        stoner.send(
            new SendMessage("The Chaos Elemental has removed some of your worn equipment."));
        stoner.getUpdateFlags().sendGraphic(new Graphic(557));
      }
    }
  }
}
