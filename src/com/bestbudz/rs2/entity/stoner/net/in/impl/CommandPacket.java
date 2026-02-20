package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.in.command.Command;
import com.bestbudz.rs2.entity.stoner.net.in.command.CommandParser;
import com.bestbudz.rs2.entity.stoner.net.in.command.impl.AdministratorCommand;
import com.bestbudz.rs2.entity.stoner.net.in.command.impl.DeveloperCommand;
import com.bestbudz.rs2.entity.stoner.net.in.command.impl.ModeratorCommand;
import com.bestbudz.rs2.entity.stoner.net.in.command.impl.OwnerCommand;
import com.bestbudz.rs2.entity.stoner.net.in.command.impl.StonerCommand;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class CommandPacket extends IncomingPacket {

  private static final Command[] COMMANDS =
      new Command[] {
        new StonerCommand(),
        new ModeratorCommand(),
        new AdministratorCommand(),
        new DeveloperCommand(),
        new OwnerCommand()
      };

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    CommandParser parser = CommandParser.create(in.readString().toLowerCase().trim());

    if (parser.getCommand().startsWith("/")) {
        return;
    }

    try {
      for (Command command : COMMANDS) {
        if (StonerConstants.isOwner(stoner) || command.meetsRequirements(stoner)) {
          if (command.handleCommand(stoner, parser)) {
            return;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      stoner.getClient().queueOutgoingPacket(new SendMessage("Invalid command format."));
    }
  }
}
