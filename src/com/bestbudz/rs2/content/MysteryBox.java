package com.bestbudz.rs2.content;

import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.chance.Chance;
import com.bestbudz.core.util.chance.WeightedChance;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.Arrays;

public class MysteryBox {
  private static final Item MYSTERY_BOX = new Item(6199);

  public static Chance<Item> LOOTS =
      new Chance<Item>(
          Arrays.asList(
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1079, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1093, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1113, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1127, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1147, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1163, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1185, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1201, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1213, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(4131, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(3476, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(3477, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(7336, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(7342, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(7348, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(7354, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(7360, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(10286, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(10288, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(10290, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(10292, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(10294, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(560, 350)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(565, 350)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(561, 500)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(892, 500)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(868, 175)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(2491, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(2497, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(2503, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(12871, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(12869, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(12867, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(12865, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1725, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(1712, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(10602, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(10601, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(4091, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(4105, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(4103, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(4113, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(4111, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(4101, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(392, 55)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(11937, 25)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(158, 5)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(2441, 5)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(2437, 5)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(2443, 5)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(166, 5)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(3027, 5)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(3025, 5)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(13066, 1)),
              new WeightedChance<Item>(WeightedChance.COMMON, new Item(6688, 5)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1149, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1187, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1215, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1231, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1249, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1305, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1377, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1434, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1615, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1631, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1645, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4087, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4585, 1)),
              new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(7158, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4980, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4986, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4998, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4992, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4956, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4962, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4968, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4974, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4932, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4938, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4944, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4950, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4908, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4914, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4920, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4926, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4860, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4866, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4872, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4878, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4884, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4890, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4896, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(4902, 1)),
              new WeightedChance<Item>(WeightedChance.RARE, new Item(11840, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6585, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11283, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11335, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(3140, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12829, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6731, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6733, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6735, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6737, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12922, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12932, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6570, 1)),
              new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12795, 1))));

  public static void open(Stoner stoner) {
    Item reward = LOOTS.nextObject().get();
    String name = reward.getDefinition().getName();
    String formatted_name = Utility.getAOrAn(name) + " " + name;
    stoner.getBox().remove(MYSTERY_BOX);
    stoner.getBox().addOrCreateGroundItem(reward);
    stoner.send(
        new SendMessage("You have opened the and were rewarded with " + formatted_name + " ."));
    if (reward.getDefinition().getGeneralPrice() >= 1_500_000) {
      World.sendGlobalMessage(
          "@mbl@"
              + stoner.deterquarryIcon(stoner)
              + " "
              + stoner.getUsername()
              + " has recieved "
              + formatted_name
              + " from a Misery Box!");
    }
  }
}
