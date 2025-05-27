package com.bestbudz.core.util;

import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;

public class UpdateableMob {
  protected final short mob;
  protected int viewed = 1;

  public UpdateableMob(Mob mob) {
    this.mob = (short) mob.getIndex();
  }

  @Override
  public boolean equals(Object o) {
    return this.mob == ((UpdateableMob) o).mob;
  }

  public Mob getMob() {
    return World.getNpcs()[mob];
  }
}
