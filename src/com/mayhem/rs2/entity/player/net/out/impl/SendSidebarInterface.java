package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendSidebarInterface extends OutgoingPacket {

	private final int tabId;

	private final int interfaceId;

	public SendSidebarInterface(int tabId, int interfaceId) {
		super();
		this.tabId = tabId;
		this.interfaceId = interfaceId;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(4);
		out.writeHeader(client.getEncryptor(), 71);
		out.writeShort(interfaceId);
		out.writeByte(tabId, StreamBuffer.ValueType.A);
		client.send(out.getBuffer());
		client.getPlayer().getInterfaceManager().setTabId(tabId, interfaceId);
	}

	@Override
	public int getOpcode() {
		return 71;
	}

}
