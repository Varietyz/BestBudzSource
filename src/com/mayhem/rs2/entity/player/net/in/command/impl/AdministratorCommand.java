package com.mayhem.rs2.entity.player.net.in.command.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.mayhem.core.definitions.ItemDefinition;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.bank.Bank;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.event.impl.DoubleGold;
import com.mayhem.rs2.content.io.PlayerSave;
import com.mayhem.rs2.content.io.PlayerSave.PlayerDetails;
import com.mayhem.rs2.content.minigames.pvptournament.PvPTournament;
import com.mayhem.rs2.content.skill.Skill;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.net.in.command.Command;
import com.mayhem.rs2.entity.player.net.in.command.CommandParser;
import com.mayhem.rs2.entity.player.net.out.impl.SendEquipment;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * A list of commands accessible to all players with the administrator's rank.
 * 
 * @author Michael | Chex
 * @author Daniel | Play Boy
 */
public class AdministratorCommand implements Command {

    @Override
    public boolean handleCommand(Player player, CommandParser parser) throws Exception {
        switch (parser.getCommand()) {
        
        case "ban":
			if (parser.hasNext()) {
				try {
					String name = parser.nextString();
					int hours = -1;
					
					if (parser.hasNext()) {
						hours = parser.nextInt();
					}
					
					Player target = World.getPlayerByName(name);
					boolean save = false;
					if (target == null) {
						target = new Player();
						target.setUsername(Utility.formatPlayerName(name));
						if (!PlayerDetails.loadDetails(target)) {
							player.send(new SendMessage("The player '" + Utility.formatPlayerName(name) + "' was not found."));
							return true;
						}
						save = true;
					}
					
					if (PlayerConstants.isOwner(target)) {
						DialogueManager.sendStatement(player, "Fuck off Pleb.");
						target.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
						return true;
					}
					
					String time = "permanently";
					
					if (hours > 0) {
						time = "for " + hours + " hour(s)";
					}
					
					player.send(new SendMessage("Successfully banned " + Utility.formatPlayerName(name) + " " + time + "."));
					target.setBanned(true);
					if (hours == -1) {
						target.setBanLength(-1);
					} else {
						target.setBanLength(System.currentTimeMillis() + hours * 3_600_000);
					}
					if (save) {
						PlayerSave.save(target);
					} else {
						target.logout(true);
					}
				} catch (Exception e) {
					player.send(new SendMessage("Invalid format"));
				}
			}
			return true;
        	
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
        	
        case "tournament":
        	int mode = parser.nextInt();
        	int delay = parser.nextInt();
        	PvPTournament.launch(player, mode, delay);
        	return true;
        	
//        case "loopgfx":        	
//        	int start = parser.nextInt();
//        	int end = parser.nextInt();
//        	int index = start;
//        	TaskQueue.queue(new Task(10) {

//				@Override
//				public void execute() {
//					if (index < end) {
//						World.sendStillGraphic(start, 0, player.getLocation());
//						index++;
//						return;
//					}
//					stop();
//				}

//				@Override
//				public void onStop() {}
        		
//        	});
//        	return true;
        	
        case "endtournament":
        	if (World.getTournament() != null)
        		World.getTournament().forceEnd(player);
        	else 
        		player.send(new SendMessage("There's no tournament active currently."));
        	return true;
        	
        case "mode2":
        	if (player.isClassic())
        	{
        		player.setClassic(false);
        		player.send(new SendMessage("@dre@Classic mode = false"));
        	}
        	else
        	{
        		player.setClassic(true);
        		player.send(new SendMessage("@dre@Classic mode = true"));
        	}
        	return true;
        /**	
        case "setlvl":
			if (parser.hasNext(2)) {
				short skill = parser.nextShort();
				short amount = parser.nextShort();
				player.getLevels()[skill] = amount;
				player.getMaxLevels()[skill] = amount;
				player.getSkill().getExperience()[skill] = Skill.EXP_FOR_LEVEL[amount - 1];
				player.getSkill().update();
				player.send(new SendMessage("You set " + Skills.SKILL_NAMES[skill] + " to level " + amount + "."));
			}
			return true;
			*/
        	/**
            case "copy":
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
                    
                	player.getInventory().clear();

                    for (int index = 0; index < p.getEquipment().getItems().length; index++) {
                    	if (p.getEquipment().getItems()[index] == null) {
                    		continue;
                    	}
                    	player.getEquipment().getItems()[index] = new Item(p.getEquipment().getItems()[index].getId(), p.getEquipment().getItems()[index].getAmount());
                		player.send(new SendEquipment(index, p.getEquipment().getItems()[index].getId(), p.getEquipment().getItems()[index].getAmount()));
                    }
                    
                    for (int index = 0; index < p.getInventory().getItems().length; index++) {
                    	if (p.getInventory().items[index] == null) {
                    		continue;
                    	}
                    	player.getInventory().items[index] = p.getInventory().items[index];
                    }

            		player.getInventory().update();
            		player.setAppearanceUpdateRequired(true);
            		player.getCombat().reset();
            		player.getEquipment().calculateBonuses();
            		player.getUpdateFlags().setUpdateRequired(true);
            		DialogueManager.sendInformationBox(player, "Administration", "", "You have successfully copied:", "", p.determineIcon(p) + " " + p.getUsername());
                }
                return true;
                */

                /*
                 * Teleport to specific coordinates
                 */
            case "tele":
                if (parser.hasNext(2)) {
                    int x = parser.nextInt();
                    int y = parser.nextInt();
                    int z = player.getLocation().getZ();

                    if (parser.hasNext()) {
                        z = parser.nextInt();
                    }

                    player.teleport(new Location(x, y, z));

                    player.send(new SendMessage("You have teleported to [" + x + ", " + y + (z > 0 ? ", " + z : "") + "]."));
                }
                return true;

                /*
                 * Gets the player's coordinates
                 */
            case "mypos":
            case "coords":
            case "pos":
                player.send(new SendMessage("You are at: " + player.getLocation() + "."));
                System.out.println("new Location {" + player.getX() + ", " + player.getY() + (player.getZ() > 0 ? ", " + player.getZ() : "") + "},");
                return true;
                
                /**
                /*
                 * Gives a specific item to bank
                 
            case "give":
                if (parser.hasNext()) {
                    String item = parser.nextString();
                    int amount = 1;

                    if (parser.hasNext()) {
                        amount = Integer.parseInt(parser.nextString().toLowerCase().replace("k", "000").replace("m", "000000").replace("b", "000000000"));
                    }

                    player.getBank().clear();

                    List<ItemDefinition> items = (List<ItemDefinition>) GameDefinitionLoader.getItemDefinitions().values().stream().filter(def -> !def.isNote() && def.getName().toLowerCase().contains(item.replace("_", " "))).collect(Collectors.toList());
        			
                    int added = 0;
                    for (ItemDefinition def: items) {
                        if (added < Bank.SIZE) {
                            player.getBank().depositFromNoting(def.getId(), amount, 0, false);
                            added++;
                        }
                    }

                    items.clear();

                    player.getBank().update();
                    player.getBank().openBank();
                    player.send(new SendMessage("Added @red@" + Utility.format(added) + "</col> of items with keywords: @red@" + item + "</col> to your bank."));
                }
                return true;
                
                */

                /*
                 * Does a mass banner
                 */
            case "masssave":
            case "saveall":
                for (Player players: World.getPlayers()) {
                    if (players != null && players.isActive()) {
                        PlayerSave.save(players);
                    }
                }
                player.send(new SendMessage(World.getActivePlayers() + " players have been saved!"));
                return true;
                
                
                
                 // Spawns a item  // Admin Does not need this power
                 
            /**   
            case "item":
            case "pickup":
            case "spawn":
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

                    player.send(new SendMessage("You have spawed x@red@" + Utility.format(amount) + "</col> of the item @red@" + def.getName() + "</col>."));
                }
                return true;
                */
                
                
                /*
                 * Opens bank
                 */
            case "bank":
                player.getBank().openBank();
                return true;

                /*
                 * Master statistics
                 */
                
            /**
            case "master":
            	if (player.getUsername() == "saint plague" || player.getUsername() == "saint_plague") {
            		player.send(new SendMessage("You should be playing on ironman ;)"));
            			return false;
            	} 
                for (int i = 0; i < 25; i++) {
                    player.getLevels()[i] = 99;
                    player.getMaxLevels()[i] = 99;
                    player.getSkill().getExperience()[i] = Skill.EXP_FOR_LEVEL[98];
                }
                player.getSkill().update();

                player.setAppearanceUpdateRequired(true);
                return true;
                
            case "doublegold":
            	World.getEventManager().setEvent(new DoubleGold());
            	return true;
            */

                /*
                 * Sets stats
                 */
            case "set":
                if (parser.hasNext()) {
                    String next = parser.nextString();
                    switch (next) {
                        case "stats":
                            if (parser.hasNext()) {
                                short amount = parser.nextShort();
                                for (int i = 0; i < Skills.SKILL_COUNT; i++) {
                                    player.getLevels()[i] = amount;
                                    player.getMaxLevels()[i] = amount;
                                    player.getSkill().getExperience()[i] = Skill.EXP_FOR_LEVEL[amount - 1];
                                }
                                player.getSkill().update();
                                player.send(new SendMessage("Your stats have been reset."));
                            }
                            return true;

                            /*
                             * Set levels
                             */
                        case "level":
                            if (parser.hasNext(2)) {
                                short skill = parser.nextShort();
                                short amount = parser.nextShort();
                                player.getLevels()[skill] = amount;
                                player.getMaxLevels()[skill] = amount;
                                player.getSkill().getExperience()[skill] = Skill.EXP_FOR_LEVEL[amount - 1];
                                player.getSkill().update();
                                player.send(new SendMessage("You set " + Skills.SKILL_NAMES[skill] + " to level " + amount + "."));
                            }
                            return true;
                           
                    }
                }
                return true;
        }
        return false;
    }

    @
    Override
    public boolean meetsRequirements(Player player) {
        return player.getRights() >= 2 && player.getRights() < 5;
    }
}