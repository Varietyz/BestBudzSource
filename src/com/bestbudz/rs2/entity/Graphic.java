package com.bestbudz.rs2.entity;

public final class Graphic {

  private final int id;
  private final int height;
  private final int delay;

  public Graphic(Graphic other) {
    id = other.id;
    height = other.height;
    delay = other.delay;
  }

  public Graphic(int id) {
    this(id, 0, false);
  }

  public Graphic(int id, boolean height) {
    this(id, 0, height);
  }

  public Graphic(int id, int delay, boolean high) {
    this.id = id;
    if (high) {
      height = 100;
    } else {
      height = 0;
    }
    this.delay = delay;
  }

  public Graphic(int id, int delay, int height) {
    this.id = id;
    this.height = height;
    this.delay = delay;
  }

  public static Graphic highGraphic(int id, int delay) {
    return new Graphic(id, delay, 100);
  }

  public static Graphic lowGraphic(int id, int delay) {
    return new Graphic(id, delay, 0);
  }

  public int getDelay() {
    return delay;
  }

  public int getHeight() {
    return height;
  }

  public int getId() {
    return id;
  }

  public int getValue() {
    return delay | height << 16;
  }
}
