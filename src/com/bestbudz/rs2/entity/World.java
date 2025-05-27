package com.bestbudz.rs2.entity;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.MobUpdateList;
import com.bestbudz.core.util.Utility;
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
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendGameUpdateTimer;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNPCUpdate;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProjectile;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStillGraphic;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerUpdate;
import java.io.File;
import java.io.IOException;
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

  public static void addCannon(DwarfCannon cannon) {
    cannons.add(cannon);
  }

  public static int getActiveStoners() {
    int r = 0;

    for (Stoner p : stoners) {
      if (p != null) {
        r++;
      }
    }

    return r;
  }

  public static long getCycles() {
    return cycles;
  }

  public static Mob[] getNpcs() {
    return mobs;
  }

  public static Stoner getStonerByName(long n) {
    for (Stoner p : stoners) {
      if ((p != null) && (p.isActive()) && (p.getUsernameToLong() == n)) {
        return p;
      }
    }

    return null;
  }

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

  public static Stoner[] getStoners() {
    return stoners;
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

  public static boolean isIgnoreTick() {
    return ignoreTick;
  }

  public static void setIgnoreTick(boolean ignore) {
    ignoreTick = ignore;
  }

  public static boolean isMobWithinRange(int mobIndex) {
    return (mobIndex > -1) && (mobIndex < mobs.length);
  }

  public static boolean isStonerWithinRange(int stonerIndex) {
    return (stonerIndex > -1) && (stonerIndex < stoners.length);
  }

  public static boolean isUpdating() {
    return updating;
  }

  public static int npcAmount() {
    int amount = 0;
    for (int i = 1; i < mobs.length; i++) {
      if (mobs[i] != null) {
        amount++;
      }
    }
    return amount;
  }

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

    // Tick cannons first
    for (var cannon : cannons) {
      cannon.tick();
    }

    // Pre-pass: process all stoners
    for (int i = 1; i < stoners.length; i++) {
      var stoner = stoners[i];
      if (stoner == null) continue;

      try {
        if (!stoner.isActive()) {
          if (stoner.getClient().getStage() == Client.Stages.LOGGED_IN) {
            stoner.setActive(true);
            stoner.start();
            stoner.getClient().resetLastPacketReceived();
          } else if (getCycles() - stoner.getClient().getLastPacketTime() > 30) {
            stoner.logout(true);
            continue;
          }
        }

        stoner.getClient().processIncomingPackets();
        stoner.process();
        stoner.getClient().reset();

        for (var cannon : cannons) {
          if (cannon.getLoc().isViewableFrom(stoner.getLocation())) {
            cannon.rotate(stoner);
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
        stoner.logout(true);
      }
    }

    // Pre-pass: process all mobs
    for (int i = 0; i < mobs.length; i++) {
      var mob = mobs[i];
      if (mob == null) continue;

      try {
        mob.process();
      } catch (Exception e) {
        e.printStackTrace();
        mob.remove();
      }
    }

    // Movement + flags for stoners
    for (int i = 1; i < stoners.length; i++) {
      var stoner = stoners[i];
      if (stoner == null || !stoner.isActive()) continue;

      try {
        stoner.getMovementHandler().process();
        pFlags[i] = new StonerUpdateFlags(stoner);
      } catch (Exception e) {
        e.printStackTrace();
        stoner.logout(true);
      }
    }

    // Movement + flags for mobs
    for (var mob : mobs) {
      if (mob == null) continue;
      try {
        mob.processMovement();
        nFlags[mob.getIndex()] = new MobUpdateFlags(mob);
      } catch (Exception e) {
        e.printStackTrace();
        mob.remove();
      }
    }

    // Send updates
    for (int i = 1; i < stoners.length; i++) {
      var stoner = stoners[i];
      if (stoner == null || !stoner.isActive() || pFlags[i] == null) continue;

      try {
        stoner.getClient().queueOutgoingPacket(new SendStonerUpdate(pFlags));
        stoner.getClient().queueOutgoingPacket(new SendNPCUpdate(nFlags, pFlags[i]));
      } catch (Exception e) {
        e.printStackTrace();
        stoner.logout(true);
      }
    }

    // Reset stoners
    for (int i = 1; i < stoners.length; i++) {
      var stoner = stoners[i];
      if (stoner == null || !stoner.isActive()) continue;

      try {
        stoner.reset();
      } catch (Exception e) {
        e.printStackTrace();
        stoner.logout(true);
      }
    }

    // Reset mobs
    for (var mob : mobs) {
      if (mob == null) continue;
      try {
        mob.reset();
      } catch (Exception e) {
        e.printStackTrace();
        mob.remove();
      }
    }

    // Update timer tick
    if (updateTimer > -1 && ((World.updateTimer = (short) (updateTimer - 1)) == 0)) {
      update();
    }

    // Reset tick ignore
    if (ignoreTick) {
      ignoreTick = false;
    }

	  if (World.getCycles() % 50 == 0) {
		  for (Stoner s : stoners) {
			  if (s != null && s.isActive()) {
				  s.decayPathMemory(); // assuming it's implemented per-entity
			  }
		  }
		  for (Mob m : mobs) {
			  if (m != null) {
				  m.decayPathMemory(); // same method in Mob
			  }
		  }
	  }


	  cycles++;
  }

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

  public static void remove(List<Mob> local) {}

  public static void removeCannon(DwarfCannon cannon) {
    cannons.remove(cannon);
  }

  public static void resetUpdate() {
    updateTimer = -1;

    synchronized (stoners) {
      for (Stoner p : stoners)
        if (p != null) p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(0));
    }
  }

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
        if (i != exceptions) i.getClient().queueOutgoingPacket(new SendMessage(message));
      }
    }
  }

  public static void sendProjectile(Projectile p, Entity e1, Entity e2) {
    int lockon = e2.isNpc() ? e2.getIndex() + 1 : -e2.getIndex() - 1;
    byte offsetX = (byte) ((e1.getLocation().getY() - e2.getLocation().getY()) * -1);
    byte offsetY = (byte) ((e1.getLocation().getX() - e2.getLocation().getX()) * -1);
    sendProjectile(p, CombatConstants.getOffsetProjectileLocation(e1), lockon, offsetX, offsetY);
  }

  public static void sendProjectile(
      Projectile projectile, Location pLocation, int lockon, byte offsetX, byte offsetY) {
    for (Stoner stoner : stoners)
      if (stoner != null) {
        if (pLocation.isViewableFrom(stoner.getLocation()))
          stoner
              .getClient()
              .queueOutgoingPacket(
                  new SendProjectile(stoner, projectile, pLocation, lockon, offsetX, offsetY));
      }
  }

  public static void sendStillGraphic(int id, int delay, Location location) {
    for (Stoner stoner : stoners)
      if ((stoner != null) && (location.isViewableFrom(stoner.getLocation())))
        stoner.getClient().queueOutgoingPacket(new SendStillGraphic(id, location, delay));
  }

  public static void sendRegionMessage(String message, Location location) {
    for (Stoner stoner : stoners) {
      if (stoner != null && location.isViewableFrom(stoner.getLocation())) {
        stoner.send(new SendMessage(message));
      }
    }
  }

  public static void unregister(Mob mob) {
    if (mob.getIndex() == -1) {
      return;
    }
    mobs[mob.getIndex()] = null;
    mobUpdateList.toRemoval(mob);
  }

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

  public static void update() {
    updating = true;
    for (Stoner p : stoners) if (p != null) p.logout(true);
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
