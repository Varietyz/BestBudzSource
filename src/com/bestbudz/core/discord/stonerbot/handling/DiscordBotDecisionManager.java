package com.bestbudz.core.discord.stonerbot.handling;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.handling.decisions.DecisionAction;
import com.bestbudz.core.discord.stonerbot.handling.decisions.DecisionCombatManager;
import com.bestbudz.core.discord.stonerbot.handling.decisions.DecisionEmoteHandler;
import com.bestbudz.core.discord.stonerbot.handling.decisions.DecisionEvaluator;
import com.bestbudz.core.discord.stonerbot.handling.decisions.DecisionState;

public class DiscordBotDecisionManager {

	private final DiscordBotStoner bot;

	private final DecisionState state;
	private final DecisionCombatManager combatManager;
	private final DecisionEmoteHandler emoteHandler;
	private final DecisionEvaluator decisionEvaluator;
	private final DecisionAction actionExecutor;

	public DiscordBotDecisionManager(DiscordBotStoner bot) {
		this.bot = bot;

		this.state = new DecisionState();
		this.combatManager = new DecisionCombatManager(bot, state);
		this.emoteHandler = new DecisionEmoteHandler(bot, state);
		this.decisionEvaluator = new DecisionEvaluator(bot, state, combatManager, emoteHandler);
		this.actionExecutor = new DecisionAction(bot, state, combatManager, emoteHandler);
	}

	public void makeDecision() {
		try {
			long currentTime = System.currentTimeMillis();

			emoteHandler.updateEmoteState();

			if (bot.getBotQuarrying().isQuarrying() || bot.getBotLumbering().isLumbering()) {
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				actionExecutor.updateCurrentActivity();
				return;
			}

			if (bot.isInStationaryPeriod()) {
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				actionExecutor.handleStationaryBehavior(currentTime);
				return;
			}

			if (bot.getBotLocation().isMoving()) {
				if (state.isCombatSessionActive()) combatManager.endCombatSession();
				return;
			}

			if (combatManager.shouldEndCombatSession(currentTime)) {
				combatManager.endCombatSession();
			}

			String decision = decisionEvaluator.choosePriorityDecision(currentTime);
			actionExecutor.executeDecision(decision, currentTime);

		} catch (Exception e) {
			System.out.println("Error in decision making: " + e.getMessage());
		}
	}

	public String getDecisionStats() {
		long currentTime = System.currentTimeMillis();
		long timeSinceQuarrying = currentTime - state.getLastQuarryingSession();
		long timeSinceLumbering = currentTime - state.getLastLumberingSession();
		long timeSinceCombat = currentTime - state.getLastCombatSession();
		long timeSinceCombatMove = currentTime - state.getLastCombatAreaMoveTime();
		long timeSinceHome = currentTime - state.getLastHomeReturnTime();
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();

		return String.format("Last quarrying: %ds ago, Last lumbering: %ds ago, Last combat seeking: %ds ago, Last combat move: %ds ago, Last home return: %ds ago, Distance from home: %d tiles, Combat sessions without return: %d, Last activity: %s",
			timeSinceQuarrying / 1000, timeSinceLumbering / 1000, timeSinceCombat / 1000,
			timeSinceCombatMove / 1000, timeSinceHome / 1000, distanceFromHome,
			state.getCombatSessionsWithoutReturn(), state.getLastActivity());
	}

	public void resetHomeTracking() {
		state.resetHomeTracking();
		System.out.println("Home tracking reset - bot considers itself as having just returned home");
	}

	public String getHomeStatus() {
		long timeSinceHome = System.currentTimeMillis() - state.getLastHomeReturnTime();
		int distanceFromHome = bot.getBotLocation().getDistanceFromHome();
		boolean shouldForceReturn = decisionEvaluator.shouldForceReturnHome(System.currentTimeMillis());

		return String.format("Distance from home: %d tiles, Time since home: %ds, Combat sessions: %d, Should force return: %s",
			distanceFromHome, timeSinceHome / 1000, state.getCombatSessionsWithoutReturn(), shouldForceReturn);
	}

	public void debugNpcVisibility() {
		combatManager.debugNpcVisibility();
	}

	public boolean isCurrentlyEmoting() {
		return emoteHandler.isCurrentlyEmoting();
	}

	public void forceCancel() {
		emoteHandler.forceCancel();
	}

	public DecisionState getState() { return state; }
	public DecisionCombatManager getCombatManager() { return combatManager; }
	public DecisionEmoteHandler getEmoteHandler() { return emoteHandler; }
	public DecisionEvaluator getDecisionEvaluator() { return decisionEvaluator; }
	public DecisionAction getActionExecutor() { return actionExecutor; }
}
