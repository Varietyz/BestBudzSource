package com.mayhem.rs2.content.skill.smithing;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class Smelting extends Task {

	private final Player player;
	private final SmeltingData data;
	private final int amount;
	private int smelted = 0;
	private final String name;
	public static final Animation SMELTING_ANIMATION = new Animation(899, 0);
	public static final String A = "You smelt ";
	public static final String B = ".";
	public static final String IRON_FAILURE = "You fail to refine the iron ore.";

	public Smelting(Player player, int amount, SmeltingData data) {
		super(player, 2, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
		this.amount = amount;
		name = data.getResult().getDefinition().getName();

		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

		if (!canSmelt(player, data, false)) {
			stop();
		}
	}

	public boolean canSmelt(Player player, SmeltingData data, boolean taskRunning) {
		if (player.getMaxLevels()[13] < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a Smithing level of " + data.getLevelRequired() + " to smelt this bar."));
			return false;
		}

		for (Item i : data.getRequiredOres()) {
			if (!player.getInventory().hasItemAmount(i.getId(), i.getAmount())) {
				player.getClient().queueOutgoingPacket(new SendMessage(taskRunning ? "You have run out of " + i.getDefinition().getName() + "." : "You don't not have any " + i.getDefinition().getName().toLowerCase() + " to smelt."));
				return false;
			}
		}

		return true;
	}

	@Override
	public void execute() {
		if (!canSmelt(player, data, true)) {
			stop();
			return;
		}

		player.getUpdateFlags().sendAnimation(SMELTING_ANIMATION);

		player.getInventory().remove(data.getRequiredOres(), false);

		if (data == SmeltingData.IRON_BAR) {
			if (Skills.isSuccess(player, 13, data.getLevelRequired())) {
				player.getInventory().add(data.getResult(), false);
				player.getClient().queueOutgoingPacket(new SendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + "."));
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You fail to refine the iron ore."));
			}
		} else {
			player.getInventory().add(data.getResult(), false);
			player.getClient().queueOutgoingPacket(new SendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + ". You now have @blu@" + player.skillPoints + " Skill points."));
		}

		player.getInventory().update();

		player.getSkill().addExperience(13, data.getExp());
		player.skillPoints += 55;
		handleBloodMoney(player);

		if (++smelted == amount)
			stop();
	}

	public boolean isSuccess(Player player, SmeltingData data) {
		return Skills.isSuccess(player, 13, data.levelRequired);
	}

	@Override
	public void onStop() {
	}
	
	public static void handleBloodMoney(Player player) {
		if (player.getInventory().hasSpaceFor(new Item(13307))) {
			player.getInventory().add(new Item(13307, Utility.randomNumber(30)));
		} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
			player.getBank().add((new Item(13307, Utility.randomNumber(30))));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood! It has been sent to your bank."));
		}
			}
}
