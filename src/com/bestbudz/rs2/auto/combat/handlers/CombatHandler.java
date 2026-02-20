package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.auto.combat.equipment.EquipmentManager;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class CombatHandler {

	private final Stoner stoner;
	private final EquipmentManager equipmentManager;

	public CombatHandler(Stoner stoner, EquipmentManager equipmentManager) {
		this.stoner = stoner;
		this.equipmentManager = equipmentManager;
	}

	public boolean shouldCastSpells(Mob target) {
		boolean hasMageGear = isWearingMageGear();

		if (hasMageGear) {
			return true;
		}

		return Utility.randomNumber(100) < AutoCombatConfig.SPELL_CAST_CHANCE_WITHOUT_MAGE_GEAR;
	}

	public void castSpell(Mob target, int[] spells) {
		int spellId = spells[Utility.randomNumber(spells.length)];

		try {
			if (stoner.getMage().getSpellCasting().isAutocasting()) {
				stoner.getMage().getSpellCasting().disableAutocast();
			}

			stoner.getMage().getSpellCasting().castCombatSpell(spellId, target);
			System.out.println("Casting spell ID: " + spellId);

			stoner.getMage().getSpellCasting().disableAutocast();
		} catch (Exception e) {
			System.out.println("Failed to cast spell, using regular combat");

			initiateProperCombat(target);
		}
	}

	public void trySpecialAttack() {
		if (Utility.randomNumber(100) > AutoCombatConfig.SPECIAL_ATTACK_CHANCE) {
			return;
		}

		Item weapon = stoner.getEquipment().getItems()[AutoCombatConfig.WEAPON_SLOT];
		if (weapon != null && weapon.getSpecialDefinition() != null) {
			if (stoner.getSpecialAssault().getAmount() >= AutoCombatConfig.MINIMUM_SPECIAL_ENERGY &&
				!stoner.getSpecialAssault().isInitialized()) {
				stoner.getSpecialAssault().toggleSpecial();
				stoner.send(new SendMessage("Auto-combat triggered 'LIGHT EM UP'"));
			}
		}
	}

	private boolean isWearingMageGear() {
		Item[] equipment = stoner.getEquipment().getItems();
		int mageGearCount = 0;

		for (Item item : equipment) {
			if (item == null) continue;

			String name = item.getDefinition().getName().toLowerCase();
			if (isMageEquipment(name)) {
				mageGearCount++;
			}
		}

		return mageGearCount >= 3;
	}

	private boolean isMageEquipment(String name) {
		return name.contains("robe") || name.contains("wizard") || name.contains("mystic") ||
			name.contains("staff") || name.contains("wand") || name.contains("mage") ||
			name.contains("tome") || name.contains("orb") || name.contains("infinity");
	}

	public void initiateCombat(Mob target) {
		initiateProperCombat(target);
	}

	private void initiateProperCombat(Mob target) {
		if (target == null) {
			return;
		}

		try {

			int deltaX = Math.abs(stoner.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(stoner.getLocation().getY() - target.getLocation().getY());
			int distance = Math.max(deltaX, deltaY);

			if (distance > 1) {

				com.bestbudz.rs2.entity.pathfinding.RS317PathFinder.findRoute(
					stoner,
					target.getLocation().getX(),
					target.getLocation().getY(),
					true,
					1,
					1
				);

				stoner.getFollowing().setFollow(target, com.bestbudz.rs2.entity.following.Following.FollowType.COMBAT);
			}

			stoner.getCombat().setAssault(target);

		} catch (Exception e) {
			System.out.println("Error in proper combat initiation: " + e.getMessage());
			e.printStackTrace();

			stoner.getCombat().setAssault(target);
		}
	}

	public boolean canReachTarget(Mob target) {
		if (target == null) {
			return false;
		}

		try {

			return stoner.getCombat().withinDistanceForAssault(
				stoner.getCombat().getCombatType(), false);
		} catch (Exception e) {
			System.out.println("Error checking target reachability: " + e.getMessage());
			return false;
		}
	}

	public void forceMovementToTarget(Mob target) {
		if (target == null) {
			return;
		}

		try {

			stoner.getMovementHandler().reset();

			com.bestbudz.rs2.entity.pathfinding.RS317PathFinder.findRoute(
				stoner,
				target.getLocation().getX(),
				target.getLocation().getY(),
				true, 1, 1);

			stoner.getFollowing().setFollow(target, com.bestbudz.rs2.entity.following.Following.FollowType.COMBAT);

		} catch (Exception e) {
			System.out.println("Error forcing movement to target: " + e.getMessage());
		}
	}
}
