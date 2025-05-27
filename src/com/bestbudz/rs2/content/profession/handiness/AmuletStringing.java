package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles amulet stringing
 * 
 * @author Jaybane
 *
 */
public class AmuletStringing {

	/**
	 * Amulet Data
	 * 
	 * @author Jaybane
	 *
	 */
	public static enum AmuletData {
		GOLD(1673, 1693),
		SAPPHIRE(1675, 1695),
		EMERALD(1677, 1697),
		RUBY(1679, 1699),
		DIAMOND(1681, 1701),
		DRAGONSTONE(1683, 1703),
		ONYX(6579, 6582);

		private int amuletId, product;

		private AmuletData(final int amuletId, final int product) {
		this.amuletId = amuletId;
		this.product = product;
		}

		public int getAmuletId() {
		return amuletId;
		}

		public int getProduct() {
		return product;
		}
		
	}

	/**
	 * Strings the amulet
	 * 
	 * @param stoner
	 * @param itemUsed
	 * @param usedWith
	 */
	public static void stringAmulet(final Stoner stoner, final int itemUsed, final int usedWith) {
	final int amuletId = (itemUsed == 1759 ? usedWith : itemUsed);
	for (final AmuletData a : AmuletData.values()) {
		if (amuletId == a.getAmuletId()) {

			// Removes the items
			stoner.getBox().remove(1759, 1);
			stoner.getBox().remove(amuletId, 1);

			// Adds the item
			stoner.getBox().add(a.getProduct(), 1);

			// Gives experience
			stoner.getProfession().addExperience(Professions.HANDINESS, Utility.random(3));

			// Gets the name of item
			String name = GameDefinitionLoader.getItemDef(a.getProduct()).getName();

			// Send the message
			stoner.send(new SendMessage("The string slapped your face, you get mad and conquer it making " + Utility.getAOrAn(name) + " " + name + "."));

			// Send the achievement
			AchievementHandler.activateAchievement(stoner, AchievementList.STRING_100_AMULETS, 1);

		}
	}
	}

}
