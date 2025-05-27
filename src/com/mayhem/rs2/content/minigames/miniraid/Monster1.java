package com.mayhem.rs2.content.minigames.miniraid;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.GameConstants;
import com.mayhem.rs2.content.minigames.miniraid.Monsters;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidConstants;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidGame;
import com.mayhem.rs2.entity.Location;

public class Monster1 extends Monsters {

	private byte delay = 0;

	public Monster1(Location location, MiniRaidGame game) {
		super(game, MiniRaidConstants.MONSTER1[Utility.randomNumber(MiniRaidConstants.MONSTER1.length)], location);
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

