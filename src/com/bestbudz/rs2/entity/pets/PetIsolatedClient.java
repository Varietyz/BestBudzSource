package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.stoner.Stoner;

public class PetIsolatedClient extends com.bestbudz.rs2.entity.stoner.net.Client {
	private final Stoner owner;
	private volatile long lastPacketTime;
	private Stoner petStoner;

	public PetIsolatedClient(Stoner owner) {
		super(null);
		this.owner = owner;
		this.lastPacketTime = System.currentTimeMillis();
		setStage(Stages.LOGGED_OUT);
	}

	public void setPetStoner(Stoner petStoner) {
		this.petStoner = petStoner;
		setStoner(petStoner);
	}

	@Override
	public void disconnect() {

	}

	@Override
	public void send(io.netty.buffer.ByteBuf buffer) {
		if (buffer != null && buffer.refCnt() > 0) {
			buffer.release();
		}
		updateLastPacketTime();
	}

	@Override
	public void processIncomingPackets() {
		updateLastPacketTime();

	}

	@Override
	public void processOutgoingPackets() {
		updateLastPacketTime();

	}

	@Override
	public void reset() {
		updateLastPacketTime();
	}

	@Override
	public long getLastPacketTime() {
		return lastPacketTime;
	}

	@Override
	public void resetLastPacketReceived() {
		updateLastPacketTime();
	}

	@Override
	public boolean checkSendString(String message, int id) {
		return true;
	}

	@Override
	public java.util.List<com.bestbudz.rs2.entity.mob.Mob> getNpcs() {

		return new java.util.ArrayList<>();
	}

	private void updateLastPacketTime() {
		this.lastPacketTime = System.currentTimeMillis();
	}

	public boolean isConnected() {
		return getStage() == Stages.LOGGED_IN;
	}

	public void simulateLogin() {
		setStage(Stages.LOGGED_IN);
		updateLastPacketTime();
	}

	public void simulateLogout() {
		setStage(Stages.LOGGED_OUT);
		updateLastPacketTime();
	}
}
