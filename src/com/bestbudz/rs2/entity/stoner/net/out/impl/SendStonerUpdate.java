package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.rs2.entity.stoner.StonerUpdateFlags;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.StonerUpdating;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendStonerUpdate extends OutgoingPacket {

	private final StonerUpdateFlags[] pFlags;

	public SendStonerUpdate(StonerUpdateFlags[] pFlags) {
	super();
	this.pFlags = pFlags;
	}

	@Override
	public void execute(Client client) {
	StonerUpdating.update(client.getStoner(), pFlags);
	}

	@Override
	public int getOpcode() {
	return 81;
	}

}
