package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.StarterKit;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.DefaultController;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;

public class Tutorial extends Dialogue {

  public static final TutorialController TUTORIAL_CONTROLLER = new TutorialController();
  public static final int GUIDE = 1558;
  public static final int[] SIDEBAR_INTERFACE_IDS = {
    -1, -1, -1, 3213, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
  };

  public Tutorial(Stoner stoner) {
    this.stoner = stoner;
    stoner.setController(TUTORIAL_CONTROLLER);
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case 9157:
        if (option == 1) {
          next = 3;
          execute();
        }
        return true;
      case 9158:
        if (option == 1) {
          next = 2;
          execute();
        }
        return true;
    }
    return false;
  }

  @Override
  public void execute() {

    for (int i = 0; i < SIDEBAR_INTERFACE_IDS.length; i++) {
      stoner.send(new SendSidebarInterface(i, SIDEBAR_INTERFACE_IDS[i]));
    }

    switch (next) {
      case 0:
        DialogueManager.sendNpcChat(
            stoner,
            GUIDE,
            Emotion.HAPPY_TALK,
            "High @blu@" + stoner.getUsername() + "</col>, you arrived in Best Budz!",
            "Shall i show you da way?");
        next++;
        break;
      case 1:
        DialogueManager.sendOption(stoner, "Ya man.", "No man.");
        option = 1;
        break;
      case 2:
        end();
        StarterKit.handle(stoner, 202051);
        stoner.send(new SendInterface(51750));
        break;
      case 3:
        nChat(
            new String[] {
              "join us on discord and check out tutorials!",
              "Developer/artworker = JayBane",
              "Community Manager/Admin = Rlmach, Admin = Drenth"
            });
        break;
      case 4:
        end();
        StarterKit.handle(stoner, 202051);
        stoner.send(new SendInterface(51750));
        break;
    }
  }

  public void nChat(String[] chat) {
    DialogueManager.sendNpcChat(stoner, GUIDE, Emotion.HAPPY_TALK, chat);
    next += 1;
  }

  public void pChat(String[] chat) {
    DialogueManager.sendStonerChat(stoner, Emotion.HAPPY, chat);
    next += 1;
  }

  public void tele(int x, int y) {
    stoner.teleport(new Location(x, y, 0));
  }

  public void tele(int x, int y, int z) {
    stoner.teleport(new Location(x, y, z));
  }

  public static class TutorialController extends DefaultController {

    @Override
    public boolean canAssaultNPC() {
      return false;
    }

    @Override
    public boolean canClick() {
      return false;
    }

    @Override
    public boolean canMove(Stoner p) {
      return false;
    }

    @Override
    public boolean canTeleport() {
      return false;
    }

    @Override
    public boolean canTrade() {
      return false;
    }

    @Override
    public void onDisconnect(Stoner p) {}

    @Override
    public boolean transitionOnWalk(Stoner p) {
      return false;
    }
  }
}
