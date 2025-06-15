package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.auto.combat.AutoCombat;
import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.auto.combat.equipment.EquipmentManager;
import com.bestbudz.rs2.auto.combat.equipment.WeaponSelector;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * FIXED: Handles combat actions with proper pathfinding integration
 */
public class CombatHandler {

	private final Stoner stoner;
	private final EquipmentManager equipmentManager;

	public CombatHandler(Stoner stoner, EquipmentManager equipmentManager) {
		this.stoner = stoner;
		this.equipmentManager = equipmentManager;
	}

	/**
	 * Check if spells should be cast
	 */
	public boolean shouldCastSpells(Mob target) {
		boolean hasMageGear = isWearingMageGear();

		if (hasMageGear) {
			return true;
		}

		// Small chance to cast without mage gear
		return Utility.randomNumber(100) < AutoCombatConfig.SPELL_CAST_CHANCE_WITHOUT_MAGE_GEAR;
	}

	/**
	 * Cast combat spell
	 */
	public void castSpell(Mob target, int[] spells) {
		int spellId = spells[Utility.randomNumber(spells.length)];

		try {
			if (stoner.getMage().getSpellCasting().isAutocasting()) {
				stoner.getMage().getSpellCasting().disableAutocast();
			}

			stoner.getMage().getSpellCasting().castCombatSpell(spellId, target);
			System.out.println("Casting spell ID: " + spellId);
			// Disable autocast after casting if not wearing mage gear
			stoner.getMage().getSpellCasting().disableAutocast();
		} catch (Exception e) {
			System.out.println("Failed to cast spell, using regular combat");
			// FIXED: Use proper combat initiation with pathfinding
			initiateProperCombat(target);
		}
	}

	/**
	 * Try to activate special attack
	 */
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

	/**
	 * Check if wearing mage gear (requires at least 3 pieces)
	 */
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

	/**
	 * Check if item name indicates mage equipment
	 */
	private boolean isMageEquipment(String name) {
		return name.contains("robe") || name.contains("wizard") || name.contains("mystic") ||
			name.contains("staff") || name.contains("wand") || name.contains("mage") ||
			name.contains("tome") || name.contains("orb") || name.contains("infinity");
	}

	/**
	 * FIXED: Initiate combat with proper pathfinding (like regular players)
	 */
	public void initiateCombat(Mob target) {
		initiateProperCombat(target);
	}

	/**
	 * CRITICAL FIX: Proper combat initiation that uses pathfinding like regular players
	 */
	private void initiateProperCombat(Mob target) {
		if (target == null) {
			return;
		}

		try {
			// Calculate distance to target
			int deltaX = Math.abs(stoner.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(stoner.getLocation().getY() - target.getLocation().getY());
			int distance = Math.max(deltaX, deltaY); // Chebyshev distance

			// CRITICAL FIX: If target is not adjacent, use pathfinding to get closer
			// This mimics how regular players engage in combat
			if (distance > 1) {
			//	System.out.println("DEBUG: Target not adjacent (distance: " + distance + "), using pathfinding");

				// Use RS317PathFinder to navigate towards the target
				// This will automatically handle obstacles and pathfinding around walls
				com.bestbudz.rs2.entity.pathfinding.RS317PathFinder.findRoute(
					stoner,
					target.getLocation().getX(),
					target.getLocation().getY(),
					true,  // run if possible
					1,     // size x
					1      // size y
				);

				// Also use the following system to keep tracking the target
				stoner.getFollowing().setFollow(target, com.bestbudz.rs2.entity.following.Following.FollowType.COMBAT);
			}

			// Set the combat target
			// The Combat.assault() method will handle the actual attack timing and validation
			stoner.getCombat().setAssault(target);

			//System.out.println("DEBUG: Discord bot initiated combat with NPC " + target.getId() +
			//	" at " + target.getLocation() + " (distance: " + distance + ")");

		} catch (Exception e) {
			System.out.println("Error in proper combat initiation: " + e.getMessage());
			e.printStackTrace();

			// Fallback: just set the assault target
			stoner.getCombat().setAssault(target);
		}
	}

	/**
	 * ENHANCED: Check if we can reach the target for combat
	 */
	public boolean canReachTarget(Mob target) {
		if (target == null) {
			return false;
		}

		try {
			// Use the same logic as Combat.withinDistanceForAssault()
			return stoner.getCombat().withinDistanceForAssault(
				stoner.getCombat().getCombatType(), false);
		} catch (Exception e) {
			System.out.println("Error checking target reachability: " + e.getMessage());
			return false;
		}
	}

	/**
	 * NEW: Force movement towards target if stuck
	 */
	public void forceMovementToTarget(Mob target) {
		if (target == null) {
			return;
		}

		try {
		//	System.out.println("DEBUG: Forcing movement to target NPC " + target.getId());

			// Reset any existing movement
			stoner.getMovementHandler().reset();

			// Use pathfinding to move towards target
			com.bestbudz.rs2.entity.pathfinding.RS317PathFinder.findRoute(
				stoner,
				target.getLocation().getX(),
				target.getLocation().getY(),
				true, 1, 1);

			// Set following to keep tracking
			stoner.getFollowing().setFollow(target, com.bestbudz.rs2.entity.following.Following.FollowType.COMBAT);

		} catch (Exception e) {
			System.out.println("Error forcing movement to target: " + e.getMessage());
		}
	}
}