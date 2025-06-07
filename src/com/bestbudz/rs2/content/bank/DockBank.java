package com.bestbudz.rs2.content.bank;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.content.io.sqlite.SaveCache;

/**
 * DockBank - Standalone utility class for dock panel banking operations
 *
 * Provides interface-free banking functionality that bypasses all interface checks.
 * All methods are self-contained and don't rely on Bank's interface-dependent methods.
 */
public class DockBank {

	// Dock button ID range starts at 115247
	public static final int DOCK_BUTTON_START = 115247;
	public static final int DOCK_BUTTON_END = 116050; // Reserve 50+ buttons for future use

	// Specific dock button IDs
	public static final int DOCK_DEPOSIT_ALL_INVENTORY = 115247;
	public static final int DOCK_DEPOSIT_ALL_EQUIPMENT = 115248;
	public static final int DOCK_TOGGLE_WITHDRAW_MODE = 115249;
	public static final int DOCK_TOGGLE_REARRANGE_MODE = 115250;
	public static final int DOCK_TOGGLE_SEARCH = 115251;

	public static final int DOCK_GRID_BASE = 115280; // Grid left-click base
	public static final int DOCK_GRID_END = 115379;  // 100 grid slots (10x10 or similar)

	public static final int DOCK_GRID_DEPOSIT_BASE = 115380; // Grid right-click base
	public static final int DOCK_GRID_DEPOSIT_END = 115479;  // 100 grid slots for deposit

	// User-configurable amount buttons (replace existing fixed amounts)
	public static final int DOCK_SET_WITHDRAW_AMOUNT = 115252; // Repurpose existing
	public static final int DOCK_SET_DEPOSIT_AMOUNT = 115253;  // Repurpose existing

	public static final int DOCK_WITHDRAW_ITEM = 116044;
	public static final int DOCK_DEPOSIT_ITEM = 116045;

	public static final int DOCK_TAB_BASE = 115262; // 115262-115271 for tabs 0-9

	private static int userWithdrawAmount = 1;
	private static int userDepositAmount = 1;

	/**
	 * Check if button ID is in the dock range
	 */
	public static boolean isDockButton(int buttonId) {
		return buttonId >= DOCK_BUTTON_START && buttonId <= DOCK_BUTTON_END;
	}

// Replace the handleDockButton method in DockBank with this enhanced version

