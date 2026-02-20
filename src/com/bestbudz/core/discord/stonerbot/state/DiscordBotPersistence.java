package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.content.io.sqlite.SaveWorker;
import com.bestbudz.rs2.content.io.sqlite.StonerSave;

import java.util.logging.Logger;

public class DiscordBotPersistence {

	private static final Logger logger = Logger.getLogger(DiscordBotPersistence.class.getSimpleName());

	private final DiscordBotStoner discordBot;
	private boolean saveEnabled = true;

	private long lastSaveTime = 0;
	private static final long MIN_SAVE_INTERVAL = 5000;

	public DiscordBotPersistence(DiscordBotStoner discordBot) {
		this.discordBot = discordBot;
	}

	public void loadBotState() {
		try {
			logger.info("Loading Discord bot state from database...");

			boolean loaded = StonerSave.load(discordBot);

			if (loaded) {
				logger.info("Discord bot state loaded successfully from database");

				if (!com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket.validate(discordBot)) {
					logger.warning("Discord bot loaded appearance invalid, resetting to defaults");
					discordBot.getBotAppearance().setupMinimalAppearance();
				}

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

	public void saveBotState() {
		if (!saveEnabled) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastSaveTime < MIN_SAVE_INTERVAL) {
			return;
		}

		try {

			SaveWorker.enqueueSave(discordBot);
			lastSaveTime = currentTime;
			logger.info("Discord bot state queued for save");

		} catch (Exception e) {
			logger.warning("Error saving Discord bot state: " + e.getMessage());
		}
	}

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

	public void onBotInventoryChange() {
		saveBotState();
	}

	public void onBotEquipmentChange() {
		saveBotState();
	}

	public void onBotAppearanceChange() {
		saveBotState();
	}

	public void onBotLocationChange() {
		saveBotState();
	}

	public void onBotGradesChange() {
		saveBotState();
	}

	public void onBotBankChange() {
		saveBotState();
	}

	public void onBotAdvancesChange() {
		saveBotState();
	}

	public void onBotSettingsChange() {
		saveBotState();
	}

	public void onBotStateChange() {
		saveBotState();
	}

	private void setupDefaultBotState() {

		discordBot.getBotAppearance().setupMinimalAppearance();

		discordBot.getBotGrades().setupDefaultGrades();

		discordBot.getBotLocation().setInitialLocation();

		giveDefaultStartingItems();

		saveBotState();

		logger.info("Discord bot setup with default state");
	}

	private void giveDefaultStartingItems() {
		try {

		} catch (Exception e) {
			logger.warning("Error giving bot starting items: " + e.getMessage());
		}
	}

	private void validateLoadedGrades() {
		try {
			boolean needsUpdate = false;

			for (int i = 0; i < discordBot.getGrades().length; i++) {
				long minGrade = (i == 3) ? 10 : 1;

				if (discordBot.getGrades()[i] < minGrade) {
					discordBot.getGrades()[i] = minGrade;

					if (discordBot.getMaxGrades()[i] < minGrade) {
						discordBot.getMaxGrades()[i] = minGrade;
					}
					needsUpdate = true;
				}

				if (discordBot.getMaxGrades()[i] < discordBot.getGrades()[i]) {
					discordBot.getMaxGrades()[i] = discordBot.getGrades()[i];
					needsUpdate = true;
				}
			}

			if (needsUpdate) {
				discordBot.getBotGrades().updateAllProfessions();
				logger.info("Discord bot grades validated and updated");
			}

		} catch (Exception e) {
			logger.warning("Error validating loaded grades: " + e.getMessage());
		}
	}

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

	private int getInventoryItemCount() {
		int count = 0;
		for (com.bestbudz.rs2.entity.item.Item item : discordBot.getBox().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	private int getBankItemCount() {
		int count = 0;
		for (com.bestbudz.rs2.entity.item.Item item : discordBot.getBank().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	private int getEquippedItemCount() {
		int count = 0;
		for (com.bestbudz.rs2.entity.item.Item item : discordBot.getEquipment().getItems()) {
			if (item != null && item.getId() > 0 && item.getAmount() > 0) {
				count++;
			}
		}
		return count;
	}

	public DiscordBotAppearance getBotAppearance() {
		return discordBot.getBotAppearance();
	}

	public DiscordBotGrades getBotGrades() {
		return discordBot.getBotGrades();
	}

	public DiscordBotLocation getBotLocation() {
		return discordBot.getBotLocation();
	}
}
