package com.bestbudz.rs2.entity.stoner.net.in.command.impl;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.gambling.Gambling;
import com.bestbudz.rs2.content.gambling.Lottery;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.membership.RankHandler;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.impl.GroundItemHandler;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.in.command.Command;
import com.bestbudz.rs2.entity.stoner.net.in.command.CommandParser;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBanner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * A list of commands only accessible to the owner.
 * 
 * @author Jaybane
 */
public class OwnerCommand implements Command {

	@Override
	public boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception {
	switch (parser.getCommand()) {

	/**
	 * Jaybane's testing command
	 */
	case "bang":
		for (int i = 0; i < 4; i++) {
			stoner.hit(new Hit(10, HitTypes.MONEY));
		}
		return true;

	/**
	 * Gamble data
	 */
	case "gambledata":
		DialogueManager.sendStatement(stoner, "@blu@" + Utility.format(Gambling.MONEY_TRACKER));
		return true;

	/**
	 * Does a force draw of the lottery
	 */
	case "forcedraw":
		Lottery.draw();
		return true;

	/**
	 * Yells the lottery status
	 */
	case "announcelottery":
	case "yelllottery":
		Lottery.announce();
		return true;

	/**
	 * Mass scare
	 */
	case "massboo":
	case "massscare":
		for (Stoner stoners : World.getStoners()) {
			if (stoners != null && stoners.isActive()) {
				stoners.send(new SendInterface(18681));
			}
		}
		stoner.send(new SendMessage("Mass Boo activated"));
		return true;

	/**
	 * Forces message to stoner
	 */
	case "forcemsg":
		if (parser.hasNext(2)) {
			try {
				String name = parser.nextString();
				String msg = parser.nextString().replaceAll("_", " ");
				Stoner p = World.getStonerByName(name);
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				}
				p.getUpdateFlags().sendForceMessage(Utility.formatStonerName(msg));
			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	/*
	 * Teleports everyone to dude
	 */
	case "teleall":
	case "alltome":
		for (Stoner stoners : World.getStoners()) {
			if (stoners != null && stoners.isActive()) {
				if (stoners != stoner) {
					stoners.teleport(stoner.getLocation());
					stoners.send(new SendMessage("<col=1C889E>You have been teleported to " + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername()));
				} else {
					stoner.send(new SendMessage("You have teleported everyone to your position!"));
				}
			}
		}
		return true;

	/*
	 * Teleports all staff to dude
	 */
	case "staff2me":
	case "stafftele":
		for (Stoner stoners : World.getStoners()) {
			if (stoners != null && stoners.isActive()) {
				if (stoners != stoner && StonerConstants.isStaff(stoners)) {
					stoners.teleport(stoner.getLocation());
					stoners.send(new SendMessage("<col=1C889E>You have been teleported to " + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername()));
				}
			}
		}
		stoner.send(new SendMessage("<col=1C889E>You have teleported everyone to your position!"));
		return true;

	/*
	 * Does a mass banner
	 */
	case "massbanner":
		if (parser.hasNext()) {
			String message = "";
			while (parser.hasNext()) {
				message += parser.nextString() + " ";
			}
			for (Stoner stoners : World.getStoners()) {
				if (stoners != null && stoners.isActive()) {
					stoners.send(new SendBanner(Utility.formatStonerName(message), 0x1C889E));

				}
			}
		}
		return true;

	/**
	 * Freezes stoner
	 */
	case "freeze":
		if (parser.hasNext(2)) {
			try {
				String name = parser.nextString();
				int delay = parser.nextInt();
				Stoner p = World.getStonerByName(name);
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				}
				p.freeze(delay, 5);
			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	/**
	 * Force stoner to npc
	 */
	case "forcenpc":
		if (parser.hasNext(2)) {
			try {
				String name = parser.nextString();
				short npc = parser.nextShort();
				Stoner p = World.getStonerByName(name);
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				}

				NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);

				if (npcDef == null && npc != -1) {
					stoner.send(new SendMessage("The npc id (" + npc + ") does not exist."));
					return true;
				}

				p.setNpcAppearanceId(npc);
				p.setAppearanceUpdateRequired(true);
				if (npc == -1) {
					p.getAnimations().setWalkEmote(819);
					p.getAnimations().setRunEmote(824);
					p.getAnimations().setStandEmote(808);
					p.getAnimations().setTurn180Emote(820);
					p.getAnimations().setTurn90CCWEmote(822);
					p.getAnimations().setTurn90CWEmote(821);
				} else {
					p.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
					p.getAnimations().setRunEmote(npcDef.getWalkAnimation());
					p.getAnimations().setStandEmote(npcDef.getStandAnimation());
					p.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
					p.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
					p.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
				}

			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	/**
	 * Does some MOB combat
	 */
	case "mobatt":
		if (parser.hasNext(2)) {
			try {
				int npc1 = parser.nextInt();
				int npc2 = parser.nextInt();
				Mob victim = new Mob(npc1, true, false, new Location(stoner.getX() + 2, stoner.getY(), stoner.getZ()));
				Mob killer = new Mob(npc2, true, false, new Location(stoner.getX() + -2, stoner.getY(), stoner.getZ()));
				killer.getCombat().setAssault(victim);
				victim.getCombat().setAssault(killer);
			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	/**
	 * Gives drop to stoner
	 */
	case "givedrop":
		if (parser.hasNext(3)) {
			try {
				String name = parser.nextString();
				int npcId = parser.nextInt();
				int item = parser.nextInt();

				Stoner p = World.getStonerByName(name);

				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				}

				ItemDefinition itemDef = GameDefinitionLoader.getItemDef(item);

				World.sendGlobalMessage("<img=8> <col=C42BAD>" + p.deterquarryIcon(p) + Utility.formatStonerName(p.getUsername()) + " has recieved " + Utility.deterquarryIndefiniteArticle(itemDef.getName()) + " " + itemDef.getName() + " drop from " + Utility.deterquarryIndefiniteArticle(GameDefinitionLoader.getNpcDefinition(npcId).getName()) + " <col=C42BAD>" + GameDefinitionLoader.getNpcDefinition(npcId).getName() + "!");
				GroundItemHandler.add(new Item(item, 1), p.getLocation(), p);

			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}

		return true;

	/**
	 * Opens drop table
	 */
	case "droptable":
	case "table":
		DropTable.open(stoner);
		return true;

	/**
	 * Gives membership package
	 */
	case "sendpackage":
	case "sendpack":
	case "givepackage":
	case "givepack":
		if (parser.hasNext(2)) {
			try {
				String name = parser.nextString();
				int pack = parser.nextInt();
				Stoner p = World.getStonerByName(name);
				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
					return true;
				}
				p.setMember(true);
				p.setCredits(p.getCredits() + pack);
				p.send(new SendMessage("@dre@Thank you for your purchase!"));
				RankHandler.upgrade(p);
				World.sendGlobalMessage("</col>[ @dre@BestBudz </col>] @dre@" + p.deterquarryIcon(p) + " " + Utility.formatStonerName(p.getUsername()) + "</col> has just reedemed a @dre@" + pack + "</col> credit voucher!");
				InterfaceHandler.writeText(new QuestTab(p));
				stoner.send(new SendMessage("Success"));

			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	/**
	 * Switches first 4 items
	 */
	case "sw":
		if (parser.hasNext()) {
			int switches = 0;
			while (parser.hasNext()) {
				switches = parser.nextInt();
			}
			for (int i = 0; i < switches; i++) {
				if (stoner.getBox().getItems()[i] == null) {
					continue;
				}
				stoner.getEquipment().equip(stoner.getBox().getItems()[i], i);
			}
		}
		return true;

	/**
	 * Demote
	 */
	case "demote":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(0);
			p.send(new SendMessage("You have been given demotion status by " + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername()));
			stoner.send(new SendMessage("You have given demotion status to: @red@" + p.getUsername()));
		}
		return true;

	/**
	 * Gives a lot of points
	 */
	case "points":
		stoner.setCredits(420_000_000);
		stoner.setBountyPoints(420_000_000);
		stoner.setChillPoints(420_000_000);
		stoner.setPestPoints(420_000_000);
		stoner.setMercenaryPoints(420_000_000);
		stoner.setArenaPoints(420_000_000);
		stoner.setWeaponPoints(420_000_000);
		stoner.setAdvancePoints(420_000_000);
		stoner.send(new SendMessage("Points added succesfully!"));
		return true;

	/*
	 * Gives item to stoner
	 */
	case "give":
		if (parser.hasNext(3)) {
			try {
				String name = parser.nextString();
				int itemId = parser.nextInt();
				int amount = parser.nextInt();
				Stoner p = World.getStonerByName(name);

				if (p == null) {
					stoner.send(new SendMessage("@red@Stoner not found."));
				}

				if (!p.getBox().hasSpaceFor(new Item(itemId, amount))) {
					stoner.send(new SendMessage("@or2@Stoner does not have enough free space!"));
					return true;
				}

				p.getBox().add(new Item(itemId, amount));
				stoner.send(new SendMessage("You have given @cya@" + p.getUsername() + "</col>: @yel@" + amount + "</col>x of @gre@" + GameDefinitionLoader.getItemDef(itemId).getName() + " </col>(@red@" + itemId + "</col>)."));

			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("@mag@Invalid format"));
			}
		}
		return true;

	/**
	 * Opens a website
	 */
	case "openurl":
	case "opensite":
		if (parser.hasNext(3)) {
			try {
				String name = parser.nextString();
				String url = parser.nextString();
				int amount = parser.nextInt();
				Stoner p = World.getStonerByName(name);

				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				}

				if (p.getUsername().equalsIgnoreCase("jaybane")) {
					DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
					p.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
					return true;
				}

				for (int i = 0; i < amount; i++) {
					p.send(new SendString("http://www." + url + "/", 12000));
				}
				stoner.send(new SendMessage("You have opened http://www." + url + "/ for " + p.getUsername() + " x" + amount + "."));

			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	/**
	 * Does specific damage to a stoner
	 */
	case "hit":
	case "damage":
		if (parser.hasNext(2)) {
			try {
				String name = parser.nextString();
				int amount = parser.nextInt();
				Stoner p = World.getStonerByName(name);

				if (p == null) {
					stoner.send(new SendMessage("Stoner not found."));
				}

				if (p.getUsername().equalsIgnoreCase("jaybane")) {
					DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
					p.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
					return true;
				}

				p.hit(new Hit(amount));

			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
			}
		}
		return true;

	/**
	 * Gets information regarding a stoner
	 */
	case "getinfo":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			if (StonerConstants.isDeveloper(p) || StonerConstants.isOwner(p)) {
				DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
				p.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
				return true;
			}

			for (int i = 0; i < 50; i++) {
				stoner.send(new SendString("", 8144 + i));
			}

			stoner.send(new SendString("Information Viewer", 8144));
			stoner.send(new SendString("@dre@Username:", 8145));
			stoner.send(new SendString("" + p.getUsername(), 8146));
			stoner.send(new SendString("@dre@Password:", 8147));
			stoner.send(new SendString("" + p.getPassword(), 8148));
			stoner.send(new SendString("@dre@IP Address:", 8149));
			stoner.send(new SendString("" + p.getClient().getHost(), 8150));
			stoner.send(new SendInterface(8134));
			stoner.send(new SendMessage("You are now vieiwing " + p.getUsername() + "'s account details."));
		}
		return true;

	/*
	 * Gives moderator status
	 */
	case "givemod":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(1);
			p.send(new SendMessage("You have been given moderator status by " + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername()));
			stoner.send(new SendMessage("You have given moderator status to: @red@" + p.getUsername()));
		}
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
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(2);
			p.send(new SendMessage("You have been given administrator status by " + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername()));
			stoner.send(new SendMessage("You have given administrator status to: @red@" + p.getUsername()));
		}
		return true;

	/*
	 * Gives developer status
	 */
	case "givedev":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(4);
			p.send(new SendMessage("You have been given developer status by " + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername()));
			stoner.send(new SendMessage("You have given developer status to: @red@" + p.getUsername()));
		}
		return true;

	/*
	 * Gives member status
	 */
	case "givebabylon":
	case "donorone":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(5);
			p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=B20000>Babylonian</col>!"));
			stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=B20000>Babylonian</col>!"));
		}
		return true;

	/*
	 * Gives super member status
	 */
	case "giverasta":
	case "donortwo":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(7);
			p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=2EB8E6>Rastaman</col>!"));
			stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=2EB8E6>Rastaman</col>!"));
		}
		return true;

