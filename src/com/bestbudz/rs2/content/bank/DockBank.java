package com.bestbudz.rs2.content.bank;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.content.io.sqlite.SaveCache;

public class DockBank {

	public static final int DOCK_BUTTON_START = 115247;
	public static final int DOCK_BUTTON_END = 116050;

	public static final int DOCK_DEPOSIT_ALL_INVENTORY = 115247;
	public static final int DOCK_DEPOSIT_ALL_EQUIPMENT = 115248;
	public static final int DOCK_TOGGLE_WITHDRAW_MODE = 115249;
	public static final int DOCK_TOGGLE_REARRANGE_MODE = 115250;
	public static final int DOCK_TOGGLE_SEARCH = 115251;

	public static final int DOCK_GRID_BASE = 115280;
	public static final int DOCK_GRID_END = 115379;

	public static final int DOCK_GRID_DEPOSIT_BASE = 115380;
	public static final int DOCK_GRID_DEPOSIT_END = 115479;

	public static final int DOCK_SET_WITHDRAW_AMOUNT = 115252;
	public static final int DOCK_SET_DEPOSIT_AMOUNT = 115253;

	public static final int DOCK_WITHDRAW_ITEM = 116044;
	public static final int DOCK_DEPOSIT_ITEM = 116045;

	public static final int DOCK_TAB_BASE = 115262;

	private static int userWithdrawAmount = 1;
	private static int userDepositAmount = 1;

	public static boolean isDockButton(int buttonId) {
		return buttonId >= DOCK_BUTTON_START && buttonId <= DOCK_BUTTON_END;
	}

	public static boolean handleDockButton(int buttonId, Bank bank) {
		if (bank == null || bank.stoner == null) {
			System.err.println("DockBank: Null bank or stoner for button " + buttonId);
			return false;
		}

		System.out.println("DockBank: Handling button " + buttonId + " for player " + bank.stoner.getUsername());

		try {

			if (buttonId >= DOCK_GRID_BASE && buttonId <= DOCK_GRID_END) {
				int gridIndex = buttonId - DOCK_GRID_BASE;
				System.out.println("DockBank: Grid withdraw at index " + gridIndex);
				debugGridState(bank, gridIndex);
				return handleGridWithdraw(bank, gridIndex);
			}

			if (buttonId >= DOCK_GRID_DEPOSIT_BASE && buttonId <= DOCK_GRID_DEPOSIT_END) {
				int gridIndex = buttonId - DOCK_GRID_DEPOSIT_BASE;
				System.out.println("DockBank: Grid deposit at index " + gridIndex);
				debugGridState(bank, gridIndex);
				return handleGridDeposit(bank, gridIndex);
			}

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

		bank.stoner.getAttributes().remove("dock_deposit_itemid");
		bank.stoner.getAttributes().remove("dock_deposit_amount");

		return dockDepositSpecificItem(bank, itemId, amount);
	}

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
					break;
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

	public static boolean dockDepositAllEquipment(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		Item[] equipment = bank.stoner.getEquipment().getItems();
		int deposited = 0;

		for (int i = 0; i < equipment.length; i++) {
			if (equipment[i] != null) {
				Item equipItem = new Item(equipment[i].getId(), equipment[i].getAmount());

				if (equipItem.getDefinition().isNote()) {
					equipItem.unNote();
				}

				int depositedAmount = dockSafeAdd(bank, equipItem);

				if (depositedAmount > 0) {

					if (bank.getItemAmount(equipItem.getId()) == depositedAmount) {

						dockChangeTabAmount(bank, bank.bankTab, 1, false);
						dockMoveItemToTab(bank, bank.getItemSlot(equipItem.getId()), bank.bankTab);
					}

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

	public static void dockToggleSearch(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return;
		}

		boolean newSearchState = !bank.isSearching();

		try {
			bank.setSearching(newSearchState);
		} catch (Exception e) {

			System.err.println("Error toggling search state: " + e.getMessage());
		}

		bank.stoner.send(new SendMessage("Search mode: " + (newSearchState ? "ON" : "OFF")));
	}

	public static void dockSwitchTab(Bank bank, int tabIndex) {
		if (bank == null || bank.stoner == null) {
			return;
		}

		bank.setBankTab(tabIndex);

		try {
			bank.setSearching(false);
		} catch (Exception e) {

		}

		dockUpdateBank(bank);
		bank.stoner.send(new SendMessage("Switched to bank tab " + tabIndex));
	}

	private static Item[] getCombinedGridItems(Bank bank) {
		Item[] inventoryItems = bank.stoner.getBox().getItems();
		Item[] bankItems = bank.getItems();

		int invCount = 0;
		for (Item item : inventoryItems) {
			if (item != null) invCount++;
		}

		int bankCount = 0;
		for (Item item : bankItems) {
			if (item != null) bankCount++;
		}

		Item[] combined = new Item[invCount + bankCount];
		int index = 0;

		for (Item item : inventoryItems) {
			if (item != null) {
				combined[index++] = item;
			}
		}

		for (Item item : bankItems) {
			if (item != null) {

				if (!isItemInInventory(bank, item.getId())) {
					combined[index++] = item;
				}
			}
		}

		return combined;
	}

	private static boolean isItemInInventory(Bank bank, int itemId) {
		Item[] inventoryItems = bank.stoner.getBox().getItems();
		for (Item item : inventoryItems) {
			if (item != null && item.getId() == itemId) {
				return true;
			}
		}
		return false;
	}

	private static int getInventoryItemCount(Bank bank) {
		int count = 0;
		Item[] inventoryItems = bank.stoner.getBox().getItems();
		for (Item item : inventoryItems) {
			if (item != null) count++;
		}
		return count;
	}

	private static boolean handleGridWithdraw(Bank bank, int gridIndex) {

		Item[] gridItems = getCombinedGridItems(bank);

		if (gridIndex >= gridItems.length || gridItems[gridIndex] == null) {
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("No item at grid position " + gridIndex)
			);
			return false;
		}

		Item clickedItem = gridItems[gridIndex];

		int inventorySlotCount = getInventoryItemCount(bank);

		if (gridIndex < inventorySlotCount) {

			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("Cannot withdraw from inventory. Item: " + clickedItem.getDefinition().getName())
			);
			return false;
		}

		int amount = (userWithdrawAmount == -1) ? clickedItem.getAmount() : userWithdrawAmount;
		return dockWithdrawSpecificItem(bank, clickedItem.getId(), amount);
	}

	private static boolean handleGridDeposit(Bank bank, int gridIndex) {

		Item[] gridItems = getCombinedGridItems(bank);

		if (gridIndex >= gridItems.length || gridItems[gridIndex] == null) {
			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("No item at grid position " + gridIndex)
			);
			return false;
		}

		Item clickedItem = gridItems[gridIndex];

		int inventorySlotCount = getInventoryItemCount(bank);

		if (gridIndex < inventorySlotCount) {

			int amount = (userDepositAmount == -1) ? clickedItem.getAmount() : userDepositAmount;
			return dockDepositSpecificItem(bank, clickedItem.getId(), amount);
		} else {

			bank.stoner.getClient().queueOutgoingPacket(
				new SendMessage("Cannot deposit from bank. Item: " + clickedItem.getDefinition().getName())
			);
			return false;
		}
	}

	public static boolean dockCycleWithdrawAmount(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		switch (userWithdrawAmount) {
			case 1: userWithdrawAmount = 5; break;
			case 5: userWithdrawAmount = 10; break;
			case 10: userWithdrawAmount = 50; break;
			case 50: userWithdrawAmount = -1; break;
			default: userWithdrawAmount = 1; break;
		}

		String amountText = (userWithdrawAmount == -1) ? "ALL" : String.valueOf(userWithdrawAmount);
		bank.stoner.send(new SendMessage("Withdraw amount: " + amountText));
		return true;
	}

	public static boolean dockCycleDepositAmount(Bank bank) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		switch (userDepositAmount) {
			case 1: userDepositAmount = 5; break;
			case 5: userDepositAmount = 10; break;
			case 10: userDepositAmount = 50; break;
			case 50: userDepositAmount = -1; break;
			default: userDepositAmount = 1; break;
		}

		String amountText = (userDepositAmount == -1) ? "ALL" : String.valueOf(userDepositAmount);
		bank.stoner.send(new SendMessage("Deposit amount: " + amountText));
		return true;
	}

