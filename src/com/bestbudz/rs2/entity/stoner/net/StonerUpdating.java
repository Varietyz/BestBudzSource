package com.bestbudz.rs2.entity.stoner.net;

import static com.bestbudz.Server.logger;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.BOOTS_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.CAPE_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.GLOVES_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.HELM_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.LEGS_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.NECKLACE_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.SHIELD_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.TORSO_SLOT;
import static com.bestbudz.rs2.entity.item.EquipmentConstants.WEAPON_SLOT;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.network.StreamBuffer.ByteOrder;
import com.bestbudz.core.network.StreamBuffer.OutBuffer;
import com.bestbudz.core.network.StreamBuffer.ValueType;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerUpdateFlags;
import com.bestbudz.rs2.entity.pets.PetManager;
import java.util.Iterator;
import java.util.Objects;

public final class StonerUpdating {

	public static void addStoner(
		OutBuffer out, StonerUpdateFlags local, StonerUpdateFlags other, int index) {
		out.writeBits(11, index);
		out.writeBit(true); // placement required
		out.writeBit(true); // update block required

		Location delta = Utility.delta(local.getLocation(), other.getLocation());
		out.writeBits(5, delta.getY());
		out.writeBits(5, delta.getX());
	}

	public static void appendAppearance(StonerUpdateFlags flags, OutBuffer out) {
		OutBuffer block = StreamBuffer.newOutBuffer(128);

		block.writeByte(flags.getGender());
		block.writeByte(flags.getHeadicon());
		block.writeByte(flags.getSkullIcon());
		block.writeByte(flags.getRights());

		short[] equip = flags.getEquipment();
		int[] look = flags.getAppearance();

		if (flags.getNpcAppearanceId() == -1) {
			writeLookSlot(block, equip[HELM_SLOT], 0x200, 0);
			writeLookSlot(block, equip[CAPE_SLOT], 0x200, 0);
			writeLookSlot(block, equip[NECKLACE_SLOT], 0x200, 0);
			writeLookSlot(block, equip[WEAPON_SLOT], 0x200, 0);

			if (equip[TORSO_SLOT] != 0) block.writeShort(0x200 + equip[TORSO_SLOT]);
			else block.writeShort(0x100 + look[1]);

			writeLookSlot(block, equip[SHIELD_SLOT], 0x200, 0);

			if (equip[TORSO_SLOT] != 0 && EquipmentConstants.isFullBody(equip[TORSO_SLOT])) {
				block.writeByte(0);
			} else {
				block.writeShort(0x100 + look[2]);
			}

			if (equip[LEGS_SLOT] != 0) block.writeShort(0x200 + equip[LEGS_SLOT]);
			else block.writeShort(0x100 + look[4]);

			if (equip[HELM_SLOT] != 0
				&& (EquipmentConstants.isFullHelm(equip[HELM_SLOT])
				|| EquipmentConstants.isFullMask(equip[HELM_SLOT]))) {
				block.writeByte(0);
			} else if (EquipmentConstants.isForceNewHair(equip[HELM_SLOT])) {
				block.writeShort(259);
			} else {
				block.writeShort(0x100 + look[0]);
			}

			if (equip[GLOVES_SLOT] != 0) block.writeShort(0x200 + equip[GLOVES_SLOT]);
			else block.writeShort(0x100 + look[3]);

			if (equip[BOOTS_SLOT] != 0) block.writeShort(0x200 + equip[BOOTS_SLOT]);
			else block.writeShort(0x100 + look[5]);

			if (flags.getGender() == 1 || EquipmentConstants.isFullMask(equip[HELM_SLOT])) {
				block.writeByte(0);
			} else {
				block.writeShort(0x100 + look[6]);
			}
		} else {
			block.writeShort(-1);
			block.writeShort(flags.getNpcAppearanceId());
		}

		for (int i = 0; i < 5; i++) {
			block.writeByte(flags.getColors()[i]);
		}

		block.writeShort(flags.getStandEmote());
		block.writeShort(flags.getStandTurnEmote());
		block.writeShort(flags.getWalkEmote());
		block.writeShort(flags.getTurn180Emote());
		block.writeShort(flags.getTurn90CWEmote());
		block.writeShort(flags.getTurn90CCWEmote());
		block.writeShort(flags.getRunEmote());

		// FIXED: Use the captured display name
		block.writeString(flags.getDisplayName());

		if (flags.getStonerTitle() == null) {
			block.writeString("0");
			block.writeString("");
			block.writeByte(0);
		} else {
			block.writeString(Integer.toHexString(flags.getStonerTitle().getColor()));
			block.writeString(flags.getStonerTitle().getTitle());
			block.writeByte(flags.getStonerTitle().isSuffix() ? 1 : 0);
		}

		block.writeInt(flags.getCombatGrade(), ByteOrder.BIG);
		block.writeShort(0);

		out.writeByte(block.getBuffer().writerIndex(), ValueType.C);
		out.writeBytes(block.getBuffer());
	}

