package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendMapRegion extends OutgoingPacket {

	private final Location p;

	public SendMapRegion(Player player) {
		player.getCurrentRegion().setAs(player.getLocation());
		p = player.getLocation();
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
