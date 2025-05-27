package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendItemOnInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class HideTanning {
  public static final int[][] TANNING_HIDE = {
    {1739, 1741, 2},
    {1739, 1743, 3},
    {6287, 6289, 45},
    {1753, 1745, 45},
    {1751, 2505, 45},
    {1749, 2507, 45},
    {1747, 2509, 45}
  };

  public static final boolean clickButton(Stoner stoner, int id) {
    switch (id) {
      case 57225:
        tan(stoner, 1, 0);
        return true;
      case 57226:
        tan(stoner, 1, 1);
        return true;
      case 57227:
        tan(stoner, 1, 2);
        return true;
      case 57228:
        tan(stoner, 1, 3);
        return true;
      case 57229:
        tan(stoner, 1, 4);
        return true;
      case 57230:
        tan(stoner, 1, 5);
        return true;
      case 57231:
        tan(stoner, 1, 6);
        return true;
      case 57217:
        tan(stoner, 5, 0);
        return true;
      case 57218:
        tan(stoner, 5, 1);
        return true;
      case 57219:
        tan(stoner, 5, 2);
        return true;
      case 57220:
        tan(stoner, 5, 3);
        return true;
      case 57221:
        tan(stoner, 5, 4);
        return true;
      case 57222:
        tan(stoner, 5, 5);
        return true;
      case 57223:
        tan(stoner, 5, 6);
        return true;
      case 57201:
      case 57209:
        tan(stoner, stoner.getBox().getItemAmount(TANNING_HIDE[0][0]), 0);
        return true;
      case 57202:
      case 57210:
        tan(stoner, stoner.getBox().getItemAmount(TANNING_HIDE[1][0]), 1);
        return true;
      case 57203:
      case 57211:
        tan(stoner, stoner.getBox().getItemAmount(TANNING_HIDE[2][0]), 2);
        return true;
      case 57204:
      case 57212:
        tan(stoner, stoner.getBox().getItemAmount(TANNING_HIDE[3][0]), 3);
        return true;
      case 57205:
      case 57213:
        tan(stoner, stoner.getBox().getItemAmount(TANNING_HIDE[4][0]), 4);
        return true;
      case 57206:
      case 57214:
        tan(stoner, stoner.getBox().getItemAmount(TANNING_HIDE[5][0]), 5);
        return true;
      case 57207:
      case 57215:
        tan(stoner, stoner.getBox().getItemAmount(TANNING_HIDE[6][0]), 6);
        return true;
      case 57208:
      case 57216:
      case 57224:
    }
    return false;
  }

  public static final void sendTanningInterface(Stoner stoner) {
    for (int i = 0; i < TANNING_HIDE.length; i++) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendItemOnInterface(14769 + i, 250, TANNING_HIDE[i][1]));
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendString(Item.getDefinition(TANNING_HIDE[i][1]).getName(), 14777 + i));
      stoner
          .getClient()
          .queueOutgoingPacket(new SendString(TANNING_HIDE[i][2] + " BestBucks", 14785 + i));
    }

    stoner.getClient().queueOutgoingPacket(new SendString("", 14784));
    stoner.getClient().queueOutgoingPacket(new SendString("", 14792));
    stoner.getClient().queueOutgoingPacket(new SendInterface(14670));
  }

  public static final void tan(Stoner stoner, int amount, int index) {
    int price = TANNING_HIDE[index][2];
    int bestbucks = stoner.getBox().getItemAmount(995);

    if (bestbucks < price) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("This is a bug, report it."));
      return;
    }

    int toTan = TANNING_HIDE[index][0];

    int invAm = stoner.getBox().getItemAmount(toTan);

    if (invAm == 0) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have any of this hide."));
      return;
    }

    if (invAm < amount) {
      amount = invAm;
    }

    int total = amount * price;

    if (total > bestbucks) {
      amount = bestbucks / price;
      total = amount * price;
    }

    stoner.getBox().remove(toTan, amount, false);
    stoner.getBox().add(TANNING_HIDE[index][1], amount);

    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendMessage("You tan " + amount + " hide(s) for " + total + " BestBucks."));
  }
}
