package com.bestbudz.rs2.content.dwarfcannon;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.HitTask;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.pathfinding.StraightPathFinder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendAnimateObject;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayList;
import java.util.Iterator;

public class DwarfCannon extends RSObject {

	public static final int[] DIRECIONS = { 1, 2, 4, 7, 6, 5, 3, 0 };
	public static int[] ROTATION_DIRECTIONS = { 515, 516, 517, 518, 519, 520, 521, 514 };
	private final Stoner cannonOwner;

	private final Location cannonLocation;

	private final Location ownerLocation;

	private int ammunition = 0;

	private byte stage = 1;

	private boolean notify = true;

	private int dir = 7;

	public DwarfCannon(Stoner owner, int x, int y, int z) {
	super(x - 1, y - 1, z, 7, 10, 0);
	this.cannonOwner = owner;
	Region.getRegion(x, y).addObject(this);
	ObjectManager.register(getGameObject());
	cannonLocation = new Location(x - 1, y - 1, z);
	ownerLocation = new Location(x, y, z);
	cannonOwner.getUpdateFlags().sendAnimation(827, 0);
	}

	public boolean isOwner(Stoner stoner) {
	return cannonOwner.equals(stoner);
	}

	public GameObject getGameObject() {
	return new GameObject(getId(), getX(), getY(), getZ(), getType(), getFace());
	}

	public Hit getHit() {
	return new Hit(cannonOwner, Utility.randomNumber(31), Hit.HitTypes.CANNON);
	}

	public Location getLoc() {
	return cannonLocation;
	}

	public Projectile getCannonFire() {
	Projectile p = new Projectile(53);
	p.setStartHeight(50);
	p.setEndHeight(50);
	p.setCurve(0);
	return p;
	}

	public boolean construct(int id) {
	if (stage == 1 && id == 8) {
		cannonOwner.getBox().remove(8);
		stage = ((byte) (stage + 1));
		ObjectManager.removeFromList(getGameObject());
		setId(8);
		Region.getRegion(getX(), getY()).addObject(this);
		ObjectManager.register(getGameObject());
		cannonOwner.getUpdateFlags().sendAnimation(827, 0);
		return true;
	}
	if (stage == 2 && id == 10) {
		cannonOwner.getBox().remove(10);
		stage = ((byte) (stage + 1));
		ObjectManager.removeFromList(getGameObject());
		setId(9);
		Region.getRegion(getX(), getY()).addObject(this);
		ObjectManager.register(getGameObject());
		cannonOwner.getUpdateFlags().sendAnimation(827, 0);
		return true;
	}
	if (stage == 3 && id == 12) {
		cannonOwner.getBox().remove(12);
		stage = ((byte) (stage + 1));
		ObjectManager.removeFromList(getGameObject());
		setId(6);
		Region.getRegion(getX(), getY()).addObject(this);
		ObjectManager.register(getGameObject());
		World.addCannon(this);
		cannonOwner.getUpdateFlags().sendAnimation(827, 0);
		return true;
	}

	return false;
	}

	public Item[] getItemsForStage() {
	switch (stage) {
	case 1:
		return new Item[] { new Item(6, 1) };
	case 2:
		return new Item[] { new Item(6, 1), new Item(8, 1) };
	case 3:
		return new Item[] { new Item(6, 1), new Item(8, 1), new Item(10) };
	case 4:
		return new Item[] { new Item(6, 1), new Item(8, 1), new Item(10, 1), new Item(12, 1) };
	}
	return null;
	}

	public Mob[] getMobsInPath() {
	ArrayList<Mob> assault = new ArrayList<Mob>();
	for (Iterator<Mob> mobs = cannonOwner.getClient().getNpcs().iterator(); mobs.hasNext();) {
		Mob mob = mobs.next();
		int dir = GameConstants.getDirection(Integer.signum(cannonLocation.getX() - mob.getX()), Integer.signum(cannonLocation.getY() - mob.getY()));
		if (DIRECIONS[dir] == this.dir) {
			boolean canAssault = !cannonOwner.getCombat().inCombat() || cannonOwner.inMultiArea() || (cannonOwner.getCombat().inCombat() && cannonOwner.getCombat().getLastAssaultedBy().equals(mob));
			boolean clearPath = StraightPathFinder.isProjectilePathClear(getX(), getY(), cannonLocation.getZ(), mob.getX(), mob.getY());
			if (mob.getGrades()[3] > 0 && canAssault && clearPath) {
				assault.add(mob);
			}
		}
	}
	Mob[] mob = new Mob[assault.size()];
	for (int i = 0; i < mob.length; i++) {
		mob[i] = assault.get(i);
	}
	return mob;
	}

