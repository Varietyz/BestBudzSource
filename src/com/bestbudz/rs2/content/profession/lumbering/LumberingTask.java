package com.bestbudz.rs2.content.profession.lumbering;

// import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
// import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.object.GameObject;
// import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

/**
 * Handles the lumbering profession
 * 
 * @author Arithium
 * 
 */
public class LumberingTask extends Task {

	/**
	 * Attempts to chop a tree down
	 * 
	 * @param stoner
	 *                     The stoner lumbering
	 * @param objectId
	 *                     The id of the object
	 * @param x
	 *                     The x coordinate of the object
	 * @param y
	 *                     The y coordinate of the object
	 */
	public static void attemptLumbering(Stoner stoner, int objectId, int x, int y) {
	GameObject object = new GameObject(objectId, x, y, stoner.getLocation().getZ(), 10, 0);
	LumberingTreeData tree = LumberingTreeData.forId(object.getId());
	if (tree == null) {
		return;
	}

	if (!meetsRequirements(stoner, tree, object)) {
		return;
	}

	LumberingAxeData[] axes = new LumberingAxeData[15];

	int d = 0;

	for (int s : AXES) {
		if (stoner.getEquipment().getItems()[12] != null && stoner.getEquipment().getItems()[12].getId() == s) {
			axes[d] = LumberingAxeData.forId(s);
			d++;
			break;
		}
	}

	// if (axes == null) {
	if (d == 0) {
		for (Item i : stoner.getEquipment().getItems()) {
			if (i != null) {
				for (int c : AXES) {
					if (i.getId() == c) {
						axes[d] = LumberingAxeData.forId(c);
						d++;
						break;
					}
				}
			}
		}
	}
	// }

	int professionGrade = 0;
	int anyGradeAxe = 0;
	int index = -1;
	int indexb = 0;

	for (LumberingAxeData i : axes) {
		if (i != null) {
			if (meetsAxeRequirements(stoner, i)) {
				if (index == -1 || i.getGradeRequired() > professionGrade) {
					index = indexb;
					professionGrade = i.getGradeRequired();
				}
			}

			anyGradeAxe = i.getGradeRequired();
		}

		indexb++;
	}

	if (index == -1) {
		if (anyGradeAxe != 0) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You need a lumbering grade of " + anyGradeAxe + " to use this axe."));
		} else {
					DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
		}
		return;
	}

	LumberingAxeData axe = axes[index];