	/**
	 * Static handler for dock button clicks - completely interface independent
	 * ENHANCED with debug logging
	 */
	public static boolean handleDockButton(int buttonId, Bank bank) {
		if (bank == null || bank.stoner == null) {
			System.err.println("DockBank: Null bank or stoner for button " + buttonId);
			return false;
		}

		// Debug logging
		System.out.println("DockBank: Handling button " + buttonId + " for player " + bank.stoner.getUsername());

		try {
			// Handle grid left-clicks (withdraw)
			if (buttonId >= DOCK_GRID_BASE && buttonId <= DOCK_GRID_END) {
				int gridIndex = buttonId - DOCK_GRID_BASE;
				System.out.println("DockBank: Grid withdraw at index " + gridIndex);
				debugGridState(bank, gridIndex);
				return handleGridWithdraw(bank, gridIndex);
			}

			// Handle grid right-clicks (deposit)
			if (buttonId >= DOCK_GRID_DEPOSIT_BASE && buttonId <= DOCK_GRID_DEPOSIT_END) {
				int gridIndex = buttonId - DOCK_GRID_DEPOSIT_BASE;
				System.out.println("DockBank: Grid deposit at index " + gridIndex);
				debugGridState(bank, gridIndex);
				return handleGridDeposit(bank, gridIndex);
			}

			// Handle amount setting buttons
			switch (buttonId) {
				case DOCK_SET_WITHDRAW_AMOUNT:
					System.out.println("DockBank: Cycling withdraw amount");
					return dockCycleWithdrawAmount(bank);
				case DOCK_SET_DEPOSIT_AMOUNT:
					System.out.println("DockBank: Cycling deposit amount");
					return dockCycleDepositAmount(bank);

				case DOCK_WITHDRAW_ITEM:
					return handleParameterizedWithdraw(bank);

				case DOCK_DEPOSIT_ITEM:
					return handleParameterizedDeposit(bank);

				case DOCK_DEPOSIT_ALL_INVENTORY:
					System.out.println("DockBank: Deposit all inventory");
					return dockDepositAllInventory(bank);

				case DOCK_DEPOSIT_ALL_EQUIPMENT:
					System.out.println("DockBank: Deposit all equipment");
					return dockDepositAllEquipment(bank);

				case DOCK_TOGGLE_WITHDRAW_MODE:
					System.out.println("DockBank: Toggle withdraw mode");
					bank.withdrawType = (bank.withdrawType == Bank.WithdrawTypes.ITEM) ?
						Bank.WithdrawTypes.NOTE : Bank.WithdrawTypes.ITEM;
					bank.stoner.getClient().queueOutgoingPacket(
						new SendMessage("Withdraw mode: " + bank.withdrawType.toString())
					);
					return true;

				case DOCK_TOGGLE_REARRANGE_MODE:
					bank.rearrangeType = (bank.rearrangeType == Bank.RearrangeTypes.SWAP) ?
						Bank.RearrangeTypes.INSERT : Bank.RearrangeTypes.SWAP;
					bank.stoner.getClient().queueOutgoingPacket(
						new SendMessage("Rearrange mode: " + bank.rearrangeType.toString())
					);
					return true;

				case DOCK_TOGGLE_SEARCH:
					dockToggleSearch(bank);
					return true;

				default:
					// Handle bank tab buttons (115262-115271)
					if (buttonId >= DOCK_TAB_BASE && buttonId <= DOCK_TAB_BASE + 9) {
						int tabIndex = buttonId - DOCK_TAB_BASE;
						dockSwitchTab(bank, tabIndex);
						return true;
					}

					System.out.println("DockBank: Unknown button ID " + buttonId);
					break;
			}
		} catch (Exception e) {
			System.err.println("DockBank: Exception handling button " + buttonId + ": " + e.getMessage());
			e.printStackTrace();
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("Error processing dock bank action: " + e.getMessage())
			);
		}

