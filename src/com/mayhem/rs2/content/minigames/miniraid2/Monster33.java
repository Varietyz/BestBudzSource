package com.mayhem.rs2.content.minigames.miniraid2;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.content.minigames.miniraid2.Monsters2;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Constants;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Game;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;

public class Monster33 extends Monsters2 {

	private byte delay = 0;

	public Monster33(Location location, MiniRaid2Game game) {
		super(game, MiniRaid2Constants.MONSTER33[Utility.randomNumber(MiniRaid2Constants.MONSTER33.length)], location);
		setCanAttack(true);
	}

	@Override
	public void tick() {
		if (++delay == 7) {
				if (!isMovedLastCycle() && getCombat().getAttackTimer() == 0) {
					if (getCombat().getAttacking() != null) {
						} 
					if (getCombat().getAttacking() == null) {
						return;
					}
				}
			}

			delay = 0;
		}
	}