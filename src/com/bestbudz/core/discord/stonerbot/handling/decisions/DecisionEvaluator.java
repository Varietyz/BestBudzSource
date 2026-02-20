package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DecisionEvaluator {

	private final DiscordBotStoner bot;
	private final DecisionState state;
	private final DecisionCombatManager combatManager;
	private final DecisionEmoteHandler emoteHandler;

	private static final int[] QUARRY_OBJECTS = {
		13708, 13709, 13712, 13713, 13710, 13711, 13714, 13706,
		13715, 13707, 13718, 13719, 13720, 14168, 14175,
		14912, 2491, 14856, 14855, 14854
	};

	private static final int[] TREE_OBJECTS = {
		11684, 11685,
		1276, 1277, 1278, 1279,
		1308, 1309, 5552, 5553,
		1307, 4674, 4675,
		1309, 7402, 7403,
		1306, 7401,
	};

	public DecisionEvaluator(DiscordBotStoner bot, DecisionState state, DecisionCombatManager combatManager, DecisionEmoteHandler emoteHandler) {
		this.bot = bot;
		this.state = state;
		this.combatManager = combatManager;
		this.emoteHandler = emoteHandler;
	}

	public String choosePriorityDecision(long currentTime) {

		if (needsToMoveAfterSkilling(currentTime)) {
			System.out.println("Decision: Move after skilling");
			return "move_after_skilling";
		}

		if (shouldForceReturnHome(currentTime)) {
			System.out.println("Decision: FORCED return home (been away too long)");
			state.setForceReturnHome(true);
			state.resetCombatSessionsWithoutReturn();
			return "return_home";
		}

		if (state.isEmoting()) {
			long emoteDuration = currentTime - state.getEmoteStartTime();
			if (emoteDuration < 5000) {
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				return "observe";
			}
		}

		List<RSObject> nearbyRocks = findNearbyQuarryingRocks();
		List<RSObject> nearbyTrees = findNearbyTrees();
		boolean inQuarryingArea = !nearbyRocks.isEmpty();
		boolean inLumberingArea = !nearbyTrees.isEmpty();

		if (inQuarryingArea && currentTime - state.getLastQuarryingSession() > 180000) {
			if (Math.random() < 0.25) {
				System.out.println("Decision: Start quarrying");
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				return "start_quarrying";
			}
		}

		if (inLumberingArea && currentTime - state.getLastLumberingSession() > 180000) {
			if (Math.random() < 0.25) {
				System.out.println("Decision: Start lumbering");
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				return "start_lumbering";
			}
		}

		if (combatManager.shouldSeekCombat(currentTime)) {
			System.out.println("Decision: Seek combat opportunities");
			state.incrementCombatSessionsWithoutReturn();
			return "seek_combat";
		}

		if (shouldReturnHome(currentTime)) {
			System.out.println("Decision: Regular return home");
			state.resetCombatSessionsWithoutReturn();
			return "return_home";
		}

		if (shouldExplore(currentTime)) {
			System.out.println("Decision: Long distance exploration");
			return "explore";
		}

		double movementChance = Math.random();
		if (movementChance < 0.25) {
			String moveType = chooseMovementType();
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			System.out.println("Decision: Movement - " + moveType);
			return moveType;
		}

		if (emoteHandler.canPerformEmote(currentTime)) {
			System.out.println("Decision: Emote");
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			return "emote";
		}

		if (currentTime - state.getLastStatusTime() > 120000 && Math.random() < 0.15) {
			System.out.println("Decision: Status update");
			return "status_update";
		}

		return "observe";
	}

	public boolean shouldForceReturnHome(long currentTime) {
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();

		boolean awayTooLong = (currentTime - state.getLastHomeReturnTime()) > 600000;
		boolean tooManyCombatSessions = state.getCombatSessionsWithoutReturn() >= 3;
		boolean veryFarFromHome = distanceFromHome > 40 && (currentTime - state.getLastHomeReturnTime()) > 300000;

		return awayTooLong || tooManyCombatSessions || veryFarFromHome;
	}

	public boolean shouldReturnHome(long currentTime) {
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();

		if (distanceFromHome > 25 && (currentTime - state.getLastHomeReturnTime()) > 300000) {
			if (Math.random() < 0.3) {
				return true;
			}
		}

		if (distanceFromHome > 15) {
			double returnChance = Math.min(0.4, distanceFromHome * 0.01);
			if (Math.random() < returnChance) {
				return true;
			}
		}

		return false;
	}

	public boolean shouldExplore(long currentTime) {

		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();

		if (distanceFromHome < 20 && (currentTime - state.getLastExploreTime()) > 480000) {
			if (Math.random() < 0.1) {
				return true;
			}
		}

		return false;
	}

	public boolean needsToMoveAfterSkilling(long currentTime) {

		boolean justStoppedQuarrying = !bot.getBotQuarrying().isQuarrying() &&
			(currentTime - state.getLastQuarryingSession()) < 10000 &&
			state.getLastQuarryingSession() > 0;

		boolean justStoppedLumbering = !bot.getBotLumbering().isLumbering() &&
			(currentTime - state.getLastLumberingSession()) < 10000 &&
			state.getLastLumberingSession() > 0;

		return justStoppedQuarrying || justStoppedLumbering;
	}

	public String chooseMovementType() {
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();
		long currentTime = System.currentTimeMillis();

		if (state.isForceReturnHome()) {
			state.setForceReturnHome(false);
			return "return_home";
		}

		if (distanceFromHome > 35) {
			return "return_home";
		} else if (distanceFromHome < 8 && (currentTime - state.getLastHomeReturnTime()) > 60000) {
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
				return "return_home";
			}
		}
	}

	public List<RSObject> findNearbyQuarryingRocks() {
		try {
			Set<RSObject> rocks = bot.getBotObjectHandler().findObjectsByType(
				bot.getLocation(), 12, QUARRY_OBJECTS);
			return new ArrayList<>(rocks);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

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
