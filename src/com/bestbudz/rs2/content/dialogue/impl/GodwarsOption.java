package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class GodwarsOption extends Dialogue {

  public GodwarsOption(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
    }
    return false;
  }

  @Override
  public void execute() {
    switch (next) {
      case 0:
        DialogueManager.sendStatement(stoner, "Coming soon!");
        break;
    }
  }
}
