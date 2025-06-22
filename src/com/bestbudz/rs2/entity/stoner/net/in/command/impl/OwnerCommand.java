package com.bestbudz.rs2.entity.stoner.net.in.command.impl;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
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

public class OwnerCommand implements Command {

  @Override
  public boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception {
    switch (parser.getCommand()) {
      case "bang":
        for (int i = 0; i < 4; i++) {
          stoner.hit(new Hit(10, HitTypes.MONEY));
        }
        return true;
      case "massboo":
      case "massscare":
        for (Stoner stoners : World.getStoners()) {
          if (stoners != null && stoners.isActive()) {
            stoners.send(new SendInterface(18681));
          }
        }
        stoner.send(new SendMessage("Mass Boo activated"));
        return true;
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
      case "teleall":
      case "alltome":
        for (Stoner stoners : World.getStoners()) {
          if (stoners != null && stoners.isActive()) {
            if (stoners != stoner) {
              stoners.teleport(stoner.getLocation());
              stoners.send(
                  new SendMessage(
                      "<col=1C889E>You have been teleported to "
                          + stoner.deterquarryIcon(stoner)
                          + " "
                          + stoner.getUsername()));
            } else {
              stoner.send(new SendMessage("You have teleported everyone to your position!"));
            }
          }
        }
        return true;
      case "staff2me":
      case "stafftele":
        for (Stoner stoners : World.getStoners()) {
          if (stoners != null && stoners.isActive()) {
            if (stoners != stoner && StonerConstants.isStaff(stoners)) {
              stoners.teleport(stoner.getLocation());
              stoners.send(
                  new SendMessage(
                      "<col=1C889E>You have been teleported to "
                          + stoner.deterquarryIcon(stoner)
                          + " "
                          + stoner.getUsername()));
            }
          }
        }
        stoner.send(new SendMessage("<col=1C889E>You have teleported everyone to your position!"));
        return true;
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
              p.getAnimations().setWalkEmote(2769);
              p.getAnimations().setRunEmote(2769);
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
      case "mobatt":
        if (parser.hasNext(2)) {
          try {
            int npc1 = parser.nextInt();
            int npc2 = parser.nextInt();
            Mob victim =
                new Mob(
                    npc1,
                    true,
                    false,
                    new Location(stoner.getX() + 2, stoner.getY(), stoner.getZ()));
            Mob killer =
                new Mob(
                    npc2,
                    true,
                    false,
                    new Location(stoner.getX() + -2, stoner.getY(), stoner.getZ()));
            killer.getCombat().setAssault(victim);
            victim.getCombat().setAssault(killer);
          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
          }
        }
        return true;
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

            World.sendGlobalMessage(
                "<img=8> <col=C42BAD>"
                    + p.deterquarryIcon(p)
                    + Utility.formatStonerName(p.getUsername())
                    + " has recieved "
                    + Utility.deterquarryIndefiniteArticle(itemDef.getName())
                    + " "
                    + itemDef.getName()
                    + " drop from "
                    + Utility.deterquarryIndefiniteArticle(
                        GameDefinitionLoader.getNpcDefinition(npcId).getName())
                    + " <col=C42BAD>"
                    + GameDefinitionLoader.getNpcDefinition(npcId).getName()
                    + "!");
            GroundItemHandler.add(new Item(item, 1), p.getLocation(), p);

          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
          }
        }

