package com.mayhem.rs2.content.skill.woodcutting;

import com.mayhem.core.cache.map.RSObject;
import com.mayhem.core.cache.map.Region;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendSound;




/**
 * Handles the woodcutting skill
 * 
 * @author Arithium
 * 
 */
public class WoodcuttingTask extends Task {

	/**
	 * Attempts to chop a tree down
	 * 
	 * @param player
	 *            The player woodcutting
	 * @param objectId
	 *            The id of the object
	 * @param x
	 *            The x coordinate of the object
	 * @param y
	 *            The y coordinate of the object
	 */
	public static void attemptWoodcutting(Player player, int objectId, int x, int y) {
		GameObject object = new GameObject(objectId, x, y, player.getLocation().getZ(), 10, 3);
		WoodcuttingTreeData tree = WoodcuttingTreeData.forId(object.getId());
		if (tree == null) {
			return;
		}

		if (!meetsRequirements(player, tree, object)) {
			return;
		}

		WoodcuttingAxeData[] axes = new WoodcuttingAxeData[15];

		int d = 0;

		for (int s : AXES) {
			if (player.getEquipment().getItems()[3] != null && player.getEquipment().getItems()[3].getId() == s) {
				axes[d] = WoodcuttingAxeData.forId(s);
				d++;
				break;
			}
		}

		// if (axes == null) {
		if (d == 0) {
			for (Item i : player.getInventory().getItems()) {
				if (i != null) {
					for (int c : AXES) {
						if (i.getId() == c) {
							axes[d] = WoodcuttingAxeData.forId(c);
							d++;
							break;
						}
					}
				}
			}
		}
		// }

		int skillLevel = 0;
		int anyLevelAxe = 0;
		int index = -1;
		int indexb = 0;

		for (WoodcuttingAxeData i : axes) {
			if (i != null) {
				if (meetsAxeRequirements(player, i)) {
					if (index == -1 || i.getLevelRequired() > skillLevel) {
						index = indexb;
						skillLevel = i.getLevelRequired();
					}
				}

				anyLevelAxe = i.getLevelRequired();
			}

			indexb++;
		}

		if (index == -1) {
			if (anyLevelAxe != 0) {
				player.getClient().queueOutgoingPacket(
						new SendMessage("You need a woodcutting level of " + anyLevelAxe + " to use this axe."));
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have an axe."));
			}
			return;
		}

		WoodcuttingAxeData axe = axes[index];

		player.getClient().queueOutgoingPacket(new SendMessage("You swing your axe at the tree."));
		player.getUpdateFlags().sendAnimation(axe.getAnimation());
		player.getUpdateFlags().sendFaceToDirection(object.getLocation());
		TaskQueue.queue(new WoodcuttingTask(player, objectId, tree, object, axe));
	}

