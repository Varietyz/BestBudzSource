package com.bestbudz.rs2.content.combat;

import com.bestbudz.rs2.entity.Entity;

public class Hit {

  private final Entity assaulter;
  private final boolean success;
  private long damage;
  private HitTypes type;

  public Hit(Entity assaulter, long damage, HitTypes type) {
    this.assaulter = assaulter;
    this.damage = damage;
    this.type = type;
    success = (damage > 0);
  }

  public Hit(long damage) {
    this(null, damage, HitTypes.NONE);
  }

  public Hit(long damage, HitTypes type) {
    this(null, damage, type);
  }

  public Entity getAssaulter() {
    return assaulter;
  }

  public int getCombatHitType() {
    switch (type) {
      case MELEE:
        return 0;
      case SAGITTARIUS:
        return 1;
      case MAGE:
        return 2;
      case DEFLECT:
        return 3;
      case CANNON:
        return 4;
      case MONEY:
        return 5;
      case NONE:
      case POISON:
      case DISEASE:
        return 6;
    }

    return 0;
  }

  public int getHitType() {
    switch (type) {
      case NONE:
      case MELEE:
      case SAGITTARIUS:
      case MAGE:
      case DEFLECT:
      case CANNON:
      case MONEY:
        if (damage > 0) {
          return 1;
        }
        return 0;
      case POISON:
        return 2;
      case DISEASE:
        return 3;

      default:
        break;
    }

    return 0;
  }

  public long getDamage() {
    return damage;
  }

  public void setDamage(long damage) {
    this.damage = damage;
  }

  public HitTypes getType() {
    return type;
  }

  public void setType(HitTypes type) {
    this.type = type;
  }

  public boolean isSuccess() {
    return success;
  }

  public enum HitTypes {
    NONE,
    MELEE,
    SAGITTARIUS,
    MAGE,
    POISON,
    DISEASE,
    DEFLECT,
    CANNON,
    MONEY
  }
}
