package com.bestbudz.rs2.entity.stoner.net.in.command;

import com.bestbudz.rs2.entity.stoner.Stoner;

public interface Command {

  boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception;

  boolean meetsRequirements(Stoner stoner);
}