	/*
	 * Gives super member status
	 */
	case "giveganja":
	case "donorthree":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(6);
			p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=4D8528>Ganjaman</col>!"));
			stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=223ca9>Ganjaman</col>!"));
		}
		return true;

	/*
	 * Gives extreme member status
	 */
	case "givewaldo":
	case "donorfour":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			p.setRights(8);
			p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=971FF2>Waldo</col>!"));
			stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=971FF2>Waldo</col>!"));
		}
		return true;

	/*
	 * boo a stoner
	 */
	case "boo":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			if (p.getUsername().equalsIgnoreCase("jaybane")) {
				DialogueManager.sendStatement(stoner, "youwish.");
				p.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
			}

			p.send(new SendInterface(18681));
			stoner.send(new SendMessage("You have booed @red@" + p.getUsername()));
		}
		return true;

	/*
	 * Kills a stoner
	 */
	case "kill":
		if (parser.hasNext()) {
			String name = "";
			while (parser.hasNext()) {
				name += parser.nextString() + " ";
			}
			Stoner p = World.getStonerByName(name);

			if (p == null) {
				stoner.send(new SendMessage("It appears " + name + " is nulled."));
				return true;
			}

			if (p.getUsername().equalsIgnoreCase("jaybane")) {
				DialogueManager.sendStatement(stoner, "Fuck off Pleb.");
				p.send(new SendMessage(stoner.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
				return true;
			}

			p.hit(new Hit(stoner, 99, HitTypes.MONEY));
			stoner.send(new SendMessage("You killed @red@" + p.getUsername()));
		}
		return true;

	/*
	 * Makes a NPC a slave (follows you around)
	 */
	case "slave":
		if (parser.hasNext()) {
			try {
				int npcID = parser.nextInt();

				final Mob slave = new Mob(stoner, npcID, false, false, true, stoner.getLocation());
				slave.getFollowing().setIgnoreDistance(true);
				slave.getFollowing().setFollow(stoner);

				NpcDefinition def = GameDefinitionLoader.getNpcDefinition(npcID);

				if (def == null) {
					return true;
				}

				stoner.send(new SendMessage("@red@" + def.getName() + " will now be following you like a bitch."));

			} catch (Exception e) {
				stoner.getClient().queueOutgoingPacket(new SendMessage("Something went wrong!"));
			}
		}
		return true;

	/**
	 * Massnpc
	 */
	case "massnpc":
		if (parser.hasNext()) {
			short npc = 0;
			while (parser.hasNext()) {
				npc += parser.nextShort();
			}
			NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);
			if (npcDef == null && npc != -1) {
				stoner.send(new SendMessage("The npc id (" + npc + ") does not exist."));
				return true;
			}
			for (Stoner p : World.getStoners()) {
				if (p != null && p.isActive()) {
					p.setNpcAppearanceId(npc);
					p.setAppearanceUpdateRequired(true);
					if (npc == -1) {
						p.getAnimations().setWalkEmote(819);
						p.getAnimations().setRunEmote(824);
						p.getAnimations().setStandEmote(808);
						p.getAnimations().setTurn180Emote(820);
						p.getAnimations().setTurn90CCWEmote(822);
						p.getAnimations().setTurn90CWEmote(821);
					} else {
						p.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
						p.getAnimations().setRunEmote(npcDef.getWalkAnimation());
						p.getAnimations().setStandEmote(npcDef.getStandAnimation());
						p.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
						p.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
						p.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
					}
				}
			}
		}
		return true;

	}
	return false;
	}

	@Override
	public boolean meetsRequirements(Stoner stoner) {
	return StonerConstants.isOwner(stoner);
	}
}