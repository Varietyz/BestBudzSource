package com.bestbudz.rs2.content.profession.mercenary;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class MercenaryMonsters {

  private static final Map<Integer, Byte> mercenaryRequired = new HashMap<Integer, Byte>();

  public static boolean canAssaultMob(Stoner stoner, Mob mob) {
    int req = getRequiredGrade(mob.getId());

    if ((req != 0) && (stoner.getMaxGrades()[18] < req)) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You need a Mercenary grade of " + req + " to assault this mob."));
      return false;
    }

    return true;
  }

  public static final void declare() {
    for (int i = 0; i < 18000; i++) {
      NpcDefinition def = Mob.getDefinition(i);

      int lvl = 0;

      if ((def != null) && (def.getName() != null)) {
        String check = def.getName().toLowerCase();
        lvl = getGradeForName(check);
      }

      if (lvl > 0) mercenaryRequired.put(Integer.valueOf(i), Byte.valueOf((byte) lvl));
    }
  }

  public static byte getGradeForName(String check) {
    byte lvl = 0;

    switch (check) {
      case "crawling hand":
        lvl = (byte) 5;
        break;
      case "cave bug":
        lvl = (byte) 7;
        break;
      case "cave crawler":
        lvl = (byte) 10;
        break;
      case "banshee":
        lvl = (byte) 15;
        break;
      case "cave slime":
        lvl = (byte) 17;
        break;
      case "rockslug":
        lvl = (byte) 20;
        break;
      case "desert lizard":
        lvl = (byte) 22;
        break;
      case "cockatrice":
        lvl = (byte) 25;
        break;
      case "pyrefiend":
        lvl = (byte) 30;
        break;
      case "mogre":
        lvl = (byte) 32;
        break;
      case "infernal mage":
        lvl = (byte) 45;
        break;
      case "bloodveld":
        lvl = (byte) 50;
        break;
      case "jelly":
        lvl = (byte) 62;
        break;
      case "cave horror":
        lvl = (byte) 58;
        break;
      case "aberrant spectre":
        lvl = (byte) 60;
        break;
      case "dust devil":
        lvl = (byte) 65;
        break;
      case "spiritual sagittarius":
        lvl = (byte) 63;
        break;
      case "spiritual warrior":
        lvl = (byte) 68;
        break;
      case "kurask":
        lvl = (byte) 70;
        break;
      case "gargoyle":
        lvl = (byte) 75;
        break;
      case "aquanite":
        lvl = (byte) 78;
        break;
      case "nechryael":
        lvl = (byte) 80;
        break;
      case "spiritual mage":
        lvl = (byte) 83;
        break;
      case "abyssal demon":
        lvl = (byte) 85;
        break;
      case "dark beast":
        lvl = (byte) 90;
        break;
    }

    return lvl;
  }

  public static byte getRequiredGrade(int id) {
    return mercenaryRequired.containsKey(Integer.valueOf(id))
        ? mercenaryRequired.get(Integer.valueOf(id)).byteValue()
        : 1;
  }
}
