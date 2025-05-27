/**
 * 
 */
package com.mayhem.rs2.content.challenges;

import com.mayhem.rs2.entity.item.Item;

/**
 * Enum containing data for daily challenges.
 * @author ReverendDread
 * Aug 10, 2018
 */
public enum DailyChallenges {
	
	MAGIC_LONGS("fletch @amount@ Magic longbows", 50, 150, false, new Item(6199, 1)),
	YEW_SHORT("fletch @amount@ Yew shortbows", 100, 200, false, new Item(6199, 1)),
	ADAMANT_MINING("mine @amount@ Adamant ores", 50, 150, false, new Item(6199, 1)),
	RUNITE_MINING("mine @amount@ Runite ores", 50, 150, false, new Item(6199, 1));
	
	String description;
	int min, max;
	boolean skiller;
	Item reward;
	
	public static DailyChallenges[] VALUES = DailyChallenges.values();
	
	/**
	 * DailyChallenges constructor.
	 * @param description
	 * 				the description of the challenege.
	 * @param min
	 * 				the minimum amount.
	 * @param max
	 * 				the maximum amount.
	 * @param skiller
	 * 				if the challenge is a skilling challenge.
	 * @param reward
	 * 				the reward for completing the challenge.
	 */
	private DailyChallenges(String description, int min, int max, boolean skiller, Item reward) {
		this.description = description;
		this.min = min;
		this.max = max;
		this.skiller = skiller;
		this.reward = reward;
	}
	
	private DailyChallenges(String description, int min, int max) {
		this(description, min, max, false, null);
	}
	
	private DailyChallenges(String description) {
		this(description, 1, 1, false, null);
	}
	
	public String getDescription(int amount) {
		return this.description.replaceAll("@amount@", "" + amount);
	}
	
}
