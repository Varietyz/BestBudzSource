package com.bestbudz.rs2.content.profession.fisher;

import java.util.HashMap;
import java.util.Map;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class Fisher {

	public static enum FisherSpots {

		SMALL_NET_OR_BAIT(1518, new FishableData.Fishable[] { FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES, FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB, FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH, FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS, FishableData.Fishable.SHARK, FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE, FishableData.Fishable.TROUT, FishableData.Fishable.SALMON }),

		LURE_OR_BAIT(1526, new FishableData.Fishable[] { FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES, FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB, FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH, FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS, FishableData.Fishable.SHARK, FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE, FishableData.Fishable.TROUT, FishableData.Fishable.SALMON }),

		CAGE_OR_HARPOON(1519, new FishableData.Fishable[] { FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES, FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB, FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH, FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS, FishableData.Fishable.SHARK, FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE, FishableData.Fishable.TROUT, FishableData.Fishable.SALMON }),

		LARGE_NET_OR_HARPOON(1520, new FishableData.Fishable[] { FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES, FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB, FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH, FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS, FishableData.Fishable.SHARK, FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE, FishableData.Fishable.TROUT, FishableData.Fishable.SALMON }),

		HARPOON_OR_SMALL_NET(1534, new FishableData.Fishable[] { FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES, FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB, FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH, FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS, FishableData.Fishable.SHARK, FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE, FishableData.Fishable.TROUT, FishableData.Fishable.SALMON }),

		MANTA_RAY(3019, new FishableData.Fishable[] { FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES, FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB, FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH, FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS, FishableData.Fishable.SHARK, FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE, FishableData.Fishable.TROUT, FishableData.Fishable.SALMON }),

		DARK_CRAB(1536, new FishableData.Fishable[] { FishableData.Fishable.FLAX, FishableData.Fishable.FISH_BONES, FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB, FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH, FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS, FishableData.Fishable.SHARK, FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE, FishableData.Fishable.TROUT, FishableData.Fishable.SALMON });

		/*
		 * FishableData.Fishable.FLAX, FishableData.Fishable.BABY_DRAGON_BONES,
		 * FishableData.Fishable.DRAGON_ARROWS, FishableData.Fishable.SHRIMP,
		 * FishableData.Fishable.ANCHOVIES, FishableData.Fishable.DARK_CRAB,
		 * FishableData.Fishable.MANTA_RAY, FishableData.Fishable.TUNA,
		 * FishableData.Fishable.SWORD_FISH, FishableData.Fishable.MONK_FISH,
		 * FishableData.Fishable.MACKEREL, FishableData.Fishable.COD,
		 * FishableData.Fishable.BASS, FishableData.Fishable.SHARK,
		 * FishableData.Fishable.LOBSTER, FishableData.Fishable.SARDINE,
		 * FishableData.Fishable.HERRING, FishableData.Fishable.PIKE,
		 * FishableData.Fishable.TROUT, FishableData.Fishable.SALMON
		 */

		private int id;
		private FishableData.Fishable[] option_1;
		private static Map<Integer, FisherSpots> fisherSpots = new HashMap<Integer, FisherSpots>();

		public static final void declare() {
		for (FisherSpots spots : values())
			fisherSpots.put(Integer.valueOf(spots.getId()), spots);
		}

		public static FisherSpots forId(int id) {
		return fisherSpots.get(Integer.valueOf(id));
		}

		private FisherSpots(int id, FishableData.Fishable[] option_1) {
		this.id = id;
		this.option_1 = option_1;
		}

		public int getId() {
		return id;
		}

		public FishableData.Fishable[] getOption_1() {
		return option_1;
		}

	}

	public static boolean canFish(Stoner p, FishableData.Fishable fish, boolean message) {

	return true;
	}

	public static boolean hasFisherItems(Stoner stoner, FishableData.Fishable fish, boolean message) {
	int tool = fish.getToolId();
	int bait = fish.getBaitRequired();

	if (tool == 6577) {
		if (!stoner.getBox().hasItemAmount(new Item(tool, 1))) {

			Item necklace = stoner.getEquipment().getItems()[2];
			if ((necklace != null) && (necklace.getId() == 6577)) {
				return true;
			}
		}
		if (message) {
			DialogueManager.sendItem1(stoner, "You must be wearing a fisher necklace to splash at the fishes!", 6577);
		}
		return false;

	} else if ((!stoner.getBox().hasItemAmount(new Item(tool, 1))) && (message)) {
		String name = Item.getDefinition(tool).getName();
		stoner.getClient().queueOutgoingPacket(new SendMessage("BUG WHILE CHECKING FOR TOOL " + Utility.getAOrAn(name) + " " + name + ", PLEASE SHARE SCREENSHOT."));
		return false;
	}

	if ((bait > -1) && (!stoner.getBox().hasItemAmount(new Item(bait, 1)))) {
		String name = Item.getDefinition(bait).getName();
		if (message) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("BUG WHILE CHECKING FOR BAIT " + Utility.getAOrAn(name) + " " + name + ", PLEASE SHARE SCREENSHOT."));
		}
		return false;
	}

	return true;
	}

	private final Stoner stoner;

	private FishableData.Fishable[] fisher = null;

	private ToolData.Tools tool = null;

	public Fisher(Stoner stoner) {
	this.stoner = stoner;
	}

	public boolean clickNpc(Mob mob, int id, int option) {
	if (FisherSpots.forId(id) == null) {
		return false;
	}

	FisherSpots spot = FisherSpots.forId(id);

	FishableData.Fishable[] f = new FishableData.Fishable[20];
	int amount = 0;
	FishableData.Fishable[] fish;
	switch (option) {
	case 1:
		fish = spot.option_1;
		for (int i = 0; i < fish.length; i++) {
			if (canFish(stoner, fish[i], i == 0)) {
				f[i] = fish[i];
				amount++;
			}
		}
		break;

	}

	if (amount == 0) {
		return true;
	}

	FishableData.Fishable[] fisher = new FishableData.Fishable[amount];

	for (int i = 0; i < amount; i++) {
		fisher[i] = f[i];
	}

	start(mob, fisher, 0);

	return true;
	}

	public boolean fish() {
	if (fisher == null) {
		return false;
	}

	FishableData.Fishable[] fish = new FishableData.Fishable[20];

	byte c = 0;

	for (int i = 0; i < fisher.length; i++) {
		if (canFish(stoner, fisher[i], false)) {
			fish[c] = fisher[i];
			c = (byte) (c + 1);
		}
	}
	if (c == 0) {
		return false;
	}

	FishableData.Fishable f = fish[Utility.randomNumber(c)];

	if (stoner.getBox().getFreeSlots() == 0) {
		DialogueManager.sendStatement(stoner, "U can start a fisher stall with all these fish, now get.");
		return false;
	}

	if (success(f)) {
		if (f.getBaitRequired() != -1) {
			stoner.getBox().remove(new Item(f.getBaitRequired(), 0));

		}

		stoner.getClient().queueOutgoingPacket(new SendSound(378, 0, 0));

		int id = f.getRawFishId();
		String name = Item.getDefinition(id).getName();
		stoner.getBox().add(new Item(id, 1));
		stoner.getProfession().addExperience(10, f.getExperience());
		stoner.getClient().queueOutgoingPacket(new SendMessage("" + getFishStringMod(name) + name + "."));

	}

	stoner.getProfession().lock(4);

	return true;
	}

	public String getFishStringMod(String name) {
	return name.substring(name.length() - 2, name.length() - 1).equals("s") ? "You splashed the water and got " : "You splashed the water and got ";
	}

	public void reset() {
	fisher = null;
	tool = null;
	}

	public void start(final Mob mob, FishableData.Fishable[] fisher, int option) {
	if ((fisher == null) || (fisher[option] == null) || (fisher[option].getToolId() == -1)) {
		return;
	}

	this.fisher = fisher;

	tool = ToolData.Tools.forId(fisher[option].getToolId());

	if (!hasFisherItems(stoner, fisher[option], true)) {
		return;
	}

	stoner.getClient().queueOutgoingPacket(new SendSound(289, 0, 0));

	stoner.getUpdateFlags().sendAnimation(tool.getAnimationId(), 0);

	Task profession = new Task(stoner, 4, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.FISHER) {
		@Override
		public void execute() {
		stoner.face(mob);
		stoner.getUpdateFlags().sendAnimation(tool.getAnimationId(), 0);

		if (!fish()) {
			stop();
			reset();
			return;
		}
		}

		@Override
		public void onStop() {
		}
	};
	TaskQueue.queue(profession);
	}

	public boolean success(FishableData.Fishable fish) {
	return Professions.isSuccess(stoner, 10, fish.getRequiredGrade());
	}
}
