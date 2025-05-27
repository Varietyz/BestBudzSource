package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer.ByteOrder;
import com.bestbudz.core.network.StreamBuffer.InBuffer;
import com.bestbudz.core.network.StreamBuffer.ValueType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.FollowToEntityTask;
import com.bestbudz.rs2.content.wilderness.TargetSystem;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.following.Following.FollowType;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

@SuppressWarnings("all")
public class StonerOptionPacket extends IncomingPacket {

  public static final int TRADE = 153;
  public static final int FOLLOW = 128;
  public static final int ASSAULT = 73;
  public static final int OPTION_4 = 139;
  public static final int MAGE_ON_STONER = 249;
  public static final int USE_ITEM_ON_STONER = 14;
  public static final int TRADE_ANSWER2 = 39;

  @Override
  public int getMaxDuplicates() {
    return 2;
  }

  @Override
  public void handle(final Stoner stoner, InBuffer in, int opcode, int length) {
    if ((stoner.isDead()) || (!stoner.getController().canClick())) {
      return;
    }

    int stonerSlot = -1;

    int itemSlot = -1;
    TaskQueue.onMovement(stoner);

    Stoner other = null;

    if (stoner.getDueling().getInteracting() != null) {
      if (stoner.getDueling().isScreen()) {
        stoner.getDueling().decline();
      }
    }

    stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    stoner.getMage().getSpellCasting().resetOnAssault();

    switch (opcode) {
      case 153:
      case 128:
        final int slot = in.readShort(true, ByteOrder.LITTLE);

        if ((!World.isStonerWithinRange(slot))
            || (World.getStoners()[slot] == null)
            || (slot == stoner.getIndex())) {
          return;
        }

        TaskQueue.queue(
            new FollowToEntityTask(stoner, World.getStoners()[slot]) {
              @Override
              public void onDestination() {
                Stoner other = World.getStoners()[slot];

                if (other == null) {
                  stoner.getMovementHandler().reset();
                  return;
                }

                if (stoner.getController().equals(ControllerManager.DUEL_ARENA_CONTROLLER)) {
                  if (TargetSystem.getInstance().stonerHasTarget(stoner)) {
                    stoner
                        .getClient()
                        .queueOutgoingPacket(
                            new SendMessage(
                                "You can't duel whilst having an active wilderness target."));
                    return;
                  }
                  stoner.face(other);
                  stoner.getDueling().request(other);
                }
              }
            });
        break;
      case 39:
        final int tradeSlot = in.readShort(true, ByteOrder.LITTLE);

        if ((!World.isStonerWithinRange(tradeSlot))
            || (World.getStoners()[tradeSlot] == null)
            || (tradeSlot == stoner.getIndex())) {
          return;
        }

        TaskQueue.queue(
            new FollowToEntityTask(stoner, World.getStoners()[tradeSlot]) {
              @Override
              public void onDestination() {
                Stoner other = World.getStoners()[tradeSlot];

                if (other == null) {
                  stop();
                  stoner.getMovementHandler().reset();
                  return;
                }

                stoner.face(other);

                stoner.getTrade().request(other);
                stop();
              }
            });
        break;
      case 139:
        stoner.getMovementHandler().reset();
        stonerSlot = in.readShort(true, ByteOrder.LITTLE);

        if ((!World.isStonerWithinRange(stonerSlot)) || (stonerSlot == stoner.getIndex())) {
          stoner.send(
              new SendMessage(
                  (!World.isStonerWithinRange(stonerSlot))
                      + " "
                      + (stonerSlot == stoner.getIndex())));
          return;
        }

        other = World.getStoners()[stonerSlot];

        if (other == null) {
          return;
        }
        if (stoner.getDueling().getInteracting() != null) {
          if (stoner.getDueling().isScreen()) {
            return;
          }
        }
        stoner.getFollowing().setFollow(other);
        break;
      case 73:
        stonerSlot = in.readShort(true, ByteOrder.LITTLE);
        stoner.getMovementHandler().reset();

        if ((stonerSlot == stoner.getIndex()) || (!World.isStonerWithinRange(stonerSlot))) {
          return;
        }

        other = World.getStoners()[stonerSlot];

        if (other == null) {
          return;
        }

        if (stoner.getController().equals(ControllerManager.DUEL_ARENA_CONTROLLER)) {
          final Stoner o = other;
          TaskQueue.queue(
              new FollowToEntityTask(stoner, o) {
                @Override
                public void onDestination() {
                  if (o == null) {
                    stoner.getMovementHandler().reset();
                    return;
                  }

                  if (stoner.getController().equals(ControllerManager.DUEL_ARENA_CONTROLLER)) {
                    if (TargetSystem.getInstance().stonerHasTarget(stoner)) {
                      stoner
                          .getClient()
                          .queueOutgoingPacket(
                              new SendMessage(
                                  "You can't duel whilst having an active wilderness target."));
                      return;
                    }
                    stoner.face(o);
                    stoner.getDueling().request(o);
                  }
                }
              });
          return;
        }

        if (stoner.getController().canMove(stoner)) {
          stoner.getFollowing().setFollow(other, FollowType.COMBAT);
        }
        stoner.getCombat().setAssaulting(other);

        stoner.getMage().getSpellCasting().disableClickCast();

        break;
      case 249:
        stonerSlot = in.readShort(true, ValueType.A);
        int mageId = in.readShort(true, ByteOrder.LITTLE);

        stoner.getMovementHandler().reset();

        if ((!World.isStonerWithinRange(stonerSlot))
            || (World.getStoners()[stonerSlot] == null)
            || (stonerSlot == stoner.getIndex())) {
          return;
        }

        other = World.getStoners()[stonerSlot];

        stoner.getMage().getSpellCasting().castCombatSpell(mageId, other);
        break;
      case 14:
        int interfaceId = in.readShort(ValueType.A);
        stonerSlot = in.readShort();
        int item = in.readShort();
        itemSlot = in.readShort(ByteOrder.LITTLE);

        if ((!World.isStonerWithinRange(stonerSlot)) || (stonerSlot == stoner.getIndex())) {
          return;
        }

        break;
    }
  }
}
