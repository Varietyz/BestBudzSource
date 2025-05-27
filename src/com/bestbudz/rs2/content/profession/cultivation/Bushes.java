package com.bestbudz.rs2.content.profession.cultivation;

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
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Bushes {

	public static final int GROWING = 0x00;
	public static final int DISEASED = 0x01;
	public static final int DEAD = 0x02;
	public static final int CHECK = 0x03;
	public static final int MAIN_BUSHES_CONFIG = 509;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	private final Stoner stoner;
	public int[] bushesStages = new int[4];
	public int[] bushesSeeds = new int[4];
	public int[] bushesState = new int[4];
	public long[] bushesTimer = new long[4];
	public double[] diseaseChance = { 1, 1, 1, 1 };
	public boolean[] hasFullyGrown = { false, false, false, false };
	public boolean[] bushesWatched = { false, false, false, false };

	public Bushes(Stoner stoner) {
	this.stoner = stoner;
	}

	public void updateBushesStates() {
	int[] configValues = new int[bushesStages.length];

	int configValue;
	for (int i = 0; i < bushesStages.length; i++) {
		configValues[i] = getConfigValue(bushesStages[i], bushesSeeds[i], bushesState[i], i);
	}

	configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
	stoner.send(new SendConfig(MAIN_BUSHES_CONFIG, configValue));
	}

	public int getConfigValue(int bushesStage, int seedId, int plantState, int index) {
	BushesData bushesData = BushesData.forId(seedId);
	switch (bushesStage) {
	case 0:
		return 0;
	case 1:
		return 0x01;
	case 2:
		return 0x02;
	case 3:
		return 0x03;
	}
	if (bushesData == null) {
		return -1;
	}
	if (bushesStage > bushesData.getEndingState() - bushesData.getStartingState() - 1) {
		hasFullyGrown[index] = true;
	}
	if (getPlantState(plantState) == 3)
		return (getPlantState(plantState) << 6) + bushesData.getCheckHealthState();
	if (seedId == 5106) {
		if (getPlantState(plantState) == 1) {
			return bushesData.getStartingState() + bushesStage - 4 + 12;
		} else if (getPlantState(plantState) == 2) {
			return bushesData.getStartingState() + bushesStage - 4 + 20;
		}
	}
	return (getPlantState(plantState) << 6) + bushesData.getStartingState() + bushesStage - 4 + (getPlantState(plantState) == 2 ? -1 : 0);
	}

	public int getPlantState(int plantState) {
	switch (plantState) {
	case 0:
		return GROWING;
	case 1:
		return DISEASED;
	case 2:
		return DEAD;
	case 3:
		return CHECK;
	}
	return -1;
	}

	public void doCalculations() {
	for (int i = 0; i < bushesSeeds.length; i++) {
		if (bushesStages[i] > 0 && bushesStages[i] <= 3 && Cultivation.getMinutesCounter(stoner) - bushesTimer[i] >= 5) {
			bushesStages[i]--;
			bushesTimer[i] = Cultivation.getMinutesCounter(stoner);
			updateBushesStates();
			continue;
		}
		BushesData bushesData = BushesData.forId(bushesSeeds[i]);
		if (bushesData == null) {
			continue;
		}

		long difference = Cultivation.getMinutesCounter(stoner) - bushesTimer[i];
		long growth = bushesData.getGrowthTime();
		int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		if (bushesTimer[i] == 0 || bushesState[i] == 2 || bushesState[i] == 3 || (state > nbStates)) {
			continue;
		}
		if (4 + state != bushesStages[i]) {
			if (bushesStages[i] == bushesData.getEndingState() - bushesData.getStartingState() - 1) {
				bushesStages[i] = bushesData.getEndingState() - bushesData.getStartingState() + 4;
				bushesState[i] = 3;
				updateBushesStates();
				return;
			}
			bushesStages[i] = 4 + state;
			if (bushesStages[i] <= 4 + state)
				for (int j = bushesStages[i]; j <= 4 + state; j++)
					doStateCalculation(i);
			updateBushesStates();
		}
	}
	}

	public void modifyStage(int i) {
	BushesData bushesData = BushesData.forId(bushesSeeds[i]);
	if (bushesData == null)
		return;
	long difference = Cultivation.getMinutesCounter(stoner) - bushesTimer[i];
	long growth = bushesData.getGrowthTime();
	int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
	int state = (int) (difference * nbStates / growth);
	bushesStages[i] = 4 + state;
	updateBushesStates();

	}

	public void doStateCalculation(int index) {
	if (bushesState[index] == 2) {
		return;
	}
	if (bushesState[index] == 1) {
		if (bushesWatched[index]) {
			bushesState[index] = 0;
			BushesData bushesData = BushesData.forId(bushesSeeds[index]);
			if (bushesData == null)
				return;
			System.out.println(bushesSeeds[index]);
			int difference = bushesData.getEndingState() - bushesData.getStartingState();
			int growth = bushesData.getGrowthTime();
			bushesTimer[index] += (growth / difference);
			modifyStage(index);
		} else {
			bushesState[index] = 2;
		}
	}

	if (bushesState[index] == 5 && bushesStages[index] != 2) {
		bushesState[index] = 0;
	}

	if (bushesState[index] == 0 && bushesStages[index] >= 5 && !hasFullyGrown[index]) {
		BushesData bushesData = BushesData.forId(bushesSeeds[index]);
		if (bushesData == null) {
			return;
		}

		double chance = diseaseChance[index] * bushesData.getDiseaseChance();
		int maxChance = (int) chance * 100;
		if (Utility.random(100) <= maxChance && !stoner.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
			bushesState[index] = 1;
		}
	}
	}

	public boolean clearPatch(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(objectX, objectY);
	int finalAnimation;
	int finalDelay;
	if (bushesFieldsData == null || (itemId != CultivationConstants.RAKE && itemId != CultivationConstants.SPADE)) {
		return false;
	}
	if (bushesStages[bushesFieldsData.getBushesIndex()] == 3) {
		return true;
	}
	if (bushesStages[bushesFieldsData.getBushesIndex()] <= 3) {
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
		if (bushesStages[bushesFieldsData.getBushesIndex()] <= 2) {
			bushesStages[bushesFieldsData.getBushesIndex()]++;
			stoner.getBox().add(6055, 1);
		} else {
			bushesStages[bushesFieldsData.getBushesIndex()] = 3;
			stop();
		}
		stoner.getProfession().addExperience(Professions.CULTIVATION, CLEARING_EXPERIENCE);
		bushesTimer[bushesFieldsData.getBushesIndex()] = Cultivation.getMinutesCounter(stoner);
		updateBushesStates();
		if (bushesStages[bushesFieldsData.getBushesIndex()] == 3) {
			stop();
		}
		}

		@Override
		public void onStop() {
		resetBushes(bushesFieldsData.getBushesIndex());
		stoner.send(new SendMessage("You clear the patch."));
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;

	}

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(objectX, objectY);
	final BushesData bushesData = BushesData.forId(seedId);
	if (bushesFieldsData == null || bushesData == null) {
		return false;
	}
	if (bushesStages[bushesFieldsData.getBushesIndex()] != 3) {
		stoner.send(new SendMessage("You can't plant a seed here."));
		return false;
	}
	if (bushesData.getGradeRequired() > stoner.getGrades()[Professions.CULTIVATION]) {
		DialogueManager.sendStatement(stoner, "You need a cultivation grade of " + bushesData.getGradeRequired() + " to plant this seed.");
		return true;
	}
	if (!stoner.getEquipment().isWearingItem(CultivationConstants.SEED_DIBBER)) {
		DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant seed here.");
		return true;
	}
	if (stoner.getBox().getItemAmount(bushesData.getSeedId()) < bushesData.getSeedAmount()) {
		DialogueManager.sendStatement(stoner, "You need atleast " + bushesData.getSeedAmount() + " seeds to plant here.");
		return true;
	}
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SEED_DIBBING));
	bushesStages[bushesFieldsData.getBushesIndex()] = 4;
	stoner.getBox().remove(seedId, bushesData.getSeedAmount());

	stoner.getProfession().lock(3);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 3, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {

		@Override
		public void execute() {
		bushesState[bushesFieldsData.getBushesIndex()] = 0;
		bushesSeeds[bushesFieldsData.getBushesIndex()] = seedId;
		bushesTimer[bushesFieldsData.getBushesIndex()] = Cultivation.getMinutesCounter(stoner);
		stoner.getProfession().addExperience(Professions.CULTIVATION, bushesData.getPlantingXp());
		stop();
		}

		@Override
		public void onStop() {
		updateBushesStates();
		stoner.setController(controller);
		}
	});
	return true;
	}

	@SuppressWarnings("unused")
	private void displayAll() {
	for (int i = 0; i < bushesStages.length; i++) {
		System.out.println("index : " + i);
		System.out.println("state : " + bushesState[i]);
		System.out.println("seeds : " + bushesSeeds[i]);
		System.out.println("grade : " + bushesStages[i]);
		System.out.println("timer : " + bushesTimer[i]);
		System.out.println("disease chance : " + diseaseChance[i]);
		System.out.println("-----------------------------------------------------------------");
	}
	}

	public boolean harvestOrCheckHealth(int objectX, int objectY) {
	final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(objectX, objectY);
	if (bushesFieldsData == null) {
		return false;
	}
	final BushesData bushesData = BushesData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);
	if (bushesData == null) {
		return false;
	}
	if (stoner.getBox().getFreeSlots() <= 0) {
		stoner.send(new SendMessage("Not enough space in your box."));
		return true;
	}
	stoner.getUpdateFlags().sendAnimation(new Animation(832));
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {

		@Override
		public void execute() {
		if (stoner.getBox().getFreeSlots() <= 0) {
			stop();
			return;
		}

		if (bushesState[bushesFieldsData.getBushesIndex()] == 3) {
			stoner.send(new SendMessage("You inspect the bush for signs of disease and find that it's in perfect health."));
			stoner.getProfession().addExperience(Professions.CULTIVATION, bushesData.getCheckHealthXp());
			bushesState[bushesFieldsData.getBushesIndex()] = 0;
			hasFullyGrown[bushesFieldsData.getBushesIndex()] = false;
			bushesTimer[bushesFieldsData.getBushesIndex()] = Cultivation.getMinutesCounter(stoner) - bushesData.getGrowthTime();
			bushesStages[bushesFieldsData.getBushesIndex()] -= 2;
			modifyStage(bushesFieldsData.getBushesIndex());
			stop();
			return;
		}
		stoner.send(new SendMessage("You harvest the crop, and pick some berries."));
		stoner.getBox().add(bushesData.getHarvestId(), 1);
		stoner.getProfession().addExperience(Professions.CULTIVATION, bushesData.getHarvestXp());
		bushesTimer[bushesFieldsData.getBushesIndex()] = Cultivation.getMinutesCounter(stoner);
		int difference = bushesData.getEndingState() - bushesData.getStartingState();
		int growth = bushesData.getGrowthTime();
		lowerStage(bushesFieldsData.getBushesIndex(), growth - (growth / difference) * (difference + 5 - bushesStages[bushesFieldsData.getBushesIndex()]));
		modifyStage(bushesFieldsData.getBushesIndex());
		stop();
		}

		@Override
		public void onStop() {
		stoner.setController(controller);
		}
	});
	return true;
	}

	public void lowerStage(int index, int timer) {
	hasFullyGrown[index] = false;
	bushesTimer[index] -= timer;
	}

	public boolean putCompost(int objectX, int objectY, final int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	if (itemId != 6032 && itemId != 6034) {
		return false;
	}
	final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(objectX, objectY);
	if (bushesFieldsData == null) {
		return false;
	}

	if (bushesStages[bushesFieldsData.getBushesIndex()] != 3 || bushesState[bushesFieldsData.getBushesIndex()] == 5) {
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
		diseaseChance[bushesFieldsData.getBushesIndex()] *= itemId == 6032 ? COMPOST_CHANCE : SUPERCOMPOST_CHANCE;
		bushesState[bushesFieldsData.getBushesIndex()] = 5;
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

	public boolean inspect(int objectX, int objectY) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(objectX, objectY);
	if (bushesFieldsData == null) {
		return false;
	}
	final InspectData inspectData = InspectData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);
	final BushesData bushesData = BushesData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);
	if (bushesState[bushesFieldsData.getBushesIndex()] == 1) {
		DialogueManager.sendStatement(stoner, "This plant is diseased. Use a PK 13-14 on it to cure it, ", "or clear the patch with a spade.");
		return true;
	} else if (bushesState[bushesFieldsData.getBushesIndex()] == 2) {
		DialogueManager.sendStatement(stoner, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
		return true;
	} else if (bushesState[bushesFieldsData.getBushesIndex()] == 3) {
		DialogueManager.sendStatement(stoner, "This plant has fully grown. You can check it's health", "to gain some cultivation experiences.");
		return true;
	}
	if (bushesStages[bushesFieldsData.getBushesIndex()] == 0) {
		DialogueManager.sendStatement(stoner, "This is a bush patch. The soil has not been treated.", "The patch needs weeding.");
	} else if (bushesStages[bushesFieldsData.getBushesIndex()] == 3) {
		DialogueManager.sendStatement(stoner, "This is a bush patch. The soil has not been treated.", "The patch is empty and weeded.");
	} else if (inspectData != null && bushesData != null) {
		stoner.send(new SendMessage("You bend down and start to inspect the patch..."));

		stoner.getUpdateFlags().sendAnimation(new Animation(1331));
		stoner.getProfession().lock(5);
		Controller controller = stoner.getController();
		stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		TaskQueue.queue(new Task(stoner, 5, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {

			@Override
			public void execute() {
			if (bushesStages[bushesFieldsData.getBushesIndex()] - 4 < inspectData.getMessages().length - 2) {
				DialogueManager.sendStatement(stoner, inspectData.getMessages()[bushesStages[bushesFieldsData.getBushesIndex()] - 4]);
			} else if (bushesStages[bushesFieldsData.getBushesIndex()] < bushesData.getEndingState() - bushesData.getStartingState() + 2) {
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

	public boolean curePlant(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(objectX, objectY);
	if (bushesFieldsData == null || itemId != 6036) {
		return false;
	}
	final BushesData bushesData = BushesData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);
	if (bushesData == null) {
		return false;
	}
	if (bushesState[bushesFieldsData.getBushesIndex()] != 1) {
		stoner.send(new SendMessage("This plant doesn't need to be cured."));
		return true;
	}
	stoner.getBox().remove(itemId, 1);
	stoner.getBox().add(229, 1);
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.CURING_ANIM));
	stoner.getProfession().lock(7);
	bushesState[bushesFieldsData.getBushesIndex()] = 0;
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
		updateBushesStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;

	}

	private void resetBushes(int index) {
	bushesSeeds[index] = 0;
	bushesState[index] = 0;
	diseaseChance[index] = 1;
	hasFullyGrown[index] = false;
	bushesWatched[index] = false;
	}

	public boolean checkIfRaked(int objectX, int objectY) {
	final BushesFieldsData bushesFieldData = BushesFieldsData.forIdPosition(objectX, objectY);
	if (bushesFieldData == null)
		return false;
		return bushesStages[bushesFieldData.getBushesIndex()] == 3;
	}

	public enum BushesData {
		REDBERRY(5101, 1951, 1, 10, new int[] { 5478, 4 }, 100, 0.20, 11.5, 4.5, 0x05, 0x0e, 0x09, 0x3a, 64),
		CADAVABERRY(5102, 753, 1, 22, new int[] { 5968, 3 }, 140, 0.20, 18, 7, 0x0f, 0x19, 0x14, 0x3b, 102.5),
		DWELLBERRY(5103, 2126, 1, 36, new int[] { 5406, 3 }, 140, 0.20, 31.5, 12, 0x1a, 0x25, 0x20, 0x3c, 177.5),
		JANGERBERRY(5104, 247, 1, 48, new int[] { 5982, 6 }, 160, 0.20, 50.5, 19, 0x26, 0x32, 0x2d, 0x3d, 284.5),
		WHITEBERRY(5105, 239, 1, 59, new int[] { 6004, 8 }, 160, 0.20, 78, 29, 0x33, 0x3f, 0x3a, 0x3e, 437.5),
		POISONIVYBERRY(5106, 6018, 1, 70, null, 160, 0.20, 120, 45, 0xc5, 0xd1, 0xcc, 0x3f, 674);

		private static final Map<Integer, BushesData> seeds = new HashMap<Integer, BushesData>();

		static {
			for (BushesData data : BushesData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		private final int seedId;
		private final int harvestId;
		private final int seedAmount;
		private final int gradeRequired;
		private final int[] paymentToWatch;
		private final int growthTime;
		private final double diseaseChance;
		private final double plantingXp;
		private final double harvestXp;
		private final int startingState;
		private final int endingState;
		private final int limitState;
		private final int checkHealthState;
		private final double checkHealthExperience;

		BushesData(int seedId, int harvestId, int seedAmount, int gradeRequired, int[] paymentToWatch, int growthTime, double diseaseChance, double plantingXp, double harvestXp, int startingState, int endingState, int limitState, int checkHealthState, double checkHealthExperience) {
		this.seedId = seedId;
		this.harvestId = harvestId;
		this.seedAmount = seedAmount;
		this.gradeRequired = gradeRequired;
		this.paymentToWatch = paymentToWatch;
		this.growthTime = growthTime;
		this.diseaseChance = diseaseChance;
		this.plantingXp = plantingXp;
		this.harvestXp = harvestXp;
		this.startingState = startingState;
		this.endingState = endingState;
		this.limitState = limitState;
		this.checkHealthState = checkHealthState;
		this.checkHealthExperience = checkHealthExperience;
		}

		public static BushesData forId(int seedId) {
		return seeds.get(seedId);
		}

		public int getSeedId() {
		return seedId;
		}

		public int getHarvestId() {
		return harvestId;
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

		public int getLimitState() {
		return limitState;
		}

		public int getCheckHealthState() {
		return checkHealthState;
		}

		public double getCheckHealthXp() {
		return checkHealthExperience;
		}
	}

	public enum BushesFieldsData {
		ETCETERIA(0, new Point[] { new Point(2591, 3863), new Point(2592, 3864) }, 2337),
		SOUTH_ARDOUGNE(1, new Point[] { new Point(2617, 3225), new Point(2618, 3226) }, 2338),
		CHAMPION_GUILD(2, new Point[] { new Point(3181, 3357), new Point(3182, 3358) }, 2335),
		RIMMINGTON(3, new Point[] { new Point(2940, 3221), new Point(2941, 3222) }, 2336);

		private static final Map<Integer, BushesFieldsData> npcsProtecting = new HashMap<Integer, BushesFieldsData>();

		static {
			for (BushesFieldsData data : BushesFieldsData.values()) {
				npcsProtecting.put(data.npcId, data);

			}
		}

		private final int bushesIndex;
		private final Point[] bushesPosition;
		private final int npcId;

		BushesFieldsData(int bushesIndex, Point[] bushesPosition, int npcId) {
		this.bushesIndex = bushesIndex;
		this.bushesPosition = bushesPosition;
		this.npcId = npcId;
		}

		public static BushesFieldsData forId(int npcId) {
		return npcsProtecting.get(npcId);
		}

		public static BushesFieldsData forIdPosition(int x, int y) {
		for (BushesFieldsData bushesFieldsData : BushesFieldsData.values()) {
			if (CultivationConstants.inRangeArea(bushesFieldsData.getBushesPosition()[0], bushesFieldsData.getBushesPosition()[1], x, y)) {
				return bushesFieldsData;
			}
		}
		return null;
		}

		public int getBushesIndex() {
		return bushesIndex;
		}

		public Point[] getBushesPosition() {
		return bushesPosition;
		}

		public int getNpcId() {
		return npcId;
		}
	}

	public enum InspectData {

		REDBERRY(5101, new String[][] { { "The Redberry seeds have only just been planted." }, { "The Redberry bush grows larger." }, { "The Redberry bush grows larger." }, { "The Redberry bush grows small, unripe,", "green berries." }, { "The berries grow larger, and pink." }, { "The Redberry bush is ready to harvest.", "The berries on the bush are red." }, }),
		CADAVABERRY(5102, new String[][] { { "The Cadavaberry seeds have only just been planted." }, { "The Cadavaberry bush grows larger." }, { "The Cadavaberry bush grows larger." }, { "The Cadavaberry bush grows larger." }, { "The Cadavaberry bush grows small, unripe,", "green berries." }, { "The berries grow larger, and pink." }, { "The Cadavaberry bush is ready to harvest.", "The berries on the bush are purple." } }),
		DWELLBERRY(5103, new String[][] { { "The Dwellbery seeds have only just been planted." }, { "The Dwellbery bush grows larger." }, { "The Dwellbery bush grows larger." }, { "The Dwellbery bush grows larger." }, { "The Dwellbery bush grows larger." }, { "The Dwellbery bush grows small, unripe,", "green berries." }, { "The berries grow larger, and light blue." }, { "The Dwellbery bush is ready to harvest.", "The berries on the bush are blue." }, }),
		JANGERBERRY(5104, new String[][] { { "The Jangerberry seeds have only just been planted." }, { "The Jangerberry bush grows larger." }, { "The Jangerberry bush grows larger." }, { "The Jangerberry bush grows larger." }, { "The Jangerberry bush grows larger." }, { "The Jangerberry bush grows small, unripe,", "green berries." }, { "The berries grow larger." }, { "The berries grow larger, and light green." }, { "The Jangerberry bush is ready to harvest.", "The berries on the bush are green." } }),
		WHITEBERRY(5105, new String[][] { { "The Whiteberry seeds have only just been planted." }, { "The Whiteberry bush grows larger." }, { "The Whiteberry bush grows larger." }, { "The Whiteberry bush grows larger." }, { "The Whiteberry bush grows larger." }, { "The Whiteberry bush grows larger." }, { "The Whiteberry bush grows small, unripe,", "green berries." }, { "The berries grow larger." }, { "The Whiteberry bush is ready to harvest.", "The berries on the bush are white." }, }),
		POISONIVYBERRY(5106, new String[][] { { "The Poison ivy seeds have only just been planted." }, { "The Poison ivy bush grows larger." }, { "The Poison ivy bush grows larger." }, { "The Poison ivy bush grows larger." }, { "The Poison ivy bush grows larger." }, { "The Poison ivy bush grows small, unripe,", "green berries." }, { "The berries grow larger." }, { "The berries grow larger, and light green." }, { "The Poison ivy bush is ready to harvest.", "The berries on the bush are pale yellow." } });

		private static final Map<Integer, InspectData> seeds = new HashMap<Integer, InspectData>();

		static {
			for (InspectData data : InspectData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		private final int seedId;
		private final String[][] messages;

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
}