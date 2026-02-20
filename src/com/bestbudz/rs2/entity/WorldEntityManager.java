package com.bestbudz.rs2.entity;

import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.core.util.MobUpdateList;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendGameUpdateTimer;

public class WorldEntityManager {

	private final Stoner[] stoners;
	private final Mob[] mobs;

	public WorldEntityManager(Stoner[] stoners, Mob[] mobs) {
		this.stoners = stoners;
		this.mobs = mobs;
	}

	public int getActiveStoners() {
		int count = 0;
		for (Stoner stoner : stoners) {
			if (stoner != null && stoner.isActive() && !stoner.isPetStoner()) {
				count++;
			}
		}
		return count;
	}

	public int getRealStonerCount() {
		int count = 0;
		for (Stoner stoner : stoners) {
			if (stoner != null && stoner.isActive() && !stoner.isPetStoner() && !isDiscordBot(stoner) && !stoner.isPet()) {
				count++;
			}
		}
		return count;
	}

	public int getStonerCount() {
		int count = 0;
		for (Stoner stoner : stoners) {
			if (stoner != null && stoner.isActive() && !stoner.isPetStoner()) {
				count++;
			}
		}
		return count;
	}

	public boolean isDiscordBot(Stoner stoner) {
		return stoner != null && DEFAULT_USERNAME.equals(stoner.getUsername());
	}

	public boolean isPet(Stoner stoner) {
		return stoner != null && stoner.isPetStoner();
	}

	public Stoner getStonerByName(Stoner[] stoners, long n) {
		for (Stoner p : stoners) {
			if ((p != null) && (p.isActive()) && (p.getUsernameToLong() == n)) {
				return p;
			}
		}
		return null;
	}

	public Stoner getStonerByName(Stoner[] stoners, String username) {
		if (username == null) {
			return null;
		}

		long n = Utility.nameToLong(username.toLowerCase());

		for (Stoner p : stoners) {
			if ((p != null) && (p.isActive()) && (p.getUsernameToLong() == n)) {
				return p;
			}
		}
		return null;
	}

	public int register(Mob mob, Mob[] mobs) {
		for (int i = 1; i < mobs.length; i++) {
			if (mobs[i] == null) {
				mobs[i] = mob;
				mob.setIndex(i);
				return i;
			}
		}
		return -1;
	}

	public int register(Stoner stoner, Stoner[] stoners, short updateTimer) {
		int[] ids = new int[stoners.length];
		int c = 0;

		for (int i = 1; i < stoners.length; i++) {
			if (stoners[i] == null) {
				ids[c] = i;
				c++;
			}
		}

		if (c == 0) {
			return -1;
		}
		int index = ids[Utility.randomNumber(c)];

		stoners[index] = stoner;
		stoner.setIndex(index);

		for (int k = 1; k < stoners.length; k++) {
			if ((stoners[k] != null) && (stoners[k].isActive())) {
				stoners[k].getPrivateMessaging().updateOnlineStatus(stoner, true);
			}
		}
		if (updateTimer > -1) {
			stoner.getClient().queueOutgoingPacket(new SendGameUpdateTimer(updateTimer));
		}

		return c;
	}

	public void unregister(Mob mob, Mob[] mobs, MobUpdateList mobUpdateList) {
		if (mob.getIndex() == -1) {
			return;
		}
		mobs[mob.getIndex()] = null;
		mobUpdateList.toRemoval(mob);
	}

	public void unregister(Stoner stoner, Stoner[] stoners) {
		if ((stoner.getIndex() == -1) || (stoners[stoner.getIndex()] == null)) {
			return;
		}

		stoners[stoner.getIndex()] = null;

		for (int i = 0; i < stoners.length; i++)
			if ((stoners[i] != null) && (stoners[i].isActive())) {
				stoners[i].getPrivateMessaging().updateOnlineStatus(stoner, false);
			}
	}
}