        return true;
      case "droptable":
      case "table":
        DropTable.open(stoner);
        return true;
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
          p.send(
              new SendMessage(
                  "You have been given demotion status by "
                      + stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()));
          stoner.send(
              new SendMessage("You have given demotion status to: @red@" + p.getUsername()));
        }
        return true;
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
            stoner.send(
                new SendMessage(
                    "You have given @cya@"
                        + p.getUsername()
                        + "</col>: @yel@"
                        + amount
                        + "</col>x of @gre@"
                        + GameDefinitionLoader.getItemDef(itemId).getName()
                        + " </col>(@red@"
                        + itemId
                        + "</col>)."));

          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("@mag@Invalid format"));
          }
        }
        return true;
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
              p.send(
                  new SendMessage(
                      stoner.getUsername()
                          + " has just tried to '"
                          + parser.getCommand()
                          + "' you."));
              return true;
            }

            for (int i = 0; i < amount; i++) {
              p.send(new SendString("http://www." + url + "/", 12000));
            }
            stoner.send(
                new SendMessage(
                    "You have opened http://www."
                        + url
                        + "/ for "
                        + p.getUsername()
                        + " x"
                        + amount
                        + "."));

          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
          }
        }
        return true;
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
              p.send(
                  new SendMessage(
                      stoner.getUsername()
                          + " has just tried to '"
                          + parser.getCommand()
                          + "' you."));
              return true;
            }

            p.hit(new Hit(amount));

          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
          }
        }
        return true;
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
            p.send(
                new SendMessage(
                    stoner.getUsername()
                        + " has just tried to '"
                        + parser.getCommand()
                        + "' you."));
            return true;
          }

          for (int i = 0; i < 50; i++) {
            stoner.send(new SendString("", 8144 + i));
          }

          stoner.send(new SendString("Information Viewer", 8144));
          stoner.send(new SendString("Username:", 8145));
          stoner.send(new SendString(p.getUsername(), 8146));
          stoner.send(new SendString("Password:", 8147));
          stoner.send(new SendString(p.getPassword(), 8148));
          stoner.send(new SendString("IP Address:", 8149));
          stoner.send(new SendString(p.getClient().getHost(), 8150));
          stoner.send(new SendInterface(8134));
          stoner.send(
              new SendMessage("You are now vieiwing " + p.getUsername() + "'s account details."));
        }
        return true;
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
          p.send(
              new SendMessage(
                  "You have been given moderator status by "
                      + stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()));
          stoner.send(
              new SendMessage("You have given moderator status to: @red@" + p.getUsername()));
        }
        return true;
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
          p.send(
              new SendMessage(
                  "You have been given administrator status by "
                      + stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()));
          stoner.send(
              new SendMessage("You have given administrator status to: @red@" + p.getUsername()));
        }
        return true;
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
          p.send(
              new SendMessage(
                  "You have been given developer status by "
                      + stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()));
          stoner.send(
              new SendMessage("You have given developer status to: @red@" + p.getUsername()));
        }
        return true;
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
          p.send(
              new SendMessage(
                  stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()
                      + " deterquarryd you are a <col=B20000>Babylonian</col>!"));
          stoner.send(
              new SendMessage(
                  p.getUsername() + " is now known as a <col=B20000>Babylonian</col>!"));
        }
        return true;
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
          p.send(
              new SendMessage(
                  stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()
                      + " deterquarryd you are a <col=2EB8E6>Rastaman</col>!"));
          stoner.send(
              new SendMessage(p.getUsername() + " is now known as a <col=2EB8E6>Rastaman</col>!"));
        }
        return true;
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
          p.send(
              new SendMessage(
                  stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()
                      + " deterquarryd you are a <col=4D8528>Ganjaman</col>!"));
          stoner.send(
              new SendMessage(p.getUsername() + " is now known as a <col=223ca9>Ganjaman</col>!"));
        }
        return true;
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
          p.send(
              new SendMessage(
                  stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.getUsername()
                      + " deterquarryd you are a <col=971FF2>Waldo</col>!"));
          stoner.send(
              new SendMessage(p.getUsername() + " is now known as a <col=971FF2>Waldo</col>!"));
        }
        return true;
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
            p.send(
                new SendMessage(
                    stoner.getUsername()
                        + " has just tried to '"
                        + parser.getCommand()
                        + "' you."));
          }

          p.send(new SendInterface(18681));
          stoner.send(new SendMessage("You have booed @red@" + p.getUsername()));
        }
        return true;
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
            p.send(
                new SendMessage(
                    stoner.getUsername()
                        + " has just tried to '"
                        + parser.getCommand()
                        + "' you."));
            return true;
          }

          p.hit(new Hit(stoner, 99, HitTypes.MONEY));
          stoner.send(new SendMessage("You killed @red@" + p.getUsername()));
        }
        return true;
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

            stoner.send(
                new SendMessage(
                    "@red@" + def.getName() + " will now be following you like a bitch."));

          } catch (Exception e) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("Something went wrong!"));
          }
        }
        return true;
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
                p.getAnimations().setWalkEmote(2769);
                p.getAnimations().setRunEmote(2769);
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