	/**
	 * CRITICAL FIX: Get proper display name for appearance block
	 * This determines what name players actually see
	 */
	private static String getDisplayNameForAppearance(StonerUpdateFlags flags) {
		String username = flags.getUsername();

		// Check if this is a pet by username format
		if (PetManager.isPetUsername(username)) {
			// For pets, extract and format the display name
			com.bestbudz.rs2.entity.pets.PetData petData = PetManager.getPetDataFromUsername(username);
			if (petData != null) {
				return PetManager.formatPetDisplayName(petData);
			}
			return "Unknown Pet";
		}

		// For regular players and Discord bot, use the username as-is
		return username;
	}

	private static void writeLookSlot(OutBuffer block, int itemId, int base, int fallback) {
		if (itemId != 0) {
			block.writeShort(base + itemId);
		} else {
			block.writeByte(fallback);
		}
	}

	public static void appendChat(StonerUpdateFlags flags, StreamBuffer.OutBuffer out) {
		// Add null check for chat text
		byte[] chatText = flags.getChatText();
		if (chatText == null) {
			// If chat text is null, don't write anything or write empty chat
			logger.warning("Chat text is null for user: " + flags.getUsername());
			return; // or write empty chat data if needed
		}

		int colorAndEffect = ((flags.getChatColor() & 0xFF) << 8) | (flags.getChatEffects() & 0xFF);
		out.writeShort(colorAndEffect, ByteOrder.LITTLE);
		out.writeByte(flags.getRights());
		out.writeByte(chatText.length, ValueType.C);
		out.writeBytesReverse(chatText);
	}

	public static void appendPlacement(
		OutBuffer out,
		int localX,
		int localY,
		int z,
		boolean discardMovementQueue,
		boolean attributesUpdate) {

		out.writeBits(2, 3); // Placement update
		out.writeBits(2, z);
		out.writeBit(discardMovementQueue);
		out.writeBit(attributesUpdate);
		out.writeBits(7, localY);
		out.writeBits(7, localX);
	}

	public static void appendRun(
		OutBuffer out, int direction, int direction2, boolean attributesUpdate) {
		out.writeBits(2, 2); // Run update
		out.writeBits(3, direction);
		out.writeBits(3, direction2);
		out.writeBit(attributesUpdate);
	}

	public static void appendStand(StreamBuffer.OutBuffer out) {
		out.writeBits(2, 0);
	}

	public static void appendWalk(OutBuffer out, int direction, boolean attributesUpdate) {
		out.writeBits(2, 1); // Walk update
		out.writeBits(3, direction);
		out.writeBit(attributesUpdate);
	}

