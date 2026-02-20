package com.bestbudz.rs2.entity;

import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.MobUpdateList;
import com.bestbudz.rs2.content.combat.CombatConstants;
import com.bestbudz.rs2.content.dwarfcannon.DwarfCannon;
import com.bestbudz.rs2.content.io.sqlite.StonerSave;
import com.bestbudz.rs2.content.minigames.fightpits.FightPits;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControl;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGame;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobUpdateFlags;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.StonerUpdateFlags;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendGameUpdateTimer;
import java.util.ArrayList;
import java.util.List;

public class World {

	public static final short MAX_STONERS = 2048;
	public static final short MAX_MOBS = 8192;

	private static final Stoner[] stoners = new Stoner[MAX_STONERS];
	private static final Mob[] mobs = new Mob[MAX_MOBS];
	private static final MobUpdateList mobUpdateList = new MobUpdateList();
	private static final List<DwarfCannon> cannons = new ArrayList<DwarfCannon>();

	public static boolean worldUpdating = false;
	private static long cycles = 0L;
	private static short updateTimer = -1;
	private static boolean updating = false;
	private static boolean ignoreTick = false;

	private static final WorldEntityManager entityManager = new WorldEntityManager(stoners, mobs);
	private static final WorldUpdateManager updateManager = new WorldUpdateManager();
	private static final WorldMessageManager messageManager = new WorldMessageManager();
	private static final WorldDebugManager debugManager = new WorldDebugManager();

	public static void process() {
		var pFlags = new StonerUpdateFlags[stoners.length];
		var nFlags = new MobUpdateFlags[mobs.length];

		try {
			FightPits.tick();
			PestControl.tick();
			WeaponGame.tick();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (var cannon : cannons) {
			cannon.tick();
		}

		updateManager.processEntities(stoners, mobs, cannons, pFlags, nFlags);

		if (updateTimer > -1 && ((World.updateTimer = (short) (updateTimer - 1)) == 0)) {
			update();
		}

		if (ignoreTick) {
			ignoreTick = false;
		}

		if (World.getCycles() % 50 == 0) {
			for (Stoner s : stoners) {
				if (s != null && s.isActive() && !entityManager.isDiscordBot(s)) {
					s.decayPathMemory();
				}
			}
		}

		if (getCycles() % 100 == 0) {
			debugManager.debugPlayerVisibility(stoners);
			debugManager.checkUsernameCollisions(stoners);
		}

		cycles++;
	}

	public static void addCannon(DwarfCannon cannon) {
		cannons.add(cannon);
	}

	public static void removeCannon(DwarfCannon cannon) {
		cannons.remove(cannon);
	}

	public static long getCycles() {
		return cycles;
	}

	public static Mob[] getNpcs() {
		return mobs;
	}

	public static Stoner[] getStoners() {
		return stoners;
	}

	public static boolean isUpdating() {
		return updating;
	}

	public static int getActiveStoners() {
		return entityManager.getActiveStoners();
	}

	public static int getRealStonerCount() {
		return entityManager.getRealStonerCount();
	}

	public static int getStonerCount() {
		return entityManager.getStonerCount();
	}

	public static boolean isDiscordBot(Stoner stoner) {
		return entityManager.isDiscordBot(stoner);
	}

	public static boolean isPet(Stoner stoner) {
		return entityManager.isPet(stoner);
	}

	public static Stoner getStonerByName(long n) {
		return entityManager.getStonerByName(stoners, n);
	}

	public static Stoner getStonerByName(String username) {
		return entityManager.getStonerByName(stoners, username);
	}

	public static boolean isMobWithinRange(int mobIndex) {
		return (mobIndex > -1) && (mobIndex < mobs.length);
	}

	public static boolean isStonerWithinRange(int stonerIndex) {
		return (stonerIndex > -1) && (stonerIndex < stoners.length);
	}

	public static int register(Mob mob) {
		return entityManager.register(mob, mobs);
	}

	public static int register(Stoner stoner) {
		return entityManager.register(stoner, stoners, updateTimer);
	}

	public static void unregister(Mob mob) {
		entityManager.unregister(mob, mobs, mobUpdateList);
	}

	public static void unregister(Stoner stoner) {
		entityManager.unregister(stoner, stoners);
	}

	public static void remove(List<Mob> local) {

	}

	public static void initUpdate(int time, boolean reboot) {
		worldUpdating = true;
		for (Stoner p : stoners) {
			if (p != null) {
				p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(time));
			}
		}
		TaskQueue.queue(
			new Task((int) Math.ceil((time * 5) / 3.0)) {
				@Override
				public void execute() {
					for (Stoner p : stoners)
						if (p != null) {
							p.logout(true);
							StonerSave.save(p);
						}
					stop();
				}

				@Override
				public void onStop() {
					System.exit(0);
				}
			});
	}

	public static void update() {
		updating = true;
		for (Stoner p : stoners) if (p != null) p.logout(true);
	}

	public static void sendGlobalMessage(String message, boolean format) {
		messageManager.sendGlobalMessage(stoners, message, format);
		DiscordMessageManager.announceGameMessage(message);
	}

	public static void sendGlobalMessage(String message) {
		messageManager.sendGlobalMessage(stoners, message);
		DiscordMessageManager.announceGameMessage(message);
	}

	public static void sendGlobalMessage(String message, Stoner exceptions) {
		messageManager.sendGlobalMessage(stoners, message, exceptions);
		DiscordMessageManager.announceGameMessage(message);
	}

	public static void sendProjectile(Projectile p, Entity e1, Entity e2) {
		int lockon = e2.isNpc() ? e2.getIndex() + 1 : -e2.getIndex() - 1;
		byte offsetX = (byte) ((e1.getLocation().getY() - e2.getLocation().getY()) * -1);
		byte offsetY = (byte) ((e1.getLocation().getX() - e2.getLocation().getX()) * -1);
		sendProjectile(p, CombatConstants.getOffsetProjectileLocation(e1), lockon, offsetX, offsetY);
	}

	public static void sendProjectile(
		Projectile projectile, Location pLocation, int lockon, byte offsetX, byte offsetY) {
		messageManager.sendProjectile(stoners, projectile, pLocation, lockon, offsetX, offsetY);
	}

	public static void sendStillGraphic(int id, int delay, Location location) {
		messageManager.sendStillGraphic(stoners, id, delay, location);
	}

	public static void sendRegionMessage(String message, Location location) {
		messageManager.sendRegionMessage(stoners, message, location);
	}

	public static int getStaff() {
		int amount = 0;
		for (Stoner stoners : World.getStoners()) {
			if (stoners != null) {
				if (StonerConstants.isStaff(stoners)) {
					amount++;
				}
			}
		}
		return amount;
	}
}
