package com.bestbudz.rs2.content.profession.summoning.impl;

import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.content.profession.summoning.FamiliarSpecial;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SpiritWolf implements FamiliarSpecial {
  @Override
  public boolean execute(Stoner stoner, FamiliarMob mob) {
    Entity a = mob.getOwner().getCombat().getAssaulting();

    if (a != null) {
      Projectile p = new Projectile(1333);

      p.setCurve(0);
      p.setStartHeight(0);
      p.setEndHeight(0);

      World.sendProjectile(p, mob, a);

      mob.face(a);

      if (!a.isNpc()) {
        stoner
            .getClient()
            .queueOutgoingPacket(new SendMessage("This special does not effect stoners!"));
      } else {
        Mob m = World.getNpcs()[a.getIndex()];

        if (m != null) {
          if (m.getDefinition().getGrade() > 100) {
            stoner
                .getClient()
                .queueOutgoingPacket(
                    new SendMessage("The mob was too strong and resisted the special move!"));
            return true;
          }

          stoner.getCombat().reset();
          m.retreat();
        }
      }
    } else {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You are not fighting anything."));
      return false;
    }

    return true;
  }

  @Override
  public int getAmount() {
    return 3;
  }

  @Override
  public double getExperience() {
    return 0.1D;
  }

  @Override
  public FamiliarSpecial.SpecialType getSpecialType() {
    return FamiliarSpecial.SpecialType.NONE;
  }
}
