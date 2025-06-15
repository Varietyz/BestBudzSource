package com.bestbudz.rs2.entity;

public class UpdateFlags {
  protected boolean isUpdateRequired;
  protected boolean isForceChatUpdate;
  protected String forceChatMessage;
  protected boolean graphicsUpdateRequired;
  protected boolean animationUpdateRequired;
  protected int animationId;
  protected int animationDelay;
  protected boolean entityFaceUpdate;
  protected int entityFaceIndex = -1;
  protected boolean faceToDirection;
  protected Location face;
  protected boolean hitUpdate;
  protected boolean hitUpdate2;
  protected boolean forceMovement;
  protected int hitUpdateCombatType = 1;
  protected int hitUpdateCombatType2 = 1;
  protected long damage;
  protected long damage2;
  protected int hitType;
  protected int hitType2;
  protected Graphic graphic;

  public void faceEntity(int entityFaceIndex) {
    this.entityFaceIndex = entityFaceIndex;
    entityFaceUpdate = true;
    isUpdateRequired = true;
  }

  public int getAnimationDelay() {
    return animationDelay;
  }

  public void setAnimationDelay(int animationDelay) {
    this.animationDelay = animationDelay;
  }

  public int getAnimationId() {
    return animationId;
  }

  public void setAnimationId(int animationId) {
    this.animationId = animationId;
  }

  public long getDamage() {
    return damage;
  }

  public void setDamage(long damage) {
    this.damage = damage;
  }

  public long getDamage2() {
    return damage2;
  }

  public void setDamage2(long damage2) {
    this.damage2 = damage2;
  }

  public int getEntityFaceIndex() {
    return entityFaceIndex;
  }

  public void setEntityFaceIndex(int entityFaceIndex) {
    this.entityFaceIndex = entityFaceIndex;
  }

  public Location getFace() {
    return face;
  }

  public void setFace(Location face) {
    this.face = face;
  }

  public String getForceChatMessage() {
    return forceChatMessage;
  }

  public void setForceChatMessage(String forceChatMessage) {
    this.forceChatMessage = forceChatMessage;
  }

  public Graphic getGraphic() {
    return graphic;
  }

  public int getHitType() {
    return hitType;
  }

  public void setHitType(int hitType) {
    this.hitType = hitType;
  }

  public int getHitType2() {
    return hitType2;
  }

  public void setHitType2(int hitType2) {
    this.hitType2 = hitType2;
  }

  public int getHitUpdateCombatType() {
    return hitUpdateCombatType;
  }

  public void setHitUpdateCombatType(int hitUpdateCombatType) {
    this.hitUpdateCombatType = hitUpdateCombatType;
  }

  public int getHitUpdateCombatType2() {
    return hitUpdateCombatType2;
  }

  public void setHitUpdateCombatType2(int hitUpdateCombatType2) {
    this.hitUpdateCombatType2 = hitUpdateCombatType2;
  }

  public boolean isAnimationUpdateRequired() {
    return animationUpdateRequired;
  }

  public void setAnimationUpdateRequired(boolean animationUpdateRequired) {
    this.animationUpdateRequired = animationUpdateRequired;
  }

  public boolean isEntityFaceUpdate() {
    return entityFaceUpdate;
  }

  public void setEntityFaceUpdate(boolean entityFaceUpdate) {
    this.entityFaceUpdate = entityFaceUpdate;
  }

  public boolean isFaceToDirection() {
    return faceToDirection;
  }

  public void setFaceToDirection(boolean faceToDirection) {
    this.faceToDirection = faceToDirection;
  }

  public boolean isForceChatUpdate() {
    return isForceChatUpdate;
  }

  public void setForceChatUpdate(boolean isForceChatUpdate) {
    this.isForceChatUpdate = isForceChatUpdate;
  }

  public boolean isGraphicsUpdateRequired() {
    return graphicsUpdateRequired;
  }

  public void setGraphicsUpdateRequired(boolean graphicsUpdateRequired) {
    this.graphicsUpdateRequired = graphicsUpdateRequired;
  }

  public boolean isHitUpdate() {
    return hitUpdate;
  }

  public void setHitUpdate(boolean hitUpdate) {
    this.hitUpdate = hitUpdate;
  }

  public boolean isHitUpdate2() {
    return hitUpdate2;
  }

  public void setHitUpdate2(boolean hitUpdate2) {
    this.hitUpdate2 = hitUpdate2;
  }

  public boolean isUpdateRequired() {
    return isUpdateRequired;
  }

  public void setUpdateRequired(boolean isUpdateRequired) {
    this.isUpdateRequired = isUpdateRequired;
  }

  public boolean isForceMovement() {
    return forceMovement;
  }

  public void setForceMovement(boolean forceMovement) {
    this.forceMovement = forceMovement;
  }

	public void reset() {
		if (graphicsUpdateRequired) graphicsUpdateRequired = false;
		if (animationUpdateRequired) {
			animationUpdateRequired = false;
			animationId = -1;  // Reset to "no animation"
			animationDelay = 0;
		}
		if (faceToDirection) faceToDirection = false;
		if (hitUpdate) hitUpdate = false;
		if (hitUpdate2) hitUpdate2 = false;
		if (graphic != null) graphic = null;
		if (isForceChatUpdate) isForceChatUpdate = false;
		if (forceMovement) forceMovement = false;

		// FIX: Reset face index without triggering updates
		if (entityFaceIndex != 65535) {
			entityFaceIndex = 65535;
			entityFaceUpdate = false;  // Don't trigger update during reset
		}
	}

  public void sendAnimation(Animation animation) {
    animationId = animation.getId();
    animationDelay = animation.getDelay();
    animationUpdateRequired = true;
    isUpdateRequired = true;
  }

  public void sendAnimation(int animationId, int animationDelay) {
    this.animationId = animationId;
    this.animationDelay = animationDelay;
    animationUpdateRequired = true;
    isUpdateRequired = true;
  }

  public void sendFaceToDirection(int x, int y) {
    face = new Location(x, y);
    faceToDirection = true;
    isUpdateRequired = true;
  }

  public void sendFaceToDirection(Location face) {
    this.face = face;
    faceToDirection = true;
    isUpdateRequired = true;
  }

  public void sendForceMessage(String forceChatMessage) {
    this.forceChatMessage = forceChatMessage;
    isForceChatUpdate = true;
    isUpdateRequired = true;
  }

  public void sendGraphic(Graphic graphic) {
    this.graphic = graphic;
    graphicsUpdateRequired = true;
    isUpdateRequired = true;
  }

  public void sendHit(long damage, int hitType, int hitUpdateCombatType) {
    this.damage = damage;
    this.hitType = hitType;
    hitUpdate = true;
    isUpdateRequired = true;
    this.hitUpdateCombatType = hitUpdateCombatType;
  }

  public void sendHit2(long damage, int hitType, int hitUpdateCombatType2) {
    damage2 = damage;
    hitType2 = hitType;
    hitUpdate2 = true;
    isUpdateRequired = true;
    this.hitUpdateCombatType2 = hitUpdateCombatType2;
  }

	public void faceDirection(int direction) {
		this.entityFaceIndex = direction;
		this.entityFaceUpdate = true;
		this.isUpdateRequired = true;
	}


}
