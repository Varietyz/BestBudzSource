package com.mayhem.rs2.content.minigames.miniraid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mayhem.core.cache.map.Region;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.GameConstants;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidConstants;
import com.mayhem.rs2.content.minigames.miniraid.MiniRaidGame;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.content.skill.prayer.PrayerBook;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;

public class MainMonster extends Mob {

	private final List<Mob> monsters = new ArrayList<Mob>();

	private final List<Mob> monster1 = new ArrayList<Mob>();

	private final MiniRaidGame game;

	public MainMonster(MiniRaidGame game, int id, Location p, int z) {
		super(game.getVirtualRegion(), id, false, false, new Location(p, z));
		getLevels()[Skills.HITPOINTS] = 5000;
		getMaxLevels()[Skills.HITPOINTS] = 5000;
		this.game = game;
		getAttributes().set(MiniRaidGame.MINIRAID_GAME_KEY, game);
	}

	public void cleanup() {
		if (monsters.size() > 0) {
			for (Mob i : monsters) {
				if (i == null)
					continue;
				i.remove();
			}
		}

		if (monster1.size() > 0) {
			for (Mob i : monster1) {
				if (i == null)
					continue;
				i.remove();
			}
		}
	}

	public List<Mob> getMonsters() {
		return monsters;
	}
	
	public void init() {
	}

	
	//Just for shits and gigs
	public void heal(int amount) {
		getLevels()[3] += amount;
		System.out.println("Main monster healed");
		if (getLevels()[3] > getMaxLevels()[3])
			getLevels()[3] = getMaxLevels()[3];
	}

	public boolean isDamaged() {
		return getLevels()[3] < getMaxLevels()[3];
	}
	
	
	@Override
	public void onDeath() {
		cleanup();
		remove();
	}

	@Override
	public void process() {
		if (isDead()) {
			return;
		}
	//	for (Iterator<Mob> i = monsters.iterator(); i.hasNext();) {
	//	if (i.next().isDead()) {
	//	i.remove(); //Doesnt mobs get removed on dieing anyway?
	//		}
	//	}

	//	for (Iterator<Mob> i = monster2.iterator(); i.hasNext();) {
	//		if (i.next().isDead()) {
	//			i.remove();
	//		}
	//	}
		if (Utility.randomNumber(10) == 0 && monsters.size() < 12) {
			Location l = GameConstants.getClearAdjacentLocation(getLocation(), getSize(), game.getVirtualRegion());

			if (l != null) {
				monsters.add(MiniRaidConstants.getRandomMonster(l, game, this));
			}
		}

		if (game.getPlayers().size() > 2) {
			if (Utility.randomNumber(10) == 0 && monster1.size() < 12) {
				int baseX = 3025;
				int baseY = 5234;

				int x = baseX + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));
				int y = baseY + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));

				while (Region.getRegion(x, y).getClip(x, y, 0) == 256) {
					x = baseX + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));
					y = baseY + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));
				}
				monster1.add(new Monster1(new Location(x, y, game.getZ()), game)); 
			}
		}
		try {
			super.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