	public static boolean dockWithdrawSpecificItem(Bank bank, int itemId, int amount) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		int withdrawAmount = (amount == -1) ? userWithdrawAmount : amount;

		Item[] bankItems = bank.getItems();

		for (int i = 0; i < bankItems.length; i++) {
			if (bankItems[i] != null && bankItems[i].getId() == itemId) {
				int availableAmount = bankItems[i].getAmount();
				int actualWithdraw = (withdrawAmount == -1) ? availableAmount : Math.min(withdrawAmount, availableAmount);

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

						bank.remove(new Item(itemId, added), false);

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

	public static boolean dockDepositSpecificItem(Bank bank, int itemId, int amount) {
		if (bank == null || bank.stoner == null) {
			return false;
		}

		int depositAmount = (amount == -1) ? userDepositAmount : amount;

		Item[] boxItems = bank.stoner.getBox().getItems();

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

						if (wasNewItem) {
							dockChangeTabAmount(bank, bank.bankTab, 1, false);
							dockMoveItemToTab(bank, bank.getItemSlot(depositItem.getId()), bank.bankTab);
						}

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

	private static int dockSafeAdd(Bank bank, Item item) {
		int added = bank.add(item, false);
		if (added > 0) {
			SaveCache.markDirty(bank.stoner);
		}
		return added;
	}

	private static boolean dockHasSpaceFor(Bank bank, Item item) {

		if (bank.hasItemId(item.getId())) {
			return true;
		}

		return bank.getTakenSlots() < bank.getSize();
	}

	private static boolean dockInventoryHasSpaceFor(Bank bank, Item item) {
		return bank.stoner.getBox().hasSpaceFor(item);
	}

	private static void dockUpdateBank(Bank bank) {
		bank.update();
	}

	private static void dockChangeTabAmount(Bank bank, int tab, int amount, boolean collapse) {
		int[] tabAmounts = bank.getTabAmounts();
		tabAmounts[tab] += amount;

		if (tabAmounts[tab] <= 0 && collapse) {

			bank.collapse(tab, 0);
		}
	}

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

	private static int dockGetItemTab(Bank bank, int slot) {
		return bank.getData(slot, 0);
	}

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

			if (wasNewItem) {
				dockChangeTabAmount(bank, bank.bankTab, 1, false);
				dockMoveItemToTab(bank, bank.getItemSlot(depositItem.getId()), bank.bankTab);
			}

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
