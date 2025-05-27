package com.mayhem.rs2.content.dialogue.impl.teleport;

import com.mayhem.rs2.content.TeleportHandler;
import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.skill.magic.MagicSkill;
import com.mayhem.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class RingOfSlayingDialogue extends Dialogue {

	private int itemId;
	private boolean operate = false;

	public RingOfSlayingDialogue(Player player, boolean operate, int itemId) {
		this.player = player;
		this.operate = operate;
		this.itemId = itemId;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case 9178:
			getPlayer().getMagic().teleport(3095, 3485, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId + 1 != 11874) {
					player.getEquipment().getItems()[2].setId(itemId + 1);
					player.getEquipment().onLogin();
					player.setAppearanceUpdateRequired(true);
				}
			}
			if (operate == false) {
				if (itemId + 1 != 11874) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId + 1, 1);
				}
			}
			if (itemId + 1 == 11874) {
				player.getInventory().remove(itemId);
				player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up the last charge from your " + Item.getDefinition(itemId).getName() + "."));
				break;
			}
			player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));

			break;
		case 9179:
			getPlayer().getMagic().teleport(TeleportHandler.TeleportationData.SLAYER_TOWER.getPosition(), TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId + 1 != 11874) {
					player.getEquipment().getItems()[2].setId(itemId + 1);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId + 1 != 11874) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId + 1, 1);
				}
			}
			if (itemId + 1 == 11874) {
				player.getInventory().remove(itemId);
				player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up the last charge from your " + Item.getDefinition(itemId).getName() + "."));
				break;
			}
			player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));

			break;
		case 9180:
			getPlayer().getMagic().teleport(TeleportHandler.TeleportationData.TAVERLY_DUNG.getPosition(), TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId + 1 != 11874) {
					player.getEquipment().getItems()[2].setId(itemId + 1);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId + 1 != 11874) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId + 1, 1);
				}
			}
			if (itemId + 1 == 11874) {
				player.getInventory().remove(itemId);
				player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up the last charge from your " + Item.getDefinition(itemId).getName() + "."));
				break;
			}
			player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));

			break;
		case 9181:
			getPlayer().getMagic().teleport(TeleportHandler.TeleportationData.BRIMHAVEN_DUNG.getPosition(), TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId + 1 != 11874) {
					player.getEquipment().getItems()[2].setId(itemId + 1);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId + 1 != 11874) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId + 1, 1);
				}
			}
			if (itemId + 1 == 11874) {
				player.getInventory().remove(itemId);
				player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up the last charge from your " + Item.getDefinition(itemId).getName() + "."));
				break;
			}
			player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You use up a charge from your " + Item.getDefinition(itemId).getName() + "."));

			break;
		}
		return false;
	}

	@Override
	public void execute() {
		DialogueManager.sendOption(player, new String[] { "Vannaka",
				"Slayer Tower", "Taverly Dungeon",
				"Brimhaven Dungeon" });
	}
}