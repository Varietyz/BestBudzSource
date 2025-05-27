package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.item.impl.GroundItem;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendGroundItem extends OutgoingPacket {

	private final GroundItem g;
	private final Location base;

	public SendGroundItem(Player p, GroundItem g) {
		super();
		this.g = g;
		this.base = new Location(p.getCurrentRegion());
	}

	@Override
	public void execute(Client client) {
		new SendCoordinates(g.getLocation(), base).execute(client);
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(6);
		out.writeHeader(client.getEncryptor(), 44);
		out.writeShort(g.getItem().getId(), StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
		out.writeShort(g.getItem().getAmount());
		out.writeByte(0);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 44;
	}

}
