package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.rs2.entity.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DamageMap {

  private final Map<Entity, Integer> dmg = new HashMap<Entity, Integer>();

  private long lastDamage = 0L;

  public DamageMap(Entity e) {}

  public void addDamage(Entity assaulter, long damage) {
    lastDamage = System.currentTimeMillis() + 60000;

    if (damage == 0) {
      return;
    }

    if (dmg.get(assaulter) == null) {
      dmg.put(assaulter, Integer.valueOf((int) damage));
    } else {
      int total = dmg.get(assaulter).intValue();
      dmg.remove(assaulter);
      dmg.put(assaulter, Integer.valueOf((int) (total + damage)));
    }
  }

  public void clear() {
    dmg.clear();
    lastDamage = 0L;
  }

  public Entity getKiller() {
    int highDmg = 0;
    Entity highEn = null;

    for (Entry<Entity, Integer> i : dmg.entrySet()) {
      if (i != null && i.getValue() > highDmg) {
        if (!i.getKey().isNpc() && i.getKey().getStoner() != null && dmg.size() > 1) {
          continue;
        }
        highDmg = i.getValue();
        highEn = i.getKey();
      }
    }

    return highEn;
  }

  public boolean isClearHistory() {
    return lastDamage != 0 && dmg.size() > 0 && lastDamage <= System.currentTimeMillis();
  }
}
