package com.bestbudz.rs2.content.profession.mage;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.ArrayList;
import java.util.HashMap;

public class TabCreation {

  public static void handle(Stoner stoner, int itemID) {

    ItemData data = ItemData.tabs.get(itemID);

    if (data == null) {
      return;
    }

    if (stoner.getInterfaceManager().main != 26700) {
      return;
    }

    if (stoner.getGrades()[Professions.MAGE] < data.getGrade()) {
      stoner.send(
          new SendMessage(
              "<col=2555>You need a Mage grade of " + data.getGrade() + " to do this!"));
      return;
    }

    if (StonerConstants.isStoner(stoner)) {
      return;
    }

    ArrayList<String> required = new ArrayList<String>();

    for (int index = 0; index < data.getItems().length; index++) {
      if (!stoner.getBox().hasAllItems(data.getItems()[index])
          || !stoner.getBox().hasItemAmount(1761, 1)) {
        String name = GameDefinitionLoader.getItemDef(data.getItems()[index].getId()).getName();
        int amount = data.getItems()[index].getAmount();
        required.add(amount + " " + name);
        continue;
      }
    }

    for (int index = 0; index < data.getItems().length; index++) {
      if (!stoner.getBox().hasAllItems(data.getItems()[index])
          || !stoner.getBox().hasItemAmount(1761, 1)) {
        required.add("1 Soft clay");
        stoner.send(new SendMessage("<col=2555>You need " + required));
        return;
      }
    }

    for (int index = 0; index < data.getItems().length; index++) {
      stoner.getBox().remove(data.getItems()[index]);
    }

    stoner.getBox().remove(1761, 1);

    stoner.getBox().add(data.getTab(), 1);

    stoner.getProfession().addExperience(Professions.MAGE, 500);

    String name = GameDefinitionLoader.getItemDef(data.getTab()).getName();

    stoner.send(
        new SendMessage(
            "<col=2555>You have successfully created "
                + Utility.getAOrAn(name)
                + " "
                + name
                + "."));
  }

  public static void getInfo(Stoner stoner, int itemID) {

    ItemData data = ItemData.tabs.get(itemID);

    if (data == null) {
      return;
    }

    ArrayList<String> required = new ArrayList<String>();

    for (int index = 0; index < data.getItems().length; index++) {
      String name = GameDefinitionLoader.getItemDef(data.getItems()[index].getId()).getName();
      int amount = data.getItems()[index].getAmount();
      required.add(amount + " " + name);
      continue;
    }

    required.add("1 Soft clay");

    stoner.send(new SendString("" + required, 26707));
    stoner.send(new SendString("You need a Mage grade of " + data.getGrade(), 26708));
  }

  public enum ItemData {
    VARROCK(8007, 25, new Item(557, 1), new Item(556, 3), new Item(563, 1)),
    LUMBRIDGE(8008, 31, new Item(557, 1), new Item(556, 3), new Item(563, 1)),
    FALADOR(8009, 43, new Item(555, 1), new Item(556, 3), new Item(563, 1)),
    CAMELOT(8010, 55, new Item(556, 5), new Item(563, 1)),
    ARDOUGNE(8011, 63, new Item(555, 2), new Item(556, 3), new Item(563, 2)),
    WATCHTOWER(8012, 58, new Item(563, 2), new Item(555, 2)),
    HOME(8013, 90, new Item(557, 3), new Item(556, 5), new Item(563, 2)),
    BONES_TO_BANANAS(8014, 15, new Item(561, 1), new Item(557, 2), new Item(555, 2)),
    BONES_TO_PEACHES(8015, 60, new Item(561, 2), new Item(557, 4), new Item(555, 4));

    private static final HashMap<Integer, ItemData> tabs = new HashMap<Integer, ItemData>();

    static {
      for (final ItemData tab : ItemData.values()) {
        ItemData.tabs.put(tab.tabID, tab);
      }
    }

    private final int tabID;
    private final int gradeRequired;
    private final Item[] itemsRequired;

    ItemData(int tabID, int gradeRequired, Item... itemsRequired) {
      this.tabID = tabID;
      this.gradeRequired = gradeRequired;
      this.itemsRequired = itemsRequired;
    }

    public int getTab() {
      return tabID;
    }

    public int getGrade() {
      return gradeRequired;
    }

    public Item[] getItems() {
      return itemsRequired;
    }
  }
}
