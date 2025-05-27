package com.bestbudz.rs2.entity.item;

import com.bestbudz.core.definitions.WeaponDefinition;
import com.bestbudz.rs2.content.EasterRing;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.content.profession.sagittarius.SagittariusConstants;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Entity.AssaultType;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.item.EquipmentConstants.WeaponAssaultStyles;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEquipment;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.math.BigInteger;

public class Equipment {

  private final Item[] items = new Item[14];
  private final Stoner stoner;
  private AssaultStyles assaultStyle = AssaultStyles.ACCURATE;

  public Equipment(Stoner stoner) {
    this.stoner = stoner;
  }

  public static boolean ignoreShieldEmote(int id) {
    return id == 3842 || id == 3844 || id == 3840 || id == 6889;
  }

  public void addOnLogin(Item item, int slot) {
    if (item == null) {
      return;
    }

    getItems()[slot] = item;
  }

  public void calculateBonuses() {
    stoner.setBonuses(new int[stoner.getBonuses().length]);

    for (Item i : items) {
      if (i != null) {
        short[] b = i.getItemBonuses();

        if (b != null) {
          for (int k = 0; k < b.length; k++) {
            stoner.getBonuses()[k] += b[k];
          }
        }
      }
    }

    updateBonusInterface();
  }

  public boolean canEquip(Item item, int slot) {
    if ((slot > items.length) || (slot < 0)) {
      return false;
    }

    return (item.getId() != 773) || (item.getId() != 774) || (StonerConstants.isStaff(stoner));
  }

  public void clear() {
    if (!stoner.isActive()) {
      for (int i = 0; i < items.length; i++) {
        if (items[i] != null) {
          items[i] = null;
        }
      }

      return;
    }

    for (int i = 0; i < items.length; i++) {
      if (items[i] != null) {
        items[i] = null;
        stoner.getClient().queueOutgoingPacket(new SendEquipment(i, 0, 0));
      }
    }
    updateSidebar();
    updateStonerAnimations();
    updateAssaultStyle();
    stoner.getSpecialAssault().onEquip();
    stoner.getMage().getSpellCasting().disableAutocast();
    stoner.updateCombatType();
    updateBlockAnimation();

    stoner.setBonuses(new int[stoner.getBonuses().length]);
    stoner.setAppearanceUpdateRequired(true);
    stoner.getCombat().reset();
  }

  public boolean contains(int itemID) {
    Item[] itemsEquipped = getItems();

    for (Item equippedItem : itemsEquipped) {
      if (equippedItem != null && equippedItem.getId() == itemID) {
        return true;
      }
    }

    return false;
  }

  public void equip(Item item, int clickSlot) {
    int slot = item.getEquipmentDefinition() != null ? item.getEquipmentDefinition().getSlot() : 3;

    if ((!canEquip(item, slot)) || (!stoner.getController().canEquip(stoner, item.getId(), slot))) {
      return;
    }

    if ((item.getId() == 7927) && (!EasterRing.canEquip(stoner))) {
      return;
    }

    if ((items[slot] != null)
        && (items[slot].getId() == item.getId())
        && (items[slot].getDefinition().isStackable())) {
      if (items[slot].getAmount() + stoner.getBox().getItems()[clickSlot].getAmount() < 0) {
        return;
      }
      items[slot] =
          new Item(
              items[slot].getId(),
              items[slot].getAmount() + stoner.getBox().getItems()[clickSlot].getAmount());
      stoner.getBox().getItems()[clickSlot] = null;
    } else if (items[slot] != null) {
      Item add = items[slot];
      items[slot] = item;

      if (add.getDefinition().isStackable()) {
        stoner.getBox().setSlot(null, clickSlot);
        stoner.getBox().add(add);
      } else {
        stoner.getBox().setSlot(add, clickSlot);
      }
    } else {
      items[slot] = item;
      stoner.getBox().clear(clickSlot);
    }

    if (slot == 3) {
      boolean twoHanded =
          item.getWeaponDefinition() != null && item.getWeaponDefinition().isTwoHanded();

      if ((twoHanded) && (items[5] != null)) {
        stoner.getBox().add(items[5]);
        items[5] = null;
        stoner.getClient().queueOutgoingPacket(new SendEquipment(5, 0, 0));
      }
    } else if (slot == 5) {
      if (items[3] != null) {
        boolean twoHanded =
            items[3].getWeaponDefinition() != null && items[3].getWeaponDefinition().isTwoHanded();

        if (twoHanded) {
          stoner.getBox().add(items[3]);
          items[3] = null;
          stoner.getClient().queueOutgoingPacket(new SendEquipment(3, 0, 0));
          updateStonerAnimations();
        }
      }
    }

    if ((slot == 5) || ((slot == 3) && (items[5] == null))) {
      updateBlockAnimation();
    }

    if (slot == 3 || slot == EquipmentConstants.SHIELD_SLOT) {
      updateSidebar();
      updateStonerAnimations();
      updateAssaultStyle();
      stoner.getSpecialAssault().onEquip();
      stoner.getMage().getSpellCasting().disableAutocast();
      stoner.updateCombatType();
    } else if ((slot == 13) && (stoner.getCombat().getCombatType() == CombatTypes.SAGITTARIUS)) {
      stoner.updateCombatType();
    }

    stoner
        .getClient()
        .queueOutgoingPacket(new SendEquipment(slot, items[slot].getId(), items[slot].getAmount()));
    stoner.getBox().update();
    stoner.setAppearanceUpdateRequired(true);
    stoner.getCombat().reset();

    if (item.getId() == 7927) {
      EasterRing.init(stoner);
    }

    if (item.getDefinition().getName() != "staff" && item.getDefinition().getName() != "wand") {}

    calculateBonuses();
  }

