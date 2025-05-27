package com.bestbudz.rs2.content.profession.sagittarius;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BoltSpecials {

  public static void checkForBoltSpecial(Stoner stoner, Entity assaulting, Hit hit) {
    Item arrow = stoner.getEquipment().getItems()[13];
    Item weapon = stoner.getEquipment().getItems()[3];

    if ((arrow == null)
        || (weapon == null)
        || (stoner.getCombat().getCombatType() != CombatTypes.SAGITTARIUS)
        || (!SagittariusProfession.requiresArrow(stoner, weapon.getId()))) {
      return;
    }

    int decrease =
        (assaulting instanceof Stoner
                && assaulting.getStoner() != null
                && assaulting.getStoner().getEquipment().contains(9944)
            ? 5
            : 0);
    int random = Utility.random(230);

    if (random > 30 - decrease) {
      return;
    }

    switch (arrow.getId()) {
      case 9244:
        if (!assaulting.isNpc()) {
          Stoner p = com.bestbudz.rs2.entity.World.getStoners()[assaulting.getIndex()];

          if (p == null) {
            return;
          }

          Item shield = p.getEquipment().getItems()[5];

          if ((shield != null) && ((shield.getId() == 1540) || (shield.getId() == 11283))) {
            return;
          }
        }

        assaulting.getUpdateFlags().sendGraphic(Graphic.lowGraphic(756, 0));
        break;
      case 9245:
        stoner.getSagittarius().setOnyxEffectActive(true);
        assaulting.getUpdateFlags().sendGraphic(Graphic.lowGraphic(753, 0));
        break;
      case 9241:
        assaulting.getUpdateFlags().sendGraphic(Graphic.lowGraphic(752, 0));
        if (Utility.randomNumber(3) == 0) {
          assaulting.poison(5);
        }
        break;
      case 9243:
        assaulting.getUpdateFlags().sendGraphic(Graphic.lowGraphic(758, 0));
        break;

      case 9242:
        assaulting.getUpdateFlags().sendGraphic(Graphic.lowGraphic(754, 0));
        stoner.getSagittarius().setBloodForfeitEffectActive(true);
        int self_inflict_damage =
            (int) (stoner.getProfession().getGrades()[Professions.LIFE] * 0.1);

        int max_hit =
            (int)
                (assaulting.isNpc()
                    ? assaulting.getGrades()[Professions.LIFE] * 0.2
                    : assaulting.getStoner().getProfession().getGrades()[Professions.LIFE] * 0.2);
        if (max_hit > 200) {
          max_hit = 200;
        }

        assaulting.checkForDeath();
        stoner.checkForDeath();

        assaulting.hit(new Hit(max_hit));
        stoner.hit(new Hit(self_inflict_damage));

        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage("You drain 10% of your life and 20% of your opponent's life."));
        stoner.getSagittarius().setBloodForfeitEffectActive(false);
        break;
    }
  }
}
