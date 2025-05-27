package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class RingOfRecoil implements CombatEffect {

  public static final String RECOIL_STAGE_KEY = "recoilhits";
  public static final String RECOIL_DAMAGE_KEY = "rordamage";

  public static void doRecoil(Stoner p, Entity e, long damage) {
    Item ring = p.getEquipment().getItems()[12];

    if (ring != null && ring.getId() == 2550) {
      long mageLevel = p.getProfession().getGrades()[6];
      int mageAdvances = p.getProfessionAdvances()[6];

      double percent = 0.10 + (mageLevel / 1000.0) + (mageAdvances * 0.01); // scale to ~25% max
      if (percent > 0.35) percent = 0.35;

      int reflected = (int) Math.ceil(damage * percent);
      if (reflected > 0) {
        p.getAttributes().set(RECOIL_DAMAGE_KEY, reflected);
        new RingOfRecoil().execute(p, e);
        onRecoil(p, reflected, p);
      }
    }
  }

  public static void onRecoil(Stoner p, int dmg, Stoner stoner) {
    int baseCap = 40;

    int aegis = (int) stoner.getProfession().getGrades()[1];
    int aegisAdv = stoner.getProfessionAdvances()[1];

    int maxDurability = baseCap + (aegis / 5) + (aegisAdv * 5); // scale to ~150 durability max

    int current =
        p.getAttributes().get(RECOIL_STAGE_KEY) == null
            ? 0
            : p.getAttributes().getInt(RECOIL_STAGE_KEY);

    current += dmg;

    if (current >= maxDurability) {
      p.getAttributes().remove(RECOIL_STAGE_KEY);

      p.getEquipment().getItems()[12] = null;
      p.getEquipment().update(12);

      p.getClient()
          .queueOutgoingPacket(
              new SendMessage("@blu@Your enhanced Ring of Recoil crumbles to dust."));
    } else {
      p.getAttributes().set(RECOIL_STAGE_KEY, current);
    }
  }

  @Override
  public void execute(Stoner p, Entity e) {
    if (e.isDead()) {
      return;
    }
    e.hit(new Hit(p.getAttributes().getInt("rordamage")));
    e.getAttributes().remove("rordamage");
  }
}
