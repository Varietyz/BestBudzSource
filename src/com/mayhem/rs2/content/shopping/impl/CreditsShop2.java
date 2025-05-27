package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for pest credits
 * 
 * @author Daniel
 */
public class CreditsShop2 extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 90;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	case 13173: // phat set
		return 13500;
	case 1038:
	case 1040:
	case 1044:
	case 1046:
	case 1048:
		return 3800;
	case 1042: // blue 
		return 4000; 
	case 11863: // rainbow phat
	case 11862: // black phat
		return 5000;
	case 13175: // ween set
		return 4000;
	case 1053: // green ween
	case 1055: // blue ween
		return 2500; 
	case 1057:
		return 2800; 
	case 11847:
		return 4000; 
	case 1050:
		return 2000; 
	case 12104:
	case 12093:
		return 3500;
	case 13343:
		return 3700; 
	case 4084:	
		return 3000; 
		
	case 1037:
	case 21214:
	case 4565: 
		return 2200;	
	case 1961: 
	case 1959:
		return 1500;
	case 981:
		return 1800;
		

	
		
	}

	return 1900;
}

/**
 * Items in shop
 */
public CreditsShop2() {
	super(SHOP_ID, new Item[] { 
			new Item(13173),
			new Item(1038), // red party hat
			new Item(1040),
			new Item(1042),
			new Item(1044),
			new Item(1046),
			new Item(1048),
			new Item(11863),
			new Item(11862),
			new Item(13175),
			new Item(1053),
			new Item(1055),
			new Item(1057), // red  ween
			new Item(11847), // black ween
			new Item(1050), // santa 
			new Item(12104), // lime santa 
			new Item(12093), // magenta santa 
			new Item(13343), // black santa 
			new Item(4084), // sled 
			new Item(1037), // bunny ears
			new Item(21214), // easter egg helm
			new Item(4565), // easter basket 
			new Item(1961), // easter egg 
			new Item(1959), // pumpkin 
			new Item(981), // disk of returning 
			new Item(7761), // toy soldier
			new Item(7765), // toy doll
			new Item(20834), //Sack of presents 
			new Item(20836), // Giant present 
			
			
			
	}, false, "FalconExpress Bucks Store");
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

		if (player.getCredits() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough FalconExpress Bucks to buy that."));
			return;
		}

		player.setCredits(player.getCredits() - (amount * getPrice(id)));

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
		return "Credits";
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
