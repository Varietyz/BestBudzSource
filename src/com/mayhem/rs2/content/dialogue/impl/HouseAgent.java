package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueConstants;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.content.houses.HouseManager;
import com.mayhem.rs2.content.houses.HouseType;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendEnterString;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class HouseAgent extends Dialogue {

	public static final int NPC_ID = 4291;
	private static final String SMALL = null;
	private int startIndex;

	public HouseAgent(Player player, int startIndex) {
		this.player = player;
		this.startIndex = startIndex;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case DialogueConstants.OPTIONS_2_1:
			if (next == 9) {
				end();
				HouseManager.deleteHouse(player);
			}
			return true;
		case DialogueConstants.OPTIONS_4_1:
			if (next == 5) {
				buyHouse(HouseType.LARGE);
			}
			return true;
		case DialogueConstants.OPTIONS_4_2:
			if (next == 5) {
				buyHouse(HouseType.MEDIUM);
			}
			return true;
		case DialogueConstants.OPTIONS_4_3:
			if (next == 5) {
				buyHouse(HouseType.SMALL);
			}
			return true;
		case DialogueConstants.OPTIONS_5_1:
			if (next == 3) {
				next++;
				execute();
			}
			return true;
		case DialogueConstants.OPTIONS_5_2:
			if (next == 3) {
				next = 6;
				execute();
			}
			return true;
		case DialogueConstants.OPTIONS_5_3:
			if (next == 3) {
				next = 7;
				execute();
			}
			return true;
		case DialogueConstants.OPTIONS_5_4:
			if (next == 3) {
				next = 8;
				execute();
			}
			return true;
		case DialogueConstants.OPTIONS_2_2:
		case DialogueConstants.OPTIONS_4_4:
		case DialogueConstants.OPTIONS_5_5:
			exit();
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		if (player == null)
			return;
		switch (startIndex) {
		case 1:
			next = 7;
			break;
		case 2:
			next = 6;
			break;
		}
		startIndex = -1;
		boolean ownsHousePlot = player.hasBoughtLargePlot() || player.hasBoughtMediumPlot()
				|| player.hasBoughtSmallPlot();
		boolean ownsHouse = HouseManager.ownsHouse(player.getUsername());
		switch (next) {
		case 0:
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
					"Hello, " + Utility.formatPlayerName(player.getUsername()) + ", I'm in charge of player houses",
					"on FalconExpress. For the right price, in FalconExpress Bucks, I can",
					"sell you a house. If you've already purchased one,",
					"I can sell you an upgrade, or send you to it.");
			next++;
			break;
		case 1:
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
					"On top of that, you'll be able to visit other players",
					"in their house, assuming they're not busy and their doors",
					"are open. That also means players can visit you, if you", "allow it.");
			next++;
			break;
		case 2:
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
					"Sorry for the long introduction... How may I help?");
			next++;
			break;
		case 3:
			DialogueManager.sendOption(player, (ownsHousePlot ? "Upgrade" : "Buy") + " House", "Enter my house",
					"Visit a friend's house", "Delete my house", "Do nothing");
			break;
		case 4:// Upgrade/buy house option
			if (ownsHouse) {
				DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
						"I see you already own a house. Upgrading your house,",
						"if possible, removes @red@everything@bla@ from it and you",
						"start from scratch. If you like your current",
						"house, you might want wait until you have supplies.");
			} else {
				DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
						"Ahh, buying your first house? Your first one may not be",
						"the best but that's okay. You can upgrade for a discounted",
						"price later. Don't forget that you'll have to", "bring materials in to build furniture.");
			}
			next++;
			break;
		case 5:
			int largePrice = getPriceOfHouse(HouseType.LARGE);
			int mediumPrice = getPriceOfHouse(HouseType.MEDIUM);
			int smallPrice = getPriceOfHouse(HouseType.SMALL);
			DialogueManager.sendOption(player,
					(largePrice > 0 ? (HouseType.LARGE.getPrice() > largePrice ? "Upgrade to" : "Buy") : "Select")
							+ " " + HouseType.LARGE.getName()
							+ (largePrice < 1 ? "" : (" for " + largePrice + " Gems")),
					(mediumPrice > 0 ? (HouseType.MEDIUM.getPrice() > mediumPrice ? "Upgrade to" : "Buy") : "Select")
							+ " " + HouseType.MEDIUM.getName()
							+ (mediumPrice < 1 ? "" : (" for " + mediumPrice + " Gems")),
					(smallPrice > 0 ? (HouseType.SMALL.getPrice() > smallPrice ? "Upgrade" : "Buy") : "Select") + " "
							+ HouseType.SMALL.getName() + (smallPrice < 1 ? "" : (" for " + smallPrice + " Gems")),
					"Cancel");
			break;
		case 6:// Enter own house
			if (ownsHouse) {
				exit();
				HouseManager.enterHouse(player, player.getUsername());
				break;
			}
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
					ownsHousePlot ? "You need to select a house before you can enter it."
							: "Sorry, but you need to buy a house first.");
			next = 3;
			break;
		case 7:// Visit a friend
			player.getAttributes().set("house-friend-name", true);
			player.send(new SendEnterString());
			break;
		case 8:// Delete house
			if (ownsHouse) {
				DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
						"Are you sure you want to delete your house?", "There's no fee to re-build the same house, but",
						"upgrading costs extra. Plus, deleting the house", "deletes all objects in it permanently.");
				next++;
				break;
			}
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT, "You can't remove what doesn't exist.",
					"Please buy or select a house first.");
			next = 3;
			break;
		case 9:
			DialogueManager.sendOption(player, "Delete house", "I changed my mind");
			break;
		default:
			exit();
			break;
		}
	}

	private int getPriceOfHouse(HouseType type) {
		if (player == null)
			return 0;
		switch (type) {
		case LARGE:
			return player
					.hasBoughtLargePlot()
							? 0
							: (player.hasBoughtMediumPlot() ? (type.getPrice() - HouseType.MEDIUM.getPrice())
									: (player.hasBoughtSmallPlot()
											? (type.getPrice() - HouseType.SMALL.getPrice())
											: type.getPrice()));
		case MEDIUM:
			return player.hasBoughtMediumPlot() ? 0
					: (player.hasBoughtSmallPlot() ? (type.getPrice() - HouseType.SMALL.getPrice())
							: type.getPrice());
		case SMALL:
			return player.hasBoughtSmallPlot() ? 0 : type.getPrice();
		default:
			return 0;
		}
	}

	private void buyHouse(HouseType large) {
		int price = 0;
		switch (large) {
		case LARGE:
			if (player.hasBoughtLargePlot())
				price = -1;
			else if (player.hasBoughtMediumPlot())
				price = (HouseType.LARGE.getPrice() - HouseType.MEDIUM.getPrice());
			else if (player.hasBoughtSmallPlot())
				price = (HouseType.LARGE.getPrice() - HouseType.SMALL.getPrice());
			else
				price = HouseType.LARGE.getPrice();
			break;
		case MEDIUM:
			if (player.hasBoughtMediumPlot())
				price = -1;
			else if (player.hasBoughtSmallPlot())
				price = (HouseType.MEDIUM.getPrice() - HouseType.SMALL.getPrice());
			else
				price = HouseType.MEDIUM.getPrice();
			break;
		case SMALL:
			if (player.hasBoughtSmallPlot())
				price = -1;
			else
				price = HouseType.SMALL.getPrice();
			break;
		default:
			break;

		}
		if (price == -1 && HouseManager.ownsHouse(player.getUsername())) {
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT, "You already own this house.");
			return;
		}
		if (price == -1 && !HouseManager.ownsHouse(player.getUsername())) {
			HouseManager.createHouse(player, large);
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT, "Your new house has been set to: ",
					large.getName());
			end();
			return;
		}
		if (price < 1) {
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT, "You can't buy a house for that price!");
			return;
		}
		if (price > player.getMoneyPouch() || (player.getMoneyPouch() - price) < 1) {
			DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT,
					"You don't have enough money to buy that house.");
			return;
		}
		switch (large) {
		case LARGE:
			player.setBoughtLargePlot(true);
			player.setBoughtMediumPlot(true);
			player.setBoughtSmallPlot(true);
			break;
		case MEDIUM:
			player.setBoughtMediumPlot(true);
			player.setBoughtSmallPlot(true);
			break;
		case SMALL:
			player.setBoughtSmallPlot(true);
			break;
		default:
			break;
		}
		HouseManager.createHouse(player, large);
		player.setMoneyPouch(player.getMoneyPouch() - price);
		DialogueManager.sendNpcChat(player, NPC_ID, Emotion.DEFAULT, "Congratulations on buying your new house:",
				large.getName());
		end();
	}

	public static String getSmall() {
		return SMALL;
	}

	private void exit() {
		if (player == null)
			return;
		end();
		player.send(new SendRemoveInterfaces());
	}
}
