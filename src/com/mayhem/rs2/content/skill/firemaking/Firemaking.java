package com.mayhem.rs2.content.skill.firemaking;

import com.mayhem.core.cache.map.Region;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.impl.GroundItem;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class Firemaking extends Task {
	
	public static boolean attemptFiremaking(Player player, Item used, Item usedWith) {
		if (LogData.getLogById(used.getId()) != null && LogData.getLogById(usedWith.getId()) != null) {
			return false;
		}
		
		Item log = usedWith.getId() != 590 ? usedWith : used.getId() != 590 ? used : null;
		LogData logData = LogData.getLogById(log.getId());

		if (logData == null || used.getId() == 946 || usedWith.getId() == 946) {
			return false;
		}

		if (!meetsRequirements(player, log)) {
			return true;
		}

		player.getUpdateFlags().sendAnimation(new Animation(733));
		groundLog = new GroundItem(new Item(log.getId(), 1), new Location(player.getLocation()), player.getUsername());
		GroundItemHandler.add(groundLog);
		player.getInventory().remove(log);
		TaskQueue.queue(new Firemaking(1, player, log, logData));

		return true;
	}

	private static boolean meetsRequirements(Player player, Item log) {
		int skillLevel = player.getSkill().getLevels()[11];
		LogData data = LogData.getLogById(log.getId());

		int x = player.getLocation().getX();
		int y = player.getLocation().getY();

		if ((x >= 3090) && (y >= 3488) && (x <= 3098) && (y <= 3500)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot light fires here."));
			return false;
		}

		if (skillLevel < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a firemaking level of " + data.getLevelRequired() + " to light this log."));
			return false;
		}

		if (ObjectManager.objectExists(player.getLocation())) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot light a fire here."));
			return false;
		}

		if (!player.getInventory().hasItemId(590)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a tinderbox to light this log."));
			return false;
		}

		return true;
	}

	public static boolean success(Player player, Item log) {
		return Skills.isSuccess(player, 11, LogData.getLogById(log.getId()).getLevelRequired());
	}

	private Player player;
	private LogData logData;

	private Item log;

	private int animationCycle;

	private static GroundItem groundLog;

	public Firemaking(int delay, Player entity, Item log, LogData logData) {
		super(entity, delay, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		player = entity;
		this.log = log;
		this.logData = logData;

		player.getMovementHandler().reset();
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, log)) {
			stop();
			return;
		}
		if (success(player, log)) {
			successfullFiremake(player);
			stop();
			return;
		}
		if (animationCycle < 6) {
			animationCycle += 1;
		} else {
			player.getUpdateFlags().sendAnimation(new Animation(733));
			animationCycle = 0;
		}
	}

	@Override
	public void onStop() {
	}

	private void successfullFiremake(Player player) {
		GroundItemHandler.remove(groundLog);

		player.getUpdateFlags().sendAnimation(65535, 0);

		GameObject object = new GameObject(FireColor.FIRE, new Location(player.getLocation()), 10, 0);

		TaskQueue.queue(new FireTask(player, 50 + logData.ordinal() * 15, object));

		player.getSkill().addExperience(11, logData.getExperience());
		handleBloodMoney(player);
		player.skillPoints += 55;
		handlePetDrop(player);
		handleFmCasket(player);
		handleClueBox(player);
		player.getClient().queueOutgoingPacket(new SendMessage("You burn a log. You now have @blu@" + player.skillPoints + "</col> Skill points."));
		AchievementHandler.activate(player, AchievementList.BURN_500_LOGS, 1);
		AchievementHandler.activate(player, AchievementList.BURN_1250_LOGS, 1);
		walk(player);

	}

	private void walk(Player player) {
		if (!Region.getRegion(player.getLocation().getX(), player.getLocation().getY()).blockedWest(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())) {
			player.getMovementHandler().walkTo(-1, 0);
		} else if (!Region.getRegion(player.getLocation().getX(), player.getLocation().getY()).blockedEast(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
			player.getMovementHandler().walkTo(1, 0);
	}
	
	public static void handlePetDrop(Player player) {
		int random = Utility.randomNumber(5000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(4138))) {
				player.getInventory().add(new Item(4138));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Pyrofiend!"));
			} else if (player.getBank().hasSpaceFor((new Item(4138)))) {
				player.getBank().add((new Item(4138)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive pet Pyrofiend! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a the skill pet Pyrofiend while Smithing!");
		}
			}
	
	public static void handleBloodMoney(Player player) {
		if (player.getInventory().hasSpaceFor(new Item(13307))) {
			player.getInventory().add(new Item(13307, Utility.randomNumber(30)));
		} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
			player.getBank().add((new Item(13307, Utility.randomNumber(30))));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some bloodmoney! It has been sent to your bank."));
		}
			}
	
	public static void handleFmCasket(Player player) {
		int random = Utility.randomNumber(200);
		if (random == 1) {
		if (player.getInventory().hasSpaceFor(new Item(19836))) {
			player.getInventory().add(new Item(19836, 1));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Firemaking casket!"));
		} else if (player.getBank().hasSpaceFor((new Item(19836)))) {
			player.getBank().add((new Item(19836, 1)));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Firemaking casket! It has been sent to your bank."));
		}
			}
	}
	
	public static void handleClueBox(Player player) {
		int random = Utility.randomNumber(500);
		if (random == 1) {
		if (player.getInventory().hasSpaceFor(new Item(12789))) {
			player.getInventory().add(new Item(12789, 1));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Clue box!"));
		} else if (player.getBank().hasSpaceFor((new Item(12789)))) {
			player.getBank().add((new Item(12789, 1)));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Clue box! It has been sent to your bank."));
		}
			}
	}
	
}
