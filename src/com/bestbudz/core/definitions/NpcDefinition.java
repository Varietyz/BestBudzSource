package com.bestbudz.core.definitions;

public class NpcDefinition {

  private String name;
  private short id;
  private short walkAnimation;
  private short standAnimation;
  private short turn180Animation;
  private short turn90CWAnimation;
  private short turn90CCWAnimation;
  private short grade;
  private byte size;
  private boolean assaultable;

  public int getId() {
    return id;
  }

  public short getWalkAnimation() {
    return walkAnimation;
  }

  public short getStandAnimation() {
    return standAnimation;
  }

  public short getTurn180Animation() {
    return turn180Animation;
  }

  public short getTurn90CWAnimation() {
    return turn90CWAnimation;
  }

  public short getTurn90CCWAnimation() {
    return turn90CCWAnimation;
  }

  public int getGrade() {
    return grade;
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return size;
  }

  public boolean isAssaultable() {
    return assaultable;
  }
}
