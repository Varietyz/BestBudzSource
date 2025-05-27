package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.item.impl.GroundItem;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendRemoveGroundItem extends OutgoingPacket {

	private final GroundItem g;
	private final Location pRegion;

	public SendRemoveGroundItem(Player p, GroundItem g) {
		super();
		this.g = g;
		pRegion = new Location(p.getCurrentRegion());
	}

	@Override
	public void execute(Client client) {
		new SendCoordinates(g.getLocation(), pRegion).execute(client);
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(4);
		out.writeHeader(client.getEncryptor(), 156);
		out.writeByte(0, StreamBuffer.ValueType.S);
		out.writeShort(g.getItem().getId());
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 156;
	}

}
