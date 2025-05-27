package com.mayhem.rs2.content.minigames.miniraid2;



import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.combat.Combat.CombatTypes;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;

/**
 * Mini Raid Monsters
 * @author Mod Divine
 *
 */
public abstract class Monsters2 extends Mob {

	private final MiniRaid2Game game; 

	public Monsters2(MiniRaid2Game game, int id, Location p) {
		super(game.getVirtualRegion(), id, true, false, p);


		getAttributes().set(MiniRaid2Game.MINIRAID2_GAME_KEY, game);

		this.game = game;

	}

	public MiniRaid2Game getGame() {
		return game;
	}

	public abstract void tick();

}
