package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.core.definitions.SagittariusWeaponDefinition;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class RangeFormulas {

  public static final double[][] SAGITTARIUS_NECROMANCE_MODIFIERS = {
    {3.0D, 0.05D}, {11.0D, 0.1D}, {19.0D, 0.15D}
  };

  public static long calculateRangeAegis(Entity defending) {
    Stoner defender = null;

    if (!defending.isNpc()) {
      defender = World.getStoners()[defending.getIndex()];
    } else {
      if (defending.getBonuses() != null && defending.getGrades() != null) {
        return defending.getGrades()[1]
            + defending.getBonuses()[9]
            + (defending.getBonuses()[9] / 2);
      }
      return 0;
    }

    long aegisGrade = defender.getProfession().getGrades()[1];

    return aegisGrade + defender.getBonuses()[9] + (defender.getBonuses()[9] / 2);
  }

  public static int calculateRangeAssault(Entity entity) {

    Stoner assaulter = null;

    if (!entity.isNpc()) {
      assaulter = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];

      if ((assaulter != null)
          && (ItemCheck.hasDFireShield(assaulter))
          && (assaulter.getMage().isDFireShieldEffect())) {
        return 100;
      }

    } else {
      if (entity.getGrades() != null) {
        long rangeGrade = entity.getGrades()[4];
        return (int) (rangeGrade + (entity.getBonuses()[4] * 1.25));
      }
      return 0;
    }

    double rangeGrade = assaulter.getProfession().getGrades()[4];
    if (assaulter.getSpecialAssault().isInitialized()) {
      rangeGrade *= getSagittariusSpecialAccuracyModifier(assaulter);
    }
    if (ItemCheck.wearingFullVoidSagittarius(assaulter)) {
      rangeGrade += assaulter.getMaxGrades()[4] * 5.5;
    }

    if (assaulter.getSpecialAssault().isInitialized()) {
      if (ItemCheck.wearingFullVoidSagittarius(assaulter)) {
        rangeGrade *= 5.50;
      }
    }
    return (int) (rangeGrade + (assaulter.getBonuses()[4] * 3.50));
  }

  public static int getEffectiveSagittariusVigour(Stoner stoner) {
    Item weapon = stoner.getEquipment().getItems()[3];

    if ((weapon == null) || (weapon.getSagittariusDefinition() == null)) {
      return 0;
    }

    if (weapon.getId() == 12926) {
      if (stoner.getToxicBlowpipe().getBlowpipeAmmo() == null) {
        return 0;
      }
      return stoner.getToxicBlowpipe().getBlowpipeAmmo().getSagittariusVigourBonus() + 40;
    }

    int rStr = stoner.getBonuses()[12];

    if ((weapon.getSagittariusDefinition().getType()
            == SagittariusWeaponDefinition.SagittariusTypes.THROWN)
        || (weapon.getSagittariusDefinition().getArrows() == null)
        || (weapon.getSagittariusDefinition().getArrows().length == 0)) {
      rStr = weapon.getSagittariusVigourBonus();
    } else {
      Item ammo = stoner.getEquipment().getItems()[13];
      if (ammo != null) {
        rStr = ammo.getSagittariusVigourBonus();
      }
    }

    return rStr;
  }

  public static int getSagittariusMaxHit(Stoner stoner) {
    double pBonus = 1.0D;
    int vBonus = 0;
    int sBonus = 0;

    if (ItemCheck.wearingFullVoidSagittarius(stoner)) {
      vBonus *= 80.0;
    }

    switch (stoner.getEquipment().getAssaultStyle()) {
      case ACCURATE:
        sBonus = 3;
        break;
      case AGGRESSIVE:
        sBonus = 2;
        break;
      case DEFENSIVE:
        sBonus = 1;
        break;
      default:
        break;
    }



    long str = stoner.getProfession().getGrades()[4];
    double eS = (int) (str * pBonus + sBonus + vBonus);
    int rngStr = getEffectiveSagittariusVigour(stoner);
    double base = 5.0D + (eS + 8.0D) * (rngStr + 64) / 64.0D;

    if (stoner.getSpecialAssault().isInitialized()) {
      base = (int) (base * getSagittariusSpecialModifier(stoner));
    }

    Item helm = stoner.getEquipment().getItems()[0];

    if ((helm != null)
        && (helm.getId() == 15492)
        && (stoner.getCombat().getAssaulting().isNpc())
        && (stoner.getMercenary().hasTask())) {
      Mob m =
          com.bestbudz.rs2.entity.World.getNpcs()[stoner.getCombat().getAssaulting().getIndex()];
      if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
        base += base * 0.125D;
      }
    }
    // Apply advance level scaling
    int lifeAdv = stoner.getProfessionAdvances()[3];
    if (lifeAdv > 0) {
      base += base * (lifeAdv * 0.03);
    }

    int necroAdv = stoner.getProfessionAdvances()[5];
    if (necroAdv > 0) {
      base += base * (necroAdv * 0.01);
    }

    int sagitAdv = stoner.getProfessionAdvances()[4];
    if (sagitAdv > 0) {
      base += base * (sagitAdv * 0.08);
    }

    int weedAdv = stoner.getProfessionAdvances()[16];
    if (weedAdv > 0) {
      base += base * (weedAdv * 0.01);
    }

    int mercAdv = stoner.getProfessionAdvances()[18];
    if (mercAdv > 0) {
      base += base * (mercAdv * 0.02);
    }

    return (int) base / 11;
  }

  public static double getSagittariusSpecialAccuracyModifier(Stoner stoner) {
    Item weapon = stoner.getEquipment().getItems()[3];
    Item arrow = stoner.getEquipment().getItems()[13];

    if (weapon == null) {
      return 1.0D;
    }

    switch (weapon.getId()) {
      case 11785:
        return 2D;
      case 12926:
        return 1.4D;
      case 11235:
        if (arrow.getId() == 11212) {
          return 1.5D;
        }
        return 1.3D;
      case 13883:
      case 15241:
        return 1.2D;
      case 9185:
        if (arrow.getId() == 9243) return 1.15D;
        if (arrow.getId() == 9244) return 1.65D;
        if (arrow.getId() == 9245) {
          return 1.15D;
        }
        return 1.0D;
    }

    return 1.0D;
  }

  public static double getSagittariusSpecialModifier(Stoner stoner) {
    Item weapon = stoner.getEquipment().getItems()[3];
    Item arrow = stoner.getEquipment().getItems()[13];

    if (weapon == null) {
      return 1.0D;
    }

    switch (weapon.getId()) {
      case 12926:
        return 1.5D;
      case 11235:
        if (arrow.getId() == 11212) {
          return 1.5D;
        }
        return 1.3D;
      case 13883:
      case 15241:
        return 1.2D;
      case 11785:
      case 9185:
        if (arrow.getId() == 9243) return 1.15D;
        if (arrow.getId() == 9244) return 1.45D;
        if (arrow.getId() == 9245) {
          return 1.15D;
        }
        return 1.0D;
    }

    return 1.0D;
  }
}
