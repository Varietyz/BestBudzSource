package com.bestbudz.rs2.content.dialogue;

import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;

public class OneLineDialogue {

  private static final HashMap<Integer, String> idsForChat = new HashMap<Integer, String>();

  public static void declare() {
    idsForChat.put(462, "Welcome to the Mages' guild!");
    idsForChat.put(553, "Hello there, I've got all kinds of mageal supply!");
  }

  public static boolean doOneLineChat(Stoner stoner, int id) {
    if (idsForChat.containsKey(id)) {
      DialogueManager.sendNpcChat(stoner, id, Emotion.HAPPY, idsForChat.get(id));
      return true;
    }
    return false;
  }
}
