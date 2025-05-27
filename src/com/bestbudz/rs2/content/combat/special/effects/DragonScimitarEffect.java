package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonScimitarEffect implements CombatEffect {

  public final Necromance PROTECT_FROM_MAGE = Necromance.PROTECT_FROM_MAGE;
  public final Necromance PROTECT_FROM_SAGITTARIUS = Necromance.PROTECT_FROM_RANGE;
  public final Necromance PROTECT_FROM_MELEE = Necromance.PROTECT_FROM_MELEE;

  @Override
  public void execute(Stoner stoner, Entity opponent) {

    if (opponent.isNpc()) {
      return;
    }

    if (stoner.getLastDamageDealt() > 0) {
      if (opponent.getStoner() == null) {
        return;
      }

      if (opponent.getStoner().getNecromance().active(PROTECT_FROM_MAGE)) {
        opponent.getStoner().getNecromance().disable(PROTECT_FROM_MAGE);
      } else if (opponent.getStoner().getNecromance().active(PROTECT_FROM_SAGITTARIUS)) {
        opponent.getStoner().getNecromance().disable(PROTECT_FROM_SAGITTARIUS);
      } else if (opponent.getStoner().getNecromance().active(PROTECT_FROM_MELEE)) {
        opponent.getStoner().getNecromance().disable(PROTECT_FROM_MELEE);
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
