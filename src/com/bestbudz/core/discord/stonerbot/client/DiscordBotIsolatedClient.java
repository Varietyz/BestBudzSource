package com.bestbudz.core.discord.stonerbot.client;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotStonerConfig;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.net.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DiscordBotIsolatedClient extends Client {
	private static final Logger logger = Logger.getLogger(DiscordBotIsolatedClient.class.getSimpleName());

	private volatile long lastPacketTime;
	private List<com.bestbudz.rs2.entity.mob.Mob> cachedNpcs = new ArrayList<>();
	private volatile long lastNpcUpdate = 0;
	private com.bestbudz.rs2.entity.stoner.Stoner botStoner;

	public DiscordBotIsolatedClient() {
		super(null);
		setStage(Stages.LOGGED_OUT);
		this.lastPacketTime = System.currentTimeMillis();
	}

	public void setBotStoner(com.bestbudz.rs2.entity.stoner.Stoner botStoner) {
		this.botStoner = botStoner;
		setStoner(botStoner);
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

		populateNpcsFromWorld();
	}

	@Override
	public void processOutgoingPackets() {
		updateLastPacketTime();
	}

	@Override
	public void reset() {
		updateLastPacketTime();
		cachedNpcs.clear();
	}

	@Override
	public long getLastPacketTime() {
		return lastPacketTime;
	}

	private void updateLastPacketTime() {
		this.lastPacketTime = System.currentTimeMillis();
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
	public List<com.bestbudz.rs2.entity.mob.Mob> getNpcs() {

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastNpcUpdate > DiscordBotStonerConfig.NPC_UPDATE_INTERVAL) {
			populateNpcsFromWorld();
			lastNpcUpdate = currentTime;
		}
		return new ArrayList<>(cachedNpcs);
	}

	private void populateNpcsFromWorld() {
		try {

			cachedNpcs.clear();

			if (botStoner == null || botStoner.getLocation() == null) {
				return;
			}

			com.bestbudz.rs2.entity.mob.Mob[] worldNpcs = com.bestbudz.rs2.entity.World.getNpcs();
			if (worldNpcs == null) {
				return;
			}

			Location botLoc = botStoner.getLocation();
			int npcsAdded = populateNpcsInRange(worldNpcs, botLoc);

			if (DiscordBotStonerConfig.DEBUG_ENABLED && npcsAdded > 0) {

			}

		} catch (Exception e) {
			logger.warning("Error populating NPCs for Discord bot: " + e.getMessage());
			cachedNpcs.clear();
		}
	}

	private int populateNpcsInRange(com.bestbudz.rs2.entity.mob.Mob[] worldNpcs, Location botLoc) {
		int npcsAdded = 0;

		for (com.bestbudz.rs2.entity.mob.Mob npc : worldNpcs) {
			if (npc == null || !npc.isActive()) {
				continue;
			}

			int distance = DiscordBotStonerConfig.calculateDistance(botLoc, npc.getLocation());

			if (distance <= DiscordBotStonerConfig.MOVEMENT_RANGE_MAX) {
				cachedNpcs.add(npc);
				npcsAdded++;
			}
		}

		return npcsAdded;
	}

	public int getCachedNpcCount() {
		return cachedNpcs.size();
	}

	public void forceRefreshNpcs() {
		lastNpcUpdate = 0;
		populateNpcsFromWorld();
	}

	public boolean isConnected() {
		return getStage() == Stages.LOGGED_IN;
	}

	public void simulateLogin() {
		setStage(Stages.LOGGED_IN);
		updateLastPacketTime();
		forceRefreshNpcs();
	}

	public void simulateLogout() {
		setStage(Stages.LOGGED_OUT);
		cachedNpcs.clear();
		updateLastPacketTime();
	}
}
