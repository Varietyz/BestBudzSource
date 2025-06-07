package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonScimitarEffect implements CombatEffect {


  @Override
  public void execute(Stoner stoner, Entity opponent) {

    if (opponent.isNpc()) {
      return;
    }

    if (stoner.getLastDamageDealt() > 0) {
      if (opponent.getStoner() == null) {
        return;
      }

      stoner.send(
          new SendMessage(
              "You have cancelled "
                  + opponent.getStoner().getUsername()
                  + "'s protection necromance."));
      opponent
          .getStoner()
          .send(
              new SendMessage(
                  "Your protection necromance's been cancelled by " + stoner.getUsername()));
    }
  }
}
