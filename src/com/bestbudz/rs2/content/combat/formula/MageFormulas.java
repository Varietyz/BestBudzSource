package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.rs2.content.profession.mage.spells.Charge;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MageFormulas {

  public static double getEffectiveMageAccuracy(Entity entity) {
    Stoner assaulter = null;

    if (!entity.isNpc()) {
      assaulter = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
    } else {
      double assaultBonus = 0;
      double baseAssault = 0;

      if (entity.getBonuses() != null) {
        assaultBonus = entity.getBonuses()[3];
      }

      if (entity.getGrades() != null) {
        baseAssault = entity.getGrades()[6];
      }

      return Math.floor(baseAssault + assaultBonus) + 8;
    }

    double assaultBonus = assaulter.getBonuses()[3];
    double baseAssault = assaulter.getProfession().getGrades()[6];

    return Math.floor(baseAssault + assaultBonus) + 15;
  }

  public static double getMageAssaultRoll(Entity entity) {
    double specAccuracy = 1.0;
    double effectiveAccuracy = getEffectiveMageAccuracy(entity);
    int styleBonusAssault = 0;
    effectiveAccuracy *= (1 + (styleBonusAssault) / 64);
    return (int) (effectiveAccuracy * specAccuracy);
  }

  public static double getMageAegisRoll(Entity entity) {
    Stoner blocker = null;
    if (!entity.isNpc()) {
      blocker = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
    } else {
      if (entity.getBonuses() != null && entity.getGrades() != null) {
        double effectiveAegis = getEffectiveMageAegis(entity);
        effectiveAegis += entity.getBonuses()[8];
        long grade = entity.getGrades()[6];
        effectiveAegis = (int) (Math.floor(grade * 1.10) + Math.floor(effectiveAegis * 0.25));
        return effectiveAegis;
      }
      return 0;
    }
    int styleBonusAegis = 0;
    double effectiveAegis = getEffectiveMageAegis(entity);
    effectiveAegis += blocker.getBonuses()[8];
    long grade = blocker.getProfession().getGrades()[6];
    effectiveAegis = (int) (Math.floor(grade * 1.10) + Math.floor(effectiveAegis * 0.25));
    effectiveAegis *= (1 + (styleBonusAegis) / 64);
    return effectiveAegis;
  }

  public static double getEffectiveMageAegis(Entity entity) {
    Stoner blocker = null;
    if (!entity.isNpc()) {
      blocker = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
    } else {
      if (entity.getGrades() != null) {
        return Math.floor(entity.getGrades()[1]) + 8;
      }
      return 0;
    }
    double baseAegis = blocker.getProfession().getGrades()[1];
    return Math.floor(baseAegis) + 10;
  }

  public static int mageMaxHit(Stoner stoner) {

    int spellId = stoner.getMage().getSpellCasting().getCurrentSpellId();

    if (spellId == -1) {
      return 0;
    }
    double damage = stoner.getMage().getSpellCasting().getDefinition(spellId).getBaseMaxHit();
    double damageMultiplier = 1;

    Item helm = stoner.getEquipment().getItems()[0];

    if ((helm != null)
        && (helm.getId() == 15492)
        && (stoner.getCombat().getAssaulting().isNpc())
        && (stoner.getMercenary().hasTask())) {
      Mob m =
          com.bestbudz.rs2.entity.World.getNpcs()[stoner.getCombat().getAssaulting().getIndex()];
      if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
        damageMultiplier += 0.125D;
      }
    }

    if (stoner.getMage().isDFireShieldEffect()) {
      return 23;
    }

    if ((spellId >= 1190) && (spellId <= 1192) && (Charge.isChargeActive(stoner))) {
      damageMultiplier += 0.6D;
    }

    if (stoner.getProfession().getGrades()[6]
            > stoner
                .getProfession()
                .getGradeForExperience(6, stoner.getProfession().getExperience()[6])
        && stoner
                .getProfession()
                .getGradeForExperience(6, stoner.getProfession().getExperience()[6])
            >= 95) {
      damageMultiplier += .03 * (stoner.getProfession().getGrades()[6] - 420);
    }
    if (stoner.getEquipment().getItems()[3] != null) {
      switch (stoner.getEquipment().getItems()[3].getId()) {
        case 20076:
        case 20074:
          damageMultiplier += 0.15;
          break;
        case 20086:
          damageMultiplier += 0.8;
          break;
      }
    }
    if (spellId > 0) {
      switch (spellId) {
        case 12037:
          damage += stoner.getProfession().getGrades()[6] / 10;
          break;
      }
    }
    // Apply advance level scaling
    int lifeAdvances = stoner.getProfessionAdvances()[3];
    if (lifeAdvances > 0) {
      damageMultiplier += lifeAdvances * 0.03;
    }
    int necromanceAdvances = stoner.getProfessionAdvances()[5];
    if (necromanceAdvances > 0) {
      damageMultiplier += necromanceAdvances * 0.01;
    }
    int mageAdvances = stoner.getProfessionAdvances()[6];
    if (mageAdvances > 0) {
      damageMultiplier += mageAdvances * 0.08;
    }
    int weedsmokingAdvances = stoner.getProfessionAdvances()[16];
    if (weedsmokingAdvances > 0) {
      damageMultiplier += weedsmokingAdvances * 0.01;
    }

    int mercenaryAdvances = stoner.getProfessionAdvances()[18];
    if (mercenaryAdvances > 0) {
      damageMultiplier += mercenaryAdvances * 0.02;
    }

    damage *= damageMultiplier;
    return (int) damage;
  }
}
