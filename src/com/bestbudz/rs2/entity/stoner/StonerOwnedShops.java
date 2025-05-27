package com.bestbudz.rs2.entity.stoner;

import java.util.ArrayList;
import java.util.List;

import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.logger.StonerLogger;
import com.bestbudz.rs2.content.dialogue.impl.ConfirmDialogue;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.content.shopping.ShopConstants;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterXInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

/**
 * Stoner owned shop exchange
 * 
 * @author JayBane
 *
 */
public class StonerOwnedShops extends Shop {

	/**
	 * Stoner owned shops
	 * 
	 * @param owner
	 */
	public StonerOwnedShops(Stoner owner) {
	super(new Item[40], owner.getUsername());
	this.owner = owner;
	}

	/**
	 * Stoner
	 */
	private final Stoner owner;

	/**
	 * The shop tax
	 */
	public static int TAX = 3;

	/**
	 * If system should be disabled
	 */
	public static boolean disabled = false;

	/**
	 * Key
	 */
	public static final String ADDING_ITEM_KEY = "addingitemkey";

	/**
	 * Item prices
	 */
	private int[] prices = new int[36];

	public int[] getPrices() {
	return prices;
	}

	public void setPrices(int[] prices) {
	this.prices = prices;
	}

	/**
	 * Searching
	 */
	private String search = null;

	public void setSearch(String search) {
	this.search = search;
	}

	public boolean hasSearch() {
	return search != null;
	}

	public void resetSearch() {
	search = null;
	}

