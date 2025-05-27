package com.bestbudz.rs2.entity.stoner.net.in;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class IncomingPacket {

	public abstract int getMaxDuplicates();

	public abstract void handle(Stoner paramStoner, StreamBuffer.InBuffer paramInBuffer, int paramInt1, int paramInt2);
}