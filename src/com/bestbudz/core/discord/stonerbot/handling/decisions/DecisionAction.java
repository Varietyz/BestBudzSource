package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import static com.bestbudz.core.discord.stonerbot.config.DiscordBotSpeech.statusMessages;
import java.util.Random;

/**
 * Executes bot decisions and actions
 */
public class DecisionAction {

	private final DiscordBotStoner bot;
	private final DecisionState state;
	private final DecisionCombatManager combatManager;
	private final DecisionEmoteHandler emoteHandler;
	private final Random random = new Random();

	public DecisionAction(DiscordBotStoner bot, DecisionState state, DecisionCombatManager combatManager, DecisionEmoteHandler emoteHandler) {
		this.bot = bot;
		this.state = state;
		this.combatManager = combatManager;
		this.emoteHandler = emoteHandler;
	}

	/**
	 * Execute the chosen decision with proper tracking
	 */
	public void executeDecision(String decision, long currentTime) {
		switch (decision) {
			case "start_quarrying":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotQuarrying().startQuarrying();
				bot.setCurrentActivity("starting quarrying");
				state.setLastQuarryingSession(currentTime);
				break;

			case "start_lumbering":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotLumbering().startLumbering();
				bot.setCurrentActivity("starting lumbering");
				state.setLastLumberingSession(currentTime);
				break;

			case "move_after_skilling":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotLocation().performRandomWalk(3, 1);
				bot.setCurrentActivity("moving after skilling");
				System.out.println("Bot moving to interrupt remaining skill tasks");
				break;

			case "return_home":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotLocation().moveTowardsHome();
				bot.setCurrentActivity("returning home");
				state.setLastHomeReturnTime(currentTime);
				state.resetCombatSessionsWithoutReturn(); // Reset combat session count
				break;

			case "explore":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotLocation().performRandomWalk(20, 8); // Increased range for exploration
				bot.setCurrentActivity("exploring");
				state.setLastExploreTime(currentTime);
				break;

			case "wander":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotLocation().performAreaWander();
				bot.setCurrentActivity("wandering");
				break;

			case "short_walk":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotLocation().performRandomWalk(5, 2);
				bot.setCurrentActivity("walking");
				break;

			case "emote":
				executeEmoteAction(currentTime);
				break;

			case "status_update":
				sendRandomStatusUpdate();
				state.setLastStatusTime(currentTime);
				break;

			// Seek combat with variety in areas
			case "seek_combat":
				emoteHandler.cancelEmoteIfActive();
				combatManager.startCombatSession(); // START combat session
				combatManager.seekCombatOpportunities();
				bot.setCurrentActivity("seeking combat");
				state.setLastCombatSession(currentTime);
				break;

			case "move_to_combat_area":
				emoteHandler.cancelEmoteIfActive();
				combatManager.startCombatSession(); // START combat session
				combatManager.moveTowardsCombatArea();
				bot.setCurrentActivity("moving to combat area");
				state.setLastCombatAreaMoveTime(currentTime);
				state.setLastCombatSession(currentTime); // Update combat session time
				break;

			case "observe":
			default:
				bot.setCurrentActivity("observing");
				break;
		}

		state.setLastActivity(decision);
	}

	/**
	 * Execute emote action with proper skilling object avoidance
	 */
	private void executeEmoteAction(long currentTime) {
		emoteHandler.cancelEmoteIfActive();

		// Check if near skilling objects and move away if needed
		if (isNearSkillingObjects()) {
			bot.getBotLocation().performRandomWalk(3, 1);
			bot.setCurrentActivity("moving away to emote");
			System.out.println("Bot moving away from skilling objects before emoting");
			// Schedule the actual emote to happen after movement
			scheduleDelayedEmote(currentTime);
		} else {
			// No skilling objects nearby, emote immediately
			emoteHandler.performRandomEmote();
			state.setLastEmoteTime(currentTime);
		}
	}

	/**
	 * Check if bot is near skilling objects (trees or rocks)
	 */
	private boolean isNearSkillingObjects() {
		try {
			// Check for nearby quarrying rocks
			java.util.List<com.bestbudz.core.cache.map.RSObject> nearbyRocks = findNearbyQuarryingRocks();
			if (!nearbyRocks.isEmpty()) {
				System.out.println("Found " + nearbyRocks.size() + " nearby quarrying rocks");
				return true;
			}

			// Check for nearby trees
			java.util.List<com.bestbudz.core.cache.map.RSObject> nearbyTrees = findNearbyTrees();
			if (!nearbyTrees.isEmpty()) {
				System.out.println("Found " + nearbyTrees.size() + " nearby trees");
				return true;
			}

			return false;
		} catch (Exception e) {
			System.out.println("Error checking nearby skilling objects: " + e.getMessage());
			return false; // If we can't check, assume no objects nearby
		}
	}

