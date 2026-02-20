package com.bestbudz.rs2.content.profession.lumbering;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotPrivileges;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class LumberingTask extends Task {

	private static final int[] AXES = {6575};
	private final Stoner stoner;
	private final GameObject object;
	private final LumberingAxeData axe;
	private final LumberingTreeData tree;
	private final int treeId;
	private final int[] NORMAL_TREES = {11684, 11685};
	private int animationCycle;
	private int pos;
	private long previousLumberingLevel;

	public LumberingTask(
		Stoner stoner, int treeId, LumberingTreeData tree, GameObject object, LumberingAxeData axe) {
		super(
			stoner,
			1,
			false,
			Task.StackType.NEVER_STACK,
			Task.BreakType.ON_MOVE,
			TaskIdentifier.CURRENT_ACTION);
		this.stoner = stoner;
		this.object = object;
		this.tree = tree;
		this.axe = axe;
		this.treeId = treeId;
		this.previousLumberingLevel = stoner.getProfession().getGrades()[Professions.LUMBERING];
	}

	public static void attemptLumbering(Stoner stoner, int objectId, int x, int y) {
		GameObject object = new GameObject(objectId, x, y, stoner.getLocation().getZ(), 10, 0);
		LumberingTreeData tree = LumberingTreeData.forId(object.getId());
		if (tree == null) {
			return;
		}

		if (!meetsRequirements(stoner, tree, object)) {
			return;
		}

		// Use a default axe instead of checking for equipped tools
		LumberingAxeData axe = LumberingAxeData.forId(AXES[0]); // Default to first axe
		if (axe == null) {
			// Fallback - create a basic axe data if needed
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage("Unable to find axe data."));
			return;
		}

		stoner
			.getClient()
			.queueOutgoingPacket(
				new SendMessage("You start chopping the tree."));
		stoner.getUpdateFlags().sendAnimation(axe.getAnimation());
		stoner.getUpdateFlags().sendFaceToDirection(object.getLocation());
		TaskQueue.queue(new LumberingTask(stoner, objectId, tree, object, axe));
	}

	private static boolean meetsRequirements(
		Stoner stoner, LumberingTreeData data, GameObject object) {
		// Removed grade requirement check

		if (!Region.objectExists(
			object.getId(),
			object.getLocation().getX(),
			object.getLocation().getY(),
			object.getLocation().getZ())) {
			return false;
		}
		if (stoner.getBox().getFreeSlots() == 0) {
			stoner.getUpdateFlags().sendAnimation(-1, 0);
			stoner
				.getClient()
				.queueOutgoingPacket(new SendMessage("You don't have enough box space to cut this."));
			return false;
		}
		return true;
	}

	private void animate() {
		// Check if player leveled up and reset animation cycle
		long currentLumberingLevel = stoner.getProfession().getGrades()[Professions.LUMBERING];
		if (currentLumberingLevel > previousLumberingLevel) {
			animationCycle = 0;
			previousLumberingLevel = currentLumberingLevel;
		}

		stoner.getClient().queueOutgoingPacket(new SendSound(472, 0, 0));

		// Animation loops every 9 ticks (0-8, then resets to 0)
		if (animationCycle % 9 == 0) {
			stoner.getUpdateFlags().sendAnimation(axe.getAnimation());
		}

		animationCycle++;
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

	@Override
	public void onStop() {}

	private void handleGivingLogs() {
		stoner.getBox().add(new Item(tree.getReward(), 1));
		stoner.getProfession().addExperience(Professions.LUMBERING, tree.getExperience());
		AchievementHandler.activateAchievement(stoner, AchievementList.CHOP_1000_WOOD, 1);
		AchievementHandler.activateAchievement(stoner, AchievementList.CHOP_4000_WOOD, 1);
	}

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

	private boolean isNormalTree() {
		for (int i : NORMAL_TREES) {
			if (i == treeId) {
				return true;
			}
		}
		return false;
	}

	private void successfulAttempt() {
		stoner.getClient().queueOutgoingPacket(new SendSound(1312, 5, 0));
		stoner
			.getClient()
			.queueOutgoingPacket(new SendMessage("Your arm grew tired of wacking the tree."));

		// INTEGRATION: Discord bot items go directly to bank
		DiscordBotPrivileges.addItemWithBanking(stoner, tree.getReward(), 1);
		stoner.getProfession().addExperience(Professions.LUMBERING, tree.getExperience());

		// Auto-handle inventory management for Discord bot
		DiscordBotPrivileges.handleInventoryManagement(stoner);
	}

	private boolean successfulAttemptChance() {
		// Always return true for success since we removed grade requirements
		return true;
	}
}