package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Voting store
 * 
 * @author Daniel
 */
public class VotingShop extends Shop {

	/**
	 * Id of Bounty shop
	 */
	public static final int SHOP_ID = 92;

	/**
	 * Price of items in Bounty store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 11730:
		return 1;
	
	case 989:
		return 3; // cost per vote point 
	
	case 6199:
		return 21; 
	
	case 290:
		return 36;  
	
	case 11789:
	case 4151: 
	case 11235: 
		return 9;
		
	case 11891:
	case 11892:	
		return 28;
	
	case 4071:
	case 4069:
	case 4070:
	
	case 4506:
	case 4504:
	case 4505:
	
	case 4511:
	case 4509:
	case 4510:
		return 25;
		
	case 12061:
	case 12091:
	case 12031:
		return 200;

	case 12785:
		return 50;	
		
	case 2572:
		return 9;
	case 11770:
	case 11771:
	case 11772:
	case 11773:
		return 15;
		
			 
		
			
	}
	return 12; // default value 
}

/**
 * All items in Bounty store
 */
public VotingShop() {
	super(SHOP_ID, new Item[] {
			
			new Item(11730, 25), // overload(needs fix: Timer and Damage by 50 points and Stat Buff)
			new Item(989, 25), // crystal key
			new Item(6199), // mystery box 
			new Item(290), // Super Mystery Box 
			new Item(12759), // green dark bow paint
			new Item(12761), // yellow dark bow paint
			new Item(12763), // white dark bow paint
			new Item(12757), // blue dark bow paint
			new Item(12771), // volcanic whip paint
			new Item(12769), // frozen whip paint
			new Item(12798), // mystic battle staff upgrade mix
			new Item(11789), // mystic battle staff
			new Item(4151),  // whip 
			new Item(11235), // dark bow
			new Item(2581), // robin hood hat 
			new Item(2577), // ranger boots 
			new Item(11891), // Saradomin Banner 
			new Item(11892), // Zamorak Banner 
			
			new Item(4071), // red Decorative helm (melee)
			new Item(4069), // red Decorative armor 
			new Item(4070), // red Decorative legs 
	
			new Item(4506), // white Decorative helm (mage bonus)
			new Item(4504), // white Decorative armor
			new Item(4505), // white Decorative legs
			
			
			new Item(4511), // green Decorative helm (range bonus) 
			new Item(4509), // green Decorative armor
			new Item(4510), // green Decorative legs 
			
			new Item(12061), // Dark Green H'ween 
			new Item(12091), // Dark Green Santa
			new Item(12031), // Dark Green Partyhat 
			new Item(12785), // ring of wealth (i) 
			new Item(2572), // ring of wealth
			new Item(11770), // seers ring (i)
			new Item(11771), // archer ring (i) 
			new Item(11772), // warrior ring (i) 
			new Item(11773), // ring of wealth (i) 
			
			
			
			
 			
			
			
		
			
			
	}, false, "Vote Point Store");
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

		if (player.getVotePoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Vote points to buy that."));
			return;
		}

		player.setVotePoints(player.getVotePoints() - amount * getPrice(id));

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
		return "Vote points";
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
