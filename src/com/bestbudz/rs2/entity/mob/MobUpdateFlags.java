package com.bestbudz.rs2.entity.mob;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.UpdateFlags;
import java.util.BitSet;

public final class MobUpdateFlags {
  private final BitSet set = new BitSet(12);
  private final short transformId;
  private final byte primaryDirection;
  private final long hp;
  private final long maxHP;
  private final short x;
  private final short y;
  private final short z;
  private final short id;
  private final byte faceDir;
  private final String forceChatMessage;
  private final short animationId;
  private final byte animationDelay;
  private final int entityFaceIndex;
  private final short faceX;
  private final short faceY;
  private final long damage;
  private final long damage2;
  private final long hitType;
  private final long hitType2;
  private final short graphicId;
  private final byte graphicHeight;
  private final byte graphicDelay;
  private final long hitUpdateCombatType;
  private final long hitUpdateCombatType2;

  public MobUpdateFlags(Mob mob) {
    UpdateFlags u = mob.getUpdateFlags();

    set.set(0, mob.isVisible());
    set.set(1, mob.isTransformUpdate());
    set.set(2, mob.isPlacement());
    set.set(3, u.isUpdateRequired());
    set.set(4, u.isForceChatUpdate());
    set.set(5, u.isGraphicsUpdateRequired());
    set.set(6, u.isAnimationUpdateRequired());
    set.set(7, u.isHitUpdate());
    set.set(8, u.isHitUpdate2());
    set.set(9, u.isEntityFaceUpdate());
    set.set(10, u.isFaceToDirection());

    if (set.get(6)) {
      animationId = ((short) u.getAnimationId());
      animationDelay = ((byte) u.getAnimationDelay());
    } else {
      animationId = 0;
      animationDelay = 0;
    }

    if (set.get(5)) {
      graphicId = ((short) u.getGraphic().getId());
      graphicHeight = ((byte) u.getGraphic().getHeight());
      graphicDelay = ((byte) u.getGraphic().getDelay());
    } else {
      graphicId = 0;
      graphicHeight = 0;
      graphicDelay = 0;
    }

    if (set.get(1)) transformId = ((short) mob.getTransformId());
    else {
      transformId = 0;
    }

    if (set.get(10)) {
      faceX = ((short) u.getFace().getX());
      faceY = ((short) u.getFace().getY());
    } else {
      faceX = 0;
      faceY = 0;
    }

    if ((set.get(7)) || (set.get(8))) {
      hp = mob.getGrades()[3];
      maxHP = mob.getMaxGrades()[3];
      damage = (u.getDamage());
      damage2 = (u.getDamage2());
      hitType = (u.getHitType());
      hitType2 = (u.getHitType2());
      hitUpdateCombatType = u.getHitUpdateCombatType();
      hitUpdateCombatType2 = u.getHitUpdateCombatType2();
    } else {
      hp = 0;
      maxHP = 0;
      damage = 0;
      damage2 = 0;
      hitType = 0;
      hitType2 = 0;
      hitUpdateCombatType = 0;
      hitUpdateCombatType2 = 0;
    }

    if (set.get(4)) forceChatMessage = u.getForceChatMessage();
    else {
      forceChatMessage = null;
    }

    primaryDirection = ((byte) mob.getMovementHandler().getPrimaryDirection());
    x = ((short) mob.getLocation().getX());
    y = ((short) mob.getLocation().getY());
    z = ((short) mob.getLocation().getZ());
    id = ((short) mob.getId());
    faceDir = ((byte) mob.getFaceDirection());
    entityFaceIndex = u.getEntityFaceIndex();
  }

  public byte getAnimationDelay() {
    return animationDelay;
  }

  public short getAnimationId() {
    return animationId;
  }

  public long getDamage() {
    return damage;
  }

  public long getDamage2() {
    return damage2;
  }

  public int getEntityFaceIndex() {
    return entityFaceIndex;
  }

  public int getFaceDirection() {
    return faceDir;
  }

  public Location getFaceLocation() {
    return new Location(faceX, faceY);
  }

  public short getFaceX() {
    return faceX;
  }

  public short getFaceY() {
    return faceY;
  }

  public String getForceChatMessage() {
    return forceChatMessage;
  }

  public byte getGraphicDelay() {
    return graphicDelay;
  }

  public byte getGraphicHeight() {
    return graphicHeight;
  }

  public short getGraphicId() {
    return graphicId;
  }

  public long getHitType() {
    return hitType;
  }

  public long getHitType2() {
    return hitType2;
  }

  public long getHp() {
    return hp;
  }

  public int getId() {
    return id;
  }

  public Location getLocation() {
    return new Location(x, y, z);
  }

  public long getMaxHP() {
    return maxHP;
  }

  public int getPrimaryDirection() {
    return primaryDirection;
  }

  public int getTransformId() {
    return transformId;
  }

  public short getX() {
    return x;
  }

  public short getY() {
    return y;
  }

  public short getZ() {
    return z;
  }

  public long getHitUpdateType() {
    return hitUpdateCombatType;
  }

  public long getHitUpdateType2() {
    return hitUpdateCombatType2;
  }

  public boolean isAnimationUpdateRequired() {
    return set.get(6);
  }

  public boolean isEntityFaceUpdate() {
    return set.get(9);
  }

  public boolean isFaceToDirection() {
    return set.get(10);
  }

  public boolean isForceChatUpdate() {
    return set.get(4);
  }

  public boolean isGraphicsUpdateRequired() {
    return set.get(5);
  }

  public boolean isHitUpdate() {
    return set.get(7);
  }

  public boolean isHitUpdate2() {
    return set.get(8);
  }

  public boolean isPlacement() {
    return set.get(2);
  }

  public boolean isTransformUpdate() {
    return set.get(1);
  }

  public boolean isUpdateRequired() {
    return true;
  }

  public boolean isVisible() {
    return set.get(0);
  }
}
