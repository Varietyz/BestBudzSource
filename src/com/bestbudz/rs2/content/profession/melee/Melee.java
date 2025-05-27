package com.bestbudz.rs2.content.profession.melee;

public class Melee {
  private boolean veracEffectActive = false;
  private boolean guthanEffectActive = false;
  private boolean toragEffectActive = false;

  public void afterCombat() {
    if (veracEffectActive) veracEffectActive = false;
  }

  public boolean isGuthanEffectActive() {
    return guthanEffectActive;
  }

  public void setGuthanEffectActive(boolean guthanEffectActive) {
    this.guthanEffectActive = guthanEffectActive;
  }

  public boolean isToragEffectActive() {
    return toragEffectActive;
  }

  public void setToragEffectActive(boolean toragEffectActive) {
    this.toragEffectActive = toragEffectActive;
  }

  public boolean isVeracEffectActive() {
    return veracEffectActive;
  }

  public void setVeracEffectActive(boolean veracEffectActive) {
    this.veracEffectActive = veracEffectActive;
  }
}
