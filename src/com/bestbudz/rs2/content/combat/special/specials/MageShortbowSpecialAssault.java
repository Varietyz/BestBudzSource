package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.impl.Sagittarius;
import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MageShortbowSpecialAssault implements Special {
  public static final int MAGE_SHORTBOW_PROJECTILE_ID = 256;
  public static final int DOUBLE_SHOOT_ANIMATION_ID = 1074;

  @Override
  public boolean checkRequirements(Stoner stoner) {
    return true;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 55;
  }

  @Override
  public void handleAssault(Stoner stoner) {
    Sagittarius r = stoner.getCombat().getSagittarius();

    r.setStart(new Graphic(256, 5, true));
    r.setAnimation(new Animation(1074, 0));
    r.setProjectile(new Projectile(249));
    r.setStartGfxOffset((byte) 1);

    r.getProjectile().setDelay(35);

    r.execute(stoner.getCombat().getAssaulting());

    r.setStartGfxOffset((byte) 0);
    r.setProjectileOffset(0);

    r.setProjectile(new Projectile(249));
  }
}
