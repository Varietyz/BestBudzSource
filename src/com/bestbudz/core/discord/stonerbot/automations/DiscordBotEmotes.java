package com.bestbudz.core.discord.stonerbot.automations;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class DiscordBotEmotes {
	private static final Logger logger = Logger.getLogger(DiscordBotEmotes.class.getSimpleName());

	private static final long MIN_EMOTE_INTERVAL = 30000;
	private static final long MAX_EMOTE_INTERVAL = 300000;
	private static final long LOOP_EMOTE_DURATION = 60000;
	private static final long YOYO_ANIMATION_DELAY = 1500;
	private static final int[] YOYO_ANIMATIONS = {1457, 1458, 1459, 1460};

	private final DiscordBotStoner bot;
	private final Random random = new Random();

	private final AtomicBoolean emotesEnabled = new AtomicBoolean(true);
	private final AtomicBoolean isPerformingEmote = new AtomicBoolean(false);
	private final AtomicLong lastEmoteTime = new AtomicLong(0);
	private final AtomicLong nextEmoteTime = new AtomicLong(0);

	private volatile EmoteType currentEmoteType = EmoteType.NONE;
	private volatile Location emoteStartLocation = null;
	private volatile Thread emoteThread = null;

	public DiscordBotEmotes(DiscordBotStoner bot) {
		this.bot = bot;
		scheduleNextEmote();
	}

	public void update() {
		if (!emotesEnabled.get() || !bot.isActive()) {
			return;
		}

		long currentTime = System.currentTimeMillis();

		if (currentTime >= nextEmoteTime.get() && !isPerformingEmote.get()) {
			performRandomEmote();
		}

		if (isPerformingEmote.get() && hasMovedFromEmoteStart()) {
			stopCurrentEmote();
		}
	}

	private void performRandomEmote() {
		if (bot.getCombat().inCombat()) {
			scheduleNextEmote();
			return;
		}

		emoteStartLocation = new Location(bot.getLocation());

		EmoteType[] emoteTypes = EmoteType.values();
		EmoteType chosenEmote = emoteTypes[random.nextInt(emoteTypes.length - 1)];

		logger.info("Discord bot performing emote: " + chosenEmote.name());

		switch (chosenEmote) {
			case QUICK_EMOTE:
				performQuickEmote();
				break;
			case CHILL_SITTING:
				performChillSitting();
				break;
			case YOYO_LOOP:
				performYoYoLoop();
				break;
		}

		lastEmoteTime.set(System.currentTimeMillis());
		scheduleNextEmote();
	}

	private void performQuickEmote() {
		QuickEmote[] quickEmotes = QuickEmote.values();
		QuickEmote chosen = quickEmotes[random.nextInt(quickEmotes.length)];

		isPerformingEmote.set(true);
		currentEmoteType = EmoteType.QUICK_EMOTE;

		bot.getUpdateFlags().sendAnimation(new Animation(chosen.animId));
		if (chosen.gfxId != -1) {
			bot.getUpdateFlags().sendGraphic(Graphic.lowGraphic(chosen.gfxId, 0));
		}

		emoteThread = new Thread(() -> {
			try {
				Thread.sleep(chosen.duration);
				if (currentEmoteType == EmoteType.QUICK_EMOTE) {
					stopCurrentEmote();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, "BotQuickEmote");
		emoteThread.start();
	}

	private void performChillSitting() {
		isPerformingEmote.set(true);
		currentEmoteType = EmoteType.CHILL_SITTING;

		bot.getUpdateFlags().sendAnimation(new Animation(2339));

		emoteThread = new Thread(() -> {
			try {
				Thread.sleep(LOOP_EMOTE_DURATION);
				if (currentEmoteType == EmoteType.CHILL_SITTING) {
					stopCurrentEmote();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, "BotChillSitting");
		emoteThread.start();
	}

	private void performYoYoLoop() {
		isPerformingEmote.set(true);
		currentEmoteType = EmoteType.YOYO_LOOP;

		emoteThread = new Thread(() -> {
			long startTime = System.currentTimeMillis();

			while (currentEmoteType == EmoteType.YOYO_LOOP &&
				System.currentTimeMillis() - startTime < LOOP_EMOTE_DURATION) {
				try {

					if (hasMovedFromEmoteStart()) {
						break;
					}

					int randomAnim = YOYO_ANIMATIONS[random.nextInt(YOYO_ANIMATIONS.length)];
					bot.getUpdateFlags().sendAnimation(new Animation(randomAnim));

					Thread.sleep(YOYO_ANIMATION_DELAY + random.nextInt(500));

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}

			if (currentEmoteType == EmoteType.YOYO_LOOP) {
				stopCurrentEmote();
			}
		}, "BotYoYoLoop");
		emoteThread.start();
	}

	private void stopCurrentEmote() {
		if (!isPerformingEmote.get()) {
			return;
		}

		bot.getUpdateFlags().sendAnimation(new Animation(-1));
		bot.getUpdateFlags().sendGraphic(Graphic.lowGraphic(-1, 0));

		isPerformingEmote.set(false);
		currentEmoteType = EmoteType.NONE;
		emoteStartLocation = null;

		if (emoteThread != null && emoteThread.isAlive()) {
			emoteThread.interrupt();
		}
	}

	private boolean hasMovedFromEmoteStart() {
		if (emoteStartLocation == null) {
			return false;
		}

		Location currentLoc = bot.getLocation();
		return currentLoc.getX() != emoteStartLocation.getX() ||
			currentLoc.getY() != emoteStartLocation.getY();
	}

	private void scheduleNextEmote() {
		long delay = MIN_EMOTE_INTERVAL + random.nextInt((int)(MAX_EMOTE_INTERVAL - MIN_EMOTE_INTERVAL));
		nextEmoteTime.set(System.currentTimeMillis() + delay);
	}

	public void setEmotesEnabled(boolean enabled) {
		emotesEnabled.set(enabled);
		if (!enabled && isPerformingEmote.get()) {
			stopCurrentEmote();
		}
	}

	public boolean areEmotesEnabled() {
		return emotesEnabled.get();
	}

	public boolean isPerformingEmote() {
		return isPerformingEmote.get();
	}

	public String getEmoteStatus() {
		if (!emotesEnabled.get()) {
			return "Emotes disabled";
		}

		if (isPerformingEmote.get()) {
			return "Currently performing: " + currentEmoteType.name();
		}

		long timeUntilNext = nextEmoteTime.get() - System.currentTimeMillis();
		if (timeUntilNext > 0) {
			return "Next emote in: " + (timeUntilNext / 1000) + " seconds";
		}

		return "Ready for emote";
	}

	public void shutdown() {
		emotesEnabled.set(false);
		stopCurrentEmote();
	}

	private enum EmoteType {
		NONE,
		QUICK_EMOTE,
		CHILL_SITTING,
		YOYO_LOOP
	}

	private enum QuickEmote {
		WAVE(863, -1, 3000),
		DANCE(866, -1, 4000),
		LAUGH(861, -1, 3000),
		CHEER(862, -1, 3000),
		CLAP(865, -1, 3000),
		THINK(857, -1, 3000),
		JUMP_FOR_JOY(2109, -1, 3000),
		TWIRL(2107, -1, 3000),
		HEADBANG(2108, -1, 4000),
		BLOW_KISS(1368, -1, 3000),
		SALUTE(2112, -1, 3000),
		IDEA(4276, 712, 3000),
		STOMP(4278, -1, 3000),
		FLAP(4280, -1, 3000),
		BUNNY_HOP(6111, -1, 3000),
		CHICKEN(1835, -1, 4000),
		JUMPING_JACKS(2761, -1, 5000),
		PUSHUP(2762, -1, 5000),
		SITUP(2763, -1, 5000),
		JOGGING(2764, -1, 5000),
		ELEGANT_BOW(5312, -1, 3000),
		ADVANCED_YAWN(5313, -1, 3000),
		SMOKE(884, 354, 4000),
		PREACH(1670, -1, 4000),
		MATRIX(1110, -1, 3000),
		RESPECT(1818, -1, 3000),
		JUMP(3067, -1, 2000),
		EXCITED_JUMP(6382, -1, 3000),
		THINK_HARD(6380, -1, 4000);

		final int animId;
		final int gfxId;
		final long duration;

		QuickEmote(int animId, int gfxId, long duration) {
			this.animId = animId;
			this.gfxId = gfxId;
			this.duration = duration;
		}
	}
}
