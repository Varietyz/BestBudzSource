package com.mayhem.rs2.content.minigames.miniraid2;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.FollowToEntityTask;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.minigames.miniraid2.Monsters2;
import com.mayhem.rs2.content.minigames.miniraid2.MainMonster2;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Constants;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Game;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Constants;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Game;
import com.mayhem.rs2.entity.Location;

public class Monster22 extends Monsters2 {
	
	private final MainMonster2 mainmonster2;

	public Monster22(Location l, MiniRaid2Game game, MainMonster2 mainmonster2) {
		super(game, MiniRaid2Constants.MONSTER22[Utility.randomNumber(MiniRaid2Constants.MONSTER22.length)], l);
		setRetaliate(true);
		setCanAttack(true);
		this.mainmonster2 = mainmonster2;
	}


	@Override
	public void tick() {
		if (mainmonster2.isDead()) {
			return;
		}
	}
}
