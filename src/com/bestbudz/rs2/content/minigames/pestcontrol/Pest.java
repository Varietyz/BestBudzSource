package com.bestbudz.rs2.content.minigames.pestcontrol;

import static com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants.BRAWLERS;
import static com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants.DEFILERS;
import static com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants.RAVAGERS;
import static com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants.SHIFTERS;
import static com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants.SPINNERS;
import static com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants.SPLATTERS;
import static com.bestbudz.rs2.content.minigames.pestcontrol.PestControlConstants.TORCHERS;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.content.minigames.pestcontrol.monsters.Shifter;
import com.bestbudz.rs2.content.minigames.pestcontrol.monsters.Spinner;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class Pest extends Mob {

  private final PestControlGame game;

  private byte offset = 0;

  public Pest(PestControlGame game, int id, Location p) {
    super(game.getVirtualRegion(), id, true, false, p);
    PestControlConstants.setGrades(this);

    if (!(this instanceof Spinner)) {
      getCombat().setAssault(game.getVoidKnight());
    }

    getFollowing().setIgnoreDistance(true);

    getAttributes().set(PestControlGame.PEST_GAME_KEY, game);

    this.game = game;

    for (int i : BRAWLERS) {
      if (id == i) {
        getCombat().getMelee().setAssault(new Assault(1, 6), new Animation(3897));
        getCombat().setBlockAnimation(new Animation(3895, 0));
        return;
      }
    }

    for (int i : DEFILERS) {
      if (id == i) {
        getCombat()
            .getSagittarius()
            .setAssault(new Assault(3, 5), new Animation(3920), null, null, null);
        getCombat().setBlockAnimation(new Animation(3921, 0));
        getCombat().setCombatType(CombatTypes.SAGITTARIUS);
        return;
      }
    }

    for (int i : RAVAGERS) {
      if (id == i) {
        getCombat().getMelee().setAssault(new Assault(1, 5), new Animation(3915));
        getCombat().setBlockAnimation(new Animation(3916, 0));
        return;
      }
    }

    for (int i : SHIFTERS) {
      if (id == i) {
        getCombat().setBlockAnimation(new Animation(3902, 0));
        getCombat().getMelee().setAssault(new Assault(1, 4), new Animation(3901));
        return;
      }
    }

    for (int i : SPINNERS) {
      if (id == i) {
        getCombat().getMelee().setAssault(new Assault(1, 4), new Animation(3908));
        getCombat().setBlockAnimation(new Animation(3909, 0));
        return;
      }
    }

    for (int i : SPLATTERS) {
      if (id == i) {
        getCombat().getMelee().setAssault(new Assault(1, 4), new Animation(3891));
        getCombat().setBlockAnimation(new Animation(3890, 0));
        return;
      }
    }

    for (int i : TORCHERS) {
      if (id == i) {
        getCombat().setBlockAnimation(new Animation(3880, 0));
        getCombat().getMage().setAssault(new Assault(3, 5), new Animation(3882), null, null, null);
        getCombat().setCombatType(CombatTypes.MAGE);
        return;
      }
    }
  }

  @Override
  public void doAliveMobProcessing() {
    tick();

    if (!(this instanceof Spinner || this instanceof Shifter)) {
      if (!isMovedLastCycle() && getCombat().getAssaultTimer() == 0 && ++offset >= 4) {

        Stoner p = null;
        int dist = 99999;

        for (Stoner k : game.getStoners()) {
          int thisDist = Utility.getManhattanDistance(getLocation(), k.getLocation());
          if (thisDist <= 8) {
            if (p == null && game.getAssaulters(k) < 2
                || thisDist < dist && game.getAssaulters(k) < 2) {
              getCombat().setAssault(k);
            }
          }
        }

        offset = 0;
      }
    }
  }

  @Override
  public Animation getDeathAnimation() {
    final int id = getId();

    for (int i : BRAWLERS) {
      if (id == i) {
        return new Animation(3894);
      }
    }

    for (int i : DEFILERS) {
      if (id == i) {
        return new Animation(3922);
      }
    }

    for (int i : RAVAGERS) {
      if (id == i) {
        return new Animation(3917);
      }
    }

    for (int i : SHIFTERS) {
      if (id == i) {
        return new Animation(3903);
      }
    }

    for (int i : SPINNERS) {
      if (id == i) {
        return new Animation(3910);
      }
    }

    for (int i : SPLATTERS) {
      if (id == i) {
        return new Animation(3888);
      }
    }

    for (int i : TORCHERS) {
      if (id == i) {
        return new Animation(3881);
      }
    }

    return new Animation(-1);
  }

  public PestControlGame getGame() {
    return game;
  }

  @Override
  public int getMaxHit(CombatTypes type) {
    return getDefinition().getGrade() / 10;
  }

  public abstract void tick();

  @Override
  public void updateCombatType() {}
}