	/**
	 * CRITICAL FIX: Check if local list contains stoner using UNIQUE username comparison
	 * This ensures proper identification of each entity (pets, Discord bot, regular players)
	 */
	public static boolean doesLocalListContainStoner(Stoner local, long usernameToLong) {
		if (local == null || local.getStoners() == null) {
			return false;
		}

		for (Stoner p : local.getStoners()) {
			if (p != null && p.getUsernameToLong() == usernameToLong) {
				return true;
			}
		}
		return false;
	}

	public static void update(Stoner stoner, StonerUpdateFlags[] pFlags) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(12228);
		StreamBuffer.OutBuffer block = StreamBuffer.newOutBuffer(8192);

		out.writeVariableShortPacketHeader(stoner.getClient().getEncryptor(), 81);
		out.setAccessType(StreamBuffer.AccessType.BIT_ACCESS);

		final int index = stoner.getIndex();
		final StonerUpdateFlags local = pFlags[index];
		final Location localLoc = local.getLocation();
		final var stoners = stoner.getStoners();

		updateLocalStonerMovement(local, out);

		if (local.isUpdateRequired()) {
			updateState(local, block, false, true);
		}

		out.writeBits(8, stoners.size());

		// CRITICAL FIX: Process existing stoners in local list
		for (Iterator<Stoner> it = stoners.iterator(); it.hasNext(); ) {
			Stoner target = it.next();

			// CRITICAL FIX: Null check and bounds check
			if (target == null || target.getIndex() < 0 || target.getIndex() >= pFlags.length) {
				it.remove();
				out.writeBit(true);
				out.writeBits(2, 3); // remove from local list
				continue;
			}

			StonerUpdateFlags flags = pFlags[target.getIndex()];

			if (flags != null
				&& flags.isActive()
				&& flags.isVisible()
				&& !flags.isPlacement()
				&& flags.getLocation().isViewableFrom(localLoc)) {

				updateOtherStonerMovement(flags, out);
				if (flags.isUpdateRequired()) {
					boolean ignoreChat = stoner.getPrivateMessaging().ignored(getOriginalUsernameForIgnoreCheck(flags));
					updateState(flags, block, false, ignoreChat);
				}
			} else {
				out.writeBit(true);
				out.writeBits(2, 3); // remove from local list
				it.remove();
			}
		}

		// CRITICAL FIX: Add new stoners to local list with better logic
		int added = 0;
		final int maxLocalStoners = 255;
		final int maxAddPerCycle = 15;

		for (int i = 0; i < World.getStoners().length && stoners.size() < maxLocalStoners && added < maxAddPerCycle; i++) {
			Stoner other = World.getStoners()[i];

			// Skip if null, same stoner, or inactive
			if (other == null || other == stoner || !other.isActive()) {
				continue;
			}

			// Skip if already in local list (more efficient check)
			if (stoners.contains(other)) {
				continue;
			}

			// Bounds check for pFlags
			if (i >= pFlags.length) {
				continue;
			}

			StonerUpdateFlags flags = pFlags[i];
			if (flags != null
				&& flags.isActive()
				&& flags.isVisible()
				&& flags.getLocation().isViewableFrom(localLoc)) {

				// CRITICAL FIX: Add debug logging for Discord bot and pets
				if (World.isDiscordBot(other) || World.isPet(other)) {
					System.out.println("VISIBILITY: Player " + stoner.getUsername() +
						" attempting to add " + other.getUsername() +
						" (Type: " + (World.isDiscordBot(other) ? "DISCORD_BOT" : "PET") +
						", Index: " + other.getIndex() +
						", UsernameToLong: " + other.getUsernameToLong() + ")");
				}

				stoners.add(other);
				addStoner(out, local, flags, i);

				boolean ignoreChat = stoner.getPrivateMessaging().ignored(getOriginalUsernameForIgnoreCheck(flags));
				updateState(flags, block, true, ignoreChat);
				added++;

				// Success debug
				if (World.isDiscordBot(other) || World.isPet(other)) {
					System.out.println("VISIBILITY: Successfully added " + other.getUsername() + " to " + stoner.getUsername() + "'s local list");
				}
			}
		}

