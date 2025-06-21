package com.bestbudz.rs2.entity.mob.ai;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.core.definitions.NpcCombatDefinition.Mage;
import com.bestbudz.core.definitions.NpcCombatDefinition.Melee;
import com.bestbudz.core.definitions.NpcCombatDefinition.Sagittarius;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.sounds.MobSounds;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.Walking;
import com.bestbudz.rs2.entity.mob.abilities.MobAbilities;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.bosses.Kraken;
import com.bestbudz.rs2.entity.mob.bosses.Tentacles;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simplified 317-style combat handler with communication
 */
public class MobCombatHandler {
	private final Mob mob;
	private final MobCommunication communication;
	private List<Stoner> combatants;
	private byte combatIndex = 0;
	private boolean assaulted = false;

	public MobCombatHandler(Mob mob) {
		this.mob = mob;
		this.communication = new MobCommunication(mob);
		if (mob.isAssaultable()) {
			this.combatants = new ArrayList<>();
		}
	}

	/**
	 * Simple 317-style processing
	 */
	public void process() {
		if (!mob.isAssaultable()) {
			return;
		}

		if (mob.isDead()) {
			mob.getCombat().reset();
			return;
		}

		// Clean up dead combatants
		manageCombatants();

		// Simple movement logic
		handleMovement();

		// Process combat
		mob.getCombat().process();
	}

	private void handleMovement() {
		// Walk home if too far from spawn
		if (mob.getMovementController().isWalkToHome()) {
			mob.getCombat().reset();
			mob.getFollowing().reset();
			com.bestbudz.core.task.TaskQueue.queue(
				new com.bestbudz.core.task.impl.MobWalkTask(mob, mob.getMovementController().getSpawnLocation(), true));
			return;
		}

		// Random walking when not in combat
		if (!mob.isDead() &&
			mob.getCombat().getAssaulting() == null &&
			!mob.getFollowing().isFollowing() &&
			mob.getMovementController().isWalks() &&
			!mob.getMovementController().isForceWalking()) {

			// Simple random walk chance
			if (Utility.randomNumber(10) == 0) {
				Walking.randomWalk(mob);
			}
			mob.getMovementController().handleGodWarsMovement();
		}
	}

