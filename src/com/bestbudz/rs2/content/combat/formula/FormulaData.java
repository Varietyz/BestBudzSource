package com.bestbudz.rs2.content.combat.formula;

import java.util.Random;

public class FormulaData {

  public static final Random r = new Random(System.currentTimeMillis());

  public static boolean isAccurateHit(double chance) {
    if (chance >= 0.90) return true; // guaranteed hit
    if (chance <= 0.10) return false; // guaranteed miss
    return r.nextDouble() <= chance;
  }

  public static boolean isDoubleHit(double chance, long chainStage) {
    double multiplier = 0.10 + (chainStage * 0.05);
    if (chance >= 0.85) {
      return r.nextDouble() <= multiplier;
    }
    return false;
  }

  public static double getChance(double assault, double aegis) {
    if (assault <= 0) return 0.01;
    if (aegis <= 0) return 0.99;

    double ratio = assault / (assault + aegis); // bias toward attacker
    double scaled = Math.pow(ratio, 0.85); // slight curve to favor chaining
    return Math.max(0.05, Math.min(0.99, scaled));
  }
}
