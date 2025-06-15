package com.bestbudz.core.discord.stonerbot.automations.banking;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotStonerConfig;
import com.bestbudz.rs2.entity.item.Item;

import java.util.logging.Logger;

/**
 * Manages banking operations for Discord Bot
 * Handles direct-to-bank functionality and inventory management
 */
public class DiscordBotBankingManager {
	private static final Logger logger = Logger.getLogger(DiscordBotBankingManager.class.getSimpleName());

	private final DiscordBotStoner bot;

	public DiscordBotBankingManager(DiscordBotStoner bot) {
		this.bot = bot;
	}

	/**
	 * Perform automatic banking - items go directly to bank, never inventory
	 */
	public void performAutoBanking() {
		if (!DiscordBotStonerConfig.AUTO_BANKING_ENABLED) {
			return;
		}

		try {
			int itemsSent = 0;

			// Send all non-essential items to bank
			for (int slot = 0; slot < bot.getBox().getItems().length; slot++) {
				Item item = bot.getBox().getItems()[slot];
				if (item != null && !DiscordBotStonerConfig.isEssentialItem(item.getId())) {
					// Remove from inventory and send to bank
					bot.getBox().remove(item);
					addItemToBank(item.getId(), item.getAmount());
					itemsSent++;

					if (itemsSent >= DiscordBotStonerConfig.MAX_ITEMS_PER_BANKING) {
						break;
					}
				}
			}

			if (itemsSent > 0) {
				bot.getActions().sendAutonomousStatusUpdate("Auto-banked " + itemsSent + " items");

				if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
					logger.info("Auto-banked " + itemsSent + " items");
				}
			}

		} catch (Exception e) {
			logger.warning("Error during auto-banking: " + e.getMessage());
		}
	}

	/**
	 * Add item to bank with Discord bot privileges - FIXED VERSION
	 */
	public void addItemToBank(int itemId, int amount) {
		try {
			if (DiscordBotStonerConfig.DIRECT_BANK_ACCESS) {
				// Create the item to bank
				Item itemToBank = new Item(itemId, amount);

				// Handle noted items - convert to unnoted for bank storage
				if (itemToBank.getDefinition().isNote()) {
					itemToBank.unNote();
				}

				// Check if this is a new item for tab placement
				boolean isNewItem = !bot.getBank().hasItemId(itemToBank.getId());

				// Add to bank using the Bank's add method
				int actuallyAdded = bot.getBank().add(itemToBank, false); // Don't auto-update

				if (actuallyAdded > 0) {
					// Handle tab placement for new items
					if (isNewItem) {
						// Add to current tab (default tab 0)
						int currentTab = bot.getBank().bankTab;
						bot.getBank().getTabAmounts()[currentTab]++;

						// Move item to the correct tab position
						int itemSlot = bot.getBank().getItemSlot(itemToBank.getId());
						if (itemSlot >= 0) {
							moveItemToTab(itemSlot, currentTab);
						}
					}

					// Force bank update to send packets
					bot.getBank().update();

					// Trigger persistence save
					bot.getBotPersistence().onBotBankChange();

					if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
						logger.info("Added item " + itemId + " (x" + actuallyAdded + ") directly to bank");
					}
				} else {
					logger.warning("Failed to add item " + itemId + " to bank - bank may be full");
				}
			}
		} catch (Exception e) {
			logger.warning("Error adding item to bank: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to move item to specific tab
	 */
	private void moveItemToTab(int itemSlot, int targetTab) {
		try {
			// Save current rearrange mode
			com.bestbudz.rs2.content.bank.Bank.RearrangeTypes originalMode = bot.getBank().rearrangeType;

			// Temporarily set to INSERT mode for proper tab placement
			bot.getBank().rearrangeType = com.bestbudz.rs2.content.bank.Bank.RearrangeTypes.INSERT;

			// Calculate target slot (end of the tab)
			int targetSlot = targetTab == 0 ?
				bot.getBank().getTabAmount(targetTab) - 1 :
				bot.getBank().getData(targetTab, 1);

			// Perform the swap/move
			bot.getBank().swap(targetSlot, itemSlot);

			// Restore original rearrange mode
			bot.getBank().rearrangeType = originalMode;

		} catch (Exception e) {
			logger.warning("Error moving item to tab: " + e.getMessage());
		}
	}

	/**
	 * Add items directly to bank (skip inventory completely)
	 */
	public void addItemDirectlyToBank(int itemId, int amount) {
		try {
			if (DiscordBotStonerConfig.SKIP_INVENTORY) {
				// All items go directly to bank
				addItemToBank(itemId, amount);

				if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
					logger.info("Item " + itemId + " (x" + amount + ") sent directly to bank");
				}
			}
		} catch (Exception e) {
			logger.warning("Error adding item directly to bank: " + e.getMessage());
		}
	}

	/**
	 * Check inventory space - Discord bot always has "space" due to direct banking
	 */
	public boolean hasInventorySpace(int requiredSlots) {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {
			// Discord bot sends everything to bank, so always has "space"
			return true;
		}

		// Fallback to normal inventory check using proper Box methods
		return getAvailableSlots() >= requiredSlots;
	}

	/**
	 * Get available inventory slots (always max for Discord bot)
	 */
	public int getAvailableSlots() {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {
			return Integer.MAX_VALUE; // Unlimited due to direct banking
		}

		// Calculate free slots by counting null items in the box
		int usedSlots = 0;
		for (Item item : bot.getBox().getItems()) {
			if (item != null) {
				usedSlots++;
			}
		}
		return 28 - usedSlots; // Box has 28 slots total
	}

	/**
	 * Clear inventory by banking all items
	 */
	public void clearInventory() {
		try {
			int itemsCleared = 0;

			for (int slot = 0; slot < bot.getBox().getItems().length; slot++) {
				Item item = bot.getBox().getItems()[slot];
				if (item != null) {
					// Bank the item first
					addItemToBank(item.getId(), item.getAmount());
					// Then remove from inventory
					bot.getBox().remove(item);
					itemsCleared++;
				}
			}

			if (itemsCleared > 0) {
				logger.info("Cleared " + itemsCleared + " items from inventory to bank");
			}

		} catch (Exception e) {
			logger.warning("Error clearing inventory: " + e.getMessage());
		}
	}

	/**
	 * Check if banking is currently needed
	 */
	public boolean needsBanking() {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {
			return false; // Never needs banking due to direct bank access
		}

		// Check if inventory is getting full (less than 3 slots available)
		return getAvailableSlots() <= 3;
	}

	/**
	 * Get banking status for display
	 */
	public String getBankingStatus() {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {
			return "Direct Banking Active - Items: " + getBankItemCount();
		}

		int freeSlots = getAvailableSlots();
		return "Inventory: " + (28 - freeSlots) + "/28 slots used, Bank: " + getBankItemCount() + " items";
	}

	/**
	 * Get count of items in bank
	 */
	private int getBankItemCount() {
		int count = 0;
		for (Item item : bot.getBank().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Force banking operation
	 */
	public void forceBanking() {
		performAutoBanking();
	}
}