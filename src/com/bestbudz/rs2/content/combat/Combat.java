package com.bestbudz.rs2.content.combat;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.combat.impl.DamageMap;
import com.bestbudz.rs2.content.combat.impl.Mage;
import com.bestbudz.rs2.content.combat.impl.Melee;
import com.bestbudz.rs2.content.combat.impl.Sagittarius;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
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
		// EMERGENCY: For pets, bypass some checks temporarily for testing
		if (entity instanceof Stoner && ((Stoner)entity).isPetStoner()) {
			System.out.println("EMERGENCY: Pet assault called - bypassing some checks");

			if (assaulting == null) {
				System.out.println("EMERGENCY: Pet has no target, aborting");
				return;
			}

			if (!assaulting.isActive() || assaulting.isDead()) {
				System.out.println("EMERGENCY: Pet target invalid, resetting");
				reset();
				return;
			}

			// Skip distance check for now - force attack
			System.out.println("EMERGENCY: Forcing pet combat execution");
			entity.face(assaulting);
			entity.onCombatProcess(assaulting);
			executePetNpcCombat((Stoner)entity, assaulting);
			entity.afterCombatProcess(assaulting);
			return;
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

		// Face the target when assault timer is 0 (actually attacking)
		if (assaultTimer == 0) {
			entity.face(assaulting);
		}

		entity.onCombatProcess(assaulting);

		// CRITICAL FIX: Handle pets as NPCs with special combat
		if (entity instanceof Stoner && ((Stoner)entity).isPetStoner()) {
			// Pets are NPCs but need special damage calculation
			executePetNpcCombat((Stoner)entity, assaulting);
		} else {
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
		}

		entity.afterCombatProcess(assaulting);
	}

	private void executePetNpcCombat(Stoner pet, Entity target) {
		System.out.println("DEBUG: Pet NPC " + pet.getUsername() + " executing combat against " +
			(target.isNpc() ? "NPC" : "Player"));

		// Get combat type for this attack
		CombatTypes combatType = determinePetCombatType(pet, target);

		// Get proper animation and timing based on combat type
		Animation attackAnimation = null;
		com.bestbudz.rs2.content.combat.impl.Assault assault = null;
		int maxHit = 0;

		switch (combatType) {
			case MELEE:
				attackAnimation = (Animation) pet.getAttributes().get("PET_MELEE_ANIMATION");
				assault = (com.bestbudz.rs2.content.combat.impl.Assault) pet.getAttributes().get("PET_MELEE_ASSAULT");
				Integer meleeMax = (Integer) pet.getAttributes().get("PET_MELEE_MAX_HIT");
				maxHit = meleeMax != null ? meleeMax : 50;
				System.out.println("DEBUG: Pet using MELEE attack");
				break;

			case MAGE:
				attackAnimation = (Animation) pet.getAttributes().get("PET_MAGE_ANIMATION");
				assault = (com.bestbudz.rs2.content.combat.impl.Assault) pet.getAttributes().get("PET_MAGE_ASSAULT");
				Integer mageMax = (Integer) pet.getAttributes().get("PET_MAGE_MAX_HIT");
				maxHit = mageMax != null ? mageMax : 50;

				// Handle mage graphics and projectiles
				Graphic startGraphic = (Graphic) pet.getAttributes().get("PET_MAGE_START");
				Projectile projectile = (Projectile) pet.getAttributes().get("PET_MAGE_PROJECTILE");
				Graphic endGraphic = (Graphic) pet.getAttributes().get("PET_MAGE_END");

				if (startGraphic != null) {
					pet.getUpdateFlags().sendGraphic(startGraphic);
				}
				if (projectile != null) {
					World.sendProjectile(projectile, pet, target);
				}
				// End graphic will be handled when hit lands

				System.out.println("DEBUG: Pet using MAGE attack with graphics");
				break;

			case SAGITTARIUS:
				attackAnimation = (Animation) pet.getAttributes().get("PET_SAGITTARIUS_ANIMATION");
				assault = (com.bestbudz.rs2.content.combat.impl.Assault) pet.getAttributes().get("PET_SAGITTARIUS_ASSAULT");
				Integer sagittariusMax = (Integer) pet.getAttributes().get("PET_SAGITTARIUS_MAX_HIT");
				maxHit = sagittariusMax != null ? sagittariusMax : 50;

				// Handle sagittarius graphics and projectiles
				Graphic startGraphic2 = (Graphic) pet.getAttributes().get("PET_SAGITTARIUS_START");
				Projectile projectile2 = (Projectile) pet.getAttributes().get("PET_SAGITTARIUS_PROJECTILE");

				if (startGraphic2 != null) {
					pet.getUpdateFlags().sendGraphic(startGraphic2);
				}
				if (projectile2 != null) {
					World.sendProjectile(projectile2, pet, target);
				}

				System.out.println("DEBUG: Pet using SAGITTARIUS attack with projectile");
				break;

			default:
				// Fallback to basic melee
				attackAnimation = new Animation(422, 0);
				maxHit = getPetMaxHit(pet);
				System.out.println("DEBUG: Pet using FALLBACK melee attack");
				break;
		}

		// Use the stored assault data for proper timing
		int hitDelay = 2;
		int attackSpeed = 4;

		if (assault != null) {
			hitDelay = assault.getHitDelay();
			attackSpeed = assault.getAssaultDelay();
		}

		// Calculate damage using pet's custom system
		int damage = com.bestbudz.core.util.Utility.randomNumber(maxHit + 1);

		// CRITICAL: Play the correct NPC animation
		if (attackAnimation != null) {
			pet.getUpdateFlags().sendAnimation(attackAnimation);
			System.out.println("DEBUG: Pet played animation: " + attackAnimation.getId());
		} else {
			System.out.println("ERROR: No animation found for pet combat type: " + combatType);
			pet.getUpdateFlags().sendAnimation(new Animation(422, 0)); // Fallback
		}

		// Set combat timer for next attack
		updateTimers(attackSpeed);

		// Create and schedule the hit
		Hit hit = new Hit(pet, damage, getHitTypeForCombatType(combatType));

		com.bestbudz.core.task.TaskQueue.queue(
			new com.bestbudz.core.task.impl.HitTask(hitDelay, false, hit, target)
		);

		System.out.println("DEBUG: Pet dealt " + damage + " damage (max: " + maxHit +
			") with " + hitDelay + " hit delay, " + attackSpeed + " attack speed, animation: " +
			(attackAnimation != null ? attackAnimation.getId() : "null"));
	}

	// Helper method to determine combat type for pets
	private static CombatTypes determinePetCombatType(Stoner pet, Entity target) {
		// Get the pet's NPC combat type configuration
		Object combatTypeObj = pet.getAttributes().get("PET_COMBAT_TYPE");

		if (combatTypeObj instanceof NpcCombatDefinition.CombatTypes) {
			NpcCombatDefinition.CombatTypes npcCombatType = (NpcCombatDefinition.CombatTypes) combatTypeObj;

			// Determine which attack to use based on NPC combat type and distance
			double distance = pet.getCombat().getDistanceFromTarget();

			switch (npcCombatType) {
				case MELEE:
					return CombatTypes.MELEE;

				case MAGE:
					return CombatTypes.MAGE;

				case SAGITTARIUS:
					return CombatTypes.SAGITTARIUS;

				case MELEE_AND_MAGE:
					// Use melee if close, mage if far or random
					if (distance <= 1 && com.bestbudz.core.util.Utility.randomNumber(2) == 0) {
						return CombatTypes.MELEE;
					} else {
						return CombatTypes.MAGE;
					}

				case MELEE_AND_SAGITTARIUS:
					// Use melee if close, sagittarius if far
					if (distance <= 1 && com.bestbudz.core.util.Utility.randomNumber(2) == 0) {
						return CombatTypes.MELEE;
					} else {
						return CombatTypes.SAGITTARIUS;
					}

				case SAGITTARIUS_AND_MAGE:
					// Random between sagittarius and mage
					return com.bestbudz.core.util.Utility.randomNumber(2) == 0 ?
						CombatTypes.SAGITTARIUS : CombatTypes.MAGE;

				case ALL:
					// Random between all three
					int roll = com.bestbudz.core.util.Utility.randomNumber(3);
					if (roll == 0) return CombatTypes.MELEE;
					else if (roll == 1) return CombatTypes.MAGE;
					else return CombatTypes.SAGITTARIUS;

				default:
					return CombatTypes.MELEE;
			}
		}

		// Default to melee if no combat type configured
		return CombatTypes.MELEE;
	}

	// Helper method to get hit type for combat type
	public static Hit.HitTypes getHitTypeForCombatType(CombatTypes combatType) {
		switch (combatType) {
			case MELEE:
				return Hit.HitTypes.MELEE;
			case MAGE:
				return Hit.HitTypes.MAGE;
			case SAGITTARIUS:
				return Hit.HitTypes.SAGITTARIUS;
			default:
				return Hit.HitTypes.MELEE;
		}
	}


	// 5. NEW: Get max hit for pets using their attributes
	private int getPetMaxHit(Stoner pet) {
		Integer petBaseDamage = (Integer) pet.getAttributes().get("PET_BASE_DAMAGE");
		if (petBaseDamage != null) {
			// Calculate pet damage based on their grades and base damage
			long attackGrade = pet.getGrades()[0]; // Attack
			long strengthGrade = pet.getGrades()[1]; // Strength

			// Pet damage formula: base damage + grade bonuses
			int maxHit = petBaseDamage + (int)(attackGrade / 50) + (int)(strengthGrade / 50);

			return Math.max(1, maxHit); // Ensure at least 1 damage
		} else {
			// Fallback if no base damage set
			return 50; // Default pet damage
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

	public double getDistanceFromTarget() {
		if (assaulting == null) {
			return -1.0D;
		}

		// FIXED: Use proper tile distance (Chebyshev distance) instead of Manhattan
		int deltaX = Math.abs(entity.getLocation().getX() - assaulting.getLocation().getX());
		int deltaY = Math.abs(entity.getLocation().getY() - assaulting.getLocation().getY());

		// Return the maximum of the two deltas (proper tile distance)
		return Math.max(deltaX, deltaY);
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
		// Debug for pets specifically
		if (entity instanceof Stoner && ((Stoner)entity).isPetStoner()) {
			Stoner pet = (Stoner)entity;
			System.out.println("=== PET COMBAT PROCESS DEBUG: " + pet.getUsername() + " ===");
			System.out.println("assaultTimer: " + assaultTimer);
			System.out.println("assaulting: " + (assaulting != null ? (assaulting.isNpc() ? "NPC" : "Player") : "null"));
			System.out.println("inCombat: " + inCombat());
			System.out.println("canAssault: " + entity.canAssault());
			if (assaulting != null) {
				System.out.println("target active: " + assaulting.isActive());
				System.out.println("target dead: " + assaulting.isDead());
				System.out.println("distance: " + getDistanceFromTarget());
				System.out.println("within distance: " + withinDistanceForAssault(combatType, false));
			}
			System.out.println("====================================================");
		}

		if (assaultTimer > 0) {
			assaultTimer -= 1;
		}

		if ((assaulting != null) && (assaultTimer == 0)) {
			// DEBUG: This should trigger for pets
			if (entity instanceof Stoner && ((Stoner)entity).isPetStoner()) {
				System.out.println("DEBUG: Pet " + ((Stoner)entity).getUsername() + " CALLING ASSAULT!");
			}
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

	  if (entity instanceof Stoner && ((Stoner)entity).isPetStoner()) {
		  // Pets should use simple distance checking like NPCs
		  // Don't apply the complex NPC-specific distance modifications
		  dist = 1; // Melee distance for pets
		  ignoreClipping = true; // Simplify pathfinding for pets

		  if (!isWithinDistance(dist)) {
			  System.out.println("DEBUG: Pet not within distance " + dist + " (actual: " + getDistanceFromTarget() + ")");
			  return false;
		  }

		  System.out.println("DEBUG: Pet IS within distance, allowing assault");
		  return true; // Skip complex clipping checks for pets
	  }

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
