package com.bestbudz.core.discord.stonerbot.automations.banking;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotStonerConfig;
import com.bestbudz.rs2.entity.item.Item;

import java.util.logging.Logger;

public class DiscordBotBankingManager {
	private static final Logger logger = Logger.getLogger(DiscordBotBankingManager.class.getSimpleName());

	private final DiscordBotStoner bot;

	public DiscordBotBankingManager(DiscordBotStoner bot) {
		this.bot = bot;
	}

	public void performAutoBanking() {
		if (!DiscordBotStonerConfig.AUTO_BANKING_ENABLED) {
			return;
		}

		try {
			int itemsSent = 0;

			for (int slot = 0; slot < bot.getBox().getItems().length; slot++) {
				Item item = bot.getBox().getItems()[slot];
				if (item != null && !DiscordBotStonerConfig.isEssentialItem(item.getId())) {

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

	public void addItemToBank(int itemId, int amount) {
		try {
			if (DiscordBotStonerConfig.DIRECT_BANK_ACCESS) {

				Item itemToBank = new Item(itemId, amount);

				if (itemToBank.getDefinition().isNote()) {
					itemToBank.unNote();
				}

				boolean isNewItem = !bot.getBank().hasItemId(itemToBank.getId());

				int actuallyAdded = bot.getBank().add(itemToBank, false);

				if (actuallyAdded > 0) {

					if (isNewItem) {

						int currentTab = bot.getBank().bankTab;
						bot.getBank().getTabAmounts()[currentTab]++;

						int itemSlot = bot.getBank().getItemSlot(itemToBank.getId());
						if (itemSlot >= 0) {
							moveItemToTab(itemSlot, currentTab);
						}
					}

					bot.getBank().update();

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

	private void moveItemToTab(int itemSlot, int targetTab) {
		try {

			com.bestbudz.rs2.content.bank.Bank.RearrangeTypes originalMode = bot.getBank().rearrangeType;

			bot.getBank().rearrangeType = com.bestbudz.rs2.content.bank.Bank.RearrangeTypes.INSERT;

			int targetSlot = targetTab == 0 ?
				bot.getBank().getTabAmount(targetTab) - 1 :
				bot.getBank().getData(targetTab, 1);

			bot.getBank().swap(targetSlot, itemSlot);

			bot.getBank().rearrangeType = originalMode;

		} catch (Exception e) {
			logger.warning("Error moving item to tab: " + e.getMessage());
		}
	}

	public void addItemDirectlyToBank(int itemId, int amount) {
		try {
			if (DiscordBotStonerConfig.SKIP_INVENTORY) {

				addItemToBank(itemId, amount);

				if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
					logger.info("Item " + itemId + " (x" + amount + ") sent directly to bank");
				}
			}
		} catch (Exception e) {
			logger.warning("Error adding item directly to bank: " + e.getMessage());
		}
	}

	public boolean hasInventorySpace(int requiredSlots) {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {

			return true;
		}

		return getAvailableSlots() >= requiredSlots;
	}

	public int getAvailableSlots() {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {
			return Integer.MAX_VALUE;
		}

		int usedSlots = 0;
		for (Item item : bot.getBox().getItems()) {
			if (item != null) {
				usedSlots++;
			}
		}
		return 28 - usedSlots;
	}

	public void clearInventory() {
		try {
			int itemsCleared = 0;

			for (int slot = 0; slot < bot.getBox().getItems().length; slot++) {
				Item item = bot.getBox().getItems()[slot];
				if (item != null) {

					addItemToBank(item.getId(), item.getAmount());

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

	public boolean needsBanking() {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {
			return false;
		}

		return getAvailableSlots() <= 3;
	}

	public String getBankingStatus() {
		if (DiscordBotStonerConfig.SKIP_INVENTORY) {
			return "Direct Banking Active - Items: " + getBankItemCount();
		}

		int freeSlots = getAvailableSlots();
		return "Inventory: " + (28 - freeSlots) + "/28 slots used, Bank: " + getBankItemCount() + " items";
	}

	private int getBankItemCount() {
		int count = 0;
		for (Item item : bot.getBank().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	public void forceBanking() {
		performAutoBanking();
	}
}
