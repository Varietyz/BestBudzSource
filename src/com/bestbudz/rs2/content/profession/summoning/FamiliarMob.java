package com.bestbudz.rs2.content.profession.summoning;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobAbilities;
import com.bestbudz.rs2.entity.mob.Walking;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class FamiliarMob extends Mob {
	private final Familiar familiar;

	public FamiliarMob(Familiar familiar, Stoner owner, Location l) {
	super(owner, null, familiar.mob, false, false, true, l);

	getFollowing().setFollow(owner);
	getFollowing().setIgnoreDistance(true);

	this.familiar = familiar;

	getCombat().getMelee().setAssault(new Assault(1, 5), new Animation(familiar.assaultAnimation));
	getCombat().getMage().setAssault(new Assault(3, 5), new Animation(familiar.assaultAnimation), null, null, null);

	if (familiar.wildMob > 0) {
		getMaxGrades()[0] = ((short) familiar.assault);
		getMaxGrades()[1] = ((short) familiar.aegis);
		getMaxGrades()[3] = ((short) (Mob.getDefinition(familiar.wildMob).getGrade() * 2));

		getGrades()[0] = ((short) familiar.assault);
		getGrades()[1] = ((short) familiar.aegis);
		getGrades()[3] = ((short) (Mob.getDefinition(familiar.wildMob).getGrade() * 2));
	}
	}

	@Override
	public void afterCombatProcess(Entity assault) {
	if (assault.isDead())
		getCombat().reset();
	else {
		MobAbilities.executeAbility(getId(), this, assault);
	}

	getCombat().setCombatType(CombatTypes.MELEE);
	}

	@Override
	public void doAliveMobProcessing() {
	if ((inWilderness()) && (getId() != familiar.wildMob))
		transform(familiar.wildMob);
	else if ((!inWilderness()) && (getId() != familiar.mob)) {
		transform(familiar.mob);
	}

	if (getOwner().getSummoning().isAssault()) {
		if ((getOwner().getCombat().getAssaulting() != null) && (inMultiArea()) && (getOwner().getCombat().getAssaulting().inMultiArea())) {
			getCombat().setAssault(getOwner().getCombat().getAssaulting());
		} else if (getCombat().getAssaulting() != null) {
			getCombat().reset();
			getFollowing().setFollow(getOwner());
		}
	} else if (getCombat().getAssaulting() != null) {
		getCombat().reset();
		getFollowing().setFollow(getOwner());
	}
	}

	public Familiar getData() {
	return familiar;
	}

	@Override
	public Animation getDeathAnimation() {
	return new Animation(familiar.deathAnimation);
	}

	@Override
	public int getMaxHit(CombatTypes type) {
	if (getAttributes().get("summonfammax") != null) {
		int max = getAttributes().getInt("summonfammax");
		getAttributes().remove("summonfammax");
		return max;
	}

	return getData().max;
	}

	@Override
	public void onDeath() {
	getOwner().getSummoning().onFamiliarDeath();
	}

	@Override
	public void teleport(Location p) {
	Walking.setNpcOnTile(this, false);
	getMovementHandler().getLastLocation().setAs(new Location(p.getX(), p.getY() + 1));
	getLocation().setAs(p);
	Walking.setNpcOnTile(this, true);
	setPlacement(true);
	getMovementHandler().resetMoveDirections();
	getUpdateFlags().sendGraphic(new Graphic(getSize() == 1 ? 1314 : 1315, 0, false));
	}

	@Override
	public void updateCombatType() {
	}
}
