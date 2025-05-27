package com.mayhem.core.network.mysql;

import com.mayhem.Constants;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.rspserver.motivote.MotivoteHandler;
import com.rspserver.motivote.Reward;

public class RewardHandler extends MotivoteHandler<Reward> {
	@Override
	public void onCompletion(Reward reward) {
		Player player = World.getPlayerByName(reward.username());
		if (player != null) {
			reward.complete();
		
			if (reward.rewardName().equalsIgnoreCase("gold")) {
				handleRewards(player, new Item(995, 1_000_000));
			} else if (reward.rewardName().equalsIgnoreCase("crystal keys")) {
				handleRewards(player, new Item(990, 3));				
			} else if (reward.rewardName().equalsIgnoreCase("herb box")) {
				handleRewards(player, new Item(11738, 1));
			} else if (reward.rewardName().equalsIgnoreCase("mystery box")) {
				handleRewards(player, new Item(6199, 1));
			}
			//System.out.println(reward.internalID() + " | Reward received for " + reward.username() + " (" + reward.rewardName() + ", " + reward.amount() + ")");
		}
	}
	
	private static void handleRewards(Player player, Item item) {
	if (player != null) {
		Constants.LAST_VOTER = player.getUsername();
		Constants.CURRENT_VOTES++;
		player.setVotePoints(player.getVotePoints() + 1);
		player.send(new SendMessage("Thank you for voting, " + Utility.formatPlayerName(player.getUsername()) + "!"));
		if (player.ironPlayer()) {
			player.send(new SendMessage("As you are using an Iron account you only recieved vote points."));
			return;
		}
		player.getInventory().addOrCreateGroundItem(item);
		
	}

	}
}
