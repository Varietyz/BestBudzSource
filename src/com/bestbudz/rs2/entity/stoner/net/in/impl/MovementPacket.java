package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGame;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.pathfinding.RS317PathFinder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class MovementPacket extends IncomingPacket {
  @Override
  public int getMaxDuplicates() {
    return 2;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    stoner.getBank().setSearching(false);
    if ((stoner.isDead())
        || (stoner.getMage().isTeleporting())
        || (!stoner.getController().canMove(stoner))
        || (StonerConstants.isSettingAppearance(stoner))) {
      stoner.getCombat().reset();
      return;
    }

    if (WeaponGame.gameStoners.contains(stoner) && !stoner.inWGGame()) {
      WeaponGame.leaveGame(stoner, false);
      return;
    }

    if (WeaponGame.lobbyStoners.contains(stoner) && !stoner.inWGLobby()) {
      WeaponGame.leaveLobby(stoner, false);
      return;
    }

    if (stoner.isStunned()) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You are stunned!"));
      stoner.getCombat().reset();
      return;
    }

    if (stoner.isFrozen()) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You are rooted, man!"));
      stoner.getCombat().reset();
      return;
    }

    if (stoner.isJailed() && !stoner.inJailed()) {
      stoner.teleport(new Location(StonerConstants.JAILED_AREA));
      stoner.send(new SendMessage("You were jailed!"));
    }

    if (stoner.getInterfaceManager().main == 48500) {
      stoner.getPriceChecker().withdrawAll();
    }

    if (opcode == 248) {
      length -= 14;
    }

    if (opcode != 98) {
      stoner.getMovementHandler().setForced(false);

      stoner.getMage().getSpellCasting().disableClickCast();
      stoner.getFollowing().reset();
      stoner.getCombat().reset();
      stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

      if (stoner.getTrade().trading()) {
        stoner.getTrade().end(false);
      }

      if (stoner.getDueling().isStaking()) {
        stoner.getDueling().decline();
      }

      stoner.start(null);
      stoner.getShopping().reset();
      stoner.getInterfaceManager().reset();
      TaskQueue.onMovement(stoner);
      stoner.setEnterXInterfaceId(0);
    } else {
      stoner.getMovementHandler().setForced(true);
    }

    int steps = (length - 5) / 2;
    int[][] path = new int[steps][2];

    int firstStepX = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);

    for (int i = 0; i < steps; i++) {
      path[i][0] = in.readByte();
      path[i][1] = in.readByte();
    }

    int firstStepY = in.readShort(StreamBuffer.ByteOrder.LITTLE);
    in.readByte(StreamBuffer.ValueType.C);

    stoner.getMovementHandler().reset();

    for (int i = 0; i < steps; i++) {
      path[i][0] += firstStepX;
      path[i][1] += firstStepY;
    }

    if (steps > 0) {
      if ((Math.abs(path[(steps - 1)][0] - stoner.getLocation().getX()) > 21)
          || (Math.abs(path[(steps - 1)][1] - stoner.getLocation().getY()) > 21)) {
        stoner.getMovementHandler().reset();
      }

    } else if ((Math.abs(firstStepX - stoner.getLocation().getX()) > 21)
        || (Math.abs(firstStepY - stoner.getLocation().getY()) > 21)) {
      stoner.getMovementHandler().reset();
      return;
    }
    if (steps > 0)
      RS317PathFinder.findRoute(stoner, path[(steps - 1)][0], path[(steps - 1)][1], true, 16, 16);
    else {
      RS317PathFinder.findRoute(stoner, firstStepX, firstStepY, true, 16, 16);
    }
  }
}
