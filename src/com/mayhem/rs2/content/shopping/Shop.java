package com.mayhem.rs2.content.shopping;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.shopping.Shopping.ShopType;
import com.mayhem.rs2.content.shopping.impl.AchievementShop;
import com.mayhem.rs2.content.shopping.impl.AgilityShop;
import com.mayhem.rs2.content.shopping.impl.BloodMoneyShop;
import com.mayhem.rs2.content.shopping.impl.SkillPointShop;
import com.mayhem.rs2.content.shopping.impl.SkillPointShop2;
import com.mayhem.rs2.content.shopping.impl.BountyShop;
import com.mayhem.rs2.content.shopping.impl.ClueShop;
import com.mayhem.rs2.content.shopping.impl.CreditsShop;
import com.mayhem.rs2.content.shopping.impl.CreditsShop2;
import com.mayhem.rs2.content.shopping.impl.CreditsShop3;
import com.mayhem.rs2.content.shopping.impl.GracefulShop;
import com.mayhem.rs2.content.shopping.impl.MageArenaShop;
import com.mayhem.rs2.content.shopping.impl.PestShop;
import com.mayhem.rs2.content.shopping.impl.PrestigeShop;
import com.mayhem.rs2.content.shopping.impl.SkillcapeShop;
import com.mayhem.rs2.content.shopping.impl.SlayerShop;
import com.mayhem.rs2.content.shopping.impl.TokkulShop;
import com.mayhem.rs2.content.shopping.impl.VotingShop;
import com.mayhem.rs2.content.skill.summoning.Pouch;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.ItemContainer;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
import com.mayhem.rs2.content.shopping.impl.BossShop;
import com.mayhem.rs2.content.shopping.impl.MiningSkillShop;
import com.mayhem.rs2.content.shopping.impl.PvmShop;
import com.mayhem.rs2.content.shopping.impl.PlatinumShop;
import com.mayhem.rs2.content.shopping.impl.RunecraftingSkillShop;
import com.mayhem.rs2.content.shopping.impl.SmithingSkillShop;
import com.mayhem.rs2.content.shopping.impl.ThievingSkillShop;
import com.mayhem.rs2.content.shopping.impl.TriviaShop;
import com.mayhem.rs2.content.shopping.impl.WoodcuttingSkillShop;
import com.mayhem.rs2.content.shopping.impl.PrayerSkillShop;
import com.mayhem.rs2.content.shopping.impl.HerbloreSkillShop;
import com.mayhem.rs2.content.shopping.impl.Hunterskillshop;
import com.mayhem.rs2.content.shopping.impl.FarmingSkillShop;
import com.mayhem.rs2.content.shopping.impl.FiremakingSkillShop;
import com.mayhem.rs2.content.shopping.impl.FishingSkillShop;
import com.mayhem.rs2.content.shopping.impl.FletchingSkillShop;
import com.mayhem.rs2.content.shopping.impl.CookingSkillShop;
import com.mayhem.rs2.content.shopping.impl.CraftingSkillShop;
import com.mayhem.rs2.content.shopping.impl.CustomShop;
import com.mayhem.rs2.content.shopping.impl.CustomShop2;

/**
 * Shops
 * 
 * @author Daniel
 * @author Michael
 */
public class Shop extends ItemContainer {

	/**
	 * Amount of shops available
	 */
	private static Shop[] shops = new Shop[900];
	
	/**
	 * Shop size
	 */
	public static final int SHOP_SIZE = 36;

	/**
	 * Shop id
	 */
	private final int store;

	/**
	 * Default items
	 */
	private final Item[] defaultItems;

	/**
	 * Checks if general shop
	 */
	private boolean general = false;

	/**
	 * Shop name
	 */
	private String name;

	/**
	 * Restock
	 */
	private int restock = 50;

	/**
	 * Update time
	 */
	private long update = System.currentTimeMillis();

	/**
	 * Shop Type
	 */
	protected ShopType type;

