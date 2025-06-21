package com.bestbudz.rs2.entity.mob.bosses;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.List;

public class DarkEnergyCore extends Mob {

  public static final int CORPOREAL_BEAST_INDEX = 1;

  public static final int DARK_ENERGY_CORE_ID = 8127;
  private final Stoner bind;
  public boolean moving = false;
  private byte pause = -1;

  public DarkEnergyCore(Location location, Stoner bind) {
    super(8127, false, location, null, false, false, null);
    this.bind = bind;
  }

  public static final Mob getCorp() {
    return World.getNpcs()[1];
  }

  private static final Projectile getProjectile() {
    return new Projectile(0);
  }

  public static final Mob[] spawn() {
    List<Stoner> stoners = getCorp().getCombatants();

    Mob[] cores = new Mob[stoners.size()];

    for (int i = 0; i < stoners.size(); i++) {
      Location l = new Location(stoners.get(i).getLocation());
      l.move(1, 0);
      cores[i] = new DarkEnergyCore(l, stoners.get(i));
    }

    return cores;
  }

  public Hit getHit() {
    return new Hit(this, Utility.randomNumber(10), Hit.HitTypes.NONE);
  }

  @Override
  public void onHit(Entity e, Hit hit) {
    Mob corp = getCorp();
    int tmp9_8 = 3;
    long[] tmp9_5 = corp.getGrades();
    tmp9_5[tmp9_8] = ((short) (tmp9_5[tmp9_8] + hit.getDamage() / 4));

    if (corp.getGrades()[3] > corp.getMaxGrades()[3]) corp.getGrades()[3] = corp.getMaxGrades()[3];
  }

  @Override
  public void process() {
    if ((bind.isDead())
        || (!bind.isActive())
        || (getCorp().isDead())
        || (!getCorp().getCombatants().contains(bind))) {
      remove();
      return;
    }

    if ((!moving) && (!isDead()))
      if ((Math.abs(bind.getLocation().getX() - getLocation().getX()) <= 1)
          && (Math.abs(bind.getLocation().getY() - getLocation().getY()) <= 1)) {
        bind.hit(getHit());
      } else {
        if (pause == -1) {
          pause = 4;
        }

        if ((this.pause = (byte) (pause - 1)) == 0) {
          pause = -1;
          travel();
        }
      }
  }

  public void travel() {
    moving = true;

    final int lockon = -bind.getIndex() - 1;
    final byte offsetX = (byte) ((bind.getLocation().getY() - bind.getLocation().getY()) * -1);
    final byte offsetY = (byte) ((bind.getLocation().getX() - bind.getLocation().getX()) * -1);

    final Projectile p = getProjectile();

    TaskQueue.queue(
        new Task(this, 1) {
          byte stage = 0;

          @Override
          public void execute() {
            if (stage == 0) {
              World.sendProjectile(p, getLocation(), lockon, offsetX, offsetY);
            } else if (stage == 1) {
              getUpdateFlags().sendAnimation(10393, 0);
              face(bind);
            } else if (stage == 2) {
              setVisible(false);
            } else if (stage == 4) {
              moving = false;
              teleport(new Location(bind.getLocation().getX() + 1, bind.getLocation().getY()));
              setVisible(true);
              stop();
            }

            stage = ((byte) (stage + 1));
          }

          @Override
          public void onStop() {}
        });
  }
}
