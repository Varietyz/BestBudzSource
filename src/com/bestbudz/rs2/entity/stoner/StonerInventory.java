package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.Box;
import com.bestbudz.rs2.content.MoneyPouch;
import com.bestbudz.rs2.content.PriceChecker;
import com.bestbudz.rs2.content.consumables.Consumables;
import com.bestbudz.rs2.content.bank.Bank;
import com.bestbudz.rs2.content.shopping.Shopping;
import com.bestbudz.rs2.content.trading.Trade;
import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.item.ItemDegrading;
import com.bestbudz.rs2.entity.item.impl.LocalGroundItems;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.content.profession.melee.SerpentineHelmet;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Manages all inventory-related functionality including items, equipment, trading, banking
 */
public class StonerInventory {
	private final Stoner stoner;

	// Core inventory components
	private final Box box;
	private final Bank bank;
	private final MoneyPouch pouch;
	private final Equipment equipment;
	private final LocalGroundItems groundItems;
	private final ItemDegrading degrading;
	private final PriceChecker priceChecker;

	// Trading and shopping
	private final Trade trade;
	private final Shopping shopping;

	// Consumables
	private final Consumables consumables;

	// Special charged items
	private ToxicBlowpipe toxicBlowpipe;
	private TridentOfTheSeas seasTrident;
	private TridentOfTheSwamp swampTrident;
	private SerpentineHelmet serpentineHelment;

	// Transaction delays
	public long shopDelay;
	public long tradeDelay;

	// Enter X interface state
	private int enterXSlot = -1;
	private int enterXInterfaceId = -1;
	private int enterXItemId = 1;

	public StonerInventory(Stoner stoner) {
		this.stoner = stoner;
		this.box = new Box(stoner);
		this.bank = new Bank(stoner);
		this.pouch = new MoneyPouch(stoner);
		this.equipment = new Equipment(stoner);
		this.groundItems = new LocalGroundItems(stoner);
		this.degrading = new ItemDegrading();
		this.priceChecker = new PriceChecker(stoner, 28);
		this.trade = new Trade(stoner);
		this.shopping = new Shopping(stoner);
		this.consumables = new Consumables(stoner);

		// Initialize special items with default values
		this.toxicBlowpipe = new ToxicBlowpipe(null, 0);
		this.seasTrident = new TridentOfTheSeas(0);
		this.swampTrident = new TridentOfTheSwamp(0);
		this.serpentineHelment = new SerpentineHelmet(0);
	}

	public void process() {
		if (stoner.isPetStoner()) {
			return; // Pets don't need inventory processing
		}

		shopping.update();
		// Any other periodic inventory processing
	}

	/**
	 * Handles payment transactions using either money pouch or BestBucks
	 */
	public boolean payment(int amount) {
		if (stoner.getStats().isPouchPayment()) {
			if (stoner.getStats().getMoneyPouch() < amount) {
				stoner.send(new SendMessage("Insufficient funds on your Debit card!"));
				return false;
			}
			stoner.getStats().setMoneyPouch(stoner.getStats().getMoneyPouch() - amount);
			stoner.send(new SendString(stoner.getStats().getMoneyPouch() + "", 8135));
			return true;
		} else {
			if (!box.hasItemAmount(995, amount)) {
				stoner.send(new SendMessage("You do not have enough BestBucks to do this!"));
				return false;
			}
			box.remove(995, amount);
			return true;
		}
	}

	// Getters for all inventory components
	public Box getBox() { return box; }
	public Bank getBank() { return bank; }
	public MoneyPouch getPouch() { return pouch; }
	public Equipment getEquipment() { return equipment; }
	public LocalGroundItems getGroundItems() { return groundItems; }
	public ItemDegrading getDegrading() { return degrading; }
	public PriceChecker getPriceChecker() { return priceChecker; }
	public Trade getTrade() { return trade; }
	public Shopping getShopping() { return shopping; }
	public Consumables getConsumables() { return consumables; }

	// Special items getters and setters
	public ToxicBlowpipe getToxicBlowpipe() { return toxicBlowpipe; }
	public void setToxicBlowpipe(ToxicBlowpipe toxicBlowpipe) { this.toxicBlowpipe = toxicBlowpipe; }

	public TridentOfTheSeas getSeasTrident() { return seasTrident; }
	public void setSeasTrident(TridentOfTheSeas trident) { this.seasTrident = trident; }

	public TridentOfTheSwamp getSwampTrident() { return swampTrident; }
	public void setSwampTrident(TridentOfTheSwamp swampTrident) { this.swampTrident = swampTrident; }

	public SerpentineHelmet getSerpentineHelment() { return serpentineHelment; }
	public void setSerpentineHelment(SerpentineHelmet serpentineHelment) { this.serpentineHelment = serpentineHelment; }

	// Enter X interface getters and setters
	public int getEnterXSlot() { return enterXSlot; }
	public void setEnterXSlot(int enterXSlot) { this.enterXSlot = enterXSlot; }

	public int getEnterXInterfaceId() { return enterXInterfaceId; }
	public void setEnterXInterfaceId(int enterXInterfaceId) { this.enterXInterfaceId = enterXInterfaceId; }

	public int getEnterXItemId() { return enterXItemId; }
	public void setEnterXItemId(int enterXItemId) { this.enterXItemId = enterXItemId; }

	// Money pouch delegation methods for backward compatibility
	public long getMoneyPouch() { return stoner.getStats().getMoneyPouch(); }
	public void setMoneyPouch(long amount) { stoner.getStats().setMoneyPouch(amount); }
	public boolean isPouchPayment() { return stoner.getStats().isPouchPayment(); }
	public void setPouchPayment(boolean pouchPayment) { stoner.getStats().setPouchPayment(pouchPayment); }
}