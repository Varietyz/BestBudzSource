package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TeleOtherTask;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for Runecrafting training teleport
 * @author Daniel
 *
 */
public class RunecraftingTeleport extends Dialogue {
	
	final Mob mob;

	public RunecraftingTeleport(Player player, Mob mob) {
		this.player = player;
		this.mob = mob;
	}

	@Override
	public boolean clickButton(int id) {
		if (id == 9157) {
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			TaskQueue.queue(new TeleOtherTask(mob, player, new Location(3039, 4834)));
			return true;
		}
		if (id == 9158) {
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			TaskQueue.queue(new TeleOtherTask(mob, player, new Location(2923, 4819)));
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		case 0:
			DialogueManager.sendNpcChat(player, mob.getId(), Emotion.DEFAULT, "Hello "+Utility.formatPlayerName(player.getUsername())+".", "I can teleport you to a Runecrafting training area." , "Where would you like to go?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, new String[] { "Abyss", "Essence mine" });	
			break;
		}
	}
}
