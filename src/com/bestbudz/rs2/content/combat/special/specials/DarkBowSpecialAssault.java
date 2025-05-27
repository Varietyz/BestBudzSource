package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.impl.Sagittarius;
import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DarkBowSpecialAssault implements Special {

  @Override
  public boolean checkRequirements(Stoner stoner) {
    return true;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 60;
  }

  @Override
  public void handleAssault(Stoner stoner) {
    Sagittarius r = stoner.getCombat().getSagittarius();
    Item ammo = stoner.getEquipment().getItems()[13];

    if (ammo != null) {
      if ((ammo.getId() == 11212)
          || (ammo.getId() == 11227)
          || (ammo.getId() == 0)
          || (ammo.getId() == 11228)) {
        r.setProjectile(new Projectile(1099));
        r.setEnd(new Graphic(1100, 0, true));
      } else {
        r.setProjectile(new Projectile(1101));
        r.setEnd(new Graphic(1103, 0, true));
      }
    }

    r.setProjectileOffset(1);
  }
}
