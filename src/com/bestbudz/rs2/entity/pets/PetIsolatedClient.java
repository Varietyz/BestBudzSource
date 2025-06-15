package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * FINAL FIX: Truly isolated client for pets with proper stoner reference management
 */
public class PetIsolatedClient extends com.bestbudz.rs2.entity.stoner.net.Client {
	private final Stoner owner;
	private volatile long lastPacketTime;
	private Stoner petStoner; // Reference to the actual pet stoner

	public PetIsolatedClient(Stoner owner) {
		super(null); // This no longer creates a conflicting Stoner instance
		this.owner = owner;
		this.lastPacketTime = System.currentTimeMillis();
		setStage(Stages.LOGGED_OUT);
	}

	// CRITICAL FIX: Set the pet stoner reference after creation
	public void setPetStoner(Stoner petStoner) {
		this.petStoner = petStoner;
		setStoner(petStoner); // Set in parent Client class
	}

	@Override
	public void disconnect() {
		// No actual network connection to disconnect
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
		// Minimal processing for pets
	}

	@Override
	public void processOutgoingPackets() {
		updateLastPacketTime();
		// Pets don't send packets
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

	/**
	 * CRITICAL FIX: Return empty NPC list for pets to avoid conflicts
	 */
	@Override
	public java.util.List<com.bestbudz.rs2.entity.mob.Mob> getNpcs() {
		// Pets don't need to see NPCs for combat/interaction
		// This prevents conflicts with other entity NPC lists
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