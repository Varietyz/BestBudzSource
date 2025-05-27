package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueConstants;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.houses.HouseManager;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class HouseObjectRotate extends Dialogue {

	private int objectId;
	private Location objectLocation;

	public HouseObjectRotate(Player player, int objectId, Location objectLocation) {
		this.player = player;
		this.objectId = objectId;
		this.objectLocation = objectLocation;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case DialogueConstants.OPTIONS_3_1:
			endChat();
			HouseManager.rotateHouseObject(player, objectId, objectLocation, true);
			return true;
		case DialogueConstants.OPTIONS_3_2:
			endChat();
			HouseManager.rotateHouseObject(player, objectId, objectLocation, false);
			return true;
		case DialogueConstants.OPTIONS_3_3:
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
			DialogueManager.sendOption(player, "Rotate Left", "Rotate Right", "Nevermind");
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
