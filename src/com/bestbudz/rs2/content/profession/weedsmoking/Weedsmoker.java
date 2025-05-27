package com.bestbudz.rs2.content.profession.weedsmoking;

import java.util.HashMap;
import java.util.Map;

// import com.bestbudz.core.task.Task;
// import com.bestbudz.core.task.Task.BreakType;
// import com.bestbudz.core.task.Task.StackType;
// import com.bestbudz.core.task.TaskQueue;
// import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
// import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
// import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;


public class Weedsmoker {

	public static enum Weed {
		KUSH(249, 520.0D, 4), // THC VALUE 17 %
		HAZE(251, 650.0D, 4), // THC VALUE 20 %
		OG_KUSH(253, 600.0D, 4), // THC VALUE 19 %
		POWERPLANT(255, 520.0D, 4), // THC VALUE 17 %
		CHEESE_HAZE(257, 810.0D, 4), // THC VALUE 24 %
		BUBBA_KUSH(2998, 650.0D, 4), // THC VALUE 20 %
		CHOCOLOPE(259, 760.0D, 4), // THC VALUE 22 %
		GORILLA_GLUE(261, 915.0D, 4), // THC VALUE 26 %
		JACK_HERER(263, 520.0D, 4), // THC VALUE 17 %
		DURBAN_POISON(3000, 780.0D, 4), // THC VALUE 23 % 
		AMNESIA(265, 700.0D, 4), // THC VALUE 21 %
		SUPER_SILVER_HAZE(2481, 950.0D, 4), // THC VALUE 27 %  
		GIRL_SCOUT_COOKIES(267, 1005.0D, 4), // THC VALUE 29.5 %
		KHALIFA_KUSH(269, 1105.0D, 4); // THC VALUE 31 %

		private int id;
		private double experience;
		int stunTime;
		private static Map<Integer, Weed> weed = new HashMap<Integer, Weed>();

		public static final void declare() {
		for (Weed w : values())
			weed.put(Integer.valueOf(w.getId()), w);
		}

		public static Weed forId(int id) {
		return weed.get(Integer.valueOf(id));
		}

		private Weed(int id, double experience, int time) {
		this.id = id;
		this.experience = experience;
		stunTime = time;
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

	public static final String SMOKE_WITH_BUD_KEY = "Smokeweedwithbud";

	public static boolean burnin(Stoner stoner, int id, int experience, int time) {
	Weed weed = Weed.forId(id);

	if (weed == null) {
		return false;
	}

	if (!stoner.getEquipment().isWearingItem(6575)) {
				DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to spark the pipe!", 6575);
		return false;
	}

	if (!stoner.getEquipment().isWearingItem(1511)) {
		DialogueManager.sendItem1(stoner, "You must be wearing wood to keep the fire going!", 1511);
    return false;
    }

	if (!stoner.getBox().hasItemId(1785)) {
		DialogueManager.sendItem1(stoner, "You must have a weed pipe in your box to smoke!", 1785);
    return false;
    }

	if (System.currentTimeMillis() - stoner.getCurrentStunDelay() < stoner.getSetStunDelay()) {
		return false;
	}

	 if (stoner.getProfession().locked()) {
		 return true;
	 }

   // stoner.freeze(5, 5);
	stoner.setCurrentStunDelay(System.currentTimeMillis() + weed.getStunTime() * 1000);
	stoner.getUpdateFlags().sendGraphic(new Graphic(354, true));
	stoner.getUpdateFlags().sendAnimation(new Animation(884));
	stoner.getBox().remove(weed.getId(), 1);
	stoner.getBox().add(995, 100);
	stoner.getProfession().addExperience(16, weed.experience);
	AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_100G_WEED, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_1000G_WEED, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_10000G_WEED, 1);


/*
	return true;
	}

	public static void finishOnAltar(Stoner stoner, int amount) {
	if (stoner.getAttributes().get("Smokeweedwithbud") == null) {
		return;
	}

	int item = stoner.getAttributes().getInt("Smokeweedwithbud");

	Weed weed = Weed.forId(item);

	if (weed == null) {
		return;
	}

	int invAmount = stoner.getBox().getItemAmount(item);

	if (invAmount == 0)
		return;
	if (invAmount < amount) {
		amount = invAmount;
	}

	stoner.getProfession().lock(5);

	stoner.getUpdateFlags().sendGraphic(new Graphic(354));
	stoner.getUpdateFlags().sendAnimation(new Animation(884));
	stoner.getBox().remove(new Item(item, amount));
	stoner.getProfession().addExperience(16, (weed.experience) * amount);
	stoner.getBox().add(995, 100);
	AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_100G_WEED, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_1000G_WEED, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_10000G_WEED, 1);
	}

	public static boolean useWeedOnBud(Stoner p, int item, Entity entity) {
	if (entity == p) {
		Weed weed = Weed.forId(item);

		if (weed == null) {
			return false;
		}
		if (!p.getEquipment().isWearingItem(6575)) {
			DialogueManager.sendItem1(p, "You must be wearing a tool ring to spark the pipe!", 6575);
	return false;
    }

     if (!p.getEquipment().isWearingItem(1511)) {
	     DialogueManager.sendItem1(p, "You must be wearing wood to keep the fire going!", 1511);
         return false;
    }

     if (!p.getBox().hasItemId(1785)) {
	     DialogueManager.sendItem1(p, "You must have a weed pipe in your box to smoke!", 1785);
          return false;
      }

		TaskQueue.queue(new Task(p, 3, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.WEED_WITH_BUD) {


			@Override
			public void execute() {
			if (!p.getBox().hasItemId(item)) {
				stop();
				return;
			}

			if (p.getProfession().locked()) {
				return;
			}

			p.getProfession().lock(5);

			p.getUpdateFlags().sendGraphic(new Graphic(354));
			p.getUpdateFlags().sendAnimation(new Animation(884));
			p.getBox().remove(item);
			p.getBox().add(995, 100);
			p.getProfession().addExperience(16, weed.experience * 2.0);
			}

			@Override
			public void onStop() {
			}
		});

		return true;
	} 
*/
	return false;
	}
}
