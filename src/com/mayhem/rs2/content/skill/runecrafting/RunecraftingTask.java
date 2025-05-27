package com.mayhem.rs2.content.skill.runecrafting;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendSound;

public class RunecraftingTask extends Task {

	public static boolean attemptRunecrafting(Player player, GameObject object) {
		RunecraftingData data = RunecraftingData.forId(object.getId());

		if (data == null) {
			return true;
		}

		if (getEssenceId(player) == -1) {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have any essence to craft runes with."));
			return true;
		}

		if (!meetsRequirements(player, data, object)) {
			return true;
		}

		TaskQueue.queue(new RunecraftingTask(player, data, getEssenceId(player)));
		return true;
	}

	private static int getEssenceId(Player player) {
		if (player.getInventory().hasItemId(1436)) {
			return 1436;
		}
		if (player.getInventory().hasItemId(7936)) {
			return 7936;
		}
		return -1;
	}

	private static boolean meetsRequirements(Player player, RunecraftingData data, GameObject object) {
		if (player.getSkill().getLevels()[20] < data.getLevel()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a runecrafting level of " + data.getLevel() + " to craft this rune."));
			return false;
		}
		return true;
	}

	private final Player player;

	private final RunecraftingData data;

	private final int essenceId;

	public static final int PURE_ESSENCE = 7936;

	public RunecraftingTask(Player player, RunecraftingData data, int essenceId) {
		super(player, 1, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
		this.essenceId = essenceId;
	}

	@Override
	public void execute() {
		player.getClient().queueOutgoingPacket(new SendSound(481, 1, 0));
		player.getUpdateFlags().sendAnimation(new Animation(791));
		player.getUpdateFlags().sendGraphic(Graphic.highGraphic(186, 0));
		
		int amount = player.getInventory().getItemAmount(essenceId);
		
		if (essenceId == PURE_ESSENCE) {
			amount *= 2;
		}

		player.getSkill().addExperience(20, amount * data.getXp());
		player.skillPoints += 100;
		handleBloodMoney(player);
		handlePetDrop(player);
		handleClueBox(player);
		player.getClient().queueOutgoingPacket(new SendMessage("You create some runes. You now have @blu@" + player.skillPoints + "</col> Skill points."));

		player.getInventory().remove(new Item(essenceId, amount / (essenceId == PURE_ESSENCE ? 2 : 1)));
		player.getInventory().add(new Item(data.getRuneId(), amount * getMultiplier()));

		if (Utility.random(5) == 0) {
			if (!player.getInventory().hasItemId(5509) && !player.getBank().hasItemId(5509)) {
				player.getInventory().add(5509, 1);
				player.send(new SendMessage("You find a small pouch while runecrafting!"));
			} else if (!player.getInventory().hasItemId(5510) && !player.getBank().hasItemId(5510)) {
				player.getInventory().add(5510, 1);
				player.send(new SendMessage("You find a medium pouch while runecrafting!"));
			} else if (!player.getInventory().hasItemId(5512) && !player.getBank().hasItemId(5512)) {
				player.getInventory().add(5512, 1);
				player.send(new SendMessage("You find a large pouch while runecrafting!"));
			} else if (!player.getInventory().hasItemId(5514) && !player.getBank().hasItemId(5514)) {
				player.getInventory().add(5514, 1);
				player.send(new SendMessage("You find a giant pouch while runecrafting!"));
			}
		}

		if (data == RunecraftingData.BLOOD) {
			AchievementHandler.activate(player, AchievementList.CRAFT_1500_BLOOD_RUNES, amount);
		}

		stop();
	}

	private int getMultiplier() {
		int multiplier = 1;
		for (int i = 1; i < data.getMultiplier().length; i++) {
			if (player.getMaxLevels()[20] >= data.getMultiplier()[i]) {
				multiplier = i;
			}
		}

		return multiplier;
	}

	@Override
	public void onStop() {
	}
	
	public static void handleBloodMoney(Player player) {
		if (player.getInventory().hasSpaceFor(new Item(13307))) {
			player.getInventory().add(new Item(13307, Utility.randomNumber(80)));
		} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
			player.getBank().add((new Item(13307, Utility.randomNumber(80))));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood money! It has been sent to your bank."));
		}
			}
	
	public static void handlePetDrop(Player player) {
		int random = Utility.randomNumber(1000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(7986))) {
				player.getInventory().add(new Item(7986));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Abyssal leech!"));
			} else if (player.getBank().hasSpaceFor((new Item(7986)))) {
				player.getBank().add((new Item(7986)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Abyssal leech! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Abyssal leech while Runecrafting!");
		}
			}
	
	public static void handleClueBox(Player player) {
		int random = Utility.randomNumber(150);
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
