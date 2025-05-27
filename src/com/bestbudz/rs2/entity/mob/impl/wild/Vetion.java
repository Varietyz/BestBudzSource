package com.bestbudz.rs2.entity.mob.impl.wild;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Vetion extends Mob {

	private Mob pet1, pet2;
	private boolean spawnPets = true;
	private boolean secondTrans = false;
	public Vetion() {
	super(6611, true, new Location(3214, 3793, 0));
	}

	@Override
	public void hit(Hit hit) {
	if (isDead()) {
		return;
	}

	if (pet1 != null && pet2 != null) {
		if (!pet1.isDead() || !pet2.isDead()) {
			if (hit.getAssaulter() != null || !hit.getAssaulter().isNpc()) {
				hit.getAssaulter().getStoner().send(new SendMessage("@dre@Vet'ion can't take damage with his pets spawned!"));
				return;
			}
		} else if (pet1.isDead() && pet2.isDead()) {
			spawnPets = false;
			setTakeDamage(true);
		}
	}

	super.hit(hit);

	if (!secondTrans && getGrades()[3] - hit.getDamage() <= 0) {
		doReborn();
	}

	if (spawnPets && getGrades()[3] <= 127 && hit.getDamage() >= 0) {
		if (getId() == 6611) {
			spawnPets(6613);
		} else if (getId() == 6612) {
			spawnPets(6614);
		}
	}
	}

	@Override
	public void onDeath() {
	transform(6611);
	spawnPets = true;
	secondTrans = false;
	pet1 = pet2 = null;
	}

	@Override
	public void onHit(Entity entity, Hit hit) {
	if (entity != null && !entity.isNpc()) {
		int random = Utility.random(10);
		if (random == 1) {
			earthquake();
		}
	}
	}

	@Override
	public void updateCombatType() {
	if (getCombat().getAssaulting() != null) {
		if (!getCombat().getAssaulting().isNpc()) {
			if (!getCombat().withinDistanceForAssault(CombatTypes.MELEE, true)) {
				getCombat().setCombatType(CombatTypes.MAGE);
				castLightning(getCombat().getAssaulting().getStoner());
			} else {
				getCombat().setCombatType(CombatTypes.MELEE);
			}
		}
	}
	}

	private void doReborn() {
	transform(6612);
	getGrades()[3] = 255;
	getUpdateFlags().sendForceMessage("Do it again!");
	spawnPets = true;
	secondTrans = true;
	pet1 = pet2 = null;
	}

	private void castLightning(Stoner stoner) {
	for (int i = 0; i < 3; i++) {
		int offsetX = stoner.getX() - getX();
		int offsetY = stoner.getY() - getY();
		if (i == 0 || i == 2) {
			offsetX += i == 0 ? -1 : 1;
			offsetY++;
		}
		Location end = new Location(getX() + offsetX, getY() + offsetY, 0);
		World.sendProjectile(new Projectile(592, 1, 10, 100, 65, 10, 20), getLocation(), -1, (byte) offsetX, (byte) offsetY);
		World.sendStillGraphic(775, 100, end);
		TaskQueue.queue(new Task(stoner, 3, false) {
			@Override
			public void execute() {
			stop();
			}

			@Override
			public void onStop() {
			if (stoner.getLocation().equals(end)) {
				stoner.hit(new Hit(30, HitTypes.MAGE));
				stoner.getUpdateFlags().sendForceMessage("OUCH!");
			}
			}
		});
	}
	}

	private void earthquake() {
	getUpdateFlags().sendAnimation(new Animation(5507));
	for (Stoner stoner : World.getStoners()) {
		if (stoner == null || !stoner.isActive()) {
			continue;
		}

		if (Utility.getExactDistance(stoner.getLocation(), getLocation()) <= 11) {
			stoner.hit(new Hit(25 + Utility.random(20), HitTypes.MELEE));
			stoner.send(new SendMessage("Vet'ion pummels the ground sending a shattering earthquake shockwave through you."));
		}
	}
	}

	private void spawnPets(int npcID) {
	setTakeDamage(false);
	pet1 = new Mob(null, npcID, true, false, new Location(getX(), getY() - 2, getZ()));
	pet2 = new Mob(null, npcID, true, false, new Location(getX(), getY() + 2, getZ()));
	getUpdateFlags().sendForceMessage(npcID == 6613 ? "Kill, my pets!" : "Bahh! Go, dogs!!");
	pet1.getUpdateFlags().sendForceMessage("GRRRRRRRRRRRR");
	pet2.getUpdateFlags().sendForceMessage("GRRRRRRRRRRRR");
	if (getCombat().getAssaulting() != null) {
		if (!getCombat().getAssaulting().isNpc()) {
			pet1.getCombat().setAssault(getCombat().getAssaulting() == null ? getCombat().getLastAssaultedBy() : getCombat().getAssaulting());
			pet2.getCombat().setAssault(getCombat().getAssaulting() == null ? getCombat().getLastAssaultedBy() : getCombat().getAssaulting());
		}
	}
	}
}