	/**
	 * Simple hit handling
	 */
	public void handleHit(Hit hit) {
		if (!mob.canTakeDamage()) {
			return;
		}

		if (mob.isDead()) {
			hit.setDamage(0);
		} else {
			hit.setDamage(mob.getAffectedDamage(hit));
		}

		handleSpecialNpcHits(hit);

		// Cap damage to current HP
		if (hit.getDamage() > mob.getGrades()[3]) {
			hit.setDamage(mob.getGrades()[3]);
		}

		// Apply damage
		mob.getGrades()[3] = (short) (mob.getGrades()[3] - hit.getDamage());

		// Update hit graphics
		if (!mob.getUpdateFlags().isHitUpdate()) {
			mob.getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		} else {
			mob.getUpdateFlags().sendHit2(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		}

		// Handle attacker
		if (hit.getAssaulter() != null) {
			handleAssaulterHit(hit);
		}

		// Check for death
		if (!mob.isDead()) {
			mob.getStateManager().checkForDeath();
		}

		// Callback to attacker
		if (hit.getAssaulter() != null) {
			hit.getAssaulter().onHit(mob, hit);
		}
	}

	public void onCombatProcess(Entity assault) {
		// Special case for NPC 8133
		if (mob.getId() == 8133 && mob.getCombat().getCombatType() == CombatTypes.MELEE && combatIndex == 0) {
			mob.getUpdateFlags().sendGraphic(new Graphic(1834, 0, false));
		}

		assaulted = true;

		// Send communication message (simplified)
		if (!assault.isNpc()) {
			Stoner player = (Stoner) assault;
			communication.sendCombatMessage(player);
		}

		// Handle sounds and add to combatants
		if (!assault.isNpc()) {
			Stoner p = World.getStoners()[assault.getIndex()];
			if (p != null) {
				if (mob.inMultiArea()) {
					MobSounds.sendBlockSound(p, mob.getId());
				} else {
					MobSounds.sendAssaultSound(p, mob.getId(), mob.getCombat().getCombatType(), mob.getLastDamageDealt() > 0);
				}
				addCombatant(p);
			}
		}
	}

	/**
	 * Simple combatant management
	 */
	public void manageCombatants() {
		if (combatants != null && combatants.size() > 0) {
			if (mob.getCombat().getAssaulting() == null) {
				combatants.clear();
			} else {
				for (Iterator<Stoner> i = combatants.iterator(); i.hasNext(); ) {
					Stoner p = i.next();
					if (!p.getLocation().isViewableFrom(mob.getLocation()) ||
						!p.getCombat().inCombat() || p.isDead()) {
						i.remove();
					}
				}
			}
		}
	}

	public void addCombatant(Stoner p) {
		if (combatants == null) {
			combatants = new ArrayList<>();
		}
		if (!combatants.contains(p)) {
			combatants.add(p);
		}
	}

	public void clearCombatants() {
		if (combatants != null) {
			combatants.clear();
		}
	}

	private void handleSpecialNpcHits(Hit hit) {
		int npcId = mob.getId();

		// Kraken whirlpool logic
		if (npcId == 493 && mob.getOwner() != null && mob.getOwner().inKraken()) {
			if (hit.getDamage() != 0) {
				hit.setDamage(0);
				mob.remove();
				new Tentacles(mob.getOwner(), new Location(mob.getX(), mob.getY(), mob.getOwner().getZ()));
				mob.getOwner().whirlpoolsHit++;
			}
		}

		// Kraken boss logic
		if (npcId == 496 && mob.getOwner() != null && mob.getOwner().inKraken()) {
			if (mob.getOwner().whirlpoolsHit != 4) {
				mob.getOwner().hit(new Hit(Utility.random(10)));
				mob.getOwner().send(new SendMessage(
					"You need to assault all 4 whirlpools before doing this! Remaining: " +
						(4 - mob.getOwner().whirlpoolsHit)));
				return;
			}
			if (hit.getDamage() != 0) {
				hit.setDamage(0);
				mob.remove();
				new Kraken(mob.getOwner(), new Location(3695, 5811, mob.getOwner().getZ()));
			}
		}

		// Special defense NPCs
		if (npcId == 2883 && (hit.getType() == Hit.HitTypes.MELEE || hit.getType() == Hit.HitTypes.SAGITTARIUS)) {
			hit.setDamage(0);
		}

		if (npcId == 2881 && (hit.getType() == Hit.HitTypes.MAGE || hit.getType() == Hit.HitTypes.SAGITTARIUS)) {
			hit.setDamage(0);
		}

		if (npcId == 2882 && (hit.getType() == Hit.HitTypes.MAGE || hit.getType() == Hit.HitTypes.MELEE)) {
			hit.setDamage(hit.getDamage() / 10);
		}
	}

	private void handleAssaulterHit(Hit hit) {
		// Track damage
		mob.getCombat().getDamageTracker().addDamage(hit.getAssaulter(), hit.getDamage());

		// Simple retaliation logic
		if (mob.isRetaliate()) {
			if (mob.getCombat().getAssaulting() == null || !mob.inMultiArea()) {
				mob.getCombat().setAssault(hit.getAssaulter());
			}
		}

		// Add to combatants and play sounds
		if (!hit.getAssaulter().isNpc()) {
			Stoner p = World.getStoners()[hit.getAssaulter().getIndex()];
			if (p != null) {
				MobSounds.sendBlockSound(p, mob.getId());
				if (mob.isAssaultable()) {
					addCombatant(p);
				}
			}
		}

		// Post-hit processing
		mob.doPostHitProcessing(hit);
	}

	public int getMaxHit(CombatTypes type) {
		NpcCombatDefinition combatDef = mob.getDefinitionProvider().getCombatDefinition();
		if (combatDef == null) {
			return 1;
		}

		switch (type) {
			case MAGE:
				if (combatDef.getMage() != null) {
					int base = combatDef.getMage()[combatIndex].getMax();
					int bonus = (int) (base * mob.getCombat().getHitChainBonus() / 100);
					return base + bonus;
				}
				break;

			case MELEE:
				if (combatDef.getMelee() != null) {
					int base = combatDef.getMelee()[combatIndex].getMax();
					// Special case for NPC 1673
					if (mob.getId() == 1673) {
						base = (int) (base * (1.0D + (2.0D - mob.getGrades()[3] / mob.getMaxGrades()[3])));
					}
					int bonus = (int) (base * mob.getCombat().getHitChainBonus() / 100);
					return base + bonus;
				}
				break;

			case SAGITTARIUS:
				if (combatDef.getSagittarius() != null) {
					int base = combatDef.getSagittarius()[combatIndex].getMax();
					int bonus = (int) (base * mob.getCombat().getHitChainBonus() / 100);
					return base + bonus;
				}
				break;
		}

		return 1;
	}

	public void updateCombatType() {
		NpcCombatDefinition def = mob.getDefinitionProvider().getCombatDefinition();
		if (def == null) {
			return;
		}

		CombatTypes combatType = CombatTypes.MELEE;

		// Simple combat type selection
		switch (def.getCombatType()) {
			case MAGE:
				combatType = CombatTypes.MAGE;
				break;
			case MELEE_AND_MAGE:
				combatType = (!mob.getCombat().withinDistanceForAssault(CombatTypes.MELEE, true) ||
					Utility.randomNumber(2) == 1) ? CombatTypes.MAGE : CombatTypes.MELEE;
				break;
			case MELEE_AND_SAGITTARIUS:
				combatType = (!mob.getCombat().withinDistanceForAssault(CombatTypes.MELEE, true) ||
					Utility.randomNumber(2) == 1) ? CombatTypes.SAGITTARIUS : CombatTypes.MELEE;
				break;
			case SAGITTARIUS:
				combatType = CombatTypes.SAGITTARIUS;
				break;
			case SAGITTARIUS_AND_MAGE:
				combatType = (!mob.getCombat().withinDistanceForAssault(CombatTypes.SAGITTARIUS, true) ||
					Utility.randomNumber(2) == 1) ? CombatTypes.MAGE : CombatTypes.SAGITTARIUS;
				break;
			case ALL:
				if (!mob.getCombat().withinDistanceForAssault(CombatTypes.MELEE, true)) {
					combatType = (mob.getCombat().withinDistanceForAssault(CombatTypes.SAGITTARIUS, true) &&
						Utility.randomNumber(2) == 0) ? CombatTypes.SAGITTARIUS : CombatTypes.MAGE;
				} else {
					int roll = Utility.randomNumber(3);
					combatType = (roll == 0) ? CombatTypes.MAGE :
						(roll == 1) ? CombatTypes.SAGITTARIUS : CombatTypes.MELEE;
				}
				break;
		}

		mob.getCombat().setCombatType(combatType);
		mob.getCombat().setBlockAnimation(def.getBlock());

		// Set combat data based on type
		switch (combatType) {
			case MELEE:
				if (def.getMelee() == null || def.getMelee().length < 1) {
					mob.remove();
					System.out.println("Null combat def error:melee for npc: " + mob.getId());
					return;
				}
				combatIndex = (byte) Utility.randomNumber(def.getMelee().length);
				Melee melee = def.getMelee()[combatIndex];
				mob.getCombat().getMelee().setAssault(melee.getAssault(), melee.getAnimation());
				break;
			case MAGE:
				if (def.getMage() == null || def.getMage().length < 1) {
					mob.remove();
					System.out.println("Null combat def error:mage for npc: " + mob.getId());
					return;
				}
				combatIndex = (byte) Utility.randomNumber(def.getMage().length);
				Mage mage = def.getMage()[combatIndex];
				mob.getCombat().getMage().setAssault(
					mage.getAssault(), mage.getAnimation(), mage.getStart(),
					mage.getEnd(), mage.getProjectile());
				break;
			case SAGITTARIUS:
				if (def.getSagittarius() == null || def.getSagittarius().length < 1) {
					mob.remove();
					System.out.println("Null combat def error:sagittarius for npc: " + mob.getId());
					return;
				}
				combatIndex = (byte) Utility.randomNumber(def.getSagittarius().length);
				Sagittarius sagittarius = def.getSagittarius()[combatIndex];
				mob.getCombat().getSagittarius().setAssault(
					sagittarius.getAssault(), sagittarius.getAnimation(), sagittarius.getStart(),
					sagittarius.getEnd(), sagittarius.getProjectile());
				break;
		}
	}

	public void afterCombatProcess(Entity assault) {
		if (assault.isDead()) {
			mob.getCombat().reset();
		} else {
			MobAbilities.executeAbility(mob.getId(), mob, assault);
		}
	}

	public void onHit(Entity e, Hit hit) {
		if (e.isDead() && !e.isNpc()) {
			if (combatants == null) {
				combatants = new ArrayList<>();
			}
			combatants.remove(World.getStoners()[e.getIndex()]);
		}
	}

	public void doAliveMobProcessing() {
		// Override in subclasses for custom alive processing
	}

	// Getters and setters
	public List<Stoner> getCombatants() {
		if (combatants == null) {
			combatants = new ArrayList<>();
		}
		return combatants;
	}

	public int getCombatIndex() {
		return combatIndex;
	}

	public boolean isAssaulted() {
		return assaulted;
	}

	public void setAssaulted(boolean assaulted) {
		this.assaulted = assaulted;
	}
}