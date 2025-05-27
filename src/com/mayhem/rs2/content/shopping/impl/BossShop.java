package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Boss store
 * 
 * @author Dez
 */
public class BossShop extends Shop {

	/**
	 * Id of Boss shop
	 */
	public static final int SHOP_ID = 349;

	/**
	 * Price of items in Boss Skill store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 6570: // fire cape
		return 50;
	
	case 11772: // warrior ring(i)
	case 11773: // berserker ring(i)
	case 11770: // seers ring(i)
	case 11771: // archer ring(i)
	case 6199: // mystery box
		return 100;
		
	case 21295: // infernal cape
		return 200;
		
	case 12006: // Abyssal tentacle
		return 250;
		
	case 290:
		return 300; // legendary mystery box
		
	case 11826:
		return 375; // armadyl helm
		
	case 11804: // bandos gs
	case 11283: // dfs
		return 400;
		
	case 11808: // zammy gs
		return 425;
		
	case 11806: // sara gs
	case 12931: // serpentine helm
		return 500;
		
	case 11802: // armadyl gs
	case 11832: // bandos chestplate
	case 11834: // bandos tassets
	case 11828: // armadyl chestplate
	case 11830: // armadyl chainskirt
		return 700;
		
	case 20784: // d claws
		return 525;
		
	case 11785: // aramadyl cbow
	case 12785: // row(i)
		return 600;
		
	case 12817:
		return 2000; // elysian spirit shield
		
	case 12108: //twisted bow
		return 3000;
		
		
		
	
		

	}
	return 2147483647;
}

/**
 * All items in hunter
 */
public BossShop() {
	super(SHOP_ID, new Item[] {
			
			new Item (11772),
			new Item (11773),
			new Item (11770),
			new Item (11771),
			new Item (12006),
			new Item (11808),
			new Item (11806),
			new Item (11802),
			new Item (20784),
			new Item (11785),
			new Item (12108),
			new Item (12931),
			new Item (11832),
			new Item (11834),
			new Item (11826),
			new Item (11828),
			new Item (11830),
			new Item (6570),
			new Item (21295),
			new Item (12785),
			new Item (11283),
			new Item (12817),
			new Item (6199),
			new Item (290),
			
			
			
			
			
			
			
			
			
			
			
			
			}, 
			false, "Boss Point Shop");
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

		if (player.getbossPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Boss points to buy that."));
			return;
		}

		player.setbossPoints(player.getbossPoints() - amount * getPrice(id));

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
		return "Boss Points";
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
