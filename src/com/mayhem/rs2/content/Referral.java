package com.mayhem.rs2.content;

import java.util.HashMap;
import java.util.UUID;

import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * @author Andy || ReverendDread Feb 23, 2017
 */
public class Referral {
	
	/**
	 * Map containing all referals.
	 */
	public static HashMap<String, Boolean> referrals = new HashMap<String, Boolean>();
	
	/**
	 * Instanced referral id object.
	 */
	private static String randomReferral;
	
	/**
	 * Generates a random referral id.
	 * @param player The player.
	 */
	public static void generateReferralID(Player player) {	
		randomReferral = UUID.randomUUID().toString().replace("-", "");
		referrals.put(randomReferral, false);
		player.referralID = randomReferral;
		System.out.println("[Referrals]: Referral Generated - Referral Code = " + randomReferral);
		player.send(new SendMessage("Your Referral ID is: " + player.referralID));
	}

	/**
	 * Claims a referral id.
	 * @param player The player
	 * @param referral The referral code.
	 */
	public static void claimReferral(Player player, String referral) {
		if (referrals.containsKey(referral)) {
			//if referral has been claimed, do nothing.
			if (referrals.get(referral).booleanValue()) {
				player.send(new SendMessage("This referral has already been claimed."));
				return;
			}
			//if referral id = the players referral id, do nothing
			if (referrals.get(referral).equals(player.referralID)) {
				player.send(new SendMessage("You can't claim your own referral code!"));
				return;
			}
			System.out.println("" + player.getUsername() + " has redeemed a referral.");
			player.send(new SendMessage("Thank you for redeeming a referral code!"));
			/**
			 * Put rewards here.
			 */
			player.getInventory().add(new Item(995, 1));
			//removes key from map
			referrals.remove(referral);
		}
	}
	
}
