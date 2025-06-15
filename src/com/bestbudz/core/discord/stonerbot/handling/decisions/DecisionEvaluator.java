package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluates and prioritizes bot decisions
 */
public class DecisionEvaluator {

	private final DiscordBotStoner bot;
	private final DecisionState state;
	private final DecisionCombatManager combatManager;
	private final DecisionEmoteHandler emoteHandler;

	// Object detection arrays
	private static final int[] QUARRY_OBJECTS = {
		13708, 13709, 13712, 13713, 13710, 13711, 13714, 13706,
		13715, 13707, 13718, 13719, 13720, 14168, 14175,
		14912, 2491, 14856, 14855, 14854
	};

	private static final int[] TREE_OBJECTS = {
		11684, 11685, // Normal trees
		1276, 1277, 1278, 1279, // Oak trees
		1308, 1309, 5552, 5553, // Willow trees
		1307, 4674, 4675, // Maple trees
		1309, 7402, 7403, // Yew trees
		1306, 7401, // Magic trees
	};

	public DecisionEvaluator(DiscordBotStoner bot, DecisionState state, DecisionCombatManager combatManager, DecisionEmoteHandler emoteHandler) {
		this.bot = bot;
		this.state = state;
		this.combatManager = combatManager;
		this.emoteHandler = emoteHandler;
	}

	/**
	 * Priority-based decision making with proper home return and combat variety
	 */
	public String choosePriorityDecision(long currentTime) {
		// PRIORITY 1: Check if we just finished a skill session and need to move away
		if (needsToMoveAfterSkilling(currentTime)) {
			System.out.println("Decision: Move after skilling");
			return "move_after_skilling";
		}

		// PRIORITY 2: FORCED home return after too much combat or being away too long
		if (shouldForceReturnHome(currentTime)) {
			System.out.println("Decision: FORCED return home (been away too long)");
			state.setForceReturnHome(true);
			state.resetCombatSessionsWithoutReturn();
			return "return_home";
		}

		// Don't start new activities if currently emoting (short duration check)
		if (state.isEmoting()) {
			long emoteDuration = currentTime - state.getEmoteStartTime();
			if (emoteDuration < 5000) { // Only wait 5 seconds for emotes
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				return "observe";
			}
		}

		// PRIORITY 3: Check if near quarrying or lumbering areas
		List<RSObject> nearbyRocks = findNearbyQuarryingRocks();
		List<RSObject> nearbyTrees = findNearbyTrees();
		boolean inQuarryingArea = !nearbyRocks.isEmpty();
		boolean inLumberingArea = !nearbyTrees.isEmpty();

		// PRIORITY 4: Skill activity decisions (higher chance if in appropriate area)
		if (inQuarryingArea && currentTime - state.getLastQuarryingSession() > 180000) { // 3 minutes
			if (Math.random() < 0.25) { // 25% chance
				System.out.println("Decision: Start quarrying");
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				return "start_quarrying";
			}
		}

		if (inLumberingArea && currentTime - state.getLastLumberingSession() > 180000) { // 3 minutes
			if (Math.random() < 0.25) { // 25% chance
				System.out.println("Decision: Start lumbering");
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				return "start_lumbering";
			}
		}

		// PRIORITY 5: Combat seeking decision - but with limits and variety
		if (combatManager.shouldSeekCombat(currentTime)) {
			System.out.println("Decision: Seek combat opportunities");
			state.incrementCombatSessionsWithoutReturn();
			return "seek_combat";
		}

		// PRIORITY 6: Regular home return (not forced)
		if (shouldReturnHome(currentTime)) {
			System.out.println("Decision: Regular return home");
			state.resetCombatSessionsWithoutReturn();
			return "return_home";
		}

		// PRIORITY 7: Long distance exploration
		if (shouldExplore(currentTime)) {
			System.out.println("Decision: Long distance exploration");
			return "explore";
		}

		// PRIORITY 8: Movement decisions
		double movementChance = Math.random();
		if (movementChance < 0.25) { // 25% chance
			String moveType = chooseMovementType();
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			System.out.println("Decision: Movement - " + moveType);
			return moveType;
		}

		// PRIORITY 9: Emote decision
		if (emoteHandler.canPerformEmote(currentTime)) {
			System.out.println("Decision: Emote");
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			return "emote";
		}

		// PRIORITY 10: Status update
		if (currentTime - state.getLastStatusTime() > 120000 && Math.random() < 0.15) {
			System.out.println("Decision: Status update");
			return "status_update";
		}

		return "observe";
	}

