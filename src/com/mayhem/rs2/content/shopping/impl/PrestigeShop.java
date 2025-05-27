package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Prestige store
 * 
 * @author Daniel
 */
public class PrestigeShop extends Shop {

	/**
	 * Id of Bounty shop
	 */
	public static final int SHOP_ID = 93;

	/**
	 * Price of items in Bounty store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		
		case 12526:
		case 20062:
		case 6199:
		case 20065:
		case 22246:
			return 5;
			
		case 18834: // pet Karamel
		case 18835: // pet Agrith Na-Na
		case 18836: // pet Dessourt
		case 18837: // pet Culinomancer
		case 12816: // pet dark-core
			return 10;
			
		case 19553:
		
		case 12002:
		case 19547:
			return 15;
			
		case 4224:
		case 4212:
		case 6585:
		case 13091:
			return 20;
			
		case 13192:
			return 25;
			
		case 4151:
			return 30;
			
		case 13195:
			return 100;
		
		case 5444:
			return 500;
			
		}
		return 15;
	}

	/**
	 * All items in Bounty store
	 */
	public PrestigeShop() {
		super(SHOP_ID, new Item[] {
				
				new Item(6199), // mysterybox
				new Item(18834), // pet Karamel
				new Item(18835), // pet Agrith Na-Na
				new Item(18836), // pet Dessourt
				new Item(18837), // Culinomancer
				new Item(12816), // pet dark-core
				new Item(13192), // $5 bond
				new Item(13195), // $25 bond
				new Item(5444), // $100 bond
				new Item(12526), // fury oranment kit	
				new Item(20062), // torture kit
				new Item(20065), // occult kit
				new Item(22246), // anguish kit
				new Item(19553), // torture amulet
				new Item(12002), // occult amulet
				new Item(19547), // anguish amulet
				new Item(13091),
				new Item(4224),
				new Item(4212),
				
		}, false, "Prestige Store");
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

		if (player.getPrestigePoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Prestige points to buy that."));
			return;
		}

		player.setPrestigePoints(player.getPrestigePoints() - amount * getPrice(id));

		//InterfaceHandler.writeText(new QuestTab(player));

		player.getInventory().add(buying);
		update();
	}

	@Override
	public int getBuyPrice(int id) {
		return 0;
	}

	@Override
	public String getCurrencyName() {
		return "Prestige points";
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
