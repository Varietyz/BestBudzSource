package com.bestbudz.rs2.content.minigames.pestcontrol.monsters;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.minigames.pestcontrol.Pest;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControlGame;
import com.bestbudz.rs2.entity.Location;

public class Shifter extends Pest {

  private byte delay = 0;

  public Shifter(Location location, PestControlGame game) {
    super(
        game,
        PestControlConstants.SHIFTERS[Utility.randomNumber(PestControlConstants.SHIFTERS.length)],
        location);
  }

  @Override
  public void tick() {
    if (++delay == 7) {
      if (Utility.getManhattanDistance(getLocation(), getGame().getVoidKnight().getLocation())
          > 2) {
        if (!isMovedLastCycle() && getCombat().getAssaultTimer() == 0) {
          if (getCombat().getAssaulting() != null) {
            if (getCombat().getAssaulting().equals(getGame().getVoidKnight())) {
              Location l =
                  GameConstants.getClearAdjacentLocation(
                      getGame().getVoidKnight().getLocation(),
                      getSize(),
                      getGame().getVirtualRegion());

              if (l != null) {
                teleport(l);
              }
            } else {
              getCombat().setAssault(getGame().getVoidKnight());
            }
          } else {
            getCombat().setAssault(getGame().getVoidKnight());
          }
        }
      }

      delay = 0;
    }
  }
}
