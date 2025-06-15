package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.content.io.sqlite.SaveWorker;
import com.bestbudz.rs2.content.io.sqlite.StonerSave;

import java.util.logging.Logger;

/**
 * Handles Discord bot persistence - saving and loading bot state
 */
public class DiscordBotPersistence {

	private static final Logger logger = Logger.getLogger(DiscordBotPersistence.class.getSimpleName());

	private final DiscordBotStoner discordBot;
	private boolean saveEnabled = true;

	private long lastSaveTime = 0;
	private static final long MIN_SAVE_INTERVAL = 5000;

	public DiscordBotPersistence(DiscordBotStoner discordBot) {
		this.discordBot = discordBot;
	}

	/**
	 * Load the Discord bot's saved state from database
	 */
	public void loadBotState() {
		try {
			logger.info("Loading Discord bot state from database...");

			// Try to load existing bot data
			boolean loaded = StonerSave.load(discordBot);

			if (loaded) {
				logger.info("Discord bot state loaded successfully from database");

				// Validate loaded appearance
				if (!com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket.validate(discordBot)) {
					logger.warning("Discord bot loaded appearance invalid, resetting to defaults");
					discordBot.getBotAppearance().setupMinimalAppearance();
				}

				// Ensure grades are reasonable (not below minimums)
				validateLoadedGrades();

			} else {
				logger.info("No existing Discord bot data found, using defaults");
				setupDefaultBotState();
			}

		} catch (Exception e) {
			logger.warning("Error loading Discord bot state, using defaults: " + e.getMessage());
			setupDefaultBotState();
		}
	}

	/**
	 * Save the Discord bot's current state to database
	 */
	public void saveBotState() {
		if (!saveEnabled) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastSaveTime < MIN_SAVE_INTERVAL) {
			return; // Skip save if too recent
		}


