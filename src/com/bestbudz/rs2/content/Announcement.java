package com.bestbudz.rs2.content;

import com.bestbudz.core.util.Stopwatch;
import com.bestbudz.rs2.entity.World;

public class Announcement {

  private static final int TIME = 1800000;

  private static final Stopwatch timer = new Stopwatch().reset();

  public String stonerPanelFrame;

  public static void sequence() {
    if (timer.elapsed(TIME)) {
      World.sendGlobalMessage("Time to roll a spliff or hit the bong!");
      timer.reset();
    }
  }
}
