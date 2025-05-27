package com.mayhem.rs2.content.skill.thieving;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles stalls for thieving class
 * 
 * @author Daniel
 *
 */
public class HomeStalls extends Task {
	
	private Player player;

	private stallData data;

	public HomeStalls(int delay, Player player, stallData data) {
		super(player, delay, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, data)) {
			stop();
			return;
		}
		successfull(player, data);
		stop();
	}

	@Override
	public void onStop() {
	}

	private enum stallData {
		FOOD("food", 4875, 3162, 1500, 1, 30),
		GENERAL("general", 4876, 1887, 2500, 25, 50),
		CRAFT("crafting", 4874, 1635, 5500, 50, 70),
		MAGIC("magic", 4877, 8788, 1, 75, 90),
		SCIMITAR("scimitar", 4878, 6721, 12000, 85, 125);

		private String name;
		private int objectId;
		private int itemId;
		private int levelReq;
		private int xpGained;

		private stallData(String name, int objectId, int itemId, int itemAmount, int levelReq, int xpGained) {
			this.name = name;
			this.objectId = objectId;
			this.itemId = itemId;
			this.levelReq = levelReq;
			this.xpGained = xpGained;
		}

		public static stallData getObjectById(int id) {
			for (stallData data : stallData.values())
				if (data.objectId == id)
					return data;
			return null;
		}
	}
	
	public static void attempt(Player player, int id, Location location) {
		stallData data = stallData.getObjectById(id);

		if (data == null) {
			return;
		}

		if (player.getSkill().locked()) {
			return;
		}
		if (!meetsRequirements(player, data)) {
			return;
		}
		player.getSkill().lock(3);
		player.getUpdateFlags().sendAnimation(new Animation(832));

		TaskQueue.queue(new HomeStalls(4, player, data));
	}
	
	public static boolean meetsRequirements(Player player, stallData stall) {
		if (stall == null) {
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have enough inventory spaces left to hold this."));
			return false;
		}
		if (player.getSkill().getLevels()[17] < stall.levelReq) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a thieving level of " + stall.levelReq + " to do this!"));
			return false;
		}
		return true;
	}

	public static void successfull(Player player, stallData stall) {
		player.getUpdateFlags().sendAnimation(new Animation(832));
		handleBloodMoney(player);
		player.getInventory().add(new Item(stall.itemId, 1));
		player.getSkill().addExperience(17, player.inMemberZone() ? stall.xpGained * 2: stall.xpGained);
		player.skillPoints += 45;
		player.getClient().queueOutgoingPacket(new SendMessage("You steal some loot from the " + stall.name + " stall. You now have @blu@" + player.skillPoints + "</col> Skill points."));
		AchievementHandler.activate(player, AchievementList.THIEVE_300_TIMES_FROM_STALLS, 1);
		handleRandom(player);
		handleThievingCasket(player);
		handleRaccoon(player);
		handleArdyCape1(player);
		handleArdyCape2(player);
		handleArdyCape3(player);
}

public static void handleRandom(Player player) {
	int random = Utility.randomNumber(100);
	if (random == 1) {
		//if (player.inMemberZone()) {
			//player.teleport(new Location(2854, 3337, 0));
	//	} else {
		//	player.teleport(new Location(3087, 3492, 0));
		//}
		player.getUpdateFlags().sendAnimation(new Animation(4367));
		player.getClient().queueOutgoingPacket(new SendMessage("Some mystical force drops you from the sky causing damage."));
		player.hit(new Hit(2, Hit.HitTypes.NONE));
		player.getUpdateFlags().sendForceMessage(Utility.randomElement(FORCED_CHAT));
		AchievementHandler.activate(player, AchievementList.FAIL_50_TIMES_THIEVING_STALLS, 1);
	}
}

public final static String FORCED_CHAT[] = { "Ow!", "Ouch!", "What the-?", "Noooo!", "I want to live!", "Somebody help me!", };

public static void handleBloodMoney(Player player) {
	if (player.getInventory().hasSpaceFor(new Item(13307))) {
		player.getInventory().add(new Item(13307, Utility.randomNumber(30)));
	} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
		player.getBank().add((new Item(13307, Utility.randomNumber(30))));
		player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood! It has been sent to your bank."));
	}
		}

public static void handleThievingCasket(Player player) {
	int random = Utility.randomNumber(600);
	if (random == 1) {
		GroundItemHandler.add(new Item(3849, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@A Casket falls off the table and lands at your feet. better pick it up!"));
	}
		}

public static void handleRaccoon(Player player) {
	int random = Utility.randomNumber(5000);
	if (random == 1) {
		if (player.getInventory().hasSpaceFor(new Item(20663))) {
			player.getInventory().add(new Item(20663));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Raccoon!"));
		} else if (player.getBank().hasSpaceFor((new Item(20663)))) {
			player.getBank().add((new Item(20663)));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Raccoon! It has been sent to your bank."));
		}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Raccoon while Thieving!");
		}
	}

public static void handleArdyCape1(Player player) {

	int random = Utility.randomNumber(600);
	if (random == 1) {
		if (player.getEquipment().isWearingItem(13121, 1)) {
			player.getEquipment().remove(new Item(13121, 1));
			player.getEquipment().equip(new Item(13122), 1);
			player.setAppearanceUpdateRequired(true);
			player.getUpdateFlags().setUpdateRequired(true);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Ardougne cape has been upgraded!")); }
		}
	}

public static void handleArdyCape2(Player player) {

	int random = Utility.randomNumber(1000);
	if (random == 1) {
		if (player.getEquipment().isWearingItem(13122, 1)) {
			player.getEquipment().remove(new Item(13122, 1));
			player.getEquipment().equip(new Item(13123), 1);
			player.setAppearanceUpdateRequired(true);
			player.getUpdateFlags().setUpdateRequired(true);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Ardougne cape has been upgraded!")); }
	}
}
public static void handleArdyCape3(Player player) {

	int random = Utility.randomNumber(3000);
	if (random == 1) {
		if (player.getEquipment().isWearingItem(13123, 1)) {
			player.getEquipment().remove(new Item(13123, 1));
			player.getEquipment().equip(new Item(13124), 1);
			player.setAppearanceUpdateRequired(true);
			player.getUpdateFlags().setUpdateRequired(true);
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Ardougne cape has been upgraded!")); }
	}
}

}