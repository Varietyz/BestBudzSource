package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueConstants;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.houses.HouseManager;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class HouseObjectDelete extends Dialogue {

	private int objectId;
	private Location objectLocation;

	public HouseObjectDelete(Player player, int objectId, Location objectLocation) {
		this.player = player;
		this.objectId = objectId;
		this.objectLocation = objectLocation;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case DialogueConstants.OPTIONS_2_1:
			endChat();
			HouseManager.deleteHouseObject(player, objectId, objectLocation);
			return true;
		case DialogueConstants.OPTIONS_2_2:
			endChat();
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		if (player == null || objectId < 1 || objectLocation == null) {
			endChat();
			return;
		}
		switch (next) {
		case 0:
			DialogueManager.sendStatement(player, "Are you sure you want to delete the object at: ",
					objectLocation.toString() + "?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Delete object", "Nevermind");
			break;
		default:
			endChat();
			break;
		}
	}

	private void endChat() {
		end();
		player.send(new SendRemoveInterfaces());
	}

}
