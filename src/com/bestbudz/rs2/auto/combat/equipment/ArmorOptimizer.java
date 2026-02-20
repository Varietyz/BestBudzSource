package com.bestbudz.rs2.auto.combat.equipment;

import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ArmorOptimizer {

  private final Stoner stoner;

  public ArmorOptimizer(Stoner stoner) {
    this.stoner = stoner;
  }

  public void optimizeArmorForStyle(CombatTypes style) {
    for (int slot : AutoCombatConfig.ARMOR_SLOTS) {
      optimizeArmorSlot(slot, style);
    }
  }

  public void optimizeOffhandForStyle(CombatTypes style) {
    Item weapon = stoner.getEquipment().getItems()[AutoCombatConfig.WEAPON_SLOT];
    if (weapon == null) return;

    if (weapon.getWeaponDefinition() != null && weapon.getWeaponDefinition().isTwoHanded()) {

      Item currentOffhand = stoner.getEquipment().getItems()[AutoCombatConfig.SHIELD_SLOT];
      if (currentOffhand != null) {
        unequipItem(AutoCombatConfig.SHIELD_SLOT);
      }
      return;
    }

    Item currentOffhand = stoner.getEquipment().getItems()[AutoCombatConfig.SHIELD_SLOT];
    Item bestOffhand = findBestOffhandForStyle(style);

    if (bestOffhand != null) {
      if (currentOffhand == null || isArmorBetterForStyle(bestOffhand, currentOffhand, style)) {
        if (currentOffhand != null) {
          unequipItem(AutoCombatConfig.SHIELD_SLOT);
        }
        equipItem(bestOffhand, AutoCombatConfig.SHIELD_SLOT);
      }
    }
  }

  private void optimizeArmorSlot(int slot, CombatTypes style) {
    Item currentItem = stoner.getEquipment().getItems()[slot];
    Item bestItem = findBestArmorForSlot(slot, style);

    if (bestItem != null) {

      if (currentItem == null) {
        equipItem(bestItem, slot);
        return;
      }

      if (isArmorBetterForStyle(bestItem, currentItem, style)) {

        unequipItem(slot);

        equipItem(bestItem, slot);
      }
    }
  }

  private Item findBestOffhandForStyle(CombatTypes style) {
    Item bestOffhand = null;
    int bestScore = -1;

    Item[] boxItems = stoner.getBox().getItems();
    for (int i = 0; i < boxItems.length; i++) {
      Item item = boxItems[i];
      if (item == null || item.getEquipmentDefinition() == null) continue;
      if (item.getEquipmentDefinition().getSlot() != AutoCombatConfig.SHIELD_SLOT) continue;

      int score = calculateArmorScoreForStyle(item, style);
      if (score > bestScore) {
        bestScore = score;
        bestOffhand = item;
      }
    }

    return bestOffhand;
  }

  private Item findBestArmorForSlot(int slot, CombatTypes style) {
    Item bestArmor = null;
    int bestScore = -1;

    Item[] boxItems = stoner.getBox().getItems();
    for (int i = 0; i < boxItems.length; i++) {
      Item item = boxItems[i];
      if (item == null || item.getEquipmentDefinition() == null) continue;
      if (item.getEquipmentDefinition().getSlot() != slot) continue;

      int score = calculateArmorScoreForStyle(item, style);
      if (score > bestScore) {
        bestScore = score;
        bestArmor = item;
      }
    }

    return bestArmor;
  }

  public int calculateArmorScoreForStyle(Item armor, CombatTypes style) {
    if (armor.getItemBonuses() == null) {
      return armor.getId() / 100;
    }

    short[] bonuses = armor.getItemBonuses();
    String name = armor.getDefinition().getName().toLowerCase();

    if (hasNegativeBonusesForStyle(bonuses, style)) {
      return -1000;
    }

    int score = 0;

    int tierScore = armor.getId() / 100;
    score += tierScore;

    switch (style) {
      case MELEE:
        score += calculateMeleeScore(bonuses, name);
        break;
      case SAGITTARIUS:
        score += calculateSagittariusScore(bonuses, name);
        break;
      case MAGE:
        score += calculateMageScore(bonuses, name);
        break;
    }

    return score;
  }

  private boolean hasNegativeBonusesForStyle(short[] bonuses, CombatTypes style) {
    switch (style) {
      case MELEE:

        if (bonuses.length > 0 && bonuses[0] < 0) return true;
        if (bonuses.length > 1 && bonuses[1] < 0) return true;
        if (bonuses.length > 2 && bonuses[2] < 0) return true;
        if (bonuses.length > 10 && bonuses[10] < 0) return true;
        break;
      case SAGITTARIUS:

        if (bonuses.length > 4 && bonuses[4] < 0) return true;
        if (bonuses.length > 10 && bonuses[10] < 0) return true;
        break;
      case MAGE:

        if (bonuses.length > 3 && bonuses[3] < 0) return true;
        if (bonuses.length > 11 && bonuses[11] < 0) return true;
        break;
    }
    return false;
  }

  private int calculateMeleeScore(short[] bonuses, String name) {
    int score = 0;

    if (isMeleeArmor(name)) {
      score += AutoCombatConfig.STYLE_SPECIFIC_ARMOR_BONUS;
    }

    if (bonuses.length > 0 && bonuses[0] > 0)
      score += bonuses[0] * AutoCombatConfig.MELEE_ATTACK_BONUS_MULTIPLIER;
    if (bonuses.length > 1 && bonuses[1] > 0)
      score += bonuses[1] * AutoCombatConfig.MELEE_ATTACK_BONUS_MULTIPLIER;
    if (bonuses.length > 2 && bonuses[2] > 0)
      score += bonuses[2] * AutoCombatConfig.MELEE_ATTACK_BONUS_MULTIPLIER;
    if (bonuses.length > 10 && bonuses[10] > 0)
      score += bonuses[10] * AutoCombatConfig.MELEE_STRENGTH_BONUS_MULTIPLIER;

    boolean hasOffensiveBonuses =
        hasOffensiveBonuses(bonuses, 0, 3) || (bonuses.length > 10 && bonuses[10] > 0);
    int defenseMultiplier =
        hasOffensiveBonuses
            ? AutoCombatConfig.MELEE_DEFENSE_BONUS_MULTIPLIER
            : AutoCombatConfig.MELEE_DEFENSE_BONUS_MULTIPLIER_NO_OFFENSE;

    if (bonuses.length > 5 && bonuses[5] > 0)
      score += bonuses[5] * defenseMultiplier;
    if (bonuses.length > 6 && bonuses[6] > 0)
      score += bonuses[6] * defenseMultiplier;
    if (bonuses.length > 7 && bonuses[7] > 0)
      score += bonuses[7] * defenseMultiplier;

    return score;
  }

  private int calculateSagittariusScore(short[] bonuses, String name) {
    int score = 0;

    if (isSagittariusArmor(name)) {
      score += AutoCombatConfig.STYLE_SPECIFIC_ARMOR_BONUS;
    }

    if (bonuses.length > 4 && bonuses[4] > 0)
      score +=
          bonuses[4] * AutoCombatConfig.SAGITTARIUS_ATTACK_BONUS_MULTIPLIER;
    if (bonuses.length > 10 && bonuses[10] > 0)
      score +=
          bonuses[10] * AutoCombatConfig.SAGITTARIUS_STRENGTH_BONUS_MULTIPLIER;

    if (bonuses.length > 9 && bonuses[9] > 0)
      score +=
          bonuses[9] * AutoCombatConfig.SAGITTARIUS_DEFENSE_BONUS_MULTIPLIER;

    return score;
  }

  private int calculateMageScore(short[] bonuses, String name) {
    int score = 0;

    if (isMageArmor(name)) {
      score += AutoCombatConfig.STYLE_SPECIFIC_ARMOR_BONUS;
    }

    if (bonuses.length > 3 && bonuses[3] > 0)
      score += bonuses[3] * AutoCombatConfig.MAGE_ATTACK_BONUS_MULTIPLIER;
    if (bonuses.length > 11 && bonuses[11] > 0)
      score += bonuses[11] * AutoCombatConfig.MAGE_DAMAGE_BONUS_MULTIPLIER;

    if (bonuses.length > 8 && bonuses[8] > 0)
      score += bonuses[8] * AutoCombatConfig.MAGE_DEFENSE_BONUS_MULTIPLIER;

    return score;
  }

  private boolean isArmorBetterForStyle(Item armorA, Item armorB, CombatTypes style) {
    int scoreA = calculateArmorScoreForStyle(armorA, style);
    int scoreB = calculateArmorScoreForStyle(armorB, style);

    if (scoreA < 0) return false;

    return scoreA > scoreB + AutoCombatConfig.ARMOR_IMPROVEMENT_THRESHOLD;
  }

  private boolean isMeleeArmor(String name) {
    return name.contains("strength")
        || name.contains("warrior")
        || name.contains("fighter")
        || name.contains("dragon")
        || name.contains("rune")
        || name.contains("adamant")
        || name.contains("mithril")
        || name.contains("steel")
        || name.contains("iron")
        || name.contains("bronze")
        || name.contains("platebody")
        || name.contains("chainbody")
        || name.contains("platelegs")
        || name.contains("plateskirt")
        || name.contains("full helm")
        || name.contains("med helm")
        || name.contains("square shield")
        || name.contains("kiteshield");
  }

  private boolean isSagittariusArmor(String name) {
    return name.contains("archer")
        || name.contains("sagittarius")
        || name.contains("leather")
        || name.contains("coif")
        || name.contains("vamb")
        || name.contains("d'hide")
        || name.contains("dragonhide")
        || name.contains("studded")
        || name.contains("hardleather");
  }

  private boolean isMageArmor(String name) {
    return name.contains("wizard")
        || name.contains("robe")
        || name.contains("mystic")
        || name.contains("enchanted")
        || name.contains("magic")
        || name.contains("splitbark")
        || name.contains("ahrim")
        || name.contains("ancestral")
        || name.contains("infinity")
        || name.contains("lunar")
        || (name.contains("void") && name.contains("mage"));
  }

  private boolean hasOffensiveBonuses(short[] bonuses, int start, int count) {
    for (int i = start; i < Math.min(start + count, bonuses.length); i++) {
      if (bonuses[i] > 0) {
        return true;
      }
    }
    return false;
  }

  private boolean equipItem(Item item, int targetSlot) {
    for (int i = 0; i < stoner.getBox().getItems().length; i++) {
      Item boxItem = stoner.getBox().getItems()[i];
      if (boxItem != null && boxItem.getId() == item.getId()) {
        try {
          stoner.getEquipment().equip(boxItem, i);
          return true;
        } catch (Exception e) {
          return false;
        }
      }
    }
    return false;
  }

  private boolean unequipItem(int slot) {
    try {
      stoner.getEquipment().unequip(slot);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
	}
