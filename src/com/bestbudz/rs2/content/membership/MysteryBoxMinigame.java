package com.bestbudz.rs2.content.membership;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.chance.Chance;
import com.bestbudz.core.util.chance.WeightedChance;
import com.bestbudz.core.util.chance.WeightedObject;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class MysteryBoxMinigame {

	private final static int EXPENSIVE_AMOUNT = 1_500_000;

	private final static int CREDITS_REQUIRED = 200;

	private final static int INTERFACE_ID = 17000;

	private final static int CONTAINER_ID = 17002;

	private final static String MESSAGE_COLOR = "<col=255>";

	public static Chance<Item> available = new Chance<Item>(Arrays.asList(
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1079, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1093, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1113, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1127, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1147, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1163, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1185, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1201, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1213, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4131, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3476, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3477, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7336, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7342, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7348, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7354, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7360, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10286, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10288, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10290, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10292, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10294, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(560, 350)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(565, 350)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(561, 500)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(892, 500)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(868, 175)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2491, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2497, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2503, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12871, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12869, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12867, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12865, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1725, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1712, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10602, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10601, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4091, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4105, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4103, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4113, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4111, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4101, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(392, 55)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11937, 25)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(158, 5)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2441, 5)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2437, 5)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2443, 5)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(166, 5)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3027, 5)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3025, 5)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(13066, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6688, 5)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1149, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1187, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1215, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1231, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1249, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1305, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1377, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1434, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1615, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1631, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1645, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4087, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4585, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(7158, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4980, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4986, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4998, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4992, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4956, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4962, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4968, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4974, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4932, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4938, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4944, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4950, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4908, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4914, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4920, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4926, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4860, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4866, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4872, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4878, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4884, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4890, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4896, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4902, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11840, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6585, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11283, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11335, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(3140, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12829, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6731, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6733, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6735, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6737, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12922, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12932, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6570, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12795, 1))

	));

	public static void open(Stoner stoner) {
	stoner.send(new SendString("", 17015));
	stoner.send(new SendString("</col>CannaCredits: @gre@" + Utility.format(stoner.getCredits()), 17006));
	stoner.send(new SendString("Misery Box is a game where you can bet @gre@" + CREDITS_REQUIRED + " </col>CannaCredits.", 17008));
	stoner.send(new SendUpdateItemsAlt(CONTAINER_ID, -1, 0, 0));
	stoner.send(new SendInterface(INTERFACE_ID));
	}

	public static boolean can(Stoner stoner) {
	if (stoner.getInterfaceManager().main != INTERFACE_ID) {
		stoner.send(new SendRemoveInterfaces());
		return false;
	}
	if (StonerConstants.isStoner(stoner)) {
		stoner.send(new SendMessage(MESSAGE_COLOR + "You must be a member to do this!"));
		return false;
	}
	if (stoner.getCredits() < CREDITS_REQUIRED) {
		stoner.send(new SendMessage(MESSAGE_COLOR + "You do not have enough cannacredits to do this!"));
		return false;
	}
	if (stoner.getBox().getFreeSlots() == 0) {
		stoner.send(new SendMessage(MESSAGE_COLOR + "You do not have enough box spaces to do this!"));
		return false;
	}
	if (stoner.playingMB) {
		stoner.send(new SendMessage(MESSAGE_COLOR + "Please wait before doing this!"));
		return false;
	}
	return true;
	}

	public static void play(Stoner stoner) {
	if (!can(stoner)) {
		return;
	}

	stoner.playingMB = true;
	stoner.setCredits(stoner.getCredits() - CREDITS_REQUIRED);
	stoner.send(new SendString("</col>CannaCredits: @gre@" + Utility.format(stoner.getCredits()), 17006));
	TaskQueue.queue(new Task(stoner, 1, true) {
		final int ticks = 10;
		int cycles = 0;

		@Override
		public void execute() {
		Item item = available.nextObject().get();
		stoner.send(new SendUpdateItemsAlt(CONTAINER_ID, item.getId(), item.getAmount(), 0));
		if (cycles++ == ticks) {
			reward(stoner, item);
			stop();
		}
		}

		@Override
		public void onStop() {
		stoner.playingMB = false;
		}
	});
	}

	public static void reward(Stoner stoner, Item itemWon) {
	ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemWon.getId());

	stoner.send(new SendMessage(MESSAGE_COLOR + "Congratulations! You have won " + Utility.getAOrAn(itemDef.getName()) + " " + itemDef.getName() + "!"));
	stoner.send(new SendString("Won: " + itemWon.getDefinition().getName() + "!", 17015));
	stoner.getBox().add(itemWon);

	if (itemWon.getDefinition().getGeneralPrice() >= EXPENSIVE_AMOUNT) {
		World.sendGlobalMessage("[ " + MESSAGE_COLOR + "Misery Box </col>] " + MESSAGE_COLOR + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + "</col> has just won " + Utility.getAOrAn(itemDef.getName()) + MESSAGE_COLOR + " " + itemDef.getName() + "</col>!");
	}
	}

	public static void main(String[] args) {
	int common = 0;
	int uncommon = 0;
	int rare = 0;
	int very_rare = 0;
	double trials = 1_000_000.0;

	for (int i = 0; i < trials; i++) {
		WeightedObject<Item> item = available.nextObject();
		switch ((int) item.getWeight()) {

		case 10:
			common++;
			break;

		case 7:
			uncommon++;
			break;

		case 3:
			rare++;
			break;

		case 1:
			very_rare++;
			break;
		}
	}
	DecimalFormat formatter = new DecimalFormat("#.##");
	formatter.setRoundingMode(RoundingMode.DOWN);
	System.out.println("runs: " + trials);
	trials = (common + uncommon + rare + very_rare);
	System.out.println(formatter.format(common * 100 / trials) + "% - common: " + Utility.format(common));
	System.out.println(formatter.format(uncommon * 100 / trials) + "% - uncommon: " + Utility.format(uncommon));
	System.out.println(formatter.format(rare * 100 / trials) + "% - rares: " + Utility.format(rare));
	System.out.println(formatter.format(very_rare * 100 / trials) + "% - very rares: " + Utility.format(very_rare));
	}

}
