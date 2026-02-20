package com.bestbudz.rs2.content;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.cluescroll.ClueScrollManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.impl.clickbuttons.ButtonAssignment;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Emotes {

	private static final Map<Integer, ProfessionCapeEmote> capeEmotes =
		new HashMap<Integer, ProfessionCapeEmote>();
	private static final int[] YOYO_ANIMATIONS = {1457, 1458, 1459, 1460};

	private static final Map<Stoner, EmoteTracker> activeEmotes = new ConcurrentHashMap<>();

	public static boolean clickButton(Stoner stoner, int id) {
		if (id == 115230) {
			handleProfessionCapeEmote(stoner);
			return true;
		}

		if (id == 115236) {
			startYoYoEmoteSequence(stoner);
			return true;
		}

		if (stoner.getEquipment().isWearingItem(10394)) {
			if (id == 166) {
				startEmoteTracking(stoner, 5316, -1);
				stoner.getUpdateFlags().sendAnimation(new Animation(5316));
				return true;
			}
		}

		for (ButtonAssignment.Emote i : ButtonAssignment.Emote.values()) {
			if (i.buttonID == id) {
				if (i.animID != 1) {
					ClueScrollManager.SINGLETON.handleEmote(stoner, i);
					startEmoteTracking(stoner, i.animID, i.gfxID);
					stoner.getUpdateFlags().sendAnimation(new Animation(i.animID));
				}
				if (i.gfxID != 1) stoner.getUpdateFlags().sendGraphic(Graphic.lowGraphic(i.gfxID, 0));
				return true;
			}
		}

		return false;
	}

	public static final void declare() {
		for (int i = 0; i < 20145; i++) {
			ItemDefinition def = GameDefinitionLoader.getItemDef(i);
			if ((def != null) && (def.getName() != null)) {
				String name = def.getName();

				if ((name.contains("Assault cape")) || (name.contains("Assault Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4959, 823));
				else if ((name.contains("Aegis cape")) || (name.contains("Aegis Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4961, 824));
				else if ((name.contains("Vigour cape")) || (name.contains("Vigour Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4981, 828));
				else if ((name.contains("Life cape")) || (name.contains("Life Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4971, 833));
				else if ((name.contains("Ranging cape")) || (name.contains("Sagittarius Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4973, 832));
				else if ((name.contains("Mage cape")) || (name.contains("Mage Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4939, 813));
				else if ((name.contains("Resonance cape")) || (name.contains("Resonance Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4979, 829));
				else if ((name.contains("Foodie cape")) || (name.contains("Foodie Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4955, 821));
				else if ((name.contains("Lumber. cape"))
					|| (name.contains("Lumbering cape"))
					|| (name.contains("Lumbering Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4957, 822));
				else if ((name.contains("Woodcarving cape")) || (name.contains("Woodcarving Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4937, 812));
				else if ((name.contains("Fisher cape")) || (name.contains("Fisher Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4951, 819));
				else if ((name.contains("Pyromaniac cape")) || (name.contains("Pyromaniac Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4975, 831));
				else if ((name.contains("Handiness cape")) || (name.contains("Handiness Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4949, 818));
				else if ((name.contains("Forging cape")) || (name.contains("Forging Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4943, 815));
				else if ((name.contains("Quarrying cape")) || (name.contains("Quarrying Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4941, 814));
				else if ((name.contains("THC-hempistry cape"))
					|| (name.contains("THC-hempistry Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4969, 835));
				else if ((name.contains("Weedsmoking cape")) || (name.contains("Weedsmoking Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4977, 830));
				else if ((name.contains("Starter cape"))
					|| (name.contains("Starter Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4965, 826));
				else if ((name.contains("Mercenary cape")) || (name.contains("Mercenary Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4967, 827));
				else if ((name.contains("BankStanding cape")) || (name.contains("BankStanding Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4963, 825));
				else if ((name.contains("Consumer cape")) || (name.contains("Consumer Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4947, 817));
				else if ((name.contains("Construct. cape")) || (name.contains("Construction Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4953, 820));
				else if ((name.contains("Summoning cape")) || (name.contains("Summoning Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(921, 281));
				else if ((name.contains("Hunter cape")) || (name.contains("Hunter Master Cape")))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(5158, 907));
				else if (name.contains("Quest point cape"))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4945, 816));
				else if (name.contains("Achievement diary cape"))
					capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(2709, 323));
			}
		}
	}

	public static void handleProfessionCapeEmote(Stoner stoner) {
		Item cape = stoner.getEquipment().getItems()[1];

		if (stoner.getCombat().inCombat()) {
			stoner
				.getClient()
				.queueOutgoingPacket(new SendMessage("You can't perform emotes whilst in combat."));
		}

		if (cape == null) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage("You need to be wearing a professioncape to do this."));
			return;
		}

		ProfessionCapeEmote emote = capeEmotes.get(Integer.valueOf(cape.getId()));

		if (emote != null) {
			startEmoteTracking(stoner, emote.getAnim(), emote.getGfx());
			stoner.getUpdateFlags().sendAnimation(emote.getAnim(), 0);
			stoner.getUpdateFlags().sendGraphic(Graphic.lowGraphic(emote.getGfx(), 0));
			AchievementHandler.activateAchievement(stoner, AchievementList.DO_A_PROFESSIONCAPE_EMOTE, 1);
		} else {
			stoner
				.getClient()
				.queueOutgoingPacket(new SendMessage("You must be wearing a professioncape to do this."));
		}
	}

	private static void startEmoteTracking(Stoner stoner, int animId, int gfxId) {

		EmoteTracker tracker = new EmoteTracker(stoner.getLocation(), animId, gfxId);
		activeEmotes.put(stoner, tracker);

		new Thread(() -> {
			while (activeEmotes.containsKey(stoner)) {
				try {
					Thread.sleep(100);

					EmoteTracker currentTracker = activeEmotes.get(stoner);
					if (currentTracker != null) {

						if (stoner.getLocation().getX() != currentTracker.startX ||
							stoner.getLocation().getY() != currentTracker.startY) {

							stoner.getUpdateFlags().sendAnimation(new Animation(-1));
							if (currentTracker.gfxId != -1) {
								stoner.getUpdateFlags().sendGraphic(Graphic.lowGraphic(-1, 0));
							}

							activeEmotes.remove(stoner);
							break;
						}
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		}).start();
	}

	private static void startYoYoEmoteSequence(Stoner stoner) {
		if (stoner.getCombat().inCombat()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You can't perform emotes whilst in combat."));
			return;
		}

		EmoteTracker tracker = new EmoteTracker(stoner.getLocation(), 1457, -1);
		activeEmotes.put(stoner, tracker);

		new Thread(() -> {
			java.util.Random random = new java.util.Random();

			while (activeEmotes.containsKey(stoner)) {
				try {
					EmoteTracker currentTracker = activeEmotes.get(stoner);
					if (currentTracker != null) {

						if (stoner.getLocation().getX() != currentTracker.startX ||
							stoner.getLocation().getY() != currentTracker.startY) {

							stoner.getUpdateFlags().sendAnimation(new Animation(-1));
							activeEmotes.remove(stoner);
							break;
						}

						int randomAnim = YOYO_ANIMATIONS[random.nextInt(YOYO_ANIMATIONS.length)];
						stoner.getUpdateFlags().sendAnimation(new Animation(randomAnim));

						Thread.sleep(1300 + random.nextInt(300));
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		}).start();
	}

	public static void stopEmoteTracking(Stoner stoner) {
		activeEmotes.remove(stoner);
	}

	public static void onLogin(Stoner p) {
		for (int i = 744; i <= 760; i++) p.getClient().queueOutgoingPacket(new SendConfig(i, 1));
	}

	private static class EmoteTracker {
		final int startX;
		final int startY;
		final int animId;
		final int gfxId;

		EmoteTracker(Location location, int animId, int gfxId) {
			this.startX = location.getX();
			this.startY = location.getY();
			this.animId = animId;
			this.gfxId = gfxId;
		}
	}

	private static class ProfessionCapeEmote {
		private final int anim;
		private final int gfx;

		public ProfessionCapeEmote(int anim, int gfx) {
			this.anim = anim;
			this.gfx = gfx;
		}

		public int getAnim() {
			return anim;
		}

		public int getGfx() {
			return gfx;
		}
	}
}
