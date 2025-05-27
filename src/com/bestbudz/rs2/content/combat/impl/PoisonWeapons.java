package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;
import java.util.Map;

public class PoisonWeapons {
  private static final Map<Integer, PoisonData> poison = new HashMap<Integer, PoisonData>();

  public static void checkForPoison(Stoner stoner, Entity assault) {
    if (Utility.randomNumber(3) != 0) {
      return;
    }

    if ((assault != null) && (!assault.isNpc())) {
      Stoner o = com.bestbudz.rs2.entity.World.getStoners()[assault.getIndex()];

      if (o != null) {
        Item shield = o.getEquipment().getItems()[5];

        if ((shield != null) && (shield.getId() == 18340)) {
          return;
        }
      }
    }

    Item weapon = stoner.getEquipment().getItems()[3];
    Item ammo = stoner.getEquipment().getItems()[13];

    CombatTypes type = stoner.getCombat().getCombatType();

    if (type == CombatTypes.MELEE) {
      if ((weapon == null) || (poison.get(Integer.valueOf(weapon.getId())) == null)) {
        return;
      }
      assault.poison(poison.get(Integer.valueOf(weapon.getId())).getStart());
    } else if (type == CombatTypes.SAGITTARIUS) {
      if ((ammo == null) || (poison.get(Integer.valueOf(ammo.getId())) == null)) {
        return;
      }
      assault.poison(poison.get(Integer.valueOf(ammo.getId())).getStart());
    }
  }

  public static final void declare() {
    for (int i = 0; i < 20145; i++) {
      ItemDefinition def = GameDefinitionLoader.getItemDef(i);

      if ((def != null) && (def.getName() != null)) {
        String name = def.getName();

        if (name.equalsIgnoreCase("toxic blowpipe")) {
          poison.put(Integer.valueOf(i), new PoisonData(20));
        }

        if (name.contains("(p)")) poison.put(Integer.valueOf(i), new PoisonData(4));
        else if (name.contains("(p+)")) poison.put(Integer.valueOf(i), new PoisonData(5));
        else if (name.contains("(p++)")) poison.put(Integer.valueOf(i), new PoisonData(6));
      }
    }
  }
}
