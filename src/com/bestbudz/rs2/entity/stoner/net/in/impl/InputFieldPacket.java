package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.moderation.StaffTab;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class InputFieldPacket extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    int id = in.readShort();
    String text = in.readString();

    if (id < 0 || text == null || text.length() <= 0) {
      return;
    }

    if (StonerConstants.isOwner(stoner) && BestbudzConstants.DEV_MODE) {
      stoner.send(new SendMessage("ID: " + id + " | Text: " + text));
    }

    if (StaffTab.inputField(stoner, id, text)) {
      return;
    }

    switch (id) {
      case 59814:
        DropTable.searchItem(stoner, text);
        break;
      case 59815:
        DropTable.searchNpc(stoner, text);
        break;
    }
  }
}
