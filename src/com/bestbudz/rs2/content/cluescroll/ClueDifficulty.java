package com.bestbudz.rs2.content.cluescroll;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.cluescroll.ClueScrollManager.ClueReward;
import com.bestbudz.rs2.entity.item.Item;

public enum ClueDifficulty {
  EASY(
      () -> {
        Item reward = null;
        int amount;

        if (Math.random() > (1 - (1 / 5.0))) {
          reward = crossTrialItems();
          amount = reward.getAmount();
        } else {
          reward = ClueScrollManager.EASY.nextObject().get();
          amount = reward.getAmount();

          if (amount > 1) {
            amount = Utility.randomNumber(amount) + 1;
          }
        }

        return new Item(reward.getId(), amount);
      }),

  MEDIUM(
      () -> {
        Item reward = null;
        int amount;

        if (Math.random() > (1 - (1 / 5.0))) {
          reward = EASY.getRewards().getReward();
          amount = reward.getAmount();
        } else {
          reward = ClueScrollManager.MEDIUM.nextObject().get();
          amount = reward.getAmount();

          if (amount > 1) {
            amount = Utility.randomNumber(amount) + 1;
          }
        }

        return new Item(reward.getId(), amount);
      }),

  HARD(
      () -> {
        Item reward = null;
        int amount;

        if (Math.random() > (1 - (1 / 5.0))) {
          reward = MEDIUM.getRewards().getReward();
          amount = reward.getAmount();
        } else {
          reward = ClueScrollManager.HARD.nextObject().get();
          amount = reward.getAmount();

          if (amount > 1) {
            amount = Utility.randomNumber(amount) + 1;
          }
        }

        return new Item(reward.getId(), amount);
      });

  private final ClueReward reward;

  ClueDifficulty(ClueReward reward) {
    this.reward = reward;
  }

  private static Item crossTrialItems() {
    return new ClueReward() {

      @Override
      public Item getReward() {
        Item reward = ClueScrollManager.CROSS_TRAILS.nextObject().get();
        int amount = reward.getAmount();

        if (amount > 1) {
          amount = Utility.randomNumber(amount) + 1;
        }
        return new Item(reward.getId(), amount);
      }
    }.getReward();
  }

  public ClueReward getRewards() {
    return reward;
  }
}
