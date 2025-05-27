package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.impl.ProductionTask;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class GlassBlowing extends ProductionTask {
  private final short productionCount;
  private final Glass glass;

  public GlassBlowing(Stoner entity, short productionCount, Glass glass) {
    super(
        entity,
        0,
        false,
        Task.StackType.NEVER_STACK,
        Task.BreakType.ON_MOVE,
        TaskIdentifier.CURRENT_ACTION);
    this.productionCount = productionCount;
    this.glass = glass;
  }

  @Override
  public boolean canProduce() {
    return true;
  }

  @Override
  public Animation getAnimation() {
    return new Animation(884);
  }

  @Override
  public Item[] getConsumedItems() {
    return new Item[] {new Item(glass.getMaterialId())};
  }

  @Override
  public int getCycleCount() {
    return 7;
  }

  @Override
  public double getExperience() {
    return glass.getExperience();
  }

  @Override
  public Graphic getGraphic() {
    return null;
  }

  @Override
  public String getInsufficentGradeMessage() {
    return "You need a "
        + com.bestbudz.rs2.content.profession.Professions.PROFESSION_NAMES[getProfession()]
        + " grade of "
        + getRequiredGrade()
        + " to blow this glass.";
  }

  @Override
  public int getProductionCount() {
    return productionCount;
  }

  @Override
  public int getRequiredGrade() {
    return glass.getRequiredGrade();
  }

  @Override
  public Item[] getRewards() {
    return new Item[] {new Item(glass.getRewardId())};
  }

  @Override
  public int getProfession() {
    return 12;
  }

  @Override
  public String getSuccessfulProductionMessage() {
    String itemName =
        GameDefinitionLoader.getItemDef(getRewards()[0].getId()).getName().toLowerCase();
    return "you make " + (Utility.startsWithVowel(itemName) ? "an" : "a") + " " + itemName + ".";
  }

  @Override
  public String noIngredients(Item item) {
    return null;
  }

  @Override
  public void onStop() {}
}
