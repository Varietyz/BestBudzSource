package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.UpdateFlags;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.pets.PetManager;
import java.util.BitSet;

public final class StonerUpdateFlags {

	private final BitSet set = new BitSet(30);
	private final boolean isActive = set.get(0);

	private final byte rights;
	private final int chatColor;
	private final int chatEffects;
	private final byte[] chatText;
	private final byte gender;
	private final int[] appearance;
	private final byte[] colors;
	private final short npcAppearanceId;
	private final byte primaryDirection;
	private final byte secondaryDirection;
	private final long hp;
	private final long maxHP;
	private final Location forceStart;
	private final Location forceEnd;
	private final short forceSpeed1;
	private final short forceSpeed2;
	private final byte forceDirection;
	private final short x;
	private final short y;
	private final short z;
	private final short regionX;
	private final short regionY;
	private final short regionZ;
	private final short[] equipment;
	private final short standEmote;
	private final short standTurnEmote;
	private final short walkEmote;
	private final short turn180Emote;
	private final short turn90CWEmote;
	private final short turn90CCWEmote;
	private final short runEmote;
	private final String username;
	private final int combatGrade;
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
	private final byte team;
	private final long usernameToLong;
	private final long hitUpdateCombatType;
	private final long hitUpdateCombatType2;
	private final StonerTitle stonerTitle;
	private final String displayName;

