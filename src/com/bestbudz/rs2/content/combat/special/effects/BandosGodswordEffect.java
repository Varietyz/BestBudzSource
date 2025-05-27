package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BandosGodswordEffect implements CombatEffect {

  public static final int[] BGS_DRAIN_IDS = {1, 2, 5, 0, 6, 4};

  @Override
  public void execute(Stoner p, Entity e) {
    int id = -1;
    int drain = p.getLastDamageDealt() / 10;

    Stoner p2 = null;

    if (!e.isNpc()) {
      p2 = com.bestbudz.rs2.entity.World.getStoners()[e.getIndex()];
    }

    if (drain <= 0) {
      return;
    }

    for (int i = 0; i < BGS_DRAIN_IDS.length; i++) {
      if (e.getGrades()[BGS_DRAIN_IDS[i]] != 0) {
        id = i;
      }
    }

    if (id == -1) {
      return;
    }

    if (e.getGrades()[BGS_DRAIN_IDS[id]] - drain < 0) {
      long diff = drain - e.getGrades()[BGS_DRAIN_IDS[id]];
      e.getGrades()[BGS_DRAIN_IDS[id]] = 0;
      p.getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "You drain your opponents "
                      + com.bestbudz.rs2.content.profession.Professions.PROFESSION_NAMES[
                          BGS_DRAIN_IDS[id]]
                      + " down to 0."));

      if (p2 != null) {
        p2.getProfession().update(id);
      }

      if (id < BGS_DRAIN_IDS.length - 1) {
        id++;
        int tmp202_201 = BGS_DRAIN_IDS[id];
        long[] tmp202_194 = e.getGrades();
        tmp202_194[tmp202_201] = ((short) (tmp202_194[tmp202_201] - diff));
        p2.getProfession().update(BGS_DRAIN_IDS[id]);
        p.getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You drain some of your opponents "
                        + com.bestbudz.rs2.content.profession.Professions.PROFESSION_NAMES[
                            BGS_DRAIN_IDS[id]]
                        + "."));
      }
    } else {
      int tmp277_276 = BGS_DRAIN_IDS[id];
      long[] tmp277_269 = e.getGrades();
      tmp277_269[tmp277_276] = ((short) (tmp277_269[tmp277_276] - drain));
      if (p2 != null) {
        p2.getProfession().update(BGS_DRAIN_IDS[id]);
      }
      if (e.getGrades()[BGS_DRAIN_IDS[id]] == 0)
        p.getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You drain your opponents "
                        + com.bestbudz.rs2.content.profession.Professions.PROFESSION_NAMES[
                            BGS_DRAIN_IDS[id]]
                        + " down to 0."));
      else
        p.getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You drain some of your opponents "
                        + com.bestbudz.rs2.content.profession.Professions.PROFESSION_NAMES[
                            BGS_DRAIN_IDS[id]]
                        + "."));
    }
  }
}
