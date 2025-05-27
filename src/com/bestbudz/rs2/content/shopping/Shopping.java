package com.bestbudz.rs2.content.shopping;

import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

public class Shopping {

	public static final int SHOP_INTERFACE_ID = 3900;
	public static final int BOX_INTERFACE_ID = 3823;
	private final Stoner stoner;
	private long shopId = -1L;
	private ShopType shopType = ShopType.DEFAULT;

	public Shopping(Stoner stoner) {
	this.stoner = stoner;
	}

	public void buy(int id, int amount, int slot) {
	if (shopId == -1L)
		return;
	Shop shop;
	if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
		shop = Shop.getShops()[((int) shopId)];
	} else {
		Stoner p = World.getStonerByName(shopId);

		if (p == null) {
			DialogueManager.sendStatement(p, "The shop owner is no longer online.");
			return;
		}

		shop = p.getStonerShop();
	}

	if (shop == null) {
		return;
	}

	shop.buy(stoner, slot, id, amount);
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3823, stoner.getBox().getItems()));
	}

	public void doShopPriceUpdate() {
	Item[] prices = new Item[Shop.SHOP_SIZE];
	Shop shop;

	if (shopId == -1) {
		return;
	}

	if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
		shop = Shop.getShops()[(int) shopId];

		for (int i = 0; i < prices.length; i++) {
			if (shop.get(i) != null) {
				prices[i] = new Item(0, shop.getSellPrice(shop.get(i).getId()));
				int currency = 0;
				if (shop != null && shop.getCurrencyName() != null) {
					switch (shop.getCurrencyName()) {
					case "Achievements points":
						currency = 10;
						break;
					case "Mage Arena points":
						currency = 9;
						break;
					case "Advance points":
						currency = 7;
						break;
					case "CannaCredits":
						currency = 6;
						break;
					case "Pest points":
						currency = 5;
						break;
					case "Mercenary points":
						currency = 4;
						break;
					case "Bounty points":
						currency = 3;
						break;
					case "Chill points":
						currency = 2;
						break;
					case "Tokkul":
						currency = 1;
						break;
					default:
						currency = 0;
						break;
					}
				}
				stoner.send(new SendString(shop.getSellPrice(shop.get(i).getId()) + "," + currency, 28000 + i));
			}
		}
	} else {
		Stoner owner = World.getStonerByName(shopId);

		if (owner == null) {
			return;
		}

		shop = owner.getStonerShop();

		for (int i = 0; i < prices.length; i++) {
			if (shop.get(i) != null) {
				int currency = 0;
				if (shop != null && shop.getCurrencyName() != null) {
					switch (shop.getCurrencyName()) {
					case "Achievements points":
						currency = 10;
						break;
					case "Mage Arena points":
						currency = 9;
						break;
					case "Advance points":
						currency = 7;
						break;
					case "CannaCredits":
						currency = 6;
						break;
					case "Pest points":
						currency = 5;
						break;
					case "Mercenary points":
						currency = 4;
						break;
					case "Bounty points":
						currency = 3;
						break;
					case "Chill points":
						currency = 2;
						break;
					case "Tokkul":
						currency = 1;
						break;
					default:
						currency = 0;
						break;
					}
				}
				stoner.send(new SendString(shop.getSellPrice(shop.get(i).getId()) + "," + currency, 28000 + i));
				prices[i] = new Item(0, shop.getSellPrice(shop.get(i).getId()));
			}
		}
	}
	}

	public long getShopId() {
	return shopId;
	}

	public void open(int id) {

	Shop shop = Shop.getShops()[id];

	if (shop == null) {
		return;
	}

	shopType = shop.getShopType();

	shopId = id;

	if (shopType == ShopType.INSTANCE) {
		shop.onOpen(stoner);
	}

	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3823, stoner.getBox().getItems()));
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3900, shop.getItems()));
	stoner.getClient().queueOutgoingPacket(new SendString(shop.getName(), 3901));
	stoner.getClient().queueOutgoingPacket(new SendBoxInterface(3824, 3822));

	doShopPriceUpdate();
	}

	public void open(Stoner owner) {

	shopType = ShopType.STONER;

	Shop shop = owner.getStonerShop();

	if (shop == null) {
		return;
	}

	shopId = owner.getUsernameToLong();

	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3823, stoner.getBox().getItems()));
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3900, shop.getItems()));
	stoner.getClient().queueOutgoingPacket(new SendString(owner.deterquarryIcon(owner) + shop.getName() + "'s shop ", 3901));
	stoner.getClient().queueOutgoingPacket(new SendBoxInterface(3824, 3822));

	doShopPriceUpdate();
	}

	public void reset() {
	shopId = -1L;
	}

	public void sell(int id, int amount, int slot) {
	if (shopId == -1L)
		return;
	Shop shop;
	if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
		shop = Shop.getShops()[((int) shopId)];
	} else {
		Stoner p = World.getStonerByName(shopId);

		if (p == null) {
			DialogueManager.sendStatement(p, "The shop owner is no longer online.");
			return;
		}

		shop = p.getStonerShop();
	}

	if (shop == null) {
		return;
	}

	if (shop.sell(stoner, id, amount)) {
		stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3823, stoner.getBox().getItems()));
	}
	}

	public void sendBuyPrice(int id) {
	if ((shopId == -1L) || (shopType == ShopType.STONER)) {
		return;
	}

	if (id == 995) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell bestbucks to a shop."));
		return;
	}

	if (!Item.getDefinition(id).isTradable()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item."));
		return;
	}

	Shop shop = Shop.getShops()[((int) shopId)];

	if ((shop != null) && (!shop.isGeneral()) && (!shop.isDefaultItem(id))) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item to this shop."));
		return;
	}

	int value = Shop.getShops()[((int) shopId)].getBuyPrice(id);
	String itemName = Item.getDefinition(id).getName();
	String price = "" + value;
	String bestbucks = "bestbucks";

	if ((value > 1000) && (value < 1000000))
		price = value / 1000 + "k (" + value + ")";
	else if (value >= 1000000)
		price = value / 1000000 + "m (" + value / 1000 + "k)";
	else if (value == 1) {
		price = "one";
	}

	stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] " + ShopConstants.COLOUR + itemName + "</col>: shop will buy for " + ShopConstants.COLOUR + price + "</col> " + bestbucks + "."));
	}

	public void sendSellPrice(int id) {
	if (shopId == -1L)
		return;
	int value;
	if (shopType == ShopType.STONER) {
		Stoner p = World.getStonerByName(shopId);

		if (p == null) {
			DialogueManager.sendStatement(p, "The shop owner is no longer online.");
			return;
		}

		value = p.getStonerShop().getSellPrice(id);
	} else {
		value = Shop.getShops()[((int) shopId)].getSellPrice(id);
	}

	String itemName = Item.getDefinition(id).getName();

	if (shopType != ShopType.STONER || shopType == ShopType.INSTANCE) {
		if (Shop.getShops()[((int) shopId)].getCurrencyName() == null) {
			String price = "" + value;
			String bestbucks = "bestbucks";

			if ((value > 1000) && (value < 1000000))
				price = value / 1000 + "k (" + value + ")";
			else if (value >= 1000000)
				price = value / 1000000 + "m (" + value / 1000 + "k)";
			else if (value == 1) {
				price = "one";
			}

			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] " + ShopConstants.COLOUR + itemName + "</col>: currently costs " + ShopConstants.COLOUR + price + "</col> " + bestbucks + "."));
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] " + ShopConstants.COLOUR + itemName + "</col>: currentky costs " + ShopConstants.COLOUR + value + "</col> " + Shop.getShops()[((int) shopId)].getCurrencyName() + "."));
		}
	} else {
		String price = "" + value;
		String bestbucks = "bestbucks";

		if ((value > 1000) && (value < 1000000))
			price = value / 1000 + "k (" + value + ")";
		else if (value >= 1000000)
			price = value / 1000000 + "m (" + value / 1000 + "k)";
		else if (value == 1) {
			price = "one";
		}

		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] " + ShopConstants.COLOUR + itemName + "</col>: currently costs " + ShopConstants.COLOUR + price + "</col> " + bestbucks + "."));
	}
	}

	public boolean shopping() {
	return shopId > -1L;
	}

	public void update() {
	if (!shopping())
		return;
	Shop shop;
	if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
		shop = Shop.getShops()[((int) shopId)];
	} else {
		Stoner p = World.getStonerByName(shopId);

		if (p == null) {
			DialogueManager.sendStatement(stoner, "This stoner is no longer online.");
			shopId = -1L;
			return;
		}

		shop = p.getStonerShop();
	}

	if (shop.isUpdate()) {
		stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3900, shop.getItems()));
		doShopPriceUpdate();
	}
	}

	public enum ShopType {
		DEFAULT,
		STONER,
		INSTANCE
	}
}