		return false;
	}

	public static boolean handleParameterizedWithdraw(Bank bank) {
		Integer itemId = (Integer) bank.stoner.getAttributes().get("dock_withdraw_itemid");
		Integer amount = (Integer) bank.stoner.getAttributes().get("dock_withdraw_amount");

		if (itemId == null || amount == null) {
			bank.stoner.send(new SendMessage("Error: Missing withdraw parameters"));
			return false;
		}

		// Clear attributes after reading
		bank.stoner.getAttributes().remove("dock_withdraw_itemid");
		bank.stoner.getAttributes().remove("dock_withdraw_amount");

		return dockWithdrawSpecificItem(bank, itemId, amount);
	}

	public static boolean handleParameterizedDeposit(Bank bank) {
		Integer itemId = (Integer) bank.stoner.getAttributes().get("dock_deposit_itemid");
		Integer amount = (Integer) bank.stoner.getAttributes().get("dock_deposit_amount");

		if (itemId == null || amount == null) {
			bank.stoner.send(new SendMessage("Error: Missing deposit parameters"));
			return false;
		}

		// Clear attributes after reading
		bank.stoner.getAttributes().remove("dock_deposit_itemid");
		bank.stoner.getAttributes().remove("dock_deposit_amount");

		return dockDepositSpecificItem(bank, itemId, amount);
	}

	/**
	 * Deposit all inventory items - called from button handler
	 */
	public static boolean dockDepositAllInventory(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		Item[] boxItems = bank.stoner.getBox().getItems();
		int deposited = 0;

		for (int i = 0; i < boxItems.length; i++) {
			if (boxItems[i] != null) {
				if (dockSafeDeposit(bank, boxItems[i], i)) {
					deposited++;
				} else {
					break; // Stop if bank is full
				}
			}
		}

		if (deposited > 0) {
			dockUpdateBank(bank);
			bank.stoner.send(new SendMessage("Deposited " + deposited + " items from inventory."));
			return true;
		} else {
			bank.stoner.send(new SendMessage("No items to deposit or bank is full."));
		}

		return false;
	}

	/**
	 * Deposit all equipment - called from button handler
	 */
	public static boolean dockDepositAllEquipment(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		Item[] equipment = bank.stoner.getEquipment().getItems();
		int deposited = 0;

		for (int i = 0; i < equipment.length; i++) {
			if (equipment[i] != null) {
				Item equipItem = new Item(equipment[i].getId(), equipment[i].getAmount());

				// Convert to unnoted version for bank storage
				if (equipItem.getDefinition().isNote()) {
					equipItem.unNote();
				}

				int depositedAmount = dockSafeAdd(bank, equipItem);

				if (depositedAmount > 0) {
					// Handle tab placement for new items
					if (bank.getItemAmount(equipItem.getId()) == depositedAmount) {
						// New item, add to current tab
						dockChangeTabAmount(bank, bank.bankTab, 1, false);
						dockMoveItemToTab(bank, bank.getItemSlot(equipItem.getId()), bank.bankTab);
					}

					// Remove from equipment
					if (equipment[i].getAmount() == depositedAmount) {
						bank.stoner.getEquipment().getItems()[i] = null;
					} else {
						equipment[i].remove(depositedAmount);
					}
					deposited++;
				}
			}
		}

		if (deposited > 0) {
			dockUpdateBank(bank);
			bank.stoner.getEquipment().onLogin();
			bank.stoner.setAppearanceUpdateRequired(true);
			bank.stoner.send(new SendMessage("Deposited " + deposited + " equipped items."));
			return true;
		}

		return false;
	}

	/**
	 * Toggle search mode - called from button handler
	 */
	public static void dockToggleSearch(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return;
		}

		boolean newSearchState = !bank.isSearching();

		// Manually set search state without interface calls
		try {
			bank.setSearching(newSearchState);
		} catch (Exception e) {
			// Fallback: directly toggle the field if setSearching has interface dependencies
			System.err.println("Error toggling search state: " + e.getMessage());
		}

		bank.stoner.send(new SendMessage("Search mode: " + (newSearchState ? "ON" : "OFF")));
	}

	/**
	 * Switch bank tab - called from button handler
	 */
	public static void dockSwitchTab(Bank bank, int tabIndex) {
		if (bank == null || bank.stoner == null) {
			return;
		}

		bank.setBankTab(tabIndex);

		// Turn off search when switching tabs
		try {
			bank.setSearching(false);
		} catch (Exception e) {
			// Handle if setSearching has interface dependencies
		}

		dockUpdateBank(bank);
		bank.stoner.send(new SendMessage("Switched to bank tab " + tabIndex));
	}



	/**
	 * Get combined grid items (inventory first, then bank) - SERVER SIDE
	 */
	private static Item[] getCombinedGridItems(Bank bank) {
		Item[] inventoryItems = bank.stoner.getBox().getItems();
		Item[] bankItems = bank.getItems();

		// Count actual inventory items (non-null)
		int invCount = 0;
		for (Item item : inventoryItems) {
			if (item != null) invCount++;
		}

		// Count actual bank items (non-null)
		int bankCount = 0;
		for (Item item : bankItems) {
			if (item != null) bankCount++;
		}

		// Create combined array
		Item[] combined = new Item[invCount + bankCount];
		int index = 0;

		// Add inventory items first (matching client order)
		for (Item item : inventoryItems) {
			if (item != null) {
				combined[index++] = item;
			}
		}

		// Add bank items (excluding duplicates that are in inventory)
		for (Item item : bankItems) {
			if (item != null) {
				// Only add if not already in inventory
				if (!isItemInInventory(bank, item.getId())) {
					combined[index++] = item;
				}
			}
		}

		return combined;
	}

	/**
	 * Check if item ID exists in player's inventory
	 */
	private static boolean isItemInInventory(Bank bank, int itemId) {
		Item[] inventoryItems = bank.stoner.getBox().getItems();
		for (Item item : inventoryItems) {
			if (item != null && item.getId() == itemId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Count non-null inventory items - SERVER SIDE
	 */
	private static int getInventoryItemCount(Bank bank) {
		int count = 0;
		Item[] inventoryItems = bank.stoner.getBox().getItems();
		for (Item item : inventoryItems) {
			if (item != null) count++;
		}
		return count;
	}

	/**
	 * Handle grid click for withdraw (left-click) - IMPROVED
	 */
	private static boolean handleGridWithdraw(Bank bank, int gridIndex) {
		// Get combined grid items (inventory + bank)
		Item[] gridItems = getCombinedGridItems(bank);

		if (gridIndex >= gridItems.length || gridItems[gridIndex] == null) {
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("No item at grid position " + gridIndex)
			);
			return false;
		}

		Item clickedItem = gridItems[gridIndex];

		// Check if this is an inventory item (appears in first X slots)
		int inventorySlotCount = getInventoryItemCount(bank);

		if (gridIndex < inventorySlotCount) {
			// This is an inventory item, can't withdraw from inventory
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("Cannot withdraw from inventory. Item: " + clickedItem.getDefinition().getName())
			);
			return false;
		}

		// This is a bank item, withdraw it
		int amount = (userWithdrawAmount == -1) ? clickedItem.getAmount() : userWithdrawAmount;
		return dockWithdrawSpecificItem(bank, clickedItem.getId(), amount);
	}

	/**
	 * Handle grid click for deposit (right-click) - IMPROVED
	 */
	private static boolean handleGridDeposit(Bank bank, int gridIndex) {
		// Get combined grid items (inventory + bank)
		Item[] gridItems = getCombinedGridItems(bank);

		if (gridIndex >= gridItems.length || gridItems[gridIndex] == null) {
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("No item at grid position " + gridIndex)
			);
			return false;
		}

		Item clickedItem = gridItems[gridIndex];

		// Check if this is an inventory item
		int inventorySlotCount = getInventoryItemCount(bank);

		if (gridIndex < inventorySlotCount) {
			// This is an inventory item, deposit it
			int amount = (userDepositAmount == -1) ? clickedItem.getAmount() : userDepositAmount;
			return dockDepositSpecificItem(bank, clickedItem.getId(), amount);
		} else {
			// This is a bank item, can't deposit from bank
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("Cannot deposit from bank. Item: " + clickedItem.getDefinition().getName())
			);
			return false;
		}
	}

	/**
	 * Cycle through common withdraw amounts - called from button handler
	 */
	public static boolean dockCycleWithdrawAmount(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		switch (userWithdrawAmount) {
			case 1: userWithdrawAmount = 5; break;
			case 5: userWithdrawAmount = 10; break;
			case 10: userWithdrawAmount = 50; break;
			case 50: userWithdrawAmount = -1; break; // ALL
			default: userWithdrawAmount = 1; break;
		}

		String amountText = (userWithdrawAmount == -1) ? "ALL" : String.valueOf(userWithdrawAmount);
		bank.stoner.send(new SendMessage("Withdraw amount: " + amountText));
		return true;
	}

	/**
	 * Cycle through common deposit amounts - called from button handler
	 */
	public static boolean dockCycleDepositAmount(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		switch (userDepositAmount) {
			case 1: userDepositAmount = 5; break;
			case 5: userDepositAmount = 10; break;
			case 10: userDepositAmount = 50; break;
			case 50: userDepositAmount = -1; break; // ALL
			default: userDepositAmount = 1; break;
		}

		String amountText = (userDepositAmount == -1) ? "ALL" : String.valueOf(userDepositAmount);
		bank.stoner.send(new SendMessage("Deposit amount: " + amountText));
		return true;
	}

	/**
	 * Withdraw specific item by ID and amount - called from grid packet
	 */
	public static boolean dockWithdrawSpecificItem(Bank bank, int itemId, int amount) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		// Use user-set amount if amount is -1
		int withdrawAmount = (amount == -1) ? userWithdrawAmount : amount;

		// Find the item in bank
		Item[] bankItems = bank.getItems();

		for (int i = 0; i < bankItems.length; i++) {
			if (bankItems[i] != null && bankItems[i].getId() == itemId) {
				int availableAmount = bankItems[i].getAmount();
				int actualWithdraw = (withdrawAmount == -1) ? availableAmount : Math.min(withdrawAmount, availableAmount);

				// Handle noting if withdraw mode is NOTE
				int withdrawId = itemId;
				if (bank.withdrawType == Bank.WithdrawTypes.NOTE) {
					if (Item.getDefinition(itemId).canNote()) {
						withdrawId = Item.getDefinition(itemId).getNoteId();
					} else {
						bank.stoner.send(new SendMessage("This item cannot be withdrawn as a note."));
					}
				}

				Item withdrawItem = new Item(withdrawId, actualWithdraw);
				if (dockInventoryHasSpaceFor(bank, withdrawItem)) {
					int added = bank.stoner.getBox().add(withdrawId, actualWithdraw, true);

					if (added > 0) {
						// Remove from bank
						bank.remove(new Item(itemId, added), false);

						// Handle tab amount changes
						if (bank.getItemAmount(itemId) == 0) {
							int tab = dockGetItemTab(bank, i);
							dockChangeTabAmount(bank, tab, -1, tab == 0 && bank.getTabAmount(0) == 1);
						}

						dockUpdateBank(bank);
						bank.stoner.send(new SendMessage("Withdrew " + added + "x " + bankItems[i].getDefinition().getName()));
						return true;
					}
				} else {
					bank.stoner.send(new SendMessage("Inventory is full!"));
					return false;
				}
			}
		}

		bank.stoner.send(new SendMessage("Item " + itemId + " not found in bank"));
		return false;
	}

	/**
	 * Deposit specific item by ID and amount - called from grid packet
	 */
	public static boolean dockDepositSpecificItem(Bank bank, int itemId, int amount) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		// Use user-set amount if amount is -1
		int depositAmount = (amount == -1) ? userDepositAmount : amount;

		Item[] boxItems = bank.stoner.getBox().getItems();

		// Find the FIRST occurrence of this item in inventory
		for (int i = 0; i < boxItems.length; i++) {
			if (boxItems[i] != null && boxItems[i].getId() == itemId) {
				int availableAmount = boxItems[i].getAmount();
				int actualDeposit = (depositAmount == -1) ? availableAmount : Math.min(depositAmount, availableAmount);

				Item depositItem = new Item(itemId, actualDeposit);
				if (depositItem.getDefinition().isNote()) {
					depositItem.unNote();
				}

				if (dockHasSpaceFor(bank, depositItem)) {
					boolean wasNewItem = !bank.hasItemId(depositItem.getId());
					int added = dockSafeAdd(bank, depositItem);

					if (added > 0) {
						// Handle tab placement for new items
						if (wasNewItem) {
							dockChangeTabAmount(bank, bank.bankTab, 1, false);
							dockMoveItemToTab(bank, bank.getItemSlot(depositItem.getId()), bank.bankTab);
						}

						// Remove from inventory using the Box's remove method
						bank.stoner.getBox().remove(itemId, added, true);

						dockUpdateBank(bank);
						bank.stoner.send(new SendMessage("Deposited " + added + "x " + depositItem.getDefinition().getName()));
						return true;
					}
				} else {
					bank.stoner.send(new SendMessage("Bank is full!"));
					return false;
				}
			}
		}

		bank.stoner.send(new SendMessage("Item " + itemId + " not found in inventory"));
		return false;
	}


	/**
	 * Safe add to bank without interface dependencies
	 */
	private static int dockSafeAdd(Bank bank, Item item) {
		int added = bank.add(item, false); // Don't auto-update, we'll do it manually
		if (added > 0) {
			SaveCache.markDirty(bank.stoner);
		}
		return added;
	}

	/**
	 * Check if bank has space for item
	 */
	private static boolean dockHasSpaceFor(Bank bank, Item item) {
		// If item already exists, check if we can add to existing stack
		if (bank.hasItemId(item.getId())) {
			return true; // Can always add to existing stack (within max stack limits)
		}

		// Check if we have empty slots
		return bank.getTakenSlots() < bank.getSize();
	}

	/**
	 * Check if inventory has space for item
	 */
	private static boolean dockInventoryHasSpaceFor(Bank bank, Item item) {
		return bank.stoner.getBox().hasSpaceFor(item);
	}

	/**
	 * Standalone bank update
	 */
	private static void dockUpdateBank(Bank bank) {
		bank.update(); // This should work as it just sends packets
	}

	/**
	 * Change tab amount safely
	 */
	private static void dockChangeTabAmount(Bank bank, int tab, int amount, boolean collapse) {
		int[] tabAmounts = bank.getTabAmounts();
		tabAmounts[tab] += amount;

		if (tabAmounts[tab] <= 0 && collapse) {
			// Handle collapse logic if needed
			bank.collapse(tab, 0);
		}
	}

	/**
	 * Move item to specific tab
	 */
	private static void dockMoveItemToTab(Bank bank, int itemSlot, int toTab) {
		if (itemSlot >= 0) {
			Bank.RearrangeTypes temp = bank.rearrangeType;
			bank.rearrangeType = Bank.RearrangeTypes.INSERT;

			int targetSlot = toTab == 0 ?
				bank.getTabAmount(toTab) - 1 :
				bank.getData(toTab, 1);

			bank.swap(targetSlot, itemSlot);
			bank.rearrangeType = temp;
		}
	}

	/**
	 * Get which tab an item slot belongs to
	 */
	private static int dockGetItemTab(Bank bank, int slot) {
		return bank.getData(slot, 0);
	}

