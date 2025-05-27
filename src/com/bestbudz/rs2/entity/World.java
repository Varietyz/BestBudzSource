package com.bestbudz.rs2.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.MobUpdateList;
import com.bestbudz.rs2.content.combat.CombatConstants;
import com.bestbudz.rs2.content.dwarfcannon.DwarfCannon;
import com.bestbudz.rs2.content.gambling.Lottery;
import com.bestbudz.rs2.content.io.StonerSave;
import com.bestbudz.rs2.content.minigames.fightpits.FightPits;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControl;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGame;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobUpdateFlags;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.StonerUpdateFlags;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendGameUpdateTimer;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNPCUpdate;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerUpdate;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProjectile;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStillGraphic;

/**
 * Handles the in-game world
 * 
 * @author Jaybane
 * 
 */
public class World {

	/**
	 * The maximum amount of stoners that can be processed
	 */
	public static final short MAX_STONERS = 2048;

	/**
	 * The maximum amount of mobs available in the in-game world
	 */
	public static final short MAX_MOBS = 8192;

	/**
	 * A list of stoners registered into the game world
	 */
	private static final Stoner[] stoners = new Stoner[MAX_STONERS];

	/**
	 * A list of mobs registered into the game world
	 */
	private static final Mob[] mobs = new Mob[MAX_MOBS];

	/**
	 * The servers cycles?
	 */
	private static long cycles = 0L;

	/**
	 * A list of updated mobs
	 */
	private static MobUpdateList mobUpdateList = new MobUpdateList();

	/**
	 * A list of cannons in-game
	 */
	private static List<DwarfCannon> cannons = new ArrayList<DwarfCannon>();

	/**
	 * The current server update timer
	 */
	private static short updateTimer = -1;

	/**
	 * The server is being updated
	 */
	private static boolean updating = false;

	/**
	 * is the tick ignored
	 */
	private static boolean ignoreTick = false;

	/**
	 * Is the world Updating
	 */
	public static boolean worldUpdating = false;

	/**
	 * Adds a cannon to the list
	 * 
	 * @param cannon
	 */
	public static void addCannon(DwarfCannon cannon) {
	cannons.add(cannon);
	}

	/**
	 * Gets the active amount of stoners online
	 * 
	 * @return
	 */
	public static int getActiveStoners() {
	int r = 0;

	for (Stoner p : stoners) {
		if (p != null) {
			r++;
		}
	}

	return r;
	}

	/**
	 * Gets the cycles
	 * 
	 * @return
	 */
	public static long getCycles() {
	return cycles;
	}

	/**
	 * Gets the list of in-game mobs
	 * 
	 * @return
	 */
	public static Mob[] getNpcs() {
	return mobs;
	}

	/**
	 * Gets a stoner by their name as a long
	 * 
	 * @param n
	 *              The stoners username as a long
	 * @return
	 */
	public static Stoner getStonerByName(long n) {
	for (Stoner p : stoners) {
		if ((p != null) && (p.isActive()) && (p.getUsernameToLong() == n)) {
			return p;
		}
	}

	return null;
	}

