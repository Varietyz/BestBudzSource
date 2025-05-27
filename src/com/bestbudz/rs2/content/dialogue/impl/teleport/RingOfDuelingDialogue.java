package com.bestbudz.rs2.content.dialogue.impl.teleport;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class RingOfDuelingDialogue extends Dialogue {

	private int itemId;
	private boolean operate = false;

	public RingOfDuelingDialogue(Stoner stoner, boolean operate, int itemId) {
	this.stoner = stoner;
	this.operate = operate;
	this.itemId = itemId;
	}

	@Override
	public boolean clickButton(int id) {
	switch (id) {
	case 9157:
		getStoner().getMage().teleport(2659, 2661, 0, MageProfession.TeleportTypes.SPELL_BOOK);
		if (operate == true) {
			if (itemId + 2 != 2568) {
				stoner.getEquipment().getItems()[12].setId(1639);
				stoner.getEquipment().update();
			}
		}
		if (operate == false) {
			if (itemId + 2 != 2568) {
				stoner.getBox().remove(itemId);
				stoner.getBox().add(itemId + 2, 1);
			}
		}
		stoner.getClient().queueOutgoingPacket(new SendMessage("@pur@You use up a charge from your " + Item.getDefinition(itemId).getName() + " and teleport away."));
		break;
	case 9158:
		getStoner().getMage().teleport(3356, 3268, 0, MageProfession.TeleportTypes.SPELL_BOOK);
		if (operate == true) {
			if (itemId + 2 != 2568) {
				stoner.getEquipment().getItems()[12].setId(itemId + 2);
				stoner.getEquipment().update();
			}
		}
		if (operate == false) {
			if (itemId + 2 != 2568) {
				stoner.getBox().remove(itemId);
				stoner.getBox().add(itemId + 2, 1);
			}
		}
		stoner.getClient().queueOutgoingPacket(new SendMessage("@pur@You use up a charge from your " + Item.getDefinition(itemId).getName() + " and teleport away."));
		break;
	}
	return false;
	}

	@Override
	public void execute() {
	DialogueManager.sendOption(stoner, new String[] { "Pest Control", "Duel Arena" });
	}
}
