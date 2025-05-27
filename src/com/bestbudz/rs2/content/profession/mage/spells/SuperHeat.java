package com.bestbudz.rs2.content.profession.mage.spells;

import java.util.ArrayList;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.mage.Spell;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
//import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendOpenTab;

/**
 * Handles super heating items
 * 
 * @author Jaybane
 *
 */
public class SuperHeat extends Spell {

	/**
	 * Super Heat data
	 * 
	 * @author Jaybane
	 *
	 */
	public enum HeatData {
		BRONZE_BAR(2349, new int[][] { { 438, 1 }, { 436, 1 } }, 1, 16),
		BLURITE_BAR(9467, new int[][] { { 688, 1 } }, 8, 8),
		IRON_BAR(2351, new int[][] { { 440, 1 } }, 15, 12.5),
		SILVER_BAR(2355, new int[][] { { 442, 1 } }, 20, 13.75),
		STEEL_BAR(2353, new int[][] { { 440, 1 }, { 453, 2 } }, 30, 17.5),
		GOLD_BAR(2357, new int[][] { { 444, 1 } }, 40, 22.5),
		MITHRIL_BAR(2359, new int[][] { { 447, 1 }, { 453, 4 } }, 50, 30),
		ADAMANT_BAR(2361, new int[][] { { 449, 1 }, { 453, 6 } }, 70, 37.5),
		RUNITE_BAR(2363, new int[][] { { 451, 1 }, { 453, 8 } }, 85, 50);

		private final int productId;
		private final int[][] requiredItems;
		private final int gradeRequired;
		private final double experience;

		private HeatData(int productId, int[][] requiredItems, int gradeRequired, double experience) {
		this.productId = productId;
		this.requiredItems = requiredItems;
		this.gradeRequired = gradeRequired;
		this.experience = experience;
		}

		public int getProduct() {
		return productId;
		}

		public int[][] getRequired() {
		return requiredItems;
		}

		public int getGrade() {
		return gradeRequired;
		}

		public double getExperience() {
		return experience;
		}

		public static HeatData getItem(Stoner stoner, int item) {
		for (HeatData data : HeatData.values()) {
			for (int i = 0; i < data.getRequired().length; i++) {
				if (item == data.getRequired()[i][0])
					return data;
			}
		}
		stoner.send(new SendMessage("You can't superheat this item!"));
		return null;
		}

	}

	@Override
	public boolean execute(Stoner stoner) {
	if (stoner.getProfession().locked()) {
		return false;
	}

	if (stoner.getAttributes().get("mageitem") == null) {
		return false;
	}

	int item = stoner.getAttributes().getInt("mageitem");

	HeatData data = HeatData.getItem(stoner, item);

	if (data == null) {
		return false;
	}

	ArrayList<String> required = new ArrayList<String>();

	for (int i = 0; i < data.getRequired().length; i++) {
		if (!stoner.getBox().hasItemAmount(data.getRequired()[i][0], data.getRequired()[i][1])) {
			required.add(data.getRequired()[i][1] + "x " + GameDefinitionLoader.getItemDef(data.getRequired()[i][0]).getName() + " ");
			continue;
		}
	}

	if (!required.isEmpty()) {
		stoner.send(new SendMessage("Super heating " + GameDefinitionLoader.getItemDef(data.getProduct()).getName() + " requires: " + required + "."));
		return false;
	}

	if (stoner.getProfession().getGrades()[Professions.FORGING] < data.getGrade()) {
		DialogueManager.sendStatement(stoner, "You need a forging grade of " + data.getGrade() + " to do this!");
		return false;
	}

	stoner.getBox().remove(561, 1);
	stoner.getBox().remove(554, 4);

	for (int i = 0; i < data.getRequired().length; i++) {
		stoner.getBox().remove(data.getRequired()[i][0], data.getRequired()[i][1]);
	}

	stoner.getUpdateFlags().sendAnimation(new Animation(722));
	stoner.getUpdateFlags().sendGraphic(new Graphic(148));
	stoner.getProfession().lock(4);

	if (Utility.random(100) <= 10) {
		stoner.getUpdateFlags().sendForceMessage("Ouch!");
		stoner.send(new SendMessage("Something went wrong whilst super heating your item; hurting you badly!"));
		stoner.hit(new Hit(Utility.random(10)));
		return true;
	}

	stoner.send(new SendMessage("You have successfully super heated " + GameDefinitionLoader.getItemDef(data.getProduct()).getName() + "."));
	stoner.getProfession().addExperience(Professions.FORGING, data.getExperience());
	stoner.getBox().add(data.getProduct(), 1);
	stoner.send(new SendOpenTab(6));
	return true;
	}

	@Override
	public double getExperience() {
	return 0;
	}

	@Override
	public int getGrade() {
	return 43;
	}

	@Override
	public String getName() {
	return "Super Heat";
	}

	// @Override
	// public Item[] getRunes() {
	// return new Item[] { new Item(561, 1), new Item(554, 4) };
	// }

}