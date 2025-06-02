package com.bestbudz.rs2.content.profession.bankstanding;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Bank Standing - Instance-based AFK skill for individual players
 * Each player has their own BankStanding instance
 */
public class BankStanding {

	public static final int BANKSTANDING_SKILL_ID = 19;

	// Player this instance belongs to
	private final Stoner stoner;

	// Session data
	private boolean isActive = false;
	private long sessionStartTime = 0;
	private Location startLocation = null;
	private long lastXPTime = 0;
	private long lastActivityTime = 0;
	private int standingMinutes = 0;
	private int totalMovement = 0;
	private int ticksStanding = 0;
	private int processTicks = 0; // Counter for processing cycles
	private int sessionXPGained = 0; // Track total XP gained this session
	private int sessionBonusXPGained = 0;

	// XP and bonus constants - TWEAK THESE VALUES
	private static final int BASE_XP = 110;              // Increased from 8 to 25
	private static final int MAX_STANDING_BONUS = 35;   // Increased from 15 to 20
	private static final int ACTIVITY_BONUS = 10;       // Increased from 5 to 10
	private static final double BASE_MULTIPLIER = 1.2;
	private static final int XP_INTERVAL_TICKS = 25;    // Reduced from 50 to 30 (more frequent XP)
	private static final int PROCESS_INTERVAL = 5; // Process every 5 game ticks

	/**
	 * Constructor - called when player logs in
	 */
	public BankStanding(Stoner stoner) {
		this.stoner = stoner;
	}

	/**
	 * Process this player's bank standing - MEMORY SAFE with exception handling
	 */
	public void process() {
		try {
			if (!stoner.isActive()) {
				return;
			}

			processTicks++;

			// Only check bank proximity every few ticks for performance
			if (processTicks % PROCESS_INTERVAL == 0) {
				boolean nearBank = isNearBank();

				if (nearBank && !isActive) {
					// Start bank standing
					startSession();
				} else if (!nearBank && isActive) {
					// Stop bank standing
					endSession();
				}
			}

			// Continue processing if active
			if (isActive) {
				processTick();
			}
		} catch (Exception e) {
			// Log error and safely stop bank standing to prevent cascading issues
			System.err.println("BankStanding error for " + stoner.getUsername() + ": " + e.getMessage());
			e.printStackTrace();
			forceStop();
		}
	}

	/**
	 * Start a new bank standing session
	 */
	private void startSession() {
		isActive = true;
		sessionStartTime = System.currentTimeMillis();
		startLocation = new Location(stoner.getLocation());
		lastXPTime = sessionStartTime;
		lastActivityTime = sessionStartTime;
		standingMinutes = 0;
		totalMovement = 0;
		ticksStanding = 0;
		sessionXPGained = 0; // Reset session XP counter
		sessionBonusXPGained = 0;

		stoner.send(new SendMessage("@gre@You begin bank standing training..."));

		// Debug message for testing
		if (stoner.getRights() >= 1) {
			stoner.send(new SendMessage("@blu@[DEBUG] Bank standing session started at " + startLocation.toString()));
		}
	}

	/**
	 * End the current bank standing session
	 */
	private void endSession() {
		if (isActive) {
			isActive = false;
			long duration = System.currentTimeMillis() - sessionStartTime;
			int minutes = (int) (duration / 60000);
			int seconds = (int) ((duration % 60000) / 1000);

			// Send summary message with total XP gained
			if (sessionXPGained > 0) {
				stoner.send(new SendMessage("@red@You stop bank standing training after " + minutes + "m " + seconds + "s."));
				stoner.send(new SendMessage("@gre@Total bank standing experience gained: " + sessionXPGained));
				if (sessionBonusXPGained > 0){
					stoner.send(new SendMessage("@gre@Total bonus experience gained: " + sessionBonusXPGained));
				}
			} else {
				stoner.send(new SendMessage("@red@You stop bank standing training after " + minutes + "m " + seconds + "s."));
			}

			// Debug message for testing
			if (stoner.getRights() >= 1) {
				stoner.send(new SendMessage("@blu@[DEBUG] Bank standing session ended. Total XP: " + sessionXPGained));
			}
		}
	}

	/**
	 * Process a tick while bank standing is active
	 */
	private void processTick() {
		ticksStanding++;
		long currentTime = System.currentTimeMillis();

		// Track movement from start position
		updateMovementTracking();

		// Give passive XP every ~30 seconds
		if (ticksStanding % XP_INTERVAL_TICKS == 0) {
			givePassiveXP();
			updateStandingBonus(currentTime);
		}
	}

