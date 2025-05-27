package com.bestbudz.rs2.content.shopping;

import com.bestbudz.rs2.content.dialogue.DialogueManager;
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

	public long getShopId() {
		return shopId;
	}

	public void open(int id) {
		Shop shop = Shop.getShops()[id];
		if (shop == null) return;

		shopType = shop.getShopType();
		shopId = id;

		if (shopType == ShopType.INSTANCE) {
			shop.onOpen(stoner);
		}

		stoner.getClient().queueOutgoingPacket(new SendUpdateItems(BOX_INTERFACE_ID, stoner.getBox().getItems()));
		stoner.getClient().queueOutgoingPacket(new SendUpdateItems(SHOP_INTERFACE_ID, shop.getItems()));
		stoner.getClient().queueOutgoingPacket(new SendString(shop.getName(), 3901));
		stoner.getClient().queueOutgoingPacket(new SendBoxInterface(3824, 3822));

		doShopPriceUpdate();
	}

	public void buy(int id, int amount, int slot) {
		if (shopId == -1L) return;
		Shop shop = Shop.getShops()[(int) shopId];
		if (shop == null) return;

		shop.buy(stoner, slot, id, amount);
		stoner.getClient().queueOutgoingPacket(new SendUpdateItems(BOX_INTERFACE_ID, stoner.getBox().getItems()));
	}

	public void sell(int id, int amount, int slot) {
		if (shopId == -1L) return;
		Shop shop = Shop.getShops()[(int) shopId];
		if (shop == null) return;

		if (shop.sell(stoner, id, amount)) {
			stoner.getClient().queueOutgoingPacket(new SendUpdateItems(BOX_INTERFACE_ID, stoner.getBox().getItems()));
		}
	}

	public void sendBuyPrice(int id) {
		if (shopId == -1L) return;
		if (id == 995 || !Item.getDefinition(id).isTradable()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot sell this item."));
			return;
		}
		Shop shop = Shop.getShops()[(int) shopId];
		if (!shop.isGeneral() && !shop.isDefaultItem(id)) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot sell this item to this shop."));
			return;
		}

		int value = shop.getBuyPrice(id);
		String price = formatPrice(value);
		stoner.getClient().queueOutgoingPacket(new SendMessage(Item.getDefinition(id).getName() + ": shop will buy for " + price + " bestbucks."));
	}

	public void sendSellPrice(int id) {
		if (shopId == -1L) return;
		Shop shop = Shop.getShops()[(int) shopId];
		int value = shop.getSellPrice(id);
		String currency = shop.getCurrencyName() != null ? shop.getCurrencyName() : "bestbucks";
		String price = formatPrice(value);
		stoner.getClient().queueOutgoingPacket(new SendMessage(Item.getDefinition(id).getName() + ": currently costs " + price + " " + currency + "."));
	}

	private String formatPrice(int value) {
		if (value >= 1000000) return value / 1000000 + "m (" + value / 1000 + "k)";
		if (value >= 1000) return value / 1000 + "k (" + value + ")";
		if (value == 1) return "one";
		return "" + value;
	}

	public void doShopPriceUpdate() {
		Item[] prices = new Item[Shop.SHOP_SIZE];
		if (shopId == -1L) return;

		Shop shop = Shop.getShops()[(int) shopId];
		if (shop == null) return;

		for (int i = 0; i < prices.length; i++) {
			if (shop.get(i) != null) {
				int price = shop.getSellPrice(shop.get(i).getId());
				int currency = resolveCurrency(shop.getCurrencyName());
				stoner.send(new SendString(price + "," + currency, 28000 + i));
			}
		}
	}

	private int resolveCurrency(String name) {
		if (name == null) return 0;
		switch (name) {
			case "Tokkul": return 1;
			case "Chill points": return 2;
			case "Bounty points": return 3;
			case "Mercenary points": return 4;
			case "Pest points": return 5;
			case "CannaCredits": return 6;
			case "Advance points": return 7;
			case "Mage Arena points": return 9;
			case "Achievements points": return 10;
			default: return 0;
		}
	}

	public void update() {
		if (!shopping()) return;
		Shop shop = Shop.getShops()[(int) shopId];
		if (shop != null && shop.isUpdate()) {
			stoner.getClient().queueOutgoingPacket(new SendUpdateItems(SHOP_INTERFACE_ID, shop.getItems()));
			doShopPriceUpdate();
		}
	}

	public void reset() {
		shopId = -1L;
	}

	public boolean shopping() {
		return shopId > -1L;
	}

	public enum ShopType {
		DEFAULT,
		INSTANCE
	}
}