	stoner.getClient().queueOutgoingPacket(new SendMessage("You generated an axe and started chopping the tree."));
	stoner.getUpdateFlags().sendAnimation(axe.getAnimation());
	stoner.getUpdateFlags().sendFaceToDirection(object.getLocation());
	TaskQueue.queue(new LumberingTask(stoner, objectId, tree, object, axe));
	}

	/**
	 * Gets if the stoner meets the requirements to chop the tree with the axe
	 * 
	 * @param stoner
	 *                   The stoner chopping the tree
	 * @param data
	 *                   The data for the axe the stoner is wielding
	 * @return
	 */
	private static boolean meetsAxeRequirements(Stoner stoner, LumberingAxeData data) {
	if (data == null) {
				DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
		return false;
	}
	if (stoner.getProfession().getGrades()[8] < data.getGradeRequired()) {
		return false;
	}
	return true;
	}

	/**
	 * Gets if the stoner meets the requirements to chop the tree
	 * 
	 * @param stoner
	 *                   The stoner chopping the tree
	 * @param data
	 *                   The tree data
	 * @param object
	 *                   The tree object
	 * @return
	 */
	private static boolean meetsRequirements(Stoner stoner, LumberingTreeData data, GameObject object) {
	if (stoner.getProfession().getGrades()[Professions.LUMBERING] < data.getGradeRequired()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You need a lumbering grade of " + data.getGradeRequired() + " to cut this tree."));
		return false;
	}
	if (!Region.objectExists(object.getId(), object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ())) {
		return false;
	}
	if (stoner.getBox().getFreeSlots() == 0) {
		stoner.getUpdateFlags().sendAnimation(-1, 0);
		stoner.getClient().queueOutgoingPacket(new SendMessage("You don't have enough box space to cut this."));
		return false;
	}
	return true;
	}

	/**
	 * Constructs a new stoner instance
	 */
	private Stoner stoner;

	/**
	 * The tree the stoner is chopping
	 */
	private GameObject object;

	/**
	 * The lumbering axe data for the axe the stoner is using
	 */
	private LumberingAxeData axe;
	/**
	 * The lumbering tree data for the tree the stoner is chopping
	 */
	private LumberingTreeData tree;
	/**
	 * The id of the tree the stoner is chopping
	 */
	private final int treeId;

	/**
	 * The animation cycle for the chopping animation
	 */
	private int animationCycle;

	private int pos;

	/**
	 * An array of normal tree ids
	 */
	private final int[] NORMAL_TREES = { 11684, 11685 };

	/**
	 * An array of axes starting from the best to the worst
	 */
	private static final int[] AXES = { 6575 };

	/**
	 * Constructs a new lumbering task
	 * 
	 * @param stoner
	 * @param treeId
	 * @param tree
	 * @param object
	 * @param axe
	 */
	public LumberingTask(Stoner stoner, int treeId, LumberingTreeData tree, GameObject object, LumberingAxeData axe) {
	super(stoner, 1, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	this.object = object;
	this.tree = tree;
	this.axe = axe;
	this.treeId = treeId;
	}

	/**
	 * Sends the animation to swing the axe
	 */
	private void animate() {
	stoner.getClient().queueOutgoingPacket(new SendSound(472, 0, 0));

	if (++animationCycle == 1) {
		stoner.getUpdateFlags().sendAnimation(axe.getAnimation());
		animationCycle = 0;
	}
	}

	@Override
	public void execute() {
	if (!meetsRequirements(stoner, tree, object)) {
		stop();
		return;
	}

	if (pos == 3) {
		if ((successfulAttemptChance()) && (handleTreeChopping())) {
			stop();
			return;
		}

		pos = 0;
	} else {
		pos += 1;
	}

	animate();
	}

	/**
	 * Handles giving a log after cutting a tree
	 */
	private void handleGivingLogs() {
	stoner.getBox().add(new Item(tree.getReward(), 1));
	stoner.getProfession().addExperience(Professions.LUMBERING, tree.getExperience());
	AchievementHandler.activateAchievement(stoner, AchievementList.CHOP_1000_WOOD, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.CHOP_4000_WOOD, 1);

	}

	/**
	 * Handles chopping a tree down
	 * 
	 * @return
	 */
	private boolean handleTreeChopping() {
	if (isNormalTree()) {
		successfulAttempt();
		return true;
	}

	if (Utility.randomNumber(420 + 4200) == 1) {
		successfulAttempt();
		return true;
	}

	handleGivingLogs();

	return false;
	}

	/**
	 * Gets if the tree is a normal tree or not
	 * 
	 * @return
	 */
	private boolean isNormalTree() {
	for (int i : NORMAL_TREES) {
		if (i == treeId) {
			return true;
		}
	}
	return false;
	}

	@Override
	public void onStop() {
	}

	/**
	 * Handles chopping a tree down
	 */
	private void successfulAttempt() {
	stoner.getClient().queueOutgoingPacket(new SendSound(1312, 5, 0));
	stoner.getClient().queueOutgoingPacket(new SendMessage("Your arm grew tired of wacking the tree."));
	stoner.getBox().add(new Item(tree.getReward(), 1));
	stoner.getProfession().addExperience(Professions.LUMBERING, tree.getExperience());
	stoner.getUpdateFlags().sendAnimation(new Animation(65535));

	// GameObject replacement = new GameObject(tree.getReplacement(),
	// object.getLocation().getX(), object.getLocation().getY(),
	// object.getLocation().getZ(), 10, 0);
	// RSObject rsObject = new RSObject(replacement.getLocation().getX(),
	// replacement.getLocation().getY(), replacement.getLocation().getZ(),
	// object.getId(), 10, 0);

	// if (rsObject != null) {
	// ObjectManager.register(replacement);
	// Region.getRegion(rsObject.getX(), rsObject.getY()).removeObject(rsObject);
	// TaskQueue.queue(new StumpTask(object, treeId, tree.getRespawnTimer()));
	// }
	}

	/**
	 * Gets if the chop was a successful attempt
	 * 
	 * @return
	 */
	private boolean successfulAttemptChance() {
	return Professions.isSuccess(stoner, Professions.LUMBERING, tree.getGradeRequired(), axe.getGradeRequired());
	}
}
