package com.bestbudz.rs2.content.profession.forging;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum Forging {
	SINGLETON;

	private static final int[] BARS = {2349, 2351, 2353, 2359, 2361, 2363};

	private static final Item[][][] FORGING_ITEMS = {
		{
			{new Item(1206), new Item(1278), new Item(1322), new Item(1292), new Item(1308)},
			{new Item(1352), new Item(1423), new Item(1338), new Item(1376)},
			{new Item(1104), new Item(1076), new Item(1088), new Item(1118)},
			{new Item(1140), new Item(1156), new Item(1174), new Item(1190), new Item(4819, 15)},
			{new Item(819, 15), new Item(39, 15), new Item(864, 15)}
		},
		{
			{new Item(1204), new Item(1280), new Item(1324), new Item(1294), new Item(1310)},
			{new Item(1350), new Item(1421), new Item(1336), new Item(1364)},
			{new Item(1102), new Item(1068), new Item(1082), new Item(1116)},
			{new Item(1138), new Item(1154), new Item(1176), new Item(1192), new Item(4820, 15)},
			{new Item(820, 15), new Item(40, 15), new Item(863, 15)}
		},
		{
			{new Item(1208), new Item(1282), new Item(1326), new Item(1296), new Item(1312)},
			{new Item(1354), new Item(1425), new Item(1340), new Item(1366)},
			{new Item(1106), new Item(1070), new Item(1084), new Item(1120)},
			{new Item(1142), new Item(1158), new Item(1178), new Item(1194), new Item(1539, 15)},
			{new Item(821, 15), new Item(41, 15), new Item(865, 15)}
		},
		{
			{new Item(1210), new Item(1286), new Item(1330), new Item(1300), new Item(1316)},
			{new Item(1356), new Item(1429), new Item(1344), new Item(1370)},
			{new Item(1110), new Item(1072), new Item(1086), new Item(1122)},
			{new Item(1144), new Item(1160), new Item(1182), new Item(1198), new Item(4822, 15)},
			{new Item(822, 15), new Item(42, 15), new Item(866, 15)}
		},
		{
			{new Item(1212), new Item(1288), new Item(1332), new Item(1302), new Item(1318)},
			{new Item(1358), new Item(1431), new Item(1346), new Item(1372)},
			{new Item(1112), new Item(1074), new Item(1092), new Item(1124)},
			{new Item(1146), new Item(1162), new Item(1184), new Item(1200), new Item(4823, 15)},
			{new Item(823, 15), new Item(43, 15), new Item(867, 15)}
		},
		{
			{new Item(1214), new Item(1290), new Item(1334), new Item(1304), new Item(1320)},
			{new Item(1360), new Item(1433), new Item(1348), new Item(1374)},
			{new Item(1114), new Item(1080), new Item(1094), new Item(1128)},
			{new Item(1148), new Item(1164), new Item(1186), new Item(1202), new Item(4824, 15)},
			{new Item(824, 15), new Item(44, 15), new Item(868, 15)}
		}
	};

	public boolean itemOnObject(Stoner stoner, Item item, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		if (isAnvil(objectId)) {

			return autoCraftAllForging(stoner);
		} else if (isFurnace(objectId)) {

			return autoCraftAllSmelting(stoner);
		}

		return false;
	}

	private boolean isAnvil(int objectId) {
		return objectId == 4306 || objectId == 2783 || objectId == 2097;
	}

	private boolean isFurnace(int objectId) {
		return objectId == 3044 || objectId == 11666 || objectId == 45310 ||
			objectId == 9472 || objectId == 9741 || objectId == 9371 ||
			objectId == 2030 || objectId == 21303 || objectId == 24009 ||
			objectId == 26814 || objectId == 3998 || objectId == 4304;
	}

	public boolean handleObjectClick(Stoner stoner, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		switch (objectId) {

			case 4306:
			case 2783:
			case 2097:
				return autoCraftAllForging(stoner);

			case 3044:
			case 11666:
			case 45310:
			case 9472:
			case 9741:
			case 9371:
			case 2030:
			case 21303:
			case 24009:
			case 26814:
			case 3998:
			case 4304:
				return autoCraftAllSmelting(stoner);

			default:

				return autoCraftAllAvailableItems(stoner);
		}
	}

	private boolean autoCraftAllAvailableItems(Stoner stoner) {
		List<ForgingTask> availableTasks = new ArrayList<>();

		for (SmeltingData smelting : SmeltingData.values()) {
			boolean hasAllOres = true;
			for (Item ore : smelting.getRequiredOres()) {
				if (!stoner.getBox().hasItemAmount(ore.getId(), ore.getAmount())) {
					hasAllOres = false;
					break;
				}
			}
			if (hasAllOres) {
				availableTasks.add(new ForgingTask(ForgingType.SMELTING, smelting.getResult().getId(), 0, 0));
			}
		}

		for (int barIndex = 0; barIndex < BARS.length; barIndex++) {
			if (stoner.getBox().contains(new Item(BARS[barIndex]))) {

				for (int category = 0; category < FORGING_ITEMS[barIndex].length; category++) {
					for (int itemIndex = 0; itemIndex < FORGING_ITEMS[barIndex][category].length; itemIndex++) {
						Item forgingItem = FORGING_ITEMS[barIndex][category][itemIndex];
						int requiredBars = getBarRequirement(category, itemIndex);

						if (stoner.getBox().hasItemAmount(BARS[barIndex], requiredBars)) {
							availableTasks.add(new ForgingTask(ForgingType.FORGING, forgingItem.getId(), barIndex, requiredBars));
						}
					}
				}
			}
		}

		if (availableTasks.isEmpty()) {
			stoner.send(new SendMessage("@red@No forging materials found in inventory."));
			return true;
		}

		startMultiForging(stoner, availableTasks);
		return true;
	}

	private boolean autoCraftAllSmelting(Stoner stoner) {
		List<ForgingTask> availableSmelting = new ArrayList<>();

		for (SmeltingData smelting : SmeltingData.values()) {
			boolean hasAllOres = true;
			for (Item ore : smelting.getRequiredOres()) {
				if (!stoner.getBox().hasItemAmount(ore.getId(), ore.getAmount())) {
					hasAllOres = false;
					break;
				}
			}
			if (hasAllOres) {
				availableSmelting.add(new ForgingTask(ForgingType.SMELTING, smelting.getResult().getId(), 0, 0));
			}
		}

		if (availableSmelting.isEmpty()) {
			stoner.send(new SendMessage("@red@No ores found in inventory."));
			return true;
		}

		startMultiForging(stoner, availableSmelting);
		return true;
	}

	private boolean autoCraftAllForging(Stoner stoner) {
		List<ForgingTask> availableForging = new ArrayList<>();

		for (int barIndex = 0; barIndex < BARS.length; barIndex++) {
			if (stoner.getBox().contains(new Item(BARS[barIndex]))) {

				for (int category = 0; category < FORGING_ITEMS[barIndex].length; category++) {
					for (int itemIndex = 0; itemIndex < FORGING_ITEMS[barIndex][category].length; itemIndex++) {
						Item forgingItem = FORGING_ITEMS[barIndex][category][itemIndex];
						int requiredBars = getBarRequirement(category, itemIndex);

						if (stoner.getBox().hasItemAmount(BARS[barIndex], requiredBars)) {
							availableForging.add(new ForgingTask(ForgingType.FORGING, forgingItem.getId(), barIndex, requiredBars));
						}
					}
				}
			}
		}

		if (availableForging.isEmpty()) {
			stoner.send(new SendMessage("@red@No bars found in inventory."));
			return true;
		}

		startMultiForging(stoner, availableForging);
		return true;
	}

	private int getBarRequirement(int category, int itemIndex) {

		switch (category) {
			case 0:
				return itemIndex == 4 ? 3 : (itemIndex >= 2 ? 2 : 1);
			case 1:
				return itemIndex == 4 ? 2 : (itemIndex >= 2 ? 3 : 1);
			case 2:
				return itemIndex >= 2 ? 3 : (itemIndex == 1 ? 3 : 1);
			case 3:
				return itemIndex == 4 ? 1 : (itemIndex >= 2 ? 2 : 1);
			case 4:
				return 1;
			default:
				return 1;
		}
	}

	private static class ForgingTask {
		final ForgingType type;
		final int itemId;
		final int barIndex;
		final int barRequirement;

		ForgingTask(ForgingType type, int itemId, int barIndex, int barRequirement) {
			this.type = type;
			this.itemId = itemId;
			this.barIndex = barIndex;
			this.barRequirement = barRequirement;
		}
	}

	public enum ForgingType {
		SMELTING,
		FORGING
	}

	private void startMultiForging(Stoner stoner, List<ForgingTask> forgingTasks) {
		stoner.send(new SendMessage("@gre@Auto-forging started - found " + forgingTasks.size() + " craftable items!"));
		TaskQueue.queue(new MultiForgingTask(stoner, forgingTasks));
	}

	private static class MultiForgingTask extends Task {
		private final Stoner stoner;
		private final List<ForgingTask> forgingTasks;
		private int currentTaskIndex = 0;

		public MultiForgingTask(Stoner stoner, List<ForgingTask> forgingTasks) {
			super(stoner, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.PROFESSION_CREATING);
			this.stoner = stoner;
			this.forgingTasks = forgingTasks;
		}

		@Override
		public void execute() {
			if (!stoner.getBox().hasSpaceFor(new Item(1, 1))) {
				stoner.send(new SendMessage("@gre@Auto-forging stopped - inventory is full!"));
				stop();
				return;
			}

			ForgingTask nextTask = findNextCraftableItem();
			if (nextTask == null) {
				stoner.send(new SendMessage("@gre@Auto-forging completed - no more materials available!"));
				stop();
				return;
			}

			executeTask(nextTask);
		}

		private void executeTask(ForgingTask task) {
			stoner.getProfession().lock(2);

			if (task.type == ForgingType.SMELTING) {
				executeSmelting(task.itemId);
			} else {
				executeForging(task.itemId, task.barIndex, task.barRequirement);
			}
		}

		private void executeSmelting(int barId) {
			SmeltingData smelting = getSmeltingData(barId);
			if (smelting == null) return;

			boolean hasAllOres = true;
			for (Item ore : smelting.getRequiredOres()) {
				if (!stoner.getBox().hasItemAmount(ore.getId(), ore.getAmount())) {
					hasAllOres = false;
					break;
				}
			}

			if (!hasAllOres) return;

			stoner.getUpdateFlags().sendAnimation(new Animation(899));
			stoner.getBox().remove(smelting.getRequiredOres(), false);

			if (smelting == SmeltingData.IRON_BAR) {
				if (Professions.isSuccess(stoner, Professions.FORGING, smelting.getGradeRequired())) {
					stoner.getBox().add(smelting.getResult(), false);
					stoner.send(new SendMessage("You smelt " + Utility.getAOrAn(smelting.getResult().getDefinition().getName()) +
						" " + smelting.getResult().getDefinition().getName() + "."));
				} else {
					stoner.send(new SendMessage("You fail to refine the iron ore."));
				}
			} else {
				stoner.getBox().add(smelting.getResult(), false);
				stoner.send(new SendMessage("You smelt " + Utility.getAOrAn(smelting.getResult().getDefinition().getName()) +
					" " + smelting.getResult().getDefinition().getName() + "."));
			}

			stoner.getBox().update();
			stoner.getProfession().addExperience(Professions.FORGING, smelting.getExp());
		}

		private static final Set<Integer> UNNOTED_STACK = Set.of(
			39, 40, 41, 42, 43, 44,
			819, 820, 821, 822, 823, 824,
			864, 863, 865, 866, 867, 868,
			4819, 4820, 4822, 4823, 4824,
			1539
		);

		private void executeForging(int itemId, int barIndex, int barRequirement) {
			if (barIndex < 0 || barIndex >= BARS.length) return;

			int barId = BARS[barIndex];
			if (!stoner.getBox().hasItemAmount(barId, barRequirement)) return;

			Item forgedItem = new Item(itemId);

			if (forgedItem.getDefinition().isStackable()) {
				if (UNNOTED_STACK.contains(itemId)) {
					forgedItem = new Item(itemId, 50);
				} else {
					forgedItem = new Item(itemId);
				}
			}

			stoner.getUpdateFlags().sendAnimation(new Animation(898));
			stoner.getClient().queueOutgoingPacket(new SendSound(468, 10, 10));

			stoner.getBox().remove(new Item(barId, barRequirement), false);
			stoner.getBox().add(forgedItem, true);
			stoner.getBox().update();

			double experience = getExperienceForBar(barId) * barRequirement;
			stoner.getProfession().addExperience(Professions.FORGING, experience);

			if (forgedItem.getAmount() == 1) {
				stoner.send(new SendMessage("You make " + Utility.getAOrAn(forgedItem.getDefinition().getName()) +
					" " + forgedItem.getDefinition().getName() + "."));
			} else {
				stoner.send(new SendMessage("You make " + forgedItem.getAmount() + " " +
					forgedItem.getDefinition().getName() +
					(!forgedItem.getDefinition().getName().endsWith("s") ? "s" : "") + "."));
			}
		}

		private SmeltingData getSmeltingData(int barId) {
			for (SmeltingData smelting : SmeltingData.values()) {
				if (smelting.getResult().getId() == barId) {
					return smelting;
				}
			}
			return null;
		}

		private double getExperienceForBar(int barId) {
			switch (barId) {
				case 2349: return 12.5;
				case 2351: return 25.0;
				case 2353: return 37.5;
				case 2359: return 50.0;
				case 2361: return 62.5;
				case 2363: return 75.0;
				default: return 12.5;
			}
		}

		private ForgingTask findNextCraftableItem() {
			for (int i = 0; i < forgingTasks.size(); i++) {
				int taskIndex = (currentTaskIndex + i) % forgingTasks.size();
				ForgingTask task = forgingTasks.get(taskIndex);

				if (canExecuteTask(task)) {
					currentTaskIndex = (currentTaskIndex + i + 1) % forgingTasks.size();
					return task;
				}
			}
			return null;
		}

		private boolean canExecuteTask(ForgingTask task) {
			if (task.type == ForgingType.SMELTING) {
				SmeltingData smelting = getSmeltingData(task.itemId);
				if (smelting == null) return false;

				for (Item ore : smelting.getRequiredOres()) {
					if (!stoner.getBox().hasItemAmount(ore.getId(), ore.getAmount())) {
						return false;
					}
				}
				return true;
			} else {
				return task.barIndex >= 0 && task.barIndex < BARS.length &&
					stoner.getBox().hasItemAmount(BARS[task.barIndex], task.barRequirement);
			}
		}

		@Override
		public void onStop() {
			stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		}
	}

	public boolean craft(Stoner stoner, int index, int amount) {
		stoner.send(new SendMessage("@red@Manual forging is disabled. Use item-on-item or click objects for auto-forging."));
		return false;
	}

	public boolean clickButton(Stoner stoner, int button) {
		stoner.send(new SendMessage("@red@Interface forging is disabled. Use item-on-item or click objects for auto-forging."));
		return false;
	}
}
