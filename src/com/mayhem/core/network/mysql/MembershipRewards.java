package com.mayhem.core.network.mysql;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.core.util.Utility;
import com.mayhem.core.util.logger.PlayerLogger;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.player.Player;

public class MembershipRewards {

	private static ExternalDatabase database = null;
	private static boolean prepared = false;

	private static ExecutorService executorService = null;

	public static void prepare() {
		database = new ExternalDatabase("mayhem_store", "code", "40.86.94.111/mayhem_store", 3);
		database.initialise();
		executorService = Executors.newSingleThreadExecutor();
		prepared = true;
	}

	public static void shutdown() {
		database.shutdown();
		executorService.shutdown();
	}

	/**
	 * Handles a donation request for a player
	 */
	public static void collectReward(Player player) {
		if (!prepared) {
			throw new IllegalStateException("unprepared");
		}

		if ((System.currentTimeMillis() - player.getLastRequestedLookup()) < 30000) {
			DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "I can only check the database once per 30 seconds!");
			return;
		}

		player.setLastRequestedLookup(System.currentTimeMillis());

		String query = "SELECT * FROM payments WHERE player_name='"+player.getUsername() + "'" +  "AND claimed=0 AND status='Completed'";
		String updatequery = "UPDATE payments SET claimed = 1 WHERE `player_name` = '" + player.getUsername() +"'" + "AND claimed=0 AND status='Completed'";
		executorService.submit(() -> {
			try (ResultSet resultSet = database.executeQuery(query)) {

				boolean go = false;

				while (resultSet.next()) {
					final int prod = resultSet.getInt("item_number");
					final String itemname = resultSet.getString("item_name");
					final int amount = resultSet.getInt("quantity");
					//final int price = resultSet.getInt("item_price");
					
					if (prod == 12) {
						giveItem(player, 13190, amount); // $1 bond 
					} else if (prod == 13) {
						giveItem(player, 13191, amount); // $3 bond can't trade it
					} else if (prod == 14) {
						giveItem(player, 13192, amount); // $5 bond 
					} else if (prod == 15) {
						giveItem(player, 13193, amount); // $75 bond
					} else if (prod == 16) {
						giveItem(player, 14000, amount); // $10 bond
					} else if (prod == 17) {
						giveItem(player, 13195, amount); // $25 bond
					} else if (prod == 18) {
						giveItem(player, 13196, amount); // $50 bond
					} else if (prod == 19) {
						giveItem(player, 13197, amount); // Tanzanite Helm (Serp)
					} else if (prod == 20) {
						giveItem(player, 13198, amount); // $2000 bond 
					}
					
					go = true;
					PlayerLogger.DONATION_LOGGER.log(Utility.formatPlayerName(player.getUsername()), String.format("%s has purchased package %s : %s", Utility.formatPlayerName(player.getUsername()), prod, itemname));
					DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "Thank you for purchase, " + Utility.formatPlayerName(player.getUsername()) + "!");
					database.executeQuery(updatequery);

				}

				if (!go) {
					DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "It seems there is nothing here for you to claim!");
					player.getDialogue().setNext(0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	
	public static void giveItem(Player player, int item, int amount) {
		String name = GameDefinitionLoader.getItemDef(item).getName();
		DialogueManager.sendItem1(player, "You have been given " + Utility.getAOrAn(name) + " " + name + "!" , item);
		player.getInventory().addOrCreateGroundItem(item, amount, true);
	}
		
}