package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendRemoveInterfaces extends OutgoingPacket {
	
	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(1);
		out.writeHeader(client.getEncryptor(), 219);
		client.send(out.getBuffer());
		client.getPlayer().getInterfaceManager().reset();
	}

	@Override
	public int getOpcode() {
		return 219;
	}

}
