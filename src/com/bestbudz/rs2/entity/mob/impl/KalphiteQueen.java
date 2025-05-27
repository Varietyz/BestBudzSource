package com.bestbudz.rs2.entity.mob.impl;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class KalphiteQueen extends Mob {

  private Entity lastKilledBy = null;

  public KalphiteQueen() {
    super(1158, true, new Location(3480, 9495));
  }

  @Override
  public long getAffectedDamage(Hit hit) {
    switch (hit.getType()) {
      case SAGITTARIUS:
      case MAGE:
        if (getId() == 1158) {
          return 0;
        }
        break;
      case MELEE:
        if (getId() == 1160) {
          if ((hit.getAssaulter() != null) && (!hit.getAssaulter().isNpc())) {
            Stoner stoner = World.getStoners()[hit.getAssaulter().getIndex()];

            if ((stoner != null) && (stoner.getMelee().isVeracEffectActive())) {
              return hit.getDamage();
            }
          }

          return 0;
        }
        break;
      default:
        return hit.getDamage();
    }

    return hit.getDamage();
  }

  @Override
  public void onDeath() {
    lastKilledBy = getCombat().getLastAssaultedBy();
  }

  public void transform() {
    transform(getId() == 1160 ? 1158 : 1160);

    if (lastKilledBy != null) {
      getCombat().setAssault(lastKilledBy);
    }
  }
}
