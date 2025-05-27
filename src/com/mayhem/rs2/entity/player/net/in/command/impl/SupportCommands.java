package com.mayhem.rs2.entity.player.net.in.command.impl;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.content.interfaces.impl.SupportCommandsInterface;
import com.mayhem.rs2.content.io.PlayerSave.PlayerContainer;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.net.in.command.Command;
import com.mayhem.rs2.entity.player.net.in.command.CommandParser;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendInventory;
import com.mayhem.rs2.entity.player.net.out.impl.SendInventoryInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
import com.mayhem.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * A list of commands accessible to all players with the Support rank.
 * 
 * @author Qwerty159063/Trebble
 */
public class SupportCommands implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		
		/*
		 * Teleport to player or teleport player to me
		 */
	case "t2":
	case "teleto":
		if (parser.hasNext()) {
			String name = parser.nextString();

			while (parser.hasNext()) {
				name += " " + parser.nextString();
			}

			name = name.trim();

			Player target = World.getPlayerByName(name);

			if (target == null) {
				player.send(new SendMessage("The player \'" + name + "\' could not be found."));
				return true;
			}

			player.teleport(target.getLocation());
			player.send(new SendMessage("You have teleported to \'" + name + "\''s position."));
		}
		return true;

	case "t2m":
	case "teletome":
		if (parser.hasNext()) {
			String name = parser.nextString();

			while (parser.hasNext()) {
				name += " " + parser.nextString();
			}

			name = name.trim();

			Player target = World.getPlayerByName(name);

			if (target == null) {
				player.send(new SendMessage("The player \'" + name + "\' could not be found."));
				return true;
			}

			target.teleport(player.getLocation());
			player.send(new SendMessage("You have teleported the player \'" + name + "\' to your position."));

		}
		return true;

		
		/*
		 * Search the economy
		 */
		case "ecosearch":
			if (parser.hasNext()) {
				try {
					int id = parser.nextInt();
					long amount = 0L;
					for (Player p : World.getPlayers()) {
						if ((p != null) && (p.isActive())) {
							amount += p.getInventory().getItemAmount(id);
							amount += p.getBank().getItemAmount(id);
						}
					}
					player.getClient().queueOutgoingPacket(new SendMessage("There is currently @dre@" + Utility.format(amount) + "x @bla@of: " + Item.getDefinition(id).getName() + " in the game."));
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
			}
			return true;
		
		case "staffzone":
		case "staffarea":
			if (player.inWilderness()) {
				player.send(new SendMessage("You cannot teleport out of the wilderness."));
				return true;
			}
			player.teleport(PlayerConstants.STAFF_AREA);
			return true;
		
		case "jailarea":
			player.teleport(new Location(2767, 2795, 0));
			return true;
		
		case "checkbank":
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
				
				player.send(new SendMessage("@blu@" + target.getUsername() + " has " + Utility.format(target.getMoneyPouch()) + " in their pouch."));
				player.send(new SendUpdateItems(5064, target.getInventory().getItems()));
				player.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
				player.send(new SendInventory(target.getInventory().getItems()));
				player.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
				player.send(new SendInventoryInterface(5292, 5063));
			}
			return true;

		/*
		 * List of support commands
		 */
		case "supportcommands":
		case "supportcommand":
			player.send(new SendString("FalconExpress Support Command List", 8144));
			InterfaceHandler.writeText(new SupportCommandsInterface(player));
			player.send(new SendInterface(8134));
			return true;

			/*
			 * Log packets for specific player
			 */
		case "logpackets":
			if (parser.hasNext()) {
				try {
					String name = parser.nextString();
					Player p = World.getPlayerByName(name.replaceAll("_", " "));
					if (p == null) {
						player.send(new SendMessage("Player not found."));
					} else {
						p.getClient().setLogPlayer(true);
						player.send(new SendMessage("Now logging incoming packets for: " + p.getUsername() + "."));
					}
				} catch (Exception e) {
					player.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
				}
			}
			return true;

			/*
			 * Unlog packets for specific player
			 */
		case "unlogpackets":
			if (parser.hasNext()) {
				try {
					String name = parser.nextString();
					Player p = World.getPlayerByName(name);
					if (p == null) {
						player.send(new SendMessage("Player not found."));
					} else {
						p.getClient().setLogPlayer(false);
						player.send(new SendMessage("No longer logging incoming packets for: " + p.getUsername() + "."));
					}
				} catch (Exception e) {
					player.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return player.getRights() == 13;
		
	}
}
