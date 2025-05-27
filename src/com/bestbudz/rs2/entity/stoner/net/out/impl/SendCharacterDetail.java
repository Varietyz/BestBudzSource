package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendCharacterDetail extends OutgoingPacket {

  @Override
  public void execute(Client client) {}

  @Override
  public int getOpcode() {
    return 36;
  }
}
