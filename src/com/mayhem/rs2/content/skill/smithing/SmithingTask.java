package com.mayhem.rs2.content.skill.smithing;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.mayhem.rs2.entity.player.net.out.impl.SendSound;

public class SmithingTask extends Task {
	public static void start(Player p, int item, int amount, int interfaceId, int slot) {
		String check = Item.getDefinition(item).getName().substring(0, 3);

		int bar = -1;

		int make = 1;

		if (Item.getDefinition(item).isStackable()) {
			make = 15;
		}

		switch (check) {
		case "Bro":
			bar = SmithingConstants.BARS[0];
			break;
		case "Iro":
			bar = SmithingConstants.BARS[1];
			break;
		case "Ste":
			bar = SmithingConstants.BARS[2];
			break;
		case "Mit":
			bar = SmithingConstants.BARS[3];
			break;
		case "Ada":
			bar = SmithingConstants.BARS[4];
			break;
		case "Run":
			bar = SmithingConstants.BARS[5];
			break;
		}

		TaskQueue.queue(new SmithingTask(p, new Item(item, make), new Item(bar, SmithingConstants.getBarAmount(interfaceId, slot)), amount));
	}

	private final Player player;
	private final Item smith;
	private final Item bar;
	private final int amount;

	private int loop = 0;

	public SmithingTask(Player player, Item smith, Item bar, int amount) {
		super(player, 2, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.smith = smith;
		this.bar = bar;
		this.amount = amount;

		int lvl = SmithingConstants.getLevel(smith.getId());

		if (player.getMaxLevels()[13] < lvl) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a Smithing level of " + lvl + " to make that."));
			stop();
		} else if (!player.getInventory().hasItemAmount(new Item(bar))) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough bars to make that."));
			stop();
		} else if (!player.getInventory().hasItemId(2347)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have a hammer to smith with."));
			stop();
		} else {
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		}
	}

	@Override
	public void execute() {
		if (!hasRequirements()) {
			stop();
			return;
		}

		player.getSkill().addExperience(13, getExperience());

		player.getInventory().remove(new Item(bar), false);
		player.getInventory().add(new Item(smith), true);
		player.skillPoints += 65;
		handleBloodMoney(player);
		handlePetDrop(player);
		handleClueBox(player);
		handleVarrockPlate1(player);
		handleVarrockPlate2(player);
		handleVarrockPlate3(player);

		player.getUpdateFlags().sendAnimation(898, 0);
		player.getClient().queueOutgoingPacket(new SendSound(468, 10, 10));

		if (smith.getAmount() == 1)
			player.getClient().queueOutgoingPacket(new SendMessage("You make " + Utility.getAOrAn(smith.getDefinition().getName()) + " " + smith.getDefinition().getName() + ". You now have @blu@" + player.skillPoints + "</col> Skill points."));
		else {
			player.getClient().queueOutgoingPacket(new SendMessage("You make " + smith.getAmount() + " " + smith.getDefinition().getName() + (!smith.getDefinition().getName().endsWith("s") ? "s" : "") + "."));
		}

		if (++loop == amount)
			stop();
	}

	public double getExperience() {
		switch (bar.getId()) {
		case 2349:
			return 12.5D * bar.getAmount();
		case 2351:
			return 25 * bar.getAmount();
		case 2353:
			return 37.5D * bar.getAmount();
		case 2359:
			return 50 * bar.getAmount();
		case 2361:
			return 62.5D * bar.getAmount();
		case 2363:
			return 75 * bar.getAmount();
		case 2350:
		case 2352:
		case 2354:
		case 2355:
		case 2356:
		case 2357:
		case 2358:
		case 2360:
		case 2362:
		}
		return 0.0D;
	}

	public boolean hasRequirements() {
		if (!player.getInventory().hasItemAmount(new Item(bar))) {
			player.getClient().queueOutgoingPacket(new SendMessage("You have run out of " + bar.getDefinition().getName() + "s."));
			return false;
		}

		if (!player.getInventory().hasItemId(2347)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have a hammer to smith with."));
			return false;
		}

		return true;
	}

	@Override
	public void onStop() {
	}
	public static void handleBloodMoney(Player player) {
		if (player.getInventory().hasSpaceFor(new Item(13307))) {
			player.getInventory().add(new Item(13307, Utility.randomNumber(30)));
		} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
			player.getBank().add((new Item(13307, Utility.randomNumber(30))));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood money! It has been sent to your bank."));
		}
			}
	
	public static void handlePetDrop(Player player) {
		int random = Utility.randomNumber(3000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(20693))) {
				player.getInventory().add(new Item(20693));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Phoenix!"));
			} else if (player.getBank().hasSpaceFor((new Item(20693)))) {
				player.getBank().add((new Item(20693)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive pet Phoenix! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a the skill pet Phoenix while Smithing!");
		}
			}
	
	public static void handleClueBox(Player player) {
		int random = Utility.randomNumber(450);
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
	public static void handleVarrockPlate1(Player player) {

		int random = Utility.randomNumber(600);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13104, 4)) {
				player.getEquipment().remove(new Item(13104, 4));
				player.getEquipment().equip(new Item(13105), 4);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your varrock plate has been upgraded!")); }
			}
		}
	
	public static void handleVarrockPlate2(Player player) {

		int random = Utility.randomNumber(900);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13105, 4)) {
				player.getEquipment().remove(new Item(13105, 4));
				player.getEquipment().equip(new Item(13106), 4);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your varrock plate has been upgraded!")); }
		}
	}
	public static void handleVarrockPlate3(Player player) {

		int random = Utility.randomNumber(2500);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13106, 4)) {
				player.getEquipment().remove(new Item(13106, 4));
				player.getEquipment().equip(new Item(13107), 4);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your varrock plate has been upgraded!")); }
		}
	}
} 
	
	

