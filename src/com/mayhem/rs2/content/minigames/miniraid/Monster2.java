package com.mayhem.rs2.content.minigames.miniraid;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.FollowToEntityTask;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.minigames.miniraid.Monsters;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidConstants;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidGame;
import com.mayhem.rs2.entity.Location;

public class Monster2 extends Monsters {
	
	private final MainMonster mainmonster;
	private final Task heal = null;

	public Monster2(Location l, MiniRaidGame game, MainMonster mainmonster) {
		super(game, MiniRaidConstants.MONSTER2[Utility.randomNumber(MiniRaidConstants.MONSTER2.length)], l);
		setRetaliate(true);
		setCanAttack(true);
		this.mainmonster = mainmonster;
	}

	public void heal() {
		if ((heal == null) || (heal.stopped())) {
			TaskQueue.queue(new Task(this, 1) {
				
				@Override
				public void execute() {
					mainmonster.heal(25); //this method was never called, try a diff task or whatever intead of followtoentity
					stop();
				}

				@Override
				public void onStop() {}
				
			});
		}
	}

	@Override
	public void tick() {
		if (mainmonster.isDead()) {
			return;
		}

		if ((mainmonster.isDamaged()) && (Utility.randomNumber(3) == 0)) {
			heal();
		}
	}
}
