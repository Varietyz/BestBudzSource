package com.bestbudz.rs2.content.bank;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * DockBox - Minimal data provider for inventory display in dock panel
 *
 * Simple utility class that provides inventory data to the client for:
 * - Grid highlighting (inventory items vs bank items)
 * - Right-click context menus on inventory items
 * - Unified grid display (inventory + bank)
 *
 * All actual banking operations are handled by DockBank.
 */
public class DockBox {

	/**
	 * Get inventory items for grid display
	 * Returns array where non-null items are in inventory
	 */
	public static Item[] getInventoryItems(Stoner stoner) {
		if (stoner == null || stoner.getBox() == null) {
			return new Item[28]; // Empty inventory
		}
		return stoner.getBox().getItems();
	}

	/**
	 * Get count of non-null inventory items
	 * Used by client to determine grid highlighting boundaries
	 */
	public static int getInventoryItemCount(Stoner stoner) {
		if (stoner == null || stoner.getBox() == null) {
			return 0;
		}

		int count = 0;
		Item[] items = stoner.getBox().getItems();
		for (Item item : items) {
			if (item != null) count++;
		}
		return count;
	}

	/**
	 * Get combined grid data for client display
	 * Returns: [inventory_items...] + [bank_items...]
	 *
	 * Client uses this to:
	 * - Highlight first X items as "inventory" (different color/border)
	 * - Show remaining items as "bank" items
	 * - Display faded placeholders for empty bank slots
	 */
	public static GridData getCombinedGridData(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return new GridData(new Item[0], 0);
		}

		Item[] inventoryItems = getInventoryItems(bank.stoner);
		Item[] bankItems = bank.getItems();

		// Count non-null items
		int invCount = 0;
		for (Item item : inventoryItems) {
			if (item != null) invCount++;
		}

		int bankCount = 0;
		for (Item item : bankItems) {
			if (item != null) bankCount++;
		}

		// Create combined array: inventory first, then bank
		Item[] combined = new Item[invCount + bankCount];
		int index = 0;

		// Add inventory items first
		for (Item item : inventoryItems) {
			if (item != null) {
				combined[index++] = item;
			}
		}

		// Add bank items (excluding duplicates in inventory)
		for (Item item : bankItems) {
			if (item != null && !isItemInInventory(bank.stoner, item.getId())) {
				combined[index++] = item;
			}
		}

		return new GridData(combined, invCount);
	}

	/**
	 * Check if item exists in inventory
	 */
	private static boolean isItemInInventory(Stoner stoner, int itemId) {
		Item[] inventoryItems = getInventoryItems(stoner);
		for (Item item : inventoryItems) {
			if (item != null && item.getId() == itemId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if grid index represents an inventory item
	 * Used by client for highlighting and context menus
	 */
	public static boolean isInventorySlot(Bank bank, int gridIndex) {
		GridData data = getCombinedGridData(bank);
		return gridIndex < data.inventoryCount;
	}

	/**
	 * Get item at grid index
	 */
	public static Item getItemAtGridIndex(Bank bank, int gridIndex) {
		GridData data = getCombinedGridData(bank);
		if (gridIndex >= 0 && gridIndex < data.items.length) {
			return data.items[gridIndex];
		}
		return null;
	}

	/**
	 * Data class for grid information
	 */
	public static class GridData {
		public final Item[] items;          // Combined inventory + bank items
		public final int inventoryCount;    // Number of inventory items at start of array

		public GridData(Item[] items, int inventoryCount) {
			this.items = items;
			this.inventoryCount = inventoryCount;
		}

		public boolean isInventoryIndex(int index) {
			return index < inventoryCount;
		}

		public boolean isBankIndex(int index) {
			return index >= inventoryCount && index < items.length;
		}
	}
}