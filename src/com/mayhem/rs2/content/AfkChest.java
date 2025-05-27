package com.mayhem.rs2.content;

import com.mayhem.rs2.entity.player.Player;

public class AfkChest {
	
	
	public static void Reward(Player player) throws InterruptedException {
		int loop = 10;
			
		if (player.getRights() == 0) {
			long startTime = System.currentTimeMillis();
			System.out.println(startTime);
			for (int count = 0; ;count++) {
			long now = System.currentTimeMillis();
			if(startTime >= 10000);
			// Do nothing
			}
		} else {
			Thread.sleep(10000);
			player.getInventory().add(995, 8334 * 2);
		}		
		}

	
}
