package com.bestbudz.core.definitions;

public class PotionDefinition {

  private short id;
  private String name;
  private short replaceId;
  private PotionTypes potionType;
  private ProfessionData[] professionData;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public PotionTypes getPotionType() {
    return potionType;
  }

  public int getReplaceId() {
    return replaceId;
  }

  public ProfessionData[] getProfessionData() {
    return professionData;
  }

  public enum PotionTypes {
    NORMAL,
    RESTORE,
    ANTIFIRE,
    SUPER_ANTIFIRE
  }

  public class ProfessionData {

    private byte professionId;
    private byte add;
    private double modifier;

    public int getAdd() {
      return add;
    }

    public double getModifier() {
      return modifier;
    }

    public int getProfessionId() {
      return professionId;
    }
  }

  public void setName(String name) {
    this.name = name;
  }
}
