package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ModCommandsInterface extends InterfaceHandler {

  private final String[] text = {
    "::jailarea - teleports to jail area",
    "::jail - jail a stoner",
    "::unjail - unjails a stoner",
    "::ban - bans a stoner",
    "::unban - unbans a stoner",
    "::mute - mutes a stoner",
    "::unmute - unmutes stoner",
    "::checkbank checks stoner bank",
    "::teleto - teleports to stoner",
    "::teletome - telepors stoner to me",
    "::staffzone - teleports to staffzone",
    "::ecosearch - searches for amount of itemId in eco",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
  };

  public ModCommandsInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 8145;
  }
}
