package com.bestbudz.rs2.content.achievements;

import com.bestbudz.rs2.content.achievements.AchievementHandler.AchievementDifficulty;

public enum AchievementList {
  CAME_WITH_STYLE("Join BestBudz with style", 1, AchievementDifficulty.EASY),
  KILL_75_COWS("Kill 75 cows", 75, AchievementDifficulty.EASY),
  WIN_20_DUELS("Win 20 duels", 20, AchievementDifficulty.EASY),
  STRING_100_AMULETS("String 100 amulets", 100, AchievementDifficulty.EASY),
CHOP_1000_WOOD("Chop 1,000 wood", 1000, AchievementDifficulty.EASY),
  COOK_250_FOODS("Cook 250 foods", 250, AchievementDifficulty.EASY),
  DIE_1_TIME("Die 1 time", 1, AchievementDifficulty.EASY),
  HARVEST_100_BUDS("Harvest 100 buds", 100, AchievementDifficulty.EASY),
  SHEAR_10_SHEEPS("Shear 10 Sheeps", 10, AchievementDifficulty.EASY),
  DO_A_PROFESSIONCAPE_EMOTE("Do a professioncape emote", 1, AchievementDifficulty.EASY),
  SMOKE_100G_WEED("Smoke 100G of weed", 100, AchievementDifficulty.EASY),

  HARVEST_1000_BUDS("Harvest 1,000 buds", 1000, AchievementDifficulty.MEDIUM),
  KILL_ROCK_CRABS("Kill 100 Rock crabs", 100, AchievementDifficulty.MEDIUM),
  COMPLETE_10_MERCENARY_TASKS("Complete 10 mercenary tasks", 10, AchievementDifficulty.MEDIUM),
  OBTAIN_1_BOSS_PET("Obtain 1 boss pet", 1, AchievementDifficulty.MEDIUM),
  HIGH_ALCH_250_ITEMS("High alch 250 items", 250, AchievementDifficulty.MEDIUM),
  OBTAIN_10_FIRECAPES("Obtain 10 firecapes", 10, AchievementDifficulty.MEDIUM),
  WIN_A_DUEL_WORTH_OVER_10M("Win a duel worth over 10m", 1, AchievementDifficulty.MEDIUM),
  CHOP_4000_WOOD("Chop 4,000 wood", 4000, AchievementDifficulty.MEDIUM),
  BURN_1500_WOOD("Burn 1,500 wood", 1500, AchievementDifficulty.MEDIUM),
  RESET_5_STATISTICS("Reset 5 professions", 5, AchievementDifficulty.MEDIUM),
  DIE_10_TIME("Die 10 times", 10, AchievementDifficulty.MEDIUM),
  CUT_2500_GEMS("Cut 2,500 gems", 2500, AchievementDifficulty.MEDIUM),
  KILL_25_KRAKENS("Kill 25 Krakens", 25, AchievementDifficulty.MEDIUM),
  EARN_100_MAGE_ARENA_POINTS("Earn 100 Mage Arena Points", 100, AchievementDifficulty.MEDIUM),
  SHEAR_150_SHEEPS("Shear 150 Sheeps", 150, AchievementDifficulty.MEDIUM),
  BURY_150_BONES("Bury or use on altar 150 bones", 150, AchievementDifficulty.MEDIUM),
  SMOKE_1000G_WEED("Smoke 1KG of weed", 1000, AchievementDifficulty.MEDIUM),

  HARVEST_10000_BUDS("Harvest 10,000 buds", 10000, AchievementDifficulty.HARD),
  ADVANCE_105_TIMES("Advance 105 times", 105, AchievementDifficulty.HARD),
  KILL_KING_BLACK_DRAGON("Kill King Black dragon 50 times", 50, AchievementDifficulty.HARD),
  KILL_250_SKELETAL_WYVERNS("Kill 250 Skeletal wyverns", 250, AchievementDifficulty.HARD),
  COMPLETE_100_MERCENARY_TASKS("Complete 100 mercenary tasks", 100, AchievementDifficulty.HARD),
  OBTAIN_10_BOSS_PET("Obtain 10 boss pets", 10, AchievementDifficulty.HARD),
  OBTAIN_50_FIRECAPES("Obtain 50 firecapes", 50, AchievementDifficulty.HARD),
  BURN_12500_WOOD("Burn 12,500 wood", 12500, AchievementDifficulty.HARD),
  COOK_10000_FOODS("Cook 10,000 foods", 10000, AchievementDifficulty.HARD),
  OPEN_70_CRYSTAL_CHESTS("Open 70 Crystal chests", 70, AchievementDifficulty.HARD),
  DIE_50_TIME("Die 50 times", 50, AchievementDifficulty.HARD),
  QUARRY_12500_ROCKS("Quarry 12,500 ore", 12500, AchievementDifficulty.HARD),
  EARN_500_MAGE_ARENA_POINTS("Earn 500 Mage Arena points", 500, AchievementDifficulty.HARD),
  KILL_100_ZULRAHS("Kill 100 Zulrahs", 100, AchievementDifficulty.HARD),
  KILL_150_KRAKENS("Kill 150 Krakens", 150, AchievementDifficulty.HARD),
  KILL_100_CALLISTO("Kill 100 Callisto", 100, AchievementDifficulty.HARD),
  OBTAIN_10_RARE_DROPS("Obtain 10 rare drops", 10, AchievementDifficulty.HARD),
  BURY_1000_BONES("Bury or use on altar 1,000 bones", 1000, AchievementDifficulty.HARD),
  SMOKE_10000G_WEED("Smoke 10KG of weed", 10000, AchievementDifficulty.HARD);

  private final String name;
  private final int completeAmount;
  private final AchievementDifficulty difficulty;

  AchievementList(String name, int completeAmount, AchievementDifficulty difficulty) {
    this.name = name;
    this.completeAmount = completeAmount;
    this.difficulty = difficulty;
  }

  public String getName() {
    return name;
  }

  public int getCompleteAmount() {
    return completeAmount;
  }

  public int getReward() {
    switch (difficulty) {
      case MEDIUM:
        return 2;
      case HARD:
        return 3;
      case EASY:
      default:
        return 1;
    }
  }

  public AchievementDifficulty getDifficulty() {
    return difficulty;
  }
}
