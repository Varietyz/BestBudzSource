package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class StringInputPacket extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    String input = Utility.longToStonerName2(in.readLong());
    input = input.replaceAll("_", " ");

    if (stoner.getInterfaceManager().getMain() == 41750) {
      stoner.reportName = Utility.capitalize(input);
      return;
    }

    if (stoner.getInterfaceManager().getMain() == 59800) {
      DropTable.searchItem(stoner, input);
      return;
    }

    if (stoner.getEnterXInterfaceId() == 56002) {
      for (int i = 0; i < BestbudzConstants.BAD_STRINGS.length; i++) {
        if (input.equalsIgnoreCase(BestbudzConstants.BAD_STRINGS[i])) {
          DialogueManager.sendStatement(stoner, "Grow up! That title can not be used.");
          return;
        }
      }
      if (input.length() >= 15) {
        DialogueManager.sendStatement(stoner, "Titles can not exceed 15 characters!");
        return;
      }
      stoner.setStonerTitle(StonerTitle.create(input, stoner.getStonerTitle().getColor(), false));
      stoner.setAppearanceUpdateRequired(true);
      stoner.send(new SendRemoveInterfaces());
      return;
    }

    if (stoner.getEnterXInterfaceId() == 55776) {
      return;
    }

    if (stoner.getEnterXInterfaceId() == 55777) {
      return;
    }

    if (stoner.getEnterXInterfaceId() == 55778) {
      return;
    }
  }
}
