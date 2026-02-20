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
import com.bestbudz.rs2.entity.pets.Pet;
import static com.bestbudz.rs2.entity.pets.PetCombatSystem.findPetObjectFromStoner;
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
	private static final int[] TARGET_GRAPHICS = {451};
	private long lastCombatActionTime = 0;

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
		if (entity instanceof Stoner && assaulting instanceof Stoner) {
			Stoner targetStoner = (Stoner) assaulting;
			if (targetStoner.isPetStoner()) {
				System.out.println("CRITICAL: Stoner attempting to assault pet - aborting and resetting combat");
				reset();
				return;
			}
		}

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

		if (entity.isNpc()) {
			// Force mobs to face their target regardless of movement state
			if (assaulting != null) {
				if (!assaulting.isNpc()) {
					entity.getUpdateFlags().faceEntity(assaulting.getIndex() + 32768);
				} else {
					entity.getUpdateFlags().faceEntity(assaulting.getIndex());
				}
			}
		} else {
			// Players use the existing face() method
			if (!entity.getMovementHandler().moving()) {
				entity.face(assaulting);
			}
		}
		entity.onCombatProcess(assaulting);

			// Regular combat execution for players and NPCs
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

		if (entity instanceof Stoner && ((Stoner) entity).isPetStoner()) {
			Stoner petStoner = (Stoner) entity;

			// Find the Pet object and notify PetMaster
			Pet petObject = findPetObjectFromStoner(petStoner);
			if (petObject != null) {
				// This could be called after successful hit/damage
				onPetCombatActivity(petObject);
			}
		}
		entity.afterCombatProcess(assaulting);
	}

	private void onPetCombatActivity(Pet pet) {
		if (pet.getOwner() != null && pet.getOwner().getPetMaster() != null) {
			pet.getOwner().getPetMaster().onPetCombat(pet);
		}
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

		// FIXED: Use proper tile distance for initial check
		int tileDistance = Math.max(Math.abs(x - x2), Math.abs(y - y2));
		if (tileDistance <= req) {
			return true;
		}

		// Fallback to border checking for complex entity sizes
		Location[] a = GameConstants.getBorder(x, y, entity.getSize());
		Location[] b = GameConstants.getBorder(x2, y2, assaulting.getSize());

		for (Location i : a) {
			for (Location k : b) {
				if (Math.max(Math.abs(i.getX() - k.getX()), Math.abs(i.getY() - k.getY())) <= req) {
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
	entity.clearAnimationLock();
    entity.getFollowing().reset();
	  if (entity instanceof Stoner && ((Stoner) entity).isPetStoner()) {
		  Stoner owner = (Stoner) entity.getAttributes().get("PET_OWNER");
		  if (owner != null && owner.isActive()) {
			  entity.getFollowing().setFollow(owner);
		  }
	  }
	int direction = entity.getFaceDirection();
	entity.faceDirection(direction);
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

	  if (type == CombatTypes.MELEE) {
		  dist = 1; // Allow adjacent tiles AND same tile
	  }

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

	  if (!ignoreClipping && type == CombatTypes.MELEE) {
		  // For melee, be much more lenient about clipping
		  // Allow attack if ANY edge can reach target
		  for (Location i : GameConstants.getEdges(
			  entity.getLocation().getX(), entity.getLocation().getY(), entity.getSize())) {
			  if (StraightPathFinder.isInteractionPathClear(i, assaulting.getLocation())) {
				  return true; // Found one clear path, allow attack
			  }
		  }
		  return false;
	  }
    return true;
  }

	/**
	 * Checks if the entity is currently performing a combat action
	 */
	public boolean isPerformingCombatAction() {
		// You might track this with a flag that gets set during combat execution
		// and cleared when the action completes
		return isInCombat() && hasRecentCombatAction();
	}

	private boolean hasRecentCombatAction() {
		// Check if a combat action was performed recently (within the last few ticks)
		return (System.currentTimeMillis() - lastCombatActionTime) < 1800; // 3 ticks
	}

	/**
	 * Checks if the entity is currently in combat (wrapper for existing inCombat method)
	 */
	public boolean isInCombat() {
		return inCombat();
	}

	/**
	 * Sets the timestamp when a combat action was performed
	 */
	public void setLastCombatActionTime(long time) {
		this.lastCombatActionTime = time;
	}

	/**
	 * Gets the last combat action timestamp
	 */
	public long getLastCombatActionTime() {
		return lastCombatActionTime;
	}

  public enum CombatTypes {
    MELEE,
    SAGITTARIUS,
    MAGE,
    NONE
  }
}
