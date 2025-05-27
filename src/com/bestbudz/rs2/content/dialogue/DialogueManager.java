package com.bestbudz.rs2.content.dialogue;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterfaceConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendModelAnimation;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNPCDialogueHead;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerDialogueHead;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class DialogueManager {

  public static void sendItem2(Stoner stoner, String text1, String text2, int item1, int item2) {
    stoner.send(new SendString(text1, 6232));
    stoner.send(new SendString(text2, 6233));
    stoner.send(new SendInterfaceConfig(6235, 170, item1));
    stoner.send(new SendInterfaceConfig(6236, 170, item2));
    stoner.send(new SendChatBoxInterface(6231));
  }

  public static void sendItem2zoom(
      Stoner stoner, String text1, String text2, int item1, int item2) {
    stoner.send(new SendString(text1, 6232));
    stoner.send(new SendString(text2, 6233));
    stoner.send(new SendInterfaceConfig(6235, 130, item1));
    stoner.send(new SendInterfaceConfig(6236, 100, item2));
    stoner.send(new SendChatBoxInterface(6231));
  }

  public static void sendItem1(Stoner stoner, String text, int item) {
    stoner.send(new SendString(text, 308));
    stoner.send(new SendInterfaceConfig(307, 200, item));
    stoner.send(new SendChatBoxInterface(306));
  }

  public static void makeItem3(
      Stoner stoner,
      int itemId1,
      String itemName1,
      int itemId2,
      String itemName2,
      int itemId3,
      String itemName3) {
    stoner.send(new SendChatBoxInterface(8880));
    stoner.send(new SendInterfaceConfig(8883, 190, itemId1));
    stoner.send(new SendInterfaceConfig(8884, 190, itemId2));
    stoner.send(new SendInterfaceConfig(8885, 190, itemId3));
    stoner.send(new SendString(itemName1, 8889));
    stoner.send(new SendString(itemName2, 8893));
    stoner.send(new SendString(itemName3, 8897));
  }

  public static void sendInformationBox(
      Stoner stoner, String title, String line1, String line2, String line3, String line4) {
    stoner.send(new SendString(title, 6180));
    stoner.send(new SendString(line1, 6181));
    stoner.send(new SendString(line2, 6182));
    stoner.send(new SendString(line3, 6183));
    stoner.send(new SendString(line4, 6184));
    stoner.send(new SendChatBoxInterface(6179));
  }

  public static void sendNpcChat(Stoner stoner, int npcId, Emotion emotion, String... lines) {
    String npcName = GameDefinitionLoader.getNpcDefinition(npcId).getName();
    switch (lines.length) {
      case 1:
        stoner.send(new SendModelAnimation(4883, emotion.getEmoteId()));
        stoner.send(new SendString(npcName, 4884));
        stoner.send(new SendString(lines[0], 4885));
        stoner.send(new SendNPCDialogueHead(npcId, 4883));
        stoner.send(new SendChatBoxInterface(4882));
        break;
      case 2:
        stoner.send(new SendModelAnimation(4888, emotion.getEmoteId()));
        stoner.send(new SendString(npcName, 4889));
        stoner.send(new SendString(lines[0], 4890));
        stoner.send(new SendString(lines[1], 4891));
        stoner.send(new SendNPCDialogueHead(npcId, 4888));
        stoner.send(new SendChatBoxInterface(4887));
        break;
      case 3:
        stoner.send(new SendModelAnimation(4894, emotion.getEmoteId()));
        stoner.send(new SendString(npcName, 4895));
        stoner.send(new SendString(lines[0], 4896));
        stoner.send(new SendString(lines[1], 4897));
        stoner.send(new SendString(lines[2], 4898));
        stoner.send(new SendNPCDialogueHead(npcId, 4894));
        stoner.send(new SendChatBoxInterface(4893));
        break;
      case 4:
        stoner.send(new SendModelAnimation(4901, emotion.getEmoteId()));
        stoner.send(new SendString(npcName, 4902));
        stoner.send(new SendString(lines[0], 4903));
        stoner.send(new SendString(lines[1], 4904));
        stoner.send(new SendString(lines[2], 4905));
        stoner.send(new SendString(lines[3], 4906));
        stoner.send(new SendNPCDialogueHead(npcId, 4901));
        stoner.send(new SendChatBoxInterface(4900));
    }
  }

  public static void sendOption(Stoner stoner, String... options) {
    if (options.length < 2) {
      return;
    }
    switch (options.length) {
      case 1:
        throw new IllegalArgumentException("1 option is not possible! (DialogueManager.java)");
      case 2:
        stoner.send(new SendString(options[0], 2461));
        stoner.send(new SendString(options[1], 2462));
        stoner.send(new SendChatBoxInterface(2459));
        break;
      case 3:
        stoner.send(new SendString(options[0], 2471));
        stoner.send(new SendString(options[1], 2472));
        stoner.send(new SendString(options[2], 2473));
        stoner.send(new SendChatBoxInterface(2469));
        break;
      case 4:
        stoner.send(new SendString(options[0], 2482));
        stoner.send(new SendString(options[1], 2483));
        stoner.send(new SendString(options[2], 2484));
        stoner.send(new SendString(options[3], 2485));
        stoner.send(new SendChatBoxInterface(2480));
        break;
      case 5:
        stoner.send(new SendString(options[0], 2494));
        stoner.send(new SendString(options[1], 2495));
        stoner.send(new SendString(options[2], 2496));
        stoner.send(new SendString(options[3], 2497));
        stoner.send(new SendString(options[4], 2498));
        stoner.send(new SendChatBoxInterface(2492));
    }
  }

  public static void sendStonerChat(Stoner stoner, Emotion emotion, String... lines) {
    switch (lines.length) {
      case 1:
        stoner.send(new SendModelAnimation(969, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 970));
        stoner.send(new SendString(lines[0], 971));
        stoner.send(new SendStonerDialogueHead(969));
        stoner.send(new SendChatBoxInterface(968));
        break;
      case 2:
        stoner.send(new SendModelAnimation(974, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 975));
        stoner.send(new SendString(lines[0], 976));
        stoner.send(new SendString(lines[1], 977));
        stoner.send(new SendStonerDialogueHead(974));
        stoner.send(new SendChatBoxInterface(973));
        break;
      case 3:
        stoner.send(new SendModelAnimation(980, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 981));
        stoner.send(new SendString(lines[0], 982));
        stoner.send(new SendString(lines[1], 983));
        stoner.send(new SendString(lines[2], 984));
        stoner.send(new SendStonerDialogueHead(980));
        stoner.send(new SendChatBoxInterface(979));
        break;
      case 4:
        stoner.send(new SendModelAnimation(987, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 988));
        stoner.send(new SendString(lines[0], 989));
        stoner.send(new SendString(lines[1], 990));
        stoner.send(new SendString(lines[2], 991));
        stoner.send(new SendString(lines[3], 992));
        stoner.send(new SendStonerDialogueHead(987));
        stoner.send(new SendChatBoxInterface(986));
    }
  }

  public static void sendStatement(Stoner stoner, String... lines) {
    switch (lines.length) {
      case 1:
        stoner.send(new SendString(lines[0], 357));
        stoner.send(new SendChatBoxInterface(356));
        break;
      case 2:
        stoner.send(new SendString(lines[0], 360));
        stoner.send(new SendString(lines[1], 361));
        stoner.send(new SendChatBoxInterface(359));
        break;
      case 3:
        stoner.send(new SendString(lines[0], 364));
        stoner.send(new SendString(lines[1], 365));
        stoner.send(new SendString(lines[2], 366));
        stoner.send(new SendChatBoxInterface(363));
        break;
      case 4:
        stoner.send(new SendString(lines[0], 369));
        stoner.send(new SendString(lines[1], 370));
        stoner.send(new SendString(lines[2], 371));
        stoner.send(new SendString(lines[3], 372));
        stoner.send(new SendChatBoxInterface(368));
        break;
      case 5:
        stoner.send(new SendString(lines[0], 375));
        stoner.send(new SendString(lines[1], 376));
        stoner.send(new SendString(lines[2], 377));
        stoner.send(new SendString(lines[3], 378));
        stoner.send(new SendString(lines[4], 379));
        stoner.send(new SendChatBoxInterface(374));
    }
  }

  public static void sendTimedNpcChat(Stoner stoner, int npcId, Emotion emotion, String... lines) {
    String npcName = GameDefinitionLoader.getNpcDefinition(npcId).getName();
    switch (lines.length) {
      case 2:
        stoner.send(new SendModelAnimation(12379, emotion.getEmoteId()));
        stoner.send(new SendString(npcName, 12380));
        stoner.send(new SendString(lines[0], 12381));
        stoner.send(new SendString(lines[1], 12382));
        stoner.send(new SendNPCDialogueHead(npcId, 12379));
        stoner.send(new SendChatBoxInterface(12378));
        break;
      case 3:
        stoner.send(new SendModelAnimation(12384, emotion.getEmoteId()));
        stoner.send(new SendString(npcName, 12385));
        stoner.send(new SendString(lines[0], 12386));
        stoner.send(new SendString(lines[1], 12387));
        stoner.send(new SendString(lines[2], 12388));
        stoner.send(new SendNPCDialogueHead(npcId, 12384));
        stoner.send(new SendChatBoxInterface(12383));
        break;
      case 4:
        stoner.send(new SendModelAnimation(11892, emotion.getEmoteId()));
        stoner.send(new SendString(npcName, 11893));
        stoner.send(new SendString(lines[0], 11894));
        stoner.send(new SendString(lines[1], 11895));
        stoner.send(new SendString(lines[2], 11896));
        stoner.send(new SendString(lines[3], 11897));
        stoner.send(new SendNPCDialogueHead(npcId, 11892));
        stoner.send(new SendChatBoxInterface(11891));
    }
  }

  public static void sendTimedStonerChat(Stoner stoner, Emotion emotion, String... lines) {
    switch (lines.length) {
      case 1:
        stoner.send(new SendModelAnimation(12774, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 12775));
        stoner.send(new SendString(lines[0], 12776));
        stoner.send(new SendStonerDialogueHead(12774));
        stoner.send(new SendChatBoxInterface(12773));
        break;
      case 2:
        stoner.send(new SendModelAnimation(12778, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 12779));
        stoner.send(new SendString(lines[0], 12780));
        stoner.send(new SendString(lines[1], 12781));
        stoner.send(new SendStonerDialogueHead(12778));
        stoner.send(new SendChatBoxInterface(12777));
        break;
      case 3:
        stoner.send(new SendModelAnimation(12783, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 12784));
        stoner.send(new SendString(lines[0], 12785));
        stoner.send(new SendString(lines[1], 12786));
        stoner.send(new SendString(lines[2], 12787));
        stoner.send(new SendStonerDialogueHead(12783));
        stoner.send(new SendChatBoxInterface(12782));
        break;
      case 4:
        stoner.send(new SendModelAnimation(11885, emotion.getEmoteId()));
        stoner.send(new SendString(stoner.getUsername(), 11886));
        stoner.send(new SendString(lines[0], 11887));
        stoner.send(new SendString(lines[1], 11888));
        stoner.send(new SendString(lines[2], 11889));
        stoner.send(new SendString(lines[3], 11890));
        stoner.send(new SendStonerDialogueHead(11885));
        stoner.send(new SendChatBoxInterface(11884));
    }
  }

  public static void sendTimedStatement(Stoner stoner, String... lines) {
    switch (lines.length) {
      case 1:
        stoner.send(new SendString(lines[0], 12789));
        stoner.send(new SendChatBoxInterface(12788));
        break;
      case 2:
        stoner.send(new SendString(lines[0], 12791));
        stoner.send(new SendString(lines[1], 12792));
        stoner.send(new SendChatBoxInterface(12790));
        break;
      case 3:
        stoner.send(new SendString(lines[0], 12794));
        stoner.send(new SendString(lines[1], 12795));
        stoner.send(new SendString(lines[2], 12796));
        stoner.send(new SendChatBoxInterface(12793));
        break;
      case 4:
        stoner.send(new SendString(lines[0], 12798));
        stoner.send(new SendString(lines[1], 12799));
        stoner.send(new SendString(lines[2], 12800));
        stoner.send(new SendString(lines[3], 12801));
        stoner.send(new SendChatBoxInterface(12797));
        break;
      case 5:
        stoner.send(new SendString(lines[0], 12803));
        stoner.send(new SendString(lines[1], 12804));
        stoner.send(new SendString(lines[2], 12805));
        stoner.send(new SendString(lines[3], 12806));
        stoner.send(new SendString(lines[4], 12807));
        stoner.send(new SendChatBoxInterface(12802));
    }
  }
}
