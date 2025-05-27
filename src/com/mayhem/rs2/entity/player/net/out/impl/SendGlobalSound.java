package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendGlobalSound extends OutgoingPacket {

	private final int id;

	private final int type;

	private final int delay;

	public SendGlobalSound(int id, int type, int delay) {
		super();
		this.id = id;
		this.type = type;
		this.delay = delay;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(18);
		out.writeHeader(client.getEncryptor(), getOpcode());
		out.writeShort(id);
		out.writeByte(type);
		out.writeShort(delay);
		for (Player player : World.getPlayers()) {
			if (player != null) {
				if (Utility.getExactDistance(client.getPlayer().getLocation(),
						player.getLocation()) < 10) {
					player.getClient().send(out.getBuffer());
				}
			}
		}
	}

	@Override
	public int getOpcode() {
		return 174;
	}

}
