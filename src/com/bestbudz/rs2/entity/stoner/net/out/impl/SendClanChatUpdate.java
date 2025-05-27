package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.network.StreamBuffer.ByteOrder;
import com.bestbudz.core.network.StreamBuffer.ValueType;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendClanChatUpdate extends OutgoingPacket {

	private final long[] names;
	private final byte[] ranks;

	public SendClanChatUpdate(Stoner[] inClan, long owner) {
	ranks = new byte[100];
	names = new long[100];

	for (int i = 0; i < 100; i++) {
		ranks[i] = -1;
	}

	int index = 0;
	for (Stoner i : inClan) {
		if (i != null) {
			names[index] = i.getUsernameToLong();
			ranks[index] = (owner == i.getUsernameToLong() ? (byte) 3 : (byte) 1);
			index++;
		}
	}
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer((100 * 8) + 100 + 5);
	out.writeHeader(client.getEncryptor(), 1);

	for (int i = 0; i < names.length; i++) {
		out.writeLong(names[i], ValueType.STANDARD, ByteOrder.LITTLE);
		out.writeByte(ranks[i]);
	}

	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 2;
	}

}
