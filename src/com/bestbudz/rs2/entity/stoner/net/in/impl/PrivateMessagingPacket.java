package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class PrivateMessagingPacket extends IncomingPacket {

  public static final int ADD_FRIEND = 188;
  public static final int REMOVE_FRIEND = 215;
  public static final int ADD_IGNORE = 133;
  public static final int REMOVE_IGNORE = 74;
  public static final int SEND_PM = 126;
  public static final int ENTER_CLAN_CHAT = 76;

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    switch (opcode) {
      case 188:
      case 215:
      case 133:
        long name = in.readLong();
        stoner.getPrivateMessaging().addIgnore(name);
        break;
      case 74:
        name = in.readLong();
        stoner.getPrivateMessaging().removeIgnore(name);
        break;
      case 126:
        name = in.readLong();
        int size = length - 8;
        byte[] message = in.readBytes(size);
        stoner.getPrivateMessaging().sendPrivateMessage(name, size, message);
        break;
    }
  }
}
