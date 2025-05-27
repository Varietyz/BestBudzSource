package com.bestbudz.rs2.content.minigames;

import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.Allegiance;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.Arrays;

public class StonerMinigames {

  private final Stoner stoner;
  private short[] kc = new short[Allegiance.values().length];

  public StonerMinigames(Stoner stoner) {
    this.stoner = stoner;
  }

  public short[] getGWKC() {
    return kc;
  }

  public void setGWKC(short[] kc) {
    this.kc = kc;
  }

  public void changeGWDKills(int delta, Allegiance allegiance) {
    kc[allegiance.ordinal()] += delta;
    updateGWKC(allegiance);
  }

  public int getGWDKills(Allegiance allegiance) {
    return kc[allegiance.ordinal()];
  }

  public void updateGWKC(Allegiance allegiance) {
    stoner.send(
        new SendString(String.valueOf(getGWDKills(allegiance)), 61756 + allegiance.ordinal()));
  }

  public void resetGWD() {
    Arrays.fill(kc, (short) 0);
    updateGWKC(Allegiance.ARMADYL);
    updateGWKC(Allegiance.BANDOS);
    updateGWKC(Allegiance.SARADOMIN);
    updateGWKC(Allegiance.ZAMORAK);
  }
}