	/**
	 * Declare all the shops
	 */
	public static void declare() {
		shops[TokkulShop.SHOP_ID] = new TokkulShop();
		shops[PestShop.SHOP_ID] = new PestShop();
		shops[SlayerShop.SHOP_ID] = new SlayerShop();
		shops[BountyShop.SHOP_ID] = new BountyShop();
		shops[GracefulShop.SHOP_ID] = new GracefulShop();
		shops[BloodMoneyShop.SHOP_ID] = new BloodMoneyShop();
		shops[BossShop.SHOP_ID] = new BossShop();
		shops[PvmShop.SHOP_ID] = new PvmShop();
		shops[PlatinumShop.SHOP_ID] = new PlatinumShop();
		shops[MiningSkillShop.SHOP_ID] = new MiningSkillShop();
		shops[TriviaShop.SHOP_ID] = new TriviaShop();
		shops[ClueShop.SHOP_ID] = new ClueShop();
		shops[SkillPointShop.SHOP_ID] = new SkillPointShop();
		shops[SkillPointShop2.SHOP_ID] = new SkillPointShop2();
		shops[Hunterskillshop.SHOP_ID] = new Hunterskillshop();
		shops[PrayerSkillShop.SHOP_ID] = new PrayerSkillShop();
		shops[RunecraftingSkillShop.SHOP_ID] = new RunecraftingSkillShop();
		shops[HerbloreSkillShop.SHOP_ID] = new HerbloreSkillShop();
		shops[ThievingSkillShop.SHOP_ID] = new ThievingSkillShop();
		shops[CraftingSkillShop.SHOP_ID] = new CraftingSkillShop();
		shops[FletchingSkillShop.SHOP_ID] = new FletchingSkillShop();
		shops[SmithingSkillShop.SHOP_ID] = new SmithingSkillShop();
		shops[FishingSkillShop.SHOP_ID] = new FishingSkillShop();
		shops[CookingSkillShop.SHOP_ID] = new CookingSkillShop();
		shops[FiremakingSkillShop.SHOP_ID] = new FiremakingSkillShop();
		shops[WoodcuttingSkillShop.SHOP_ID] = new WoodcuttingSkillShop();
		shops[FarmingSkillShop.SHOP_ID] = new FarmingSkillShop();
		shops[SkillcapeShop.SHOP_ID] = new SkillcapeShop();
		shops[VotingShop.SHOP_ID] = new VotingShop();
		shops[CreditsShop.SHOP_ID] = new CreditsShop();
		shops[CreditsShop2.SHOP_ID] = new CreditsShop2();
		shops[CreditsShop3.SHOP_ID] = new CreditsShop3();
		shops[PrestigeShop.SHOP_ID] = new PrestigeShop();
		shops[MageArenaShop.SHOP_ID] = new MageArenaShop();
		shops[AchievementShop.SHOP_ID] = new AchievementShop();
		shops[CustomShop.SHOP_ID] = new CustomShop();
		shops[CustomShop2.SHOP_ID] = new CustomShop2();
		
		shops[91] = new AgilityShop();

		Item[] stock = new Item[SHOP_SIZE];
		Item[] stock2 = new Item[SHOP_SIZE];

		stock[0] = new Item(18016, 800000);
		stock[1] = new Item(12525, 100000);

		for (int i = 2; i < stock.length; i++) {
			int id = Pouch.values()[(i - 2)].secondIngredientId;

			if ((id != 1635) && (id != 440) && (id != 1519) && (id != 2349) && (id != 249) && (id != 29152) && (id != 2351) && (id != 3095)) {
				stock[i] = new Item(Pouch.values()[(i - 2)].secondIngredientId, 50000);
			}
		}
		for (int i = 0; i < stock.length; i++) {
			if (i + 50 >= Pouch.values().length) {
				break;
			}
			int id = Pouch.values()[(i + 50)].secondIngredientId;

			if ((id != 383) && (id != 2363) && (id != 2361) && (id != 1635) && (id != 6155) && (id != 1119) && (id != 1115)) {
				stock2[i] = new Item(id, 50000);
			}
		}
		new Shop(50, stock, false, "Summoning Shop");
		new Shop(51, stock2, false, "Summoning Shop");
	}

