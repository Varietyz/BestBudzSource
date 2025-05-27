package com.mayhem.rs2.content.minigames.miniraid2;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.GameConstants;
import com.mayhem.rs2.content.minigames.miniraid2.Monsters2;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Constants;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Game;
import com.mayhem.rs2.entity.Location;

public class Monster11 extends Monsters2 {

	private byte delay = 0;

	public Monster11(Location location, MiniRaid2Game game) {
		super(game, MiniRaid2Constants.MONSTER11[Utility.randomNumber(MiniRaid2Constants.MONSTER11.length)], location);
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

