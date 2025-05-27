package com.bestbudz.rs2.content.dialogue.impl.teleport;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class GloryDialogue extends Dialogue {

	private int itemId;
	private boolean operate = false;

	public GloryDialogue(Stoner stoner, boolean operate, int itemId) {
	this.stoner = stoner;
	this.operate = operate;
	this.itemId = itemId;
	}

	@Override
	public boolean clickButton(int id) {
	if (!stoner.getStoner().getMage().canTeleport(TeleportTypes.SPELL_BOOK)) {
		stoner.getDialogue().end();
		return false;
	}

	if (stoner.getMage().isTeleporting()) {
		return false;
	}

	switch (id) {
	case 9178:
		getStoner().getMage().teleport(3086, 3489, 0, MageProfession.TeleportTypes.SPELL_BOOK);
		if (operate == true) {
			if (itemId - 2 != 1702) {
				stoner.getEquipment().getItems()[2].setId(itemId - 2);
				stoner.getEquipment().onLogin();
				stoner.setAppearanceUpdateRequired(true);
			}
		}
		if (operate == false) {
			if (itemId - 2 != 1702) {
				stoner.getBox().remove(itemId);
				stoner.getBox().add(itemId - 2, 1);
			}
		}
		stoner.getClient().queueOutgoingPacket(new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));
		stoner.getDialogue().end();
		break;
	case 9179:
		getStoner().getMage().teleport(3093, 3244, 0, MageProfession.TeleportTypes.SPELL_BOOK);
		if (operate == true) {
			if (itemId - 2 != 1702) {
				stoner.getEquipment().getItems()[2].setId(itemId - 2);
				stoner.getEquipment().onLogin();
				stoner.setAppearanceUpdateRequired(true);
			}
		}
		if (operate == false) {
			if (itemId - 2 != 1702) {
				stoner.getBox().remove(itemId);
				stoner.getBox().add(itemId - 2, 1);
			}
		}
		stoner.getClient().queueOutgoingPacket(new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));
		stoner.getDialogue().end();
		break;
	case 9180:
		getStoner().getMage().teleport(2909, 3151, 0, MageProfession.TeleportTypes.SPELL_BOOK);
		if (operate == true) {
			if (itemId - 2 != 1702) {
				stoner.getEquipment().getItems()[2].setId(itemId - 2);
				stoner.getEquipment().onLogin();
				stoner.setAppearanceUpdateRequired(true);
			}
		}
		if (operate == false) {
			if (itemId - 2 != 1702) {
				stoner.getBox().remove(itemId);
				stoner.getBox().add(itemId - 2, 1);
			}
		}
		stoner.getClient().queueOutgoingPacket(new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));
		stoner.getDialogue().end();
		break;
	case 9181:
		getStoner().getMage().teleport(3091, 3476, 0, MageProfession.TeleportTypes.SPELL_BOOK);
		if (operate == true) {
			if (itemId - 2 != 1702) {
				stoner.getEquipment().getItems()[2].setId(itemId - 2);
				stoner.getEquipment().onLogin();
				stoner.setAppearanceUpdateRequired(true);
			}
		}
		if (operate == false) {
			if (itemId - 2 != 1702) {
				stoner.getBox().remove(itemId);
				stoner.getBox().add(itemId - 2, 1);
			}
		}
		stoner.getClient().queueOutgoingPacket(new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));
		stoner.getClient().queueOutgoingPacket(new SendMessage("<col=C60DDE>The Wilderness lever is on the wall next to you."));
		stoner.getDialogue().end();
		break;
	}
	return false;
	}

	@Override
	public void execute() {
	DialogueManager.sendOption(stoner, new String[] { "Edgeville", "Draynor", "Karamja", "Wilderness Lever" });
	}
}