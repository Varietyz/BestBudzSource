package com.bestbudz.rs2.entity.stoner.net.in.command.impl;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.StonersOnline;
import com.bestbudz.rs2.content.Yelling;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.content.dialogue.impl.ChangePasswordDialogue;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.CommandInterface;
import com.bestbudz.rs2.content.interfaces.impl.TrainingInterface;
import com.bestbudz.rs2.content.profession.Profession;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.content.profiles.StonerProfiler;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.command.Command;
import com.bestbudz.rs2.entity.stoner.net.in.command.CommandParser;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class StonerCommand implements Command {

  @Override
  public boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception {
    switch (parser.getCommand()) {
      case "command":
      case "commands":
      case "commandlist":
      case "commandslist":
      case "bevel":
        stoner.send(new SendString("BestBudz Command List", 8144));
        InterfaceHandler.writeText(new CommandInterface(stoner));
        stoner.send(new SendInterface(8134));
        return true;
      case "teleport":
      case "teleports":
      case "teleporting":
      case "teleportings":
      case "tp":
        InterfaceHandler.writeText(new TrainingInterface(stoner));
        stoner.send(new SendInterface(61000));
        stoner.send(new SendString("Selected: @red@None", 61031));
        stoner.send(new SendString("Cost: @red@Free", 61032));
        stoner.send(new SendString("Requirement: @red@None", 61033));
        stoner.send(new SendString("Other: @red@None", 61034));
        return true;
      case "stoners":
        stoner.send(
            new SendMessage(
                "There are currently @red@"
                    + Utility.format(World.getActiveStoners())
                    + "</col> stoners online."));
        StonersOnline.showStoners(
            stoner,
            p -> {
              return true;
            });
        return true;
      case "discord":
        stoner.send(
            new SendMessage("Please press 'BestBudz' in the top left corner and select discord!"));
        return true;
      case "find":
        if (parser.hasNext()) {
          String name = parser.nextString();

          while (parser.hasNext()) {
            name += " " + parser.nextString();
          }

          name = name.trim();

          StonerProfiler.search(stoner, name);
        }
        return true;
      case "smokeweed":
        stoner.getUpdateFlags().sendGraphic(new Graphic(354, true));
        stoner.getUpdateFlags().sendAnimation(new Animation(884));
        return true;
      case "withdrawmp":
        if (parser.hasNext()) {
          try {
            int amount = 1;

            if (parser.hasNext()) {
              long temp =
                  Long.parseLong(
                      parser
                          .nextString()
                          .toLowerCase()
                          .replaceAll("k", "000")
                          .replaceAll("m", "000000")
                          .replaceAll("b", "000000000"));

              if (temp > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE;
              } else {
                amount = (int) temp;
              }
            }

            stoner.getPouch().withdrawPouch(amount);

          } catch (Exception e) {
            stoner.send(new SendMessage("Something went wrong!"));
            e.printStackTrace();
          }
        }
        return true;
      case "changepassword":
      case "changepass":
        if (parser.hasNext()) {
          try {
            String password = parser.nextString();
            if ((password.length() > 4) && (password.length() < 15))
              stoner.start(new ChangePasswordDialogue(stoner, password));
            else
              DialogueManager.sendStatement(
                  stoner, "Your password must be between 4 and 15 characters.");
          } catch (Exception e) {
            stoner
                .getClient()
                .queueOutgoingPacket(
                    new SendMessage("Invalid password format, syntax: ::changepass password here"));
          }
        }
        return true;
      case "yelltitle":
        if (parser.hasNext()) {
          try {
            String message = parser.nextString();
            while (parser.hasNext()) {
              message += " " + parser.nextString();
            }

            for (int i = 0; i < BestbudzConstants.BAD_STRINGS.length; i++) {
              if (message.contains(BestbudzConstants.BAD_STRINGS[i])) {
                stoner.send(new SendMessage("Choose something else."));
                return true;
              }
            }

            for (int i = 0; i < BestbudzConstants.BAD_TITLES.length; i++) {
              if (message.contains(BestbudzConstants.BAD_TITLES[i])) {
                stoner.send(new SendMessage("Choose something else."));
                return true;
              }
            }

            stoner.setYellTitle(message);
            DialogueManager.sendTimedStatement(stoner, "Your yell title is now @gre@" + message);
          } catch (Exception e) {
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -title"));
          }
        }
        return true;
      case "yell":
      case "y":
      case "roep":
        if (parser.hasNext()) {
          try {
            String message = parser.nextString();
            while (parser.hasNext()) {
              message += " " + parser.nextString();
            }
            Yelling.yell(stoner, message.trim());
          } catch (Exception e) {
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -messsage"));
          }
        }
        return true;
      case "empty":
        if (stoner.getRights() == 2 || stoner.getRights() == 3) {
          stoner.getBox().clear();
          stoner.send(new SendMessage("You have emptied your box."));
          stoner.send(new SendRemoveInterfaces());
          return true;
        }

        stoner.start(
            new OptionDialogue(
                "Yes, empty my box.",
                p -> {
                  p.getBox().clear();
                  p.send(new SendMessage("You have emptied your box."));
                  p.send(new SendRemoveInterfaces());
                },
                "Wait, nevermind!",
                p -> p.send(new SendRemoveInterfaces())));
        return true;
      case "home":
        if (stoner.inWilderness()) {
          stoner.send(
              new SendMessage(
                  "You normally cannot teleport above 20 wilderness, but fuck that, beep boop going home!"));
          stoner.getMage().teleport(3434, 2890, 0, TeleportTypes.SPELL_BOOK);
          return true;
        }
        stoner.getMage().teleport(3434, 2890, 0, TeleportTypes.SPELL_BOOK);
        return true;

      case "devilspact":
        for (int i = 0; i < 25; i++) {
          stoner.getGrades()[i] = 419;
          stoner.getMaxGrades()[i] = 419;
          stoner.getProfession().getExperience()[i] = Profession.EXP_FOR_GRADE[418];
        }
        stoner.getProfession().update();
        World.sendGlobalMessage(
            "<col=F01C1C>"
                + Utility.formatStonerName(stoner.getUsername())
                + " has made a pact with the devil!");
        stoner.getBank().clear();
        stoner.getBox().clear();
        stoner.getPouch().clear();
        stoner.setCredits(0);
        stoner.setBountyPoints(0);
        stoner.setChillPoints(0);
        stoner.setPestPoints(0);
        stoner.setMercenaryPoints(0);
        stoner.send(
            new SendMessage("You have lost your rank, items, cash, cannacredits and points!"));
        stoner.setRights(0);
        String title = "DEVILSBITCH";
        title = title.trim();
        stoner.setStonerTitle(StonerTitle.create(title, 0xFF0000, false));
		stoner.setDead(true);
        stoner.setAppearanceUpdateRequired(true);
        return true;
    }
    return false;
  }

  @Override
  public boolean meetsRequirements(Stoner stoner) {
    return true;
  }
}
