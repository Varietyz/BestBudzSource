package com.bestbudz.rs2.entity.item.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GlobalItemHandler {

  private static final Logger logger = Logger.getLogger(GlobalItemHandler.class.getName());

  private static final List<GroundItem> groundItems = new ArrayList<GroundItem>();

  private static void add(GroundItem item) {
    groundItems.add(item);
  }

  public static void createRespawnTask(final GroundItem groundItem) {
    final GroundItem tempItem =
        new GroundItem(
            groundItem.getItem(), groundItem.getLocation(), groundItem.getRespawnTimer());
    TaskQueue.queue(
        new Task(tempItem.getRespawnTimer()) {
          @Override
          public void execute() {
            GroundItemHandler.add(tempItem);
            GroundItemHandler.globalize(tempItem);
            this.stop();
          }

          @Override
          public void onStop() {}
        });
  }

  public static void spawnGroundItems() {
    add(new GroundItem(new Item(952, 1), new Location(3563, 3305), 50));

    logger.info(
        "Successfully loaded " + Utility.format(groundItems.size()) + " global ground items.");
    for (GroundItem item : groundItems) {
      if (item == null) continue;
      GroundItemHandler.add(item);
    }
  }
}
