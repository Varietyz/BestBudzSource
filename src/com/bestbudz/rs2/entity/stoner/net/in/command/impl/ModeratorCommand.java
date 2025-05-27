package com.bestbudz.rs2.entity.stoner.net.in.command.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.ModCommandsInterface;
import com.bestbudz.rs2.content.io.StonerSave;
import com.bestbudz.rs2.content.io.StonerSave.StonerContainer;
import com.bestbudz.rs2.content.io.StonerSave.StonerDetails;
import com.bestbudz.rs2.content.io.StonerSaveUtil;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.in.command.Command;
import com.bestbudz.rs2.entity.stoner.net.in.command.CommandParser;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBox;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendOpenTab;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSystemBan;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

public class ModeratorCommand implements Command {

	@Override
	public boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception {
	switch (parser.getCommand()) {
	case "stafftab":
	case "tabon":
		stoner.send(new SendString("</col>Rank: " + stoner.deterquarryIcon(stoner) + " " + stoner.deterquarryRank(stoner), 49705));
		if (stoner.getRights() == 1) {
			stoner.send(new SendString("You have limited access.", 49704));
		} else {
			stoner.send(new SendString("You have full access.", 49704));
		}
		stoner.send(new SendSidebarInterface(2, 49700));
		stoner.send(new SendOpenTab(2));
		stoner.send(new SendMessage("Staff tab has been turned on."));
		return true;
	case "taboff":
		stoner.send(new SendSidebarInterface(2, 29400));
		stoner.send(new SendOpenTab(2));
		stoner.send(new SendMessage("Staff tab has been turned off."));
		return true;
	case "ecosearch":
		if (parser.hasNext()) {
			try {
				int id = parser.nextInt();
				long amount = 0L;
				for (Stoner p : World.getStoners()) {
					if ((p != null) && (p.isActive())) {
						amount += p.getBox().getItemAmount(id);
						amount += p.getBank().getItemAmount(id);
					}
				}
				stoner.getClient().queueOutgoingPacket(new SendMessage("There is currently @dre@" + Utility.format(amount) + "x @bla@of: " + Item.getDefinition(id).getName() + " in the game."));
			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	case "staffzone":
	case "staffarea":
		stoner.teleport(StonerConstants.STAFF_AREA);
		return true;

	case "jailarea":
		stoner.teleport(new Location(2767, 2795, 0));
		return true;

	case "checkbank":
		if (parser.hasNext()) {
			String name = parser.nextString();

			while (parser.hasNext()) {
				name += " " + parser.nextString();
			}

			Stoner target = World.getStonerByName(name);

			if (target == null) {
				target = new Stoner();
				target.setUsername(name);
				if (!StonerContainer.loadDetails(target)) {
					stoner.send(new SendMessage("The stoner '" + name + "' could not be found."));
					return true;
				}
			}

			stoner.send(new SendMessage("@blu@" + target.getUsername() + " has " + Utility.format(target.getMoneyPouch()) + " in their pouch."));
			stoner.send(new SendUpdateItems(5064, target.getBox().getItems()));
			stoner.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
			stoner.send(new SendBox(target.getBox().getItems()));
			stoner.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
			stoner.send(new SendBoxInterface(5292, 5063));
		}
		return true;
	case "modcommands":
	case "modcommand":
		stoner.send(new SendString("BestBudz Mod Command List", 8144));
		InterfaceHandler.writeText(new ModCommandsInterface(stoner));
		stoner.send(new SendInterface(8134));
		return true;
	case "ipban":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();
				Stoner p = World.getStonerByName(name);
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				} else {
					if (p.getUsername().equalsIgnoreCase("jaybane")) {
						DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
						p.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
						return true;
					}
					stoner.send(new SendMessage("Success."));
					new SendSystemBan().execute(p.getClient());
					StonerSaveUtil.setIPBanned(p);
					p.logout(true);
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;
	case "ipmute":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();
				Stoner p = World.getStonerByName(name);
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				} else {
					if (p.getUsername().equalsIgnoreCase("jaybane")) {
						DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
						p.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
						return true;
					}
					StonerSaveUtil.setIPMuted(p);
					stoner.send(new SendMessage("Success."));
					p.setMuted(true);
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;
	case "mute":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();
				int hours = -1;
				if (parser.hasNext()) {
					hours = parser.nextInt();
				}
				Stoner target = World.getStonerByName(name);
				boolean save = false;
				if (target == null) {
					target = new Stoner();
					target.setUsername(Utility.formatStonerName(name));
					if (!StonerDetails.loadDetails(target)) {
						stoner.send(new SendMessage("The stoner '" + Utility.formatStonerName(name) + "' was not found."));
						return true;
					}
					save = true;
				}

				if (StonerConstants.isOwner(target)) {
					DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
					target.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
					return true;
				}

				String time = "permanently";

				if (hours > 0) {
					time = "for " + hours + " hour(s)";
				}

				stoner.send(new SendMessage("Successfully muted " + Utility.formatStonerName(name) + " " + time + "."));
				target.setMuted(true);
				if (hours == -1) {
					target.setMuteLength(-1);
				} else {
					target.setMuteLength(System.currentTimeMillis() + hours * 3_600_000L);
				}
				if (save) {
					StonerSave.save(target);
				} else {
					DialogueManager.sendStatement(target, "You have been muted " + time + ".");
					target.send(new SendMessage("You have been muted " + time + "."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;
	case "ban":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();
				int hours = -1;

				if (parser.hasNext()) {
					hours = parser.nextInt();
				}

				Stoner target = World.getStonerByName(name);
				boolean save = false;
				if (target == null) {
					target = new Stoner();
					target.setUsername(Utility.formatStonerName(name));
					if (!StonerDetails.loadDetails(target)) {
						stoner.send(new SendMessage("The stoner '" + Utility.formatStonerName(name) + "' was not found."));
						return true;
					}
					save = true;
				}

				if (StonerConstants.isOwner(target)) {
					DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
					target.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
					return true;
				}

				String time = "permanently";

				if (hours > 0) {
					time = "for " + hours + " hour(s)";
				}

				stoner.send(new SendMessage("Successfully banned " + Utility.formatStonerName(name) + " " + time + "."));
				target.setBanned(true);
				if (hours == -1) {
					target.setBanLength(-1);
				} else {
					target.setBanLength(System.currentTimeMillis() + hours * 3_600_000L);
				}
				if (save) {
					StonerSave.save(target);
				} else {
					target.logout(true);
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;

	case "jail":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();
				int hours = -1;

				if (parser.hasNext()) {
					hours = parser.nextInt();
				}

				Stoner target = World.getStonerByName(name);
				boolean save = false;
				if (target == null) {
					target = new Stoner();
					target.setUsername(Utility.formatStonerName(name));
					if (!StonerDetails.loadDetails(target)) {
						stoner.send(new SendMessage("The stoner '" + Utility.formatStonerName(name) + "' was not found."));
						return true;
					}
					save = true;
				}

				if (StonerConstants.isOwner(target)) {
					DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
					target.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
					return true;
				}

				String time = "permanently";

				if (hours > 0) {
					time = "for " + hours + " hour(s)";
				}

				stoner.send(new SendMessage("Successfully jailed " + Utility.formatStonerName(name) + " " + time + "."));
				target.setJailed(true);
				target.teleport(StonerConstants.JAILED_AREA);
				if (hours == -1) {
					target.setJailLength(-1);
				} else {
					target.setJailLength(System.currentTimeMillis() + hours * 3_600_000L);
				}
				if (save) {
					StonerSave.save(target);
				} else {
					DialogueManager.sendStatement(target, "You have been jailed " + time + ".");
					target.send(new SendMessage("You have been jailed " + time + "."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;

	case "unjail":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();

				Stoner target = World.getStonerByName(name);

				boolean save = false;
				if (target == null) {
					target = new Stoner();
					target.setUsername(Utility.formatStonerName(name));
					if (!StonerDetails.loadDetails(target)) {
						stoner.send(new SendMessage("The stoner '" + Utility.formatStonerName(name) + "' was not found."));
						return true;
					}
					save = true;
				}

				if (StonerSaveUtil.unJailOfflineStoner(target.getUsername())) {
					if (target != null) {
						target.setJailed(false);
						if (save) {
							StonerSave.save(target);
						}
					}
					stoner.send(new SendMessage("Success."));
				} else {
					stoner.send(new SendMessage("Stoner not found."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;
	case "unmute":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();

				Stoner target = World.getStonerByName(name);

				boolean save = false;
				if (target == null) {
					target = new Stoner();
					target.setUsername(Utility.formatStonerName(name));
					if (!StonerDetails.loadDetails(target)) {
						stoner.send(new SendMessage("The stoner '" + Utility.formatStonerName(name) + "' was not found."));
						return true;
					}
					save = true;
				}

				if (StonerSaveUtil.unmuteOfflineStoner(target.getUsername())) {
					if (target != null) {
						target.setMuted(false);
						target.setMuteLength(0);
						if (save) {
							StonerSave.save(target);
						}
					}
					stoner.send(new SendMessage("Success."));
				} else {
					stoner.send(new SendMessage("Stoner not found."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;
	case "unban":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();

				Stoner target = World.getStonerByName(name);

				boolean save = false;
				if (target == null) {
					target = new Stoner();
					target.setUsername(Utility.formatStonerName(name));
					if (!StonerDetails.loadDetails(target)) {
						stoner.send(new SendMessage("The stoner '" + Utility.formatStonerName(name) + "' was not found."));
						return true;
					}
					save = true;
				}

				if (StonerSaveUtil.unbanOfflineStoner(target.getUsername())) {
					if (target != null) {
						target.setBanned(false);
						if (save) {
							StonerSave.save(target);
						}
					}
					stoner.send(new SendMessage("Success."));
				} else {
					stoner.send(new SendMessage("Stoner not found."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid format"));
			}
		}
		return true;
	case "t2":
	case "teleto":
		if (parser.hasNext()) {
			String name = parser.nextString();

			while (parser.hasNext()) {
				name += " " + parser.nextString();
			}

			name = name.trim();

			Stoner target = World.getStonerByName(name);

			if (target == null) {
				stoner.send(new SendMessage("The stoner '" + name + "' could not be found."));
				return true;
			}

			stoner.teleport(target.getLocation());
			stoner.send(new SendMessage("You have teleported to '" + name + "''s position."));
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

			Stoner target = World.getStonerByName(name);

			if (target == null) {
				stoner.send(new SendMessage("The stoner '" + name + "' could not be found."));
				return true;
			}

			target.teleport(stoner.getLocation());
			stoner.send(new SendMessage("You have teleported the stoner '" + name + "' to your position."));

		}
		return true;
	case "kick":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				Stoner target = World.getStonerByName(name);
				if (target == null) {
					stoner.send(new SendMessage("Stoner not found."));
				} else {
					if (StonerConstants.isOwner(target)) {
						DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
						target.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
						return true;
					}
					target.logout(true);
					stoner.send(new SendMessage("Kicked."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
			}
		}
		return true;
	case "logpackets":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();
				Stoner p = World.getStonerByName(name.replaceAll("_", " "));
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				} else {
					p.getClient().setLogStoner(true);
					stoner.send(new SendMessage("Now logging incoming packets for: " + p.getUsername() + "."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
			}
		}
		return true;
	case "unlogpackets":
		if (parser.hasNext()) {
			try {
				String name = parser.nextString();
				Stoner p = World.getStonerByName(name);
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				} else {
					p.getClient().setLogStoner(false);
					stoner.send(new SendMessage("No longer logging incoming packets for: " + p.getUsername() + "."));
				}
			} catch (Exception e) {
				stoner.send(new SendMessage("Invalid password format, syntax: ::changepass password here"));
			}
		}
		return true;
	}
	return false;
	}

	@Override
	public boolean meetsRequirements(Stoner stoner) {
	return stoner.getRights() >= 1 && stoner.getRights() < 5;
	}
}