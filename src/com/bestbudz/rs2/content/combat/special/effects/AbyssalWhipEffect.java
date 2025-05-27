package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class AbyssalWhipEffect implements CombatEffect {

  @Override
  public void execute(Stoner p, Entity e) {
    if (!e.isNpc()) {
      Stoner p2 = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];
      if (p2 == null) {
        return;
      }
      if (p2.getRunEnergy().getEnergy() >= 4) {
        int absorb = (int) (p2.getRunEnergy().getEnergy() * 0.25D);
        p2.getRunEnergy().deduct(absorb);
        p.getRunEnergy().add(absorb);
        p.getClient()
            .queueOutgoingPacket(new SendMessage("You absorb 25% of your opponents energy."));
        p2.getClient()
            .queueOutgoingPacket(new SendMessage("25% of your energy has been absorbed!"));
      }
    }
  }
}