	/**
	 * Gets if the player meets the requirements to chop the tree with the axe
	 * 
	 * @param player
	 *            The player chopping the tree
	 * @param data
	 *            The data for the axe the player is wielding
	 * @return
	 */
	private static boolean meetsAxeRequirements(Player player, WoodcuttingAxeData data) {
		if (data == null) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have a hatchet."));
			return false;
		}
		if (player.getSkill().getLevels()[8] < data.getLevelRequired()) {
			return false;
		}
		return true;
	}

	/**
	 * Gets if the player meets the requirements to chop the tree
	 * 
	 * @param player
	 *            The player chopping the tree
	 * @param data
	 *            The tree data
	 * @param object
	 *            The tree object
	 * @return
	 */
	private static boolean meetsRequirements(Player player, WoodcuttingTreeData data, GameObject object) {
		if (player.getSkill().getLevels()[Skills.WOODCUTTING] < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage(
					"You need a woodcutting level of " + data.getLevelRequired() + " to cut this tree."));
			return false;
		}
		if (!Region.objectExists(object.getId(), object.getLocation().getX(), object.getLocation().getY(),
				object.getLocation().getZ()) && !ObjectManager.objectExists(object.getLocation())) {
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getUpdateFlags().sendAnimation(-1, 0);
			player.getClient()
					.queueOutgoingPacket(new SendMessage("You don't have enough inventory space to cut this."));
			return false;
		}
		return true;
	}

	/**
	 * Constructs a new player instance
	 */
	private Player player;

	/**
	 * The tree the player is chopping
	 */
	private GameObject object;

	/**
	 * The woodcutting axe data for the axe the player is using
	 */
	private WoodcuttingAxeData axe;
	/**
	 * The woodcutting tree data for the tree the player is chopping
	 */
	private WoodcuttingTreeData tree;
	/**
	 * The id of the tree the player is chopping
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
	private final int[] NORMAL_TREES = { 1278, 1276 };

	/**
	 * An array of axes starting from the best to the worst
	 */
	private static final int[] AXES = { 20011, 13241, 13661, 6739, 1359, 1357, 1355, 1361, 1353, 1349, 1351 };

	/**
	 * Constructs a new woodcutting task
	 * 
	 * @param player
	 * @param treeId
	 * @param tree
	 * @param object
	 * @param axe
	 */
	public WoodcuttingTask(Player player, int treeId, WoodcuttingTreeData tree, GameObject object,
			WoodcuttingAxeData axe) {
		super(player, 1, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.object = object;
		this.tree = tree;
		this.axe = axe;
		this.treeId = treeId;
	}

	/**
	 * Sends the animation to swing the axe
	 */
	private void animate() {
		player.getClient().queueOutgoingPacket(new SendSound(472, 0, 0));

		if (++animationCycle == 1) {
			player.getUpdateFlags().sendAnimation(axe.getAnimation());
			animationCycle = 0;
		}
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, tree, object)) {
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
		handleRandom(player);
		handleSeedNest(player);
		handleRingNest(player);
		handleBloodMoney(player);
		handleEasyNest(player);
		handleMediumNest(player);
		handleHardNest(player);
		player.getInventory().add(new Item(tree.getReward(), 1));
		player.getSkill().addExperience(Skills.WOODCUTTING, tree.getExperience());
		player.skillPoints += 50;
		player.getClient().queueOutgoingPacket(new SendMessage("You receive a log. You now have @blu@" + player.skillPoints + "</col> Skill points."));
		//player.setSkillingPoints(player.getSkillingPoints() + tree.skillingPoints); //woodcutting skilling points

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

		if (Utility.randomNumber(9 + (World.getActivePlayers() / 50)) == 1) {
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
		player.getClient().queueOutgoingPacket(new SendSound(1312, 5, 0));
		//player.getClient().queueOutgoingPacket(new SendMessage("You successfully chop down the tree. You now have @blu@" + player.woodcuttingPoints + "</col> Woodcutting points."));
		handleRandom(player);
		handlepetTreeSpirit(player);
		handleSeedNest(player);
		handleRingNest(player);
		handleBloodMoney(player);
		handleEasyNest(player);
		handleMediumNest(player);
		handleHardNest(player);
		player.getInventory().add(new Item(tree.getReward(), 1));
		player.getSkill().addExperience(Skills.WOODCUTTING, tree.getExperience());
		player.getUpdateFlags().sendAnimation(new Animation(65535));
		AchievementHandler.activate(player, AchievementList.CHOP_DOWN_150_TREES, 1);
		AchievementHandler.activate(player, AchievementList.CHOP_DOWN_750_TREES, 1);
		GameObject replacement = new GameObject(tree.getReplacement(), object.getLocation().getX(),
				object.getLocation().getY(), object.getLocation().getZ(), 10, 3);
		RSObject rsObject = new RSObject(object.getLocation().getX(), object.getLocation().getY(),
				object.getLocation().getZ(), object.getId(), 10, 3);
		player.skillPoints += 50;

		if (rsObject != null) {
			ObjectManager.registerObject(replacement);
			if (Region.getRegion(rsObject.getX(), rsObject.getY()) != null)
				Region.getRegion(rsObject.getX(), rsObject.getY()).removeObject(rsObject);
			TaskQueue.queue(new StumpTask(object, treeId, tree.getRespawnTimer()));
		}
	}

	/**
	 * Gets if the chop was a successful attempt
	 * 
	 * @return
	 */
	private boolean successfulAttemptChance() {
		return Skills.isSuccess(player, Skills.WOODCUTTING, tree.getLevelRequired(), axe.getLevelRequired());
	}

/**handles random items received
 * 
 * @param player
 */
public static void handleRandom(Player player) {
	int random = Utility.randomNumber(5000);
	if (random == 1) {
		if (player.getInventory().hasSpaceFor(new Item(13322))) {
			player.getInventory().add(new Item(13322));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Beaver!"));
		} else if (player.getBank().hasSpaceFor((new Item(13322)))) {
			player.getBank().add((new Item(13322)));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Beaver! It has been sent to your bank."));
		}
		World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Beaver while woodcutting!");
	}
		}

public static void handlepetTreeSpirit(Player player) {
	int random = Utility.randomNumber(6000);
	if (random == 1) {
		if (player.getInventory().hasSpaceFor(new Item(771))) {
			player.getInventory().add(new Item(771));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Tree spirit!"));
		} else if (player.getBank().hasSpaceFor((new Item(771)))) {
			player.getBank().add((new Item(771)));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Tree spirit! It has been sent to your bank."));
		}
		World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Tree spirit while woodcutting!");
	}
		}
			

public static void handleSeedNest(Player player) {
	int random = Utility.randomNumber(350);
	if (random == 1) {
		GroundItemHandler.add(new Item(5073, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@A birds nest falls out of the tree!"));
	}
		}
public static void handleRingNest(Player player) {
	int random = Utility.randomNumber(500);
	if (random == 1) {
		GroundItemHandler.add(new Item(5074, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@A birds nest falls out of the tree!"));
	}
		}

public static void handleEasyNest(Player player) {
	int random = Utility.randomNumber(700);
	if (random == 1) {
		GroundItemHandler.add(new Item(19712, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@A birds nest falls out of the tree!"));
	}
		}

public static void handleMediumNest(Player player) {
	int random = Utility.randomNumber(700);
	if (random == 1) {
		GroundItemHandler.add(new Item(19714, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@A birds nest falls out of the tree!"));
	}
		}

public static void handleHardNest(Player player) {
	int random = Utility.randomNumber(1000);
	if (random == 1) {
		GroundItemHandler.add(new Item(19716, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@A birds nest falls out of the tree!"));
	}
		}

public static void handleBloodMoney(Player player) {
	if (player.getInventory().hasSpaceFor(new Item(13307))) {
		player.getInventory().add(new Item(13307, Utility.randomNumber(50)));
	} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
		player.getBank().add((new Item(13307, Utility.randomNumber(50))));
		player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood money! It has been sent to your bank."));
	}
	
		}


}
			