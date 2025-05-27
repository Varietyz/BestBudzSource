/**
 * 
 */
package com.mayhem.rs2.content.challenges;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * This class handles all daily challenge related methods.
 * @author ReverendDread
 * Aug 10, 2018
 */
public class DailyChallengeHandler {

	/**
	 * The player.
	 */
	private final Player player;
	
	/**
	 * Represents 1 day in milliseconds.
	 */
	private static final int ONE_DAY = 86400000;
	
	/**
	 * The daily challenge.
	 */
	private DailyChallenges challenge;
	
	/**
	 * If the player ops into skilling only.
	 */
	private boolean skiller;
	
	/**
	 * The players current challenge amount.
	 */
	private int challengeAmount;
	
	/**
	 * If the challenge has been completed.
	 */
	private boolean completed;
	
	/**
	 * The time in milliseconds the last challenge was recieved.
	 */
	private long lastChallenge;
	
	/**
	 * The constructor
	 * @param player
	 * 				the player.
	 * @param challenge
	 * 				the challenge.
	 */
	public DailyChallengeHandler(final Player player, DailyChallenges challenge) {
		this.player = player;
		this.challenge = challenge;
		if (needsReset())
		resetChallenge();
	}
	
	/**
	 * Resets the daily challenge and gives the players a new challenge.
	 */
	public void resetChallenge() {
		int size = DailyChallenges.VALUES.length;
		this.lastChallenge = System.currentTimeMillis();
		this.challenge = DailyChallenges.VALUES[Utility.randomNumber(size)];
		//Resets the challenge until the player gets a skiller challenge if they opt into skilling only.
		if (!challenge.skiller && skiller) {
			resetChallenge();
			return;
		}
		this.challengeAmount = Utility.random(this.challenge.max) + this.challenge.min;
		player.getInventory().addOrCreateGroundItem(new Item(6199, 1));
	}
	
	/**
	 * Completes the daily challenge and grants the player its rewards if it has any.
	 */
	public void completeChallenge() {
		Item reward = this.challenge.reward;
		if (reward == null)
			player.send(new SendMessage("@blu@Well done, you've completed your daily challenge."));
		else  {
			player.send(new SendMessage("@blu@Well done, you've completed your daily challenge. You've been awarded x" + reward.getAmount() + " " + reward.getDefinition().getName() + "."));
			player.getInventory().addOrCreateGroundItem(reward);
		}
		this.challenge = null;	
	}
	
	/**
	 * Deincrements the amount of the challenge required till completion.
	 */
	public void deincrementChallenge(DailyChallenges challenge) {
		if (this.challenge == null)
			return;
		if (this.challenge == challenge) {
			this.challengeAmount--;
			if (challengeAmount == 0)
				completeChallenge();
			else if (this.challengeAmount % 25 == 0) {
                player.send(new SendMessage("You've progressed in your daily challenge, you now need to " 
                        + challenge.getDescription(this.challengeAmount)));
            }
		}
	}
	
	/**
	 * If the challenge needs reset or not.
	 * @return
	 * 			true if its been more then 1 day since the last challenge, false otherwise.
	 */
	public boolean needsReset() {
		return (this.lastChallenge + ONE_DAY > System.currentTimeMillis());
	}
	
	/**
	 * Sets the daily challenge.
	 * @param challenge
	 * 			the challenge.
	 */
	public void setChallenge(DailyChallenges challenge) {
		this.challenge = challenge;
	}
	
	/**
	 * Sets the amount of the players current challenge progress.
	 * @param amount
	 * 			the amount.
	 */
	public void setAmount(int amount) {
		this.challengeAmount = amount;
	}
	
	/**
	 * Gets the progress of the daily challenge.
	 * @return
	 */
	public int getAmount() {
		return this.challengeAmount;
	}
	
	/**
	 * Sets the last time the player recieved a daily challenge.
	 * @param time
	 * 			the time in milliseconds.
	 */
	public void setLastChallenge(long time) {
		this.lastChallenge = time;
	}
	
	/**
	 * Gets the last time a player recieved a daily challenge.
	 * @return
	 */
	public long getLastChallenge() {
		return this.lastChallenge;
	}
	
	/**
	 * Sets if the player opts out of pvp challenges.
	 * @param skiller
	 */
	public void setSkiller(boolean skiller) {
		this.skiller = skiller;
	}
	
	/**
	 * If the player is skiller challenges only.
	 * @return
	 */
	public boolean isSkiller() {
		return this.skiller;
	}
	
	/**
	 * Gets the daily challenge.
	 * @return
	 */
	public DailyChallenges getChallenge() {
		return challenge;
	}
	
	/**
	 * If the player has completed the daily challenge or not.
	 * @return
	 */
	public boolean hasCompleted() {
		return completed;
	}
	
}