	/**
	 * Update movement tracking
	 */
	private void updateMovementTracking() {
		if (startLocation != null) {
			Location currentLoc = stoner.getLocation();
			int distance = Math.max(
				Math.abs(currentLoc.getX() - startLocation.getX()),
				Math.abs(currentLoc.getY() - startLocation.getY())
			);

			if (distance > totalMovement) {
				totalMovement = distance;

				// If player moved too far, reduce effectiveness
				if (totalMovement > 5) {
					stoner.send(new SendMessage("@red@You've moved too far from your starting position!"));
				}
			}
		}
	}

	/**
	 * Give passive bank standing XP
	 */
	private void givePassiveXP() {
		int baseXP = calculatePassiveXP();

		// Apply bank standing XP bonus to the base XP (not multiplied by it)
		double finalXP = stoner.getProfession().addExperience(BANKSTANDING_SKILL_ID, baseXP);

		// Track session XP
		sessionXPGained += (int)finalXP;

		lastXPTime = System.currentTimeMillis();

		// Only send messages to admins for debugging - no regular XP messages
		if (stoner.getRights() >= 1) {
			long timeSinceActivity = System.currentTimeMillis() - lastActivityTime;
			boolean hasActivityBonus = timeSinceActivity < 60000;
			stoner.send(new SendMessage("@blu@[DEBUG] Gained " + (int)finalXP + " XP | Base: " + BASE_XP + ", Time: +" + Math.min(standingMinutes, MAX_STANDING_BONUS) + ", Activity: " + (hasActivityBonus ? "+" + ACTIVITY_BONUS : "0") + ", Movement penalty: " + (totalMovement > 3 ? "Yes" : "No")));
		}
	}

	/**
	 * Calculate passive XP based on standing conditions
	 */
	private int calculatePassiveXP() {
		int xp = BASE_XP;

		// Standing time bonus - FIXED: This should ADD to XP, not replace it
		int timeBonus = Math.min(standingMinutes, MAX_STANDING_BONUS);
		xp += timeBonus;

		// Activity bonus (if they've done something recently)
		long timeSinceActivity = System.currentTimeMillis() - lastActivityTime;
		if (timeSinceActivity < 60000) { // Within last minute
			xp += ACTIVITY_BONUS;
		}

		// Movement penalty - FIXED: Only apply if moved significantly
		if (totalMovement > 3) {
			xp = Math.max(xp / 2, BASE_XP); // Halve XP but never below base
		}
		sessionBonusXPGained += xp - BASE_XP;

		return xp;
	}

	/**
	 * Update standing time bonus
	 */
	private void updateStandingBonus(long currentTime) {
		if (sessionStartTime > 0) {
			int newMinutes = (int) ((currentTime - sessionStartTime) / 60000);
			if (newMinutes > standingMinutes && standingMinutes < MAX_STANDING_BONUS) {
				standingMinutes = newMinutes;
				stoner.send(new SendMessage("@blu@Bank standing bonus increased! (" + standingMinutes + " minutes)"));
			}
		}
	}

	/**
	 * Record player activity for bonus calculations
	 */
	public void recordActivity() {
		if (isActive) {
			lastActivityTime = System.currentTimeMillis();
		}
	}

	/**
	 * Get XP multiplier for other skills
	 */
	public double getXPMultiplier() {
		if (!isActive) {
			return 1.0;
		}

		// Base multiplier with small bonus for standing time
		double bonus = Math.min(standingMinutes * 0.02, 0.3); // Max +30% bonus
		return BASE_MULTIPLIER + bonus;
	}

	/**
	 * Apply bank standing XP bonus to other skills
	 * This should be called from Profession.addExperience() method
	 */
	public int applyXPBonus(int skillId, int baseXP) {
		// Don't apply multiplier to bank standing skill itself
		if (skillId == BANKSTANDING_SKILL_ID || !isActive) {
			return baseXP;
		}

		// Record activity
		recordActivity();

		// Apply multiplier
		double multiplier = getXPMultiplier();

		return (int) (baseXP * multiplier);
	}

	/**
	 * Get session information for commands
	 */
	public String getSessionInfo() {
		if (isActive) {
			long duration = System.currentTimeMillis() - sessionStartTime;
			int minutes = (int) (duration / 60000);
			int seconds = (int) ((duration % 60000) / 1000);

			return String.format("Bank Standing: %dm %ds | Bonus: +%d mins | Multiplier: %.2fx | Movement: %d tiles",
				minutes, seconds, Math.min(standingMinutes, MAX_STANDING_BONUS), getXPMultiplier(), totalMovement);
		} else if (isNearBank()) {
			return "You are near a bank. Stand still to begin bank standing training!";
		} else {
			return "You are not near a bank. Stand within 2 tiles of a bank booth or banker!";
		}
	}

	/**
	 * Check if player is near a bank - optimized version
	 */
	private boolean isNearBank() {
		Location playerLoc = stoner.getLocation();

		// Check bank objects first (more common)
		if (hasNearbyBankObjects(playerLoc)) {
			return true;
		}

		// Check bank NPCs
		return hasNearbyBankNPCs(playerLoc);
	}