	/**
	 * Buys item from stoner's shop
	 * 
	 * @param stoner
	 * @param slot
	 * @param id
	 * @param amount
	 */
	@Override
	public void buy(Stoner stoner, final int slot, final int id, int amount) {
	if (stoner.getTrade().trading() || stoner.getDueling().isDueling() || stoner.getCombat().inCombat() || stoner.inWGGame()) {
		return;
	}
	if (disabled) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] Stoner owned shopping is disabled at the moment."));
		return;
	}
	if (!owner.isActive()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] Stoner is currently offline."));
		stoner.send(new SendRemoveInterfaces());
		return;
	}
	if ((!Item.getDefinition(id).isTradable()) && (!stoner.equals(owner))) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot buy this item."));
		return;
	}
	if (!hasItem(slot, id))
		return;
	if (get(slot).getAmount() == 0)
		return;
	if (amount > get(slot).getAmount()) {
		amount = get(slot).getAmount();
	}
	if (!stoner.getBox().hasSpaceFor(new Item(id, amount))) {
		if (Item.getDefinition(id).isStackable()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough box space to buy this item."));
			return;
		}
		int slots = stoner.getBox().getFreeSlots();
		if (slots > 0) {
			amount = slots;
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough box space to buy this item."));
			return;
		}
	}
	final Item buying = new Item(id, amount);
	if (stoner.equals(owner)) {
		remove(new Item(buying));
		stoner.getBox().add(buying);
		update();
		return;
	}
	final int price = getSellPrice(id);
	final Item gold = new Item(995, getSellPrice(id) * amount);
	final int goldAmount = prices[slot] * amount;
	stoner.start(new ConfirmDialogue(stoner, new String[] { "Confirm purchase for item:", Item.getDefinition(id).getName() + " x " + amount, "For: " + Utility.formatBestBucks(price) + " each?" }) {
		@Override
		public void onConfirm() {
		if (!stoner.isActive() || stoner == null) {
			return;
		}
		if (!owner.isActive()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] Stoner is currently offline."));
			stoner.send(new SendRemoveInterfaces());
			return;
		}
		if ((getItems()[slot] == null) || (getItems()[slot].getId() != id) || (getSellPrice(id) != price)) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] Purchase failed, the offer may have changed or the price may have been altered."));
			stoner.getShopping().open(owner);
			return;
		}

		if (!stoner.getBox().hasItemAmount(new Item(gold))) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough BestBucks to make this purchase."));
			return;
		}
		if (stoner.getShopCollection() >= Integer.MAX_VALUE) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] That stoner has too much gold already!"));
			return;
		}
		remove(new Item(buying));
		stoner.getBox().remove(new Item(gold), false);
		stoner.getBox().add(buying);
		gold.setAmount(goldAmount);
		owner.setShopCollection(owner.getShopCollection() + gold.getAmount());
		owner.getClient().queueOutgoingPacket(new SendMessage("<col=0C8214>Shop Exchange: Finished Selling " + buying.getAmount() + " x " + Utility.formatStonerName(buying.getDefinition().getName()) + " for " + Utility.format(gold.getAmount()) + " bestbucks."));
		owner.getClient().queueOutgoingPacket(new SendSound(1457, 0, 0));
		StonerLogger.STONER_SHOPS_LOGGER.log(owner.getUsername(), String.format("%s has sold %s %s for %sgp to %s.", Utility.formatStonerName(stoner.getUsername()), buying.getAmount(), Utility.formatStonerName(buying.getDefinition().getName()), goldAmount, Utility.formatStonerName(owner.getUsername())));
		StonerLogger.STONER_SHOPS_LOGGER.log(stoner.getUsername(), String.format("%s has bought %s %s for %sgp from %s.", Utility.formatStonerName(owner.getUsername()), buying.getAmount(), Utility.formatStonerName(buying.getDefinition().getName()), goldAmount, Utility.formatStonerName(stoner.getUsername())));
		update();
		stoner.getShopping().open(owner);
		}
	});
	}

	@Override
	public void shift(int slot) {
	if ((slot > getSize()) || (slot < 0)) {
		return;
	}

	for (int i = slot + 1; i < getSize(); i++) {
		if (items[i] == null) {
			break;
		}
		items[(i - 1)] = items[i];
		prices[(i - 1)] = prices[i];
		items[i] = null;
		prices[i] = 0;
	}
	}

	@Override
	public void shift(int start, int end) {
	List<ShopItem> all = new ArrayList<ShopItem>();

	for (int i = start; i <= end; i++) {
		if (items[i] != null) {
			all.add(new ShopItem(items[i], prices[i]));
			items[i] = null;
			prices[i] = 0;
		}

	}

	int index = start;
	for (ShopItem i : all) {
		items[index] = i.getItem();
		prices[index] = i.getPrice();
		index++;
	}
	}

	@Override
	public void shift() {
	List<ShopItem> all = new ArrayList<ShopItem>();

	for (int i = 0; i < getSize(); i++) {
		if (items[i] != null) {
			all.add(new ShopItem(items[i], prices[i]));
		}
	}

	items = new Item[getSize()];
	prices = new int[getSize()];

	int index = 0;
	for (ShopItem i : all) {
		items[index] = i.getItem();
		prices[index] = i.getPrice();
		index++;
	}
	}

	static class ShopItem {
		private final Item item;
		private final int price;

		public ShopItem(Item item, int price) {
		this.item = item;
		this.price = price;
		}

		public Item getItem() {
		return item;
		}

		public int getPrice() {
		return price;
		}
	}

	/**
	 * Search all available shops
	 */
	public void doSearch() {
	int c = 0;
	for (int i = 53516; i < 53716; i++) {
		owner.getClient().queueOutgoingPacket(new SendString("", i));
	}
	for (int i = 53716; i < 53916; i++) {
		owner.getClient().queueOutgoingPacket(new SendString("", i));
	}
	for (Stoner p : World.getStoners()) {
		if ((p != null) && (p.isActive()) && (p.getStonerShop().hasItemWithText(search))) {
			if (53516 + c == 53716) {
				break;
			}
			owner.getClient().queueOutgoingPacket(new SendString("Shops Exchange | @gre@" + (c + 1) + "</col> Active " + ((c + 1) == 1 ? "Shop" : "Shops"), 53505));

			String color = "";
			if (p.getShopColor() == null) {
				color = "</col>" + p.deterquarryIcon(p);
			} else {
				color = p.getShopColor();
			}

			owner.getClient().queueOutgoingPacket(new SendString(p.deterquarryIcon(p) + p.getUsername(), 53516 + c));
			if (owner.getShopMotto() != null) {
				owner.getClient().queueOutgoingPacket(new SendString(color + p.getShopMotto(), 53516 + 200 + c));
			} else {
				owner.getClient().queueOutgoingPacket(new SendString(color + "No shop description set! ", 53516 + 200 + c));
			}

			c++;
		}
	}
	owner.getClient().queueOutgoingPacket(new SendInterface(53500));
	}

	/**
	 * Set price
	 * 
	 * @param stoner
	 * @param price
	 */
	public void onSetPrice(Stoner stoner, int price) {
	if (disabled) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] Stoner owned shopping is disabled at the moment."));
		return;
	}
	if (price < 1) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You must enter a positive price for this item."));
		return;
	}
	if (stoner.getAttributes().get("addingitemkey") == null) {
		return;
	}
	final Item item = (Item) stoner.getAttributes().get("addingitemkey");
	final int setPrice = price;

	if (item == null) {
		return;
	}
	if (stoner.getAttributes().get("addingitemkey") == null) {
		return;
	}
	int invAmount = stoner.getBox().getItemAmount(item.getId());
	if (invAmount == 0)
		return;
	if (item.getAmount() > invAmount) {
		item.setAmount(invAmount);
	}
	add(new Item(item));
	stoner.getBox().remove(new Item(item));
	if (setPrice + (int) (setPrice * 0.01D) < 0) {
		prices[getItemSlot(item.getId())] = setPrice;
	} else {
		prices[getItemSlot(item.getId())] = setPrice;
	}
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3900, getItems()));
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3823, stoner.getBox().getItems()));
	update();
	stoner.getShopping().open(stoner);
	}

	/**
	 * Sells item
	 */
	@Override
	public boolean sell(Stoner stoner, int id, int amount) {
	if (disabled) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] Stoner owned shopping is disabled at the moment."));
		return false;
	}
	if (!stoner.equals(owner)) {
		return false;
	}
	if ((!Item.getDefinition(id).isTradable()) || (id == 995)) {
		owner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item."));
		return false;
	}
	if (((getItemCount() == 16)) || ((getItemCount() == 32))) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot put anymore items into your shop."));
		return false;
	}
	int invAmount = stoner.getBox().getItemAmount(id);
	if (invAmount == 0)
		return false;
	if (amount > invAmount) {
		amount = invAmount;
	}
	stoner.getAttributes().set("addingitemkey", new Item(id, amount));
	stoner.getClient().queueOutgoingPacket(new SendEnterXInterface(15460, 0));
	return true;
	}

	/**
	 * Refresh the containers
	 */
	@Override
	public void refreshContainers() {
	}

	/**
	 * Checks if is default item
	 */
	@Override
	public boolean isDefaultItem(int id) {
	return false;
	}

	/**
	 * Gets item count
	 * 
	 * @return
	 */
	public int getItemCount() {
	int c = 0;
	for (Item i : getItems()) {
		if (i != null) {
			c++;
		}
	}
	return c;
	}

	/**
	 * Gets shop selling price
	 */
	@Override
	public int getSellPrice(int id) {
	int slot = getItemSlot(id);

	if (slot == -1) {
		return 0;
	}

	return prices[slot];
	}

	/**
	 * Checks if has item
	 * 
	 * @return
	 */
	public boolean hasAnyItems() {
	for (Item i : getItems()) {
		if (i != null) {
			return true;
		}
	}

	return false;
	}

	/**
	 * Checks if has item with text
	 * 
	 * @param text
	 * @return
	 */
	public boolean hasItemWithText(String text) {
	for (Item i : getItems()) {
		if ((i != null) && (i.getDefinition().getName().toLowerCase().contains(text.toLowerCase()))) {
			return true;
		}
	}

	return false;
	}

}
