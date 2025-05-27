package com.bestbudz.core.util;

import com.bestbudz.rs2.entity.mob.Mob;
import java.util.*;

public class MobUpdateList {

  private final Map<Mob, UpdateableMob> tracked = new HashMap<>();

  private final Set<Mob> incr = new HashSet<>();
  private final Set<Mob> decr = new HashSet<>();
  private final Set<Mob> remove = new HashSet<>();

  public void incr(Mob mob) {
    incr.add(mob);
  }

  public void decr(Mob mob) {
    decr.add(mob);
  }

  public void toRemoval(Mob mob) {
    remove.add(mob);
  }

  public List<UpdateableMob> getList() {
    return new ArrayList<>(tracked.values());
  }

  public void process() {
    // Handle increments
    for (Mob mob : incr) {
      tracked.compute(
          mob,
          (m, u) -> {
            if (u == null) {
              return new UpdateableMob(m);
            }
            u.viewed++;
            return u;
          });
    }
    incr.clear();

    // Handle decrements
    for (Mob mob : decr) {
      UpdateableMob u = tracked.get(mob);
      if (u != null) {
        u.viewed--;
        if (u.viewed <= 0) {
          tracked.remove(mob);
        }
      }
    }
    decr.clear();

    // Final removals
    for (Mob mob : remove) {
      tracked.remove(mob);
    }
    remove.clear();
  }
}
