package com.bestbudz.rs2.entity;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.RegenerateProfessionTask;
import com.bestbudz.rs2.content.combat.Combat;
import static com.bestbudz.rs2.content.combat.Combat.CombatTypes.MELEE;
import com.bestbudz.rs2.content.combat.CombatInterface;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.movement.MovementHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.LinkedList;

public abstract class Entity implements CombatInterface {

  public static final int STONER_INDEX_MOD = 32768;
  private final long[] maxGrades = new long[Professions.PROFESSION_COUNT];
  private final long[] grades = new long[Professions.PROFESSION_COUNT];
  private final short[] bonuses = new short[13];
  private final LinkedList<Task> tasks = new LinkedList<Task>();
  private final Location location = new Location(0, 0, 0);
  private final UpdateFlags updateFlags = new UpdateFlags();
  private final Combat combat = new Combat(this);
  private final Attributes attributes = new Attributes();
  private short index = -1;
  private byte size = 1;
  private byte lastDamageDealt = 0;
  private Entity lastAssaulted;
  private int consecutiveAssaults;
  private double sagittariusWeaknessMod;
  private double mageWeaknessMod;
  private double meleeWeaknessMod;
  private boolean npc = true;
  private boolean active = false;
  private boolean dead = false;
  private boolean takeDamage = true;
  private boolean retaliate = true;
  private boolean lastHitSuccess = false;
  private boolean poisoned = false;
  private int poisonDamage = 0;
  private int teleblockTime;
  private long freeze;
  private long stun;
  private long iceImmunity;
  private long poisonImmunity;
  private long hitImmunity = 0L;
  private long fireImmunity = 0;
  private AssaultType assaultType = AssaultType.CRUSH;
	private int faceDirection = -1;
	private long animationLockEnd = 0;
	private Animation currentAnimation = null;

  public AssaultType getAssaultType() {
    return assaultType;
  }

  public void setAssaultType(AssaultType assaultType) {
    this.assaultType = assaultType;
  }

  public void addBonus(int id, int add) {
    int tmp5_4 = id;
    short[] tmp5_1 = bonuses;
    tmp5_1[tmp5_4] = ((short) (tmp5_1[tmp5_4] + add));
  }

  public boolean canTakeDamage() {
    return takeDamage;
  }

  public void curePoison(int immunity) {
    poisoned = false;
    poisonDamage = 0;
    if (immunity > 0) poisonImmunity = (World.getCycles() + immunity);
  }

  public void doConsecutiveAssaults(Entity assaulting) {
    if (lastAssaulted == null) {
      consecutiveAssaults = 1;
      lastAssaulted = assaulting;
      return;
    }

    if (lastAssaulted.equals(assaulting)) {
      consecutiveAssaults += 1;
    } else {
      consecutiveAssaults = 0;
      lastAssaulted = assaulting;
    }
  }

  public void doFireImmunity(int delayInSeconds) {
    fireImmunity = System.currentTimeMillis() + (delayInSeconds * 1000L);
  }

  @Override
  public boolean equals(Object other) {
    if ((other instanceof Entity)) {
      Entity e = (Entity) other;
      return (e.getIndex() == index) && (e.isNpc() == npc);
    }

    return false;
  }

	public void face(Entity entity) {
		if (entity == null) {
			return;
		}

		if (getMovementHandler().moving()) {
			return;
		}

		boolean isActuallyAttacking = (getCombat().getAssaulting() != null &&
			getCombat().getAssaultTimer() == 0 &&
			getCombat().withinDistanceForAssault(getCombat().getCombatType(), false));

		if (!isActuallyAttacking) {
			return;
		}

		if (!entity.isNpc()) {
			updateFlags.faceEntity(entity.getIndex() + 32768);
		} else {
			updateFlags.faceEntity(entity.getIndex());
		}
	}

  public void freeze(double time, int immunity) {
    if ((isFrozen()) || (isImmuneToIce())) {
      return;
    }

    freeze = (long) (System.currentTimeMillis() + (time * 1000));
    iceImmunity = (freeze + 5000);
  }

