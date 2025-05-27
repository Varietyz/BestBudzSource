package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.foodie.FoodieTask;
import com.bestbudz.rs2.content.profession.necromance.BoneBurying;
import com.bestbudz.rs2.content.profession.thchempistry.THChempistryFinishedPotionTask;
import com.bestbudz.rs2.content.profession.thchempistry.THChempistryUnfinishedPotionTask;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterXInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProfessionGoal;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

@SuppressWarnings("all")
public class ChatInterfacePacket extends IncomingPacket {
  @Override
  public int getMaxDuplicates() {
    return 10;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    switch (opcode) {
      case 40:
        handleDialogue(stoner);
        break;
      case 135:
        showEnterX(stoner, in);
        break;
      case 208:
        handleEnterX(stoner, in);
    }
  }

  public void handleDialogue(Stoner stoner) {
    if ((stoner.getDialogue() == null) || (stoner.getDialogue().getNext() == -1))
      stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    else if (stoner.getDialogue().getNext() > -1) stoner.getDialogue().execute();
  }

  public void handleEnterX(Stoner stoner, StreamBuffer.InBuffer in) {
    int amount = in.readInt();

    int slot = stoner.getEnterXSlot();
    int id = stoner.getEnterXItemId();

    if (amount < 1) {
      return;
    }

    switch (stoner.getEnterXInterfaceId()) {
      case 3917:
        double init = 0;
        int professionId = stoner.getEnterXSlot();
        int type = stoner.getEnterXItemId();

        if (professionId == Professions.PROFESSION_COUNT) {
          if (type == 2) {
            if (amount >= Integer.MAX_VALUE) {
              amount = Integer.MAX_VALUE;
            }

            long totalExp = stoner.getProfession().getTotalExperience();

            if (totalExp >= Integer.MAX_VALUE) {
              return;
            }

            init = totalExp;
          } else {
            return;
          }
        } else {
          if (type == 1) {
            if (amount > 420) {
              amount = 420;
            }

            if (stoner
                    .getProfession()
                    .getGradeForExperience(
                        professionId, stoner.getProfession().getExperience()[professionId])
                >= 420) {
				System.out.println("Grade for exp = " + stoner.getProfession().getGradeForExperience(professionId, stoner.getProfession().getExperience()[professionId]));

				return;
            }

            init =
                stoner
                    .getProfession()
                    .getGradeForExperience(
                        professionId, stoner.getProfession().getExperience()[professionId]);
          } else if (type == 2) {
            if (amount > 1_000_000_000) {
              amount = 1_000_000_000;
            }

            if (stoner.getProfession().getExperience()[professionId] >= 1_000_000_000) {
              return;
            }

            init = stoner.getProfession().getExperience()[professionId];
          } else {
            return;
          }
        }

        if (amount <= init) {
          return;
        }

        stoner.send(
            new SendProfessionGoal(
                professionId,
                (int) stoner.getProfession().getExperience()[professionId],
                amount,
                type - 1));
        break;
      case 2700:
        if (stoner.getSummoning().isFamilarBOB()) {
          stoner.getSummoning().getContainer().withdraw(slot, amount);
        }
        break;
      case 55678:
        BoneBurying.finishOnAltar(stoner, amount);
        break;
      case 3823:
        stoner.getShopping().sell(id, amount, slot);
        break;
      case 3900:
        stoner.getShopping().buy(id, amount, slot);
        break;
      case 15460:
        break;
      case 1743:
        FoodieTask.attemptFoodie(
            stoner,
            stoner.getAttributes().getInt("foodieitem"),
            stoner.getAttributes().getInt("foodieobject"),
            amount);
        break;
      case 4429:
        THChempistryUnfinishedPotionTask.attemptToCreateUnfinishedPotion(
            stoner,
            amount,
            (Item) stoner.getAttributes().get("thchempistryitem1"),
            (Item) stoner.getAttributes().get("thchempistryitem2"));
        break;
      case 4430:
        THChempistryFinishedPotionTask.attemptPotionMaking(stoner, amount);
        break;
      case 5064:
        if (!stoner.getBox().slotContainsItem(slot, id)) {
          return;
        }

        if (stoner.getInterfaceManager().hasBankOpen()) stoner.getBank().deposit(id, amount, slot);
        else if (stoner.getSummoning().isFamilarBOB()) {
          stoner.getSummoning().getContainer().store(id, amount, slot);
        }

        break;
      case 5382:
        if (stoner.getBank().hasItemId(id)) {
          if (amount == -1) {
            amount = stoner.getBank().getItemAmount(id);
          }

          stoner.getBank().withdraw(id, amount);
        }
        break;
      case 6669:
        if (stoner.getDueling().isStaking()) {
          stoner.getDueling().getContainer().withdraw(slot, amount);
        }
        break;
      case 3322:
        if (stoner.getTrade().trading()) stoner.getTrade().getContainer().offer(id, amount, slot);
        else if (stoner.getDueling().isStaking()) {
          stoner.getDueling().getContainer().offer(id, amount, slot);
        }
        break;
      case 3415:
        if (stoner.getTrade().trading()) {
          stoner.getTrade().getContainer().withdraw(slot, amount);
        }
        break;
    }
  }

  public void showEnterX(Stoner stoner, StreamBuffer.InBuffer in) {
    stoner.setEnterXSlot(in.readShort(StreamBuffer.ByteOrder.LITTLE));
    stoner.setEnterXInterfaceId(in.readShort(StreamBuffer.ValueType.A));
    stoner.setEnterXItemId(in.readShort(StreamBuffer.ByteOrder.LITTLE));
    stoner.getClient().queueOutgoingPacket(new SendEnterXInterface());
  }
}
