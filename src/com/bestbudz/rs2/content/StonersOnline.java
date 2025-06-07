package com.bestbudz.rs2.content;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.function.Predicate;

public class StonersOnline {

  public static String rank(Stoner stoner) {
    switch (stoner.getRights()) {
      case 0:
        return "<img=11>";
      case 1:
        return "<img=0> ";
      case 2:
        return "<img=1> ";
      case 3:
        return "<img=2> ";
      case 4:
        return "<img=3> ";
      case 5:
        return "<img=4> ";
      case 6:
        return "<img=5> ";
      case 7:
        return "<img=6> ";
      case 8:
        return "<img=7> ";
      case 9:
        return "<img=8> ";
      case 10:
        return "<img=9> ";
      case 11:
        return "<img=10> ";
      case 12:
        return "<img=11> ";
      case 13:
        return "<img=13> ";
    }
    return "";
  }

  public static void showStoners(Stoner stoner, Predicate<Stoner> stonerType) {
    for (int index = 0; index < 50; index++) {
      stoner.send(new SendString("", 8145 + index));
    }

    stoner.send(
        new SendString(
            "BestBudz's <img=11> Active Stoners (</col> "
                + World.getActiveStoners()
                + " )",
            8144));

    int frameBegin = 8145;

    for (Stoner p : World.getStoners()) {

      if (p == null || !p.isActive()) {
        continue;
      }

      if (stonerType.test(p)) {
        stoner.send(
            new SendString(rank(p) + Utility.formatStonerName(p.getUsername()), frameBegin++));
      }
    }

    stoner.send(new SendInterface(8134));
  }
}