  public void stun(double time) {
    if (isStunned()) {
      return;
    }
    stun = (long) (System.currentTimeMillis() + (time * 1000));
  }

  public boolean isStunned() {
    return stun > System.currentTimeMillis();
  }

  public Attributes getAttributes() {
    return attributes;
  }

  public short[] getBonuses() {
    return bonuses;
  }

  public void setBonuses(int[] bonuses) {
    for (int i = 0; i < bonuses.length; i++) this.bonuses[i] = ((short) bonuses[i]);
  }

  public Combat getCombat() {
    return combat;
  }

  public abstract Following getFollowing();

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = ((short) index);
  }

  public int getLastDamageDealt() {
    return lastDamageDealt;
  }

  public void setLastDamageDealt(long lastDamageDealt) {
    this.lastDamageDealt = ((byte) lastDamageDealt);
  }

  public long[] getGrades() {
    return grades;
  }

  public void setGrades(long[] grades) {
    System.arraycopy(grades, 0, this.grades, 0, grades.length);
  }

  public Location getLocation() {
    return location;
  }

  public double getMageWeaknessMod() {
    return mageWeaknessMod;
  }

  public void setMageWeaknessMod(double mageWeaknessMod) {
    this.mageWeaknessMod = mageWeaknessMod;
  }

  public long[] getMaxGrades() {
    return maxGrades;
  }

  public void setMaxGrades(long[] maxGrades) {
    System.arraycopy(maxGrades, 0, this.maxGrades, 0, maxGrades.length);
  }

  public double getMeleeWeaknessMod() {
    return meleeWeaknessMod;
  }

  public void setMeleeWeaknessMod(double meleeWeaknessMod) {
    this.meleeWeaknessMod = meleeWeaknessMod;
  }

  public Mob getMob() {
    return !npc ? null : World.getNpcs()[index];
  }

  public abstract MovementHandler getMovementHandler();

  public Stoner getStoner() {
    return npc ? null : World.getStoners()[index];
  }

  public int getPoisonDamage() {
    return poisonDamage;
  }

  public void setPoisonDamage(int poisonDamage) {
    this.poisonDamage = poisonDamage;
  }

  public double getSagittariusWeaknessMod() {
    return sagittariusWeaknessMod;
  }

  public void setSagittariusWeaknessMod(double sagittariusWeaknessMod) {
    this.sagittariusWeaknessMod = sagittariusWeaknessMod;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = ((byte) size);
  }

  public LinkedList<Task> getTasks() {
    return tasks;
  }

  public int getTeleblockTime() {
    return teleblockTime;
  }

  public void setTeleblockTime(int teleblockTime) {
    this.teleblockTime = teleblockTime;
  }

  public UpdateFlags getUpdateFlags() {
    return updateFlags;
  }

  public int getWildernessGrade() {
    int y = location.getY();
    int grade = -1;
    int modY = y > 6400 ? y - 6400 : y;
    grade = (((modY - 3520) / 8) + 1);
    return grade;
  }
	public int getFaceDirection() { return faceDirection; }
	public void setFaceDirection(int faceDirection) { this.faceDirection = faceDirection; }

	public void faceDirection(int direction) {
		this.faceDirection = direction;
		this.getUpdateFlags().faceDirection(direction);
	}

  public int getX() {
    return location.getX();
  }

  public int getY() {
    return location.getY();
  }

  public int getZ() {
    return location.getZ();
  }

  public boolean hasAssaultedConsecutively(Entity check, int req) {
    return (lastAssaulted != null) && (lastAssaulted.equals(check)) && (consecutiveAssaults >= req);
  }

  public boolean hasFireImmunity() {
    return fireImmunity > System.currentTimeMillis()
        || (getAttributes().get("fire_resist") != null
            && (Boolean) getAttributes().get("fire_resist"))
        || (getAttributes().get("super_fire_resist") != null
            && (Boolean) getAttributes().get("super_fire_resist"));
  }

  public boolean hasSuperFireImmunity() {
    return (getAttributes().get("super_fire_resist") != null
        && (Boolean) getAttributes().get("super_fire_resist"));
  }

  public boolean inArea(Location bottomLeft, Location topRight, boolean heightSupport) {
    if ((heightSupport) && (location.getZ() != bottomLeft.getZ())) return false;
    return (location.getX() >= bottomLeft.getX())
        && (location.getX() <= topRight.getX())
        && (location.getY() >= bottomLeft.getY())
        && (location.getY() <= topRight.getY());
  }

  public boolean inDuelArena() {
    return (location.getX() >= 3325)
        && (location.getX() <= 3396)
        && (location.getY() >= 3199)
        && (location.getY() <= 3289);
  }

  public boolean inGodwars() {
    return inArea(new Location(2816, 5243, 2), new Location(2960, 5400, 2), false);
  }

  public boolean inMultiArea() {
	  return true;
    }

  public boolean inClanWarsFFA() {
    int x = location.getX();
    int y = location.getY();
    return (x > 3275 && x < 3379 && y > 4759 && y < 4852);
  }

  public boolean inWGLobby() {
    int x = location.getX();
    int y = location.getY();
    return (x > 1859 && x < 1868 && y > 5316 && y < 5323);
  }

  public boolean inWGGame() {
    int x = location.getX();
    int y = location.getY();
    return (x > 2136 && x < 2166 && y > 5089 && y < 5108);
  }

  public boolean nearElemental() {
    int x = location.getX();
    int y = location.getY();
    return (x > 3253 && x < 3299 && y > 3911 && y < 3933);
  }

  public boolean inJailed() {
    int x = location.getX();
    int y = location.getY();
    return (x > 2770 && x < 2777 && y > 2792 && y < 2796);
  }

  public boolean inKraken() {
    int x = location.getX();
    int y = location.getY();
    return (x > 3680 && x < 3713 && y > 5789 && y < 5825);
  }

  public boolean inZulrah() {
    int x = location.getX();
    int y = location.getY();
    return (x > 2254 && x < 2283 && y > 3059 && y < 3083);
  }

  public boolean inCorp() {
    int x = location.getX();
    int y = location.getY();
    int z = location.getZ();
    return (x > 2971 && x < 3000 && y > 4361 && y < 4402 && z == 2);
  }

  public boolean inMemberZone() {
    int x = location.getX();
    int y = location.getY();
    return (x > 2800 && x < 2876 && y > 3325 && y < 3390);
  }

  public boolean inCyclops() {
    int x = location.getX();
    int y = location.getY();
    int z = location.getZ();
    return (x >= 2847 && x <= 2876 && y >= 3534 && y <= 3556 && z == 2
        || x >= 2838 && x <= 2847 && y >= 3543 && y <= 3556 && z == 2);
  }

  public boolean inWilderness() {
    int x = location.getX();
    int y = location.getY();
    return (x > 2941 && x < 3392 && y > 3521 && y < 3966)
        || (x > 2941 && x < 3392 && y > 9918 && y < 10366)
        || (x > 2583 && x < 2729 && y > 3255 && y < 3343);
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isDead() {
    return dead;
  }

  public void setDead(boolean dead) {
    this.dead = dead;
	  if (dead) {
		  clearAnimationLock();
	  }
  }

  public boolean isFrozen() {
    return freeze > System.currentTimeMillis();
  }

  public int setFreeze(int value) {
    return (int) (freeze = value);
  }

  public boolean isImmuneToHit() {
    return System.currentTimeMillis() < hitImmunity;
  }

  public boolean isImmuneToIce() {
    return iceImmunity > System.currentTimeMillis();
  }

  public boolean isNpc() {
    return npc;
  }

  public void setNpc(boolean npc) {
    this.npc = npc;
  }

  public boolean isPoisoned() {
    return poisoned;
  }

  public boolean isRetaliate() {
    return retaliate;
  }

  public void setRetaliate(boolean retaliate) {
    this.retaliate = retaliate;
  }

  public boolean isTeleblocked() {
    return teleblockTime > 0;
  }

  public void poison(int start) {
    if ((poisoned) || (World.getCycles() < poisonImmunity)) {
      return;
    }

    poisoned = true;
    poisonDamage = start;

    TaskQueue.queue(
        new Task(this, 30) {
          int count = 0;

          @Override
          public void execute() {
            if (!poisoned || poisonDamage <= 0 || getStoner() == null) {
              stop();
              return;
            }

            if (getStoner().isDead() || getStoner().getMage().isTeleporting()) {
              return;
            }

            hit(new Hit(poisonDamage, Hit.HitTypes.POISON));

            if (++count == 4) {
              poisonDamage -= 1;
              count = 0;
              if (poisonDamage == 0) stop();
            }
          }

          @Override
          public void onStop() {}
        });
  }

  public abstract void process() throws Exception;

  public abstract void reset();

  public void resetCombatStats() {
    Stoner p = null;
    if (!npc) {
      p = World.getStoners()[index];
    }

    for (int i = 0; i <= 7; i++) {
      grades[i] = maxGrades[i];
      if (!npc) p.getProfession().update(i);
    }
  }

  public void resetGrades() {
    System.arraycopy(maxGrades, 0, grades, 0, 21);

    if (!npc) {
      Stoner p = World.getStoners()[index];

      if (p != null) {
        p.getProfession().update();
      }
    }
  }

	public void setAnimationLock(int ticks) {
		this.animationLockEnd = System.currentTimeMillis() + (ticks * 600);
	}

	public boolean hasAnimationLock() {
		return System.currentTimeMillis() < animationLockEnd;
	}

	public void clearAnimationLock() {
		this.animationLockEnd = 0;
	}

	public Animation getAnimation() {
		return currentAnimation;
	}

	public void setAnimation(Animation animation) {
		this.currentAnimation = animation;
	}

	public void setAnimationWithCombatLock(Animation animation, Combat.CombatTypes combatType) {
		this.currentAnimation = animation;
		if (animation != null) {

			int lockDuration = getCombatAnimationDuration(combatType);
			setAnimationLock(lockDuration);
		}
	}

	private int getCombatAnimationDuration(Combat.CombatTypes combatType) {
		switch (combatType) {
			case MELEE:
				return getCombat().getMelee().getAssault().getAssaultDelay();
			case MAGE:
				return getCombat().getMage().getAssault().getAssaultDelay();
			case SAGITTARIUS:
				return getCombat().getSagittarius().getAssault() != null ?
					getCombat().getSagittarius().getAssault().getAssaultDelay() : 2;
			default:
				return 2;
		}
	}

  public abstract void retaliate(Entity assaulted);

  public void setHitImmunityDelay(int delay) {
    hitImmunity = (System.currentTimeMillis() + delay * 1000L);
  }

  public void setLastHitSuccess(boolean lastHitSuccess) {
    this.lastHitSuccess = lastHitSuccess;
  }

  public void setTakeDamage(boolean takeDamage) {
    this.takeDamage = takeDamage;
  }

  public void startRegeneration() {
    TaskQueue.queue(new RegenerateProfessionTask(this, 75));
  }

  public void teleblock(int time) {
    if (isTeleblocked()) {
      return;
    }

    teleblockTime = time;

    tickTeleblock();
  }

  public void tickTeleblock() {
    TaskQueue.queue(
        new Task(this, 1) {

          @Override
          public void execute() {
            if (--teleblockTime <= 0) {
              teleblockTime = 0;
              stop();
            }
          }

          @Override
          public void onStop() {}
        });
  }

  public void unfreeze() {
    freeze = 0L;
  }

  public boolean wasLastHitSuccess() {
    return lastHitSuccess;
  }

  public enum AssaultType {
    STAB,
    SLASH,
    CRUSH
  }
}
