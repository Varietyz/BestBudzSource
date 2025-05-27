package com.mayhem.rs2.content.dialogue.impl.teleport;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.skill.magic.MagicSkill;
import com.mayhem.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class CloakDialogue extends Dialogue {
	private int itemId;
	private boolean operate = false;

	public CloakDialogue(Player player, boolean operate, int itemId) {
		this.player = player;
		this.operate = operate;
		this.itemId = itemId;
	}

	@Override
	public boolean clickButton(int id) {
		if (!player.getPlayer().getMagic().canTeleport(TeleportTypes.SPELL_BOOK)) {
			player.getDialogue().end();
			return false;
		}
		
		if (player.getMagic().isTeleporting()) {
			return false;
		}
		
		switch (id) {
		case 9157:
			if (player.getWildernessLevel() > 20 && player.inWilderness()) {
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
				}
			else
			{
			getPlayer().getMagic().teleport(3088, 3502, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
			player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You have teleported to Edge."));
			player.getDialogue().end();
			}
			break;
		case 9158:
			if (player.getWildernessLevel() > 20 && player.inWilderness()) {
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
				}
			else
			{
			getPlayer().getMagic().teleport(1739, 3493, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
			player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You have teleported."));
			player.getDialogue().end();
			}
			break;
		}
		return false;
	}

	@Override
	public void execute() {
		DialogueManager.sendOption(player, new String[] { "Edge", "Quick Bank"});
	}

}