	/**
	 * Gets a stoner by their username
	 * 
	 * @param username
	 *                     The stoners username
	 * @return
	 */
	public static Stoner getStonerByName(String username) {
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

	/**
	 * Gets the list of stoners
	 * 
	 * @return
	 */
	public static Stoner[] getStoners() {
	return stoners;
	}

	/**
	 * Initiates an in-game update
	 */
	public static void initUpdate(int time, boolean reboot) {
	// try {
	// ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "Run
	// Server.bat");
	// processBuilder.directory(new File("./"));
	// processBuilder.start();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	Lottery.draw();
	worldUpdating = true;
	for (Stoner p : stoners) {
		if (p != null) {
			p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(time));
		}
	}
	TaskQueue.queue(new Task((int) Math.ceil((time * 5) / 3.0)) {
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
		if (reboot) {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "Run Server.bat");
				processBuilder.directory(new File("./"));
				processBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
		}
	});
	}

	/**
	 * Is the tick ignored
	 * 
	 * @return
	 */
	public static boolean isIgnoreTick() {
	return ignoreTick;
	}

	/**
	 * Checks if a mobs index is within range
	 * 
	 * @param mobIndex
	 * @return
	 */
	public static boolean isMobWithinRange(int mobIndex) {
	return (mobIndex > -1) && (mobIndex < mobs.length);
	}

	/**
	 * Checks if a stoner is within range to be registered
	 * 
	 * @param stonerIndex
	 *                        The index of the stoner
	 * @return
	 */
	public static boolean isStonerWithinRange(int stonerIndex) {
	return (stonerIndex > -1) && (stonerIndex < stoners.length);
	}

	/**
	 * Is the server being updated
	 * 
	 * @return
	 */
	public static boolean isUpdating() {
	return updating;
	}

	/**
	 * The amount of npcs registered into the game world
	 * 
	 * @return
	 */
	public static int npcAmount() {
	int amount = 0;
	for (int i = 1; i < mobs.length; i++) {
		if (mobs[i] != null) {
			amount++;
		}
	}
	return amount;
	}

	/**
	 * Handles processing the main game world
	 */
	public static void process() {

	StonerUpdateFlags[] pFlags = new StonerUpdateFlags[stoners.length];
	MobUpdateFlags[] nFlags = new MobUpdateFlags[mobs.length];
	try {
		FightPits.tick();
		PestControl.tick();
		WeaponGame.tick();
	} catch (Exception e) {
		e.printStackTrace();
	}

	for (DwarfCannon c : cannons) {
		c.tick();
	}

	for (int i = 1; i < 2048; i++) {
		Stoner stoner = stoners[i];
		try {
			if (stoner != null) {
				if (!stoner.isActive()) {
					if (stoner.getClient().getStage() == Client.Stages.LOGGED_IN) {
						stoner.setActive(true);
						stoner.start();

						stoner.getClient().resetLastPacketReceived();
					} else if (getCycles() - stoner.getClient().getLastPacketTime() > 30) {
						stoner.logout(true);
					}
				}

				stoner.getClient().processIncomingPackets();

				stoner.process();

				stoner.getClient().reset();

				for (DwarfCannon c : cannons) {
					if (c.getLoc().isViewableFrom(stoner.getLocation())) {
						c.rotate(stoner);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (stoner != null) {
				stoner.logout(true);
			}
		}

	}

	for (int i = 0; i < mobs.length; i++) {
		Mob mob = mobs[i];
		if (mob != null) {
			try {
				mob.process();
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}
	}

	for (int i = 1; i < 2048; i++) {
		Stoner stoner = stoners[i];
		if ((stoner == null) || (!stoner.isActive()))
			pFlags[i] = null;
		else {
			try {
				stoner.getMovementHandler().process();
				pFlags[i] = new StonerUpdateFlags(stoner);
			} catch (Exception ex) {
				ex.printStackTrace();
				stoner.logout(true);
			}
		}
	}
	for (int i = 0; i < mobs.length; i++) {
		Mob mob = mobs[i];
		if (mob != null) {
			try {
				mob.processMovement();
				nFlags[mob.getIndex()] = new MobUpdateFlags(mob);
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}
	}

	for (int i = 1; i < 2048; i++) {
		Stoner stoner = stoners[i];
		if ((stoner != null) && (pFlags[i] != null) && (stoner.isActive())) {
			try {
				stoner.getClient().queueOutgoingPacket(new SendStonerUpdate(pFlags));
				stoner.getClient().queueOutgoingPacket(new SendNPCUpdate(nFlags, pFlags[i]));
			} catch (Exception ex) {
				ex.printStackTrace();
				stoner.logout(true);
			}
		}
	}
	for (int i = 1; i < 2048; i++) {
		Stoner stoner = stoners[i];
		if ((stoner != null) && (stoner.isActive())) {
			try {
				stoner.reset();
			} catch (Exception ex) {
				ex.printStackTrace();
				stoner.logout(true);
			}
		}
	}
	for (int i = 0; i < mobs.length; i++) {
		Mob mob = mobs[i];
		if (mob != null) {
			try {
				mob.reset();
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}
	}

	if ((updateTimer > -1) && ((World.updateTimer = (short) (updateTimer - 1)) == 0)) {
		update();
	}

	if (ignoreTick) {
		ignoreTick = false;
	}

	cycles += 1L;
	}

	/**
	 * Registers a mob into the game world
	 * 
	 * @param mob
	 *                The mob to register into the game world
	 * @return
	 */
	public static int register(Mob mob) {
	for (int i = 1; i < mobs.length; i++) {
		if (mobs[i] == null) {
			mobs[i] = mob;
			mob.setIndex(i);
			return i;
		}
	}

	return -1;
	}

	/**
	 * Registers a stoner into the in-game world
	 * 
	 * @param stoner
	 *                   The stoner to register into the game world
	 * @return
	 */
	public static int register(Stoner stoner) {
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

	public static void remove(List<Mob> local) {
	}

	/**
	 * Removes a cannon from the list
	 * 
	 * @param cannon
	 */
	public static void removeCannon(DwarfCannon cannon) {
	cannons.remove(cannon);
	}

	/**
	 * Resets an in-game update
	 */
	public static void resetUpdate() {
	updateTimer = -1;

	synchronized (stoners) {
		for (Stoner p : stoners)
			if (p != null)
				p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(0));
	}
	}

	/**
	 * Sends a global message to all stoners online
	 * 
	 * @param message
	 *                    The message to send to all stoners
	 * @param format
	 *                    Should the message beformatted
	 */
	public static void sendGlobalMessage(String message, boolean format) {
	message = (format ? "<col=255>" : "") + message + (format ? "</col>" : "");

	for (Stoner p : stoners)
		if ((p != null) && (p.isActive()))
			p.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	public static void sendGlobalMessage(String message) {
	for (Stoner i : World.getStoners()) {
		if (i != null) {
			i.getClient().queueOutgoingPacket(new SendMessage(message));
		}
	}
	}

	public static void sendGlobalMessage(String message, Stoner exceptions) {
	for (Stoner i : World.getStoners()) {
		if (i != null) {
			if (i != exceptions)
				i.getClient().queueOutgoingPacket(new SendMessage(message));
		}
	}
	}

	public static void sendProjectile(Projectile p, Entity e1, Entity e2) {
	int lockon = e2.isNpc() ? e2.getIndex() + 1 : -e2.getIndex() - 1;
	byte offsetX = (byte) ((e1.getLocation().getY() - e2.getLocation().getY()) * -1);
	byte offsetY = (byte) ((e1.getLocation().getX() - e2.getLocation().getX()) * -1);
	sendProjectile(p, CombatConstants.getOffsetProjectileLocation(e1), lockon, offsetX, offsetY);
	}

	/**
	 * Sends a projectile
	 * 
	 * @param projectile
	 *                       The id of the graphic
	 * @param pLocation
	 *                       The location to send the graphic too
	 * @param lockon
	 *                       The lockon index
	 * @param offsetX
	 *                       The x offset of the projectile
	 * @param offsetY
	 *                       The y offset of the projectile
	 */
	public static void sendProjectile(Projectile projectile, Location pLocation, int lockon, byte offsetX, byte offsetY) {
	for (Stoner stoner : stoners)
		if (stoner != null) {
			if (pLocation.isViewableFrom(stoner.getLocation()))
				stoner.getClient().queueOutgoingPacket(new SendProjectile(stoner, projectile, pLocation, lockon, offsetX, offsetY));
		}
	}

	/**
	 * Sets a still graphic to a location
	 * 
	 * @param id
	 *                     The id of the graphic
	 * @param delay
	 *                     The delay of the graphic
	 * @param location
	 *                     The location of the graphic
	 */
	public static void sendStillGraphic(int id, int delay, Location location) {
	for (Stoner stoner : stoners)
		if ((stoner != null) && (location.isViewableFrom(stoner.getLocation())))
			stoner.getClient().queueOutgoingPacket(new SendStillGraphic(id, location, delay));
	}

	/**
	 * Sends message to region stoners
	 * 
	 * @param message
	 * @param location
	 */
	public static void sendRegionMessage(String message, Location location) {
	for (Stoner stoner : stoners) {
		if (stoner != null && location.isViewableFrom(stoner.getLocation())) {
			stoner.send(new SendMessage(message));

		}
	}
	}

	/**
	 * Sets the tick to be ignored
	 * 
	 * @param ignore
	 *                   Should the tick be ignored
	 */
	public static void setIgnoreTick(boolean ignore) {
	ignoreTick = ignore;
	}

	/**
	 * Unregisters a mob from the game world
	 * 
	 * @param mob
	 *                The mob to unregister from the game world
	 */
	public static void unregister(Mob mob) {
	if (mob.getIndex() == -1) {
		return;
	}
	mobs[mob.getIndex()] = null;
	mobUpdateList.toRemoval(mob);
	}

	/**
	 * Unregisters a stoner from the game world
	 * 
	 * @param stoner
	 *                   The stoner to unregister into the game world
	 */
	public static void unregister(Stoner stoner) {
	if ((stoner.getIndex() == -1) || (stoners[stoner.getIndex()] == null)) {
		return;
	}

	stoners[stoner.getIndex()] = null;

	for (int i = 0; i < stoners.length; i++)
		if ((stoners[i] != null) && (stoners[i].isActive())) {
			stoners[i].getPrivateMessaging().updateOnlineStatus(stoner, false);
		}
	}

	/**
	 * Updates the server by disconnecting all stoners
	 */
	public static void update() {
	updating = true;
	for (Stoner p : stoners)
		if (p != null)
			p.logout(true);
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