// Add debug method to help troubleshoot
	/**
	 * Debug method to check grid state
	 */
	public static void debugGridState(Bank bank, int gridIndex) {
		Item[] gridItems = getCombinedGridItems(bank);
		int inventoryCount = getInventoryItemCount(bank);

		bank.stoner.getClient().queueOutgoingPacket(
			new SendMessage("Grid Debug: Index=" + gridIndex + ", Total=" + gridItems.length +
				", InvCount=" + inventoryCount + ", IsInv=" + (gridIndex < inventoryCount))
		);

		if (gridIndex < gridItems.length && gridItems[gridIndex] != null) {
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("Item at " + gridIndex + ": " + gridItems[gridIndex].getDefinition().getName() +
					" x" + gridItems[gridIndex].getAmount())
			);
		}
	}


	// =================== UTILITY METHODS ===================

	/**
	 * Safe deposit that bypasses interface checks
	 */
	private static boolean dockSafeDeposit(Bank bank, Item item, int slot) {
		if (!bank.stoner.getBox().slotContainsItem(slot, item.getId())) {
			return false;
		}

		Item depositItem = new Item(item.getId(), item.getAmount());
		if (depositItem.getDefinition().isNote()) {
			depositItem.unNote();
		}

		if (!dockHasSpaceFor(bank, depositItem)) {
			return false;
		}

		boolean wasNewItem = !bank.hasItemId(depositItem.getId());
		int added = dockSafeAdd(bank, depositItem);

		if (added > 0) {
			// Handle tab placement for new items
			if (wasNewItem) {
				dockChangeTabAmount(bank, bank.bankTab, 1, false);
				dockMoveItemToTab(bank, bank.getItemSlot(depositItem.getId()), bank.bankTab);
			}

			// Remove from inventory
			if (item.getAmount() == 1 && !Item.getDefinition(item.getId()).isStackable()) {
				bank.stoner.getBox().setSlot(null, slot);
			} else {
				bank.stoner.getBox().remove(item.getId(), added);
			}

			return true;
		}

		return false;
	}

}