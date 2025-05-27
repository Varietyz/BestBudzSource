package com.bestbudz.rs2.entity;

public class Sound {

  public final short id;

  public final byte delay;

  public final byte type;

  public Sound(int id) {
    this.id = ((short) id);
    delay = 0;
    type = 10;
  }

  public Sound(int id, int delay) {
    this.id = ((short) id);
    this.delay = ((byte) delay);
    type = 10;
  }

  public byte getDelay() {
    return delay;
  }

  public short getId() {
    return id;
  }

  public byte getType() {
    return type;
  }
}
