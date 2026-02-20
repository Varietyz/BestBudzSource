package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import static com.bestbudz.core.discord.stonerbot.config.DiscordBotSpeech.statusMessages;
import java.util.Random;

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
				state.resetCombatSessionsWithoutReturn();
				break;

			case "explore":
				emoteHandler.cancelEmoteIfActive();
				bot.getBotLocation().performRandomWalk(20, 8);
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

			case "seek_combat":
				emoteHandler.cancelEmoteIfActive();
				combatManager.startCombatSession();
				combatManager.seekCombatOpportunities();
				bot.setCurrentActivity("seeking combat");
				state.setLastCombatSession(currentTime);
				break;

			case "move_to_combat_area":
				emoteHandler.cancelEmoteIfActive();
				combatManager.startCombatSession();
				combatManager.moveTowardsCombatArea();
				bot.setCurrentActivity("moving to combat area");
				state.setLastCombatAreaMoveTime(currentTime);
				state.setLastCombatSession(currentTime);
				break;

			case "observe":
			default:
				bot.setCurrentActivity("observing");
				break;
		}

		state.setLastActivity(decision);
	}

	private void executeEmoteAction(long currentTime) {
		emoteHandler.cancelEmoteIfActive();

		if (isNearSkillingObjects()) {
			bot.getBotLocation().performRandomWalk(3, 1);
			bot.setCurrentActivity("moving away to emote");
			System.out.println("Bot moving away from skilling objects before emoting");

			scheduleDelayedEmote(currentTime);
		} else {

			emoteHandler.performRandomEmote();
			state.setLastEmoteTime(currentTime);
		}
	}

	private boolean isNearSkillingObjects() {
		try {

			java.util.List<com.bestbudz.core.cache.map.RSObject> nearbyRocks = findNearbyQuarryingRocks();
			if (!nearbyRocks.isEmpty()) {
				System.out.println("Found " + nearbyRocks.size() + " nearby quarrying rocks");
				return true;
			}

			java.util.List<com.bestbudz.core.cache.map.RSObject> nearbyTrees = findNearbyTrees();
			if (!nearbyTrees.isEmpty()) {
				System.out.println("Found " + nearbyTrees.size() + " nearby trees");
				return true;
			}

			return false;
		} catch (Exception e) {
			System.out.println("Error checking nearby skilling objects: " + e.getMessage());
			return false;
		}
	}

	private java.util.List<com.bestbudz.core.cache.map.RSObject> findNearbyQuarryingRocks() {

		int[] QUARRY_OBJECTS = {
			13708, 13709, 13712, 13713, 13710, 13711, 13714, 13706,
			13715, 13707, 13718, 13719, 13720, 14168, 14175,
			14912, 2491, 14856, 14855, 14854
		};

		try {
			java.util.Set<com.bestbudz.core.cache.map.RSObject> rocks = bot.getBotObjectHandler().findObjectsByType(
				bot.getLocation(), 5, QUARRY_OBJECTS);
			return new java.util.ArrayList<>(rocks);
		} catch (Exception e) {
			return new java.util.ArrayList<>();
		}
	}

	private java.util.List<com.bestbudz.core.cache.map.RSObject> findNearbyTrees() {

		int[] TREE_OBJECTS = {
			11684, 11685,
			1276, 1277, 1278, 1279,
			1308, 1309, 5552, 5553,
			1307, 4674, 4675,
			1309, 7402, 7403,
			1306, 7401,
		};

		try {
			java.util.Set<com.bestbudz.core.cache.map.RSObject> trees = bot.getBotObjectHandler().findObjectsByType(
				bot.getLocation(), 5, TREE_OBJECTS);
			return new java.util.ArrayList<>(trees);
		} catch (Exception e) {
			return new java.util.ArrayList<>();
		}
	}

	private void scheduleDelayedEmote(long currentTime) {

		Thread delayedEmoteThread = new Thread(() -> {
			try {
				Thread.sleep(2500);

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

	public void handleStationaryBehavior(long currentTime) {
		if (emoteHandler.canPerformStationaryEmote(currentTime)) {

			if (isNearSkillingObjects()) {

				bot.setCurrentActivity("resting near objects");
			} else {
				emoteHandler.performStationaryEmote();
				state.setLastEmoteTime(currentTime);
			}
		} else {
			bot.setCurrentActivity("resting");
		}
	}

	public void updateCurrentActivity() {
		if (bot.getBotQuarrying().isQuarrying()) {
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			bot.setCurrentActivity("quarrying");
		} else if (bot.getBotLumbering().isLumbering()) {
			if (state.isCombatSessionActive()) combatManager.endCombatSession();
			bot.setCurrentActivity("lumbering");
		}
	}

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
