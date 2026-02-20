package com.bestbudz.rs2.content.dialogue;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;

public class OneLineDialogue {

  private static final HashMap<Integer, String> idsForChat = new HashMap<Integer, String>();

  public static void declare() {
    idsForChat.put(462, "Welcome to the Mages' guild!");
    idsForChat.put(553, "Hello there, I've got all kinds of mageal supply!");
  }

  public static boolean doOneLineChat(Stoner stoner, int id) {
    if (idsForChat.containsKey(id)) {
		stoner.send(new SendMessage(idsForChat.get(id)));
      return true;
    }
    return false;
  }
}
