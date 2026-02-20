package com.bestbudz.rs2.content.minigames.pestcontrol;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.minigames.pestcontrol.monsters.Portal;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.VirtualMobRegion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.List;

public class PestControlGame {

  public static final String PEST_DAMAGE_KEY = "pestdamagekey";
  public static final String PEST_GAME_KEY = "pestgamekey";
  private final List<Stoner> stoners;
  private final int z;
  private final VirtualMobRegion region;
  private final Portal[] portals;
  public String[] VOID_KNIGHT_MESSAGES = {
    "They're stealing our weed!",
    "Take down the portals, hurry noobs!",
    "Get em, LEEEEEEEEEROYYY JENKISSSSS STYLE!",
    "Yall da best, buddies!",
    "We are saving our weed!"
  };
  private Mob voidKnight;
  private int time = 300;
  private boolean ended = false;

  public PestControlGame(List<Stoner> stoners, int count) {
    this.stoners = stoners;
    z = (count << 2);
    region = new VirtualMobRegion();

    portals =
        new Portal[] {
          new Portal(
              this,
              PestControlConstants.PORTAL_IDS[0],
              PestControlConstants.PORTAL_SPAWN_LOCATIONS[0],
              z),
          new Portal(
              this,
              PestControlConstants.PORTAL_IDS[1],
              PestControlConstants.PORTAL_SPAWN_LOCATIONS[1],
              z),
          new Portal(
              this,
              PestControlConstants.PORTAL_IDS[2],
              PestControlConstants.PORTAL_SPAWN_LOCATIONS[2],
              z),
          new Portal(
              this,
              PestControlConstants.PORTAL_IDS[3],
              PestControlConstants.PORTAL_SPAWN_LOCATIONS[3],
              z),
        };

    init();
  }

  public void end(boolean success) {
    ended = true;

    if (success) {
      World.sendGlobalMessage(
          "<img=8> <col=C42BAD>"
              + stoners.size()
              + " stoners have completed a Pest Control minigame.");
    } else {
      World.sendGlobalMessage(
          "<img=8> <col=C42BAD>"
              + stoners.size()
              + " stoners have failed a Pest Control minigame.");
    }

    for (Portal i : portals) {
      i.remove();
    }

    voidKnight.remove();

    for (Stoner p : stoners) {
      p.teleport(new Location(2657, 2639));

      p.setController(ControllerManager.DEFAULT_CONTROLLER);

      p.getCombat().reset();
      p.resetGrades();
      p.curePoison(0);

      if (success) {
        if (p.getAttributes().get(PEST_DAMAGE_KEY) != null
            && p.getAttributes().getInt(PEST_DAMAGE_KEY) >= 80) {
			p.send(new SendMessage("You have managed to protect our weed! You got some points and Khalifa Kush. Get smoking fam."));
          p.getBox()
              .addOrCreateGroundItem(269, p.getAttributes().getInt(PEST_DAMAGE_KEY) * 6, true);
          p.setPestPoints(p.getPestPoints() + (10));
        } else {
			p.send(new SendMessage("Fellow stoners protected the weed, but u didnt seem to care much."));
        }
      } else {
		  p.send(new SendMessage("SHIT! FUCK! STUPID! THEY TOOK OUR WEED!!"));
      }

      p.getAttributes().remove(PEST_DAMAGE_KEY);
      p.getAttributes().remove(PEST_GAME_KEY);
    }

    for (Portal i : portals) {
      i.cleanup();
    }

    voidKnight.remove();

    PestControl.onGameEnd(this);
  }

  public int getAssaulters(Stoner p) {
    int i = 0;

    for (Portal k : portals) {
      for (Mob j : k.getPests()) {
        if (j.getCombat().getAssaulting() != null && j.getCombat().getAssaulting().equals(p)) {
          i++;
        }
      }
    }

    return i;
  }

  public List<Stoner> getStoners() {
    return stoners;
  }

  public VirtualMobRegion getVirtualRegion() {
    return region;
  }

  public Mob getVoidKnight() {
    return voidKnight;
  }

  public int getZ() {
    return z;
  }

  public boolean hasEnded() {
    return ended;
  }

  public void init() {
    for (Stoner p : stoners) {
      p.teleport(new Location(2656 + Utility.randomNumber(4), 2609 + Utility.randomNumber(6), z));
      p.getAttributes().set(PEST_DAMAGE_KEY, 0);
      p.getAttributes().set(PEST_GAME_KEY, this);
      p.setController(ControllerManager.PEST_CONTROLLER);

		p.send(new SendMessage("SAVE THE WEED! Get these portals down! Get! Get! Get!"));
    }

    time = 300;

    voidKnight = new Mob(region, 1756, false, false, new Location(2656, 2592, z));

    voidKnight.getGrades()[Professions.LIFE] = 200;
    voidKnight.getMaxGrades()[Professions.LIFE] = 200;
    voidKnight.getGrades()[Professions.AEGIS] = 400;

    voidKnight.setRespawnable(false);

    voidKnight.getAttributes().set(PEST_GAME_KEY, this);
  }

  public void process() {
    time--;

    if (time <= 0) {
      end(false);
      return;
    }

    if (voidKnight.isDead()) {
      end(false);
      return;
    }

    if (!portals[0].isActive()
        && !portals[1].isActive()
        && !portals[2].isActive()
        && !portals[3].isActive()) {
      end(true);
    }

    int random = 5;
    if (random == 3) {
      voidKnight.getUpdateFlags().sendForceMessage(Utility.randomElement(VOID_KNIGHT_MESSAGES));
    }

    for (Stoner p : stoners) {
      p.getClient().queueOutgoingPacket(new SendString(Utility.getFormattedTime(time), 21117));
      p.getClient()
          .queueOutgoingPacket(
              new SendString("" + voidKnight.getGrades()[Professions.LIFE], 21115));

      for (int i = 0; i < 4; i++) {
        boolean dead = portals[i].isDead();
        p.getClient()
            .queueOutgoingPacket(
                new SendString(
                    (dead ? "@red@Dead" : "" + portals[i].getGrades()[Professions.LIFE]),
                    21111 + i));
      }

      if (p.getAttributes().get(PEST_DAMAGE_KEY) != null) {
        int damage = p.getAttributes().getInt(PEST_DAMAGE_KEY);
        p.getClient()
            .queueOutgoingPacket(
                new SendString(
                    (damage >= 80 ? "" : "@red@") + p.getAttributes().getInt(PEST_DAMAGE_KEY),
                    21116));
      }
    }
  }

  public void remove(Stoner p) {
    stoners.remove(p);

    if (stoners.size() == 0) {
      for (Portal i : portals) {
        i.cleanup();
      }

      voidKnight.remove();

      PestControl.onGameEnd(this);
    }
  }
}
