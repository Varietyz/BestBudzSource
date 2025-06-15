package com.bestbudz.rs2.content.profession.weedsmoking;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class Weedsmoker {

	public static final String SMOKE_WITH_BUD_KEY = "Smokeweedwithbud";

	public static boolean burnin(Stoner stoner, int id, int slot, int interfaceId) {
		Weed weed = Weed.forId(id);

		if (weed == null) {
			return false;
		}


		if (System.currentTimeMillis() - stoner.getCurrentStunDelay() < stoner.getSetStunDelay()) {
			return false;
		}

		if (stoner.getProfession().locked()) {
			return true;
		}

		// Check if player has multiple weed items for auto-smoking
		int totalWeed = 0;
		for (Item item : stoner.getBox().getItems()) {
			if (item != null && Weed.forId(item.getId()) != null) {
				totalWeed += item.getAmount();
			}
		}

		if (totalWeed > 1) {
			// Start auto-smoking all weed
			stoner.send(new SendMessage("You begin smoking all your weed..."));
			TaskQueue.queue(new AutoSmokingTask(stoner));
			return true;
		} else {
			// Single item - original smoking logic
			stoner.setCurrentStunDelay(System.currentTimeMillis() + weed.getStunTime() * 1000L);
			stoner.getUpdateFlags().sendGraphic(new Graphic(354, true));
			stoner.getUpdateFlags().sendAnimation(new Animation(884));
			stoner.getBox().remove(weed.getId(), 1);
			stoner.getBox().add(995, 420);
			stoner.getProfession().addExperience(16, weed.experience);
			AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_100G_WEED, 1);
			AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_1000G_WEED, 1);
			AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_10000G_WEED, 1);
			return true;
		}
	}

	private static class AutoSmokingTask extends Task {

		private final Stoner stoner;

		public AutoSmokingTask(Stoner stoner) {
			super(stoner, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
			this.stoner = stoner;
		}

		@Override
		public void execute() {
			if (System.currentTimeMillis() - stoner.getCurrentStunDelay() < stoner.getSetStunDelay()) {
				return;
			}

			if (stoner.getProfession().locked()) {
				return;
			}

			if (!stoner.getEquipment().isWearingItem(6575) ||
				!stoner.getEquipment().isWearingItem(1511) ||
				!stoner.getBox().hasItemId(1785)) {
				stoner.send(new SendMessage("You can no longer smoke - missing requirements."));
				stop();
				return;
			}

			Weed weed = null;
			Item weedItem = null;

			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					Weed w = Weed.forId(item.getId());
					if (w != null) {
						weed = w;
						weedItem = item;
						break;
					}
				}
			}

			if (weed == null || weedItem == null) {
				stoner.send(new SendMessage("You finish smoking all your weed."));
				stop();
				return;
			}

			stoner.setCurrentStunDelay(System.currentTimeMillis() + weed.getStunTime() * 1000L);
			stoner.getUpdateFlags().sendGraphic(new Graphic(354, true));
			stoner.getUpdateFlags().sendAnimation(new Animation(884));
			stoner.getBox().remove(weed.getId(), 1);
			stoner.getBox().add(995, 100);
			stoner.getProfession().addExperience(16, weed.experience);
			AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_100G_WEED, 1);
			AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_1000G_WEED, 1);
			AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_10000G_WEED, 1);

			stoner.send(new SendMessage("You smoke the " + weedItem.getDefinition().getName() + "."));
			stoner.getBox().update();
		}

		@Override
		public void onStop() {
		}
	}

	public enum Weed {
		KUSH(249, 520.0D, 4),
		HAZE(251, 650.0D, 4),
		OG_KUSH(253, 600.0D, 4),
		POWERPLANT(255, 520.0D, 4),
		CHEESE_HAZE(257, 810.0D, 4),
		BUBBA_KUSH(2998, 650.0D, 4),
		CHOCOLOPE(259, 760.0D, 4),
		GORILLA_GLUE(261, 915.0D, 4),
		JACK_HERER(263, 520.0D, 4),
		DURBAN_POISON(3000, 780.0D, 4),
		AMNESIA(265, 700.0D, 4),
		SUPER_SILVER_HAZE(2481, 950.0D, 4),
		GIRL_SCOUT_COOKIES(267, 1005.0D, 4),
		KHALIFA_KUSH(269, 1105.0D, 4);

		private static final Map<Integer, Weed> weed = new HashMap<Integer, Weed>();
		private final int id;
		private final double experience;
		int stunTime;

		Weed(int id, double experience, int time) {
			this.id = id;
			this.experience = experience;
			stunTime = time;
		}

		public static final void declare() {
			for (Weed w : values()) weed.put(Integer.valueOf(w.getId()), w);
		}

		public static Weed forId(int id) {
			return weed.get(Integer.valueOf(id));
		}

		public int getStunTime() {
			return stunTime;
		}

		public double getExperience() {
			return experience;
		}

		public int getId() {
			return id;
		}
	}
}