	public static Shop[] getShops() {
		return shops;
	}

	public Shop(int id, Item[] stock, boolean general, String name, ShopType type) {
		super(SHOP_SIZE, ItemContainer.ContainerTypes.ALWAYS_STACK, true, false);
		this.general = general;
		this.name = name;
		this.store = id;
		this.type = type;
		shops[id] = this;

		defaultItems = (stock.clone());
		for (int i = 0; i < stock.length; i++) {
			if (stock[i] != null) {
				setSlot(new Item(stock[i]), i);
			}
		}

		shift();

		TaskQueue.queue(new Task(restock) {
			@Override
			public void execute() {
				refreshContainers();
			}

			@Override
			public void onStop() {
			}
		});
	}

	public Shop(Item[] stock, String name) {
		super(SHOP_SIZE, ItemContainer.ContainerTypes.ALWAYS_STACK, true, true);
		general = false;
		this.name = name;
		store = -1;
		defaultItems = (stock.clone());
	}

	public Shop(int id, Item[] stock, boolean general, String name) {
		this(id, stock, general, name, ShopType.DEFAULT);
	}

	public ShopType getShopType() {
		return type;
	}

	@Override
	public boolean allowZero(int id) {
		return isDefaultItem(id);
	}

	public void buy(Player player, int slot, int id, int amount) {
	
		if (player.ironPlayer()) {
			boolean can = true;
			 for (int index = 0; index < ShopConstants.IRON_NO_BUY_SHOPS.length; index++) {
				if (store == ShopConstants.IRON_NO_BUY_SHOPS[index]) {
					can = false;
				}
			}
			
			
			if (!can) {
				player.send(new SendMessage("<img=" + (player.getRights() - 1) + ">@gry@ Iron players may not purchase this item!"));
				return;
			}
		}

		if (System.currentTimeMillis() - player.shopDelay < 1000) {
		    player.send(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] Please wait before doing this again!"));
		    return;
		}

		if (amount > 50000) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You can only buy 50k maximum at a time from these shops."));
			amount = 50000;
		}

