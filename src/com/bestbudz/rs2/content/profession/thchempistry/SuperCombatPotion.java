package com.bestbudz.rs2.content.profession.thchempistry;

import java.util.ArrayList;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles making Jah Powa
 * 
 * @author Jaybane
 *
 */
public class SuperCombatPotion {  

	/**
	 * The Jah Powa identification
	 */
	private final static int POTION_ID = 12695;

	/**
	 * All the required items to make the potion
	 */
	private final static int[] ITEMS = { 11836, 11830, 11838, 111 };

	/**
	 * The THC-hempistry grade required to make the potion
	 */
	private final static short GRADE = 91;

	/**
	 * The experience given for making the potion
	 */
	private final static short EXPERIENCE = 8500;


		/**
	 * The THC-hempistry grade required to make the potion
	 */
	private final static short ADVANCE = 4;


	/**
	 * Handles item on item
	 * 
	 * @param stoner
	 * @param itemUsed
	 * @param usedWith
	 * @return
	 */
	public static boolean itemOnItem(Stoner stoner, Item itemUsed, Item usedWith) {

	// Check if item is being used on the same item
	if (itemUsed.getId() == usedWith.getId()) {
		return false;
	}

	// Creates item if items being used are correct
	for (int index = 0; index < ITEMS.length; index++) {
		if (itemUsed.getId() == ITEMS[index] || usedWith.getId() == ITEMS[index]) {
			create(stoner);
			return true;
		}
	}
	return false;
	}

	/**
	 * Creates the potion
	 * 
	 * @param stoner
	 */
	private static void create(Stoner stoner) {

	// Checks if stoner meets the grade requirement
	if (stoner.getGrades()[Professions.THCHEMPISTRY] < GRADE) {
		stoner.send(new SendMessage("You need a THC-hempistry grade of " + GRADE + " to do this!"));
		return;
	}

	if (stoner.getProfessionAdvances()[Professions.THCHEMPISTRY] < ADVANCE) {
		stoner.send(new SendMessage("You need to advance THC-hempistry to " + ADVANCE + " to do this!"));
		return;
	}


	if (!stoner.getEquipment().isWearingItem(6575)) {
				DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
		return;
	}

	// Checks if stoner has all items
	boolean hasItems = true;

	// List of all required items
	ArrayList<String> required = new ArrayList<String>();

	// Checks the box to see if stoner has all items
	for (int index = 0; index < ITEMS.length; index++) {
		if (!stoner.getBox().hasItemId(ITEMS[index])) {
			String name = GameDefinitionLoader.getItemDef(ITEMS[index]).getName();
			hasItems = false;
			required.add(name);
			continue;
		}
	}

	// Send message of missing items if stoner does not have all items
	if (!hasItems) {
		stoner.send(new SendMessage("@dre@Making Jah Powa (4) requires: " + required + "."));
		return;
	}

	// Removes all the items from box
	for (int index = 0; index < ITEMS.length; index++) {
		stoner.getBox().remove(ITEMS[index]);
	}

	// Adds the potion to box
	stoner.getBox().add(POTION_ID, 1);

	// Sends dialogue message of success
	DialogueManager.sendItem1(stoner, "You have combined all the ingredients!", POTION_ID);
	stoner.getUpdateFlags().sendAnimation(830, 0);

	// Adds experience to the sill
	stoner.getProfession().addExperience(Professions.THCHEMPISTRY, EXPERIENCE);
	}

}
