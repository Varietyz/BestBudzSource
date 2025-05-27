package com.mayhem.rs2.entity.player.net.in.command.impl;

import java.awt.Desktop;
import java.net.URI;

import com.mayhem.Constants;
import com.mayhem.core.definitions.ItemDefinition;
import com.mayhem.core.definitions.NpcDefinition;
import com.mayhem.core.network.mysql.Donation;
//import com.mayhem.core.network.mysql.Highscores;
import com.mayhem.core.network.mysql.Voting;
import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.PlayersOnline;
import com.mayhem.rs2.content.Yelling;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.OptionDialogue;
import com.mayhem.rs2.content.dialogue.impl.ChangePasswordDialogue;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.content.interfaces.impl.CommandInterface;
import com.mayhem.rs2.content.interfaces.impl.TrainingInterface;
import com.mayhem.rs2.content.profiles.PlayerProfiler;
import com.mayhem.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.mayhem.rs2.content.mayhembot.*;
import com.mayhem.rs2.content.membership.RankHandler;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.in.command.Command;
import com.mayhem.rs2.entity.player.net.in.command.CommandParser;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
//import com.motivoters.motivote.Vote;
//import com.motivoters.motivote.service.MotivoteRS;

/**
 * A list of commands accessible to all players disregarding rank.
 * 
 * @author Michael | Chex
 */
public class PlayerCommand implements Command {
	
	//final MotivoteRS motivote = new MotivoteRS("mayhem", "0cc7f803a3be2a0df6e5d722253f2e13");

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		
		
		
		//Qwerty159063 Vote
		case "reward":
			if (!parser.hasNext(1)) {
				player.send(new SendMessage("Please use [::reward id], [::reward id amount], or [::reward id all]."));
				return true;
			}
			final String playerName = player.getUsername();
			final String id = parser.nextString();
			final String rewardAmount = parser.hasNext(1) ? parser.nextString() : "1";

			com.everythingrs.vote.Vote.service.execute(new Runnable() {
				@Override
				public void run() {
					try {
						com.everythingrs.vote.Vote[] reward = com.everythingrs.vote.Vote.reward("ttc4K5R0Ctw0LMGlzplS6V32gEkxsu5ZikxL0vKo4Bc5NjiMQZmxuqP099bgliRg8zAVe7Nl", playerName, id, rewardAmount);
						if (reward[0].message != null) {
							player.send(new SendMessage(reward[0].message));
							return;
						}
						player.getInventory().add(new Item(reward[0].reward_id, reward[0].give_amount));
						player.send(new SendMessage("Thank you for voting!"));
					} catch (Exception e) {
						player.send(new SendMessage("Api Services are currently offline. Please check back shortly"));
						e.printStackTrace();
					}
				}
			});
			return true;
		
		/*
		 * Show EloRating
		 */
		case "elo":
			player.send(new SendMessage("Your Elo-Rating is: " + player.eloRating));
			return true;
			
			
			
		
		
		
		/*case "claimvote":
		case "claimvotes":
		case "voted":
			new Thread(new Voting(player)).start();
			return true;*/
		
		/*
		 * Claim votes
		 */
		/*case "voted":
		case "claim":
		case "auth":
		case "claimvote":
		case "claimvotes":
			
			new Thread(new Voting(player)).start();
			/*if ((System.currentTimeMillis() - player.getLastRequestedLookup()) < 30000) {
				player.send(new SendMessage("Sorry, you can only check your votes once per 30 seconds."));
				return true;
			}

			player.setLastRequestedLookup(System.currentTimeMillis());
				try {
					Vote[] votes = motivote.getVotesForIP(player.getClient().getHost());
					
					if (votes.length < 1) {
						player.send(new SendMessage("Could not find votes for your IP. Try again later."));
					}
					for (Vote aVote : votes) {
						boolean success = motivote.redeemVote(aVote);
						if (success) {
							player.getInventory().add(995, 100000);
							Constants.LAST_VOTER = player.getUsername();
							Constants.CURRENT_VOTES++;
							player.setVotePoints(player.getVotePoints() + 1);
							player.send(new SendMessage("Thank you for voting, " + Utility.formatPlayerName(player.getUsername()) + "!"));
						}
						else {
							player.send(new SendMessage("Error redeeming votes. Try again later."));
						}
						
					}
					
				}
				catch (Exception ex) {
					//ex.printStackTrace();
					player.send(new SendMessage("Could not find votes for your IP. Try again later."));
				}*/
			//return true;

