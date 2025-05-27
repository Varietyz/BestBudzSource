package com.mayhem.rs2.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendItemOnInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class RunePouch {
	public Player player;
	public List<Item> items;

	public static final int RUNE_POUCH_ID = 12791;
	public static final boolean CHECK_FOR_POUCH = true;

	final int START_ITEM_INTERFACE = 29908;
	final int START_INVENTORY_INTERFACE = 29880;

	public boolean viewingRunePouch = false;

	public int selectedItem = -1;
	public int selectedSlot = -1;
	public int interfaceId = -1;
	private String sendSpells;
	private int amt;

	public RunePouch(Player player) {
		this.player = player;
		items = new ArrayList<>();
	}
	
	public static boolean isRunePouch(Player player, int itemId) {
		return itemId == RUNE_POUCH_ID;
	}

	public boolean handleButton(int buttonId) {
		if (buttonId == 116181) {
			closeLootbag();
			return true;
		}
		return false;
	}

	public void openRunePouch() {
		if (!player.getInventory().hasItemId(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return;
		}
		onClose();
		sendItems();
		sendInventoryItems();
		player.send(new SendInterface(29875));
		viewingRunePouch = true;
	}

	public void withdrawAll() {
		for (Iterator<Item> iterator = items.iterator(); iterator.hasNext();) {
			Item item = iterator.next();
			if (!player.getInventory().hasSpaceFor(item)) {
				break;
			}
			iterator.remove();
		}
		sendItems();
		sendInventoryItems();
	}
	
	public boolean handleRunePouch(int id) {
		if (id == RUNE_POUCH_ID && !viewingRunePouch) {
			openRunePouch();
			return true;
		}
		return false;
	}

	public boolean handleClickItem(int id, int amount, int interfaceId) {
		if (!viewingRunePouch) {
			return false;
		}
		if (interfaceId >= START_ITEM_INTERFACE && (interfaceId <= START_ITEM_INTERFACE + 2)) {
			//removeItemFromRunePouch(id, amount);
			removeMultipleItemsFromBag(id, amount);
			return true;
		} else if (interfaceId >= START_INVENTORY_INTERFACE && (interfaceId <= START_INVENTORY_INTERFACE + 27)) {
			addItemToRunePouch(id, amount);
			return true;
		}
		return false;
	}
	public int countItems(int id) {
		int count = 0;
		for (Item item : items) {
			if (item.getId() == id) {
				count += item.getAmount();
			}
		}
		return count;
	}
	public void removeMultipleItemsFromBag(int id, int amount) {
		if (amount >= Integer.MAX_VALUE) {
			amount = countItems(id);
		}
		int count = 0;
		while (containsItem(id)) {
			if (!removeItemFromRunePouch(id, amount)) {
				break;
			}
			count+=amount;
			if (count >= amount) {
				break;
			}
		}
	}	
	
	public boolean containsItem(int id) {
		for (Item item : items) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	
	public int findIndexInLootBag(int id) {
		for (Item item : items) {
			if (item.getId() == id) {
				return items.indexOf(item);
			}
		}
		return -1;
	}

	public boolean removeItemFromRunePouch(int id, int amount) {
		if (items.size() <= 0) {
			return false;
		}
		int index = findIndexInLootBag(id);
		if (index == -1) {
			return false;
		}
		Item item = items.get(index);
		if (item == null) {
			return false;
		}
		if (item.getId() <= 0 || item.getAmount() <= 0) {
			return false;
		}
		if (player.getInventory().getFreeSlots() <= 0) {
			if (!(player.getInventory().hasItemId(id) && player.getInventory().isStackable(id))) {
				return false;
			}
		}
		if (player.getInventory().getItemAmount(id) + amount >= Integer.MAX_VALUE ||player.getInventory().getItemAmount(id) + amount <= 0) {
			return false;
		}

		int amountToAdd = 0;
		if ((items.get(items.indexOf(item)).getAmount()) > amount) {
			amountToAdd = amount;
			items.get(items.indexOf(item)).add(-amount);
		} else {
			amountToAdd = item.getAmount();
			items.remove(index);
		}

		player.getInventory().add(item.getId(), amountToAdd);

		System.out.println("Removing pouch item: " + item.getId());
		sendItems();
		sendInventoryItems();
		return true;
	}

	public void deleteItemFromRunePouch(int id, int amount) {
		if (items.size() <= 0) {
			return;
		}
		int index = findIndexInLootBag(id);
		if (index == -1) {
			return;
		}
		Item item = items.get(index);
		if (item == null) {
			return;
		}
		if (item.getId() <= 0 || item.getAmount() <= 0) {
			return;
		}
		if ((items.get(items.indexOf(item)).getAmount()) > amount) {
			items.get(items.indexOf(item)).add(-amount);
		} else {
			items.remove(index);
		}
		System.out.println("Deleting pouch item: " + item.getId());
		sendItems();
	}

	public boolean pouchContainsItem(int id) {
		for (Item item : items) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean pouchContainsItem(int id, int amount) {
		for (Item item : items) {
			if (item.getId() == id && item.getAmount() >= amount) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRunes(int runes, int amount) {
		if (!player.getInventory().hasItemId(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		if (runes <= 0 || amount <= 0) {
			return true;
		}
		if (!pouchContainsItem(runes, amount)) {
			return false;
		}
		return true;
	}

	public boolean hasRunes(int[] runes, int[] runeAmounts) {
		if (!player.getInventory().hasItemId(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		for (int i = 0; i < runes.length; i++) {
			if (!pouchContainsItem(runes[i], runeAmounts[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean deleteRunesOnCast(int runes, int runeAmounts) {
		if (!player.getInventory().hasItemId(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		if (!hasRunes(runes, runeAmounts)) {
			return false;
		}
		deleteItemFromRunePouch(runes, runeAmounts);
		return true;
	}

	public boolean deleteRunesOnCast(int[] runes, int[] runeAmounts) {
		if (!player.getInventory().hasItemId(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		if (!hasRunes(runes, runeAmounts)) {
			return false;
		}
		for (int i = 0; i < runes.length; i++) {
			deleteItemFromRunePouch(runes[i], runeAmounts[i]);
		}
		return true;
	}

	public void addItemToRunePouch(int id, int amount) {
		if (amount >= Integer.MAX_VALUE) {
			amount = player.getInventory().getItemAmount(id);
	
		}
		if (!((id >= 554 && id <= 566) || (id >= 4694 && id <= 4699) || id == 9075)) {
			//if(!(id == 9075))
			player.send(new SendMessage ("You can only store runes in a rune pouch."));
			return;
		}
		if (items.size() >= 3 && !(pouchContainsItem(id) && player.getInventory().isStackable(id))) {
			player.send(new SendMessage("Pouch is full."));
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		if (id == RUNE_POUCH_ID) {
			player.send(new SendMessage("Don't be silly."));
			return;
		}
		
		//int amt = player.getItems().deleteItemAndReturnAmount(id, amount);
		//addItemToList(id, amt);
		
		List<Integer> amounts = player.getInventory().deleteItemAndReturnAmount(id, amount);
		// int amt = player.getItems().deleteItemAndReturnAmount(id, amount);
		
		int count = 0;
		for (int amt : amounts) {
			if (!addItemToList(id, amt)) {
				break;
			}
			count++;
			if (count >= amount) {
				break;
			}
		}
	
		resetItems();
		sendItems();
		sendInventoryItems();
	}

	public boolean addItemToList(int id, int amount) {
		for (Item item : items) {
			if (item.getId() == id) {
				if (item.getAmount() + amount >= Integer.MAX_VALUE) {
					return false;
				}
				if (player.getInventory().isStackable(id)) {
					item.add(amount);
					return true;
				}
			}
		}
		items.add(new Item(id, amount));
		return true;
	}

	public void closeLootbag() {
		onClose();
	}

	public void onClose() {
		viewingRunePouch = false;
		player.send(new SendRemoveInterfaces());
	}

	public void onLogin() {
		sendItems();
	}

	public void sendItems() {
		if (!player.getInventory().hasItemId(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return;
		}

		setSendSpells("#");

		for (int i = 0; i < 3; i++) {
			int id = 0;
			int amt = 0;

			if (i < items.size()) {
				Item item = items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
			}

			if (id <= 0) {
				id = -1;
			}

			player.getClient().queueOutgoingPacket(new SendItemOnInterface(START_ITEM_INTERFACE,0, id));
			if (id == -1)
				id = 0;
			if (i == 2) {
				setSendSpells(getSendSpells() + id + ":" + amt);
			} else {
				setSendSpells(getSendSpells() + id + ":" + amt + "-");
			}
		}
		setSendSpells(getSendSpells() + "$");
		//player.getFunction().sendString(49999, sendSpells);
	}

	public void sendInventoryItems() {
		if (!player.getInventory().hasItemId(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return;
		}
		for (int i = 0; i < 28; i++) {
			int id = 0;
			setAmt(0);

			if (i < player.getInventory().getItems().length) {
				id = player.getInventory().getItems()[i].getId();
				//amt = player.playerItemsN[i];
			}

			
			//PlayerFunction.itemOnInterface(c, START_INVENTORY_INTERFACE + i, 0, id - 1, amt);
			player.getClient().queueOutgoingPacket(new SendItemOnInterface(START_INVENTORY_INTERFACE,0, id - 1));
		}
	}

	private void resetItems() {
		//player.getItems().resetItems(3214);
		//player.getFunction().requestUpdates();
	}

	public String getSendSpells() {
		return sendSpells;
	}

	public void setSendSpells(String sendSpells) {
		this.sendSpells = sendSpells;
	}

	public int getAmt() {
		return amt;
	}

	public void setAmt(int amt) {
		this.amt = amt;
	}
}