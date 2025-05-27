package com.bestbudz.rs2.content.profession.melee;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.GraphicTask;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BarrowsSpecials {

  public static final int BARROWS_SPECIAL_CHANCE = 42;
  public static final Graphic GUTHAN_SPECIAL_GRAPHIC = Graphic.highGraphic(398, 0);
  public static final Graphic TORAG_SPECIAL_GRAPHIC = Graphic.highGraphic(399, 0);
  public static final Graphic KARIL_SPECIAL_GRAPHIC = Graphic.highGraphic(401, 0);
  public static final Graphic AHRIM_SPECIAL_GRAPHIC = Graphic.highGraphic(400, 0);

  public static void checkForBarrowsSpecial(Stoner stoner) {
    if (Utility.randomNumber(100) > 47) {
      return;
    }

    if (stoner.getSpecialAssault().isInitialized()) {
      return;
    }

    if (ItemCheck.wearingFullBarrows(stoner, "Dharok")) stoner.getSpecialAssault().toggleSpecial();
    else if (ItemCheck.wearingFullBarrows(stoner, "Guthan"))
      stoner.getMelee().setGuthanEffectActive(true);
    else if (ItemCheck.wearingFullBarrows(stoner, "Torag"))
      stoner.getMelee().setToragEffectActive(true);
    else if ((stoner.getCombat().getCombatType() == CombatTypes.MAGE)
        && (ItemCheck.wearingFullBarrows(stoner, "Ahrim")))
      stoner.getMage().setAhrimEffectActive(true);
    else if (ItemCheck.wearingFullBarrows(stoner, "Verac"))
      stoner.getMelee().setVeracEffectActive(true);
    else if (ItemCheck.wearingFullBarrows(stoner, "Karil"))
      stoner.getSagittarius().setKarilEffectActive(true);
  }

  public static void doAhrimEffect(Stoner stoner, Entity assault, long damage) {
    TaskQueue.queue(new GraphicTask(4, false, AHRIM_SPECIAL_GRAPHIC, assault));
    stoner.getMage().setAhrimEffectActive(false);

    if ((damage > 0) && (Utility.randomNumber(4) == 0)) {
      long newLvl = assault.getGrades()[2] - 5;

      assault.getGrades()[2] = ((newLvl > 0 ? newLvl : 0));

      if (!assault.isNpc()) {
        Stoner p2 = com.bestbudz.rs2.entity.World.getStoners()[assault.getIndex()];

        if (p2 == null) {
          return;
        }

        p2.getProfession().update(2);
      }

      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You drain 5 grades from your opponent's Vigour grade."));
    }
  }

  public static void doGuthanEffect(Stoner stoner, Entity assault, Hit hit) {
    long newLvl = stoner.getGrades()[3] + hit.getDamage();
    long maxLvl = stoner.getMaxGrades()[3];

    stoner.getGrades()[3] = ((newLvl > maxLvl ? maxLvl : newLvl));
    stoner.getProfession().update(3);

    assault.getUpdateFlags().sendGraphic(GUTHAN_SPECIAL_GRAPHIC);
    stoner.getMelee().setGuthanEffectActive(false);
    stoner
        .getClient()
        .queueOutgoingPacket(new SendMessage("You absorb some of your opponent's life."));
  }

  public static void doKarilEffect(Stoner stoner, Entity assault) {
    assault.getUpdateFlags().sendGraphic(KARIL_SPECIAL_GRAPHIC);
    stoner.getSagittarius().setKarilEffectActive(false);
    long newLvl = assault.getGrades()[6] - 5;

    assault.getGrades()[6] = ((newLvl > 0 ? newLvl : 0));

    if (!assault.isNpc()) {
      Stoner p2 = com.bestbudz.rs2.entity.World.getStoners()[assault.getIndex()];

      if (p2 == null) {
        return;
      }

      p2.getProfession().update(6);
    }

    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendMessage("You drain 5 grades from your opponent's Mage grade."));
  }

  public static void doToragEffect(Stoner stoner, Entity assault) {
    assault.getUpdateFlags().sendGraphic(TORAG_SPECIAL_GRAPHIC);
    stoner.getMelee().setToragEffectActive(false);

    if (!assault.isNpc()) {
      Stoner p = com.bestbudz.rs2.entity.World.getStoners()[assault.getIndex()];

      if (p == null) {
        return;
      }

      p.getRunEnergy().deduct(0.2D);
      p.getRunEnergy().update();
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You have drained 20% of your opponent's energy."));
    }
  }
}
