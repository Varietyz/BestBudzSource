package com.bestbudz.rs2.content.trading;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.NameUtil;
import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.logger.StonerLogger;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.HashMap;

public class Trade {

  public static final int TRADE_CONTAINER_SIZE = 28;
  protected final Stoner stoner;
  protected TradeStages stage = TradeStages.NONE;
  protected Trade tradingWith = null;
  protected TradeContainer container = new TradeContainer(this);
  protected String lastRequest = null;

  public Trade(Stoner stoner) {
    this.stoner = stoner;
  }

  public static String getTotalAmount(int amount) {
    if ((amount >= 10000) && (amount < 10000000)) return amount / 1000 + "K";
    if ((amount >= 10000000) && (amount <= 2147483647)) {
      return amount / 1000000 + "M";
    }
    return amount + " bestbucks";
  }

  public static int getTradedWealth(Stoner Stoner) {
    int value = 0;
    for (Item item : Stoner.getTrade().getTradedItems()) {
      if (item != null)
        value += GameDefinitionLoader.getHighAlchemyValue(item.getId()) * item.getAmount();
    }
    return value;
  }

  public static void sendItemText(Stoner stoner) {
    Item[] traded = stoner.getTrade().getTradedItems();
    Item[] recieving = stoner.getTrade().getTradingWith().getTradedItems();

    StringBuilder trade = new StringBuilder();
    boolean empty = true;
    for (int i = 0; i < stoner.getBox().getSize(); i++) {
      Item item = traded[i];
      String prefix = "";
      if (item != null) {
        empty = false;
        if ((item.getAmount() >= 1000) && (item.getAmount() < 1000000))
          prefix = "@cya@" + item.getAmount() / 1000 + "K @whi@(" + item.getAmount() + ")";
        else if (item.getAmount() >= 1000000)
          prefix =
              "@gre@" + item.getAmount() / 1000000 + " million @whi@(" + item.getAmount() + ")";
        else {
          prefix = "" + item.getAmount();
        }
        trade.append(item.getDefinition().getName());
        trade.append(" x ");
        trade.append(prefix);
        trade.append("\\n");
      }
    }
    if (empty) {
      trade.append("Nada, Nothing!");
    }
    stoner.getClient().queueOutgoingPacket(new SendString(trade.toString(), 3557));
    trade = new StringBuilder();
    empty = true;

    for (int i = 0; i < stoner.getBox().getSize(); i++) {
      Item item = recieving[i];
      String prefix = "";
      if (item != null) {
        empty = false;
        if ((item.getAmount() >= 1000) && (item.getAmount() < 1000000))
          prefix = "@cya@" + item.getAmount() / 1000 + "K @whi@(" + item.getAmount() + ")";
        else if (item.getAmount() >= 1000000)
          prefix =
              "@gre@" + item.getAmount() / 1000000 + " million @whi@(" + item.getAmount() + ")";
        else {
          prefix = "" + item.getAmount();
        }
        trade.append(item.getDefinition().getName());
        trade.append(" x ");
        trade.append(prefix);
        trade.append("\\n");
      }
    }
    if (empty) {
      trade.append("Nada, Nothing!");
    }
    stoner.getClient().queueOutgoingPacket(new SendString(trade.toString(), 3558));
  }

  @SuppressWarnings("incomplete-switch")
  public void accept() {

    if (System.currentTimeMillis() - stoner.tradeDelay < 3500) {
      stoner.send(new SendMessage("@red@The Deal has changed! Please wait..."));
      return;
    }

    if (stage == TradeStages.STAGE_1) {
      if (!stoner.getBox().hasSpaceFor(tradingWith.getTradedItems())) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You do not have enough box space to make this " + getAction() + "."));
        return;
      }
      stoner.getClient().queueOutgoingPacket(new SendString("Waiting for stoner...", 3431));
      tradingWith
          .getStoner()
          .getClient()
          .queueOutgoingPacket(new SendString("Fellow stoner has accepted.", 3431));
      stage = TradeStages.STAGE_1_ACCEPTED;
    } else if (stage == TradeStages.STAGE_2) {
      stage = TradeStages.STAGE_2_ACCEPTED;
      stoner.getClient().queueOutgoingPacket(new SendString("Waiting for stoner...", 3535));
      tradingWith
          .getStoner()
          .getClient()
          .queueOutgoingPacket(new SendString("Fellow stoner has accepted.", 3535));
    }

