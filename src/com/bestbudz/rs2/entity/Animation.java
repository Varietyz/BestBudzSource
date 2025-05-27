package com.bestbudz.rs2.entity;

public final class Animation {

  private short id;

  private byte delay;

  public Animation(int id) {
    this.id = ((short) id);
    delay = 0;
  }

  public Animation(int id, int delay) {
    this.id = ((short) id);
    this.delay = ((byte) delay);
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = ((byte) delay);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = ((short) id);
  }
}
