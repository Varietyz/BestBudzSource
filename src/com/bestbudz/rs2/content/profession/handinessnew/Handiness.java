package com.bestbudz.rs2.content.profession.handinessnew;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.handinessnew.craftable.Craftable;
import com.bestbudz.rs2.content.profession.handinessnew.craftable.CraftableItem;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendItemOnInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.HashMap;

public enum Handiness {
  SINGLETON;

  public static final String CRAFTABLE_KEY = "CRAFTABLE_KEY";

  private final HashMap<Integer, Craftable> CRAFTABLES = new HashMap<>();

  public boolean itemOnItem(Stoner stoner, Item use, Item with) {
    if (stoner.getProfession().locked()) {
      return false;
    }

    if ((use.getId() == 1733 && with.getId() == 1741)
        || (use.getId() == 1741 && with.getId() == 1733)) {
      stoner.getClient().queueOutgoingPacket(new SendInterface(2311));
      return true;
    }
    if (!stoner.getEquipment().isWearingItem(6575)) {
      DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
      return false;
    }
    final Craftable craftable = getCraftable(use.getId(), with.getId());

    if (craftable == null) {
      return false;
    }

    switch (craftable.getCraftableItems().length) {
      case 1:
        stoner.getAttributes().set(CRAFTABLE_KEY, craftable);
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    + craftable.getCraftableItems()[0].getProduct().getDefinition().getName(),
                2799));
        stoner.send(
            new SendItemOnInterface(
                1746, 170, craftable.getCraftableItems()[0].getProduct().getId()));
        stoner.send(new SendChatBoxInterface(4429));
        return true;