  public AssaultStyles getAssaultStyle() {
    return assaultStyle;
  }

  public void setAssaultStyle(AssaultStyles assaultStyle) {
    this.assaultStyle = assaultStyle;
  }

  public long getEffectedDamage(long hit) {
    Item shield = items[5];

    if ((shield != null)
        && (stoner.getGrades()[5] > 0)
        && ((shield.getId() == 12825) || (shield.getId() == 13740) || (shield.getId() == 13742))) {
      int reduction = (int) (hit * 0.3D);
      hit -= reduction;
      stoner.getProfession().deductFromGrade(5, reduction / 2);
    }

    return hit;
  }

  public int getEquipmentCount() {
    int am = 0;

    for (Item i : items) {
      if (i != null) {
        am++;
      }
    }
    return am;
  }

  public Item[] getItems() {
    return items;
  }

  public boolean isWearingItem(int id) {
    for (Item item : items) {

      if (item == null) continue;

      if (item.getId() == id) {
        return true;
      }
    }
    return false;
  }

  public boolean isWearingItem(int id, int slot) {
    return (items[slot] != null) && (items[slot].getId() == id);
  }

  public void onLogin() {
    stoner.setBonuses(new int[stoner.getBonuses().length]);

    for (int i = 0; i < 14; i++) {
      if (items[i] != null) {
        if (i == 3) {
          stoner.getSpecialAssault().onEquip();
        }

        stoner
            .getClient()
            .queueOutgoingPacket(new SendEquipment(i, items[i].getId(), items[i].getAmount()));
      } else {
        stoner.getClient().queueOutgoingPacket(new SendEquipment(i, 0, 0));
      }
    }

    updateStonerAnimations();
    updateBlockAnimation();
    updateSidebar();
    updateAssaultStyle();
    calculateBonuses();
    stoner.updateCombatType();
  }

  public boolean remove(Item item) {
    for (int i = 0; i < items.length; i++) {
      if (items[i] != null && items[i].getId() == item.getId()) {
        items[i] = null;
        update(i);
        calculateBonuses();
        return true;
      }
    }

    return false;
  }

  public void removeAll() {
    for (int i = 0; i < 14; i++) {
      items[i] = null;
    }
  }

  public boolean slotHasItem(int slot) {
    if ((slot > items.length) || (slot < 0)) {
      return false;
    }

    return items[slot] != null;
  }

  public boolean unequip(int slot) {
    if ((slot > items.length) || (slot < 0) || (items[slot] == null)) {
      return false;
    }

    if (!stoner.getController().canUnequip(stoner)) {
      return false;
    }

    if (!stoner.getBox().hasSpaceFor(items[slot])) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You do not have enough box space to unequip that."));
      return false;
    }

    stoner.getBox().add(items[slot]);

    items[slot] = null;

    if ((slot == 5) || ((slot == 3) && (items[5] == null))) {
      updateBlockAnimation();
    }

    if (slot == 3) {
      updateSidebar();
      updateStonerAnimations();
      updateAssaultStyle();
      stoner.getSpecialAssault().onEquip();
      stoner.getMage().getSpellCasting().disableAutocast();
      stoner.updateCombatType();
    } else if ((slot == 13) && (stoner.getCombat().getCombatType() == CombatTypes.SAGITTARIUS)) {
      stoner.updateCombatType();
    }

