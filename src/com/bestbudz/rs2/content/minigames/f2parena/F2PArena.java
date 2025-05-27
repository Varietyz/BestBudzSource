package com.bestbudz.rs2.content.minigames.f2parena;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayDeque;
import java.util.Queue;

public class F2PArena {
  public static Queue<Stoner> gameStoners = new ArrayDeque<Stoner>();

  public static void enterGame(Stoner stoner) {
    if (!stoner.getActivePets().isEmpty()) {
      DialogueManager.sendStatement(stoner, "You can't bring a pet into this game!");
      return;
    }
    if (gameStoners.contains(stoner)) {
      return;
    }
    DialogueManager.sendInformationBox(
        stoner,
        "F2P Arena",
        "Welcome to the @blu@F2P Arena@bla@!",
        "There are currently @blu@" + gameStoners.size() + " @bla@members playing.",
        "Objective: @blu@Kill as many stoners as possible@bla@.",
        "To leave click on the @blu@portal@bla@.");
    stoner.setController(ControllerManager.F2P_ARENA_CONTROLLER);
    stoner.teleport(Utility.randomElement(F2PArenaConstants.RESPAWN_LOCATIONS));
    gameStoners.add(stoner);
  }

  public static void leaveGame(Stoner stoner) {
    if (!gameStoners.contains(stoner)) {
      return;
    }
    gameStoners.remove(stoner);
  }

  public static void messageStoners(String message) {
    for (Stoner stoners : gameStoners) {
      stoners.send(new SendMessage(message));
    }
  }
}
