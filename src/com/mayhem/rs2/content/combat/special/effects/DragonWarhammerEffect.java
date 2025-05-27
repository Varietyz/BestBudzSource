package com.mayhem.rs2.content.combat.special.effects;

import com.mayhem.rs2.content.combat.impl.CombatEffect;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class DragonWarhammerEffect implements CombatEffect {
	

	@Override
	public void execute(Player p, Entity e) {
	
		if(p.getLastDamageDealt() == 0) {
			return;
		}
		double drain = e.getLevels()[1] * .7;

		Player p2 = null;

		if (!e.isNpc()) {
			p2 = com.mayhem.rs2.entity.World.getPlayers()[e.getIndex()];
		}

		if (drain <= 0) {
			//System.out.println("Drain is 0.");
			return;
		}


		if (e.getLevels()[1] - drain < 0) {
			e.getLevels()[1] = 0;
			p.getClient().queueOutgoingPacket(new SendMessage("You drain your opponents " + com.mayhem.rs2.content.skill.Skills.SKILL_NAMES[1] + " down to 0."));

			if (p2 != null) {
				p2.getSkill().update(1);
			}

		} else {
			e.getLevels()[1] = (short) drain;
			if (p2 != null) {
				p2.getSkill().update(1);
			}
			if (e.getLevels()[1] == 0)
				p.getClient().queueOutgoingPacket(new SendMessage("You drain your opponents " + com.mayhem.rs2.content.skill.Skills.SKILL_NAMES[1] + " down to 0."));
			else
				p.getClient().queueOutgoingPacket(new SendMessage("You drain some of your opponents " + com.mayhem.rs2.content.skill.Skills.SKILL_NAMES[1] + "."));
				//System.out.println("Level is: " + e.getLevels()[1]);
		}
	}

}
