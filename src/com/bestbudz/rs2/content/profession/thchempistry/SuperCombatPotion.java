package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayList;

public class SuperCombatPotion {

  private static final int POTION_ID = 12695;

  private static final int[] ITEMS = {11836, 11830, 11838, 111};

  private static final short GRADE = 91;

  private static final short EXPERIENCE = 8500;

  private static final short ADVANCE = 4;

  public static boolean itemOnItem(Stoner stoner, Item itemUsed, Item usedWith) {
    if (itemUsed.getId() == usedWith.getId()) {
      return false;
    }
    for (int index = 0; index < ITEMS.length; index++) {
      if (itemUsed.getId() == ITEMS[index] || usedWith.getId() == ITEMS[index]) {
        create(stoner);
        return true;
      }
    }
    return false;
  }

  private static void create(Stoner stoner) {
    if (stoner.getGrades()[Professions.THCHEMPISTRY] < GRADE) {
      stoner.send(new SendMessage("You need a THC-hempistry grade of " + GRADE + " to do this!"));
      return;
    }

    if (stoner.getProfessionAdvances()[Professions.THCHEMPISTRY] < ADVANCE) {
      stoner.send(
          new SendMessage("You need to advance THC-hempistry to " + ADVANCE + " to do this!"));
      return;
    }

    if (!stoner.getEquipment().isWearingItem(6575)) {
      DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
      return;
    }
    boolean hasItems = true;
    ArrayList<String> required = new ArrayList<String>();
    for (int index = 0; index < ITEMS.length; index++) {
      if (!stoner.getBox().hasItemId(ITEMS[index])) {
        String name = GameDefinitionLoader.getItemDef(ITEMS[index]).getName();
        hasItems = false;
        required.add(name);
        continue;
      }
    }
    if (!hasItems) {
      stoner.send(new SendMessage("@dre@Making Jah Powa (4) requires: " + required + "."));
      return;
    }
    for (int index = 0; index < ITEMS.length; index++) {
      stoner.getBox().remove(ITEMS[index]);
    }
    stoner.getBox().add(POTION_ID, 1);
    DialogueManager.sendItem1(stoner, "You have combined all the ingredients!", POTION_ID);
    stoner.getUpdateFlags().sendAnimation(830, 0);
    stoner.getProfession().addExperience(Professions.THCHEMPISTRY, EXPERIENCE);
  }
}