      case 2:
        stoner.getAttributes().set(CRAFTABLE_KEY, craftable);
        stoner.send(
            new SendItemOnInterface(
                8869, 170, craftable.getCraftableItems()[0].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8870, 170, craftable.getCraftableItems()[1].getProduct().getId()));

        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n "
                    .concat(
                        craftable
                            .getCraftableItems()[0]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8874));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n "
                    .concat(
                        craftable
                            .getCraftableItems()[1]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8878));

        stoner.send(new SendChatBoxInterface(8866));
        return true;

      case 3:
        stoner.getAttributes().set(CRAFTABLE_KEY, craftable);
        stoner.send(
            new SendItemOnInterface(
                8883, 170, craftable.getCraftableItems()[0].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8884, 170, craftable.getCraftableItems()[1].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8885, 170, craftable.getCraftableItems()[2].getProduct().getId()));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    .concat(
                        craftable
                            .getCraftableItems()[0]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8889));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    .concat(
                        craftable
                            .getCraftableItems()[1]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8893));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    .concat(
                        craftable
                            .getCraftableItems()[2]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8897));
        stoner.send(new SendChatBoxInterface(8880));
        return true;

      case 4:
        stoner.getAttributes().set(CRAFTABLE_KEY, craftable);
        stoner.send(
            new SendItemOnInterface(
                8902, 170, craftable.getCraftableItems()[0].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8903, 170, craftable.getCraftableItems()[1].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8904, 170, craftable.getCraftableItems()[2].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8905, 170, craftable.getCraftableItems()[3].getProduct().getId()));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    .concat(
                        craftable
                            .getCraftableItems()[0]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8909));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    .concat(
                        craftable
                            .getCraftableItems()[1]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8913));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    .concat(
                        craftable
                            .getCraftableItems()[2]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8917));
        stoner.send(
            new SendString(
                "\\n \\n \\n \\n \\n"
                    .concat(
                        craftable
                            .getCraftableItems()[3]
                            .getProduct()
                            .getDefinition()
                            .getName()
                            .replace("d'hide ", "")),
                8921));
        stoner.send(new SendChatBoxInterface(8899));
        return true;

      case 5:
        stoner.getAttributes().set(CRAFTABLE_KEY, craftable);
        stoner.send(
            new SendItemOnInterface(
                8941, 170, craftable.getCraftableItems()[0].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8942, 170, craftable.getCraftableItems()[1].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8943, 170, craftable.getCraftableItems()[2].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8944, 170, craftable.getCraftableItems()[3].getProduct().getId()));
        stoner.send(
            new SendItemOnInterface(
                8945, 170, craftable.getCraftableItems()[4].getProduct().getId()));
        stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Body"), 8949));
        stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Chaps"), 8953));
        stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Vambraces"), 8957));
        stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Bandana"), 8961));
        stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Boots"), 8965));
        stoner.send(new SendChatBoxInterface(8938));
        return true;

      default:
        return false;
    }
  }

  public boolean craft(Stoner stoner, int index, int amount) {
    if (stoner.getProfession().locked()) {
      return false;
    }

    Craftable craftable = (Craftable) stoner.getAttributes().get(CRAFTABLE_KEY);

    if (craftable == null) {
      return false;
    }

    return start(stoner, craftable, index, amount);
  }

  public void addCraftable(Craftable craftable) {
    if (CRAFTABLES.put(craftable.getWith().getId(), craftable) != null) {
      System.out.println(
          "[Handiness] Conflicting item values: "
              + craftable.getWith().getId()
              + " Type: "
              + craftable.getName());
    }
  }

  public Craftable getCraftable(int use, int with) {
    return CRAFTABLES.get(use) == null ? CRAFTABLES.get(with) : CRAFTABLES.get(use);
  }

  public boolean clickButton(Stoner stoner, int button) {
    if (stoner.getAttributes().get(CRAFTABLE_KEY) == null) {
      return false;
    }

    Craftable craftable = (Craftable) stoner.getAttributes().get(CRAFTABLE_KEY);

    switch (button) {
      case 6211:
        start(stoner, craftable, 0, stoner.getBox().getItemAmount(craftable.getWith().getId()));
        return true;
      case 34205:
      case 34185:
      case 34170:
      case 10239:
      case 34245:
        start(stoner, craftable, 0, 1);
        return true;
      case 34204:
      case 34184:
      case 34169:
      case 10238:
      case 34244:
        start(stoner, craftable, 0, 5);
        return true;
      case 34203:
      case 34183:
      case 34168:
      case 34243:
        start(stoner, craftable, 0, 10);
        return true;
      case 34202:
      case 34182:
      case 34167:
      case 6212:
      case 34242:
        start(stoner, craftable, 0, 250);
        return true;
      case 34209:
      case 34189:
      case 34174:
      case 34249:
        start(stoner, craftable, 1, 1);
        return true;
      case 34208:
      case 34188:
      case 34173:
      case 34248:
        start(stoner, craftable, 1, 5);
        return true;
      case 34207:
      case 34187:
      case 34172:
      case 34247:
        start(stoner, craftable, 1, 10);
        return true;
      case 34206:
      case 34186:
      case 34171:
      case 34246:
        start(stoner, craftable, 1, 250);
        return true;
      case 34213:
      case 34193:
      case 34253:
        start(stoner, craftable, 2, 1);
        return true;
      case 34212:
      case 34192:
      case 34252:
        start(stoner, craftable, 2, 5);
        return true;
      case 34211:
      case 34191:
      case 34251:
        start(stoner, craftable, 2, 10);
        return true;
      case 34210:
      case 34190:
      case 34250:
        start(stoner, craftable, 2, 250);
        return true;
      case 34217:
      case 35001:
        start(stoner, craftable, 3, 1);
        return true;
      case 34216:
      case 35000:
        start(stoner, craftable, 3, 5);
        return true;
      case 34215:
      case 34255:
        start(stoner, craftable, 3, 10);
        return true;
      case 34214:
      case 34254:
        start(stoner, craftable, 3, 250);
        return true;
      case 35005:
        start(stoner, craftable, 4, 1);
        return true;
      case 35004:
        start(stoner, craftable, 4, 5);
        return true;
      case 35003:
        start(stoner, craftable, 4, 10);
        return true;
      case 35002:
        start(stoner, craftable, 4, 250);
        return true;

      default:
        return false;
    }
  }

  public boolean start(Stoner stoner, Craftable craftable, int index, int amount) {
    if (craftable == null) {
      return false;
    }

    stoner.getAttributes().remove(CRAFTABLE_KEY);

    CraftableItem item = craftable.getCraftableItems()[index];

    stoner.send(new SendRemoveInterfaces());

    if (stoner.getGrades()[Professions.HANDINESS] < item.getGrade()) {
      DialogueManager.sendStatement(
          stoner, "<col=369>You need a Handiness grade of " + item.getGrade() + " to do that.");
      return true;
    }

    if (!stoner.getBox().hasAllItems(craftable.getIngediants(index))) {
      Item requiredItem = craftable.getCraftableItems()[index].getRequiredItem();
      Item product = craftable.getCraftableItems()[index].getProduct();
      String productAmount = "";

      if (product.getDefinition().getName().contains("vamb")) {
        productAmount = " pair of";
      } else if (!product.getDefinition().getName().endsWith("s")) {
        productAmount = " " + Utility.getAOrAn(product.getDefinition().getName());
      }

      stoner.send(
          new SendMessage(
              "You need "
                  + requiredItem.getAmount()
                  + " piece"
                  + (requiredItem.getAmount() > 1 ? "s" : "")
                  + " of "
                  + requiredItem.getDefinition().getName().toLowerCase()
                  + " to make"
                  + productAmount
                  + " "
                  + product.getDefinition().getName().toLowerCase()
                  + "."));
      return true;
    }

    TaskQueue.queue(
        new Task(
            stoner,
            2,
            true,
            StackType.NEVER_STACK,
            BreakType.ON_MOVE,
            TaskIdentifier.PROFESSION_CREATING) {
          private int iterations = 0;

          @Override
          public void execute() {
            stoner.getProfession().lock(2);

            stoner.getUpdateFlags().sendAnimation(new Animation(craftable.getAnimation()));
            stoner.getProfession().addExperience(Professions.HANDINESS, item.getExperience());
            stoner.getBox().remove(craftable.getIngediants(index), true);
            stoner.getBox().add(item.getProduct());

            if (craftable.getProductionMessage() != null) {
              stoner.send(new SendMessage(craftable.getProductionMessage()));
            }

            if (craftable.getName() == "Gem") {
              AchievementHandler.activateAchievement(stoner, AchievementList.CUT_2500_GEMS, 1);
            }

            if (++iterations == amount) {
              stop();
              return;
            }

            if (!stoner.getBox().hasAllItems(craftable.getIngediants(index))) {
              stop();
              DialogueManager.sendStatement(stoner, "<col=369>You have run out of materials.");
            }
          }

          @Override
          public void onStop() {
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
          }
        });

    return true;
  }
}
