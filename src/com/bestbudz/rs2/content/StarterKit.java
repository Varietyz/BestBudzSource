package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.io.sqlite.SaveCache;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;

public class StarterKit {

	/**
	 * Automatically gives starter items to new players without any interface or dialog
	 */
	public static void giveStarterItems(Stoner stoner) {
		if (!Boolean.TRUE.equals(stoner.getAttributes().get("starter_ip_logged"))) {

			// Get STONER starter kit items (202051)
			StarterData stonerKit = StarterData.STONER;

			// Add items directly to inventory
			for (Item item : stonerKit.getItems()) {
				stoner.getBox().insert(item);
			}

			// Get DEALER starter kit items (202052) and add to bank
			StarterData dealerKit = StarterData.DEALER;
			for (Item item : dealerKit.getItems()) {
				stoner.getBank().depositFromNoting(item, 0, false);
			}

			// Get GROWER starter kit items (202053) and add to bank
			StarterData growerKit = StarterData.GROWER;
			for (Item item : growerKit.getItems()) {
				stoner.getBank().depositFromNoting(item, 0, false);
			}

			// Update the bank display after adding all items
			stoner.getBank().update();

			// Mark as completed
			stoner.getAttributes().set("starter_ip_logged", true);
			SaveCache.markDirty(stoner);

			// Set up player
			setupNewPlayer(stoner);

			// Send confirmation message
			stoner.send(new SendMessage("@gre@Welcome to Best Budz! Your starter items have been added."));
			stoner.send(new SendMessage("Check your inventory and bank for all your starter gear!"));
		}
	}

	/**
	 * Sets up the new player with proper interfaces and settings
	 */
	private static void setupNewPlayer(Stoner stoner) {
		// Set default controller
		stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
		stoner.setStarter(false);

		// Set up sidebar interfaces
		for (int i = 0; i < StonerConstants.SIDEBAR_INTERFACE_IDS.length; i++) {
			stoner.send(new SendSidebarInterface(i, StonerConstants.SIDEBAR_INTERFACE_IDS[i]));
		}

		stoner.send(new SendSidebarInterface(5, 5608));
		stoner.send(new SendSidebarInterface(6, 1151));

		// Set default rights (regular player)
		stoner.setRights(0);
		stoner.getUpdateFlags().setUpdateRequired(true);
	}

	// Keep the enum for item definitions but remove button handling
	public enum StarterData {
		STONER(
			202051,
			51766,
			0,
			new Item[] {
				new Item(995, 5000000),    // Coins
				new Item(7945, 100),       // Prayer potions
				new Item(11879, 1),        // Equipment items
				new Item(11881, 1),
				new Item(11883, 1),
				new Item(12009, 1),
				new Item(11885, 1),
				new Item(12859, 1),
				new Item(11738, 1),
				new Item(1511, 100),       // Logs
				new Item(1521, 100),
				new Item(1519, 100),
				new Item(1517, 100),
				new Item(1515, 100),
				new Item(1513, 100),
				new Item(6199, 1),         // Tools
				new Item(6857, 1),
				new Item(775, 1),
				new Item(1837, 1),
				new Item(13223, 1),
				new Item(6575, 1),
				new Item(6577, 1),
				new Item(12647, 1)
			}),

		DEALER(
			202052,
			51767,
			11,
			new Item[] {
				new Item(995, 5000000),    // Coins
				new Item(7947, 100),       // Super restores
				new Item(2437, 10),        // Prayer potions
				new Item(2441, 10),
				new Item(2443, 10),
				new Item(3025, 10),
				new Item(12414, 1),        // Equipment
				new Item(12415, 1),
				new Item(12416, 1),
				new Item(12417, 1),
				new Item(12418, 1),
				new Item(4587, 1),
				new Item(6585, 1),
				new Item(7461, 1),
				new Item(11840, 1),
				new Item(2675, 1),
				new Item(2673, 1),
				new Item(2671, 1),
				new Item(2669, 1),
				new Item(13223, 1),
				new Item(12654, 1)
			}),

		GROWER(
			202053,
			51768,
			12,
			new Item[] {
				new Item(995, 5000000),    // Coins
				new Item(7947, 100),       // Super restores
				new Item(2445, 10),        // Potions
				new Item(3041, 10),
				new Item(3025, 10),
				new Item(892, 500),        // Arrows
				new Item(4675, 1),         // Equipment
				new Item(2579, 1),
				new Item(2577, 1),
				new Item(4214, 1),
				new Item(861, 1),
				new Item(10376, 1),
				new Item(10380, 1),
				new Item(6585, 1),
				new Item(13223, 1),
				new Item(12648, 1)
			});

		private final int button;
		private final int stringId;
		private final int rights;
		private final Item[] items;

		StarterData(int button, int stringId, int rights, Item[] items) {
			this.button = button;
			this.stringId = stringId;
			this.rights = rights;
			this.items = items;
		}

		public int getButton() {
			return button;
		}

		public int getString() {
			return stringId;
		}

		public int getRights() {
			return rights;
		}

		public Item[] getItems() {
			return items;
		}
	}
}