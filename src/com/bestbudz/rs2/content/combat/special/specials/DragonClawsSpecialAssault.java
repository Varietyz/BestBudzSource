package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.content.combat.impl.Melee;
import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DragonClawsSpecialAssault implements Special {

  @Override
  public boolean checkRequirements(Stoner stoner) {
    return true;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 50;
  }

  @Override
  public void handleAssault(Stoner stoner) {
    Melee m = stoner.getCombat().getMelee();
    Entity a = stoner.getCombat().getAssaulting();

    m.setAnimation(new Animation(5283));

    m.execute(a);
    int d1 = stoner.getLastDamageDealt();

    if (d1 == 0) {
      m.execute(a);
      int d2 = stoner.getLastDamageDealt();

      m.setAssault(new Assault(2, m.getAssault().getAssaultDelay()), new Animation(5283));
      if (d2 == 0) {
        m.execute(a);
      } else {
        m.setNextDamage(d2 / 2);
        m.execute(a);
      }
    } else {
      m.setNextDamage(d1 / 2);
      m.execute(a);

      m.setAssault(new Assault(2, m.getAssault().getAssaultDelay()), new Animation(5283));

      int n = stoner.getLastDamageDealt();
      m.setNextDamage(n / 2);
      m.execute(a);
      m.setNextDamage(n - stoner.getLastDamageDealt());
    }
  }
}
