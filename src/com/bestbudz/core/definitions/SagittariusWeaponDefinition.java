package com.bestbudz.core.definitions;

import com.bestbudz.rs2.entity.item.Item;

public class SagittariusWeaponDefinition {

  private short id;
  private SagittariusTypes type;
  private Item[] arrows;

  public Item[] getArrows() {
    return arrows;
  }

  public int getId() {
    return id;
  }

  public SagittariusTypes getType() {
    return type;
  }

  public enum SagittariusTypes {
    THROWN,
    SHOT
  }
}
