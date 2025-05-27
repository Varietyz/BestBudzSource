package com.mayhem.rs2.content.skill.magic.effects;

import com.mayhem.rs2.content.combat.impl.CombatEffect;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

public class IceBurstEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		if (!e.isNpc() && !e.isFrozen()) {
			Player p2 = com.mayhem.rs2.entity.World.getPlayers()[e.getIndex()];
			if (p2 == null) {
				return;
			}
			p2.getClient().queueOutgoingPacket(new SendMessage(e.isImmuneToIce() ? "You are immune to being frozen." : "You have been frozen"));

		}
		e.freeze(10, 5);
		if(!e.isNpc() && e.getFreeze() > 0) {
			Player p2 = com.mayhem.rs2.entity.World.getPlayers()[e.getIndex()];
			p2.getClient().queueOutgoingPacket(new SendString("freezetimer:" + (e.getFreeze()), -1));			
		}
	}
}
