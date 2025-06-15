package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.Animation;
import java.util.Random;

/**
 * Handles emote functionality for the Discord bot
 */
public class DecisionEmoteHandler {

	private final DiscordBotStoner bot;
	private final DecisionState state;
	private final Random random = new Random();

	// Emote animations
	private static final int[] REGULAR_EMOTES = {857, 4276, 6380, 1670, 863, 861, 2339, 865, 862, 2112, 1368, 884};
	private static final int[] STATIONARY_EMOTES = {863, 861, 2339, 857, 1368};

	public DecisionEmoteHandler(DiscordBotStoner bot, DecisionState state) {
		this.bot = bot;
		this.state = state;
	}

	/**
	 * Update emote state - call this in main decision loop to auto-clear emote flag
	 */
	public void updateEmoteState() {
		if (state.isEmoting()) {
			long emoteDuration = System.currentTimeMillis() - state.getEmoteStartTime();

			// Auto-clear emote after timeout
			if (emoteDuration > 15000) { // 15 seconds
				state.setEmoting(false);
				System.out.println("Emote automatically completed after " + emoteDuration + "ms (timeout)");
			}
		}
	}

	/**
	 * Check if emote can be performed
	 */
	public boolean canPerformEmote(long currentTime) {
		return !state.isEmoting() &&
			currentTime - state.getLastEmoteTime() > 45000 &&
			Math.random() < 0.20;
	}

	/**
	 * Check if stationary emote can be performed
	 */
	public boolean canPerformStationaryEmote(long currentTime) {
		return currentTime - state.getLastEmoteTime() > 20000 &&
			Math.random() < 0.4;
	}

	/**
	 * Perform a random emote
	 */
	public void performRandomEmote() {
		try {
			int randomEmote = REGULAR_EMOTES[random.nextInt(REGULAR_EMOTES.length)];
			performEmote(randomEmote, "emoting");
		} catch (Exception e) {
			System.out.println("Error performing emote: " + e.getMessage());
		}
	}

	/**
	 * Perform stationary emote
	 */
	public void performStationaryEmote() {
		try {
			int randomEmote = STATIONARY_EMOTES[random.nextInt(STATIONARY_EMOTES.length)];
			performEmote(randomEmote, "resting emote");
		} catch (Exception e) {
			System.out.println("Error performing stationary emote: " + e.getMessage());
		}
	}

	/**
	 * Internal method to perform an emote
	 */
	private void performEmote(int emoteId, String activity) {
		bot.getUpdateFlags().sendAnimation(new Animation(emoteId));
		bot.setCurrentActivity(activity);

		// Set emote state
		state.setEmoting(true);
		state.setEmoteStartTime(System.currentTimeMillis());
		System.out.println("Bot started emote: " + emoteId);
	}

	/**
	 * Cancel emote animation if currently emoting
	 */
	public void cancelEmoteIfActive() {
		if (state.isEmoting()) {
			try {
				bot.getUpdateFlags().sendAnimation(new Animation(-1)); // Cancel animation
				state.setEmoting(false);
				long emoteDuration = System.currentTimeMillis() - state.getEmoteStartTime();
				System.out.println("Cancelled emote after " + emoteDuration + "ms to allow movement");
			} catch (Exception e) {
				System.out.println("Error cancelling emote: " + e.getMessage());
			}
		}
	}

	/**
	 * Force cancel any active emote (for external use)
	 */
	public void forceCancel() {
		if (state.isEmoting()) {
			cancelEmoteIfActive();
			System.out.println("Emote force cancelled by external request");
		}
	}

	/**
	 * Check if bot is currently emoting (for external use)
	 */
	public boolean isCurrentlyEmoting() {
		return state.isEmoting();
	}

	/**
	 * Check if emote should be cancelled for priority actions
	 */
	public boolean shouldCancelForPriorityAction(boolean needsToMoveAfterSkilling) {
		if (!state.isEmoting()) {
			return false;
		}

		long emoteDuration = System.currentTimeMillis() - state.getEmoteStartTime();

		// Force cancel emote if we need to move after skilling and emote has been going for at least 1 second
		if (needsToMoveAfterSkilling && emoteDuration > 1000) {
			cancelEmoteIfActive();
			System.out.println("Force cancelled emote for priority movement after " + emoteDuration + "ms");
			return true;
		}

		return false;
	}
}