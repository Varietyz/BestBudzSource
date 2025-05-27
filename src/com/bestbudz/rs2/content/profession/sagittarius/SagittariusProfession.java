package com.bestbudz.rs2.content.profession.sagittarius;

import com.bestbudz.core.definitions.SagittariusWeaponDefinition;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.impl.Sagittarius;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.item.BasicItemContainer;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.mob.impl.SeaTrollQueen;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SagittariusProfession {

	public static final boolean requiresArrow(Stoner stoner, int id) {
	if (id == 4214 || id == 10034 || id == 10033 || id == 12924) {
		return false;
	}

	Item weapon = stoner.getEquipment().getItems()[3];

	if ((weapon == null) || (weapon.getSagittariusDefinition() == null) || (weapon.getSagittariusDefinition().getType() == SagittariusWeaponDefinition.SagittariusTypes.THROWN)) {
		return false;
	}

	return true;
	}

	private final Stoner stoner;

	private Item arrow = null;

	private Location aLocation = null;

	private final ItemContainer savedArrows = new BasicItemContainer(40);
	private boolean onyxEffectActive = false;

	private boolean blood_forfeit_effect = false;
	private int max_bolt_hit = 0;

	private boolean karilEffectActive = false;

	public SagittariusProfession(Stoner stoner) {
	this.stoner = stoner;
	}

	public boolean canUseSagittarius() {
	Item weapon = stoner.getEquipment().getItems()[3];
	Item ammo = stoner.getEquipment().getItems()[13];

	if ((weapon == null) || (weapon.getSagittariusDefinition() == null)) {
		return false;
	}

	if (weapon.getId() == 12926) {
		if (stoner.getToxicBlowpipe().getBlowpipeAmmo() == null || stoner.getToxicBlowpipe().getBlowpipeCharge() == 0) {
			stoner.send(new SendMessage("The blowpipe needs to be charged with Zulrah's scales and loaded with darts."));
			return false;
		}
	}

	SagittariusWeaponDefinition def = weapon.getSagittariusDefinition();
	Item[] arrows = def.getArrows();

	if ((def.getType() == SagittariusWeaponDefinition.SagittariusTypes.SHOT) && (requiresArrow(stoner, weapon.getId())) && (arrows != null) && (arrows.length != 0)) {
		if (ammo == null) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have the correct ammo to use this weapon."));
			return false;
		}

		boolean has = false;

		for (Item i : arrows) {
			if (i != null) {
				if ((ammo.equals(i)) && (ammo.getAmount() >= i.getAmount())) {
					has = true;
				}
			}
		}
		if (!has) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have the correct ammo to use this weapon."));
			return false;
		}

	}

	return true;
	}

	public void doActionsForChinchompa(Entity assaulting) {
	Item weapon = stoner.getEquipment().getItems()[3];
	if (weapon != null && weapon.getId() == 10034 || weapon.getId() == 10033) {
		assaulting.getUpdateFlags().sendGraphic(Graphic.highGraphic(157, 0));
	} else {
		return;
	}
	}

	public void doActionsForDarkBow(Entity assaulting) {
	Item weapon = stoner.getEquipment().getItems()[3];

	if ((weapon == null) || (weapon.getId() != 11235)) {
		return;
	}

	Sagittarius r = stoner.getCombat().getSagittarius();

	stoner.getSpecialAssault().isInitialized();

	r.setProjectile(new Projectile(stoner.getCombat().getSagittarius().getProjectile().getId()));
	r.getProjectile().setDelay(35);

	r.execute(assaulting);
	r.setProjectile(new Projectile(stoner.getCombat().getSagittarius().getProjectile().getId()));

	stoner.getSpecialAssault().isInitialized();
	}

	public void doOnyxEffect(int damage) {
	if (damage > 0) {
		int max = stoner.getMaxGrades()[3];
		int newLvl = stoner.getProfession().getGrades()[3] + (int) (damage * 0.25D);
		int set = newLvl > max ? max : newLvl;
		stoner.getProfession().getGrades()[3] = ((byte) set);
		stoner.getProfession().update(3);
		stoner.getClient().queueOutgoingPacket(new SendMessage("You absorb some of your opponent's life."));
	}
	onyxEffectActive = false;
	}

	public void dropArrowAfterHit() {
	if ((arrow == null) || (aLocation == null)) {
		return;
	}

	if ((arrow.getId() == 15243) || (arrow.getId() == 4740) || (arrow.getId() == 10034) || (arrow.getId() == 10033)) {
		return;
	}

	if (Utility.randomNumber(2) == 0) {
		if (stoner.inZulrah()) {
			stoner.getGroundItems().drop(arrow, stoner.getLocation());
		} else {
			stoner.getGroundItems().drop(arrow, aLocation);
		}
	}

	arrow = null;
	aLocation = null;
	}

	public void getFromAvasAccumulator() {
	for (Item i : savedArrows.getItems())
		if (i != null) {
			int r = stoner.getBox().add(new Item(i));
			savedArrows.remove(new Item(i.getId(), r));
		}
	}

	public ItemContainer getSavedArrows() {
	return savedArrows;
	}

	public boolean isKarilEffectActive() {
	return karilEffectActive;
	}

	public boolean isOnyxEffectActive() {
	return onyxEffectActive;
	}

	public void removeArrowsOnAssault() {
	Item weapon = stoner.getEquipment().getItems()[3];
	Item ammo = stoner.getEquipment().getItems()[13];
	Item cape = stoner.getEquipment().getItems()[1];

	if ((weapon == null) || (weapon.getSagittariusDefinition() == null)) {
		return;
	}

	SagittariusWeaponDefinition def = weapon.getSagittariusDefinition();

	switch (def.getType()) {
	case SHOT:
		Item[] arrows = weapon.getSagittariusDefinition().getArrows();

		for (Item i : arrows) {
			if (i != null && ammo != null) {
				if ((ammo.equals(i)) && (ammo.getAmount() >= i.getAmount())) {
					arrow = new Item(i.getId(), i.getAmount());
					break;
				}
			}
		}

		if (ammo != null && arrow != null) {
			if (cape != null && (cape.getId() == 10499 || cape.getId() == 10498)) {
				if (Utility.randomNumber(100) >= 10) {
					return;
				}
			}

			ammo.remove(arrow.getAmount());

			if (ammo.getAmount() == 0) {
				stoner.getEquipment().unequip(13);
				stoner.getClient().queueOutgoingPacket(new SendMessage("You have run out of ammo."));
			} else {
				stoner.getEquipment().update(13);
			}

			Entity assault = stoner.getCombat().getAssaulting();
			aLocation = (assault == null ? stoner.getLocation() : assault.getLocation());

			if (assault != null && assault instanceof SeaTrollQueen) {
				aLocation = new Location(2344, 3699);
			}
		}
		break;
	case THROWN:
		if (cape != null && (cape.getId() == 10499 || cape.getId() == 10498)) {
			if (Utility.randomNumber(100) >= 10) {
				return;
			}
		}

		if (weapon.getAmount() == 1) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You threw the last of your ammo!"));
		}
		weapon.remove(1);
		if (weapon.getAmount() == 0)
			stoner.getEquipment().unequip(3);
		else {
			stoner.getEquipment().update(3);
		}

		arrow = new Item(weapon.getId(), 1);
		Entity assault = stoner.getCombat().getAssaulting();
		aLocation = (assault == null ? stoner.getLocation() : assault.getLocation());
		break;
	}
	}

	public void setKarilEffectActive(boolean karilEffectActive) {
	this.karilEffectActive = karilEffectActive;
	}

	public void setOnyxEffectActive(boolean onyxEffectActive) {
	this.onyxEffectActive = onyxEffectActive;
	}

	public boolean isBloodForfeitEffectActive() {
	return blood_forfeit_effect;
	}

	public void setBloodForfeitEffectActive(boolean blood_forfeit_effect) {
	this.blood_forfeit_effect = blood_forfeit_effect;
	}

	public int getMaxBoltHit() {
	return max_bolt_hit;
	}

	public void setMaxBoltHit(int max_bolt_hit) {
	this.max_bolt_hit = max_bolt_hit;
	}
}
