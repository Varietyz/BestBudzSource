package com.bestbudz.core.discord.stonerbot.client;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotStonerConfig;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.net.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * FINAL FIX: Isolated client implementation for Discord Bot with proper instance management
 */
public class DiscordBotIsolatedClient extends Client {
	private static final Logger logger = Logger.getLogger(DiscordBotIsolatedClient.class.getSimpleName());

	private volatile long lastPacketTime;
	private List<com.bestbudz.rs2.entity.mob.Mob> cachedNpcs = new ArrayList<>();
	private volatile long lastNpcUpdate = 0;
	private com.bestbudz.rs2.entity.stoner.Stoner botStoner; // Reference to the actual bot stoner

	public DiscordBotIsolatedClient() {
		super(null); // This no longer creates a conflicting Stoner instance
		setStage(Stages.LOGGED_OUT);
		this.lastPacketTime = System.currentTimeMillis();
	}

	// CRITICAL FIX: Set the bot stoner reference after creation
	public void setBotStoner(com.bestbudz.rs2.entity.stoner.Stoner botStoner) {
		this.botStoner = botStoner;
		setStoner(botStoner); // Set in parent Client class
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
		// Populate NPCs for Discord bot
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

	/**
	 * FIXED: Return Discord bot's own NPC cache
	 */
	@Override
	public List<com.bestbudz.rs2.entity.mob.Mob> getNpcs() {
		// Ensure NPCs are always up to date
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastNpcUpdate > DiscordBotStonerConfig.NPC_UPDATE_INTERVAL) {
			populateNpcsFromWorld();
			lastNpcUpdate = currentTime;
		}
		return new ArrayList<>(cachedNpcs); // Return copy to prevent external modification
	}

	/**
	 * FIXED: Populate NPCs from world for Discord bot only
	 */
	private void populateNpcsFromWorld() {
		try {
			// Clear previous cache
			cachedNpcs.clear();

			// Get the Discord bot from the stored reference
			if (botStoner == null || botStoner.getLocation() == null) {
				return;
			}

			// Get world NPCs array
			com.bestbudz.rs2.entity.mob.Mob[] worldNpcs = com.bestbudz.rs2.entity.World.getNpcs();
			if (worldNpcs == null) {
				return;
			}

			Location botLoc = botStoner.getLocation();
			int npcsAdded = populateNpcsInRange(worldNpcs, botLoc);

			// Debug output
			if (DiscordBotStonerConfig.DEBUG_ENABLED && npcsAdded > 0) {
			//	System.out.println("DiscordBotClient populated " + npcsAdded + " NPCs for Discord bot at " + botLoc);
			}

		} catch (Exception e) {
			logger.warning("Error populating NPCs for Discord bot: " + e.getMessage());
			cachedNpcs.clear();
		}
	}

	/**
	 * Populate regular NPCs within range of bot location
	 */
	private int populateNpcsInRange(com.bestbudz.rs2.entity.mob.Mob[] worldNpcs, Location botLoc) {
		int npcsAdded = 0;

		for (com.bestbudz.rs2.entity.mob.Mob npc : worldNpcs) {
			if (npc == null || !npc.isActive()) {
				continue;
			}

			// Calculate distance using configuration method
			int distance = DiscordBotStonerConfig.calculateDistance(botLoc, npc.getLocation());

			// Include NPCs within reasonable range
			if (distance <= DiscordBotStonerConfig.MOVEMENT_RANGE_MAX) {
				cachedNpcs.add(npc);
				npcsAdded++;
			}
		}

		return npcsAdded;
	}

	/**
	 * Get cached NPCs count
	 */
	public int getCachedNpcCount() {
		return cachedNpcs.size();
	}

	/**
	 * Force refresh NPCs
	 */
	public void forceRefreshNpcs() {
		lastNpcUpdate = 0; // Force update on next call
		populateNpcsFromWorld();
	}

	/**
	 * Check if client is connected (always true for bot)
	 */
	public boolean isConnected() {
		return getStage() == Stages.LOGGED_IN;
	}

	/**
	 * Simulate login process
	 */
	public void simulateLogin() {
		setStage(Stages.LOGGED_IN);
		updateLastPacketTime();
		forceRefreshNpcs();
	}

	/**
	 * Simulate logout process
	 */
	public void simulateLogout() {
		setStage(Stages.LOGGED_OUT);
		cachedNpcs.clear();
		updateLastPacketTime();
	}
}