		/*
		 * Opens the command list
		 */
		case "command":
		case "commands":
		case "commandlist":
		case "commandslist":
			player.send(new SendString("FalconExpress Command List", 8144));
			InterfaceHandler.writeText(new CommandInterface(player));
			player.send(new SendInterface(8134));
			return true;
			
		case "hardcore":
		case "hcim":
			if (player.getRights() != 11 || (player.getDayCreated() <= 179) || (player.HardcoreDeath() == true)) {
				player.send(new SendMessage("@red@You do not have permission to make a <img=10>HCIM"));
				player.send(new SendMessage("@red@You must have an <img=9>Ironman account to make a <img=10>HCIM."));
				return false;
			}   
			player.send(new SendMessage("@red@If this is your first time logging in, RELOG before typing ::HCIM"));
			player.send(new SendMessage("@red@You must have an <img=9>Ironman account to make a <img=10>HCIM."));
			player.send(new SendMessage("@red@This command is only temporary until the interface is fixed."));
			
			if (player.getRights() == 11 && (player.getDayCreated() >= 179) && (player.HardcoreDeath() == false)) {
					player.setIron(false);
					player.setHardcoreIron(true);
					player.setRights(16);
					player.getInventory().add(20792, 1);
					player.getInventory().add(20794, 1);
					player.getInventory().add(20796, 1);
					player.send(new SendMessage("@red@This command is only temporary until the interface is fixed."));
					player.send(new SendMessage("@red@Once you relog, your HCIM sprite <img=10> will be beside your name!"));
						} return true;

		/*
		 * Opens the teleporting interface
		 */
		case "teleport":
		case "teleports":
		case "teleporting":
		case "teleportings":
			InterfaceHandler.writeText(new TrainingInterface(player));
			player.send(new SendInterface(61000));
			player.send(new SendString("Selected: @red@None", 61031));
			player.send(new SendString("Cost: @red@Free", 61032));
			player.send(new SendString("Requirement: @red@None", 61033));
			player.send(new SendString("Other: @red@None", 61034));
			return true;

		/*
		 * Answers TriviaBot
		 * 
		 */
		case "answer":
			if (parser.hasNext()) {
				String answer = "";
				while (parser.hasNext()) {
					answer += parser.nextString() + " ";
				}
				MayhemBot.answer(player, answer.trim());
			}
			return true;
			/**
		case "triviasetting":
		case "triviasettings":
			
			player.start(new OptionDialogue("Turn on TriviaBot", p -> {
				p.setWantTrivia(true);
				p.send(new SendMessage("<col=482CB8>You have turned on the TriviaBot."));
				player.send(new SendRemoveInterfaces());
			}, "Turn off TriviaBot", p -> {
				p.setWantTrivia(false);
				p.send(new SendMessage("<col=482CB8>You have turned off the TriviaBot."));
				player.send(new SendRemoveInterfaces());
			}, "Turn on TriviaBot notification", p -> {
				p.setTriviaNotification(true);
				p.send(new SendMessage("<col=482CB8>You have turned on the TriviaBot notification."));
				player.send(new SendRemoveInterfaces());
			}, "Turn off TriviaBot notification", p -> {
				p.setTriviaNotification(false);
				p.send(new SendMessage("<col=482CB8>You have turned off the TriviaBot notification."));
				player.send(new SendRemoveInterfaces());
			}));		
			return true;
			*/

		/*
		 * Gets amount of online players
		 */
		case "players":
			player.send(new SendMessage("There are currently @red@" + Utility.format(World.getActivePlayers()) + "</col> players online."));
			PlayersOnline.showPlayers(player, p -> {
				return true;
			});
			return true;

		/*
		 * Opens donation page
		 */
		case "donate":
		case "donation":
		case "donating":
		case "store":
		case "credits":
			player.send(new SendString("www.FalconExpress.everythingrs.com/services/store", 12000));
			player.send(new SendMessage("Loading donation page..."));
			return true;

		/*
		 * Opens website page
		 */
		case "forum":
		case "forums":
		case "website":
			player.send(new SendString("http://falcon-express.net/", 12000));
			player.send(new SendMessage("Loading Forums!"));
			return true;
			
