package com.bestbudz.rs2.entity.mob;

import com.bestbudz.core.cache.map.Tile;
import java.util.ArrayList;
import java.util.List;

public class VirtualMobRegion {
  private final List<Tile> occ = new ArrayList<Tile>();

  public VirtualMobRegion() {}

  public VirtualMobRegion(int baseX, int baseY, int size) {}

  public boolean isMobOnTile(int x, int y, int z) {
    if (z > 3) {
      z %= 4;
    }

    return occ.contains(new Tile(x, y, z));
  }

  public void setMobOnTile(int x, int y, int z, boolean set) {
    if (z > 3) {
      z %= 4;
    }

    if (set) occ.add(new Tile(x, y, z));
    else occ.remove(new Tile(x, y, z));
  }
}
