package com.bestbudz.rs2.content.combat;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.combat.impl.DamageMap;
import com.bestbudz.rs2.content.combat.impl.Mage;
import com.bestbudz.rs2.content.combat.impl.Melee;
import com.bestbudz.rs2.content.combat.impl.Sagittarius;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.pathfinding.StraightPathFinder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.security.SecureRandom;

public class Combat {

  private static final SecureRandom random = new SecureRandom();
  private final Entity entity;
  private final Melee melee;
  private final Sagittarius sagittarius;
  private final Mage mage;
  private final DamageMap damageMap;
  private Entity assaulting = null;
  private Entity lastAssaultedBy = null;
  private Animation blockAnimation = null;
  private CombatTypes combatType = CombatTypes.MELEE;
  private long combatTimer = 0L;
  private int assaultTimer = 5;
  private long hitChainStage = 0;
  private long lastDamageDealt = 0;
  private boolean chainPrimed = false;
  private double hitChance = 0.0;

  public Combat(Entity entity) {
    this.entity = entity;
    melee = new Melee(entity);
    sagittarius = new Sagittarius(entity);
    mage = new Mage(entity);
    damageMap = new DamageMap(entity);
  }

  public static int next(int length) {
    return random.nextInt(length) + 1;
  }

  public static void applyHit(Entity target, Hit hit) {
    if (target == null || hit == null) {
      return;
    }

    target.getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());

    // Show hit
    target.setLastHitSuccess(hit.isSuccess()); // Update flag

