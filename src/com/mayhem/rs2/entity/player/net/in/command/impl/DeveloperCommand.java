package com.mayhem.rs2.entity.player.net.in.command.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.mayhem.Constants;
import com.mayhem.core.cache.map.MapLoading;
import com.mayhem.core.cache.map.ObjectDef;
import com.mayhem.core.definitions.ItemDefinition;
import com.mayhem.core.definitions.NpcDefinition;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.GameConstants;
import com.mayhem.rs2.content.CrystalChest;
import com.mayhem.rs2.content.PlayerTitle;
import com.mayhem.rs2.content.StarterKit;
import com.mayhem.rs2.content.bank.Bank;
import com.mayhem.rs2.content.cluescroll.ClueDifficulty;
import com.mayhem.rs2.content.cluescroll.ClueScrollManager;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.content.combat.Hit.HitTypes;
import com.mayhem.rs2.content.combat.special.SpecialAttackHandler;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.OneLineDialogue;
import com.mayhem.rs2.content.io.PlayerSave.PlayerContainer;
import com.mayhem.rs2.content.membership.MysteryBoxMinigame;
import com.mayhem.rs2.content.membership.RankHandler;
import com.mayhem.rs2.content.minigames.barrows.Barrows;
import com.mayhem.rs2.content.minigames.plunder.PyramidPlunder;
import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.content.skill.Skill;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.content.skill.agility.Agility;
import com.mayhem.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.mayhem.rs2.content.skill.slayer.Slayer;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.EquipmentConstants;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.ItemContainer;
import com.mayhem.rs2.entity.item.ItemContainer.ContainerTypes;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.MobDrops;
import com.mayhem.rs2.entity.mob.impl.Cerberus;
import com.mayhem.rs2.entity.mob.impl.Zulrah;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.net.in.PacketHandler;
import com.mayhem.rs2.entity.player.net.in.command.Command;
import com.mayhem.rs2.entity.player.net.in.command.CommandParser;
import com.mayhem.rs2.entity.player.net.out.impl.SendConfig;
import com.mayhem.rs2.entity.player.net.out.impl.SendEquipment;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendInventory;
import com.mayhem.rs2.entity.player.net.out.impl.SendInventoryInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.mayhem.rs2.entity.player.net.out.impl.SendSidebarInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
import com.mayhem.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * A list of developer commands accessible to all players with the developer's
 * rank.
 * 
 * @author Michael | Chex
 * @author Daniel | Play Boy
 */