	public boolean load(Stoner stoner, int item, int obj) {
	if (!isOwner(stoner)) {
		stoner.send(new SendMessage("This is not your cannon!"));
		return true;
	}
	if (item != 2 && obj != 6) {
		return false;
	}
	if (!stoner.getBox().hasItemId(2)) {
		stoner.send(new SendMessage("You do not have any Cannon balls."));
		return true;
	}
	int needed = 30 - ammunition;
	if (needed == 0) {
		stoner.send(new SendMessage("Your cannon is full."));
		return true;
	}
	int invBalls = stoner.getBox().getItemAmount(2);
	if (invBalls <= needed) {
		stoner.getBox().remove(2, invBalls);
		stoner.send(new SendMessage("You load the last of your cannon balls"));
		ammunition += invBalls;
	} else {
		stoner.getBox().remove(2, needed);
		stoner.send(new SendMessage("You load " + needed + " balls into the cannon."));
		ammunition += needed;
	}
	return true;
	}

	public void onLogout() {
	if (!pickup(cannonOwner, getX(), getY())) {
		for (Item i : getItemsForStage()) {
			cannonOwner.getBank().add(i);
		}
		if (ammunition > 0) {
			cannonOwner.getBank().add(2, ammunition);
		}
		if (stage == 4) {
			World.removeCannon(this);
		}
		cannonOwner.getAttributes().remove("dwarfmulticannon");
		Region.getRegion(getX(), getY()).removeObject(this);
		ObjectManager.remove(getGameObject());
	}
	}

	public boolean pickup(Stoner stoner, int x, int y) {
	if (!isOwner(stoner)) {
		stoner.send(new SendMessage("This is not your cannon!"));
		return true;
	}
	if (!stoner.getBox().hasSpaceFor(getItemsForStage())) {
		stoner.send(new SendMessage("You do not have enough box space to pick up your cannon."));
		return false;
	}
	if (stage == 4 && ammunition > 0 && !stoner.getBox().hasSpaceFor(new Item(2, ammunition))) {
		stoner.send(new SendMessage("You do not have enough box space to pick up your cannon."));
		return false;
	}
	if (ammunition > 0) {
		stoner.getBox().add(2, ammunition, false);
	}
	stoner.getUpdateFlags().sendFaceToDirection(getGameObject().getLocation());
	stoner.getBox().add(getItemsForStage(), true);
	Region.getRegion(x, y).removeObject(this);
	ObjectManager.remove(getGameObject());
	stoner.getAttributes().remove("dwarfmulticannon");
	cannonOwner.getUpdateFlags().sendAnimation(827, 0);
	if (stage == 4) {
		World.removeCannon(this);
	}
	return true;
	}

	public void rotate(Stoner stoner) {
	if (ammunition != 0) {
		stoner.send(new SendAnimateObject(this, ROTATION_DIRECTIONS[dir]));
	}
	}

	public void tick() {
	if (stage != 4) {
		return;
	}
	dir = (dir == 7 ? 0 : dir + 1);
	if (!cannonLocation.isViewableFrom(cannonOwner.getLocation())) {
		return;
	}
	if (ammunition == 0) {
		if (!notify) {
			notify = true;
			cannonOwner.send(new SendMessage("You have run out of Cannonballs!"));
		}
		return;
	}
	if (notify) {
		notify = false;
	}
	Mob[] mobs = getMobsInPath();
	if (mobs != null)
		for (Mob i : mobs)
			if (i != null) {
				int lockon = i.getIndex() + 1;
				byte offsetX = (byte) ((i.getLocation().getY() - i.getLocation().getY()) * -1);
				byte offsetY = (byte) ((i.getLocation().getX() - i.getLocation().getX()) * -1);
				World.sendProjectile(getCannonFire(), ownerLocation, lockon, offsetX, offsetY);
				Hit hit = getHit();
				TaskQueue.queue(new HitTask(3, false, hit, i));
				cannonOwner.getProfession().addCombatExperience(CombatTypes.SAGITTARIUS, hit.getDamage());
				if (--ammunition == 0) {
					break;
				}
			}
	}

}