	/**
	 * Check for nearby bank objects - MEMORY SAFE version
	 */
	private boolean hasNearbyBankObjects(Location playerLoc) {
		Region region = Region.getRegion(playerLoc);
		if (region == null) return false;

		RSObject[][][] objects = region.getObjects();
		if (objects == null) return false;

		int playerX = playerLoc.getX();
		int playerY = playerLoc.getY();
		int playerZ = playerLoc.getZ();

		// SAFE: Comprehensive Z bounds checking
		if (playerZ < 0 || playerZ >= objects.length || objects[playerZ] == null) {
			return false;
		}

		// Get region boundaries
		int regionAbsX = (region.getId() >> 8) << 6;
		int regionAbsY = (region.getId() & 0xff) << 6;

		// Convert player position to region-local coordinates for array indexing
		int playerLocalX = playerX - regionAbsX;
		int playerLocalY = playerY - regionAbsY;

		// Check 5x5 area around player (2 tiles in each direction)
		int startX = Math.max(0, playerLocalX - 2);
		int endX = Math.min(63, playerLocalX + 2);
		int startY = Math.max(0, playerLocalY - 2);
		int endY = Math.min(63, playerLocalY + 2);

		// MEMORY SAFE LOOP: Comprehensive bounds checking
		for (int localX = startX; localX <= endX; localX++) {
			// SAFE: Check X dimension bounds
			if (localX >= 0 && localX < objects[playerZ].length && objects[playerZ][localX] != null) {
				for (int localY = startY; localY <= endY; localY++) {
					// CRITICAL FIX: Check Y dimension bounds before access
					if (localY >= 0 && localY < objects[playerZ][localX].length) {
						RSObject obj = objects[playerZ][localX][localY];
						if (obj != null && isBankObject(obj.getId())) {
							// RSObject stores actual world coordinates, so check distance directly
							int deltaX = Math.abs(playerX - obj.getX());
							int deltaY = Math.abs(playerY - obj.getY());

							if (deltaX <= 2 && deltaY <= 2) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Check for nearby bank NPCs - MEMORY SAFE version
	 */
	private boolean hasNearbyBankNPCs(Location playerLoc) {
		// CRITICAL FIX: Null check on World.getNpcs()
		Mob[] npcs = World.getNpcs();
		if (npcs == null) {
			return false;
		}

		// SAFE LOOP: Use indexed loop with bounds checking instead of enhanced for
		for (int i = 0; i < npcs.length; i++) {
			Mob npc = npcs[i];
			if (npc != null && npc.isActive() && isBankNPC(npc.getId())) {
				Location npcLoc = npc.getLocation();

				// SAFE: Null check on location
				if (npcLoc == null) continue;

				// Quick Z-level check first
				if (npcLoc.getZ() != playerLoc.getZ()) {
					continue;
				}

				int deltaX = Math.abs(playerLoc.getX() - npcLoc.getX());
				int deltaY = Math.abs(playerLoc.getY() - npcLoc.getY());

				if (deltaX <= 2 && deltaY <= 2) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if object is a bank object
	 */
	private static boolean isBankObject(int objectId) {
		// Using the constants from BankStandingConstants for consistency
		for (int bankObjectId : BankStandingConstants.BANK_OBJECT_IDS) {
			if (objectId == bankObjectId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if NPC is a bank NPC
	 */
	private static boolean isBankNPC(int npcId) {
		// Using the constants from BankStandingConstants for consistency
		for (int bankNpcId : BankStandingConstants.BANK_NPC_IDS) {
			if (npcId == bankNpcId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get whether bank standing is currently active
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Force stop bank standing (for teleports, logout, etc.)
	 */
	public void forceStop() {
		if (isActive) {
			isActive = false;
			// Show XP summary when force stopped too
			if (sessionXPGained > 0) {
				stoner.send(new SendMessage("@red@Bank standing training interrupted."));
				stoner.send(new SendMessage("@gre@Total bank standing experience gained: " + sessionXPGained));

			} else {
				stoner.send(new SendMessage("@red@Bank standing training interrupted."));
			}
		}
	}

	/**
	 * Reset all session data to prevent memory leaks
	 */
	private void resetSession() {
		isActive = false;
		sessionStartTime = 0;
		startLocation = null;
		lastXPTime = 0;
		lastActivityTime = 0;
		standingMinutes = 0;
		totalMovement = 0;
		ticksStanding = 0;
		sessionXPGained = 0;
		processTicks = 0;
	}

	/**
	 * Cleanup method to call on player logout - prevents memory leaks
	 */
	public void cleanup() {
		if (isActive) {
			forceStop();
		}
		resetSession();
		// Clear any potential references
		startLocation = null;
	}

	/**
	 * Static declaration method (no longer needed but kept for compatibility)
	 */
	public static void declare() {
		// No longer needed - each player instance handles itself
	}
}