	/**
	 * Find nearby quarrying rocks using the same detection as DecisionEvaluator
	 */
	private java.util.List<com.bestbudz.core.cache.map.RSObject> findNearbyQuarryingRocks() {
		// Object IDs from DecisionEvaluator
		int[] QUARRY_OBJECTS = {
			13708, 13709, 13712, 13713, 13710, 13711, 13714, 13706,
			13715, 13707, 13718, 13719, 13720, 14168, 14175,
			14912, 2491, 14856, 14855, 14854
		};

		try {
			java.util.Set<com.bestbudz.core.cache.map.RSObject> rocks = bot.getBotObjectHandler().findObjectsByType(
				bot.getLocation(), 5, QUARRY_OBJECTS); // Use smaller radius (5) for emote check
			return new java.util.ArrayList<>(rocks);
		} catch (Exception e) {
			return new java.util.ArrayList<>();
		}
	}

	/**
	 * Find nearby trees using the same detection as DecisionEvaluator
	 */
	private java.util.List<com.bestbudz.core.cache.map.RSObject> findNearbyTrees() {
		// Object IDs from DecisionEvaluator
		int[] TREE_OBJECTS = {
			11684, 11685, // Normal trees
			1276, 1277, 1278, 1279, // Oak trees
			1308, 1309, 5552, 5553, // Willow trees
			1307, 4674, 4675, // Maple trees
			1309, 7402, 7403, // Yew trees
			1306, 7401, // Magic trees
		};

		try {
			java.util.Set<com.bestbudz.core.cache.map.RSObject> trees = bot.getBotObjectHandler().findObjectsByType(
				bot.getLocation(), 5, TREE_OBJECTS); // Use smaller radius (5) for emote check
			return new java.util.ArrayList<>(trees);
		} catch (Exception e) {
			return new java.util.ArrayList<>();
		}
	}

	/**
	 * Schedule an emote to happen after movement delay
	 */
	private void scheduleDelayedEmote(long currentTime) {
		// Schedule emote to happen after 2-3 seconds (enough time for movement)
		Thread delayedEmoteThread = new Thread(() -> {
			try {
				Thread.sleep(2500); // 2.5 second delay
				// Check if bot is still active and not doing other activities
				if (bot.isActive() && !bot.getBotQuarrying().isQuarrying() && !bot.getBotLumbering().isLumbering()) {
					emoteHandler.performRandomEmote();
					state.setLastEmoteTime(System.currentTimeMillis());
					System.out.println("Performed delayed emote after moving away from skilling objects");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("Delayed emote thread interrupted");
			}
		}, "DelayedEmoteThread");
		delayedEmoteThread.start();
	}

	/**
	 * Handle behavior during stationary periods
	 */
	public void handleStationaryBehavior(long currentTime) {
		if (emoteHandler.canPerformStationaryEmote(currentTime)) {
			// FIXED: Also check for skilling objects during stationary emotes
			if (isNearSkillingObjects()) {
				// Don't emote if near skilling objects during stationary period
				bot.setCurrentActivity("resting near objects");
			} else {
				emoteHandler.performStationaryEmote();
				state.setLastEmoteTime(currentTime);
			}
		} else {
			bot.setCurrentActivity("resting");
		}
	}

	/**
	 * Update current activity based on what bot is doing
	 */
	public void updateCurrentActivity() {
		if (bot.getBotQuarrying().isQuarrying()) {
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			bot.setCurrentActivity("quarrying");
		} else if (bot.getBotLumbering().isLumbering()) {
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			bot.setCurrentActivity("lumbering");
		}
	}

	/**
	 * Send a random status update
	 */
	private void sendRandomStatusUpdate() {
		try {
			String message = statusMessages[random.nextInt(statusMessages.length)];
			bot.getActions().sendAutonomousStatusUpdate(message);
			bot.setCurrentActivity("sharing thoughts");
		} catch (Exception e) {
			System.out.println("Error sending status update: " + e.getMessage());
		}
	}
}