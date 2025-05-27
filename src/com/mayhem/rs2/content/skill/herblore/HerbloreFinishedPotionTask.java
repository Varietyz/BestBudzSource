package com.mayhem.rs2.content.skill.herblore;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendEnterXInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendItemOnInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendMoveComponent;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.mayhem.rs2.entity.player.net.out.impl.SendSound;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

public class HerbloreFinishedPotionTask extends Task {

	public static final String HERBLORE_ITEM_1_KEY = "herbloreitem1";
	public static final String HERBLORE_ITEM_2_KEY = "herbloreitem2";

	public static void attemptPotionMaking(Player player, int amount) {
		FinishedPotionData data = FinishedPotionData.forIds(((Item) player.getAttributes().get("herbloreitem1")).getId(), ((Item) player.getAttributes().get("herbloreitem2")).getId());
		if (data == null) {
			data = FinishedPotionData.forId(((Item) player.getAttributes().get("herbloreitem2")).getId());
		}
		if (data == null) {
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return;
		}
		if (!meetsRequirements(player, data, false)) {
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return;
		}
		createPotion(player, data);
		player.getUpdateFlags().sendAnimation(new Animation(363));
		amount--;
		TaskQueue.queue(new HerbloreFinishedPotionTask(player, data, amount));
		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
	}

	private static void createPotion(Player player, FinishedPotionData data) {
		handleBloodMoney(player);
		handleMoryLegs1(player);
		handleMoryLegs2(player);
		handleMoryLegs3(player);
		player.skillPoints += 35;
		handlePetDrop(player);
		handleClueBox(player);
		player.getInventory().remove(new Item(data.getUnfinishedPotion(), 1));
		player.getInventory().remove(new Item(data.getItemNeeded(), 1));
		player.getInventory().add(new Item(data.getFinishedPotion(), 1));
		player.getSkill().addExperience(15, data.getExpGained());
		player.getClient().queueOutgoingPacket(new SendMessage("You manage to create a Potion! You now have @blu@" + player.skillPoints + "</col> Skill points."));

	}

	public static boolean displayInterface(Player player, Item used, Item usedWith) {
		FinishedPotionData data = FinishedPotionData.forIds(used.getId(), usedWith.getId());

		if (data == null) {
			return false;
		}

		Item finishedPotion = new Item(data.getFinishedPotion(), 1);

		if (finishedPotion.getDefinition() == null) {
			player.getClient().queueOutgoingPacket(new SendMessage("This item is not supported."));
			return true;
		}
		player.getClient().queueOutgoingPacket(new SendChatBoxInterface(4429));
		player.getClient().queueOutgoingPacket(new SendMoveComponent(0, 25, 1746));
		player.getClient().queueOutgoingPacket(new SendItemOnInterface(1746, 170, finishedPotion.getId()));

		if (finishedPotion.getDefinition() != null && finishedPotion.getDefinition().getName() != null) {
			player.getClient().queueOutgoingPacket(new SendString("\\n \\n \\n \\n \\n \\n \\n" + finishedPotion.getDefinition() != null ? finishedPotion.getDefinition().getName() : "null", 2799));
		} else {
			player.getClient().queueOutgoingPacket(new SendString("\\n \\n \\n \\n \\n \\n \\n Invalid Item", 2799));
		}

		player.getAttributes().set("herbloreitem1", used);
		player.getAttributes().set("herbloreitem2", usedWith);
		return true;
	}

	public static boolean handleHerbloreButtons(Player player, int buttonId) {
		int amount = 0;
		for (int i = 0; i < BUTTON_IDS.length; i++) {
			if (BUTTON_IDS[i][0] == buttonId) {
				amount = BUTTON_IDS[i][1];
				break;
			}
		}
		if (amount == 0) {
			return false;
		}
		if (amount != 100)
			attemptPotionMaking(player, amount);
		else {
			player.getClient().queueOutgoingPacket(new SendEnterXInterface(4430, ((Item) player.getAttributes().get("herbloreitem1")).getId()));
		}
		return true;
	}

	private static boolean meetsRequirements(Player player, FinishedPotionData data, boolean running) {
		if (player.getSkill().getLevels()[15] < data.getLevelReq()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need an herblore level of " + data.getLevelReq() + " to make this potion."));
			return false;
		}
		if ((!player.getInventory().hasItemId(data.getUnfinishedPotion())) || (!player.getInventory().hasItemId(data.getItemNeeded()))) {
			player.getClient().queueOutgoingPacket(new SendMessage(!running ? "You don't have the ingredients required to make this potion." : "You have run out of ingredients to make this potion."));
			return false;
		}
		return true;
	}

	private final Player player;

	private final FinishedPotionData data;

	private int amountToMake;

	private static final int[][] BUTTON_IDS = { { 10239, 1 }, { 10238, 5 }, { 6211, 28 }, { 6212, 100 } };

	public HerbloreFinishedPotionTask(Player player, FinishedPotionData data, int amount) {
		super(player, 2, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
		amountToMake = amount;
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, data, true)) {
			stop();
			return;
		}

		if (amountToMake == 0) {
			stop();
			return;
		}

		player.getClient().queueOutgoingPacket(new SendSound(281, 0, 0));

		player.getUpdateFlags().sendAnimation(new Animation(363));

		createPotion(player, data);

		amountToMake -= 1;
		if (amountToMake == 0)
			stop();
	}

	@Override
	public void onStop() {
	}

	
	public static void handleBloodMoney(Player player) {
		if (player.getInventory().hasSpaceFor(new Item(13307))) {
			player.getInventory().add(new Item(13307, Utility.randomNumber(15)));
		} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
			player.getBank().add((new Item(13307, Utility.randomNumber(15))));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood! It has been sent to your bank."));
		}
			}
	
	public static void handlePetDrop(Player player) {
		int random = Utility.randomNumber(4000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(13071))) {
				player.getInventory().add(new Item(13071));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Chompy chick!"));
			} else if (player.getBank().hasSpaceFor((new Item(13071)))) {
				player.getBank().add((new Item(13071)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive pet Chompy chick! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a the skill pet Chompy chick while training Herblore!");
		}
			}
	
	public static void handleClueBox(Player player) {
		int random = Utility.randomNumber(800);
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
	public static void handleMoryLegs1(Player player) {

		int random = Utility.randomNumber(500);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13112, 1)) {
				player.getEquipment().remove(new Item(13112, 1));
				player.getEquipment().equip(new Item(13113), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Morytania legs have been upgraded!"));
				}
			}
		}

	public static void handleMoryLegs2(Player player) {

		int random = Utility.randomNumber(750);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13113, 1)) {
				player.getEquipment().remove(new Item(13113, 1));
				player.getEquipment().equip(new Item(13114), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Morytania legs have been upgraded!"));
				}
		}
	}
	public static void handleMoryLegs3(Player player) {

		int random = Utility.randomNumber(1000);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13114, 1)) {
				player.getEquipment().remove(new Item(13114, 1));
				player.getEquipment().equip(new Item(13115), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Morytania legs have been upgraded!"));
				}
		}
	}
}
