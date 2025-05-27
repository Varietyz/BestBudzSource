package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Blood MOney currency
 * 
 * @author Divine
 */
public class BloodMoneyShop extends Shop {
	
	/**
	 * Item id of Blood money
	 */
	public static final int BLOODMONEY = 13307;
	
	/**
	 * Id of blood money store
	 */
	public static final int SHOP_ID = 102;

	/**
	 * Prices of items in store
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		
		case 989:
			return 15000;
		case 5030:
			return 1200000;
		case 12883:
		case 12881:
		case 12879:
		case 12877:
		case 12875:
		case 12873:
			return 190000;
		case 12785:
			return 700000;
		case 2581:
		case 19994:
		case 2577:
			return 200000;
		case 12596:
			return 400000;
		case 21793: 
		case 21795:
		case 21791:
			return 100000;
		case 20128:
		case 20134:
		case 20140:
			return 200000;
		case 20131:
		case 20137:
			return 400000;
		case 6199:
			return 50000;
		
	
			
			
		
		}

		return 700;
	}

	/**
	 * Items in store
	 */
	public BloodMoneyShop() {
		super(SHOP_ID, new Item[] { 
				new Item(11943, 27), // Lava Bones 
				new Item(11936, 27), // Dark Crab
				new Item (989, 27), // Crystal Key 
				new Item(5030), // Water spirit shield 
				new Item(12883), // Karil Set
				new Item(12881), // Ahrim Set 
				new Item(12879), // Torag Set 
				new Item(12877), // Dharok Set
				new Item(12875), // Verac Set
				new Item(12873), //Guthan Set 
				new Item(12785), //Ring of Wealth (i) 
				new Item(2581), //Ranger Hat
				new Item(19994), // Ranger Gloves
				new Item(2577), //Ranger Boots
				new Item(12596), // Ranger Tunic 
				new Item(21793), // Guthix Imbued Cape 
				new Item(21795), // Zamorak Imbued Cape 
				new Item(21791), // Saradomin Imbued Cape 
				new Item(20128), // Hood Darkness 
				new Item(20134), // Gloves Darkness 
				new Item(20140), // Boots Darkness 
				new Item(20131), // Top Darkness 
				new Item(20137), // Bottom Darkness 
				new Item(6199, 10), // Mystery Box 
				
				
				
				
				
				
				
				
			}, false, "Blood Money Shop");
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

		if (player.getInventory().getItemAmount(13307) < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Blood money to buy that."));
			return;
		}

		player.getInventory().remove(13307, amount * getPrice(id));

		player.getInventory().add(buying);
		update();
	}

	@Override
	public int getBuyPrice(int id) {
		return 0;
	}

	@Override
	public String getCurrencyName() {
		return "Blood Money";
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

