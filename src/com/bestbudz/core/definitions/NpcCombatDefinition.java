package com.bestbudz.core.definitions;

import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity.AssaultType;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Projectile;

public class NpcCombatDefinition {

  private short id;
  private CombatTypes combatType;
  private short respawnTime;
  private Animation block;
  private Animation death;
  private Profession[] professions;
  private int[] bonuses;
  private Melee[] melee;
  private Mage[] mage;
  private Sagittarius[] sagittarius;

  public Animation getBlock() {
    return block;
  }

  public int[] getBonuses() {
    return bonuses;
  }

  public CombatTypes getCombatType() {
    return combatType;
  }

  public Animation getDeath() {
    return death;
  }

  public int getId() {
    return id;
  }

  public Mage[] getMage() {
    return mage;
  }

  public Melee[] getMelee() {
    return melee;
  }

  public Sagittarius[] getSagittarius() {
    return sagittarius;
  }

  public short getRespawnTime() {
    return respawnTime;
  }

  public Profession[] getProfessions() {
    return professions;
  }

  public enum CombatTypes {
    MELEE,
    SAGITTARIUS,
    MAGE,
    MELEE_AND_SAGITTARIUS,
    MELEE_AND_MAGE,
    SAGITTARIUS_AND_MAGE,
    ALL
  }

  public class Mage {

    private Assault assault;
    private Animation animation;
    private Graphic start;
    private Projectile projectile;
    private Graphic end;
    private byte max;

    public Animation getAnimation() {
      return animation;
    }

    public Assault getAssault() {
      return assault;
    }

    public Graphic getEnd() {
      return end;
    }

    public int getMax() {
      return max;
    }

    public Projectile getProjectile() {
      return projectile;
    }

    public Graphic getStart() {
      return start;
    }
  }

  public class Melee {

    private Assault assault;
    private AssaultType assaultType;
    private Animation animation;
    private byte max;

    public Animation getAnimation() {
      return animation;
    }

    public Assault getAssault() {
      return assault;
    }

    public int getMax() {
      return max;
    }

    public AssaultType getAssaultType() {
      return assaultType;
    }
  }

  public class Sagittarius {

    private Assault assault;
    private Animation animation;
    private Graphic start;
    private Projectile projectile;
    private Graphic end;
    private byte max;

    public Animation getAnimation() {
      return animation;
    }

    public Assault getAssault() {
      return assault;
    }

    public Graphic getEnd() {
      return end;
    }

    public int getMax() {
      return max;
    }

    public Projectile getProjectile() {
      return projectile;
    }

    public Graphic getStart() {
      return start;
    }
  }

  public class Profession {

    private int id;
    private int grade;

    public int getId() {
      return id;
    }

    public int getGrade() {
      return grade;
    }

    @Override
    public String toString() {
      return "[" + id + ", " + grade + "]";
    }
  }
}
