package com.bestbudz.core.task.impl;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public abstract class OpenChestTask extends Task {

  private final int x;
  private final int y;
  private final int z;
  private final int type;
  private final int face;
  private final int replace;
  private byte stage = 0;

  public OpenChestTask(Stoner p, int x, int y, int z, int replace, int type, int face) {
    super(p, 1);
    this.x = x;
    this.y = y;
    this.z = z;
    this.type = type;
    this.face = face;
    this.replace = replace;

    p.getClient().queueOutgoingPacket(new SendMessage("You open the chest.."));
    p.getUpdateFlags().sendAnimation(832, 0);
  }

  @Override
  public void execute() {
    if (stage == 0) {
      GameObject o = new GameObject(replace, x, y, z, type, face);
      ObjectManager.removeFromList(o);
      ObjectManager.register(o);
      Region.getRegion(x, y).addObject(new RSObject(x, y, z, replace, type, face));
    } else if (stage == 2) {
      stop();
    }

    stage++;
  }
}
