package com.bestbudz.core.discord.stonerbot.handling;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

import java.util.logging.Logger;

/**
 * ENHANCED: Handles items used on the Discord bot with equipment swapping support
 * Now supports replacing existing equipment and auto-combat gear management
 */
public class DiscordBotItemHandler {

	private static final Logger logger = Logger.getLogger(DiscordBotItemHandler.class.getSimpleName());

	private final Stoner discordBot;

	public DiscordBotItemHandler(Stoner discordBot) {
		this.discordBot = discordBot;
	}

	/**
	 * ENHANCED: Handle when a player uses an item on the Discord bot with equipment swapping
	 * @param player The player using the item
	 * @param itemId The item ID being used
	 * @param itemSlot The slot in the player's inventory
	 */
	public void handleItemUsedOnBot(Stoner player, int itemId, int itemSlot) {
		try {
			// Get the item from the player's inventory
			Item playerItem = player.getBox().getItems()[itemSlot];
			if (playerItem == null || playerItem.getId() != itemId) {
				return; // Item doesn't exist or mismatch
			}

			// Check if the item is equippable
			if (playerItem.getEquipmentDefinition() == null) {
				player.send(new SendMessage("That item cannot be equipped on " + discordBot.getUsername() + "."));
				return;
			}

			// Get the target equipment slot
			int targetSlot = playerItem.getEquipmentDefinition().getSlot();

			// SIMPLE: If bot has something equipped in that slot, unequip it first
			Item currentEquippedItem = discordBot.getEquipment().getItems()[targetSlot];
			if (currentEquippedItem != null) {
				try {
					discordBot.getEquipment().unequip(targetSlot);
				} catch (Exception e) {
					// Unequip failed, but continue
				}
			}

			// Create a copy of the item for the bot
			Item botItem = new Item(playerItem.getId(), 1);

			// IMPORTANT: Make sure the bot has inventory space
			if (!discordBot.getBox().hasSpaceFor(botItem)) {
				makeSpaceInBotInventory();
			}

			// Add the new item to the bot's inventory
			discordBot.getBox().add(botItem);

			// Find where the item ended up in the bot's inventory
			int botInventorySlot = findItemInBotInventory(itemId);
			if (botInventorySlot == -1) {
				player.send(new SendMessage("Failed to add item to " + discordBot.getUsername() + "'s inventory."));
				return;
			}

			// Get the actual item from the bot's inventory
			Item actualBotItem = discordBot.getBox().getItems()[botInventorySlot];
			if (actualBotItem == null) {
				player.send(new SendMessage("Item disappeared from " + discordBot.getUsername() + "'s inventory."));
				return;
			}

			// Check if the bot can equip this item
			if (!discordBot.getEquipment().canEquip(actualBotItem, targetSlot)) {
				player.send(new SendMessage(discordBot.getUsername() + " cannot equip that item."));
				return;
			}

			// Try to equip the item on the bot
			try {
				discordBot.getEquipment().equip(actualBotItem, botInventorySlot);

				// Verify it was equipped
				if (discordBot.getEquipment().getItems()[targetSlot] != null &&
					discordBot.getEquipment().getItems()[targetSlot].getId() == itemId) {

					// SUCCESS - Remove item from player and send messages
					if (playerItem.getAmount() > 1) {
						playerItem.setAmount(playerItem.getAmount() - 1);
					} else {
						player.getBox().clear(itemSlot);
					}
					player.getBox().update();

					// Send success message
					String itemName = actualBotItem.getDefinition().getName();
					if (currentEquippedItem != null) {
						String oldItemName = currentEquippedItem.getDefinition().getName();
						player.send(new SendMessage("You gave " + itemName + " to " + discordBot.getUsername() +
							". They unequipped " + oldItemName + " and equipped the " + itemName + "!"));
					} else {
						player.send(new SendMessage("You gave " + itemName + " to " + discordBot.getUsername() +
							" and they equipped it!"));
					}

					// IMPORTANT: Save the bot's new equipment state
					if (discordBot instanceof DiscordBotStoner) {
						((DiscordBotStoner) discordBot).getBotPersistence().onBotEquipmentChange();
					}

					logger.info("Player " + player.getUsername() + " gave item " + itemId +
						" to Discord bot, equipped to slot " + targetSlot);

				} else {
					// Equipment failed for some reason
					player.send(new SendMessage("Failed to equip " + actualBotItem.getDefinition().getName() +
						" on " + discordBot.getUsername() + "."));
					logger.warning("Equipment failed - item not found in target slot " + targetSlot);
				}

			} catch (Exception equipException) {
				player.send(new SendMessage("Error equipping item on " + discordBot.getUsername() +
					": " + equipException.getMessage()));
				logger.warning("Equipment exception: " + equipException.getMessage());
			}

		} catch (Exception e) {
			logger.warning("Error handling item used on Discord bot: " + e.getMessage());
			e.printStackTrace();
			player.send(new SendMessage("Something went wrong while giving the item to " +
				discordBot.getUsername() + "."));
		}
	}

	/**
	 * Make space in the bot's inventory by removing a random item
	 */
	private void makeSpaceInBotInventory() {
		// Discord bot privilege: Auto-bank items first
		if (discordBot instanceof DiscordBotStoner) {
			DiscordBotStoner bot = (DiscordBotStoner) discordBot;
			bot.performAutoBanking();

			// If auto-banking created space, we're done
			if (bot.getBox().getFreeSlots() > 0) {
				return;
			}
		}

		Item[] items = discordBot.getBox().getItems();

		// Find a non-equipped item to remove
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && !isItemEquipped(items[i].getId())) {
				discordBot.getBox().clear(i);
				return;
			}
		}

		// If all items are equipped, remove the first item
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				discordBot.getBox().clear(i);
				return;
			}
		}
	}

	/**
	 * Find an item in the bot's inventory
	 */
	private int findItemInBotInventory(int itemId) {
		Item[] items = discordBot.getBox().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].getId() == itemId) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Check if an item is currently equipped on the bot
	 */
	private boolean isItemEquipped(int itemId) {
		Item[] equipment = discordBot.getEquipment().getItems();
		for (Item item : equipment) {
			if (item != null && item.getId() == itemId) {
				return true;
			}
		}
		return false;
	}
}
