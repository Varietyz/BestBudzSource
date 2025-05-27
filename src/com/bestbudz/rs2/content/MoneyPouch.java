package com.bestbudz.rs2.content;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class MoneyPouch {

  private final Stoner stoner;

  public MoneyPouch(Stoner stoner) {
    this.stoner = stoner;
  }

  public String formatBestBucks(long amount) {
    if (amount >= 1_000 && amount < 1_000_000) {
      return (amount / 1_000) + "K";
    }

    if (amount >= 1_000_000 && amount < 1_000_000_000) {
      return (amount / 1_000_000) + "M";
    }

    if (amount >= 1_000_000_000) {
      return (amount / 1_000_000_000) + "B";
    }
    return "" + amount;
  }

  public void addPouch() {
    if (stoner.getPin() != null && !stoner.enteredPin) {
      stoner.send(new SendInterface(48750));
      return;
    }
    if (stoner.inWGLobby()
        || stoner.inWGGame()
        || stoner.getMage().isTeleporting()
        || stoner.isDead()
        || stoner.inWilderness()
        || stoner.getCombat().inCombat()
        || stoner.getDueling().isDueling()
        || stoner.getInterfaceManager().hasInterfaceOpen()) {
      stoner.send(new SendMessage("You can't do this right now!"));
      return;
    }
    if (stoner.getMoneyPouch() == Long.MAX_VALUE) {
      stoner.send(new SendMessage("Your debit card is at maximum capacity!"));
      return;
    }
    int amount = stoner.getBox().getItemAmount(995);
    if (stoner.getMoneyPouch() + amount >= Long.MAX_VALUE) {
      stoner.send(new SendMessage("Unable to exceed debit card upload limit!"));
      return;
    }
    stoner.getBox().remove(995, amount);
    stoner.setMoneyPouch(stoner.getMoneyPouch() + amount);
    stoner.send(
        new SendMessage(
            "@dre@You have added "
                + Utility.format(amount)
                + " BestBucks to your debit card. Total: "
                + formatBestBucks(stoner.getMoneyPouch())
                + "."));
    stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
  }

  public void withdrawPouch(long amount) {
    if (stoner.getPin() != null && !stoner.enteredPin) {
      stoner.send(new SendInterface(48750));
      return;
    }
    if (stoner.inWGLobby()
        || stoner.inWGGame()
        || stoner.getMage().isTeleporting()
        || stoner.isDead()
        || stoner.inWilderness()
        || stoner.getCombat().inCombat()
        || stoner.getDueling().isDueling()
        || stoner.getInterfaceManager().hasInterfaceOpen()) {
      stoner.send(new SendMessage("You can't do this right now!"));
      return;
    }
    if (amount <= 0) {
      stoner.send(new SendMessage("You can't withdraw a negative amount!"));
      return;
    }
    if (stoner.getMoneyPouch() < amount) {
      amount = stoner.getMoneyPouch();
    }
    if ((stoner.getBox().getItemAmount(995) + amount) > Integer.MAX_VALUE) {
      stoner.send(new SendMessage("You don't have enough space to withdraw that many bestbucks"));
      amount = Integer.MAX_VALUE - stoner.getBox().getItemAmount(995);
    }
    if (amount > Integer.MAX_VALUE) {
      stoner.send(new SendMessage("You can't withdraw more than 2B BestBucks at a time!"));
      return;
    }
    if (stoner.getBox().getItemAmount(995) == Integer.MAX_VALUE) {
      stoner.send(new SendMessage("You can't withdraw any more BestBucks!"));
      return;
    }
    if (!stoner.getBox().hasItemId(995) && stoner.getBox().getFreeSlots() == 0) {
      stoner.send(new SendMessage("You do not have enough box spaces to withdraw BestBucks."));
      return;
    }
    stoner.setMoneyPouch(stoner.getMoneyPouch() - amount);
    stoner.getBox().add(995, (int) amount);
    DialogueManager.sendItem1(
        stoner, "You have withdrawn <col=255>" + Utility.format(amount) + " </col>BestBucks.", 995);
    stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
  }

  public void clear() {}
}
