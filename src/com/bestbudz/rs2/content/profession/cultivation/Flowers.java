package com.bestbudz.rs2.content.profession.cultivation;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.membership.CreditPurchase;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Flowers {

	private Stoner stoner;

	// set of global constants for Cultivation

	private static final double WATERING_CHANCE = 0.5;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	public static final int SCARECROW = 6059;

	public Flowers(Stoner stoner) {
	this.stoner = stoner;
	}

	// Cultivation data
	public int[] flowerStages = new int[4];
	public int[] flowerSeeds = new int[4];
	public int[] flowerState = new int[4];
	public long[] flowerTimer = new long[4];
	public double[] diseaseChance = { 1, 1, 1, 1 };
	public boolean[] hasFullyGrown = { false, false, false, false };

	/* set of the constants for the patch */

	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int WATERED = 0x01;
	public static final int DISEASED = 0x02;
	public static final int DEAD = 0x03;

	public static final int FLOWER_PATCH_CONFIGS = 508;

	/* This is the enum holding the seeds info */

	public enum FlowerData {

		MARIGOLD(5096, 6010, 2, 20, 0.35, 8.5, 47, 0x08, 0x0c),
		ROSEMARY(5097, 6014, 11, 20, 0.32, 12, 66.5, 0x0d, 0x11),
		NASTURTIUM(5098, 6012, 24, 20, 0.30, 19.5, 111, 0x12, 0x16),
		WOAD(5099, 1793, 25, 20, 0.27, 20.5, 115.5, 0x17, 0x1b),
		LIMPWURT(5100, 225, 26, 25, 21.5, 8.5, 120, 0x1c, 0x20),;

		private int seedId;
		private int harvestId;
		private int gradeRequired;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static Map<Integer, FlowerData> seeds = new HashMap<Integer, FlowerData>();

		static {
			for (FlowerData data : FlowerData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		FlowerData(int seedId, int harvestId, int gradeRequired, int growthTime, double diseaseChance, double plantingXp, double harvestXp, int startingState, int endingState) {
		this.seedId = seedId;
		this.harvestId = harvestId;
		this.gradeRequired = gradeRequired;
		this.growthTime = growthTime;
		this.diseaseChance = diseaseChance;
		this.plantingXp = plantingXp;
		this.harvestXp = harvestXp;
		this.startingState = startingState;
		this.endingState = endingState;
		}

		public static FlowerData forId(int seedId) {
		return seeds.get(seedId);
		}

		public int getSeedId() {
		return seedId;
		}

		public int getHarvestId() {
		return harvestId;
		}

		public int getGradeRequired() {
		return gradeRequired;
		}

		public int getGrowthTime() {
		return growthTime;
		}

		public double getDiseaseChance() {
		return diseaseChance;
		}

		public double getPlantingXp() {
		return plantingXp;
		}

		public double getHarvestXp() {
		return harvestXp;
		}

		public int getStartingState() {
		return startingState;
		}

		public int getEndingState() {
		return endingState;
		}
	}

	/* This is the enum data about the different patches */

	public enum FlowerFieldsData {
		ARDOUGNE(0, new Point[] { new Point(2666, 3374), new Point(2667, 3375) }),
		PHASMATYS(1, new Point[] { new Point(3601, 3525), new Point(3602, 3526) }),
		FALADOR(2, new Point[] { new Point(3054, 3307), new Point(3055, 3308) }),
		CATWEEDY(3, new Point[] { new Point(2809, 3463), new Point(2810, 3464) });

		private int flowerIndex;
		private Point[] flowerPosition;

		FlowerFieldsData(int flowerIndex, Point[] flowerPosition) {
		this.flowerIndex = flowerIndex;
		this.flowerPosition = flowerPosition;
		}

		public static FlowerFieldsData forIdPosition(Point point) {
		for (FlowerFieldsData flowerFieldsData : FlowerFieldsData.values()) {
			if (CultivationConstants.inRangeArea(flowerFieldsData.getFlowerPosition()[0], flowerFieldsData.getFlowerPosition()[1], point)) {
				return flowerFieldsData;
			}
		}
		return null;
		}

		public int getFlowerIndex() {
		return flowerIndex;
		}

		public Point[] getFlowerPosition() {
		return flowerPosition;
		}
	}

	/* This is the enum that hold the different data for inspecting the plant */

	public enum InspectData {

		MARIGOLD(5096, new String[][] { { "The seeds have only just been planted." }, { "The marigold plants have developed leaves." }, { "The marigold plants have begun to grow their", "flowers. The new flowers are orange and small at", "first." }, { "The marigold plants are larger, and more", "developed in their petals." }, { "The marigold plants are ready to harvest. Their", "flowers are fully matured." } }),
		ROSEMARY(5097, new String[][] { { "The seeds have only just been planted." }, { "The rosemary plant is taller than before." }, { "The rosemary plant is bushier and taller than", "before." }, { "The rosemary plant is developing a flower bud at", "its top." }, { "The plant is ready to harvest. The rosemary", "plant's flower has opened." } }),
		NASTURTIUM(5098, new String[][] { { "The nasturtium seed has only just been planted." }, { "The nasturtium plants have started to develop", "leaves." }, { "The nasturtium plants have grown more leaves,", "and nine flower buds." }, { "The nasturtium plants open their flower buds." }, { "The plants are ready to harvest. The nasturtium", "plants grow larger than before and the flowers", "fully open." } }),
		WOAD(5099, new String[][] { { "The woad seed has only just been planted." }, { "The woad plant produces more stalks, that split", "in tow near the top." }, { "The woad plant grows more segments from its", "intitial stalks." }, { "The woad plant develops flower buds on the end", "of each of its stalks." }, { "The woad plant is ready to harvest. The plant has", "all of its stalks pointing directly up, with", "all flowers open." } }),
		LIMPWURT(5100, new String[][] { { "The seed has only just been planted." }, { "The limpwurt plant produces more roots." }, { "The limpwurt plant produces an unopened pink", "flower bud and continues to grow larger." }, { "The limpwurt plant grows larger, with more loops", "in its roots. The flower bud is still unopened." }, { "The limpwurt plant is ready to harvest. The", "flower finally opens wide, with a spike in the", "middle." } });

		private int seedId;
		private String[][] messages;

		private static Map<Integer, InspectData> seeds = new HashMap<Integer, InspectData>();

		static {
			for (InspectData data : InspectData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		InspectData(int seedId, String[][] messages) {
		this.seedId = seedId;
		this.messages = messages;
		}

		public static InspectData forId(int seedId) {
		return seeds.get(seedId);
		}

		public int getSeedId() {
		return seedId;
		}

		public String[][] getMessages() {
		return messages;
		}
	}

	/* update all the patch states */

	public void updateFlowerStates() {
	// ardougne - phasmatys - falador - catweedy
	int[] configValues = new int[flowerStages.length];

	int configValue;
	for (int i = 0; i < flowerStages.length; i++) {
		configValues[i] = getConfigValue(flowerStages[i], flowerSeeds[i], flowerState[i], i);
	}

	configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
	stoner.send(new SendConfig(FLOWER_PATCH_CONFIGS, configValue));

	}

	/* getting the different config values */

	public int getConfigValue(int flowerStage, int seedId, int plantState, int index) {
	if (flowerSeeds[index] >= 0x21 && flowerSeeds[index] <= 0x24 && flowerStages[index] > 3) {
		return (GROWING << 6) + flowerSeeds[index];
	}
	FlowerData flowerData = FlowerData.forId(seedId);
	switch (flowerStage) {
	case 0:// weed
		return (GROWING << 6) + 0x00;
	case 1:// weed cleared
		return (GROWING << 6) + 0x01;
	case 2:
		return (GROWING << 6) + 0x02;
	case 3:
		return (GROWING << 6) + 0x03;
	}
	if (flowerData == null) {
		return -1;
	}
	if (flowerData.getEndingState() == flowerData.getStartingState() + flowerStage - 2) {
		hasFullyGrown[index] = true;
	}
	return (getPlantState(plantState) << 6) + flowerData.getStartingState() + flowerStage - 4;
	}

	/* getting the plant states */

	public int getPlantState(int plantState) {
	switch (plantState) {
	case 0:
		return GROWING;
	case 1:
		return WATERED;
	case 2:
		return DISEASED;
	case 3:
		return DEAD;
	}
	return -1;
	}

	/* calculating the disease chance and making the plant grow */

	public void doCalculations() {
	for (int i = 0; i < flowerSeeds.length; i++) {
		if (flowerStages[i] > 0 && flowerStages[i] <= 3 && Cultivation.getMinutesCounter(stoner) - flowerTimer[i] >= 5) {
			flowerStages[i]--;
			flowerTimer[i] = Cultivation.getMinutesCounter(stoner);
			updateFlowerStates();
		}
		if (Cultivation.getMinutesCounter(stoner) - flowerTimer[i] >= 5 && flowerSeeds[i] > 0x21 && flowerSeeds[i] <= 0x24) {
			flowerSeeds[i]--;
			updateFlowerStates();
			return;
		}
		FlowerData flowerData = FlowerData.forId(flowerSeeds[i]);
		if (flowerData == null) {
			continue;
		}

		long difference = Cultivation.getMinutesCounter(stoner) - flowerTimer[i];
		long growth = flowerData.getGrowthTime();
		int nbStates = flowerData.getEndingState() - flowerData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		if (flowerState[i] == 3 || flowerSeeds[i] == 0x21 || flowerTimer[i] == 0 || state > nbStates) {
			continue;
		}

		if (4 + state != flowerStages[i]) {
			flowerStages[i] = 4 + state;
			doStateCalculation(i);
			updateFlowerStates();
		}
	}
	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
	if (flowerState[index] == 3) {
		return;
	}
	// if the patch is diseased, it dies, if its watched by a farmer, it
	// goes back to normal
	if (flowerState[index] == 2) {
		flowerState[index] = 3;
	}

	if (flowerState[index] == 1 || flowerState[index] == 5 && flowerStages[index] != 3) {
		diseaseChance[index] *= 2;
		flowerState[index] = 0;
	}
	if (flowerState[index] == 0 && flowerStages[index] >= 5 && !hasFullyGrown[index]) {
		FlowerData flowerData = FlowerData.forId(flowerSeeds[index]);
		if (flowerData == null) {
			return;
		}
		double chance = diseaseChance[index] * flowerData.getDiseaseChance();
		int maxChance = (int) (chance * 100);

		if (Utility.random(100) <= maxChance && !stoner.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
			flowerState[index] = 2;
		}
	}
	}

	/* watering the patch */

	public boolean waterPatch(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	if (flowerFieldsData == null) {
		return false;
	}
	FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
	if (flowerData == null) {
		return false;
	}
	if (flowerState[flowerFieldsData.getFlowerIndex()] == 1 || flowerStages[flowerFieldsData.getFlowerIndex()] <= 1 || flowerStages[flowerFieldsData.getFlowerIndex()] == flowerData.getEndingState() - flowerData.getStartingState() + 4) {
		stoner.send(new SendMessage("This patch doesn't need watering."));
		return true;
	}
	stoner.getBox().remove(itemId, 1);
	stoner.getBox().add(itemId == 5333 ? itemId - 2 : itemId - 1, 1);

	if (!stoner.getEquipment().isWearingItem(CultivationConstants.RAKE)) {
		DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant seed here.");
		return true;
	}
	stoner.send(new SendMessage("You water the patch."));
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.WATERING_CAN_ANIM));

	stoner.getProfession().lock(5);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 5, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		diseaseChance[flowerFieldsData.getFlowerIndex()] *= WATERING_CHANCE;
		flowerState[flowerFieldsData.getFlowerIndex()] = 1;
		stop();
		}

		@Override
		public void onStop() {
		updateFlowerStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;
	}

	/* clearing the patch with a rake of a spade */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	int finalAnimation;
	int finalDelay;
	if (flowerFieldsData == null || (itemId != CultivationConstants.RAKE && itemId != CultivationConstants.SPADE)) {
		return false;
	}
	if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
		return true;
	}
	if (flowerStages[flowerFieldsData.getFlowerIndex()] <= 3) {
		if (!stoner.getEquipment().isWearingItem(CultivationConstants.RAKE)) {
			DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to clear this path.");
			return true;
		} else {
			finalAnimation = CultivationConstants.RAKING_ANIM;
			finalDelay = 5;
		}
	} else {
		if (!stoner.getEquipment().isWearingItem(CultivationConstants.SPADE)) {
			DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to clear this path.");
			return true;
		} else {
			finalAnimation = CultivationConstants.SPADE_ANIM;
			finalDelay = 3;
		}
	}
	final int animation = finalAnimation;
	stoner.getProfession().lock(finalDelay);
	stoner.getUpdateFlags().sendAnimation(new Animation(animation));
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, finalDelay, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		stoner.getUpdateFlags().sendAnimation(new Animation(animation));
		if (flowerStages[flowerFieldsData.getFlowerIndex()] <= 2) {
			flowerStages[flowerFieldsData.getFlowerIndex()]++;
			stoner.getBox().add(6055, 1);
		} else {
			flowerStages[flowerFieldsData.getFlowerIndex()] = 3;
			stop();
		}
		stoner.getProfession().addExperience(Professions.CULTIVATION, CLEARING_EXPERIENCE);
		flowerTimer[flowerFieldsData.getFlowerIndex()] = Cultivation.getMinutesCounter(stoner);
		updateFlowerStates();
		if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
			stop();
			return;
		}
		}

		@Override
		public void onStop() {
		resetFlowers(flowerFieldsData.getFlowerIndex());
		stoner.send(new SendMessage("You clear the patch."));
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;

	}

	/* planting the seeds */

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	final FlowerData flowerData = FlowerData.forId(seedId);
	if (flowerFieldsData == null || flowerData == null) {
		return false;
	}
	if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3) {
		DialogueManager.sendStatement(stoner, "You can't plant a seed here.");
		return false;
	}
	if (flowerData.getGradeRequired() > stoner.getGrades()[Professions.CULTIVATION]) {
		DialogueManager.sendStatement(stoner, "You need a cultivation grade of " + flowerData.getGradeRequired() + " to plant this seed.");
		return true;
	}
	if (!stoner.getEquipment().isWearingItem(CultivationConstants.SEED_DIBBER)) {
		DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant seed here.");
		return true;
	}

	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SEED_DIBBING));
	flowerStages[flowerFieldsData.getFlowerIndex()] = 4;
	stoner.getBox().remove(seedId, 1);

	stoner.getProfession().lock(3);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 3, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		flowerState[flowerFieldsData.getFlowerIndex()] = 0;
		flowerSeeds[flowerFieldsData.getFlowerIndex()] = seedId;
		flowerTimer[flowerFieldsData.getFlowerIndex()] = Cultivation.getMinutesCounter(stoner);
		stoner.getProfession().addExperience(Professions.CULTIVATION, flowerData.getPlantingXp());
		stop();
		}

		@Override
		public void onStop() {
		updateFlowerStates();
		stoner.setController(controller);
		}
	});
	return true;
	}

	@SuppressWarnings("unused")
	private void displayAll() {
	for (int i = 0; i < flowerStages.length; i++) {
		System.out.println("index : " + i);
		System.out.println("state : " + flowerState[i]);
		System.out.println("seeds : " + flowerSeeds[i]);
		System.out.println("grade : " + flowerStages[i]);
		System.out.println("timer : " + flowerTimer[i]);
		System.out.println("disease chance : " + diseaseChance[i]);
		System.out.println("-----------------------------------------------------------------");
	}
	}

	/* harvesting the plant resulted */

	public boolean harvest(int objectX, int objectY) {
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	if (flowerFieldsData == null) {
		return false;
	}
	final FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
	if (flowerData == null) {
		return false;
	}
	if (!stoner.getEquipment().isWearingItem(CultivationConstants.SPADE)) {
		DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to harvest here.");
		return true;
	}

	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SPADE_ANIM));
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		resetFlowers(flowerFieldsData.getFlowerIndex());
		flowerStages[flowerFieldsData.getFlowerIndex()] = 3;
		flowerTimer[flowerFieldsData.getFlowerIndex()] = Cultivation.getMinutesCounter(stoner);
		stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SPADE_ANIM));
		stoner.send(new SendMessage("You harvest the crop, and get some vegetables."));
		stoner.getBox().add(flowerData.getHarvestId(), flowerData.getHarvestId() == 5099 || flowerData.getHarvestId() == 5100 ? 3 : 1);
		stoner.getProfession().addExperience(Professions.CULTIVATION, flowerData.getHarvestXp());
		stop();
		}

		@Override
		public void onStop() {
		updateFlowerStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;
	}

	/* putting compost onto the plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	if (itemId != 6032 && itemId != 6034) {
		return false;
	}
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	if (flowerFieldsData == null) {
		return false;
	}
	if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3 || flowerState[flowerFieldsData.getFlowerIndex()] == 5) {
		stoner.send(new SendMessage("This patch doesn't need compost."));
		return true;
	}
	stoner.getBox().remove(itemId, 1);
	stoner.getBox().add(1925, 1);

	stoner.send(new SendMessage("You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch."));
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.PUTTING_COMPOST));
	stoner.getProfession().addExperience(Professions.CULTIVATION, itemId == 6034 ? Compost.SUPER_COMPOST_EXP_USE : Compost.COMPOST_EXP_USE);

	stoner.getProfession().lock(7);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 7, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		diseaseChance[flowerFieldsData.getFlowerIndex()] *= itemId == 6032 ? COMPOST_CHANCE : SUPERCOMPOST_CHANCE;
		flowerState[flowerFieldsData.getFlowerIndex()] = 5;
		stop();
		}

		@Override
		public void onStop() {
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;
	}

	/* inspecting a plant */

	public boolean inspect(int objectX, int objectY) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	if (flowerFieldsData == null) {
		return false;
	}
	final InspectData inspectData = InspectData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
	final FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
	if (flowerState[flowerFieldsData.getFlowerIndex()] == 2) {
		DialogueManager.sendStatement(stoner, "This plant is diseased. Use a PK 13-14 on it to cure it, ", "or clear the patch with a spade.");
		return true;
	} else if (flowerState[flowerFieldsData.getFlowerIndex()] == 3) {
		DialogueManager.sendStatement(stoner, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
		return true;
	}
	if (flowerStages[flowerFieldsData.getFlowerIndex()] == 0) {
		DialogueManager.sendStatement(stoner, "This is a flower patch. The soil has not been treated.", "The patch needs weeding.");
	} else if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
		DialogueManager.sendStatement(stoner, "This is a flower patch. The soil has not been treated.", "The patch is empty and weeded.");
	} else if (inspectData != null && flowerData != null) {
		stoner.send(new SendMessage("You bend down and start to inspect the patch..."));

		stoner.getUpdateFlags().sendAnimation(new Animation(1331));
		stoner.getProfession().lock(5);
		Controller controller = stoner.getController();
		stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		TaskQueue.queue(new Task(stoner, 5, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
			@Override
			public void execute() {
			if (flowerStages[flowerFieldsData.getFlowerIndex()] - 4 < inspectData.getMessages().length - 2) {
				DialogueManager.sendStatement(stoner, inspectData.getMessages()[flowerStages[flowerFieldsData.getFlowerIndex()] - 4]);
			} else if (flowerStages[flowerFieldsData.getFlowerIndex()] < flowerData.getEndingState() - flowerData.getStartingState() + 4) {
				DialogueManager.sendStatement(stoner, inspectData.getMessages()[inspectData.getMessages().length - 2]);
			} else {
				DialogueManager.sendStatement(stoner, inspectData.getMessages()[inspectData.getMessages().length - 1]);
			}
			stop();
			}

			@Override
			public void onStop() {
			stoner.getUpdateFlags().sendAnimation(new Animation(1332));
			stoner.setController(controller);
			}
		});
	}
	return true;
	}

	/* Curing the plant */

	public boolean curePlant(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	if (flowerFieldsData == null || itemId != 6036) {
		return false;
	}
	final FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
	if (flowerData == null) {
		return false;
	}
	if (flowerState[flowerFieldsData.getFlowerIndex()] != 2) {
		stoner.send(new SendMessage("This plant doesn't need to be cured."));
		return true;
	}
	stoner.getBox().remove(itemId, 1);
	stoner.getBox().add(229, 1);
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.CURING_ANIM));
	flowerState[flowerFieldsData.getFlowerIndex()] = 0;
	stoner.getProfession().lock(7);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 7, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		stoner.send(new SendMessage("You cure the plant with a PK 13-14."));
		stop();
		}

		@Override
		public void onStop() {
		updateFlowerStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;
	}

	/* Planting scarecrow to push off the birds */

	public boolean plantScareCrow(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	if (flowerFieldsData == null || itemId != SCARECROW) {
		return false;
	}
	if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3) {
		stoner.send(new SendMessage("You need to clear the patch before planting a scarecrow"));
		return false;
	}
	stoner.getBox().remove(SCARECROW, 1);
	stoner.getUpdateFlags().sendAnimation(new Animation(832));
	stoner.getProfession().lock(2);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		stoner.send(new SendMessage("You put a scarecrow on the flower patch, and some weeds start to grow around it."));
		flowerSeeds[flowerFieldsData.getFlowerIndex()] = 0x24;
		flowerStages[flowerFieldsData.getFlowerIndex()] = 4;
		flowerTimer[flowerFieldsData.getFlowerIndex()] = Cultivation.getMinutesCounter(stoner);
		stop();
		}

		@Override
		public void onStop() {
		updateFlowerStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;
	}

	@SuppressWarnings("unused")
	private void resetFlowers() {
	for (int i = 0; i < flowerStages.length; i++) {
		flowerSeeds[i] = 0;
		flowerState[i] = 0;
		diseaseChance[i] = 0;
	}
	}

	/* reseting the patches */

	private void resetFlowers(int index) {
	flowerSeeds[index] = 0;
	flowerState[index] = 0;
	diseaseChance[index] = 1;
	}

	/* checking if the patch is raked */

	public boolean checkIfRaked(int objectX, int objectY) {
	final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Point(objectX, objectY));
	if (flowerFieldsData == null)
		return false;
	if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3)
		return true;
	return false;
	}
}