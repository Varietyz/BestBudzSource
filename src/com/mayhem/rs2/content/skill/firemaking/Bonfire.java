package com.mayhem.rs2.content.skill.firemaking;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * @author Andy || ReverendDread Feb 26, 2017
 */
public class Bonfire extends Task {
	
	/**
	 * Constructs a new object.
	 * @param entity
	 * @param delay
	 */
	public Bonfire(int delay, Player entity, int logID, GameObject object) {
		super(entity, delay, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = entity;
		this.object = object;
		this.logID = logID;
	}
	
	private Player player;
	private GameObject object;
	private int logID;
	private static double experience;

	public static boolean addLog(int delay, Player player, int itemId, GameObject object) {
		for (LogData log : LogData.values()) {
			if (log.getLogId() == itemId) {
				experience = log.getExperience();
				TaskQueue.queue(new Bonfire(6, player, itemId, object));
				return true;
			}
		}
		return false; 
	}

	@Override
	public void execute() {
		if (player.getSkill().getLevels()[11] < LogData.getLogById(logID).getLevelRequired()) {
			player.send(new SendMessage("You need at least level " + LogData.getLogById(logID).getLevelRequired() + " firemaking to burn this log."));
			this.stop();
			return;
		}
		if (!ObjectManager.objectExists(object.getLocation())) {
			player.send(new SendMessage("You need a fire to do that."));
			this.stop();
			return;
		}
		if (!player.getInventory().hasItemId(logID)) {
			player.send(new SendMessage("You've ran out of logs."));
			this.stop();
			return;
		}
		player.getUpdateFlags().sendAnimation(new Animation(733));		
		player.send(new SendMessage("You add a log to the fire."));
		player.getSkill().addExperience(Skills.FIREMAKING, experience);
		handleBloodMoney(player);
		player.skillPoints += 55;
		handlePetDrop(player);
		handleFmCasket(player);
		handleClueBox(player);
		player.getInventory().remove(logID);
	}

	@Override
	public void onStop() {
		
	}
	
	public static void handlePetDrop(Player player) {
		int random = Utility.randomNumber(5500);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(4138))) {
				player.getInventory().add(new Item(4138));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Pyrofiend!"));
			} else if (player.getBank().hasSpaceFor((new Item(4138)))) {
				player.getBank().add((new Item(4138)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive pet Pyrofiend! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a the skill pet Pyrofiend while Firemaking!");
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
		int random = Utility.randomNumber(350);
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
		int random = Utility.randomNumber(550);
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