		try {
			// Mark as dirty for immediate save
	//		SaveCache.markDirty(discordBot);

			// Queue for background save
			SaveWorker.enqueueSave(discordBot);
			lastSaveTime = currentTime;
			logger.info("Discord bot state queued for save");

		} catch (Exception e) {
			logger.warning("Error saving Discord bot state: " + e.getMessage());
		}
	}

	/**
	 * Force immediate save of bot state (synchronous)
	 */
	public void forceSaveBotState() {
		if (!saveEnabled) {
			return;
		}

		try {
			StonerSave.save(discordBot);
			logger.info("Discord bot state saved immediately");

		} catch (Exception e) {
			logger.warning("Error force saving Discord bot state: " + e.getMessage());
		}
	}

	/**
	 * Called when bot receives/equips items to trigger save
	 */
	public void onBotInventoryChange() {
		saveBotState();
	}

	/**
	 * Called when bot's equipment changes to trigger save
	 */
	public void onBotEquipmentChange() {
		saveBotState();
	}

	/**
	 * Called when bot's appearance changes to trigger save
	 */
	public void onBotAppearanceChange() {
		saveBotState();
	}

	/**
	 * Called when bot's location changes to trigger save
	 */
	public void onBotLocationChange() {
		saveBotState();
	}

	/**
	 * Called when bot's grades/experience changes to trigger save
	 */
	public void onBotGradesChange() {
		saveBotState();
	}

	/**
	 * Called when bot's bank changes to trigger save
	 */
	public void onBotBankChange() {
		saveBotState();
	}

	/**
	 * Called when bot's profession advances change to trigger save
	 */
	public void onBotAdvancesChange() {
		saveBotState();
	}

	/**
	 * Called when bot's settings change to trigger save
	 */
	public void onBotSettingsChange() {
		saveBotState();
	}

	/**
	 * Called when any bot state changes to trigger save
	 */
	public void onBotStateChange() {
		saveBotState();
	}

	/**
	 * Setup default bot state for first time
	 */
	private void setupDefaultBotState() {
		// Set default appearance
		discordBot.getBotAppearance().setupMinimalAppearance();

		// Set default grades
		discordBot.getBotGrades().setupDefaultGrades();

		// Set default location
		discordBot.getBotLocation().setInitialLocation();

		// Give bot some starting items (optional)
		giveDefaultStartingItems();

		// Save the new defaults
		saveBotState();

		logger.info("Discord bot setup with default state");
	}

	/**
	 * Give the bot some default starting items (optional)
	 */
	private void giveDefaultStartingItems() {
		try {
			// Give bot some basic items to start with
			// You can customize this based on what you want the bot to have

			// Example: Give bot some basic gear
			// discordBot.getBox().add(new com.bestbudz.rs2.entity.item.Item(995, 1000000)); // 1M coins
			// discordBot.getBox().add(new com.bestbudz.rs2.entity.item.Item(4151, 1)); // Whip
			// discordBot.getBox().add(new com.bestbudz.rs2.entity.item.Item(1163, 1)); // Rune full helm

		} catch (Exception e) {
			logger.warning("Error giving bot starting items: " + e.getMessage());
		}
	}

	/**
	 * Validate loaded grades meet minimum requirements
	 */
	/**
	 * Validate loaded grades meet minimum requirements
	 */
	private void validateLoadedGrades() {
		try {
			boolean needsUpdate = false;

			// Ensure all grades are at least 1 (except Life which should be at least 10)
			for (int i = 0; i < discordBot.getGrades().length; i++) {
				long minGrade = (i == 3) ? 10 : 1; // Life skill minimum 10, others minimum 1

				if (discordBot.getGrades()[i] < minGrade) {
					discordBot.getGrades()[i] = minGrade;
					// Only update maxGrades if it's also below minimum
					if (discordBot.getMaxGrades()[i] < minGrade) {
						discordBot.getMaxGrades()[i] = minGrade;
					}
					needsUpdate = true;
				}

				// Ensure maxGrades is never less than current grades
				if (discordBot.getMaxGrades()[i] < discordBot.getGrades()[i]) {
					discordBot.getMaxGrades()[i] = discordBot.getGrades()[i];
					needsUpdate = true;
				}
			}

			// Update profession displays if we changed anything
			if (needsUpdate) {
				discordBot.getBotGrades().updateAllProfessions();
				logger.info("Discord bot grades validated and updated");
			}

		} catch (Exception e) {
			logger.warning("Error validating loaded grades: " + e.getMessage());
		}
	}

	/**
	 * Get save statistics
	 */
	public String getSaveStatus() {
		StringBuilder status = new StringBuilder();
		status.append("Discord Bot Save Status:\n");
		status.append("Save Enabled: ").append(saveEnabled).append("\n");
		status.append("Username: ").append(discordBot.getUsername()).append("\n");
		status.append("Items in Inventory: ").append(getInventoryItemCount()).append("\n");
		status.append("Items Equipped: ").append(getEquippedItemCount()).append("\n");
		status.append("Items in Bank: ").append(getBankItemCount()).append("\n");
		status.append("Current Location: ").append(discordBot.getLocation().toString()).append("\n");
		status.append("Combat Level: ").append(discordBot.getProfession().calcCombatGrade()).append("\n");
		status.append("Total Experience: ").append(discordBot.getProfession().getTotalExperience()).append("\n");
		status.append("Total Advances: ").append(discordBot.getTotalAdvances()).append("\n");
		status.append("Appearance Valid: ").append(com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket.validate(discordBot)).append("\n");

		return status.toString();
	}

	/**
	 * Count items in bot's inventory
	 */
	private int getInventoryItemCount() {
		int count = 0;
		for (com.bestbudz.rs2.entity.item.Item item : discordBot.getBox().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Count items in bot's bank
	 */
	private int getBankItemCount() {
		int count = 0;
		for (com.bestbudz.rs2.entity.item.Item item : discordBot.getBank().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Count items equipped by bot
	 */
	private int getEquippedItemCount() {
		int count = 0;
		for (com.bestbudz.rs2.entity.item.Item item : discordBot.getEquipment().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Get reference to bot appearance handler
	 */
	public DiscordBotAppearance getBotAppearance() {
		return discordBot.getBotAppearance();
	}

	/**
	 * Get reference to bot grades handler
	 */
	public DiscordBotGrades getBotGrades() {
		return discordBot.getBotGrades();
	}

	/**
	 * Get reference to bot location handler
	 */
	public DiscordBotLocation getBotLocation() {
		return discordBot.getBotLocation();
	}
}