package com.bestbudz.core.definitions;

public class EquipmentDefinition {

  private short id;
  private byte slot;
  private byte[] requirements;

  public int getId() {
    return id;
  }

  public byte[] getRequirements() {
    return requirements;
  }

  public void setRequirements(byte[] requirements) {
    this.requirements = requirements;
  }

  public int getSlot() {
    return slot;
  }
}
