package com.mayhem.rs2.content.minigames.miniraid;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.content.minigames.miniraid.Monsters;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidConstants;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidGame;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;

public class Monster3 extends Monsters {

	public Monster3(Location location, MiniRaidGame game) {
		super(game, MiniRaidConstants.MONSTER3[Utility.randomNumber(MiniRaidConstants.MONSTER3.length)], location);
	} //Might as well start the class fresh those last two things were also something i tried :P lol

	
	@Override
	public void onDeath() {
		for (Player k : getGame().getPlayers()) {
			if (Utility.getManhattanDistance(k.getLocation(), getLocation()) <= 2) {
				k.hit(new Hit(10 + Utility.randomNumber(25)));
			}
		}
	}
	
	@Override
	public void tick() {}

}
