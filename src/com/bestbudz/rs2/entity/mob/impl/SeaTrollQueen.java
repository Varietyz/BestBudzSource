package com.bestbudz.rs2.entity.mob.impl;

import com.bestbudz.core.definitions.NpcCombatDefinition.Mage;
import com.bestbudz.core.definitions.NpcCombatDefinition.Melee;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
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
  public int getRespawnTime() {
    return 60;
  }

  @Override
  public void updateCombatType() {
    CombatTypes type = CombatTypes.MELEE;
    if (getCombat().getAssaulting() != null) {
      if (!getCombat().getAssaulting().isNpc()) {
        Stoner stoner = (Stoner) getCombat().getAssaulting();
        if (!getCombat().withinDistanceForAssault(CombatTypes.MELEE, true)) {
          if (stoner.getNecromance().active(Necromance.PROTECT_FROM_MAGE)) {
            if (stoner.getProfession().getGrades()[Professions.NECROMANCE] > 0) {
              World.sendProjectile(getProjectile(), stoner, this);
              long modifier =
                  stoner.getProfession().getGrades()[Professions.NECROMANCE] - 20 > 0
                      ? 20
                      : stoner.getProfession().getGrades()[Professions.NECROMANCE];
              stoner.getNecromance().drain(modifier);
              type = CombatTypes.SAGITTARIUS;
              getCombat().setAssaultTimer(6);
            } else {
              type = CombatTypes.MAGE;
            }
          } else {
            type = CombatTypes.MAGE;
          }
        } else {
          if (stoner.getNecromance().active(Necromance.PROTECT_FROM_MAGE)) {
            type = CombatTypes.MELEE;
          } else if (stoner.getNecromance().active(Necromance.PROTECT_FROM_MELEE)) {
            type = CombatTypes.MAGE;
          } else {
            type = Utility.randomNumber(10) < 5 ? CombatTypes.MELEE : CombatTypes.MAGE;
          }
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
}
