package com.bestbudz.rs2.entity.mob;

import com.bestbudz.core.util.Utility;
import java.util.HashMap;
import java.util.Map;

public enum RandomMobChatting {
  MAKEOVER_MAGE(
      new int[] {599, 2676},
      150,
      "I can change your appearance.",
      "Welcome to BestBudz you stoner!"),
  COMBAT_TUTOR(
      new int[] {705}, 120, "Welcome to BestBudz!", "Dont be lazy ya mug, swing that sword!");

  private static final Map<Integer, RandomMobChatting> mobs =
      new HashMap<Integer, RandomMobChatting>();
  private final int[] mobId;

  private final String[] messages;

  private final int random;

  RandomMobChatting(int[] mobId, int random, String... messages) {
    this.mobId = mobId;
    this.random = random;
    this.messages = messages;
  }

  public static final void declare() {
    for (RandomMobChatting mob : values()) {
      for (Integer k : mob.getMobId()) {
        mobs.put(k, mob);
      }
    }
  }

  public static RandomMobChatting getMob(int id) {
    return mobs.get(id);
  }

  public static void handleRandomMobChatting(Mob mob) {
    RandomMobChatting chat = getMob(mob.getId());
    if (chat == null) return;
    if (Utility.randomNumber(chat.getRandom()) == 1)
      mob.getUpdateFlags()
          .sendForceMessage(
              chat.getMessages()[Utility.randomNumber(chat.getMessages().length - 1)]);
  }

  public String[] getMessages() {
    return messages;
  }

  public int[] getMobId() {
    return mobId;
  }

  public int getRandom() {
    return random;
  }
}
