package com.bestbudz.rs2.content.wilderness;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum BountyEmblems {
  TIER_1(12746, 50_000, 0),
  TIER_2(12748, 100_000, 1),
  TIER_3(12749, 200_000, 2),
  TIER_4(12750, 400_000, 3),
  TIER_5(12751, 750_000, 4),
  TIER_6(12752, 1_250_000, 5),
  TIER_7(12753, 1_750_000, 6),
  TIER_8(12754, 2_500_000, 7),
  TIER_9(12755, 3_500_000, 8),
  TIER_10(12756, 5_000_000, 9);

  public static final Set<BountyEmblems> EMBLEMS =
      Collections.unmodifiableSet(EnumSet.allOf(BountyEmblems.class));
  static final Comparator<BountyEmblems> BEST_EMBLEM_COMPARATOR =
      (first, second) -> Integer.compare(first.itemId, second.itemId);
  private final int itemId;
  private final int bounties;
  private final int index;

  BountyEmblems(int itemId, int bounties, int index) {
    this.itemId = itemId;
    this.bounties = bounties;
    this.index = index;
  }

  public static Optional<BountyEmblems> valueOf(int index) {
    return EMBLEMS.stream().filter(emblem -> emblem.index == index).findFirst();
  }

  public static Optional<BountyEmblems> getBest(Stoner stoner, boolean exclude) {
    List<BountyEmblems> emblems =
        EMBLEMS.stream().filter(exclude(stoner, exclude)).collect(Collectors.toList());

    if (emblems.isEmpty()) {
      return Optional.empty();
    }

    return emblems.stream().max(BEST_EMBLEM_COMPARATOR);
  }

  private static Predicate<BountyEmblems> exclude(Stoner stoner, boolean exclude) {
    return emblem ->
        stoner.getBox().hasItemId(new Item(emblem.getItemId()))
            && (!exclude || exclude && !emblem.equals(TIER_10));
  }

  public int getItemId() {
    return itemId;
  }

  public int getBounties() {
    return bounties;
  }

  public int getIndex() {
    return index;
  }

  public BountyEmblems getNextOrLast() {
    return valueOf(index + 1).orElse(TIER_10);
  }

  public BountyEmblems getPreviousOrFirst() {
    return valueOf(index - 1).orElse(TIER_1);
  }
}
