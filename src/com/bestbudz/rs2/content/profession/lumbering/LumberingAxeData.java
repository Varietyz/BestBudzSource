package com.bestbudz.rs2.content.profession.lumbering;

import com.bestbudz.rs2.entity.Animation;
import java.util.HashMap;
import java.util.Map;

public enum LumberingAxeData {
  DRAGON_AXE(6575, 1, 20, new Animation(2846));

  private static final Map<Integer, LumberingAxeData> axes =
      new HashMap<Integer, LumberingAxeData>();
  int itemId;
  int gradeRequired;
  int bonus;
  Animation animation;

  LumberingAxeData(int id, int grade, int bonus, Animation animation) {
    itemId = id;
    gradeRequired = grade;
    this.bonus = bonus;
    this.animation = animation;
  }

  public static final void declare() {
    for (LumberingAxeData data : values()) axes.put(Integer.valueOf(data.getId()), data);
  }

  public static LumberingAxeData forId(int id) {
    return axes.get(Integer.valueOf(id));
  }

  public Animation getAnimation() {
    return animation;
  }

  public int getBonus() {
    return bonus;
  }

  public int getId() {
    return itemId;
  }

  public int getGradeRequired() {
    return gradeRequired;
  }
}
