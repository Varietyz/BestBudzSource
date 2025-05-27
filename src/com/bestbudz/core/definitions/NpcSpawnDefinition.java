package com.bestbudz.core.definitions;

import com.bestbudz.rs2.entity.Location;

public class NpcSpawnDefinition {

  private byte face = -1;
  private short id;
  private Location location;
  private boolean walk;

  public int getFace() {
    return face;
  }

  public int getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  public boolean isWalk() {
    return walk;
  }
}
