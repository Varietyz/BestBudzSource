package com.bestbudz.rs2.content.minigames.godwars;

import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.Allegiance;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.DefaultController;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendWalkableInterface;

public class GodWarsController extends DefaultController {

  @Override
  public void onControllerInit(Stoner stoner) {
    stoner.send(new SendWalkableInterface(16210));

    for (Allegiance allegiance : Allegiance.values()) {
      stoner.getMinigames().updateGWKC(allegiance);
    }
  }
}
