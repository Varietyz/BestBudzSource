package com.bestbudz.core.definitions;

import java.util.Arrays;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.item.Item;

public class CombatSpellDefinition {

  private String name;
  private int id;
  private byte baseMaxHit;
  private double baseExperience;
  private Animation animation;
  private Graphic start;
  private Projectile projectile;
  private Graphic end;
  private int grade;
  private int[] weapon;
  private Item[] runes;

  public Animation getAnimation() {
    return animation;
  }

  public double getBaseExperience() {
    return baseExperience;
  }

  public int getBaseMaxHit() {
    return baseMaxHit;
  }

  public Graphic getEnd() {
    return end;
  }

  public int getId() {
    return id;
  }

  public int getGrade() {
    return grade;
  }

  public String getName() {
    return name;
  }

  public Projectile getProjectile() {
    return projectile;
  }

  public Item[] getRunes() {
    return runes;
  }

  public Graphic getStart() {
    return start;
  }

  public int[] getWeapons() {
    return weapon;
  }

  @Override
  public String toString() {
    return "CombatSpellDefinition [name="
        + name
        + ", id="
        + id
        + ", grade="
        + grade
        + ", runes="
        + Arrays.toString(runes)
        + "]";
  }
}
