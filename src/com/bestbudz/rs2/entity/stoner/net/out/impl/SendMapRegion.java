package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendMapRegion extends OutgoingPacket {

	private final Location p;

	public SendMapRegion(Stoner stoner) {
	stoner.getCurrentRegion().setAs(stoner.getLocation());
	p = stoner.getLocation();
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
	out.writeHeader(client.getEncryptor(), 73);
	out.writeShort(p.getRegionX() + 6, StreamBuffer.ValueType.A);
	out.writeShort(p.getRegionY() + 6);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 73;
	}

}
