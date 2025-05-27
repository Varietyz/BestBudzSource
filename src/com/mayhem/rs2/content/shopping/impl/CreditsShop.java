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
public class CreditsShop extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 94;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	/**
	case 11235:
	case 11838:
	case 6585:	
		return 500;
	*/
	
	
	case 12809:
	case 12006:
		return 1300;
	
	case 12926:
		return 1700;
	case 13271:
		return 2000;
		
	case 13263:
	case 11802:
	case 20784:
		return 2700;
		
	case 20997:
	case 22325:
		return 6500;
	
	/**
	case 11926:
	case 11924:	
	case 11283:
		return 1000;
	*/	
		
	/**	
	case 11759:
		return 1400;
	
	case 21633:
	case 21695:
		return 2000;
	*/
		
		
	case 12931:
		return 1700;
		
	case 13197:
	case 13199:
		return 2000;
		
	case 12002:
	case 12853:
	case 19553:
	case 19547:
	case 12785:
	case 12692:
	case 12691:
	case 13202:
	case 19710:
		return 1800;
		
	case 21018:
	case 21021:
	case 21024:
	case 22326:
	case 22327:
	case 22328:
		return 1000;
		
	case 14998:
	case 14997:
	case 14999:
	case 14991:
	case 14992:
	case 14993:
	case 14994:
	case 14995:
	case 14996:
		return 1500;
	
	}

	return 700;
}

/**
 * Items in shop
 */
public CreditsShop() {
	super(SHOP_ID, new Item[] { 
			
			
			
			/**
			new Item(11235), // dark bow
			new Item(11838), // saradomin sword
			new Item(4151), // abyssal whip 
			new Item(11889), // Zammy Hasta
			*/
			
			new Item(12809), // Saradomin Blessed Sword 
			new Item(12006), // abyssal tentacle /
			new Item(12926), // blowpipe full 
			new Item(13271), //abyssal dagger p++
			new Item(13263), // abyssal bludgeon 
			new Item(11802), // Armadyl godsword
			new Item(20784), // Dragon Claws
			new Item(20997), // Twisted Bow
			new Item(22325), // Scythe of Vitur
			
			/**
			new Item(11926), // Odium Ward
			new Item(11924), // Malediction ward 
			new Item(11283), // Dragonfire Shield 
			*/
	
			/**
			new Item(11759), // New Crystal Shield (i)
			new Item(21633), // Ancient Wyvern Shield 
			new Item(21695), // Runefest Shield 
			*/
			
			new Item(12931), // serpentine helm
			new Item(13197), // tanzanite helm
			new Item(13199), // magma helm 
			
			new Item(12002), // Occult necklace 
			new Item(12853), // amulet of the damned 
			new Item(19553), // amulet of torture 
			new Item(19547), // Necklace of anguish 
			
			new Item(12785), // Ring of wealth (i) 
			new Item(12692), // Treasonous ring (i)
			new Item(12691), // Tyrannical ring (i)
			new Item(13202), // Ring of the gods (i)
			new Item(19710), // Ring of suffering (i)
			
			
			/** Too many items 
			new Item(12883), // karil set
			new Item(12881), // ahrim set
			new Item(12877), // dharok set
			new Item(12879), // torag set
			new Item(12873), // guthan set
			new Item(12875), // verac set
			*/
			
			new Item(21018), // Ancestral hat 
			new Item(21021), // Ancestral robe top
			new Item(21024), // Ancestral robe bottom 
			new Item(22326), // Justicar faceguard 
			new Item(22327), // Justicar chestguard 
			new Item(22328), // Justicar legguard 
			
			new Item(14998), // torva helm
			new Item(14997), // torva platebody
			new Item(14999), // torva platelegs
			
			new Item(14991), //pernix cowl
			new Item(14992), // pernix top
			new Item(14993), // pernix chaps
			
			
			new Item(14994), // virtus hood
			new Item(14995), // virtus top
			new Item(14996), // virtus bottom 
			
			
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
