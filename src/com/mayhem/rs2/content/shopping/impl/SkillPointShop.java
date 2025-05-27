package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Skill point store
 * 
 * @author Divine
 */
public class SkillPointShop extends Shop {

	/**
	 * Id of Skill point shop
	 */
	public static final int SHOP_ID = 103;

	/**
	 * Price of items in Skill point store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	case 6199:
	case 12897:
		return 35000;
	case 13227:
	case 13229:
	case 13231:
		return 200000;
	case 12637:
	case 12639:
	case 12638:
		return 300000;
		
	case 21031:
	case 13241:
	case 13243:
		return 330000;
	case 20011:
	case 20014:
		return 450000;
	case 1632:
	case 220: 
		return 300;
	
		

	}
	return 12000;
}

/**
 * All items in skill point shop
 */
public SkillPointShop() {
	super(SHOP_ID, new Item[] { 
			
			new Item(6199, 25), // Mystery Box (normal)
			new Item(12897, 25), // Box of Bones 
			new Item(11738, 25), // Herb box
			new Item(11739, 25), // Cannabis box 
			new Item(10025, 25), // Magic box (gives runes)
			new Item(13227), // Eternal Crystal 
			new Item(13229), // Pegasian Crystal 
			new Item(13231), // Promordial Crystal
			new Item(12637), // Saradomin Halo 
			new Item(12639), // Guthix Halo 
			new Item(12638), // Zamorak Halo
			new Item(21031), // Infernal Harpoon
			new Item(13241), // Infernal Axe
			new Item(13243), // Infernal Pickaxe
			new Item(20011), // 3rd age axe
			new Item(20014), // 3rd age pickaxe 
			new Item(1632,5000), // Uncut Dragonstone 
			new Item(220, 5000),//Torstol Seed
		
			}, 
			false, "Skill point Shop");
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

		if (player.getskillPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Skill points to buy that."));
			return;
		}

		player.setskillPoints(player.getskillPoints() - amount * getPrice(id));

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
		return "Skill Points";
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
