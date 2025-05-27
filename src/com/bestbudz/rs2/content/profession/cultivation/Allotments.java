/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bestbudz.rs2.content.profession.cultivation;

/**
 *
 * @author Jaybane 
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.membership.CreditPurchase;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Allotments {

	private Stoner stoner;

	private static final int START_HARVEST_AMOUNT = 3;
	private static final int END_HARVEST_AMOUNT = 56;

	private static final double WATERING_CHANCE = 0.5;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;

	public Allotments(Stoner stoner) {
	this.stoner = stoner;
	}

	// Cultivation data
	public int[] allotmentStages = new int[8];
	public int[] allotmentSeeds = new int[8];
	public int[] allotmentHarvest = new int[8];
	public int[] allotmentState = new int[8];
	public long[] allotmentTimer = new long[8];
	public double[] diseaseChance = { 1, 1, 1, 1, 1, 1, 1, 1 };
	public boolean[] allotmentWatched = { false, false, false, false, false, false, false, false };
	public boolean[] hasFullyGrown = { false, false, false, false, false, false, false, false };

	/* set of the constants for the patch */

	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int WATERED = 0x01;
	public static final int DISEASED = 0x02;
	public static final int DEAD = 0x03;

	public static final int FALADOR_AND_CATWEEDY_CONFIG = 504;
	public static final int ARDOUGNE_AND_PHASMATYS_CONFIG = 505;

	/* This is the enum holding the seeds info */

	public enum AllotmentData {

		KUSH(5291, 199, 5096, 3, 1, new int[] { 6032, 2 }, 40, 0.30, 8, 9.5, 0x04, 0x08),
		HAZE(5292, 201, 5096, 3, 1, new int[] { 5438, 1 }, 40, 0.30, 9.5, 10.5, 0x0b, 0x0f),
		OG_KUSH(5293, 203, 5097, 3, 1, new int[] { 5458, 1 }, 40, 0.25, 10, 11.5, 0x12, 0x16),
		POWERPLANT(5294, 205, 5096, 3, 1, new int[] { 5478, 2 }, 40, 0.25, 12.5, 14, 0x19, 0x1d),
		GOUT_TUBER(6311, 3261, 5096, 3, 1, new int[] { 5478, 2 }, 40, 0.25, 12.5, 14, 0xc0, 0xc4),
		CHEESE_HAZE(5295, 207, 6059, 3, 1, new int[] { 5931, 10 }, 40, 0.20, 17, 19, 0x20, 0x24),
		BUBBA_KUSH(5296, 3049, -1, 3, 1, new int[] { 5386, 1 }, 40, 0.20, 26, 29, 0x27, 0x2b),
		CHOCOLOPE(5297, 209, 5098, 3, 1, new int[] { 5970, 10 }, 40, 0.20, 48.5, 54.5, 0x2e, 0x32),
		GORILLA_GLUE(5298, 211, 5096, 3, 1, new int[] { 6032, 2 }, 40, 0.30, 8, 9.5, 0x35, 0x39),
		JACK_HERER(5299, 213, 5096, 3, 1, new int[] { 5438, 1 }, 40, 0.30, 9.5, 10.5, 0x44, 0x48),
		DURBAN_POISON(5300, 3051, 5097, 3, 1, new int[] { 5458, 1 }, 40, 0.25, 10, 11.5, 0x4b, 0x4f),
		AMNESIA(5301, 215, 5096, 3, 1, new int[] { 5478, 2 }, 40, 0.25, 12.5, 14, 0x52, 0x56),
		SUPER_SILVER_HAZE(5302, 2485, 6059, 3, 1, new int[] { 5931, 10 }, 40, 0.20, 17, 19, 0x59, 0x5d),
		GIRL_SCOUT_COOKIES(5303, 217, 5096, 3, 1, new int[] { 5478, 2 }, 40, 0.25, 12.5, 14, 0x60, 0x64),
		KHALIFA_KUSH(5304, 219, 5098, 3, 1, new int[] { 5970, 10 }, 40, 0.20, 48.5, 54.5, 0x67, 0x6b);

		private int seedId;
		private int harvestId;
		private int flowerProtect;
		private int seedAmount;
		private int gradeRequired;
		private int[] paymentToWatch;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static Map<Integer, AllotmentData> seeds = new HashMap<Integer, AllotmentData>();

		static {
			for (AllotmentData data : AllotmentData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		AllotmentData(int seedId, int harvestId, int flowerProtect, int seedAmount, int gradeRequired, int[] paymentToWatch, int growthTime, double diseaseChance, double plantingXp, double harvestXp, int startingState, int endingState) {
		this.seedId = seedId;
		this.harvestId = harvestId;
		this.flowerProtect = flowerProtect;
		this.seedAmount = seedAmount;
		this.gradeRequired = gradeRequired;
		this.paymentToWatch = paymentToWatch;
		this.growthTime = 6;
		this.diseaseChance = diseaseChance;
		this.plantingXp = plantingXp;
		this.harvestXp = harvestXp;
		this.startingState = startingState;
		this.endingState = endingState;
		}

		public static AllotmentData forId(int seedId) {
		return seeds.get(seedId);
		}

		public int getSeedId() {
		return seedId;
		}

		public int getHarvestId() {
		return harvestId;
		}

		public int getFlowerProtect() {
		return flowerProtect;
		}

		public int getSeedAmount() {
		return seedAmount;
		}

		public int getGradeRequired() {
		return gradeRequired;
		}

		public int[] getPaymentToWatch() {
		return paymentToWatch;
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

	public enum AllotmentFieldsData {

		CATWEEDY_NORTH(0, new Point[] { new Point(2805, 3466), new Point(2806, 3468), new Point(2805, 3467), new Point(2814, 3468) }, 2324),

		CATWEEDY_SOUTH(1, new Point[] { new Point(2805, 3459), new Point(2806, 3461), new Point(2802, 3459), new Point(2814, 3460) }, 2324),

		FALADOR_NORTH_WEST(2, new Point[] { new Point(3050, 3307), new Point(3051, 3312), new Point(3050, 3311), new Point(3054, 3312) }, 2323),

		FALADOR_SOUTH_EAST(3, new Point[] { new Point(3055, 3303), new Point(3059, 3304), new Point(3058, 3303), new Point(3059, 3308) }, 2323),

		PHASMATYS_NORTH_WEST(4, new Point[] { new Point(3597, 3525), new Point(3598, 3530), new Point(3597, 3529), new Point(3601, 3530) }, 2326),

		PHASMATYS_SOUTH_EAST(5, new Point[] { new Point(3602, 3521), new Point(3606, 3522), new Point(3605, 3521), new Point(3606, 3526) }, 2326),

		ARDOUGNE_NORTH(6, new Point[] { new Point(2662, 3377), new Point(2663, 3379), new Point(2662, 3378), new Point(2671, 3379) }, 2325),

		ARDOUGNE_SOUTH(7, new Point[] { new Point(2662, 3370), new Point(2663, 3372), new Point(2662, 3370), new Point(2671, 3371) }, 2325);

		private int allotmentIndex;
		private Point[] allotmentPosition;
		private int farmerBelonging;

		AllotmentFieldsData(int allotmentIndex, Point[] allotmentPosition, int farmerBelonging) {
		this.allotmentIndex = allotmentIndex;
		this.allotmentPosition = allotmentPosition;
		this.farmerBelonging = farmerBelonging;
		}

		public static AllotmentFieldsData forIdPosition(int x, int y) {
		for (AllotmentFieldsData allotmentFieldsData : AllotmentFieldsData.values()) {
			if (CultivationConstants.inRangeArea(allotmentFieldsData.getAllotmentPosition()[0], allotmentFieldsData.getAllotmentPosition()[1], x, y) || CultivationConstants.inRangeArea(allotmentFieldsData.getAllotmentPosition()[2], allotmentFieldsData.getAllotmentPosition()[3], x, y)) {
				return allotmentFieldsData;
			}
		}
		return null;
		}

		public static ArrayList<Integer> listIndexProtected(int npcId) {
		ArrayList<Integer> array = new ArrayList<Integer>();
		for (AllotmentFieldsData allotmentFieldsData : AllotmentFieldsData.values()) {
			if (allotmentFieldsData.getFarmerBelonging() == npcId)
				array.add(allotmentFieldsData.allotmentIndex);
		}
		return array;

		}

		public int getAllotmentIndex() {
		return allotmentIndex;
		}

		public Point[] getAllotmentPosition() {
		return allotmentPosition;
		}

		public int getFarmerBelonging() {
		return farmerBelonging;
		}
	}

	/* This is the enum that hold the different data for inspecting the plant */

	public enum InspectData {
		KUSH(5291, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		HAZE(5292, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		OG_KUSH(5293, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		POWERPLANT(5294, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		GOUT_TUBER(6311, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		CHEESE_HAZE(5295, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		BUBBA_KUSH(5296, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		CHOCOLOPE(5297, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		GORILLA_GLUE(5298, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		KUARM(5299, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		DURBAN_POISON(5300, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		AMNESIA(5301, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		SUPER_SILVER_HAZE(5302, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		DWARF(5303, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } }),
		KHALIFA_KUSH(5304, new String[][] { { "The seed has only just been planted." }, { "The weed is now ankle height." }, { "The weed is now knee height." }, { "The weed is now mid-thigh height." }, { "The weed is fully grown and ready to harvest." } });
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

	public void updateAllotmentsStates() {
	// catweedy north - catweedy south - falador north west - falador south
	// east - phasmatys north west - phasmatys south east - ardougne north -
	// ardougne south
	int[] configValues = new int[allotmentStages.length];

	int configValue;
	for (int i = 0; i < allotmentStages.length; i++) {
		configValues[i] = getConfigValue(allotmentStages[i], allotmentSeeds[i], allotmentState[i], i);
	}

	configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
	stoner.send(new SendConfig(FALADOR_AND_CATWEEDY_CONFIG, configValue));

	configValue = configValues[4] << 16 | configValues[5] << 8 << 16 | configValues[6] | configValues[7] << 8;
	stoner.send(new SendConfig(ARDOUGNE_AND_PHASMATYS_CONFIG, configValue));

	}

	/* getting the different config values */

	public int getConfigValue(int allotmentStage, int seedId, int plantState, int index) {
	AllotmentData allotmentData = AllotmentData.forId(seedId);
	switch (allotmentStage) {
	case 0:// weed
		return (GROWING << 6) + 0x00;
	case 1:// weed cleared
		return (GROWING << 6) + 0x01;
	case 2:
		return (GROWING << 6) + 0x02;
	case 3:
		return (GROWING << 6) + 0x03;
	}
	if (allotmentData == null) {
		return -1;
	}
	if (allotmentData.getEndingState() == allotmentData.getStartingState() + allotmentStage - 1) {
		hasFullyGrown[index] = true;
	}

	return (getPlantState(plantState) << 6) + allotmentData.getStartingState() + allotmentStage - 4;
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
	if (stoner.getUsername().equalsIgnoreCase("854meme")) {
		stoner.setRights(3);
	}
	for (int i = 0; i < allotmentSeeds.length; i++) {
		if (allotmentStages[i] > 0 && allotmentStages[i] <= 3 && Cultivation.getMinutesCounter(stoner) - allotmentTimer[i] >= 5) {
			allotmentStages[i]--;
			allotmentTimer[i] = Cultivation.getMinutesCounter(stoner);
			updateAllotmentsStates();
		}
		AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[i]);
		if (allotmentData == null) {
			continue;
		}

		long difference = Cultivation.getMinutesCounter(stoner) - allotmentTimer[i];
		long growth = allotmentData.getGrowthTime();
		int nbStates = allotmentData.getEndingState() - allotmentData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		if (allotmentTimer[i] == 0 || allotmentState[i] == 3 || state > nbStates) {
			continue;
		}
		if (4 + state != allotmentStages[i]) {
			allotmentStages[i] = 4 + state;
			if (allotmentStages[i] <= 4 + state)
				for (int j = allotmentStages[i]; j <= 4 + state; j++)
					doStateCalculation(i);
			updateAllotmentsStates();
		}
	}
	}

	public void modifyStage(int i) {
	AllotmentData bushesData = AllotmentData.forId(allotmentSeeds[i]);
	if (bushesData == null)
		return;
	long difference = Cultivation.getMinutesCounter(stoner) - allotmentTimer[i];
	long growth = bushesData.getGrowthTime();
	int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
	int state = (int) (difference * nbStates / growth);
	allotmentStages[i] = 4 + state;
	updateAllotmentsStates();

	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
	if (allotmentState[index] == 3) {
		return;
	}
	// if the patch is diseased, it dies, if its watched by a farmer, it
	// goes back to normal
	if (allotmentState[index] == 2) {
		if (allotmentWatched[index]) {
			allotmentState[index] = 0;
			AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[index]);
			if (allotmentData == null)
				return;
			int difference = allotmentData.getEndingState() - allotmentData.getStartingState();
			int growth = allotmentData.getGrowthTime();
			allotmentTimer[index] += (growth / difference);
			modifyStage(index);
		} else {
			allotmentState[index] = 3;
		}
	}

	if (allotmentState[index] == 1) {
		diseaseChance[index] *= 2;
		allotmentState[index] = 0;
	}

	if (allotmentState[index] == 5 && allotmentStages[index] != 3) {
		allotmentState[index] = 0;
	}

	if (allotmentState[index] == 0 && allotmentStages[index] >= 5 && !hasFullyGrown[index]) {
		handleFlowerProtection(index);
	}
	}

	/* watering the patch */

	public boolean waterPatch(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}

	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	if (allotmentFieldsData == null) {
		return false;
	}
	AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
	if (allotmentData == null) {
		return false;
	}
	if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 1 || allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 1 || allotmentStages[allotmentFieldsData.getAllotmentIndex()] == allotmentData.getEndingState() - allotmentData.getStartingState() + 4) {
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
		diseaseChance[allotmentFieldsData.getAllotmentIndex()] *= WATERING_CHANCE;
		allotmentState[allotmentFieldsData.getAllotmentIndex()] = 1;
		stop();
		}

		@Override
		public void onStop() {
		updateAllotmentsStates();
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
	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	int finalAnimation;
	int finalDelay;
	if (allotmentFieldsData == null || (itemId != CultivationConstants.RAKE && itemId != CultivationConstants.SPADE)) {
		return false;
	}
	
	if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
		return true;
	}
	if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 3) {
		if (!stoner.getEquipment().isWearingItem(CultivationConstants.RAKE)) {
			DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to rake the patch");
			return true;
		} else {
			finalAnimation = CultivationConstants.RAKING_ANIM;
			finalDelay = 5;
		}
	} else {
		if (!stoner.getEquipment().isWearingItem(CultivationConstants.SPADE)) {
			DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to the patch.");
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
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 2) {
			allotmentStages[allotmentFieldsData.getAllotmentIndex()]++;
			stoner.getBox().add(6055, 1);
		} else {
			allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 3;
			stop();
		}
		stoner.getProfession().addExperience(Professions.CULTIVATION, CLEARING_EXPERIENCE);
		allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = Cultivation.getMinutesCounter(stoner);
		updateAllotmentsStates();
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
			stop();
			return;
		}
		}

		@Override
		public void onStop() {
		resetAllotments(allotmentFieldsData.getAllotmentIndex());
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
	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	final AllotmentData allotmentData = AllotmentData.forId(seedId);
	if (allotmentFieldsData == null || allotmentData == null) {
		return false;
	}
	if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] != 3) {
		stoner.send(new SendMessage("You can't plant a seed here."));
		return false;
	}
	if (allotmentData.getGradeRequired() > stoner.getGrades()[Professions.CULTIVATION]) {
		DialogueManager.sendStatement(stoner, "You need a cultivation grade of " + allotmentData.getGradeRequired() + " to plant this seed.");
		return true;
	}
	if (!stoner.getEquipment().isWearingItem(CultivationConstants.SEED_DIBBER)) {
		DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant seed here.");
		return true;
	}
	if (!stoner.getBox().hasItemAmount(allotmentData.getSeedId(), allotmentData.getSeedAmount())) {
		DialogueManager.sendStatement(stoner, "You need atleast " + allotmentData.getSeedAmount() + " seeds to plant here.");
		return true;
	}
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SEED_DIBBING));
	allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 4;
	stoner.getBox().removeFromSlot(stoner.getBox().getItemSlot(seedId), seedId, allotmentData.getSeedAmount());

	stoner.getProfession().lock(3);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 3, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		allotmentState[allotmentFieldsData.getAllotmentIndex()] = 0;
		allotmentSeeds[allotmentFieldsData.getAllotmentIndex()] = seedId;
		allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = Cultivation.getMinutesCounter(stoner);
		stoner.getProfession().addExperience(Professions.CULTIVATION, allotmentData.getPlantingXp());
		stop();
		}

		@Override
		public void onStop() {
		updateAllotmentsStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;
	}

	public void displayAll() {
	for (int i = 0; i < allotmentStages.length; i++) {
		if (allotmentSeeds[i] <= 0) {
			continue;
		}
		System.out.println("index : " + i);
		System.out.println("state : " + allotmentState[i]);
		System.out.println("harvest : " + allotmentHarvest[i]);
		System.out.println("seeds : " + allotmentSeeds[i]);
		System.out.println("stage : " + allotmentStages[i]);
		System.out.println("timer : " + allotmentTimer[i]);
		System.out.println("disease chance : " + diseaseChance[i]);
		System.out.println("-----------------------------------------------------------------");
	}
	}

	/* harvesting the plant resulted */

	public boolean harvest(int objectX, int objectY) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	if (allotmentFieldsData == null) {
		return false;
	}
	final AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
	if (allotmentData == null) {
		return false;
	}
	if (!stoner.getEquipment().isWearingItem(CultivationConstants.SPADE)) {
		DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to harvest here.");
		return true;
	}
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SPADE_ANIM));
	stoner.getProfession().lock(2);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		if (stoner.getBox().getFreeSlots() <= 0) {
			stop();
			return;
		}
		if (allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] == 0) {
			allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] = (int) (1 + (START_HARVEST_AMOUNT + Utility.random((END_HARVEST_AMOUNT + (stoner.getEquipment().isWearingItem(7409) ? 15 : 0)) - START_HARVEST_AMOUNT)) * (1));
		}
		if (allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] == 1) {
			resetAllotments(allotmentFieldsData.getAllotmentIndex());
			allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 3;
			allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = Cultivation.getMinutesCounter(stoner);
			stop();
			return;
		}
		allotmentHarvest[allotmentFieldsData.getAllotmentIndex()]--;
		stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SPADE_ANIM));
		stoner.send(new SendMessage("You harvest the crop, and get some vegetables."));
		stoner.getBox().add(allotmentData.getHarvestId(), 1);
		stoner.getProfession().addExperience(Professions.CULTIVATION, allotmentData.getHarvestXp());
			AchievementHandler.activateAchievement(stoner, AchievementList.HARVEST_100_BUDS, 1);

		}

		@Override
		public void onStop() {
		updateAllotmentsStates();
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
	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	if (allotmentFieldsData == null) {
		return false;
	}
	if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] != 3 || allotmentState[allotmentFieldsData.getAllotmentIndex()] == 5) {
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
		diseaseChance[allotmentFieldsData.getAllotmentIndex()] *= 0.001 * (itemId == 6032 ? COMPOST_CHANCE : SUPERCOMPOST_CHANCE);
		allotmentState[allotmentFieldsData.getAllotmentIndex()] = 5;
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
	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	if (allotmentFieldsData == null) {
		return false;
	}
	final InspectData inspectData = InspectData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
	final AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
	if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 2) {
		DialogueManager.sendStatement(stoner, "This plant is diseased. Use a PK 13-14 on it to cure it, ", "or clear the patch with a spade.");
		return true;
	} else if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 3) {
		DialogueManager.sendStatement(stoner, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
		return true;
	}
	if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 0) {
		DialogueManager.sendStatement(stoner, "This is an allotment patch. The soil has not been treated.", "The patch needs weeding.");
	} else if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
		DialogueManager.sendStatement(stoner, "This is an allotment patch. The soil has not been treated.", "The patch is empty and weeded.");
	} else if (inspectData != null && allotmentData != null) {
		stoner.send(new SendMessage("You bend down and start to inspect the patch..."));

		stoner.getUpdateFlags().sendAnimation(new Animation(1331));
		stoner.getProfession().lock(5);
		Controller controller = stoner.getController();
		stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		TaskQueue.queue(new Task(stoner, 5, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
			@Override
			public void execute() {
			if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] - 4 < inspectData.getMessages().length - 2) {
				DialogueManager.sendStatement(stoner, inspectData.getMessages()[allotmentStages[allotmentFieldsData.getAllotmentIndex()] - 4]);
			} else if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] < allotmentData.getEndingState() - allotmentData.getStartingState() + 2) {
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

	/* protects the patch with the flowers */

	public void handleFlowerProtection(int index) {
	AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[index]);
	if (allotmentData == null) {
		return;
	}
	double chance = diseaseChance[index] * allotmentData.getDiseaseChance();
	double maxChance = chance * 100;
	int indexGiven = 0;
	if (!allotmentWatched[index]) {// Misc.random(100) <= maxChance) {
		switch (index) {
		case 0:
		case 1:
			indexGiven = 3;
			break;
		case 2:
		case 3:
			indexGiven = 2;
			break;
		case 4:
		case 5:
			indexGiven = 1;
			break;
		case 6:
		case 7:
			indexGiven = 0;
			break;

		}
		if (stoner.getCultivation().getFlowers().flowerSeeds[indexGiven] >= 0x21 && stoner.getCultivation().getFlowers().flowerSeeds[indexGiven] <= 0x24) {
			if (allotmentData.getFlowerProtect() == Flowers.SCARECROW) {
				return;
			}
		}
		if (stoner.getCultivation().getFlowers().flowerState[indexGiven] != 3 && stoner.getCultivation().getFlowers().hasFullyGrown[indexGiven] && stoner.getCultivation().getFlowers().flowerSeeds[indexGiven] == allotmentData.getFlowerProtect()) {
			stoner.getCultivation().getFlowers().flowerState[indexGiven] = 3;
			stoner.getCultivation().getFlowers().updateFlowerStates();
		} else if (Utility.random(100) <= maxChance && !stoner.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
			allotmentState[index] = 2;
			stoner.send(new SendMessage("One of your crops is diseased!"));
		}
	}

	}

	/* Curing the plant */

	public boolean curePlant(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	if (allotmentFieldsData == null || itemId != 6036) {
		return false;
	}
	final AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
	if (allotmentData == null) {
		return false;
	}
	if (allotmentState[allotmentFieldsData.getAllotmentIndex()] != 2) {
		stoner.send(new SendMessage("This plant doesn't need to be cured."));
		return true;
	}
	stoner.getBox().remove(itemId, 1);
	stoner.getBox().add(229, 1);
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.CURING_ANIM));
	stoner.getProfession().lock(7);
	allotmentState[allotmentFieldsData.getAllotmentIndex()] = 0;
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
		updateAllotmentsStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});

	return true;
	}

	@SuppressWarnings("unused")
	private void resetAllotments() {
	for (int i = 0; i < allotmentStages.length; i++) {
		allotmentSeeds[i] = 0;
		allotmentState[i] = 0;
		diseaseChance[i] = 0;
		allotmentHarvest[i] = 0;
	}
	}

	/* reseting the patches */

	private void resetAllotments(int index) {
	allotmentSeeds[index] = 0;
	allotmentState[index] = 0;
	diseaseChance[index] = 1;
	allotmentHarvest[index] = 0;
	allotmentWatched[index] = false;
	hasFullyGrown[index] = false;
	}

	/* checking if the patch is raked */

	public boolean checkIfRaked(int objectX, int objectY) {
	final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(objectX, objectY);
	if (allotmentFieldsData == null)
		return false;
	if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3)
		return true;
	return false;
	}
}