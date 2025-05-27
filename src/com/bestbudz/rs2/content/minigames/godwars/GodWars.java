package com.bestbudz.rs2.content.minigames.godwars;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.WalkThroughDoorTask;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.Allegiance;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.GodWarsNpc;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

/**
 * 
 * @author Jaybane
 *
 */
public class GodWars {

	/**
	 * Points required to enter the room
	 */
	public static final int POINTS_TO_ENTER = 25;

	/**
	 * Godwars Key
	 */
	public static final String GWD_ALTAR_KEY = "GWD_ALTAR_KEY";

	/**
	 * Ecumencial Key Identification
	 */
	public static final int ECUMENICAL_KEY = 11942;

	/**
	 * Handles clicing object for Godwars
	 * 
	 * @param stoner
	 * @param id
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static boolean clickObject(Stoner stoner, int id, int x, int y, int z) {
	switch (id) {

	/**
	 * Bandos dungeon
	 */
	case 26461:
		if (stoner.getX() >= 2852) {
			TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x - 1, y, z)));
		} else {
			if (stoner.getBox().hasItemId(ECUMENICAL_KEY)) {
				stoner.getBox().remove(ECUMENICAL_KEY, 1);
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x + 1, y, z)));
				stoner.send(new SendMessage("You have used your Ecumencial key to enter the room."));
				return true;
			}
			if (stoner.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()] >= POINTS_TO_ENTER) {
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x + 1, y, z)));
				stoner.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.BANDOS);
				for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.BANDOS)) {
					mob.getCombat().setAssaulting(stoner);
					mob.getFollowing().setFollow(stoner, Following.FollowType.COMBAT);
				}
			} else {
				int req = 40 - stoner.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()];
				DialogueManager.sendStatement(stoner, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
			}
		}
		return true;

	/**
	 * Armadyl room
	 */
	case 26502:
		if (stoner.getY() > 5294) {
			TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y - 1, z)));
		} else {
			if (stoner.getBox().hasItemId(ECUMENICAL_KEY)) {
				stoner.getBox().remove(ECUMENICAL_KEY, 1);
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y + 1, z)));
				stoner.send(new SendMessage("You have used your Ecumencial key to enter the room."));
				return true;
			}
			if (stoner.getMinigames().getGWKC()[Allegiance.ARMADYL.ordinal()] >= POINTS_TO_ENTER) {
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y + 1, z)));
				stoner.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.ARMADYL);
				for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.ARMADYL)) {
					mob.getCombat().setAssaulting(stoner);
					mob.getFollowing().setFollow(stoner, Following.FollowType.COMBAT);
				}
			} else {
				int req = 40 - stoner.getMinigames().getGWKC()[Allegiance.ARMADYL.ordinal()];
				DialogueManager.sendStatement(stoner, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
			}
		}
		return true;

	/**
	 * General Gaardor room
	 */
	case 26503:
		if (stoner.getX() <= 2862) {
			if (stoner.getBox().hasItemId(ECUMENICAL_KEY)) {
				stoner.getBox().remove(ECUMENICAL_KEY, 1);
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x + 1, y, z)));
				stoner.send(new SendMessage("You have used your Ecumencial key to enter the room."));
				return true;
			}
			if (stoner.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()] >= POINTS_TO_ENTER) {
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x + 1, y, z)));
				stoner.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.BANDOS);
				for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.BANDOS)) {
					mob.getCombat().setAssaulting(stoner);
					mob.getFollowing().setFollow(stoner, Following.FollowType.COMBAT);
				}
			} else {
				int req = 40 - stoner.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()];
				DialogueManager.sendStatement(stoner, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
			}
		} else {
			TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x - 1, y, z)));
		}
		return true;

	/**
	 * Saradomin room
	 */
	case 26504:
		if (stoner.getX() >= 2909) {
			if (stoner.getBox().hasItemId(ECUMENICAL_KEY)) {
				stoner.getBox().remove(ECUMENICAL_KEY, 1);
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x - 1, y, z)));
				stoner.send(new SendMessage("You have used your Ecumencial key to enter the room."));
				return true;
			}
			if (stoner.getMinigames().getGWKC()[Allegiance.SARADOMIN.ordinal()] >= POINTS_TO_ENTER) {
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x - 1, y, z)));
				stoner.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.SARADOMIN);
				for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.SARADOMIN)) {
					mob.getCombat().setAssaulting(stoner);
					mob.getFollowing().setFollow(stoner, Following.FollowType.COMBAT);
				}
			} else {
				int req = 40 - stoner.getMinigames().getGWKC()[Allegiance.SARADOMIN.ordinal()];
				DialogueManager.sendStatement(stoner, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
			}
		} else {
			TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x + 1, y, z)));
		}
		return true;

	/**
	 * Zamorak room
	 */
	case 26505:
		if (stoner.getY() >= 5333) {
			if (stoner.getBox().hasItemId(ECUMENICAL_KEY)) {
				stoner.getBox().remove(ECUMENICAL_KEY, 1);
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y - 1, z)));
				stoner.send(new SendMessage("You have used your Ecumencial key to enter the room."));
				return true;
			}
			if (stoner.getMinigames().getGWKC()[Allegiance.ZAMORAK.ordinal()] >= POINTS_TO_ENTER) {
				TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y - 1, z)));
				stoner.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.ZAMORAK);
				for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.BANDOS)) {
					mob.getCombat().setAssaulting(stoner);
					mob.getFollowing().setFollow(stoner, Following.FollowType.COMBAT);
				}
			} else {
				int req = 40 - stoner.getMinigames().getGWKC()[Allegiance.ZAMORAK.ordinal()];
				DialogueManager.sendStatement(stoner, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
			}
		} else {
			TaskQueue.queue(new WalkThroughDoorTask(stoner, x, y, z, new Location(x, y + 1, z)));
		}
		return true;

	/**
	 * Zamorak entrance
	 */
	case 26518:
		if (stoner.getY() == 5332) {
			stoner.teleport(new Location(2885, 5345, 2));
		} else {
			stoner.teleport(new Location(2885, 5332, 2));
		}
		return true;

	/**
	 * Saradomin entrance
	 */
	case 26561:
		stoner.teleport(new Location(2918, 5300, 1));
		return true;

	/**
	 * Saradomin entrance 2
	 */
	case 26562:
		stoner.teleport(new Location(2919, 5274, 0));
		return true;

	/**
	 * Armadyl entrance
	 */
	case 26380:
		if (stoner.getY() == 5269) {
			stoner.teleport(new Location(2871, 5279, 2));
		} else {
			stoner.teleport(new Location(2871, 5269, 2));
		}
		return true;

	/**
	 * All altars
	 */
	case 26366:
	case 26365:
	case 26364:
	case 26363:
		if (stoner.getCombat().inCombat()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot use this while in combat!"));
			return true;
		}

		if ((stoner.getAttributes().get(GWD_ALTAR_KEY) == null) || (((Long) stoner.getAttributes().get(GWD_ALTAR_KEY)).longValue() < System.currentTimeMillis())) {
			stoner.getAttributes().set(GWD_ALTAR_KEY, Long.valueOf(System.currentTimeMillis() + 600_000));
			stoner.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
			stoner.getClient().queueOutgoingPacket(new SendMessage("You recharge your Necromance points at the altar."));
			stoner.getUpdateFlags().sendAnimation(645, 5);
			stoner.getGrades()[5] = stoner.getMaxGrades()[5];
			stoner.getProfession().update(5);
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot use this yet!"));
		}
		return true;

	}
	return false;
	}

	/**
	 * Handles killing Godwars npc
	 * 
	 * @param stoner
	 * @param id
	 */
	public static void onGodwarsKill(Stoner stoner, int id) {
	GodWarsNpc npc = GodWarsData.forId(id);

	if (npc == null) {
		return;
	}

	stoner.getMinigames().changeGWDKills(1, npc.getAllegiance());
	}

	/**
	 * Use item on object
	 * 
	 * @param stoner
	 * @param id
	 * @param obj
	 * @return
	 */
	public static final boolean useItemOnObject(Stoner stoner, int id, int obj) {
	return false;
	}
}