	public StonerUpdateFlags(Stoner stoner) {
		UpdateFlags u = stoner.getUpdateFlags();

		set.set(0, stoner.isVisible());
		set.set(1, stoner.isChatUpdateRequired());
		set.set(2, stoner.isAppearanceUpdateRequired());
		set.set(3, u.isUpdateRequired());
		set.set(4, u.isForceChatUpdate());
		set.set(5, u.isGraphicsUpdateRequired());
		set.set(6, u.isAnimationUpdateRequired());
		set.set(7, u.isHitUpdate());
		set.set(8, u.isHitUpdate2());
		set.set(9, u.isEntityFaceUpdate());
		set.set(10, u.isFaceToDirection());
		set.set(11, stoner.needsPlacement());
		set.set(12, stoner.isResetMovementQueue());
		set.set(13, u.isForceMovement());

		team = getTeam(stoner);

		x = ((short) stoner.getLocation().getX());
		y = ((short) stoner.getLocation().getY());
		z = ((short) stoner.getLocation().getZ());

		regionX = ((short) stoner.getCurrentRegion().getX());
		regionY = ((short) stoner.getCurrentRegion().getY());
		regionZ = ((short) stoner.getCurrentRegion().getZ());

		usernameToLong = stoner.getUsernameToLong();
		stonerTitle = stoner.getStonerTitle();

		if (stoner.isPet() && PetManager.isPetUsername(stoner.getUsername())) {
			// For pets: Use the display name that was set during pet creation (e.g., "PlayerName's Giant Eagle")
			displayName = stoner.getDisplay() != null ? stoner.getDisplay() : "Unknown Pet";
		} else {
			// For regular players and Discord bot: Use their username
			displayName = stoner.getUsername();
		}

		if (set.get(3)) {
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

			if (set.get(10)) {
				faceX = ((short) u.getFace().getX());
				faceY = ((short) u.getFace().getY());
			} else {
				faceX = 0;
				faceY = 0;
			}

			if ((set.get(7)) || (set.get(8))) {
				hp = (stoner.getGrades()[3]);
				maxHP = (stoner.getMaxGrades()[3]);
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

			if (set.get(1)) {
				chatText = stoner.getChatText();
				chatColor = stoner.getChatColor();
				chatEffects = stoner.getChatEffects();
			} else {
				chatText = null;
				chatColor = 0;
				chatEffects = 0;
			}

			entityFaceIndex = u.getEntityFaceIndex();

			if (set.get(13)) {
				forceStart = stoner.getMovementHandler().getForceStart();
				forceEnd = stoner.getMovementHandler().getForceEnd();
				forceSpeed1 = stoner.getMovementHandler().getForceSpeed1();
				forceSpeed2 = stoner.getMovementHandler().getForceSpeed2();
				forceDirection = stoner.getMovementHandler().getForceDirection();
			} else {
				forceStart = null;
				forceEnd = null;
				forceSpeed1 = 0;
				forceSpeed2 = 0;
				forceDirection = 0;
			}
		} else {
			animationId = 0;
			animationDelay = 0;
			graphicId = 0;
			graphicHeight = 0;
			graphicDelay = 0;
			forceStart = null;
			forceEnd = null;
			forceSpeed1 = 0;
			forceSpeed2 = 0;
			forceDirection = 0;
			faceX = 0;
			faceY = 0;
			hp = 0;
			maxHP = 0;
			damage = 0;
			damage2 = 0;
			hitType = 0;
			hitType2 = 0;
			forceChatMessage = null;
			entityFaceIndex = 0;
			chatText = null;
			chatColor = 0;
			chatEffects = 0;
			hitUpdateCombatType = 0;
			hitUpdateCombatType2 = 0;
		}

		primaryDirection = ((byte) stoner.getMovementHandler().getPrimaryDirection());
		secondaryDirection = ((byte) stoner.getMovementHandler().getSecondaryDirection());

		equipment = new short[14];
		for (int i = 0; i < equipment.length; i++) {
			if (stoner.getEquipment().getItems()[i] != null) {
				equipment[i] = ((short) stoner.getEquipment().getItems()[i].getId());
			}
		}

		npcAppearanceId = ((short) stoner.getNpcAppearanceId());

		rights = ((byte) stoner.getRights());
		combatGrade = (stoner.getProfession().calcCombatGrade());

		standEmote = ((short) stoner.getAnimations().getStandEmote());
		runEmote = ((short) stoner.getAnimations().getRunEmote());
		standTurnEmote = ((short) stoner.getAnimations().getStandTurnEmote());
		walkEmote = ((short) stoner.getAnimations().getWalkEmote());
		turn180Emote = ((short) stoner.getAnimations().getTurn180Emote());
		turn90CWEmote = ((short) stoner.getAnimations().getTurn90CWEmote());
		turn90CCWEmote = ((short) stoner.getAnimations().getTurn90CCWEmote());

		// CRITICAL FIX: Use UNIQUE identifier for update comparison, but display name for appearance
		// This ensures each entity (pet, Discord bot, regular player) has a unique update identifier
		if (stoner.isPet() && PetManager.isPetUsername(stoner.getUsername())) {
			// For pets: Use the unique generated username for update system (internal tracking)
			// This includes timestamps and owner info making it truly unique
			username = stoner.getUsername(); // e.g., "Pet_GIANT_EAGLE_PlayerName_1234567890"
		} else {
			// For regular players and Discord bot: Use their actual username
			username = stoner.getUsername();
		}

		gender = stoner.getGender();

		colors = new byte[stoner.getColors().length];
		System.arraycopy(stoner.getColors(), 0, colors, 0, colors.length);

		appearance = new int[stoner.getAppearance().length];
		System.arraycopy(stoner.getAppearance(), 0, appearance, 0, appearance.length);
	}

	public static byte getTeam(Stoner p) {
		Item cape = p.getEquipment().getItems()[1];

		if ((cape != null) && (cape.getId() >= 4315) && (cape.getId() <= 4413)) {
			return (byte) (cape.getId() - 4315 + 1);
		}

		return 0;
	}

	// ... (rest of the getter methods remain the same)
	public byte getAnimationDelay() {
		return animationDelay;
	}

	public short getAnimationId() {
		return animationId;
	}

	public int[] getAppearance() {
		return appearance;
	}

	public int getChatColor() {
		return chatColor;
	}

	public int getChatEffects() {
		return chatEffects;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public byte[] getColors() {
		return colors;
	}

	public int getCombatGrade() {
		return combatGrade;
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

	public short[] getEquipment() {
		return equipment;
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

	public int getGender() {
		return gender;
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

	public int getHeadicon() {
		return 15;
	}

	public long getHitType() {
		return hitType;
	}

	public long getHitUpdateType() {
		return hitUpdateCombatType;
	}

	public long getHitUpdateType2() {
		return hitUpdateCombatType2;
	}

	public long getHitType2() {
		return hitType2;
	}

	public long getHp() {
		return hp;
	}

	public Location getLocation() {
		return new Location(x, y, z);
	}

	public long getMaxHP() {
		return maxHP;
	}

	public int getNpcAppearanceId() {
		return npcAppearanceId;
	}

	public int getPrimaryDirection() {
		return primaryDirection;
	}

	public Location getRegion() {
		return new Location(regionX, regionY, regionZ);
	}

	public short getRegionX() {
		return regionX;
	}

	public short getRegionY() {
		return regionY;
	}

	public short getRegionZ() {
		return regionZ;
	}

	public int getRights() {
		return rights;
	}

	public int getRunEmote() {
		return runEmote;
	}

	public int getSecondaryDirection() {
		return secondaryDirection;
	}

	public BitSet getSet() {
		return set;
	}

	public int getStandEmote() {
		return standEmote;
	}

	public int getStandTurnEmote() {
		return standTurnEmote;
	}

	public byte getTeam() {
		return team;
	}

	public int getTurn180Emote() {
		return turn180Emote;
	}

	public int getTurn90CCWEmote() {
		return turn90CCWEmote;
	}

	public int getTurn90CWEmote() {
		return turn90CWEmote;
	}

	public String getUsername() {
		return username;
	}

	public long getUsernameToLong() {
		return usernameToLong;
	}

	public int getWalkEmote() {
		return walkEmote;
	}

	public Location getForceStart() {
		return forceStart;
	}

	public Location getForceEnd() {
		return forceEnd;
	}

	public short getForceSpeed1() {
		return forceSpeed1;
	}

	public short getForceSpeed2() {
		return forceSpeed2;
	}

	public byte getForceDirection() {
		return forceDirection;
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

	public boolean isActive() {
		return set.get(0);
	}

	public boolean isAnimationUpdateRequired() {
		return set.get(6);
	}

	public boolean isAppearanceUpdateRequired() {
		return set.get(2);
	}

	public boolean isChatUpdateRequired() {
		return set.get(1);
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
		return set.get(11);
	}

	public boolean isResetMovementQueue() {
		return set.get(12);
	}

	public boolean isUpdateRequired() {
		return set.get(3);
	}

	public boolean isVisible() {
		return set.get(0);
	}

	public StonerTitle getStonerTitle() {
		return stonerTitle;
	}

	public boolean isForceMoveMask() {
		return set.get(13);
	}
	public String getDisplayName() {
		return displayName;
	}

}