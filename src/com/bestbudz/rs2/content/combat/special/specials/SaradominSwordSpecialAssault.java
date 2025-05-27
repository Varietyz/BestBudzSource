package com.bestbudz.rs2.content.combat.special.specials;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.content.combat.special.Special;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class SaradominSwordSpecialAssault implements Special {

  @Override
  public boolean checkRequirements(Stoner paramStoner) {
    return true;
  }

  @Override
  public int getSpecialAmountRequired() {
    return 100;
  }

  @Override
  public void handleAssault(Stoner stoner) {
    stoner.getCombat().getMage().setNextHit(Utility.randomNumber(20));

    stoner
        .getCombat()
        .getMage()
        .setAssault(
            new Assault(1, stoner.getCombat().getAssaultCooldown()),
            null,
            new Graphic(1224, 0, true),
            new Graphic(1207, 0, true),
            null);
    stoner.getCombat().getMage().execute(stoner.getCombat().getAssaulting());

    if (stoner.getEquipment().getItems()[3].getId() == 11838) {
      stoner.getCombat().getMelee().setAnimation(new Animation(1132, 0));
    } else if (stoner.getEquipment().getItems()[3].getId() == 12809) {
      stoner.getCombat().getMelee().setAnimation(new Animation(1133, 0));
    }

    stoner.getCombat().getMelee().setDamageBoost(1.4);
  }
}
