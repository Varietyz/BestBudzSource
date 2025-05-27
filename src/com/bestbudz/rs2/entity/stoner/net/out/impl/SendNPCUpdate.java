package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.rs2.entity.mob.MobUpdateFlags;
import com.bestbudz.rs2.entity.stoner.StonerUpdateFlags;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.NPCUpdating;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendNPCUpdate extends OutgoingPacket {

  private final MobUpdateFlags[] nFlags;
  private final StonerUpdateFlags pFlags;

  public SendNPCUpdate(MobUpdateFlags[] nFlags, StonerUpdateFlags pFlags) {
    super();
    this.nFlags = nFlags;
    this.pFlags = pFlags;
  }

  @Override
  public void execute(Client client) {
    NPCUpdating.update(client, pFlags, nFlags);
  }

  @Override
  public int getOpcode() {
    return 65;
  }
}
