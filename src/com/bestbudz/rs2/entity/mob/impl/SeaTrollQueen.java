package com.bestbudz.rs2.entity.mob.impl;

import com.bestbudz.core.definitions.NpcCombatDefinition.Mage;
import com.bestbudz.core.definitions.NpcCombatDefinition.Melee;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class SeaTrollQueen extends Mob {

  public SeaTrollQueen() {
    super(4315, false, new Location(2342, 3702));
  }

  private static Projectile getProjectile() {
    return new Projectile(109, 1, 40, 70, 43, 31, 16);
  }

  @Override
  public void updateCombatType() {
    CombatTypes type = CombatTypes.MELEE;
    if (getCombat().getAssaulting() != null) {
      if (!getCombat().getAssaulting().isNpc()) {
        Stoner stoner = (Stoner) getCombat().getAssaulting();
        if (!getCombat().withinDistanceForAssault(CombatTypes.MELEE, true)) {

            type = CombatTypes.MAGE;

        } else {

            type = Utility.randomNumber(10) < 5 ? CombatTypes.MELEE : CombatTypes.MAGE;

        }
      }

      getCombat().setCombatType(type);
      getCombat().setBlockAnimation(getCombatDefinition().getBlock());
      switch (getCombat().getCombatType()) {
        case MAGE:
          byte combatIndex = (byte) Utility.randomNumber(getCombatDefinition().getMage().length);
          Mage mage = getCombatDefinition().getMage()[combatIndex];
          getCombat()
              .getMage()
              .setAssault(
                  mage.getAssault(),
                  mage.getAnimation(),
                  mage.getStart(),
                  mage.getEnd(),
                  mage.getProjectile());
          break;
        case MELEE:
          combatIndex = (byte) Utility.randomNumber(getCombatDefinition().getMelee().length);
          Melee melee = getCombatDefinition().getMelee()[combatIndex];
          getCombat().getMelee().setAssault(melee.getAssault(), melee.getAnimation());
          break;
        default:
          break;
      }
    }
  }

  @Override
  public int getRespawnTime() {
    return 60;
  }
}