    if ((tradingWith != null) && (tradingWith.accepted()))
      switch (stage) {
        case STAGE_1_ACCEPTED:
          stage = TradeStages.STAGE_2;
          tradingWith.setStage(TradeStages.STAGE_2);

          sendItemText(stoner);
          sendItemText(tradingWith.getStoner());

          container.update();
          stoner.getClient().queueOutgoingPacket(new SendBoxInterface(3443, 3213));
          tradingWith.getStoner().getClient().queueOutgoingPacket(new SendBoxInterface(3443, 3213));
          break;
        case STAGE_2_ACCEPTED:
          end(true);

          stage = TradeStages.NONE;
          tradingWith.setStage(TradeStages.NONE);

          tradingWith.reset();
          reset();
          break;
      }
  }

  public boolean accepted() {
    return (stage == TradeStages.STAGE_1_ACCEPTED) || (stage == TradeStages.STAGE_2_ACCEPTED);
  }

  public void begin(Trade tradingWith) {
    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendString(
                getStatus()
                    + " with: "
                    + NameUtil.uppercaseFirstLetter(tradingWith.getStoner().getUsername()),
                3417));
    tradingWith
        .getStoner()
        .getClient()
        .queueOutgoingPacket(
            new SendString(
                getStatus() + " with: " + NameUtil.uppercaseFirstLetter(stoner.getUsername()),
                3417));

    stoner.getClient().queueOutgoingPacket(new SendString("", 3431));
    tradingWith.getStoner().getClient().queueOutgoingPacket(new SendString("", 3431));

    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendString("Are you sure you want to make this " + getAction() + "?", 3535));
    tradingWith
        .getStoner()
        .getClient()
        .queueOutgoingPacket(
            new SendString("Are you sure you want to make this " + getAction() + "?", 3535));

    stoner.getClient().queueOutgoingPacket(new SendBoxInterface(3323, 3321));
    tradingWith.getStoner().getClient().queueOutgoingPacket(new SendBoxInterface(3323, 3321));

    reset();
    tradingWith.reset();

    this.tradingWith = tradingWith;
    tradingWith.setTradingWith(this);

    stage = TradeStages.STAGE_1;
    tradingWith.setStage(TradeStages.STAGE_1);

    container.update();
    tradingWith.container.update();
  }

  public boolean canAppendTrade() {
    return (stage == TradeStages.STAGE_1) || (stage == TradeStages.STAGE_1_ACCEPTED);
  }

  public boolean clickTradeButton(int buttonId) {
    switch (buttonId) {
      case 13218:
        accept();
        return true;
      case 13092:
        accept();
        return true;
    }

    return false;
  }

  public void end(boolean success) {
    Item[] traded = getTradedItems();
    Item[] recieving = tradingWith.getTradedItems();

    HashMap<Integer, Integer> trade = new HashMap<>();
    HashMap<Integer, Integer> recieved = new HashMap<>();

    for (int i = 0; i < 28; i++) {
      if (success) {
        if (traded[i] != null) {
          tradingWith.getStoner().getBox().insert(traded[i]);
          if (trade.get(traded[i].getId()) != null) {
            trade.put(traded[i].getId(), traded[i].getAmount() + trade.get(traded[i].getId()));
          } else {
            trade.put(traded[i].getId(), traded[i].getAmount());
          }
        }

        if (recieving[i] != null) {
          stoner.getBox().insert(recieving[i]);
          if (recieved.get(recieving[i].getId()) != null) {
            recieved.put(
                recieving[i].getId(),
                recieving[i].getAmount() + recieved.get(recieving[i].getId()));
          } else {
            recieved.put(recieving[i].getId(), recieving[i].getAmount());
          }
        }
      } else {
        if (traded[i] != null) {
          stoner.getBox().insert(traded[i]);
        }

        if (recieving[i] != null) {
          tradingWith.getStoner().getBox().insert(recieving[i]);
        }
      }
    }

    String[][][] strings = new String[2][trade.size()][4];
    int index = 0;
    for (int item : trade.keySet()) {
      Item tradedItem = new Item(item, trade.get(item));
      strings[0][index] =
          new String[] {
            Utility.formatStonerName(stoner.getUsername()),
            "" + tradedItem.getAmount(),
            Utility.formatStonerName(tradedItem.getDefinition().getName()),
            Utility.formatStonerName(tradingWith.stoner.getUsername())
          };
      strings[1][index] =
          new String[] {
            Utility.formatStonerName(tradingWith.stoner.getUsername()),
            "" + tradedItem.getAmount(),
            Utility.formatStonerName(tradedItem.getDefinition().getName()),
            Utility.formatStonerName(stoner.getUsername())
          };
      index++;
    }

    StonerLogger.TRADE_LOGGER.multiLog(
        stoner.getUsername(), "%s has given %s %s to %s", strings[0]);
    StonerLogger.TRADE_LOGGER.multiLog(
        tradingWith.stoner.getUsername(), "%s has received %s %s from %s", strings[1]);
    strings = new String[2][recieved.size()][4];
    index = 0;

    for (int item : recieved.keySet()) {
      Item tradedItem = new Item(item, recieved.get(item));
      strings[0][index] =
          new String[] {
            Utility.formatStonerName(tradingWith.stoner.getUsername()),
            "" + tradedItem.getAmount(),
            Utility.formatStonerName(tradedItem.getDefinition().getName()),
            Utility.formatStonerName(stoner.getUsername())
          };
      strings[1][index] =
          new String[] {
            Utility.formatStonerName(stoner.getUsername()),
            "" + tradedItem.getAmount(),
            Utility.formatStonerName(tradedItem.getDefinition().getName()),
            Utility.formatStonerName(tradingWith.stoner.getUsername())
          };
      index++;
    }

    StonerLogger.TRADE_LOGGER.multiLog(
        tradingWith.stoner.getUsername(), "%s has given %s %s to %s", strings[0]);
    StonerLogger.TRADE_LOGGER.multiLog(
        stoner.getUsername(), "%s has received %s %s from %s", strings[1]);

    if (success) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("The Deal has been accepted."));
      tradingWith
          .getStoner()
          .getClient()
          .queueOutgoingPacket(new SendMessage("The trade has been accepted."));
    } else {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You decline the " + getAction() + "."));
      tradingWith
          .getStoner()
          .getClient()
          .queueOutgoingPacket(new SendMessage("Fellow stoner declined the " + getAction() + "."));
    }

    stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    tradingWith.getStoner().getClient().queueOutgoingPacket(new SendRemoveInterfaces());

    stoner.getBox().update();
    tradingWith.getStoner().getBox().update();

    stage = TradeStages.NONE;
    tradingWith.setStage(TradeStages.NONE);
  }

  public String getAction() {
    return "Deal";
  }

  public TradeContainer getContainer() {
    return container;
  }

  public Stoner getStoner() {
    return stoner;
  }

  public String getRequestString() {
    return "Deal";
  }

  public TradeStages getStage() {
    return stage;
  }

  public void setStage(TradeStages stage) {
    this.stage = stage;
  }

  public String getStatus() {
    return "Dealing";
  }

  public Item[] getTradedItems() {
    return container.getItems();
  }

  public Trade getTradingWith() {
    return tradingWith;
  }

  public void setTradingWith(Trade tradingWith) {
    this.tradingWith = tradingWith;
  }

  public void request(Stoner requested) {
    if ((requested.isBusy()) || (stoner.isBusy())) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("That stoner is busy at the moment."));
      return;
    }

    if (!stoner.getController().canTrade()) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You can't Deal right now."));
      return;
    }

    if ((requested.getTrade().trading())) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("That stoner is busy at the moment."));
      return;
    }

    stoner
        .getClient()
        .queueOutgoingPacket(new SendMessage("Sending " + getRequestString() + " offer.."));
    lastRequest = requested.getUsername();

    if (requested.getTrade().requested(stoner)) begin(requested.getTrade());
    else if (!requested.getPrivateMessaging().ignored(stoner.getUsername()))
      requested
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  NameUtil.uppercaseFirstLetter(stoner.getUsername())
                      + ":"
                      + getRequestString()
                      + "req:"));
  }

  public boolean requested(Stoner other) {
    if (lastRequest == null) {
      return false;
    }

    return lastRequest.equalsIgnoreCase(other.getUsername());
  }

  public void reset() {
    container = new TradeContainer(this);
    stage = TradeStages.NONE;
    tradingWith = null;
    lastRequest = null;
  }

  public boolean trading() {
    return stage != TradeStages.NONE;
  }

  public enum TradeStages {
    NONE,
    STAGE_1,
    STAGE_1_ACCEPTED,
    STAGE_2,
    STAGE_2_ACCEPTED
  }
}