		if (!hasItem(slot, id))
			return;
		if (get(slot).getAmount() == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] The shop is out of stock on that item."));
			return;
		}
		if (amount > get(slot).getAmount()) {
			amount = get(slot).getAmount();
		}

		Item buying = new Item(id, amount);

		Item gold = new Item(995, getSellPrice(id) * amount);

		if (!player.getInventory().hasSpaceOnRemove(gold, buying)) {
			if (!buying.getDefinition().isStackable()) {
				int slots = player.getInventory().getFreeSlots();
				if (slots > 0) {
					buying.setAmount(slots);
					amount = slots;
					gold.setAmount(getSellPrice(id) * amount);
				} else {
					player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough inventory space to buy this item."));
					return;
				}
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough inventory space to buy this item."));
				return;
			}
		}
		
		if (gold.getAmount() > 0) {
			if (player.isPouchPayment()) {
				if (player.getMoneyPouch() < gold.getAmount()) {
					player.send(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough coins in your pouch to do this!"));
					return;
				}
			} else {
				if (!player.getInventory().hasItemAmount(gold)) {
					player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough coins to buy that."));
					return;
				}	
			}
		}

		if (this.store != 21) {
			int newAmount = get(slot).getAmount() - amount;

			if (newAmount < 1) {
				if (isDefaultItem(id))
					get(slot).setAmount(0);
				else
					remove(get(slot));
			} else {
				get(slot).setAmount(newAmount);
			}
		}

		if (gold.getAmount() > 0) {
			if (player.isPouchPayment()) {
				player.setMoneyPouch(player.getMoneyPouch() - gold.getAmount());
				player.send(new SendString(player.getMoneyPouch() + "", 8135));
			} else {
				player.getInventory().remove(gold, false);				
			}
			AchievementHandler.activate(player, AchievementList.SPEND_5000000_ON_SHOPS, gold.getAmount());
			AchievementHandler.activate(player, AchievementList.SPEND_100000000_ON_SHOPS, gold.getAmount());
		}

		if (this.store == 13) {
			buying.setId(buying.getDefinition().getNoteId() > -1 ? buying.getDefinition().getNoteId() : buying.getId());
		}

		player.getInventory().add(buying);
		update();
		player.shopDelay = System.currentTimeMillis();
	}

	public boolean empty(int slot) {
		return (get(slot) == null) || (get(slot).getAmount() == 0);
	}

	public int getBuyPrice(int id) {
		if (this.store == 21) {
			return 0;
		}

		return GameDefinitionLoader.getStoreSellToValue(id);
	}

	public String getCurrencyName() {
		return null;
	}

	public Item getDefaultItem(int id) {
		for (Item i : defaultItems) {
			if ((i != null) && (i.getId() == id)) {
				return i;
			}
		}

		return null;
	}

	public Item[] getDefaultItems() {
		return defaultItems;
	}

	public String getName() {
		return name;
	}

	public int getSellPrice(int id) {
		if (this.store == 21) {
			return 0;
		}

		return GameDefinitionLoader.getStoreBuyFromValue(id);
	}

	public boolean hasItem(int slot, int id) {
		return (get(slot) != null) && (get(slot).getId() == id);
	}

	public boolean isDefaultItem(int id) {
		for (Item i : defaultItems) {
			if ((i != null) && (i.getId() == id)) {
				return true;
			}
		}

		return false;
	}

	public boolean isGeneral() {
		return general;
	}

	public boolean isUpdate() {
		return System.currentTimeMillis() - update < 1000L;
	}

	@Override
	public void onAdd(Item item) {
	}

	@Override
	public void onFillContainer() {
	}

	@Override
	public void onMaxStack() {
	}

	@Override
	public void onRemove(Item item) {
	}

	public void refreshContainers() {
		Item[] items = getItems();

		for (int j = 0; j < SHOP_SIZE; j++) {
			if (items[j] == null) {
				break;
			}
			Item stock = getDefaultItem(items[j].getId());

			if (stock != null) {
				if (items[j].getAmount() < stock.getAmount())
					items[j].add(1);
				else if (items[j].getAmount() > stock.getAmount()) {
					items[j].remove(1);
				}
			} else if (items[j].getAmount() > 1)
				items[j].remove(1);
			else {
				remove(getItems()[j]);
			}

		}

		update();
	}

	public boolean sell(Player player, int id, int amount) {
		if (id == 995) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell coins to a shop."));
			return false;
		}
	
		for (int i = 0; i < ShopConstants.NO_SELL.length; i++) {
			if (id == ShopConstants.NO_SELL[i]) {
				player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You may not sell this item! Try selling it to a player."));
				return false;
			}
		}
		
		if (!Item.getDefinition(id).isTradable()) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item."));
			return false;
		}

		if ((this.store == 21) || ((!general) && (!isDefaultItem(id)))) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item to this shop."));
			return false;
		}

		if (amount > 5000) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You can only sell 5,000 at a time to these shops."));
			amount = 5000;
		}
		
		/*if (GameDefinitionLoader.getItemDef(id).getGeneralPrice() > 15_000_000) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell items worth more than 15,000,000 coins!"));
			return false;
		} */

		int invAmount = player.getInventory().getItemAmount(id);

		if (invAmount == 0)
			return false;
		if (invAmount < amount) {
			amount = invAmount;
		}

		Item item = new Item(id, amount);

		if (!hasSpaceFor(item)) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] The shop does not have enough space to buy this item."));
			return false;
		}

		Item gold = new Item(995, getBuyPrice(id) * amount);

		if (!player.getInventory().hasSpaceOnRemove(item, gold)) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough inventory space to sell this item."));
			return false;
		}

		player.getInventory().remove(item);

		if (gold.getAmount() > 0) {
			player.getInventory().add(gold);
		}

		add(item);
		update();
		return true;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void update() {
		update = System.currentTimeMillis();
	}

	public void onOpen(Player player) {
	}
}
