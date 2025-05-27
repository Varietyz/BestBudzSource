package com.bestbudz.rs2.content.profession.mage.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class IceBlitzEffect implements CombatEffect {
  @Override
  public void execute(Stoner p, Entity e) {
    if (!e.isNpc() && !e.isFrozen()) {
      Stoner p2 = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];
      if (p2 == null) {
        return;
      }
      p2.getClient().queueOutgoingPacket(new SendMessage("You have been frozen."));
    }
    e.freeze(15, 5);
  }
}
