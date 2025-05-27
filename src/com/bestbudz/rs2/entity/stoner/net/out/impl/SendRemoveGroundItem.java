package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.impl.GroundItem;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendRemoveGroundItem extends OutgoingPacket {

	private final GroundItem g;
	private final Location pRegion;

	public SendRemoveGroundItem(Stoner p, GroundItem g) {
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