    stoner.getClient().queueOutgoingPacket(new SendEquipment(slot, -1, -1));

    stoner.setAppearanceUpdateRequired(true);

    calculateBonuses();
    return true;
  }

  public void update() {
    for (int i = 0; i < items.length; i++)
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendEquipment(
                  i,
                  items[i] != null ? items[i].getId() : 0,
                  items[i] != null ? items[i].getAmount() : 0));
  }

  public void update(int slot) {
    if ((slot > items.length) || (slot < 0)) {
      return;
    }

    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendEquipment(
                slot,
                items[slot] != null ? items[slot].getId() : 0,
                items[slot] != null ? items[slot].getAmount() : 0));
  }

  public void updateAssaultStyle() {
    int weaponId = items[3] != null ? items[3].getId() : 0;
    WeaponAssaultStyles currentStyle = EquipmentConstants.getWeaponAssaultStyle(weaponId);
    switch (currentStyle) {
      case ASSAULT_CONTROLLED_AEGIS:
        stoner.setAssaultType(AssaultType.SLASH);
        switch (assaultStyle) {
          case AGGRESSIVE:
            assaultStyle = AssaultStyles.CONTROLLED;
            break;
          case ACCURATE:
          case CONTROLLED:
          case DEFENSIVE:
            break;
        }
        break;
      case ASSAULT_VIGOUR_CONTROLLED_DEFENSE:
        switch (assaultStyle) {
          case CONTROLLED:
            stoner.setAssaultType(AssaultType.STAB);
            break;
          case ACCURATE:
          case AGGRESSIVE:
          case DEFENSIVE:
            stoner.setAssaultType(AssaultType.SLASH);
            break;
        }
        break;
      case ASSAULT_VIGOUR_DEFENSE:
        stoner.setAssaultType(AssaultType.CRUSH);
        switch (assaultStyle) {
          case CONTROLLED:
            assaultStyle = AssaultStyles.AGGRESSIVE;
            break;
          case ACCURATE:
          case AGGRESSIVE:
          case DEFENSIVE:
            break;
        }
        break;
      case ASSAULT_VIGOUR_VIGOUR_DEFENSE:
        stoner.setAssaultType(AssaultType.STAB);
        switch (assaultStyle) {
          case CONTROLLED:
            assaultStyle = AssaultStyles.AGGRESSIVE;
            break;
          case AGGRESSIVE:
          case ACCURATE:
          case DEFENSIVE:
            break;
        }
        break;
      case CONTROLLED_CONTROLLED_CONTROLLED_DEFENSE:
        stoner.setAssaultType(AssaultType.STAB);
        switch (assaultStyle) {
          case ACCURATE:
          case AGGRESSIVE:
            assaultStyle = AssaultStyles.CONTROLLED;
            break;
          case CONTROLLED:
          case DEFENSIVE:
            break;
        }
        break;
      case CONTROLLED_VIGOUR_DEFENSE:
        stoner.setAssaultType(AssaultType.STAB);
        switch (assaultStyle) {
          case ACCURATE:
            assaultStyle = AssaultStyles.AGGRESSIVE;
            break;
          case AGGRESSIVE:
            stoner.setAssaultType(AssaultType.SLASH);
            break;
          case CONTROLLED:
          case DEFENSIVE:
            break;
        }
        break;
    }
    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendConfig(43, EquipmentConstants.getAssaultStyleConfigId(weaponId, assaultStyle)));
  }

  public void updateBlockAnimation() {
    int block = 424;

    if (items[5] != null && items[5].getDefinition().getName().contains("defender")) {
      block = 4177;
    } else if (items[5] != null && !ignoreShieldEmote(items[5].getId())) {
      block = EquipmentConstants.getShieldBlockAnimation(items[5].getId());
    } else if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
      block = items[3].getWeaponDefinition().getBlock();
    }

    stoner.getCombat().setBlockAnimation(new Animation(block, 0));
  }

  public void updateBonusInterface() {
    int offset = 0;
    String text = "";
    short[] bonuses = stoner.getBonuses();
    for (int i = 0; i < 12; i++) {
      if (bonuses[i] >= 0) text = EquipmentConstants.BONUS_NAMES[i] + ": +" + bonuses[i];
      else {
        text = EquipmentConstants.BONUS_NAMES[i] + ": -" + Math.abs(bonuses[i]);
      }

      if (i == 10) {
        offset = 1;
      }

      stoner.getClient().queueOutgoingPacket(new SendString(text, 1675 + i + offset));
    }
  }

  public void updateMeleeDataForCombat() {
    int hitDelay = 1;
    int assaultSpeed;
    int assaultAnimation;
    if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
      assaultAnimation =
          items[3].getWeaponDefinition().getAssaultAnimations()[assaultStyle.ordinal()];
      assaultSpeed = items[3].getWeaponDefinition().getAssaultSpeeds()[assaultStyle.ordinal()];
    } else {
      assaultAnimation = Item.getWeaponDefinition(0).getAssaultAnimations()[assaultStyle.ordinal()];
      assaultSpeed = 5;
    }

    stoner
        .getCombat()
        .getMelee()
        .setAssault(new Assault(hitDelay, assaultSpeed), new Animation(assaultAnimation, 0));
  }

  public void updateStonerAnimations() {
    int stand = 808;
    int walk = 819;
    int run = 824;
    int standTurn = 823;
    int turn180 = 820;
    int turn90CW = 821;
    int turn90CCW = 822;

    if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
      WeaponDefinition def = items[3].getWeaponDefinition();
      stand = def.getStand();
      walk = def.getWalk();
      run = def.getRun();
      standTurn = def.getStandTurn();
      turn180 = def.getTurn180();
      turn90CW = def.getTurn90CW();
      turn90CCW = def.getTurn90CCW();
    }

    stoner.getAnimations().set(stand, standTurn, walk, turn180, turn90CW, turn90CCW, run);
  }

  public void updateSagittariusDataForCombat() {
    int hitDelay = 2;

    Projectile proj = SagittariusConstants.getProjectile(stoner);
    int assaultAnimation;
    int assaultSpeed;
    if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
      assaultAnimation =
          items[3].getWeaponDefinition().getAssaultAnimations()[assaultStyle.ordinal()];
      assaultSpeed = items[3].getWeaponDefinition().getAssaultSpeeds()[assaultStyle.ordinal()];

      if ((ItemCheck.isUsingCrossbow(stoner)) && (proj != null)) {
        proj.setCurve(0);
        proj.setStartHeight(40);
        proj.setEndHeight(40);
        proj.setDuration(30);
      }
    } else {
      assaultAnimation = Item.getWeaponDefinition(0).getAssaultAnimations()[assaultStyle.ordinal()];
      assaultSpeed = 2;
    }

    stoner.getCombat().getSagittarius().setProjectileOffset(0);

    if (ToxicBlowpipe.hasBlowpipe(stoner)) {
      Entity assaulting = stoner.getCombat().getAssaulting();
      if (assaulting != null) {
        if (assaulting.isNpc()) {
          assaultSpeed = 1;
        } else {
          assaultSpeed = 2;
        }
      }
      stoner
          .getCombat()
          .getSagittarius()
          .setAssault(
              new Assault(hitDelay, assaultSpeed),
              new Animation(assaultAnimation, 0),
              new Graphic(65535),
              null,
              proj);
    } else {
      stoner
          .getCombat()
          .getSagittarius()
          .setAssault(
              new Assault(hitDelay, assaultSpeed),
              new Animation(assaultAnimation, 0),
              SagittariusConstants.getDrawbackGraphic(stoner),
              SagittariusConstants.getEndGraphic(),
              proj);
    }
  }

  public void updateSidebar() {
    int interfaceId = 5855;
    int textId = 5857;

    Item weapon = items[3];

    if ((weapon != null) && (weapon.getWeaponDefinition() != null)) {
      interfaceId = weapon.getWeaponDefinition().getSidebarId();
      textId = EquipmentConstants.getTextIdForInterface(interfaceId);
    }

    stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(0, interfaceId));
    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendString(
                (weapon != null) && (weapon.getDefinition() != null)
                    ? weapon.getDefinition().getName()
                    : "Unarmed",
                textId));
  }

  public BigInteger getContainerNet() {
    BigInteger toReturn = BigInteger.ZERO;
    for (Item item : items) {
      if (item == null || item.getDefinition() == null) {
        continue;
      }
      toReturn =
          toReturn.add(
              new BigInteger(String.valueOf(item.getDefinition().getGeneralPrice()))
                  .multiply(new BigInteger(String.valueOf(item.getAmount()))));
    }
    return toReturn;
  }

  public enum AssaultStyles {
    ACCURATE,
    AGGRESSIVE,
    CONTROLLED,
    DEFENSIVE
  }
}
