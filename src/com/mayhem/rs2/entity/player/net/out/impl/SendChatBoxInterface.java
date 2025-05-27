package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendChatBoxInterface extends OutgoingPacket {

	private final int id;

	public SendChatBoxInterface(int id) {
		super();
		this.id = id;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
		out.writeHeader(client.getEncryptor(), 164);
		out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
		client.send(out.getBuffer());
		client.getPlayer().getInterfaceManager().setChat(id);
	}

	@Override
	public int getOpcode() {
		return 164;
	}

}
