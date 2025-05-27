package com.bestbudz.rs2.content.profession.foodie;

import java.util.HashMap;
import java.util.Map;

public enum FoodieData {
  RAW_SHRIMP(317, 1, 99, 316, 995, 30.0D),
  SARDINE(327, 1, 99, 326, 995, 40.0D),
  ANCHOVIES(321, 1, 99, 320, 995, 30.0D),
  HERRING(345, 1, 99, 348, 995, 50.0D),
  MACKEREL(353, 1, 99, 356, 995, 60.0D),
  TROUT(335, 1, 99, 334, 995, 70.0D),
  COD(341, 1, 99, 340, 995, 75.0D),
  PIKE(349, 1, 99, 352, 995, 80.0D),
  SALMON(331, 1, 99, 330, 995, 90.0D),
  SLIMY_EEL(3379, 1, 99, 3382, 995, 95.0D),
  TUNA(359, 1, 99, 362, 995, 100.0D),
  KARAMBWAN(3142, 1, 99, 3145, 995, 190.0D),
  RAINBOW_FISH(10138, 1, 99, 10137, 995, 1110.0D),
  CAVE_EEL(5001, 1, 99, 4004, 995, 115.0D),
  LOBSTER(377, 1, 99, 380, 995, 120.0D),
  BASS(363, 1, 99, 365, 995, 130.0D),
  SWORDFISH(371, 1, 99, 374, 995, 140.0D),
  LAVA_EEL(2148, 1, 99, 2150, 995, 30.0D),
  MONKFISH(7944, 1, 99, 7947, 995, 150.0D),
  SHARK(383, 1, 99, 386, 995, 1210.0D),
  SEA_TURTLE(395, 1, 99, 398, 995, 1212.0D),
  CAVEFISH(15264, 1, 99, 15267, 995, 1214.0D),
  MANTA_RAY(389, 1, 99, 392, 995, 1216.0D),
  DARK_CRAB(11934, 1, 99, 11937, 995, 1225.0D);

  private static final Map<Integer, FoodieData> food = new HashMap<Integer, FoodieData>();
  int foodId;
  int gradeRequired;
  int noBurnGrade;
  int replacement;
  int burnt;
  double experience;

  FoodieData(int food, int grade, int noBurn, int replacement, int burnt, double exp) {
    foodId = food;
    gradeRequired = grade;
    noBurnGrade = noBurn;
    experience = exp;
    this.replacement = replacement;
    this.burnt = burnt;
  }

  public static final void declare() {
    for (FoodieData data : values()) food.put(Integer.valueOf(data.getFoodId()), data);
  }

  public static FoodieData forId(int id) {
    return food.get(Integer.valueOf(id));
  }

  public int getBurnt() {
    return burnt;
  }

  public double getExperience() {
    return experience;
  }

  public int getFoodId() {
    return foodId;
  }

  public int getGradeRequired() {
    return gradeRequired;
  }

  public int getNoBurnGrade() {
    return noBurnGrade;
  }

  public int getReplacement() {
    return replacement;
  }
}
