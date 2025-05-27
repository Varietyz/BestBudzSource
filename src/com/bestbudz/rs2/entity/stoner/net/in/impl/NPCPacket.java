package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.network.StreamBuffer.ByteOrder;
import com.bestbudz.rs2.entity.WalkToActions;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class NPCPacket extends IncomingPacket {

	public static final int FIRST_CLICK = 155;
	public static final int SECOND_CLICK = 17;
	public static final int THIRD_CLICK = 21;
	public static final int FOURTH_CLICK = 230;
	public static final int ASSAULT = 72;
	public static final int MAGE_ON_NPC = 131;
	public static final int ITEM_ON_NPC = 57;

	@Override
	public int getMaxDuplicates() {
	return 1;
	}

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
	if (stoner.isDead() || !stoner.getController().canClick() || stoner.isStunned()) {
		return;
	}

	stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

	stoner.getCombat().reset();

	if (!stoner.getMage().isDFireShieldEffect()) {
		stoner.getMage().getSpellCasting().resetOnAssault();
	}

	switch (opcode) {
	case 155:
		int slot = in.readShort(true, StreamBuffer.ByteOrder.LITTLE);

		if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
			return;
		}

		WalkToActions.clickNpc(stoner, 1, slot);
		break;
	case 17:
		slot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE) & 0xFFFF;

		if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
			return;
		}

		WalkToActions.clickNpc(stoner, 2, slot);
		break;
	case 21:
		slot = in.readShort();

		if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
			return;
		}

		WalkToActions.clickNpc(stoner, 3, slot);
		break;
	case 18:
		slot = in.readShort(true, ByteOrder.LITTLE);

		if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
			return;
		}

		WalkToActions.clickNpc(stoner, 4, slot);
		break;
	case 72:
		slot = in.readShort(StreamBuffer.ValueType.A);

		Mob mob = World.getNpcs()[slot];

		if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
			return;
		}

		if (mob.getId() == 5527 || mob.getId() == 606 || mob.getId() == 1603 || mob.getId() == 2130 || mob.getId() == 2131 || mob.getId() == 2132 || mob.getId() == 1558 || mob.getId() == 5523 || mob.getId() == 1558 || mob.getId() == 403 || mob.getId() == 490 || mob.getId() == 4936 || mob.getId() == 315) {
			WalkToActions.clickNpc(stoner, 2, slot);
			return;
		}

		if (!stoner.isHitZulrah()) {
			if (mob.getId() == 2042 || mob.getId() == 2043 || mob.getId() == 2044) {
				return;
			}
		}

		stoner.getMovementHandler().reset();

		stoner.getCombat().setAssaulting(mob);
		stoner.getFollowing().setFollow(mob, Following.FollowType.COMBAT);

		if (BestbudzConstants.DEV_MODE) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("[NPCPacket] npc id " + mob.getId()));
		}

		break;
	case 131:
		slot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
		int mageId = in.readShort(StreamBuffer.ValueType.A);

		stoner.getMovementHandler().reset();
		mob = World.getNpcs()[slot];

		if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
			return;
		}

		if (BestbudzConstants.DEV_MODE) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("Mage id: " + mageId));
		}

		if (!stoner.isHitZulrah()) {
			if (mob.getId() == 2042 || mob.getId() == 2043 || mob.getId() == 2044) {
				return;
			}
		}

		stoner.getMage().getSpellCasting().castCombatSpell(mageId, mob);
		break;
	case 57:
		int itemId = in.readShort(StreamBuffer.ValueType.A);
		slot = in.readShort(StreamBuffer.ValueType.A);
		int itemSlot = in.readShort(StreamBuffer.ByteOrder.LITTLE);

		if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
			return;
		}

		if (!stoner.getBox().slotContainsItem(itemSlot, itemId)) {
			return;
		}

		WalkToActions.useItemOnNpc(stoner, itemId, slot);
	}
	}
}
