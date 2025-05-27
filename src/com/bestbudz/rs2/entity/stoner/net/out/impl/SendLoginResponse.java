package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendLoginResponse extends OutgoingPacket {

	private final int response;
	private final int rights;

	public SendLoginResponse(int response, int rights) {
	super();
	this.response = response;
	this.rights = rights;
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
	resp.writeByte(response);
	resp.writeByte(rights);
	resp.writeByte(0);
	client.send(resp.getBuffer());
	new SendMapRegion(client.getStoner()).execute(client);
	new SendDetails(client.getStoner().getIndex()).execute(client);
	}

	@Override
	public int getOpcode() {
	return -1;
	}

}
