package com.bestbudz.rs2.content;

import java.util.HashMap;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Class that handles genie lamp
 * 
 * @author Jaybane
 *
 */
public class GenieLamp {

	/**
	 * Holds all the genie lamp data
	 * 
	 * @author Jaybane
	 *
	 */
	public enum GenieData {
		ASSAULT(10252, 0),
		VIGOUR(10253, 2),
		RANGE(10254, 4),
		MAGE(10255, 6),
		AEGIS(11000, 1),
		LIFE(11001, 3),
		NECROMANCE(11002, 5),
		WEEDSMOKING(11003, 16),
		THCHEMPISTRY(11004, 15),
		ACCOMPLISHER(11005, 17),
		HANDINESS(11006, 12),
		CONSUMER(11007, 20),
		MERCENARY(47002, 18),
		CULTIVATION(54090, 19),
		QUARRYING(11008, 14),
		FORGING(11009, 13),
		FISHER(11010, 10),
		FOODIE(11011, 7),
		PYROMANIAC(11012, 11),
		LUMBERING(11013, 8),
		WOODCARVING(11014, 9);

		int buttonId, professionId;

		private GenieData(int buttonId, int professionId) {
		this.buttonId = buttonId;
		this.professionId = professionId;
		}

		public int getButton() {
		return buttonId;
		}

		public int getProfession() {
		return professionId;
		}

		public static HashMap<Integer, GenieData> genie = new HashMap<Integer, GenieData>();

		static {
			for (final GenieData genie : GenieData.values()) {
				GenieData.genie.put(genie.buttonId, genie);
			}
		}

	}

	/**
	 * Handles the clicking of interface
	 * 
	 * @param stoner
	 * @param buttonId
	 * @return
	 */
	public static boolean handle(Stoner stoner, int buttonId) {
	GenieData genie = GenieData.genie.get(buttonId);

	if (genie == null || stoner.getDelay().elapsed() < 1000 || !stoner.getBox().hasItemId(2528)) {
		return false;
	}

	if (stoner.getInterfaceManager().main != 2808) {
		stoner.send(new SendRemoveInterfaces());
		stoner.send(new SendMessage("That interface does not exist!"));
		return false;
	}

	stoner.getDelay().reset();
	stoner.send(new SendRemoveInterfaces());
	stoner.getBox().remove(new Item(2528));
	stoner.getProfession().addExperience(genie.getProfession(), 10_000);
	stoner.send(new SendMessage("You contacted JAH and got Experience in " + Advance.getProfessionName(genie.getProfession()) + "."));
	return true;
	}
}
