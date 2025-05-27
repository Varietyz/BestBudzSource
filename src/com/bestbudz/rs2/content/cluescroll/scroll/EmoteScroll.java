package com.bestbudz.rs2.content.cluescroll.scroll;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.cluescroll.Clue;
import com.bestbudz.rs2.content.cluescroll.Clue.ClueType;
import com.bestbudz.rs2.content.cluescroll.ClueDifficulty;
import com.bestbudz.rs2.content.cluescroll.ClueScroll;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.Arrays;

public class EmoteScroll implements ClueScroll {
  private final int scrollId;
  private final ClueDifficulty difficulty;
  private final Location endLocation;
  private final int endLocationDiameter;
  private final Object[] data;
  private final Item[] requiredItems;

  public EmoteScroll(
      int scrollId,
      ClueDifficulty difficulty,
      Item[] requiredItems,
      Location endLocation,
      int endLocationDiameter,
      Object... data) {
    this.scrollId = scrollId;
    this.difficulty = difficulty;
    this.requiredItems = requiredItems;
    this.endLocation = endLocation;
    this.endLocationDiameter = endLocationDiameter;
    this.data = data;
  }

  @Override
  public boolean execute(Stoner stoner) {
    if (!stoner.getBox().hasItemId(scrollId)) {
      return false;
    }

    if (stoner.getAttributes().is("KILL_AGENT")) {
      return false;
    }

    if (Arrays.equals(requiredItems, stoner.getEquipment().getItems())) {
      if (getDifficulty().equals(ClueDifficulty.HARD)) {
        stoner.getAttributes().set("KILL_AGENT", Boolean.TRUE);
        Location spawn = GameConstants.getClearAdjacentLocation(stoner.getLocation(), 1);
        Mob doubleAgent = new Mob(stoner, 1778, false, false, true, spawn);
        doubleAgent.getCombat().setAssaulting(stoner);
        doubleAgent.getUpdateFlags().sendGraphic(new Graphic(86));
      } else {
        onAgentDeath(stoner);
      }
    } else {
      stoner.send(new SendMessage("You must only wear the required items."));
    }

    return true;
  }

  @Override
  public boolean meetsRequirements(Stoner stoner) {
    return inEndArea(stoner.getLocation());
  }

  @Override
  public boolean inEndArea(Location location) {
    return Utility.getExactDistance(endLocation, location) <= endLocationDiameter;
  }

  @Override
  public Clue getClue() {
    return new Clue(ClueType.EMOTE, data);
  }

  @Override
  public int getScrollId() {
    return scrollId;
  }

  @Override
  public ClueDifficulty getDifficulty() {
    return difficulty;
  }

  public void onAgentDeath(Stoner stoner) {
    TaskQueue.queue(
        new Task(
            stoner,
            1,
            false,
            StackType.NEVER_STACK,
            BreakType.NEVER,
            TaskIdentifier.TREASURE_TRAILS) {
          int ticks = 0;
          Location spawn;
          Mob uri = null;

          @Override
          public void execute() {
            switch (ticks++) {
              case 1:
                spawn = GameConstants.getClearAdjacentLocation(stoner.getLocation(), 1);
                World.sendStillGraphic(86, 0, spawn);
                break;

              case 2:
                uri = new Mob(stoner, 1776, false, false, false, spawn);
                uri.getUpdateFlags().faceEntity(stoner.getIndex());
                uri.getUpdateFlags().sendAnimation(new Animation(863));
                reward(stoner, "Uri gives you");
                break;

              case 5:
                if (uri != null && uri.isActive()) {
                  World.sendStillGraphic(287, 0, spawn);
                  uri.remove();
                }
                stop();
                break;
            }
          }

          @Override
          public void onStop() {
            stoner.getAttributes().set("KILL_AGENT", Boolean.FALSE);
          }
        });
  }

  public int getAnimationId() {
    return Integer.parseInt(String.valueOf(data[0]));
  }
}
