package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.entity.WalkToActions;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

@SuppressWarnings("all")
public class ObjectPacket extends IncomingPacket {
  public static final int ITEM_ON_OBJECT = 192;
  public static final int FIRST_CLICK = 132;
  public static final int SECOND_CLICK = 252;
  public static final int THIRD_CLICK = 70;
  public static final int FOURTH_CLICK = 234;
  public static final int CAST_SPELL = 35;

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    if ((stoner.isDead()) || (!stoner.getController().canClick())) {
      return;
    }

    stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    TaskQueue.onMovement(stoner);

    stoner.getCombat().reset();

    switch (opcode) {
      case 192:
        int interfaceId = in.readShort();
        int id = in.readShort(true, StreamBuffer.ByteOrder.LITTLE);
        int y = in.readShort(true, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        int slot = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        int x = in.readShort(true, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        int z = stoner.getLocation().getZ();
        int itemId = in.readShort();

        if ((!Region.objectExists(id, x, y, z))
            && (!StonerConstants.isOverrideObjectExistance(stoner, id, x, y, z))) {
          System.out.println(
              "Object found to be non-existent: id: "
                  + id
                  + " at x:"
                  + x
                  + "  y:"
                  + y
                  + "  z:"
                  + z);
          return;
        }

        if (!stoner.getBox().slotContainsItem(slot, itemId)) {
          return;
        }

        WalkToActions.itemOnObject(stoner, itemId, id, x, y);
        break;
      case 132:
        x = in.readShort(true, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        id = in.readShort();
        y = in.readShort(StreamBuffer.ValueType.A);
        z = stoner.getLocation().getZ();

        if ((!Region.objectExists(id, x, y, z))
            && (!StonerConstants.isOverrideObjectExistance(stoner, id, x, y, z))) {
          System.out.println(
              "Object found to be non-existent: id: "
                  + id
                  + " at x:"
                  + x
                  + "  y:"
                  + y
                  + "  z:"
                  + z);
          return;
        }

        WalkToActions.clickObject(stoner, 1, id, x, y);
        break;
      case 252:
        id = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        y = in.readShort(true, StreamBuffer.ByteOrder.LITTLE);
        x = in.readShort(StreamBuffer.ValueType.A);
        z = stoner.getLocation().getZ();

        if ((!Region.objectExists(id, x, y, z))
            && (!StonerConstants.isOverrideObjectExistance(stoner, id, x, y, z))) {
          System.out.println(
              "Object found to be non-existent: id: "
                  + id
                  + " at x:"
                  + x
                  + "  y:"
                  + y
                  + "  z:"
                  + z);
          return;
        }

        WalkToActions.clickObject(stoner, 2, id, x, y);
        break;
      case 70:
        x = in.readShort(true, StreamBuffer.ByteOrder.LITTLE);
        y = in.readShort();
        id = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        z = stoner.getLocation().getZ();

        if ((!Region.objectExists(id, x, y, z))
            && (!StonerConstants.isOverrideObjectExistance(stoner, id, x, y, z))) {
          System.out.println(
              "Object found to be non-existent: id: "
                  + id
                  + " at x:"
                  + x
                  + "  y:"
                  + y
                  + "  z:"
                  + z);
          return;
        }

        WalkToActions.clickObject(stoner, 3, id, x, y);
        break;
      case 234:
        x = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        id = in.readShort(StreamBuffer.ValueType.A);
        y = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        z = stoner.getLocation().getZ();

        if ((!Region.objectExists(id, x, y, z))
            && (!StonerConstants.isOverrideObjectExistance(stoner, id, x, y, z))) {
          System.out.println(
              "Object found to be non-existent: id: "
                  + id
                  + " at x:"
                  + x
                  + "  y:"
                  + y
                  + "  z:"
                  + z);
          return;
        }

        WalkToActions.clickObject(stoner, 4, id, x, y);
        break;
      case 35:
        x = in.readShort(StreamBuffer.ByteOrder.LITTLE);

        int mageId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.BIG);
        y = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.BIG);
        id = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        z = stoner.getLocation().getZ();

        if ((!Region.objectExists(id, x, y, z))
            && (!StonerConstants.isOverrideObjectExistance(stoner, id, x, y, z))) {
          System.out.println(
              "Object found to be non-existent: id: "
                  + id
                  + " at x:"
                  + x
                  + "  y:"
                  + y
                  + "  z:"
                  + z);
          return;
        }
        break;
    }
  }
}
