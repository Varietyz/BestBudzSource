package com.mayhem.rs2.entity.player.net.in.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.in.IncomingPacket;
import com.mayhem.rs2.entity.player.net.out.impl.SendExpCounter;

public class ResetCounter extends IncomingPacket {
	@Override
	public int getMaxDuplicates() {
		return 1;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
    	player.addCounterExp(-player.getCounterExp());
    	player.send(new SendExpCounter(0, 0));
	}
}
