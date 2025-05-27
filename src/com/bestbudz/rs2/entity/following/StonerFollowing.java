package com.bestbudz.rs2.entity.following;

import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pathfinding.RS317PathFinder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class StonerFollowing extends Following {
  private final Stoner stoner;

  public StonerFollowing(Stoner stoner) {
    super(stoner);
    this.stoner = stoner;
  }

  @Override
  public void findPath(Location location) {
    if (type == Following.FollowType.COMBAT) {
      if (stoner.getCombat().getCombatType() == CombatTypes.MELEE)
        RS317PathFinder.findRoute(stoner, location.getX(), location.getY(), false, 0, 0);
      else RS317PathFinder.findRoute(stoner, location.getX(), location.getY(), true, 16, 16);
    } else RS317PathFinder.findRoute(stoner, location.getX(), location.getY(), true, 16, 16);
  }

  @Override
  public void onCannotReach() {
    reset();

    if (type == Following.FollowType.COMBAT) {
      stoner.getCombat().reset();
    }

    stoner.getClient().queueOutgoingPacket(new SendMessage("I can't reach that!"));
  }

  @Override
  public boolean pause() {
    if (type == Following.FollowType.COMBAT) {
      if (GameConstants.withinBlock(
          following.getLocation().getX(),
          following.getLocation().getY(),
          following.getSize(),
          stoner.getLocation().getX(),
          stoner.getLocation().getY())) {
        return false;
      }

      if (following.isNpc()) {
        CombatTypes c = stoner.getCombat().getCombatType();

        if ((c == CombatTypes.MAGE) || (c == CombatTypes.SAGITTARIUS)) {
          Mob mob = com.bestbudz.rs2.entity.World.getNpcs()[following.getIndex()];

          if (mob == null) {
            return false;
          }

          if (!mob.withinMobWalkDistance(stoner)) {
            return false;
          }
        }
      }

      return (!stoner.getLocation().equals(following.getLocation()))
          && (stoner
              .getCombat()
              .withinDistanceForAssault(stoner.getCombat().getCombatType(), true));
    }

    return false;
  }
}
