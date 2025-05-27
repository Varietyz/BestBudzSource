package com.mayhem.rs2.content.gambling;

import com.mayhem.core.cache.map.Region;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.Task.BreakType;
import com.mayhem.core.task.Task.StackType;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * @author Andy || ReverendDread Mar 6, 2017
 */
public class Flowering {
	
	

	/**
	 * Plants a flower.
	 * @param player The player.
	 */
	public static void plantFlower(Player player) {
		int[] regularFlowers = {2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987};
		int[] rareFlowers = {2987, 2988};
		int randomRegularFlower = Utility.randomNumber(regularFlowers.length);
		int randomRareFlower = Utility.randomNumber(rareFlowers.length);
		Location loc;
		loc = player.getLocation();
		if (!player.getInventory().hasItemId(299)) {
			player.send(new SendMessage("You need at least 1 mithril seed to plant a flower."));
		}
		if (player.getSkill().locked())
			return;
		player.getInventory().remove(299, 1);
		ObjectManager.spawnWithObject(Utility.randomNumber(100) >= 1 ?
				regularFlowers[randomRegularFlower] : rareFlowers[randomRareFlower], loc, 10, 0);
		TaskQueue.queue(new Task(player, 1, false) {

			@Override
			public void execute() {
				walk(player);
				stop();
			}

			@Override
			public void onStop() {

			}
			
		});
		TaskQueue.queue(new Task(player, 10, false, StackType.NEVER_STACK, 
				BreakType.NEVER, TaskIdentifier.PLANTING_FLOWERS) {

			@Override
			public void execute() {
				ObjectManager.deleteWithObject(loc.getX(), loc.getY(), loc.getZ());
				stop();
			}

			@Override
			public void onStop() {

			}
			
		});
		player.getSkill().lock(2);
	}
	
	private static void walk(Player player) {
		if (!Region.getRegion(player.getLocation().getX(), player.getLocation().getY()).blockedWest(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())) {
			player.getMovementHandler().walkTo(-1, 0);
		} else if (!Region.getRegion(player.getLocation().getX(), player.getLocation().getY()).blockedEast(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
			player.getMovementHandler().walkTo(1, 0);
	}
	
}
