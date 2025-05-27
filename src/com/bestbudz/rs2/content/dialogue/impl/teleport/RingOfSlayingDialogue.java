package com.bestbudz.rs2.content.dialogue.impl.teleport;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class RingOfSlayingDialogue extends Dialogue {

  private final int itemId;
  private boolean operate = false;

  public RingOfSlayingDialogue(Stoner stoner, boolean operate, int itemId) {
    this.stoner = stoner;
    this.operate = operate;
    this.itemId = itemId;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case 9178:
        getStoner().getMage().teleport(3086, 3489, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        if (operate) {
          if (itemId - 2 != 1702) {
            stoner.getEquipment().getItems()[2].setId(itemId - 2);
            stoner.getEquipment().update();
          }
        }
        if (!operate) {
          if (itemId - 2 != 1702) {
            stoner.getBox().remove(itemId);
            stoner.getBox().add(itemId - 2, 1);
          }
        }
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "@pur@You use up a charge from your "
                        + Item.getDefinition(itemId).getName()
                        + " and teleport away."));
        break;
      case 9179:
        getStoner().getMage().teleport(3093, 3244, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        if (operate) {
          if (itemId - 2 != 1702) {
            stoner.getEquipment().getItems()[2].setId(itemId - 2);
            stoner.getEquipment().update();
          }
        }
        if (!operate) {
          if (itemId - 2 != 1702) {
            stoner.getBox().remove(itemId);
            stoner.getBox().add(itemId - 2, 1);
          }
        }
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "@pur@You use up a charge from your "
                        + Item.getDefinition(itemId).getName()
                        + " and teleport away."));
        break;
      case 9180:
        getStoner().getMage().teleport(2909, 3151, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        if (operate) {
          if (itemId - 2 != 1702) {
            stoner.getEquipment().getItems()[2].setId(itemId - 2);
            stoner.getEquipment().update();
          }
        }
        if (!operate) {
          if (itemId - 2 != 1702) {
            stoner.getBox().remove(itemId);
            stoner.getBox().add(itemId - 2, 1);
          }
        }
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "@pur@You use up a charge from your "
                        + Item.getDefinition(itemId).getName()
                        + " and teleport away."));
        break;
      case 9181:
        getStoner().getMage().teleport(3356, 3268, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        if (operate) {
          if (itemId - 2 != 1702) {
            stoner.getEquipment().getItems()[2].setId(itemId - 2);
            stoner.getEquipment().update();
          }
        }
        if (!operate) {
          if (itemId - 2 != 1702) {
            stoner.getBox().remove(itemId);
            stoner.getBox().add(itemId - 2, 1);
          }
        }
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "@pur@You use up a charge from your "
                        + Item.getDefinition(itemId).getName()
                        + " and teleport away."));
        break;
    }
    return false;
  }

  @Override
  public void execute() {
    DialogueManager.sendOption(
        stoner,
        "Vannaka",
        "Mercenary Tower",
        "Gold Member Mercenary Dungeon",
        "Ancient Cavern Mercenary Dungeon");
  }
}