			/*
			 * Opens discord page
			 */
			case "discord":
				player.send(new SendString("https://discord.gg/87445DC", 12000)); 
				player.send(new SendMessage("Loading Discord group chat."));
				return true;

		/*
		 * Opens voting page
		 */
		case "vote":
		case "voting":
			player.send(new SendString("www.FalconExpress.everythingrs.com/services/vote", 12000));
			player.send(new SendMessage("Loading voting page..."));
			
			return true;

		/*
		 * Finds player to view profile
		 */
		case "find":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.trim();

				PlayerProfiler.search(player, name);
			}
			return true;

		/**
		 * Withdraw from pouch
		 */
		case "withdrawmp":
			if (parser.hasNext()) {
				try {
					int amount = 1;
					
					if (parser.hasNext()) {
						long temp = Long.parseLong(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000"));

						if (temp > Integer.MAX_VALUE) {
							amount = Integer.MAX_VALUE;
						} else {
							amount = (int) temp;
						}
					}

					player.getPouch().withdrawPouch(amount);

				} catch (Exception e) {
					player.send(new SendMessage("Something went wrong!"));
					e.printStackTrace();
				}

			}
			return true;

		/*
		 * Change the password
		 */
		case "changepassword":
		case "changepass":
			if (parser.hasNext()) {
				try {
					String password = parser.nextString();
					if ((password.length() > 4) && (password.length() < 15))
						player.start(new ChangePasswordDialogue(player, password));
					else
						DialogueManager.sendStatement(player, new String[] { "Your password must be between 4 and 15 characters." });
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid password format, syntax: ::changepass password here"));
				}
			}
			return true;
			
            /*
             * Opens 
             */
		case "bank":
			if (player.getRights() == 1 || player.getRights() == 2 ||  player.getRights() == 3 ||  player.getRights() == 4 || player.getRights() == 6 || player.getRights() == 7 || player.getRights() == 8 || player.getRights() == 13 || player.getMoneySpent() >= 17) {
				if (!player.inWilderness()) {
					if (!player.inGodwars()) {
						if (!player.inCorp()) {
								if(player.getDueling().isDueling()) {
									player.getClient().queueOutgoingPacket(new SendMessage("You can't open the bank during a duel session."));
									return false;
								}
									if(!player.inClanWarsFFA()) {
				player.getBank().openBank();
            return true;
				  }
														
			   }
		    }
		}
	}
		/*
		 * Changes yell title
		 */
		case "yelltitle":
			if (player.getRights() == 0) {
				player.send(new SendMessage("You need to be a donator to do this!"));
				return true;
			}
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext()) {
						message += " " + parser.nextString();
					}

					for (int i = 0; i < Constants.BAD_STRINGS.length; i++) {
						if (message.contains(Constants.BAD_STRINGS[i])) {
							player.send(new SendMessage("You may not use that in your title!"));
							return true;
						}
					}

					for (int i = 0; i < Constants.BAD_TITLES.length; i++) {
						if (message.contains(Constants.BAD_TITLES[i])) {
							player.send(new SendMessage("You may not use that in your title!"));
							return true;
						}
					}

					player.setYellTitle(message);
					DialogueManager.sendTimedStatement(player, "Your yell title is now @red@" + message);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -title"));
				}
			}
			return true;

		/*
		 * Yell to server
		 */
		case "yell":
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext()) {
						message += " " + parser.nextString();
					}
					Yelling.yell(player, message.trim());
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -messsage"));
				}
			}
			return true;

		/*
		 * Handles player emptying inventory 
		 */
		case "empty":
			if (player.getRights() == 2 || player.getRights() == 3) {
				player.getInventory().clear();
				player.send(new SendMessage("Trash Emptied."));
				player.send(new SendRemoveInterfaces());
				return true;
			}
			
			player.start(new OptionDialogue("Empty", p -> {
				p.getInventory().clear();
				
				p.send(new SendMessage("Trash Emptied."));
				p.send(new SendRemoveInterfaces());
				
			} , " Do Not Empty!", p -> p.send(new SendRemoveInterfaces())));
			return true;

		/*
		 * Teleport player home
		 */
		case "home":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			} 
			player.getMagic().teleport(3086, 3493, 0, TeleportTypes.SPELL_BOOK);
			return true;
			
		case "oldhome":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			} 
			player.getMagic().teleport(3086, 3493, 0, TeleportTypes.SPELL_BOOK);
			return true;
			
			/** Never Finished Afk Chest 
			// You are able to spawn and get 15m in 2mins needs to be fixed
						case "afkzone":
							if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
								player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
								return true;
							} 
							
							//player.send(new SendMessage("Disabled while we fix a dupe!"));
							player.getMagic().teleport(2740, 5076, 0, TeleportTypes.SPELL_BOOK);
							return true;
							
							*/ 
			
		/** Empty currently 
		case "train":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			} 
			player.getMagic().teleport(2516, 4646, 0, TeleportTypes.SPELL_BOOK);
			return true;
			*/ 
			
		case "duel":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			} 
			player.getMagic().teleport(3367, 3266, 0, TeleportTypes.SPELL_BOOK);
			return true;
			
		case "barrows":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			}
			player.getMagic().teleport(3565, 3315, 0, TeleportTypes.SPELL_BOOK);
			return true;
			
		case "bandits":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			}  
			player.getMagic().teleport(3231, 3218, 0, TeleportTypes.SPELL_BOOK);
			return true;
			
			/** Unused Commands
		case "sdz":
			if (player.getMoneySpent() >= 25) {
			if (player.getWildernessLevel() > 20 && player.inWilderness()) {
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			}
			player.getMagic().teleport(2099, 3914, 0, TeleportTypes.SPELL_BOOK);
			return true;
			}
			
		case "edz":
			if (player.getMoneySpent() >= 150) {
			if (player.getWildernessLevel() > 20 && player.inWilderness()) {
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			}
			player.getMagic().teleport(2133, 4911, 0, TeleportTypes.SPELL_BOOK);
			return true;
			}
			
		case "dz":
			if (player.getMoneySpent() >= 5) {
			if (player.getWildernessLevel() > 20 && player.inWilderness()) {
				return true;
			}
			if (player.getRights() == 13) {
			player.getMagic().teleport(2827, 3344, 0, TeleportTypes.SPELL_BOOK);
			return true;
			}
			}
			*/
	
		
			
		case "dzone":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			} 
			if (player.getRights() == 5 || player.getRights() == 6 || player.getRights() == 7 || player.getRights() == 8 || player.getRights() == 2 || player.getRights() == 3 || player.getRights() == 4)  {
			player.getMagic().teleport(2827, 3344, 0, TeleportTypes.SPELL_BOOK);
			return true;
			}
			
		/*case "claim": 
			new Thread(new Donation(player)).start(); 
			return true;*/
			
		case "claim":
		case "claimdonation":
		case "donated":
			new java.lang.Thread() {
				public void run() {
					try {
						com.everythingrs.donate.Donation[] donations = com.everythingrs.donate.Donation.donations("ttc4K5R0Ctw0LMGlzplS6V32gEkxsu5ZikxL0vKo4Bc5NjiMQZmxuqP099bgliRg8zAVe7Nl", 
								player.getUsername());
						if (donations.length == 0) {
							player.send(new SendMessage("You currently don't have any items waiting. You must donate first!"));
							return;
						}
						if (donations[0].message != null) {
							player.send(new SendMessage(donations[0].message));
							return;
						}
						for (com.everythingrs.donate.Donation donate : donations) {
							player.getInventory().add(new Item(donate.product_id, donate.product_amount));
						}
						player.send(new SendMessage("Thank you for donating!"));
					} catch (Exception e) {
						player.send(new SendMessage("Api Services are currently offline. Please check back shortly"));
						e.printStackTrace();
					}	
				}
			}.start();
			return true;
			
			
		/** Vesupla Teleport */ 
			
		case "vesupla":
		case "vrb":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) { 
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			} 
			player.getMagic().teleport(2383, 4719, 0, TeleportTypes.SPELL_BOOK);
			return true;
			

			
		case "shop":
		case "shops":
		case "shopping":
		case "stores":
			
			if (player.getWildernessLevel() > 21 && player.inWilderness()) {
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			}
			player.getMagic().teleport(1779, 3691, 0, TeleportTypes.SPELL_BOOK);
			return true;
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return true;
	}
}