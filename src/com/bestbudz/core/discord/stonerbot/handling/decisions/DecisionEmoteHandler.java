package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.Animation;
import java.util.Random;

public class DecisionEmoteHandler {

	private final DiscordBotStoner bot;
	private final DecisionState state;
	private final Random random = new Random();

	private static final int[] REGULAR_EMOTES = {857, 4276, 6380, 1670, 863, 861, 2339, 865, 862, 2112, 1368, 884};
	private static final int[] STATIONARY_EMOTES = {863, 861, 2339, 857, 1368};

	public DecisionEmoteHandler(DiscordBotStoner bot, DecisionState state) {
		this.bot = bot;
		this.state = state;
	}

	public void updateEmoteState() {
		if (state.isEmoting()) {
			long emoteDuration = System.currentTimeMillis() - state.getEmoteStartTime();

			if (emoteDuration > 15000) {
				state.setEmoting(false);
				System.out.println("Emote automatically completed after " + emoteDuration + "ms (timeout)");
			}
		}
	}

	public boolean canPerformEmote(long currentTime) {
		return !state.isEmoting() &&
			currentTime - state.getLastEmoteTime() > 45000 &&
			Math.random() < 0.20;
	}

	public boolean canPerformStationaryEmote(long currentTime) {
		return currentTime - state.getLastEmoteTime() > 20000 &&
			Math.random() < 0.4;
	}

	public void performRandomEmote() {
		try {
			int randomEmote = REGULAR_EMOTES[random.nextInt(REGULAR_EMOTES.length)];
			performEmote(randomEmote, "emoting");
		} catch (Exception e) {
			System.out.println("Error performing emote: " + e.getMessage());
		}
	}

	public void performStationaryEmote() {
		try {
			int randomEmote = STATIONARY_EMOTES[random.nextInt(STATIONARY_EMOTES.length)];
			performEmote(randomEmote, "resting emote");
		} catch (Exception e) {
			System.out.println("Error performing stationary emote: " + e.getMessage());
		}
	}

	private void performEmote(int emoteId, String activity) {
		bot.getUpdateFlags().sendAnimation(new Animation(emoteId));
		bot.setCurrentActivity(activity);

		state.setEmoting(true);
		state.setEmoteStartTime(System.currentTimeMillis());
		System.out.println("Bot started emote: " + emoteId);
	}

	public void cancelEmoteIfActive() {
		if (state.isEmoting()) {
			try {
				bot.getUpdateFlags().sendAnimation(new Animation(-1));
				state.setEmoting(false);
				long emoteDuration = System.currentTimeMillis() - state.getEmoteStartTime();
				System.out.println("Cancelled emote after " + emoteDuration + "ms to allow movement");
			} catch (Exception e) {
				System.out.println("Error cancelling emote: " + e.getMessage());
			}
		}
	}

	public void forceCancel() {
		if (state.isEmoting()) {
			cancelEmoteIfActive();
			System.out.println("Emote force cancelled by external request");
		}
	}

	public boolean isCurrentlyEmoting() {
		return state.isEmoting();
	}

	public boolean shouldCancelForPriorityAction(boolean needsToMoveAfterSkilling) {
		if (!state.isEmoting()) {
			return false;
		}

		long emoteDuration = System.currentTimeMillis() - state.getEmoteStartTime();

		if (needsToMoveAfterSkilling && emoteDuration > 1000) {
			cancelEmoteIfActive();
			System.out.println("Force cancelled emote for priority movement after " + emoteDuration + "ms");
			return true;
		}

		return false;
	}
}