	/**
	 * Check if bot should be forced to return home
	 */
	public boolean shouldForceReturnHome(long currentTime) {
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();

		// Force return if:
		// 1. Been away from home for more than 10 minutes
		// 2. Had 3+ combat sessions without returning home
		// 3. Very far from home (>40 tiles) for more than 5 minutes

		boolean awayTooLong = (currentTime - state.getLastHomeReturnTime()) > 600000; // 10 minutes
		boolean tooManyCombatSessions = state.getCombatSessionsWithoutReturn() >= 3;
		boolean veryFarFromHome = distanceFromHome > 40 && (currentTime - state.getLastHomeReturnTime()) > 300000; // 5 minutes

		return awayTooLong || tooManyCombatSessions || veryFarFromHome;
	}

	/**
	 * Check if bot should return home (regular, non-forced)
	 */
	public boolean shouldReturnHome(long currentTime) {
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();

		// Return home if:
		// 1. Far from home and haven't been home recently
		// 2. Random chance increases with distance from home

		if (distanceFromHome > 25 && (currentTime - state.getLastHomeReturnTime()) > 300000) { // 5 minutes
			if (Math.random() < 0.3) { // 30% chance
				return true;
			}
		}

		// Distance-based return chance
		if (distanceFromHome > 15) {
			double returnChance = Math.min(0.4, distanceFromHome * 0.01); // Max 40% chance
			if (Math.random() < returnChance) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if bot should do long-distance exploration
	 */
	public boolean shouldExplore(long currentTime) {
		// Explore if haven't explored recently and not too far from home
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();

		if (distanceFromHome < 20 && (currentTime - state.getLastExploreTime()) > 480000) { // 8 minutes
			if (Math.random() < 0.1) { // 10% chance
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if bot just finished skilling and needs to move away to interrupt tasks
	 */
	public boolean needsToMoveAfterSkilling(long currentTime) {
		// Check if we just stopped quarrying/lumbering (within last 10 seconds)
		boolean justStoppedQuarrying = !bot.getBotQuarrying().isQuarrying() &&
			(currentTime - state.getLastQuarryingSession()) < 10000 &&
			state.getLastQuarryingSession() > 0;

		boolean justStoppedLumbering = !bot.getBotLumbering().isLumbering() &&
			(currentTime - state.getLastLumberingSession()) < 10000 &&
			state.getLastLumberingSession() > 0;

		// If we just stopped skilling, we need to move to interrupt any remaining tasks
		return justStoppedQuarrying || justStoppedLumbering;
	}

	/**
	 * Choose movement type based on distance from home and recent activities
	 */
	public String chooseMovementType() {
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();
		long currentTime = System.currentTimeMillis();

		// If force return home is set, always return home
		if (state.isForceReturnHome()) {
			state.setForceReturnHome(false);
			return "return_home";
		}

		if (distanceFromHome > 35) {
			return "return_home";
		} else if (distanceFromHome < 8 && (currentTime - state.getLastHomeReturnTime()) > 60000) { // 1 minute since home
			return Math.random() < 0.6 ? "explore" : "wander";
		} else {
			double moveType = Math.random();
			if (moveType < 0.3) {
				return "short_walk";
			} else if (moveType < 0.6) {
				return "wander";
			} else if (moveType < 0.8) {
				return "explore";
			} else {
				return "return_home"; // Increased chance to return home
			}
		}
	}

	/**
	 * Find nearby quarrying rocks
	 */
	public List<RSObject> findNearbyQuarryingRocks() {
		try {
			Set<RSObject> rocks = bot.getBotObjectHandler().findObjectsByType(
				bot.getLocation(), 12, QUARRY_OBJECTS);
			return new ArrayList<>(rocks);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	/**
	 * Find nearby trees
	 */
	public List<RSObject> findNearbyTrees() {
		try {
			Set<RSObject> trees = bot.getBotObjectHandler().findObjectsByType(
				bot.getLocation(), 12, TREE_OBJECTS);
			return new ArrayList<>(trees);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
}