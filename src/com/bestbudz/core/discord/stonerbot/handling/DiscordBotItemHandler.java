package com.bestbudz.core.discord.stonerbot.handling;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

import java.util.logging.Logger;

public class DiscordBotItemHandler {

	private static final Logger logger = Logger.getLogger(DiscordBotItemHandler.class.getSimpleName());

	private final Stoner discordBot;

	public DiscordBotItemHandler(Stoner discordBot) {
		this.discordBot = discordBot;
	}

	public void handleItemUsedOnBot(Stoner player, int itemId, int itemSlot) {
		try {

			Item playerItem = player.getBox().getItems()[itemSlot];
			if (playerItem == null || playerItem.getId() != itemId) {
				return;
			}

			if (playerItem.getEquipmentDefinition() == null) {
				player.send(new SendMessage("That item cannot be equipped on " + discordBot.getUsername() + "."));
				return;
			}

			int targetSlot = playerItem.getEquipmentDefinition().getSlot();

			Item currentEquippedItem = discordBot.getEquipment().getItems()[targetSlot];
			if (currentEquippedItem != null) {
				try {
					discordBot.getEquipment().unequip(targetSlot);
				} catch (Exception e) {

				}
			}

			Item botItem = new Item(playerItem.getId(), 1);

			if (!discordBot.getBox().hasSpaceFor(botItem)) {
				makeSpaceInBotInventory();
			}

			discordBot.getBox().add(botItem);

			int botInventorySlot = findItemInBotInventory(itemId);
			if (botInventorySlot == -1) {
				player.send(new SendMessage("Failed to add item to " + discordBot.getUsername() + "'s inventory."));
				return;
			}

			Item actualBotItem = discordBot.getBox().getItems()[botInventorySlot];
			if (actualBotItem == null) {
				player.send(new SendMessage("Item disappeared from " + discordBot.getUsername() + "'s inventory."));
				return;
			}

			if (!discordBot.getEquipment().canEquip(actualBotItem, targetSlot)) {
				player.send(new SendMessage(discordBot.getUsername() + " cannot equip that item."));
				return;
			}

			try {
				discordBot.getEquipment().equip(actualBotItem, botInventorySlot);

				if (discordBot.getEquipment().getItems()[targetSlot] != null &&
					discordBot.getEquipment().getItems()[targetSlot].getId() == itemId) {

					if (playerItem.getAmount() > 1) {
						playerItem.setAmount(playerItem.getAmount() - 1);
					} else {
						player.getBox().clear(itemSlot);
					}
					player.getBox().update();

					String itemName = actualBotItem.getDefinition().getName();
					if (currentEquippedItem != null) {
						String oldItemName = currentEquippedItem.getDefinition().getName();
						player.send(new SendMessage("You gave " + itemName + " to " + discordBot.getUsername() +
							". They unequipped " + oldItemName + " and equipped the " + itemName + "!"));
					} else {
						player.send(new SendMessage("You gave " + itemName + " to " + discordBot.getUsername() +
							" and they equipped it!"));
					}

					if (discordBot instanceof DiscordBotStoner) {
						((DiscordBotStoner) discordBot).getBotPersistence().onBotEquipmentChange();
					}

					logger.info("Player " + player.getUsername() + " gave item " + itemId +
						" to Discord bot, equipped to slot " + targetSlot);

				} else {

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

	private void makeSpaceInBotInventory() {

		if (discordBot instanceof DiscordBotStoner) {
			DiscordBotStoner bot = (DiscordBotStoner) discordBot;
			bot.performAutoBanking();

			if (bot.getBox().getFreeSlots() > 0) {
				return;
			}
		}

		Item[] items = discordBot.getBox().getItems();

		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && !isItemEquipped(items[i].getId())) {
				discordBot.getBox().clear(i);
				return;
			}
		}

		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				discordBot.getBox().clear(i);
				return;
			}
		}
	}

	private int findItemInBotInventory(int itemId) {
		Item[] items = discordBot.getBox().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].getId() == itemId) {
				return i;
			}
		}
		return -1;
	}

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
