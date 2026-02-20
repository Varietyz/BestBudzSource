package com.bestbudz.rs2.content.dialogue;

import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class Dialogue {
  protected int next = 0;
  protected Stoner stoner;

  public abstract boolean clickButton(int id);

  public void end() {
    next = -1;
  }

  public abstract void execute();

  public int getNext() {
    return next;
  }

  public void setNext(int next) {
    this.next = next;
  }

  public Stoner getStoner() {
    return stoner;
  }

  public void setStoner(Stoner stoner) {
    this.stoner = stoner;
  }
}
