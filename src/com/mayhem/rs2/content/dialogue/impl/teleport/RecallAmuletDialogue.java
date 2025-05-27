package com.mayhem.rs2.content.dialogue.impl.teleport;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class RecallAmuletDialogue extends Dialogue {
	private int itemId;
	private boolean operate = false;

	public RecallAmuletDialogue(Player player, boolean operate, int itemId) {
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
			if(player.getRecallLocation() == null) {
				//player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You do not have a recall location saved."));
				DialogueManager.sendStatement(player, "You do not have a recall location saved.");
				player.getDialogue().end();
				break;
			}
			getPlayer().getMagic().teleport(player.getRecallLocation(), TeleportTypes.SPELL_BOOK);
			player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You teleport to your saved location."));
			player.getDialogue().end();
			break;
		case 9158:
			if(player.inWilderness() || player.inDuelArena() || player.inGodwars() || player.isJailed() || player.inKraken() || player.inWGGame() || player.inWGLobby() || player.inZulrah() || !player.getMagic().canTeleport(TeleportTypes.SPELL_BOOK)) {
				player.getClient().queueOutgoingPacket( new SendMessage("<col=C60DDE>You cannot set your recall location in this area."));
				player.getDialogue().end();
				break;
			}
			player.setRecallLocation(new Location(player.getLocation()));
			DialogueManager.sendStatement(player, "Your recall location has been set.");
			player.getDialogue().end();
			break;
		}
		return false;
	}

	@Override
	public void execute() {
		DialogueManager.sendOption(player, new String[] { "Recall", "Set Recall" });
	}

}
