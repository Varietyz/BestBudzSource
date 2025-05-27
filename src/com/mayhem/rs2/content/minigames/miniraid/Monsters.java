package com.mayhem.rs2.content.minigames.miniraid;



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
public abstract class Monsters extends Mob {

	private final MiniRaidGame game; //try that now

	public Monsters(MiniRaidGame game, int id, Location p) {
		super(game.getVirtualRegion(), id, true, false, p);
		//MiniRaidConstants.setLevels(this);


		//getFollowing().setIgnoreDistance(true);

		getAttributes().set(MiniRaidGame.MINIRAID_GAME_KEY, game);

		this.game = game;

	} //Where it agro handled for defs

	public MiniRaidGame getGame() {
		return game;
	}

	public abstract void tick();

}
