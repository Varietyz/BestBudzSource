package com.bestbudz.rs2.content.profession.summoning.impl;

import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.content.profession.summoning.FamiliarSpecial;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class UnicornStallion implements FamiliarSpecial {
  @Override
  public boolean execute(Stoner stoner, FamiliarMob mob) {
    int tmp5_4 = 3;
    long[] tmp5_1 = stoner.getGrades();
    tmp5_1[tmp5_4] = ((short) (tmp5_1[tmp5_4] + (short) (int) (stoner.getMaxGrades()[3] * 0.2D)));

    if (stoner.getGrades()[3] > stoner.getMaxGrades()[3]) {
      stoner.getGrades()[3] = stoner.getMaxGrades()[3];
    }

    stoner.getProfession().update(3);

    if (stoner.isPoisoned()) {
      stoner.curePoison(100);
      stoner.getClient().queueOutgoingPacket(new SendMessage("Your poison has been cured."));
    }

    stoner.getUpdateFlags().sendGraphic(new Graphic(1298, 0, false));

    mob.getUpdateFlags().sendGraphic(new Graphic(1356, 0, false));
    mob.getUpdateFlags().sendAnimation(8267, 0);

    return true;
  }

  @Override
  public int getAmount() {
    return 20;
  }

  @Override
  public double getExperience() {
    return 2.0D;
  }

  @Override
  public FamiliarSpecial.SpecialType getSpecialType() {
    return FamiliarSpecial.SpecialType.NONE;
  }
}
