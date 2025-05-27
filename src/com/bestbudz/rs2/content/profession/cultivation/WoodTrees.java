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
import com.bestbudz.rs2.content.profession.cultivation.ChopTree.Tree;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class WoodTrees {

	private Stoner stoner;

	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;

	public WoodTrees(Stoner stoner) {
	this.stoner = stoner;
	}

	// Cultivation data
	public int[] treeStages = new int[4];
	public int[] treeSaplings = new int[4];
	public int[] treeHarvest = new int[4];
	public int[] treeState = new int[4];
	public long[] treeTimer = new long[4];
	public double[] diseaseChance = { 1, 1, 1, 1 };
	public boolean[] hasFullyGrown = { false, false, false, false };
	public boolean[] treeWatched = { false, false, false, false };

	/* set of the constants for the patch */

	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int DISEASED = 0x01;
	public static final int DEAD = 0x02;

	public static final int MAIN_TREE_CONFIG = 502;

	/* This is the enum holding the saplings info */

	public enum TreeData {
		OAK(5370, 6043, 15, new int[] { 5968, 1 }, 160, 0.20, 14, 467.3, 0x08, 0x0c, 0x0d, 0x0e, 1281),
		WILLOW(5371, 6045, 30, new int[] { 5386, 1 }, 240, 0.20, 25, 1456.3, 0x0f, 0x15, 0x16, 0x17, 1308),
		MAPLE(5372, 6047, 45, new int[] { 5396, 1 }, 320, 0.25, 45, 3403.4, 0x18, 0x20, 0x21, 0x22, 1307),
		YEW(5373, 6049, 60, new int[] { 6016, 10 }, 400, 0.25, 81, 7069.9, 0x23, 0x2d, 0x2e, 0x2f, 1309),
		MAGE(5374, 6051, 75, new int[] { 5976, 25 }, 480, 0.25, 145.5, 13768.3, 0x30, 0x3c, 0x3d, 0x3e, 1292);

		private int saplingId;
		private int rootsId;
		private int gradeRequired;
		private int[] paymentToWatch;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double checkHealthXp;
		private int startingState;
		private int endingState;
		private int chopDownState;
		private int stumpState;
		private int treeObjectAssociated;

		private static Map<Integer, TreeData> saplings = new HashMap<Integer, TreeData>();

		static {
			for (TreeData data : TreeData.values()) {
				saplings.put(data.saplingId, data);
			}
		}

		TreeData(int saplingId, int rootsId, int gradeRequired, int[] paymentToWatch, int growthTime, double diseaseChance, double plantingXp, double checkHealthXp, int startingState, int endingState, int chopDownState, int stumpState, int treeObjectAssociated) {
		this.saplingId = saplingId;
		this.rootsId = rootsId;
		this.gradeRequired = gradeRequired;
		this.paymentToWatch = paymentToWatch;
		this.growthTime = growthTime;
		this.diseaseChance = diseaseChance;
		this.plantingXp = plantingXp;
		this.checkHealthXp = checkHealthXp;
		this.startingState = startingState;
		this.endingState = endingState;
		this.chopDownState = chopDownState;
		this.stumpState = stumpState;
		this.treeObjectAssociated = treeObjectAssociated;
		}

		public static TreeData forId(int saplingId) {
		return saplings.get(saplingId);
		}

		public int getSaplingId() {
		return saplingId;
		}

		public int getRootsId() {
		return rootsId;
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

		public double getCheckHealthXp() {
		return checkHealthXp;
		}

		public int getStartingState() {
		return startingState;
		}

		public int getEndingState() {
		return endingState;
		}

		public int getChopDownState() {
		return chopDownState;
		}

		public int getStumpState() {
		return stumpState;
		}

		public int getTreeObjectAssociated() {
		return treeObjectAssociated;
		}
	}

	/* This is the enum data about the different patches */

	public enum TreeFieldsData {
		VARROCK(0, new Point[] { new Point(3228, 3458), new Point(3230, 3460) }, 2341),
		LUMBRIDGE(1, new Point[] { new Point(3192, 3230), new Point(3194, 3232) }, 2342),
		TAVERLEY(2, new Point[] { new Point(2935, 3437), new Point(2937, 3439) }, 2339),
		FALADOR(3, new Point[] { new Point(3003, 3372), new Point(3005, 3374) }, 2340);

		private int treeIndex;
		private Point[] treePosition;
		private int npcId;

		private static Map<Integer, TreeFieldsData> npcsProtecting = new HashMap<Integer, TreeFieldsData>();

		static {
			for (TreeFieldsData data : TreeFieldsData.values()) {
				npcsProtecting.put(data.npcId, data);

			}
		}

		public static TreeFieldsData forId(int npcId) {
		return npcsProtecting.get(npcId);
		}

		TreeFieldsData(int treeIndex, Point[] treePosition, int npcId) {
		this.treeIndex = treeIndex;
		this.treePosition = treePosition;
		this.npcId = npcId;
		}

		public static TreeFieldsData forIdPosition(int x, int y) {
		for (TreeFieldsData treeFieldsData : TreeFieldsData.values()) {
			if (CultivationConstants.inRangeArea(treeFieldsData.getTreePosition()[0], treeFieldsData.getTreePosition()[1], x, y)) {
				return treeFieldsData;
			}
		}
		return null;
		}

		public int getTreeIndex() {
		return treeIndex;
		}

		public Point[] getTreePosition() {
		return treePosition;
		}

		public int getNpcId() {
		return npcId;
		}
	}

	/* This is the enum that hold the different data for inspecting the plant */

	public enum InspectData {
		OAK(5370, new String[][] { { "The acorn sapling has only just been planted." }, { "The acorn sapling grows larger." }, { "The oak tree produces a small canopy." }, { "The oak tree grows larger." }, { "The oak tree is ready to harvest." } }),
		WILLOW(5371, new String[][] { { "The willow sapling has only just been planted." }, { "The willow sapling grows a few small branches." }, { "The willow tree develops a small canopy." }, { "The willow tree trunk becomes dark brown,", "and the canopy grows." }, { "The trunk thickens, and the canopy grows", "yet larger." }, { "The willow tree is fully grown." } }),
		MAPLE(5372, new String[][] { { "The maple sapling has only just been planted." }, { "The mapling sapling grows a few small branches." }, { "The maple tree develops a small canopy." }, { "The maple tree trunk straightens and the canopy", "grows larger." }, { "The maple tree canopy grows." }, { "The maple tree grows." }, { "The maple tree is ready to be harvested." } }),
		YEW(5373, new String[][] { { "The yew sapling has only just been planted." }, { "The yew sapling grows a few small branches." }, { "The yew tree develops several small canopies." }, { "The yew tree trunk texture becomes smoother and", "the canopies grow larger." }, { "The yew tree grows larger." }, { "The yew tree canopies become more angular and", "cone shaped due to texture placement." }, { "The yew tree gains a rougher tree bark texture and", "the base becomes darker." }, { "The yew tree base becomes light again, and the", "trunk loses its texture." }, { "The yew tree bark gains a stripy texture." }, { "The yew tree is ready to be harvested." } }),
		MAGE(5374, new String[][] { { "The mage sapling has only just been planted." }, { "The mage sapling grows a little bit." }, { "The mage sapling grows a little bit more." }, { "The mage sapling grows a few small branches." }, { "The mage tree grows a small canopy." }, { "The mage tree canopy becomes larger, and", "starts producing sparkles." }, { "The mage tree grows and the base becomes lighter." }, { "The mage tree grows and the base becomes darker." }, { "The mage tree's bark is more prominent and the", "canopy gains more sparkles." }, { "The mage tree grows taller." }, { "The mage tree grows taller." }, { "The mage tree is ready to be harvested." } });

		private int saplingId;
		private String[][] messages;

		private static Map<Integer, InspectData> saplings = new HashMap<Integer, InspectData>();

		static {
			for (InspectData data : InspectData.values()) {
				saplings.put(data.saplingId, data);
			}
		}

		InspectData(int saplingId, String[][] messages) {
		this.saplingId = saplingId;
		this.messages = messages;
		}

		public static InspectData forId(int saplingId) {
		return saplings.get(saplingId);
		}

		public int getSaplingId() {
		return saplingId;
		}

		public String[][] getMessages() {
		return messages;
		}
	}

	/* update all the patch states */

	public void updateTreeStates() {
	// varrock - lumbridge - taverley - falador
	int[] configValues = new int[treeStages.length];

	int configValue;
	for (int i = 0; i < treeStages.length; i++) {
		configValues[i] = getConfigValue(treeStages[i], treeSaplings[i], treeState[i], i);
	}

	configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
	stoner.send(new SendConfig(MAIN_TREE_CONFIG, configValue));

	}

	/* getting the different config values */

	public int getConfigValue(int treeStage, int saplingId, int plantState, int index) {
	TreeData treeData = TreeData.forId(saplingId);
	switch (treeStage) {
	case 0:// weed
		return (GROWING << 6) + 0x00;
	case 1:// weed cleared
		return (GROWING << 6) + 0x01;
	case 2:
		return (GROWING << 6) + 0x02;
	case 3:
		return (GROWING << 6) + 0x03;
	}
	if (treeData == null) {
		return -1;
	}
	if (treeData.getEndingState() == treeData.getStartingState() + treeStage - 1) {
		hasFullyGrown[index] = true;
	}

	if (plantState == 6)
		return treeData.getChopDownState();
	if (plantState == 7)
		return treeData.getStumpState();

	return (getPlantState(plantState) << 6) + treeData.getStartingState() + treeStage - 4;
	}

	/* getting the plant states */

	public int getPlantState(int plantState) {
	switch (plantState) {
	case 0:
		return GROWING;
	case 1:
		return DISEASED;
	case 2:
		return DEAD;
	}
	return -1;
	}

	/* calculating the disease chance and making the plant grow */

	public void doCalculations() {
	for (int i = 0; i < treeSaplings.length; i++) {
		if (treeStages[i] > 0 && treeStages[i] <= 3 && Cultivation.getMinutesCounter(stoner) - treeTimer[i] >= 5) {
			treeStages[i]--;
			treeTimer[i] = Cultivation.getMinutesCounter(stoner);
			updateTreeStates();
			continue;
		}
		TreeData treeData = TreeData.forId(treeSaplings[i]);
		if (treeData == null) {
			continue;
		}

		long difference = Cultivation.getMinutesCounter(stoner) - treeTimer[i];
		long growth = treeData.getGrowthTime();
		int nbStates = treeData.getEndingState() - treeData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		if (treeTimer[i] == 0 || treeState[i] == 2 || state > nbStates) {
			continue;
		}
		if (4 + state != treeStages[i]) {
			treeStages[i] = 4 + state;
			if (treeStages[i] <= 4 + state)
				for (int j = treeStages[i]; j <= 4 + state; j++)
					doStateCalculation(i);
			updateTreeStates();
		}
	}
	}

	public void modifyStage(int i) {
	TreeData bushesData = TreeData.forId(treeSaplings[i]);
	if (bushesData == null)
		return;
	long difference = Cultivation.getMinutesCounter(stoner) - treeTimer[i];
	long growth = bushesData.getGrowthTime();
	int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
	int state = (int) (difference * nbStates / growth);
	treeStages[i] = 4 + state;
	updateTreeStates();

	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
	if (treeState[index] == 2) {
		return;
	}
	// if the patch is diseased, it dies, if its watched by a farmer, it
	// goes back to normal
	if (treeState[index] == 1) {
		if (treeWatched[index]) {
			treeState[index] = 0;
			TreeData treeData = TreeData.forId(treeSaplings[index]);
			if (treeData == null)
				return;
			int difference = treeData.getEndingState() - treeData.getStartingState();
			int growth = treeData.getGrowthTime();
			treeTimer[index] += (growth / difference);
			modifyStage(index);
		} else {
			treeState[index] = 2;
		}
	}

	if (treeState[index] == 5 && treeStages[index] != 3) {
		treeState[index] = 0;
	}

	if (treeState[index] == 0 && treeStages[index] >= 5 && !hasFullyGrown[index]) {
		TreeData treeData = TreeData.forId(treeSaplings[index]);
		if (treeData == null) {
			return;
		}

		double chance = diseaseChance[index] * treeData.getDiseaseChance();
		int maxChance = (int) chance * 100;
		if (Utility.random(100) <= maxChance && !stoner.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
			treeState[index] = 1;
		}
	}
	}

	/* clearing the patch with a rake of a spade */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final TreeFieldsData hopsFieldsData = TreeFieldsData.forIdPosition(objectX, objectY);
	int finalAnimation;
	int finalDelay;
	if (hopsFieldsData == null || (itemId != CultivationConstants.RAKE && itemId != CultivationConstants.SPADE)) {
		return false;
	}

	if (treeStages[hopsFieldsData.getTreeIndex()] == 3) {
		return true;
	}
	if (treeStages[hopsFieldsData.getTreeIndex()] <= 3) {
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
		if (treeStages[hopsFieldsData.getTreeIndex()] <= 2) {
			treeStages[hopsFieldsData.getTreeIndex()]++;
			stoner.getBox().add(6055, 1);
		} else {
			treeStages[hopsFieldsData.getTreeIndex()] = 3;
			stop();
		}
		stoner.getProfession().addExperience(Professions.CULTIVATION, CLEARING_EXPERIENCE);
		treeTimer[hopsFieldsData.getTreeIndex()] = Cultivation.getMinutesCounter(stoner);
		updateTreeStates();
		if (treeStages[hopsFieldsData.getTreeIndex()] == 3) {
			stop();
			return;
		}
		}

		@Override
		public void onStop() {
		resetTrees(hopsFieldsData.getTreeIndex());
		stoner.send(new SendMessage("You clear the patch."));
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;

	}

	/* planting the saplings */

	public boolean plantSapling(int objectX, int objectY, final int saplingId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(objectX, objectY);
	final TreeData treeData = TreeData.forId(saplingId);
	if (treeFieldsData == null || treeData == null) {
		return false;
	}

	if (treeStages[treeFieldsData.getTreeIndex()] != 3) {
		stoner.send(new SendMessage("You can't plant a sapling here."));
		return true;
	}
	if (treeData.getGradeRequired() > stoner.getGrades()[Professions.CULTIVATION]) {
		DialogueManager.sendStatement(stoner, "You need a cultivation grade of " + treeData.getGradeRequired() + " to plant this sapling.");
		return true;
	}

	if (!stoner.getEquipment().isWearingItem(CultivationConstants.TROWEL)) {
		DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant the sapling here.");
		return true;
	}
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.PLANTING_POT_ANIM));
	treeStages[treeFieldsData.getTreeIndex()] = 4;
	stoner.getBox().remove(saplingId, 1);

	stoner.getProfession().lock(3);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 3, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		treeState[treeFieldsData.getTreeIndex()] = 0;
		treeSaplings[treeFieldsData.getTreeIndex()] = saplingId;
		treeTimer[treeFieldsData.getTreeIndex()] = Cultivation.getMinutesCounter(stoner);
		stoner.getProfession().addExperience(Professions.CULTIVATION, treeData.getPlantingXp());
		stop();
		}

		@Override
		public void onStop() {
		updateTreeStates();
		stoner.setController(controller);
		}
	});
	return true;
	}

	@SuppressWarnings("unused")
	private void displayAll() {
	for (int i = 0; i < treeStages.length; i++) {
		System.out.println("index : " + i);
		System.out.println("state : " + treeState[i]);
		System.out.println("harvest : " + treeHarvest[i]);
		System.out.println("saplings : " + treeSaplings[i]);
		System.out.println("grade : " + treeStages[i]);
		System.out.println("timer : " + treeTimer[i]);
		System.out.println("disease chance : " + diseaseChance[i]);
		System.out.println("-----------------------------------------------------------------");
	}
	}

	/* harvesting the plant resulted */

	public boolean checkHealth(int objectX, int objectY) {
	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(objectX, objectY);
	if (treeFieldsData == null) {
		return false;
	}
	final TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);
	if (treeData == null) {
		return false;
	}

	if (treeState[treeFieldsData.getTreeIndex()] != 0)
		return false;

	stoner.getUpdateFlags().sendAnimation(new Animation(832));
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		stoner.send(new SendMessage("You inspect the tree for signs of disease and find that it is in perfect health"));
		stoner.getProfession().addExperience(Professions.CULTIVATION, treeData.getCheckHealthXp());
		treeState[treeFieldsData.getTreeIndex()] = 6;
		stop();
		}

		@Override
		public void onStop() {
		updateTreeStates();
		stoner.setController(controller);
		}
	});
	return true;
	}

	public void respawnStumpTimer(final int index) {
	TaskQueue.queue(new Task(stoner, 500, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		if (treeState[index] == 7)
			treeState[index] = 6;
		stop();
		}

		@Override
		public void onStop() {
		}
	});
	}

	/* putting compost onto the plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	if (itemId != 6032 && itemId != 6034) {
		return false;
	}
	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(objectX, objectY);
	if (treeFieldsData == null) {
		return false;
	}

	if (treeStages[treeFieldsData.getTreeIndex()] != 3 || treeState[treeFieldsData.getTreeIndex()] == 5) {
		stoner.send(new SendMessage("This patch doesn't need compost."));
		return true;
	}
	stoner.getBox().remove(itemId, 1);
	stoner.getBox().add(1925, 1);

	stoner.send(new SendMessage("You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch."));
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.PUTTING_COMPOST));
	stoner.getProfession().addExperience(Professions.CULTIVATION, (itemId == 6034 ? Compost.SUPER_COMPOST_EXP_USE : Compost.COMPOST_EXP_USE));
	stoner.getProfession().lock(7);
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 7, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		diseaseChance[treeFieldsData.getTreeIndex()] *= itemId == 6032 ? COMPOST_CHANCE : SUPERCOMPOST_CHANCE;
		treeState[treeFieldsData.getTreeIndex()] = 5;
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
	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(objectX, objectY);
	if (treeFieldsData == null) {
		return false;
	}

	final InspectData inspectData = InspectData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);
	final TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);
	if (treeState[treeFieldsData.getTreeIndex()] == 1) {
		DialogueManager.sendStatement(stoner, "This tree is diseased. Use secateurs to prune the area, ", "or clear the patch with a spade.");
		return true;
	} else if (treeState[treeFieldsData.getTreeIndex()] == 2) {
		DialogueManager.sendStatement(stoner, "This tree is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
		return true;
	} else if (treeState[treeFieldsData.getTreeIndex()] == 6) {
		DialogueManager.sendStatement(stoner, "This is a tree stump, to remove it, use a spade on it", "to recieve some roots and clear the patch.");
		return true;
	}
	if (treeStages[treeFieldsData.getTreeIndex()] == 0) {
		DialogueManager.sendStatement(stoner, "This is a tree patch. The soil has not been treated.", "The patch needs weeding.");
	} else if (treeStages[treeFieldsData.getTreeIndex()] == 3) {
		DialogueManager.sendStatement(stoner, "This is a tree patch. The soil has not been treated.", "The patch is empty and weeded.");
	} else if (inspectData != null && treeData != null) {
		stoner.send(new SendMessage("You bend down and start to inspect the patch..."));

		stoner.getUpdateFlags().sendAnimation(new Animation(1331));
		stoner.getProfession().lock(5);
		Controller controller = stoner.getController();
		stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		TaskQueue.queue(new Task(stoner, 5, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
			@Override
			public void execute() {
			if (treeStages[treeFieldsData.getTreeIndex()] - 4 < inspectData.getMessages().length - 2) {
				DialogueManager.sendStatement(stoner, inspectData.getMessages()[treeStages[treeFieldsData.getTreeIndex()] - 4]);
			} else if (treeStages[treeFieldsData.getTreeIndex()] < treeData.getEndingState() - treeData.getStartingState() + 2) {
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

	public boolean pruneArea(int objectX, int objectY, int itemId) {
	if (stoner.getProfession().locked()) {
		return false;
	}
	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(objectX, objectY);
	if (treeFieldsData == null || (itemId != CultivationConstants.SECATEURS && itemId != CultivationConstants.MAGE_SECATEURS)) {
		return false;
	}
	final TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);
	if (treeData == null) {
		return false;
	}

	if (treeState[treeFieldsData.getTreeIndex()] != 1) {
		stoner.send(new SendMessage("This area doesn't need to be pruned."));
		return true;
	}
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.PRUNING_ANIM));
	stoner.getProfession().lock(15);
	treeState[treeFieldsData.getTreeIndex()] = 0;
	Controller controller = stoner.getController();
	stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
	TaskQueue.queue(new Task(stoner, 15, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
		@Override
		public void execute() {
		stoner.send(new SendMessage("You prune the area with your secateurs."));
		stop();
		}

		@Override
		public void onStop() {
		updateTreeStates();
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		stoner.setController(controller);
		}
	});
	return true;

	}

	public void resetTrees(int index) {
	treeSaplings[index] = 0;
	treeState[index] = 0;
	diseaseChance[index] = 1;
	treeHarvest[index] = 0;
	hasFullyGrown[index] = false;
	treeWatched[index] = false;
	}

	/**
	 * Lumbering action
	 * 
	 * @param =
	 *              tree id
	 * @param x
	 *              = tree x location
	 * @param y
	 *              = tree y location
	 * @return
	 */

	public boolean cut(final int x, final int y) {

	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(x, y);
	if (treeFieldsData == null)
		return false;
	final TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);
	if (treeData == null)
		return false;

	if (stoner.getBox().getFreeSlots() <= 0) {
		stoner.send(new SendMessage("Not enough space in your box."));
		return true;
	}

	if (ChopTree.getAxe(stoner) == null) {
		stoner.send(new SendMessage("You do not have an axe which you have the lumbering grade to use."));
		return true;
	}

	final int object = treeData.getTreeObjectAssociated();
	stoner.send(new SendMessage("You swing your axe at the tree."));
	final int emoteId = ChopTree.getAxe(stoner).getAnimation();
	stoner.getUpdateFlags().sendAnimation(new Animation(emoteId));
	TaskQueue.queue(new Task(stoner, 4, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CULTIVATION) {
		final Tree tree = ChopTree.getTree(object);

		@Override
		public void execute() {
		if (stoner.getBox().getFreeSlots() <= 0) {
			stop();
			return;
		}

		stoner.getUpdateFlags().sendAnimation(new Animation(emoteId));
		if (Professions.isSuccess(stoner, ChopTree.getTree(object).getGrade(), ChopTree.getAxe(stoner).getGrade())) {
			stoner.getBox().add(ChopTree.getTree(object).getLog(), 1);
			stoner.getProfession().addExperience(Professions.LUMBERING, ChopTree.getTree(object).getXP());
			if (Utility.random(100) <= tree.getDecayChance()) {
				respawnStumpTimer(treeFieldsData.getTreeIndex());
				treeState[treeFieldsData.getTreeIndex()] = 7;
				updateTreeStates();
				stop();
				stoner.getUpdateFlags().sendAnimation(new Animation(65535));
			}
		}
		if (!canCut(x, y)) {
			stoner.getUpdateFlags().sendAnimation(new Animation(65535));
			stop();
		}
		}

		@Override
		public void onStop() {
		}
	});
	return true;
	}

	/**
	 * Checks if you can chop the tree
	 * 
	 * @param =
	 *              tree id
	 * @param x
	 *              = tree x location
	 * @param y
	 *              = tree y location
	 * @return if can cut
	 */
	public boolean canCut(final int x, final int y) {
	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(x, y);
	if (treeFieldsData == null)
		return false;
	final TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);
	if (treeData == null)
		return false;
	final int object = treeData.getTreeObjectAssociated();

	if (!hasFullyGrown[treeFieldsData.getTreeIndex()]) {
		return false;
	}
	if (ChopTree.getAxe(stoner) == null) {
		stoner.send(new SendMessage("You do not have an axe which you have the lumbering grade to use."));
		return false;
	}
	if (stoner.getBox().getFreeSlots() + 1 >= 28) {
		return false;
	}
	if (stoner.getGrades()[Professions.LUMBERING] < ChopTree.getTree(object).getGrade()) {
		DialogueManager.sendStatement(stoner, "You need a lumbering grade of " + ChopTree.getTree(object).getGrade() + " to cho this tree.");
		return false;
	}
	return true;
	}

	/* checking if the patch is raked */

	public boolean checkIfRaked(int objectX, int objectY) {
	final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(objectX, objectY);
	if (treeFieldsData == null)
		return false;
	if (treeStages[treeFieldsData.getTreeIndex()] == 3)
		return true;
	return false;
	}

}
