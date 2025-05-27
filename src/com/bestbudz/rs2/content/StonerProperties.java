package com.bestbudz.rs2.content;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;

public class StonerProperties {

  public static final String ATTRIBUTE_KEY = "PROPERTY_";

  private final Stoner stoner;

  public StonerProperties(Stoner stoner) {
    this.stoner = stoner;
  }

  public void addProperty(String attributeSuffix, String name, int increment) {
    attributeSuffix = attributeSuffix.trim().toUpperCase().replaceAll(" ", "_");
    name = name.trim().toUpperCase().replaceAll(" ", "_");
    int current = stoner.getAttributes().getInt(ATTRIBUTE_KEY + attributeSuffix + "_" + name);

    if (!stoner.getAttributes().isSet(ATTRIBUTE_KEY + attributeSuffix + "_" + name)) {
      current = 0;
    }

    stoner.getAttributes().set(ATTRIBUTE_KEY + attributeSuffix + "_" + name, current + increment);
  }

  public void addProperty(String property, int increment) {
    addProperty("STONER", property, increment);
  }

  public void addProperty(Mob mob, int increment) {
    addProperty("MOB", mob.getDefinition().getName(), increment);
    stoner.send(
        new SendMessage(
            "Your "
                + mob.getDefinition().getName()
                + " kill count is: @red@"
                + Utility.format(
                    stoner.getProperties().getPropertyValue("MOB_" + mob.getDefinition().getName()))
                + "</col>."));
  }

  public void addProperty(Item item, int increment) {
    addProperty("ITEM", item.getDefinition().getName(), increment);
  }

  public int getPropertyValue(String property) {
    property = property.trim().toUpperCase().replaceAll(" ", "_");

    int value = 0;

    for (Object attribute : stoner.getAttributes().getAttributes().keySet()) {
      if (String.valueOf(attribute).startsWith(ATTRIBUTE_KEY + property)) {
        value = stoner.getAttributes().getInt(attribute);
      }
    }

    return value;
  }

  public HashMap<String, Integer> getPropertyValues(String property) {
    property = property.trim().toUpperCase().replaceAll(" ", "_");

    HashMap<String, Integer> properties = new HashMap<>();

    for (Object attribute : stoner.getAttributes().getAttributes().keySet()) {
      if (String.valueOf(attribute).startsWith(ATTRIBUTE_KEY + property + "_")) {
        properties.put(
            String.valueOf(attribute).replace(ATTRIBUTE_KEY + property + "_", ""),
            stoner.getAttributes().getInt(attribute));
      }
    }

    return properties;
  }

  public void setDefaults() {
    for (int npc : MobConstants.LOGGED_NPCS) {
      stoner
          .getProperties()
          .addProperty("MOB", GameDefinitionLoader.getNpcDefinition(npc).getName(), 0);
    }
  }
}