    if (target.getCombat() != null) {
      target.getCombat().updateHitChain(hit.getDamage()); // ✅ correct now
      target.getCombat().setInCombat(hit.getAssaulter());
    }
  }

  public long getHitChainStage() {
    return hitChainStage;
  }

  public long getHitChainBonus() {
    return hitChainStage * 10; // % bonus
  }

  public void resetHitChain() {
    if (hitChainStage > 0 && entity instanceof Stoner) {
      Stoner stoner = (Stoner) entity;
      stoner.getClient().queueOutgoingPacket(new SendMessage("@red@Your hit chain has ended."));
    }
    hitChainStage = 0;
  }

  public void advanceHitChain() {
    if (hitChainStage < 3) {
      hitChainStage++;
    }
    if (entity instanceof Stoner) {
      Stoner stoner = (Stoner) entity;
      if (hitChainStage > 0) {
        String color = hitChainStage == 1 ? "@blu@" : hitChainStage == 2 ? "@cya@" : "@gre@";
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(color + "You're on a hit chain! Stage: " + hitChainStage));
      }
      long bonus = entity.getCombat().getHitChainBonus();
      if (bonus > 0) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage("@gre@Chain bonus active: +" + bonus + "% damage boost."));
      }
    }
    if (entity instanceof Stoner) {}
  }

  public void updateHitChain(long newDamage) {
    if (!chainPrimed) {
      lastDamageDealt = newDamage;
      chainPrimed = true;
      return;
    }

    if (newDamage > lastDamageDealt) {
      lastDamageDealt = newDamage;
      chainPrimed = false;
      advanceHitChain(); // Stage 1 starts here
    } else {
      chainPrimed = false;
      resetHitChain();
      lastDamageDealt = newDamage;
    }
  }

  public double getHitChance() {
    return hitChance;
  }

  public void setHitChance(double chance) {
    this.hitChance = chance;
  }

  public void assault() {
    entity.face(assaulting);

    if ((!assaulting.isActive())
        || (assaulting.isDead())
        || (entity.isDead())
        || (assaulting.getLocation().getZ() != entity.getLocation().getZ())) {
      reset();
      return;
    }

    if (!withinDistanceForAssault(combatType, false)) {
      return;
    }

    if (!entity.canAssault()) {
      entity.getFollowing().reset();
      reset();
      return;
    }

    entity.onCombatProcess(assaulting);
    switch (combatType) {
      case MELEE:
        melee.execute(assaulting);
        melee.setNextDamage(-1);
        melee.setDamageBoost(1.0D);
        break;
      case MAGE:
        mage.execute(assaulting);
        mage.setMulti(true);
        mage.setpDelay((byte) 0);
        break;
      case SAGITTARIUS:
        sagittarius.execute(assaulting);
        break;
      case NONE:
        break;
    }

    entity.afterCombatProcess(assaulting);
  }

  public void forRespawn() {
    combatTimer = 0L;
    damageMap.clear();
    assaultTimer = 0;
    lastAssaultedBy = null;
    entity.setDead(false);
    entity.resetGrades();
  }

  public int getAssaultCooldown() {
    switch (combatType) {
      case NONE:
        return 3;
      case MAGE:
        return mage.getAssault().getAssaultDelay();
      case MELEE:
        return melee.getAssault().getAssaultDelay();
      case SAGITTARIUS:
        if (sagittarius == null || sagittarius.getAssault() == null) {
          return 4;
        }
        return sagittarius.getAssault().getAssaultDelay();
    }
    return 4;
  }

  public Entity getAssaulting() {
    return assaulting;
  }

  public void setAssaulting(Entity assaulting) {
    this.assaulting = assaulting;
  }

  public int getAssaultTimer() {
    return assaultTimer;
  }

  public void setAssaultTimer(int assaultTimer) {
    this.assaultTimer = assaultTimer;
  }

  public Animation getBlockAnimation() {
    return blockAnimation;
  }

  public void setBlockAnimation(Animation blockAnimation) {
    this.blockAnimation = blockAnimation;
  }

  public CombatTypes getCombatType() {
    return combatType;
  }

  public void setCombatType(CombatTypes combatType) {
    this.combatType = combatType;
  }

  public DamageMap getDamageTracker() {
    return damageMap;
  }

  public double getDistanceFromTarget() {
    if (assaulting == null) {
      return -1.0D;
    }
    return Math.abs(entity.getLocation().getX() - assaulting.getLocation().getX())
        + Math.abs(entity.getLocation().getY() - assaulting.getLocation().getY());
  }

  public Entity getLastAssaultedBy() {
    return lastAssaultedBy;
  }

  public Mage getMage() {
    return mage;
  }

  public Melee getMelee() {
    return melee;
  }

  public Sagittarius getSagittarius() {
    return sagittarius;
  }

  public boolean inCombat() {
    return combatTimer > World.getCycles();
  }

  public void increaseAssaultTimer(int amount) {
    assaultTimer += amount;
  }

  public boolean isWithinDistance(int req) {
    if (!entity.isNpc()
        && !assaulting.isNpc()
        && Utility.getManhattanDistance(assaulting.getLocation(), entity.getLocation()) == 0) {
      return false;
    }

    int x = entity.getLocation().getX();
    int y = entity.getLocation().getY();
    int x2 = assaulting.getLocation().getX();
    int y2 = assaulting.getLocation().getY();

    if (GameConstants.withinBlock(x, y, entity.getSize(), x2, y2)) {
      return true;
    }

    if (Utility.getManhattanDistance(x, y, x2, y2) <= req) {
      return true;
    }

    Location[] a = GameConstants.getBorder(x, y, entity.getSize());
    Location[] b = GameConstants.getBorder(x2, y2, assaulting.getSize());

    for (Location i : a) {
      for (Location k : b) {
        if (Utility.getManhattanDistance(i, k) <= req) {
          return true;
        }
      }
    }
    return false;
  }

  public void process() {
    if (assaultTimer > 0) {
      assaultTimer -= 1;
    }

    if ((assaulting != null) && (assaultTimer == 0)) {
      assault();
    }

    if ((!entity.isDead()) && (!inCombat()) && (damageMap.isClearHistory())) damageMap.clear();
  }

  public void reset() {
    assaulting = null;
    entity.getFollowing().reset();
  }

  public void resetCombatTimer() {
    combatTimer = 0L;
  }

  public void setAssault(Entity e) {
    assaulting = e;
    entity.getFollowing().setFollow(e, Following.FollowType.COMBAT);
  }

  public void setInCombat(Entity assaultedBy) {
    lastAssaultedBy = assaultedBy;
    combatTimer = World.getCycles() + 8;
  }

  public void updateTimers(int delay) {
    assaultTimer = delay;

    if (entity.getAttributes().get("assaulttimerpowerup") != null) {
      assaultTimer /= 2;
    }
  }

  public boolean withinDistanceForAssault(CombatTypes type, boolean noMovement) {
    if (assaulting == null) {
      return false;
    }

    if (type == null) {
      type = combatType;
    }

    int dist = CombatConstants.getDistanceForCombatType(type);

    boolean ignoreClipping = false;

    if (entity.isNpc()) {
      Mob m = World.getNpcs()[entity.getIndex()];
      if (m != null) {
        if (m.getId() == 8596) {
          dist = 18;
          ignoreClipping = true;
        } else if (m.getId() == 3847) {
          if (type == CombatTypes.MELEE) {
            dist = 2;
            ignoreClipping = true;
          }
        } else if (m.getId() == 2042 || m.getId() == 2043 || m.getId() == 2044) {
          dist = 25;
          ignoreClipping = true;
        }

        if (MobConstants.isDragon(m)) {
          dist = 1;
        }
      }
    }

    if (!entity.isNpc()) {
      if (type == CombatTypes.MELEE) {
        if (assaulting.isNpc()) {
          Mob m = World.getNpcs()[assaulting.getIndex()];
          if (m != null) {
            if (m.getId() == 3847) {
              dist = 2;
              ignoreClipping = true;
            } else if (m.getId() == 2042 || m.getId() == 2043 || m.getId() == 2044) {
              dist = 25;
              ignoreClipping = true;
            }
          }
        }
      } else if (type == CombatTypes.SAGITTARIUS || type == CombatTypes.MAGE) {
        if (assaulting.isNpc()) {
          Mob m = World.getNpcs()[assaulting.getIndex()];
          if (m != null) {
            if (m.getId() == 2042 || m.getId() == 2043 || m.getId() == 2044) {
              dist = 25;
              ignoreClipping = true;
            } else if (m.getId() == 5535 || m.getId() == 494) {
              dist = 65;
              ignoreClipping = false;
            }
          }
        }
      }
    }

    if (!noMovement
        && !entity.isNpc()
        && !assaulting.isNpc()
        && entity.getMovementHandler().moving()) {
      dist += 3;
    }

    if (!isWithinDistance(dist)) {
      return false;
    }

    if (!ignoreClipping) {
      boolean blocked = true;

      if (type == CombatTypes.MAGE || combatType == CombatTypes.SAGITTARIUS) {
        for (Location i :
            GameConstants.getEdges(
                entity.getLocation().getX(), entity.getLocation().getY(), entity.getSize())) {
          if (entity.inGodwars()) {
            if (StraightPathFinder.isProjectilePathClear(i, assaulting.getLocation())
                && StraightPathFinder.isProjectilePathClear(assaulting.getLocation(), i)) {
              blocked = false;
              break;
            }
          } else {
            if (StraightPathFinder.isProjectilePathClear(i, assaulting.getLocation())
                || StraightPathFinder.isProjectilePathClear(assaulting.getLocation(), i)) {
              blocked = false;
              break;
            }
          }
        }
      } else if (type == CombatTypes.MELEE) {
        for (Location i :
            GameConstants.getEdges(
                entity.getLocation().getX(), entity.getLocation().getY(), entity.getSize())) {
          if (entity.inGodwars()) {
            if (StraightPathFinder.isInteractionPathClear(i, assaulting.getLocation())
                && StraightPathFinder.isInteractionPathClear(assaulting.getLocation(), i)) {
              blocked = false;
              break;
            }
          } else {
            if (StraightPathFinder.isInteractionPathClear(i, assaulting.getLocation())
                || StraightPathFinder.isInteractionPathClear(assaulting.getLocation(), i)) {
              blocked = false;
              break;
            }
          }
        }
      }

      return !blocked;
    }
    return true;
  }

  public enum CombatTypes {
    MELEE,
    SAGITTARIUS,
    MAGE,
    NONE
  }
}