public class DeveloperCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		
		case "switchskiller":
			player.getDailyChallenges().setSkiller(!player.getDailyChallenges().isSkiller());
			return true;
			
		case "resetchallenge":
			player.getDailyChallenges().resetChallenge();
			player.send(new SendMessage("Your daily challenge is to " + player.getDailyChallenges().getChallenge()
					.getDescription(player.getDailyChallenges().getAmount())));
			return true;
		
		case "cerb":
			Cerberus dog = new Cerberus(player, new Location(1241, 1254, player.getIndex()));
			dog.face(player);
			return true;
			
		case "slay":
			Slayer.taskTele(player);
			return true;
			
		case "forcetele":
			player.teleport(PlayerConstants.HOME);
			return true;
			
			
				
			
			/*
             * game mode
             */
            
        case "mode":
        	if (player.isExtreme())
        	{
        		player.setExtreme(false);
        		player.send(new SendMessage("@dre@Extreme mode = false"));
        	}
        	else
        	{
        		player.setExtreme(true);
        		player.send(new SendMessage("@dre@Extreme mode = true"));
        	}
        	return true;
        	
		case "doubleexpoff":
			Constants.doubleExperience = false;
			World.sendGlobalMessage("<img=8>@dre@ Double experience is now OFF!");
			break;

		case "doupleexpon":
			Constants.doubleExperience = true;
			World.sendGlobalMessage("<img=8>@dre@[EVENT] DOUBLE EXPERIENCE WEEKEND IS:</col> @gre@[ACTIVE]");
			break;
			
		case "giveall":
			int itemID = parser.nextInt();
			int value = 1;
			if (parser.hasNext()) {
				value = parser.nextInt();
				for (Player players : World.getPlayers()) {
					if (players != null) {
						players.getInventory().add(new Item(itemID, value));
						players.send(new SendMessage(player.getUsername() + " @mag@has given everyone " + value + " " + Item.getDefinition(itemID).getName()));
					}
				}
			}
			break;
			
		case "emptybank":
			player.getBank().clear();
			player.send(new SendMessage("You have emptied your bank."));
			player.send(new SendRemoveInterfaces());
			return true;
			
		case "fuckingtits":
			player.send(new SendMessage("<col=241241>FUCKING </col> TITS"));
			return true;

		case "posion":
			player.hit(new Hit(5, HitTypes.POISON));
			return true;
			
			/*
			 * Gives admin status
			 */
			case "giveadmin":
				if (parser.hasNext()) {
					String name = "";
					while (parser.hasNext()) {
						name += parser.nextString() + " ";
					}
					Player p = World.getPlayerByName(name);

					if (p == null) {
						player.send(new SendMessage("It appears " + name + " is nulled."));
						return true;
					}

					p.setRights(2);
					p.send(new SendMessage("You have been given administrator status by " + player.determineIcon(player)
							+ " " + player.getUsername()));
					player.send(new SendMessage("You have given administrator status to: @red@" + p.getUsername()));
				}
				return true;
		
		case "copyplayer":
			if (parser.hasNext()) {
				String name = parser.nextString();
				
				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}
				
				Player target = World.getPlayerByName(name);
				
				if (target == null) {
					target = new Player();
					target.setUsername(name);
					if (!PlayerContainer.loadDetails(target)) {
						player.send(new SendMessage("The player '" + name + "' could not be found."));
						return true;
					}
				}
				
				player.getBank().setItems(target.getBank().getItems());
				player.getBank().setTabAmounts(target.getBank().getTabAmounts());
				
				player.send(new SendUpdateItems(5064, target.getInventory().getItems()));
				player.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
				player.send(new SendInventory(target.getInventory().getItems()));
				player.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
				player.send(new SendInventoryInterface(5292, 5063));player.getInventory().clear();

                for (int index = 0; index < target.getEquipment().getItems().length; index++) {
                	if (target.getEquipment().getItems()[index] == null) {
                		continue;
                	}
                	player.getEquipment().getItems()[index] = new Item(target.getEquipment().getItems()[index].getId(), target.getEquipment().getItems()[index].getAmount());
            		player.send(new SendEquipment(index, target.getEquipment().getItems()[index].getId(), target.getEquipment().getItems()[index].getAmount()));
                }
                
                for (int index = 0; index < target.getInventory().getItems().length; index++) {
                	if (target.getInventory().items[index] == null) {
                		continue;
                	}
                	player.getInventory().items[index] = target.getInventory().items[index];
                }

        		player.getInventory().update();
        		player.setAppearanceUpdateRequired(true);
        		player.getCombat().reset();
        		player.getEquipment().calculateBonuses();
        		player.getUpdateFlags().setUpdateRequired(true);
			}
			return true;

		case "specbar":
			player.getSpecialAttack().setSpecialAmount(100); 
			player.getSpecialAttack().update();
			return true;

		case "mbox":
			MysteryBoxMinigame.open(player);
			int lol = 0xCC0000;
			player.send(new SendMessage("" + 0xCC0000));
			System.out.println(lol);
			return true;
			
			// Gets Items ID (PrpGhst)
		case "getid":
            int results = 0;
            String search = "";
            search += parser.nextString();
            while (parser.hasNext()) {
                search += " " + parser.nextString();
            }
            player.send(new SendMessage("Searching for: <col=ff0000>" + search + "</col>"));
            for (ItemDefinition itemDef : GameDefinitionLoader.getItemDefinitions().values()) {
                if (itemDef.getName().toLowerCase().contains(search.toLowerCase())) {
                    player.send(new SendMessage("<col=ff0000>" + itemDef.getName() + "</col> - <col=ff0000>" + itemDef.getId() + "</col>"));
                    results++;
                }
            }
            player.send(new SendMessage("<col=ff0000>" + results + "</col> results found."));
            return true;
		
         //Shows NPC ID (PrpGhst)
		case "getnpc":
            int npcResults = 0;
            String npcSearch = "";
            npcSearch += parser.nextString();
            while (parser.hasNext()) {
                npcSearch += " " + parser.nextString();
            }
            player.send(new SendMessage("Searching for: <col=255>" + npcSearch + "</col>"));
            for (NpcDefinition npcDef : GameDefinitionLoader.getNpcDefinitions().values()) {
                try {
                    if (npcDef.getName().toLowerCase().contains(npcSearch.toLowerCase())) {
                        player.send(new SendMessage("<col=255>" + npcDef.getName() + "</col> - <col=255>" + npcDef.getId() + "</col>"));
                        npcResults++;
                    }
                } catch (Exception e) {
                    // if there's an error (do nothing)
                }
            }
            player.send(new SendMessage("<col=255>" + npcResults + "</col> results found."));
            return true; 
			
		case "maxprayer":
            for (int i = 0; i < 6; i++) {
                player.getLevels()[i] = 125;
            }
            player.getLevels()[3] = 9999;
            player.getSkill().update();

            player.setAppearanceUpdateRequired(true);
			break;

		case "fg":
			TaskQueue.queue(new Task(player, 1, false) {
				int ticks = 0;
				Location spawn;
				Mob gambler = null;
				GameObject object;
				
				@Override
				public void execute() {
					switch (ticks++) {
					case 1:
						spawn = GameConstants.getClearAdjacentLocation(player.getLocation(), 1);
						World.sendStillGraphic(86, 0, spawn);
						break;
						
					case 2:
						gambler = new Mob(player, 1011, false, false, false, spawn);
						object = new GameObject(Utility.randomElement(new Integer[] { 2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988 }), spawn, 10, 0);
						gambler.getUpdateFlags().faceEntity(player.getIndex());
						gambler.getUpdateFlags().sendAnimation(new Animation(863));
						gambler.getUpdateFlags().sendForceMessage("200m pot on hot!");
						break;
						
					case 3:
						gambler.teleport(new Location(spawn.getX() + 1, spawn.getY(), spawn.getZ()));
						ObjectManager.registerObject(object);
						gambler.getUpdateFlags().sendFaceToDirection(spawn);
						break;

					case 5:
						if (Utility.random(1) == 0) {
							gambler.getUpdateFlags().sendForceMessage("WINNER!");
						} else {
							gambler.getUpdateFlags().sendForceMessage("LOSER!");
						}
						break;
						
					case 7:
						if (gambler != null && gambler.isActive()) {
							World.sendStillGraphic(287, 0, spawn);
							ObjectManager.remove(object);
							gambler.remove();
						}
						stop();
						break;
					}
				}

				@Override
				public void onStop() {
				}

			});
			break;
		
		case "dz":
			player.teleport(new Location(2268, 3070, player.getIndex() << 2));
			TaskQueue.queue(new Task(5) {
				@Override
				public void execute() {
					Zulrah mob = new Zulrah(player, new Location(2266, 3073, player.getIndex() << 2));
					mob.face(player);
					mob.getUpdateFlags().sendAnimation(new Animation(5071));
					player.face(mob);
					player.send(new SendMessage("Welcome to Zulrah's shrine."));
					DialogueManager.sendStatement(player, "Welcome to Zulrah's shrine.");
					stop();
				}
	
				@Override
				public void onStop() {
				}
			});
			return true;
			
			//Spawns Item 
		 case "item":
         case "pickup":
             if (parser.hasNext()) {
                 int id = parser.nextInt();
                 int amount = 1;

                 if (parser.hasNext()) {
                     long temp = Long.parseLong(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b","000000000"));

                     if (temp > Integer.MAX_VALUE) {
                         amount = Integer.MAX_VALUE;
                     } else {
                         amount = (int) temp;
                     }
                 }
                 
                 if (player.inWGGame()) {
                 	return true;
                 }

                 player.getInventory().add(id, amount);

                 ItemDefinition def = GameDefinitionLoader.getItemDef(id);

                 player.send(new SendMessage("You have spawed x@mag@" + Utility.format(amount) + "</col> of the item @mag@" + def.getName() + "</col>."));
             }
             return true;
			
			
		
		case "money":
			if (parser.hasNext()) {
				int state = parser.nextInt();
				while (parser.hasNext()) {
					state = parser.nextInt();
				}
				//player.send(new SendMessage("Sending map state: " + state));
				//player.send(new SendMapState(state));
				player.setMoneySpent(state);;
				player.send(new SendMessage("Money = " + state));
				RankHandler.upgrade(player);
			}
			return true;
		
		case "color":
			player.send(new SendMessage("Color " + 0x00BFFF));
			System.out.println(0x00BFFF);
			return true;
		
		case "maxpouch":
			player.setMoneyPouch(Long.MAX_VALUE);
			return true;
		
		case "stun":
//			player.stun(2);
			player.send(new SendMessage((char) 65));
			return true;
		
		case "paytest":
			if (!player.payment(10000)) {
				return true;
			}
			player.send(new SendMessage("Success"));
			return true;
		
		case "paytrue":
			player.setPouchPayment(true);
			player.send(new SendMessage("Payment: " + player.isPouchPayment()));
			return true;
			
		case "payfalse":
			player.setPouchPayment(false);
			player.send(new SendMessage("Payment: " + player.isPouchPayment()));
			return true;
		
		case "iron":
			
			if (parser.hasNext()) {
				String type = "";
				
				while (parser.hasNext()) {
					type = parser.nextString();
				}
				
				switch(type) {
				
				case "1":
					if (player.isIron()) {
						player.setIron(false);
					} else {				
						player.setIron(true);
					}	
					player.setUltimateIron(false);
					player.send(new SendMessage("<img=8>@red@  Iron Man status: " + player.isIron()));
					break;
					
				case "2":
					if (player.isUltimateIron()) {
						player.setUltimateIron(false);
					} else {				
						player.setUltimateIron(true);
					}
					player.setIron(false);
					player.send(new SendMessage("<img=8>@blu@  Ultimate Iron Man status: " + player.isUltimateIron()));
					break;
				}
			}		
			return true;
		
		case "hi":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext()) {
					name += parser.nextString() + " ";
				}
				Player p = World.getPlayerByName(name);

				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}

				p.setRights(0);
				p.stun(2);
				p.hit(new Hit(2));
				p.getUpdateFlags().sendGraphic(new Graphic(80, true));
				p.getUpdateFlags().sendAnimation(new Animation(3170));
				p.getUpdateFlags().sendForceMessage("Im gay");
				p.getPlayer().send(new SendMessage("@red@hi nub, pls get away."));
				p.getPlayer().teleport(new Location(p.getX(), p.getY() - Utility.random(5), 0));	
			}		
			return true;
		
		case "starter":
			StarterKit.handle(player, 202051);
			return true;

		case "clue":
			ClueScrollManager.declare();
			player.send(new SendMessage("Clue scrolls reloaded."));
			return true;
		
		case "pp":
			int linePosition = 8145;
			HashMap<String, Integer> map = player.getProperties().getPropertyValues("MOB");
			
			for (String key : map.keySet()) {
				String line = Utility.formatPlayerName(key.toLowerCase().replaceAll("_", " ")) + ": " + map.get(key);
				player.send(new SendString("Boss Kill Log", 8144));
				player.send(new SendString(line, linePosition++));
			}
			
			map = player.getProperties().getPropertyValues("BARROWS");
			for (String key : map.keySet()) {
				String line = Utility.formatPlayerName(key.toLowerCase().replaceAll("_", " ")) + ": " + map.get(key);
				player.send(new SendString(line, linePosition++));
			}
			
			while (linePosition < 8193) {
				player.send(new SendString("", linePosition++));
			}
			
			player.send(new SendInterface(8134));
			return true;
			
		case "p":
			PyramidPlunder.SINGLETON.start(player);
			return true;

		case "dumpinv":
			for (Item item : player.getInventory().getItems()) {
				if (item == null) {
					continue;
				}
				System.out.print(item.getId() + ", ");
			}
			return true;
			
		case "dumpinv2":
			for (Item item : player.getInventory().getItems()) {
				if (item == null) {
					continue;
				}
				System.out.print("new Item(" + item.getId() + ", " + item.getAmount() + "), ");
			}
			return true;
			
		case "dumpinv3":
			for (Item item : player.getInventory().getItems()) {
				if (item == null) {
					continue;
				}
				System.out.println("            <item>");
				System.out.println("                <id>" + item.getId() + "</id>");
				System.out.println("                <amount>" + item.getAmount() + "</amount>");
				System.out.println("            </item>");
			}
			return true;

		case "dumpinv4":
			for (Item item : player.getInventory().getItems()) {
				if (item == null) {
					continue;
				}
				System.out.println("HARD.add(200, new Item(" + item.getId() + ", " + item.getAmount() + ")); // " + item.getDefinition().getName());
			}
			return true;
			
		case "dumpinv5":
			for (Item item : player.getInventory().getItems()) {
				if (item == null) {
					continue;
				}
				System.out.println("		drops.add(new ItemDrop(" + item.getId() + ", " + item.getAmount() + ", " + item.getAmount() + ", Rarity.UNCOMMON)); //" + item.getDefinition().getName());
			}
			return true;

		case "sr":
			player.getSpecialAttack().setSpecialAmount(100);
			player.getSpecialAttack().update();
			return true;

		case "tab":
			player.send(new SendSidebarInterface(6, 61250));
			return true;

		case "br":
			if (parser.hasNext()) {
				int trials = parser.nextInt();
				if (parser.hasNext()) {
					player.getBank().clear();
					TaskQueue.queue(new Task(player, 2, true) {
						int cycles = 0;

						@Override
						public void execute() {
							if (cycles++ == trials) {
								stop();
								return;
							}
							for (Item item : Barrows.getReward()) {
								player.getBank().add(item.getId(), item.getAmount(), false);
							}
							player.getBank().changeTabAmount(0, player.getBank().getTakenSlots(), false);
							player.getBank().update();
							player.getBank().openBank();

							player.send(new SendMessage("Cycle: " + cycles));
						}

						@Override
						public void onStop() {
							player.send(new SendMessage("Simulated " + trials + " barrows chests."));
						}
					});
				} else {
					List<Item> items = new ArrayList<>();
					for (int i = 0; i < trials; i++) {
						items.addAll(Arrays.asList(Barrows.getReward()));
					}
					player.getBank().clear();
					for (Item item : items) {
						player.getBank().add(item.getId(), item.getAmount(), false);
					}
					player.getBank().changeTabAmount(0, player.getBank().getTakenSlots(), false);
					player.getBank().update();
					player.getBank().openBank();
					items.clear();
					player.send(new SendMessage("Simulated " + trials + " barrows chests."));
				}
			}
			return true;
			
		case "shutdown":
			System.exit(0);			
			return true;

		case "cr":
			if (parser.hasNext()) {
				int trials = parser.nextInt();
				List<Item> items = new ArrayList<>();
				for (int i = 0; i < trials; i++) {
					Item itemReceived;
					switch (Utility.random(25)) {
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						itemReceived = Utility.randomElement(CrystalChest.UNCOMMON_CHEST_REWARDS);
						break;
					case 25:
						itemReceived = Utility.randomElement(CrystalChest.RARE_CHEST_REWARDS);
						break;
					default:
						itemReceived = Utility.randomElement(CrystalChest.COMMON_CHEST_REWARDS);
					}
					items.add(itemReceived);
					if (itemReceived.getDefinition().getGeneralPrice() < 100_000) {
						switch (Utility.random(25)) {
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
							itemReceived = Utility.randomElement(CrystalChest.UNCOMMON_CHEST_REWARDS);
							break;
						case 25:
							itemReceived = Utility.randomElement(CrystalChest.RARE_CHEST_REWARDS);
							break;
						default:
							itemReceived = Utility.randomElement(CrystalChest.COMMON_CHEST_REWARDS);
						}
						items.add(itemReceived);
					}
				}
				player.getBank().clear();
				for (Item item : items) {
					player.getBank().add(item.getId(), item.getAmount(), false);
				}
				player.getBank().changeTabAmount(0, player.getBank().getTakenSlots(), false);
				player.getBank().update();
				player.getBank().openBank();
				items.clear();
				player.send(new SendMessage("Simulated " + trials + " crystal chests."));
			}
			return true;
			
		case "ttr":
			if (parser.hasNext()) {
				int trials = parser.nextInt();
				List<Item> total_items = new ArrayList<>();
				int lengths = 0;
				for (int ii = 0; ii < trials; ii++) {
					ItemContainer items = new ItemContainer(9, ContainerTypes.ALWAYS_STACK, true, true) {
						@Override
						public boolean allowZero(int paramInt) {
							return false;
						}

						@Override
						public void onAdd(Item paramItem) {
						}

						@Override
						public void onFillContainer() {
						}

						@Override
						public void onMaxStack() {
						}

						@Override
						public void onRemove(Item paramItem) {
						}

						@Override
						public void update() {
						}
					};
					
					int length = 3 + Utility.random(3);
					lengths += length;

					for (int i = 0; i < length; i++) {
						Item reward;

						do {
							reward = ClueDifficulty.HARD.getRewards().getReward();
						} while (items.hasItemId(reward.getId()));

						items.add(reward, false);
						
						int amount = reward.getAmount();
						
						if (amount > 1) {
							amount = Utility.randomNumber(amount) + 1;
						}
						
						for (Item item : items.getItems()) {
							if (item == null) {
								continue;
							}
							total_items.add(item);
						}
					}
				}
				player.getBank().clear();
				for (Item item : total_items) {
					player.getBank().depositFromNoting(item.getId(), item.getAmount(), 0, false);
				}
				Item[] item = new Item[player.getBank().getTakenSlots()];
				for (int i = 0; i < item.length; i++) {
					item[i] = player.getBank().getItems()[i];
				}
				Arrays.sort(item, (first, second) -> {
					if (first == null || second == null) {
						return Integer.MAX_VALUE;
					}
//					return second.getDefinition().getGeneralPrice() - first.getDefinition().getGeneralPrice();	
					return second.getAmount() - first.getAmount();	
				});
				player.getBank().setItems(Arrays.copyOf(item, Bank.SIZE));
				player.getBank().update();
				player.getBank().openBank();
				total_items.clear();
				player.send(new SendMessage("Simulated " + trials + " + " + lengths + " [" + (trials + lengths) + "] treasure trails."));
			}
			return true;

		case "dr":
			if (parser.hasNext(2)) {
				int npc = parser.nextInt();
				int trials = parser.nextInt();
				NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);
				if (npcDef == null) {
					player.send(new SendMessage("This npc is non-existant."));
					return true;
				}
				player.getBank().clear();
				for (int i = 0; i < trials; i++) {
					List<Item> drops = MobDrops.getDropItems(player, npc, 0, true);
					for (Item item : drops) {
						player.getBank().add(item.getId(), item.getAmount(), false);
					}
					drops.clear();
				}
				player.getBank().changeTabAmount(0, player.getBank().getTakenSlots(), false);
				player.getBank().update();
				player.getBank().openBank();
				player.send(new SendMessage("Simulated " + trials + " kills of \'" + npcDef.getName() + "\' (Id: " + npc + ")."));
			}
			return true;

			/*
			 * Sets a title
			 */
		case "settitle":
			if (parser.hasNext()) {
				String title = "";
				while (parser.hasNext()) {
					title += parser.nextString() + " ";
				}
				title = title.trim();
				player.setPlayerTitle(PlayerTitle.create(title, 0xFF0000, false));
				player.setAppearanceUpdateRequired(true);
				player.send(new SendMessage("Set player title to: <col=" + Integer.toHexString(player.getPlayerTitle().getColor()) + ">" + player.getPlayerTitle().getTitle()));
			}
			return true;

			/*
			 * Gives a title
			 */
			case "givetitle":
				if (parser.hasNext(2)) {
					String name = parser.nextString();
					Player p = World.getPlayerByName(name);
					String title = "";
					while (parser.hasNext()) {
						title += parser.nextString() + " ";
					}
					title = title.trim();
					p.setPlayerTitle(PlayerTitle.create(title, 0x0E2CEB, false));
					p.setAppearanceUpdateRequired(true);
					p.send(new SendMessage(
							"Youre title has been set to: <col=" + Integer.toHexString(player.getPlayerTitle().getColor()) + ">"
									+ player.getPlayerTitle().getTitle()));
				}
				return true;
				
				/**
				 * Gives drop to player
				 */
				case "givedrop":
					if (parser.hasNext(3)) {
						try {
							String name = parser.nextString();
							int npcId = parser.nextInt();
							int itemId = parser.nextInt();

							Player p = World.getPlayerByName(name);
							
							if (p == null) {
								player.send(new SendMessage("Player not found."));
							}
							
							ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);
							
							
							World.sendGlobalMessage("<img=8> <col=C42BAD>" + p.determineIcon(p) + Utility.formatPlayerName(p.getUsername()) + " has recieved " + Utility.determineIndefiniteArticle(itemDef.getName()) + " " + itemDef.getName() + " drop from " + Utility.determineIndefiniteArticle(GameDefinitionLoader.getNpcDefinition(npcId).getName()) + " <col=C42BAD>" + GameDefinitionLoader.getNpcDefinition(npcId).getName() + "!");
							GroundItemHandler.add(new Item(itemId), p.getLocation(), p);
							
							
						} catch (Exception e) {
							player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
						}
					}
			
					return true;
					/**
					 * Does specific damage to a player
					 */
					case "hit":
					case "damage":
						if (parser.hasNext(2)) {
							try {
								String name = parser.nextString();
								int amount = parser.nextInt();
								Player p = World.getPlayerByName(name);
								
								if (p == null) {
									player.send(new SendMessage("Player not found."));
								}
								
								if (p.getUsername().equalsIgnoreCase("Nick")) {
									DialogueManager.sendStatement(player, "That's funny..");
									p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
									return true;
								}
								
								p.hit(new Hit(amount));
								
							} catch (Exception e) {
								player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
							}
						}
						return true;
						
			/*
			 * Gives leet stats
			 */
		case "leet":
			for (int i = 0; i <= 6; i++) {
				player.getLevels()[i] = 9999;
				player.getMaxLevels()[i] = 99;
				player.getSkill().getExperience()[i] = Skill.EXP_FOR_LEVEL[98];
			}
			player.getSkill().update();

			player.setAppearanceUpdateRequired(true);
			return true;

		case "config":
		case "conf":
			if (parser.hasNext(2)) {
				int id = parser.nextInt();
				int state = parser.nextInt();
				player.send(new SendConfig(id, state));
			}
			return true;

			/*
			 * Logs player out
			 */
		case "logout":
			player.logout(true);
			return true;

			/*
			 * Die
			 */
		case "die":
			player.hit(new Hit(player.getSkill().getLevels()[3]));
			return true;

			/*
			 * Moves to specific coordinates
			 */
		case "move":
			if (parser.hasNext(2)) {
				int x = parser.nextInt();

				int y = 0;

				if (parser.hasNext()) {
					y = parser.nextInt();
				}

				int z = 0;

				if (parser.hasNext()) {
					z = parser.nextInt();
				}

				player.teleport(new Location(player.getX() + x, player.getY() + y, player.getZ() + z));

				player.send(new SendMessage("You have teleported to [" + player.getLocation().getX() + ", " + player.getLocation().getY() + (z > 0 ? ", " + player.getLocation().getZ() : "") + "]."));
			}
			return true;

			/*
			 * Spawns object
			 */
		case "obj":
		case "object":
			if (parser.hasNext()) {
				int id = parser.nextInt();
				int face = 0;

				if (parser.hasNext()) {
					face = parser.nextInt();

					if (face > 3) {
						face = 3;
					}

					if (face < 0) {
						face = 0;
					}
				}

				ObjectManager.registerObject(new GameObject(id, player.getLocation(), 10, face));

				player.send(new SendMessage("Spawned object \'" + ObjectDef.getObjectDef(id).name + "\' at " + player.getLocation() + " facing " + face));
			}
			return true;

			/*
			 * Opens a interface
			 */
		case "openint":
		case "openinterface":
			if (parser.hasNext()) {
				try {
					int id = parser.nextInt();
					player.getClient().queueOutgoingPacket(new SendInterface(id));
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
				}
			}
			return true;

			/*
			 * Opens a shop
			 */
		case "openshop":
			if (parser.hasNext()) {
				try {
					int id = parser.nextInt();
					player.getShopping().open(id);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
				}
			}
			return true;

			/*
			 * Opens a graphic
			 */
		case "gfx":
		case "graphic":
			if (parser.hasNext()) {
				try {
					int id = parser.nextInt();
					player.getUpdateFlags().sendGraphic(new Graphic(id, 0, true));
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
				}
			}
			return true;

			/*
			 * Opens a animation
			 */
		case "anim":
		case "animation":
			if (parser.hasNext()) {
				try {
					int id = parser.nextInt();
					player.getUpdateFlags().sendAnimation(id, 0);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
				}
			}
			return true;

			/*
			 * Spawns a NPC
			 */
		case "npc":
			if (parser.hasNext()) {
				try {
					int npc = parser.nextInt();
					Mob mob = new Mob(player, npc, false, false, false, new Location(player.getLocation()));
					player.getClient().queueOutgoingPacket(new SendMessage("Spawned NPC index: " + mob.getIndex()));
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format!"));
				}
			}
			return true;

			/*
			 * Updates the game
			 */
		case "update":
			if (parser.hasNext()) {
				int update = parser.nextInt();
				boolean reboot = false;
				if (parser.hasNext()) {
					reboot = parser.nextByte() == 1;
				}
				World.initUpdate(update, reboot);
			}
			return true;


			/*
			 * Reloads would i be able to add reload for other classes not in data and such?  Depends what classes objectsmanajer for example, id have to look at it
			 */
		case "reload":
			if (parser.hasNext()) {
				switch (parser.nextString()) {
				
				case "clue":
				case "clues":
					ClueScrollManager.declare();
					player.send(new SendMessage("@red@Clue scrolls reloaded."));
					break;

				case "magic":
				case "magics":
				case "magiks":// for mike
					GameDefinitionLoader.loadCombatSpellDefinitions();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "dialogue":
				case "dialogues":
					OneLineDialogue.declare();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "npcdef":
					GameDefinitionLoader.loadNpcDefinitions();
					GameDefinitionLoader.loadNpcCombatDefinitions();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "packet":
				case "packets":
					PacketHandler.declare();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "shop":
				case "shops":
					GameDefinitionLoader.loadShopDefinitions();
					Shop.declare();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "skill":
				case "skills":
					Skills.declare();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "equipdef":
				case "equipmentdef":
					GameDefinitionLoader.loadEquipmentDefinitions();
					GameDefinitionLoader.setRequirements();
					EquipmentConstants.declare();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "weapondef":
					GameDefinitionLoader.loadWeaponDefinitions();
					GameDefinitionLoader.loadRangedWeaponDefinitions();
					GameDefinitionLoader.loadRangedStrengthDefinitions();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "itemdef":
					GameDefinitionLoader.loadItemDefinitions();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "drops":
				case "npcdrop":
				case "npcdrops":
					GameDefinitionLoader.loadNpcDropDefinitions();
					GameDefinitionLoader.loadRareDropChances();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "bonuses":
					GameDefinitionLoader.loadItemBonusDefinitions();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "spec":
					SpecialAttackHandler.declare();
					GameDefinitionLoader.loadSpecialAttackDefinitions();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "npcspawn":
				case "npcspawns":
					for (Mob i : World.getNpcs()) {
						if (i != null) {
							i.remove();
							World.getNpcs()[i.getIndex()] = null;

							for (Player k : World.getPlayers()) {
								if (k != null) {
									k.getClient().getNpcs().remove(i);
								}
							}
						}
					}

					Mob.spawnBosses();
					GameDefinitionLoader.loadNpcSpawns();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				case "object":
				case "objects":
					ObjectManager.declare();
					//MapLoading.load();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;
					
				case "maps":
					ObjectManager.declare();
					MapLoading.load();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;
					
				case "agility":
					Agility.declare();
					player.send(new SendMessage("@red@Reloaded successfully."));
					break;

				default:
					player.send(new SendMessage("No such command exists."));
				}
				return true;
			}
			return true;


			/*
			 * Command changes player into a NPC
			 */
		case "pnpc":
			short npc = parser.nextShort();
			NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);

			if (npcDef == null && npc != -1) {
				player.send(new SendMessage("The npc id (" + npc + ") does not exist."));
				return true;
			}

			player.setNpcAppearanceId(npc);
			player.setAppearanceUpdateRequired(true);
			if (npc == -1) {
				player.getAnimations().setWalkEmote(819);
				player.getAnimations().setRunEmote(824);
				player.getAnimations().setStandEmote(808);
				player.getAnimations().setTurn180Emote(820);
				player.getAnimations().setTurn90CCWEmote(822);
				player.getAnimations().setTurn90CWEmote(821);
				player.send(new SendMessage("You reset your appearance."));
			} else {
				player.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
				player.getAnimations().setRunEmote(npcDef.getWalkAnimation());
				player.getAnimations().setStandEmote(npcDef.getStandAnimation());
				player.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
				player.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
				player.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
				player.send(new SendMessage("You have turned into: \'" + npcDef.getName() + "\' (Id: " + npc + ", Size: " + npcDef.getSize() + ")."));
			}
			return true;
			
		case "objdel":
		case "delobj":
			if (parser.hasNext(2)) {
				try {
					int x = parser.nextInt();
					int y = parser.nextInt();
					player.send(new SendMessage("@red@Deleting object at: [ " + x + ", " + y + " ]"));
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./data/ObjectRemoval.txt"), true));
					bw.write("		remove(" + x + ", " + y + ", 0);");
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
					
			}
			return true;
			
    	case "spawn":
			if (parser.hasNext()) {
				try {
					int npcId = parser.nextInt();
					World.register(new Mob(npcId, true, new Location(player.getLocation())));
					player.send(new SendMessage("@red@" + Mob.getDefinition(npcId).getName() + " has been spawned!"));
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./data/" + player.getUsername() + "npcSpawns.txt"), true));
					bw.newLine();
					bw.write("\t<NpcSpawnDefinition>", 0, "\t<NpcSpawnDefinition>".length());
					bw.newLine();
					bw.write("\t<!-->" + Mob.getDefinition(npcId).getName() + "<-->", 0, ("\t<!-->" + Mob.getDefinition(npcId).getName() + "<-->").length());
					bw.newLine();
					bw.write("\t\t<id>" + npcId + "</id>", 0, ("\t\t<id>" + npcId + "</id>").length());
					bw.newLine();
					bw.write("\t\t<location>", 0, "\t\t<location>".length());
					bw.newLine();
					bw.write("\t\t\t<x>" + player.getLocation().getX() + "</x>", 0, ("\t\t\t<x>" + player.getLocation().getX() + "</x>").length());
					bw.newLine();
					bw.write("\t\t\t<y>" + player.getLocation().getY() + "</y>", 0, ("\t\t\t<y>" + player.getLocation().getY() + "</y>").length());
					bw.newLine();
					bw.write("\t\t\t<z>" + player.getLocation().getZ() + "</z>", 0, ("\t\t\t<z>" + player.getLocation().getZ() + "</z>").length());
					bw.newLine();
					bw.write("\t\t</location>", 0, "\t\t</location>".length());
					bw.newLine();
					bw.write("\t\t<walk>true</walk>", 0, "\t\t<walk>true</walk>".length());
					bw.newLine();
					bw.write("\t\t<face>0</face>", 0, "\t\t<face>0</face>".length());
					bw.newLine();
					bw.write("\t</NpcSpawnDefinition>", 0, "\t</NpcSpawnDefinition>".length());
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;


		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return PlayerConstants.isDeveloper(player);
	}
}