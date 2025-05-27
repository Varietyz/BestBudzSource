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
public class CreditsShop3 extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 87;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	case 6199:
		return 300;
	case 290:
		return 700;
	case 11996:
		return 1500;
	case 13346: // guarantee rare 
		return 3000;
		
	case 12856:
	case 12855:
		return 1000;
		
	case 13069:
	case 10559:
	case 10557:
	case 10556:
	case 10558:
		return 2000;
		
	case 13071:
	case 10998:
	case 964:
	case 771:
	case 5076:
	case 5077:
		return 1000;
		
		
	}

	return 200;
}

/**
 * Items in shop
 */
public CreditsShop3() {
	super(SHOP_ID, new Item[] {
		new Item(6199), //mystery box 
		new Item(290), // Super mystery box 
		new Item(11996), // Elite mystery box 
		new Item(13346), // Platinum mystery box 
		new Item(3849), // thieving casket 
		new Item(12897), // box of bones 
		new Item(7956), // fishing casket 
		new Item(19836), // firemaking casket 
		
		new Item(12856), // rogue's revenge 
		new Item(12855), // hunter honour 
		
		new Item(13069), // member cape
		
		new Item(10559), // healer icon
		new Item(10557), // collector icon 
		new Item(10556), // attacker icon
		new Item(10558), // defender icon 
		
		new Item(13071), // chompy pet 
		new Item(10998), // hell goblin pet
		new Item(964), // spiritual follower (pet)
		new Item(771), // tree spirit pet 
		new Item(5076), // black swan 
		new Item(5077), // white swan 
		
		
			
			
			
		
			
	}, false, "FalconExpress Bucks Store 3");
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
