package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Coins currency
 * 
 * @author Divine
 */
public class ClueShop extends Shop {
	
	/**
	 * Item id of coins
	 */
	public static final int COINS = 995;
	
	/**
	 * Id of customs store
	 */
	public static final int SHOP_ID = 106;

	/**
	 * Prices of items in store
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 1694:
			return 25000;
		case 2890:
			return 52834;
		case 1347:
			return 35453;
		case 1103:
		case 1075:
		case 1307:
			return 2500;
		case 1067:
			return 3434;
		case 1237:
			return 15823;
		case 1654:
			return 5475;
		case 1635:
			return 4586;
		case 1687:
			return 9000;
		case 1643:
			return 10384;
		case 1731:
			return 8453;
		case 658:
			return 1193;
		case 6328:
			return 62985;
		case 1267:
			return 3506;
		case 1133:
			return 7550;
		case 579:
			return 8574;
		case 1061:
			return 1048;
		case 845:
			return 576;
		case 2493:
			return 23945;
			
		case 318:
		case 1540:
			return 100;
		
		case 952:
			return 500;
		}

		return 2147483647;
	}

	/**
	 * Items in store
	 */
	public ClueShop() {
		super(SHOP_ID, new Item[] { 
				new Item(1694, 1),
				new Item(2890, 1),
				new Item(1347, 1),
				new Item(1103, 1),
				new Item(1075, 1),
				new Item(1307, 1),
				new Item(1067, 1),
				new Item(1237, 1),
				new Item(1654, 1),
				new Item(1635, 1),
				new Item(1643, 1),
				new Item(1731, 1),
				new Item(658, 1),
				new Item(6328, 1),
				new Item(1267, 1),
				new Item(1133, 1),
				new Item(579, 1),
				new Item(1061, 1),
				new Item(845, 1),
				new Item(2493, 1),
				new Item(1540, 25),
				new Item(952, 25),
				new Item(318, 2000),
			}, false, "Clue Scroll Shop");
	}

	@Override
	public void buy(Player player, int slot, int id, int amount) {
		if (!hasItem(slot, id))
			return;
		if (get(slot).getAmount() == 0)
			return;
		if (amount > get(slot).getAmount()) {
			amount = get(slot).getAmount();
		}

		Item buying = new Item(id, amount);

		if (!player.getInventory().hasSpaceFor(buying)) {
			if (!buying.getDefinition().isStackable()) {
				int slots = player.getInventory().getFreeSlots();
				if (slots > 0) {
					buying.setAmount(slots);
					amount = slots;
				} else {
					player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to buy this item."));
				}
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to buy this item."));
				return;
			}
		}

		if (player.getInventory().getItemAmount(995) < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Gold Coins to buy that."));
			return;
		}

		player.getInventory().remove(995, amount * getPrice(id));

		player.getInventory().add(buying);
		update();
	}

	@Override
	public int getBuyPrice(int id) {
		return 0;
	}

	@Override
	public String getCurrencyName() {
		return "Coins";
	}

	@Override
	public int getSellPrice(int id) {
		return getPrice(id);
	}

	@Override
	public boolean sell(Player player, int id, int amount) {
		player.getClient().queueOutgoingPacket(new SendMessage("You cannot sell items to this shop."));
		return false;
	}
}

