package com.mayhem.rs2.content.membership;

import java.util.HashMap;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.OptionDialogue;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.content.interfaces.impl.CreditTab;
import com.mayhem.rs2.content.interfaces.impl.oldQuestTab;
import com.mayhem.rs2.content.shopping.impl.AchievementShop;
import com.mayhem.rs2.content.shopping.impl.BloodMoneyShop;
import com.mayhem.rs2.content.shopping.impl.BossShop;
import com.mayhem.rs2.content.shopping.impl.BountyShop;
import com.mayhem.rs2.content.shopping.impl.CustomShop;
import com.mayhem.rs2.content.shopping.impl.CustomShop2;
import com.mayhem.rs2.content.shopping.impl.PrestigeShop;
import com.mayhem.rs2.content.shopping.impl.PvmShop;
import com.mayhem.rs2.content.shopping.impl.SkillPointShop;
import com.mayhem.rs2.content.shopping.impl.SkillPointShop2;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the credit system
 * @author Daniel
 *
 */
public enum CreditHandler {
	
	VOTE_SHOP(205051, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(92);
			player.send(new SendMessage("You have @blu@" + player.getVotePoints() + "</col> Voting points."));
		}
		}
	}),
	
	GENERAL_STORE(205052, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(0);
		}
		}
	}),
	
	SKILLING_SUPPLIES(205053, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Skilling Supplies", p -> {
				player.getShopping().open(17);
			} , "Farming Supplies", p -> {
				player.getShopping().open(32);
			} ,  "Herblore Supplies", p -> {
				player.getShopping().open(33);
			}
		));
			}
	}}),
	
	FOOD_POTIONS(205054, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(15);
		}
		}
	}),
	
	WEAPONS(205055, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(16);
		}
		}
	}),
	
	ARMOUR(205056, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(18);
		}
		}
	}),
	
	PURE(205057, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(27);
		}
		}
	}),
	
	MAGIC(205058, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(26);
		}
		}
	}),
	
	RANGE(205059, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(25);
		}
		}
	}),
	
	OUTFITS(205060, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Clothing shop 1", p -> {
				player.getShopping().open(28);
			} , "Clothing shop 2", p -> {
				player.getShopping().open(40);
			}));
			}
		}
		
	}),
	
	BLOOD_MONEY(205061, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(BloodMoneyShop.SHOP_ID);
		}
		}
	}),
	
	SKILL_POINT(205062, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Skillpoint Shop 1", p -> {
				player.getShopping().open(103);
				player.send(new SendMessage("You have @blu@" + player.getskillPoints() + "</col> Skill points."));
			} , "Skillpoint Shop 2", p -> {
				player.getShopping().open(121);
				player.send(new SendMessage("You have @blu@" + player.getskillPoints() + "</col> Skill points."));
			}));
			}
		}
		
	}),
	
	PVM_POINT(205063, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(PvmShop.SHOP_ID);
			player.send(new SendMessage("You have @blu@" + player.getpvmPoints() + "</col> PVM points."));
		}
		}
	}),
	
	BOSS_POINT(205064, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(BossShop.SHOP_ID);
			player.send(new SendMessage("You have @blu@" + player.getbossPoints() + "</col> Boss points."));
		}
		}
	}),
	
	DONATOR(205065, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("FalconExpress Bucks Store 1", p -> {
				player.getShopping().open(94);
			} , "FalconExpress Bucks Store 2", p -> {
				player.getShopping().open(90);
			} , "FalconExpress Bucks Store 3", p -> {
				player.getShopping().open(87);
			}));
		}
		}
	}),
	
	TITLES(205066, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.send(new SendInterface(55000));
		}
		}
	}),
	
	ACHIEVEMENT(205067, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(AchievementShop.SHOP_ID);
		}
		}
	}),
	
	PRESTIGE(205068, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(PrestigeShop.SHOP_ID);
			player.send(new SendMessage("You have @blu@" + player.getPrestigePoints() + "</col> Prestige points."));
		}
		}
	}),
	
	
	BOUNTY(205069, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.getShopping().open(BountyShop.SHOP_ID);
		}
		}
	}),
	
	IRONMAN(205070, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("General", p -> {
				player.getShopping().open(38);
			} , "Armours", p -> {
				player.getShopping().open(39);
			} , "Miscellaneous", p -> {
				player.getShopping().open(41);
			} , "Herblore", p -> {
				player.getShopping().open(33);
			} , "Farming", p -> {
				player.getShopping().open(32);
			}));
		}
		}
	}),
	
	
	
	;

	private int button;
	private int creditCost;
	private Handle handle;

	private CreditHandler(int button, int creditCost, Handle handle) {
		this.button = button;
		this.creditCost = creditCost;
		this.handle = handle;
	}

	public int getButton() {
		return button;
	}

	public int getCost() {
		return creditCost;
	}

	public Handle getHandle() {
		return handle;
	}

	public static HashMap<Integer, CreditHandler> credits = new HashMap<Integer, CreditHandler>();

	static {
		for (final CreditHandler credits : CreditHandler.values()) {
			CreditHandler.credits.put(credits.button, credits);
		}
	}

	
	/**
	 * Checks if player is allowed to access feature
	 * @param player
	 * @param amount
	 * @return
	 */
	public static boolean allowed(Player player, CreditPurchase credit, int amount) {
		if (player.isCreditUnlocked(credit)) {
			DialogueManager.sendStatement(player, "@red@You have already this unlocked.");
			return false;
		}
		if (player.getCredits() < amount) {
			DialogueManager.sendStatement(player, "@red@You do not have enough FalconExpress Bucks to do this!");
			player.send(new SendMessage("Please visit @red@::Store </col>to purchase more FalconExpress Bucks!"));
			return false;
		}
		if (player.inWilderness()) {
			DialogueManager.sendStatement(player, "You can not do this in the wilderness!");
			return false;
		}
		if (player.getCombat().inCombat()) {
			DialogueManager.sendStatement(player, "You can not do this while in combat!");
			return false;
		}
		return true;
	}

	/**
	 * Handles what happens when player has spent credits
	 * @param player
	 * @param amount
	 */
	public static void spent(Player player, int amount) {
		player.send(new SendMessage("@blu@You have spent " + amount + " FalconExpress Bucks; Remaining: " + player.getCredits() + "."));
		player.getClient().queueOutgoingPacket(new SendString("</col>FalconExpress Bucks: @gre@" + Utility.format(player.getCredits()), 52504));	
		InterfaceHandler.writeText(new CreditTab(player));
		InterfaceHandler.writeText(new oldQuestTab(player));
	}

	/**
	 * Handles clicking buttons
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean handleClicking(Player player, int buttonId) {
		CreditHandler credits = CreditHandler.credits.get(buttonId);

		if (credits == null) {
			return false;
		}

		credits.getHandle().handle(player);
		return false;
	}
	
}
