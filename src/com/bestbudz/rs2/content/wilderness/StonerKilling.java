package com.bestbudz.rs2.content.wilderness;

import com.bestbudz.rs2.entity.stoner.Stoner;

public class StonerKilling {

  public static boolean addHostToList(Stoner stoner, String host) {
    if (stoner != null && stoner.getLastKilledStoners() != null) {
      return stoner.getLastKilledStoners().add(host);
    }
    return false;
  }

  public static boolean hostOnList(Stoner stoner, String host) {
    if (stoner != null && stoner.getLastKilledStoners() != null) {
      if (stoner.getLastKilledStoners().lastIndexOf(host) >= 3) {
        removeHostFromList(stoner, host);
        return false;
      }
      return stoner.getLastKilledStoners().contains(host);
    }
    return false;
  }

  public static boolean removeHostFromList(Stoner stoner, String host) {
    if (stoner != null && stoner.getLastKilledStoners() != null) {
      return stoner.getLastKilledStoners().remove(host);
    }
    return false;
  }
}
