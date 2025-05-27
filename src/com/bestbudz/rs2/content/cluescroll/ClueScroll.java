package com.bestbudz.rs2.content.cluescroll;

import com.bestbudz.rs2.content.cluescroll.Clue.ClueType;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Contains all the fundamental methods to create a functioning clue scroll.
 * 
 * @author Jaybane
 *
 */
public interface ClueScroll {

	/**
	 * Executes the clue scroll action.
	 * 
	 * @param stoner
	 *                   - The stoner executing the clue scroll.
	 * @return <code>True if the clue scroll was successfully executed; <code>False</code>
	 *         otherwise.
	 */
	public boolean execute(Stoner stoner);

	/**
	 * Checks if the stoner can complete the scroll.
	 * 
	 * @param stoner
	 *                   - The stoner to test.
	 * @return <code>True</code> if the stoner can complete the clue scroll;
	 *         <code>False</code> otherwise.
	 */
	public boolean meetsRequirements(Stoner stoner);

	/**
	 * A check if the stoner is within the completion area of the scroll.
	 * 
	 * @param location
	 *                     - The stoner's location to test.
	 * @return <code>True</code> if the location is within the completion area;
	 *         <code>False</code> otherwise.
	 */
	public boolean inEndArea(Location location);

	/**
	 * Gets the clue for the scroll.
	 * 
	 * @return The clue.
	 */
	public Clue getClue();

	/**
	 * Gets the clue scroll item id.
	 * 
	 * @return The clue scroll item id.
	 */
	public int getScrollId();

	/**
	 * Gets the difficulty of the clue scroll.
	 * 
	 * @return The clue difficulty.
	 */
	public ClueDifficulty getDifficulty();

	/**
	 * Displays the clue for a stoner.
	 * 
	 * @param stoner
	 *                   - The stoner to display the clue for.
	 */
	public default void displayClue(Stoner stoner) {
	getClue().display(stoner);
	}

	/**
	 * Gets the clue scroll type.
	 * 
	 * @return The clue scroll type.
	 */
	public default ClueType getClueType() {
	return getClue().getClueType();
	}

	/**
	 * Rewards the stoner with either the next clue scroll in the treasure trails or
	 * rewards with the final item; a casket.
	 * 
	 * <pre>
	 * Easy scrolls have a maximum of 5 clues per trail.
	 * Medium scrolls have a maximum of 6 clues per trail.
	 * Hard scrolls have a maximum of 8 clues per trail.
	 * Elite scrolls have a maximum of 11 clues per trail.
	 * </pre>
	 * 
	 * @param stoner
	 *                        - The stoner to receive the reward.
	 * @param acquirement
	 *                        - The means of which the stoner has acquired the
	 *                        reward.
	 */
	public default void reward(Stoner stoner, String acquirement) {
	Item item;
	boolean nextClue = false;

	switch (getDifficulty()) {
	case EASY:
		nextClue = stoner.getCluesCompleted()[0] < 5 && Math.random() > 0.5;

		if (nextClue) {
			stoner.setCluesCompleted(0, stoner.getCluesCompleted()[0] + 1);
		} else {
			stoner.setCluesCompleted(0, 0);
		}
		break;

	case MEDIUM:
		nextClue = stoner.getCluesCompleted()[1] < 6 && Math.random() > 0.5;

		if (nextClue) {
			stoner.setCluesCompleted(1, stoner.getCluesCompleted()[1] + 1);
		} else {
			stoner.setCluesCompleted(1, 0);
		}
		break;

	case HARD:
		nextClue = stoner.getCluesCompleted()[2] < 8 && Math.random() > 0.5;

		if (nextClue) {
			stoner.setCluesCompleted(2, stoner.getCluesCompleted()[2] + 1);
		} else {
			stoner.setCluesCompleted(2, 0);
		}
		break;
	}

	if (!nextClue) {
		DialogueManager.sendItem1(stoner, acquirement + " a casket!", ClueScrollManager.CASKET_HARD);
		stoner.send(new SendMessage("Well done, you've completed the Treasure Trail!"));
		item = new Item(getDifficulty().equals(ClueDifficulty.EASY) ? ClueScrollManager.CASKET_EASY : getDifficulty().equals(ClueDifficulty.MEDIUM) ? ClueScrollManager.CASKET_MEDIUM : ClueScrollManager.CASKET_HARD);
	} else {
		item = ClueScrollManager.getRandomClue(stoner, getDifficulty());
		if (item == null) {
			DialogueManager.sendItem1(stoner, acquirement + " a casket!", ClueScrollManager.CASKET_HARD);
			stoner.send(new SendMessage("Well done, you've completed the Treasure Trail!"));
			item = new Item(getDifficulty().equals(ClueDifficulty.EASY) ? ClueScrollManager.CASKET_EASY : getDifficulty().equals(ClueDifficulty.MEDIUM) ? ClueScrollManager.CASKET_MEDIUM : ClueScrollManager.CASKET_HARD);
		} else {
			DialogueManager.sendItem1(stoner, acquirement + " another clue!", item.getId());
		}
	}

	stoner.getBox().remove(getScrollId());
	stoner.getBox().add(item);
	}
}