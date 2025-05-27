package com.mayhem.rs2.content;

import java.util.ArrayList;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.Area;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.ItemContainer;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendSidebarInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
import com.mayhem.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * @author ReverendDread
 * Aug 5, 2018
 */
public class LootingBag extends ItemContainer {

	/**
	 * The player this bag is assosiated with.
	 */
	private transient Player player;
	
	/** The looting bag item id. */
	private final int LOOTING_BAG = 11941;
	
	/**
	 * The looting bag constructor
	 * @param player
	 */
	public LootingBag(final Player player) {
		super(28, ContainerTypes.STACK, true, true);
		this.player = player;
	}
	
	/**
	 * Called when adding an item to the looting bag.
	 * @param item
	 * @return
	 */
	public boolean addItem(Item item) {
		if (!player.inWilderness()) {
			player.send(new SendMessage("You can't put items in the bag unless you're in the Wilderness."));
			return false;
		}
		if (item.getId() == LOOTING_BAG) {
			player.send(new SendMessage("You can't put another looting bag inside this one."));
			return false;
		}
		if (!item.getDefinition().isTradable()) {
			player.send(new SendMessage("You can't put untradable items into your looting bag."));
			return false;
		}
		int removed = add(item);
		if (removed > 0) {
			player.send(new SendMessage("You add " + Utility.formatCoins(removed) + " " + item.getDefinition().getName() + " to your looting bag."));
			player.getInventory().remove(item.getId(), removed);
		}
		return true;
	}
	
	/**
	 * Drops looting bag contents.
	 * @param killer
	 */
	public void dropItemsOnDeath(Player killer) {
		if (player.getInventory().hasItemId(LOOTING_BAG)) {
			if (containsItems()) {
				for (Item item : getItems()) {
					if (item == null || !item.getDefinition().isTradable())
						continue;
					GroundItemHandler.add(item, player.getLocation(), killer, 120);
				}
				player.send(new SendMessage("Your looting bag has dropped its items on the ground."));
				clear();
			}
		}
	}
	
	/**
	 * Deposits contents into bank.
	 * @return
	 */
	public boolean depositItems() {
		//Check if looting bag has items in it to prevent unnessesary looping of container.
		if (!containsItems()) {
			player.send(new SendMessage("Your looting bag doesn't contain any items."));
			return false;
		}
		//Check if their near a banking location.
		if (!nearBank()) {
			player.send(new SendMessage("You can't deposit your items unless you're near a banking area."));
			return false;
		}
		ArrayList<Item> removing = new ArrayList<Item>();
		for (Item item : getItems()) {
			//Check if the item is null, if so skip this loop,
			if (item == null)
				continue;
			//Check if the bank has space for the item, if so bank it.
			if (player.getBank().hasSpaceFor(item)) {
				int removed = player.getBank().add(item);
				removing.add(new Item(item.getId(), removed));
			}
		};
		//Remove the items from the container.
		removing.forEach((item) -> {
			remove(item);
		});
		player.send(new SendMessage("Your looting bag deposits its items into your bank."));
		return true;
	}
	
	/**
	 * Checks if the player is near a banking location.
	 * @return
	 * 			true if the player is in a bank area, false otherwise.
	 * TODO add more locations. (travis you can do this you lazy cunt)
	 */
	private boolean nearBank() {
		return (player.inArea(new Area(3091, 3488, 3098, 3499, (byte) 0), false)); //Edgeville bank
	}
	
	/**
	 * Called when opening the looting bag.
	 * TODO
	 */
	public void openBag() {
		player.send(new SendSidebarInterface(3, 30000));
		player.send(new SendUpdateItems(30006, getItems()));
		player.send(new SendString("Value: " + getContainerNet(), 30007));
	}


	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.item.ItemContainer#allowZero(int)
	 */
	@Override
	public boolean allowZero(int paramInt) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.item.ItemContainer#onAdd(com.mayhem.rs2.entity.item.Item)
	 */
	@Override
	public void onAdd(Item item) {}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.item.ItemContainer#onFillContainer()
	 */
	@Override
	public void onFillContainer() {
		player.send(new SendMessage("Your looting bag is full."));
	}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.item.ItemContainer#onMaxStack()
	 */
	@Override
	public void onMaxStack() {}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.item.ItemContainer#onRemove(com.mayhem.rs2.entity.item.Item)
	 */
	@Override
	public void onRemove(Item paramItem) {}

	/* (non-Javadoc)
	 * @see com.mayhem.rs2.entity.item.ItemContainer#update()
	 */
	@Override
	public void update() {}
	
}
