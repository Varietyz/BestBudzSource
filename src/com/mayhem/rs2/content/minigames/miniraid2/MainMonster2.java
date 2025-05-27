package com.mayhem.rs2.content.minigames.miniraid2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mayhem.core.cache.map.Region;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.GameConstants;
import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.content.combat.Hit.HitTypes;
import com.mayhem.rs2.content.minigames.miniraid.MainMonster;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Constants;
import com.mayhem.rs2.content.minigames.miniraid2.MiniRaid2Game;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.content.skill.prayer.PrayerBook;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.Projectile;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class MainMonster2 extends Mob {

	private final List<Mob> monsters2 = new ArrayList<Mob>();

	private final List<Mob> monster11 = new ArrayList<Mob>();

	private Entity lastKilledBy = null;

	private final MiniRaid2Game game;

	private boolean usingSpecial;

	public MainMonster2(MiniRaid2Game game, int id, Location p, int z) {
		super(game.getVirtualRegion(), id, false, false, new Location(p, z));
		if (getId() == 7605) {
			getLevels()[Skills.HITPOINTS] = 3000;
			getMaxLevels()[Skills.HITPOINTS] = 3000;
		}
		if (getId() == 7606) {
			getLevels()[Skills.HITPOINTS] = 4000;
			getMaxLevels()[Skills.HITPOINTS] = 4000;
		}
		this.game = game;
		getAttributes().set(MiniRaid2Game.MINIRAID2_GAME_KEY, game);
	}

	@Override
	public int getAffectedDamage(Hit hit) {
		switch (hit.getType()) {
		case RANGED:
		case MELEE:
			if (getId() == 7605) {
				return 0;
			}
			break;
		case MAGIC:
			if (getId() == 7606) {
				return 0;
			}
			break;

		default:
			return hit.getDamage();
		}
		return hit.getDamage();
	}

	@Override
	public void onHit(Entity entity, Hit hit) {
		if (entity != null && !entity.isNpc()) {
			int random = Utility.random(5);
			if (random == 1) {
				usingSpecial = true;
				special(entity.getPlayer());
			}
		}
	}

	@Override
	public void hit(Hit hit) {
		super.hit(hit);

		int random = Utility.random(15);
		if (random ==  5) {
			teleportSpecial();
	}
	}

	public void cleanup() {
		if (monsters2.size() > 0) {
			for (Mob i : monsters2) {
				if (i == null)
					continue;
				i.remove();
			}
		}

		if (monster11.size() > 0) {
			for (Mob i : monster11) {
				if (i == null)
					continue;
				i.remove();
			}
		}
	}

	public List<Mob> getMonsters() {
		return monsters2;
	}

	public void init() {
	}

	public boolean isDamaged() {
		return getLevels()[3] < getMaxLevels()[3];
	}

	private boolean attacking() {
		if (getCombat().getAttacking() != null) {
			if (!getCombat().getAttacking().isNpc()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDeath() {
		 if (getId() == 7606) {
			cleanup();
			remove();
		}
	}

	public void transform() {
		transform(getId() == 7605 ? 7606 : 7605);
	}

	@Override
	public void process() {
		if (isDead()) {
			return;
		}
		if (Utility.randomNumber(4) == 0 && monsters2.size() < 10) {
			Location l = GameConstants.getClearAdjacentLocation(getLocation(), getSize(), game.getVirtualRegion());

			if (l != null) {
				monsters2.add(MiniRaid2Constants.getRandomMonster(l, game, this));
			}
		}

		if (game.getPlayers().size() > 2) {
			if (Utility.randomNumber(10) == 0 && monster11.size() < 2) {
				int baseX = 1694;
				int baseY = 9888;

				int x = baseX + (Utility.randomNumber(20) == 20 ? Utility.randomNumber(20) : -Utility.randomNumber(20));
				int y = baseY + (Utility.randomNumber(20) == 20 ? Utility.randomNumber(20) : -Utility.randomNumber(20));

				while (Region.getRegion(x, y).getClip(x, y, 0) == 256) {
					x = baseX + (Utility.randomNumber(20) == 20 ? Utility.randomNumber(20) : -Utility.randomNumber(20));
					y = baseY + (Utility.randomNumber(20) == 20 ? Utility.randomNumber(20) : -Utility.randomNumber(20));
				}
				monster11.add(new Monster11(new Location(x, y, game.getZ()), game));
			}
		}
		try {
			super.process();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void special(Player player) {
		for (int i = 0; i < 3; i++) {
			int offsetX = player.getX() - getX();
			int offsetY = player.getY() - getY();
			if (i == 0 || i == 2) {
				offsetX += i == 0 ? -1 : 1;
				offsetY++;
			}
			Location end = new Location(getX() + offsetX, getY() + offsetY, 0);
			World.sendProjectile(new Projectile(551, 1, 10, 100, 65, 10, 20), getLocation(), -1, (byte) offsetX, (byte) offsetY);
			World.sendStillGraphic(659, 100, end);
			TaskQueue.queue(new Task(player, 3, false) {
				@Override
				public void execute() {
					stop();
				}
	
				@Override
				public void onStop() {
					if (player.getLocation().equals(end)) {
						int damage = Utility.random(15) + Utility.random(15) + 12;
						if (damage > 50) {
							damage = 50;
						}
						player.hit(new Hit(damage, HitTypes.MAGIC));
						usingSpecial = false;
					}
				}
			});
		}	
	}

	private Projectile getProjectile(int id) {
		return new Projectile(id, 1, 40, 70, 43, 31, 16);
	}

	/**
	 * Handles removing equipment to attacker
	 */
	/**
	 * Handles teleporting attacker away
	 */
	private void teleportSpecial() {
		if (attacking()) {
			Player player = (Player) getCombat().getAttacking();
			World.sendProjectile(getProjectile(553), player, this);
			player.teleport(new Location(1695 - Utility.random(3), 9887 - Utility.random(3), player.getZ()));
			player.send(new SendMessage("You have been teleported."));
			player.getUpdateFlags().sendGraphic(new Graphic(554));
		}
	}

}