		if (block.getBuffer().writerIndex() > 0) {
			out.writeBits(11, 2047);
			out.setAccessType(StreamBuffer.AccessType.BYTE_ACCESS);
			out.writeBytes(block.getBuffer());
		} else {
			out.setAccessType(StreamBuffer.AccessType.BYTE_ACCESS);
		}

		out.finishVariableShortPacketHeader();
		stoner.getClient().send(out.getBuffer());
	}

	private static boolean shouldRemoveFromLocalList(Stoner viewer, Stoner target, StonerUpdateFlags flags, Location viewerLoc) {
		if (target == null || flags == null) {
			return true; // Remove null entries
		}

		if (!flags.isActive() || !flags.isVisible()) {
			return true; // Remove inactive/invisible
		}

		if (!flags.getLocation().isViewableFrom(viewerLoc)) {
			return true; // Remove if out of view range
		}

		// Don't remove pets or Discord bot unless they're actually gone
		if (World.isDiscordBot(target) || World.isPet(target)) {
			// Keep them in the list as long as they're active and in range
			return false;
		}

		return false; // Keep in list
	}

	/**
	 * CRITICAL FIX: Get original username for ignore checks
	 * For pets, we need to check the owner's name, not the pet's internal name
	 */
	private static String getOriginalUsernameForIgnoreCheck(StonerUpdateFlags flags) {
		String username = flags.getUsername();

		// For pets, extract owner name for ignore checks
		if (PetManager.isPetUsername(username)) {
			// Pet usernames are formatted as "Pet_PETTYPE_OWNERNAME_timestamp"
			String[] parts = username.split("_");
			if (parts.length >= 4) {
				// Extract owner name (3rd part)
				return parts[2];
			}
		}

		// For regular players and Discord bot
		return username;
	}

	public static void updateLocalStonerMovement(
		StonerUpdateFlags flags, StreamBuffer.OutBuffer out) {
		boolean updateRequired = flags.isUpdateRequired();

		if (flags.isPlacement()) {
			out.writeBit(true);
			Location loc = flags.getLocation();
			int posX = loc.getLocalX(flags.getRegion());
			int posY = loc.getLocalY(flags.getRegion());
			appendPlacement(out, posX, posY, loc.getZ(), flags.isResetMovementQueue(), updateRequired);
			return;
		}

		int pDir = flags.getPrimaryDirection();
		int sDir = flags.getSecondaryDirection();

		if (pDir != -1) {
			out.writeBit(true);
			if (sDir != -1) {
				appendRun(out, pDir, sDir, updateRequired);
			} else {
				appendWalk(out, pDir, updateRequired);
			}
		} else if (updateRequired) {
			out.writeBit(true);
			appendStand(out);
		} else {
			out.writeBit(false);
		}
	}

	public static void updateOtherStonerMovement(
		StonerUpdateFlags flags, StreamBuffer.OutBuffer out) {
		int pDir = flags.getPrimaryDirection();
		int sDir = flags.getSecondaryDirection();

		if (pDir == -1) {
			if (flags.isUpdateRequired()) {
				out.writeBit(true);
				appendStand(out);
			} else {
				out.writeBit(false);
			}
			return;
		}

		out.writeBit(true);
		if (sDir != -1) {
			appendRun(out, pDir, sDir, flags.isUpdateRequired());
		} else {
			appendWalk(out, pDir, flags.isUpdateRequired());
		}
	}

	public static void main(String[] args) {
		System.out.println(0x40);
	}

	public static void updateState(
		StonerUpdateFlags flags,
		StreamBuffer.OutBuffer block,
		boolean forceAppearance,
		boolean noChat) {

		// Precompute flag states to avoid duplicate method calls
		final boolean forceMove = flags.isForceMoveMask();
		final boolean graphics = flags.isGraphicsUpdateRequired();
		final boolean animation = flags.isAnimationUpdateRequired();
		final boolean forceChat = flags.isForceChatUpdate();
		final boolean chat = flags.isChatUpdateRequired() && !noChat;
		final boolean entityFace = flags.isEntityFaceUpdate();
		final boolean appearance = flags.isAppearanceUpdateRequired() || forceAppearance;
		final boolean faceDir = flags.isFaceToDirection();
		final boolean hit1 = flags.isHitUpdate();
		final boolean hit2 = flags.isHitUpdate2();

		int mask = 0;
		if (forceMove) mask |= 0x400;
		if (graphics) mask |= 0x100;
		if (animation) mask |= 0x8;
		if (forceChat) mask |= 0x4;
		if (chat) mask |= 0x80;
		if (entityFace) mask |= 0x1;
		if (appearance) mask |= 0x10;
		if (faceDir) mask |= 0x2;
		if (hit1) mask |= 0x20;
		if (hit2) mask |= 0x200;

		if (mask >= 0x100) {
			mask |= 0x40;
			block.writeShort(mask, ByteOrder.LITTLE);
		} else {
			block.writeByte(mask);
		}

		// Only write blocks that are flagged for update
		if (forceMove) {
			appendForceMovement(flags, block);
		}
		if (graphics) {
			block.writeShort(flags.getGraphicId(), ByteOrder.LITTLE);
			block.writeInt(flags.getGraphicDelay() | (flags.getGraphicHeight() << 16));
		}
		if (animation) {
			block.writeShort(flags.getAnimationId(), ByteOrder.LITTLE);
			block.writeByte(flags.getAnimationDelay(), ValueType.C);
		}
		if (forceChat) {
			block.writeString(flags.getForceChatMessage());
		}
		if (chat) {
			appendChat(flags, block);
		}
		if (entityFace) {
			block.writeShort(flags.getEntityFaceIndex(), ByteOrder.LITTLE);
		}
		if (appearance) {
			appendAppearance(flags, block);
		}
		if (faceDir) {
			block.writeShort(flags.getFaceX() * 2 + 1, ValueType.A, ByteOrder.LITTLE);
			block.writeShort(flags.getFaceY() * 2 + 1, ByteOrder.LITTLE);
		}
		if (hit1) {
			block.writeByte(flags.getDamage());
			block.writeByte(flags.getHitType(), ValueType.A);
			block.writeByte(flags.getHitUpdateType());
			block.writeByte(flags.getHp(), ValueType.C);
			block.writeByte(flags.getMaxHP());
		}
		if (hit2) {
			block.writeByte(flags.getDamage2());
			block.writeByte(flags.getHitType2(), ValueType.S);
			block.writeByte(flags.getHitUpdateType2());
			block.writeByte(flags.getHp());
			block.writeByte(flags.getMaxHP(), ValueType.C);
		}
	}

	private static void appendForceMovement(StonerUpdateFlags flags, OutBuffer block) {
		final Location start = flags.getForceStart();
		final Location end = flags.getForceEnd();
		final Location region = flags.getRegion();

		final int localStartX = start.getLocalX(region);
		final int localStartY = start.getLocalY(region);
		final int localEndX = end.getX();
		final int localEndY = end.getY();

		// Write local start and relative end positions
		block.writeByte(localStartX, ValueType.S);
		block.writeByte(localStartY, ValueType.S);
		block.writeByte(localStartX + localEndX, ValueType.S);
		block.writeByte(localStartY + localEndY, ValueType.S);

		// Write speeds and direction
		block.writeShort(flags.getForceSpeed1(), ValueType.A, ByteOrder.LITTLE);
		block.writeShort(flags.getForceSpeed2(), ValueType.A, ByteOrder.BIG);
		block.writeByte(flags.getForceDirection(), ValueType.S);
	}
}