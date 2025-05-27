package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.network.StreamBuffer.OutBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendClanMessage extends OutgoingPacket {

  private final String username;

  private final String message;

  private final String clanOwner;

  private final int rights;

  public SendClanMessage(String username, String message, String owner, int rights) {
    this.username = username;
    this.message = message;
    this.clanOwner = owner;
    this.rights = rights;
  }

  @Override
  public void execute(Client client) {
    OutBuffer out = StreamBuffer.newOutBuffer(100);
    out.writeHeader(client.getEncryptor(), getOpcode());
    out.writeString(username);
    out.writeString(message);
    out.writeString(clanOwner);
    out.writeShort(rights & 0xFF);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 217;
  }
}
