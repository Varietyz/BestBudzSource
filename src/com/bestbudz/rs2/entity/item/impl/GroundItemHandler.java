package com.bestbudz.rs2.entity.item.impl;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GroundItemHandler {

  public static final int SHOW_GROUND_ITEM = 100;
  public static final int REMOVE_GROUND_ITEM = 350;
  public static final int MAX_GLOBALIZATION = 10;
  public static final int MAX_REMOVAL = 10;
  private static final List<GroundItem> active = new LinkedList<GroundItem>();
  private static final List<GroundItem> globalizeQueue = new LinkedList<GroundItem>();

  public static boolean add(GroundItem groundItem) {
    Stoner owner = groundItem.getOwner();

    if ((owner != null) && (owner.getGroundItems().stack(groundItem))) {
      return true;
    }

    if ((owner != null) && (visible(owner, groundItem))) {
      owner.getGroundItems().add(groundItem);
    }

    active.add(groundItem);

    return true;
  }

  public static boolean add(Item item, Location location, Stoner stoner) {
    GroundItem groundItem =
        new GroundItem(item, new Location(location), stoner == null ? null : stoner.getUsername());
    add(groundItem);
    return true;
  }

  public static boolean add(Item item, Location location, Stoner stoner, Stoner include) {
    GroundItem groundItem =
        new GroundItem(item, new Location(location), stoner == null ? null : stoner.getUsername());
    groundItem.include(stoner == null ? null : stoner.getUsername());
    add(groundItem);
    return true;
  }

  public static boolean add(Item item, Location location, Stoner stoner, int time) {
    GroundItem groundItem =
        new GroundItem(item, new Location(location), stoner == null ? null : stoner.getUsername());

    if (time >= 0) {
      groundItem.setTime(time);
    }

    return add(groundItem);
  }

  public static boolean exists(GroundItem g) {
    return active.contains(g);
  }

  public static List<GroundItem> getActive() {
    return active;
  }

  public static GroundItem getGroundItem(
      int id, int x, int y, int z, String name, boolean specific) {
    long longAsName = name == null ? -1 : Utility.nameToLong(name);

    Location l = new Location(x, y, z);

    for (Iterator<GroundItem> i = active.iterator(); i.hasNext(); ) {
      GroundItem g = i.next();

      if (g.getLocation().equals(l) && g.exists()) {
        if (longAsName != -1 && longAsName == g.getLongOwnerName() && g.getItem().getId() == id
            || !specific && g.isGlobal() && g.getItem().getId() == id) {
          return g;
        }
      }
    }

    return null;
  }

  public static GroundItem getNonGlobalGroundItem(int id, int x, int y, int z, long name) {
    Location l = new Location(x, y, z);

    for (Iterator<GroundItem> i = active.iterator(); i.hasNext(); ) {
      GroundItem g = i.next();

      if ((g.getLocation().equals(l)) && (!g.isGlobal()) && (g.exists())) {
        if ((g.getLongOwnerName() == name) && (g.getItem().getId() == id)) {
          return g;
        }
      }
    }
    return null;
  }

  public static Region getRegion(GroundItem groundItem) {
    return Region.getRegion(groundItem.getLocation().getX(), groundItem.getLocation().getY());
  }

  public static void globalize(GroundItem groundItem) {
    globalizeQueue.add(groundItem);
  }

  public static void process() {
    synchronized (active) {
      // Phase 1: update ground items (timers + visibility)
      for (var item : active) {
        item.countdown();
        if (item.globalize()) {
          globalize(item);
        }
      }

      // Phase 2: remove items
      Iterator<GroundItem> i = active.iterator();
      while (i.hasNext()) {
        var item = i.next();
        if (!item.remove()) continue;

        item.erase();

        if (!item.isGlobal()) {
          var owner = item.getOwner();
          if (owner != null && visible(owner, item)) {
            owner.getGroundItems().remove(item);
          }
        } else {
          for (var stoner : World.getStoners()) {
            if (stoner != null && visible(stoner, item)) {
              stoner.getGroundItems().remove(item);
            }
          }
        }

        i.remove();
      }

      // Phase 3: globalize items
      Iterator<GroundItem> gq = globalizeQueue.iterator();
      while (gq.hasNext()) {
        var groundItem = gq.next();

        if (!groundItem.exists()) {
          gq.remove();
          continue;
        }

        groundItem.setGlobal(true);
        var owner = groundItem.getOwner();

        for (var stoner : World.getStoners()) {
          if (stoner != null && !stoner.equals(owner)) {
            if (visible(stoner, groundItem)) {
              stoner.getGroundItems().add(groundItem);
            }
          }
        }

        gq.remove();
      }
    }
  }

  public static boolean remove(GroundItem groundItem) {
    if (groundItem.isGlobal) {
      GlobalItemHandler.createRespawnTask(groundItem);
    }
    groundItem.erase();

    if (!groundItem.isGlobal()) {
      Stoner owner = groundItem.getOwner();

      if ((owner != null) && (visible(owner, groundItem)))
        owner.getGroundItems().remove(groundItem);
    } else {
      for (int k = 1; k < World.getStoners().length; k++) {
        Stoner stoner = World.getStoners()[k];

        if (stoner != null) {
          if (visible(stoner, groundItem)) {
            stoner.getGroundItems().remove(groundItem);
          }
        }
      }
    }
    active.remove(groundItem);

    return true;
  }

  public static boolean visible(Stoner stoner, GroundItem groundItem) {
    Stoner owner = groundItem.getOwner();

    return (stoner.withinRegion(groundItem.getLocation()))
        && (stoner.getLocation().getZ() == groundItem.getLocation().getZ())
        && ((groundItem.isGlobal()) || ((stoner.equals(owner))));
  }
}
