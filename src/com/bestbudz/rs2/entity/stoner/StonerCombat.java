package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.auto.combat.AutoCombat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import static com.bestbudz.rs2.content.combat.Combat.getHitTypeForCombatType;
import com.bestbudz.rs2.content.combat.CombatInterface;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.StonerCombatInterface;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.content.combat.impl.Skulling;
import com.bestbudz.rs2.content.combat.impl.SpecialAssault;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.mob.RareDropEP;
import com.bestbudz.rs2.entity.pets.PetCombat;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.GodWarsNpc;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles all combat-related functionality for Stoner
 */
public class StonerCombat {
	private final Stoner stoner;
	private final CombatInterface combatInterface;
	private final SpecialAssault specialAssault;
	private final Skulling skulling;
	private final RareDropEP rareDropEP;
	private final AutoCombat autoCombat;

	// Combat state
	private long aggressionDelay = System.currentTimeMillis();
	private long currentStunDelay;
	private long setStunDelay;

	// Special combat flags
	private boolean hitZulrah;

	public StonerCombat(Stoner stoner) {
		this.stoner = stoner;
		this.combatInterface = new StonerCombatInterface(stoner);
		this.specialAssault = new SpecialAssault(stoner);
		this.skulling = new Skulling();
		this.rareDropEP = new RareDropEP();
		this.autoCombat = new AutoCombat(stoner);
	}

	public void process() {
		if (stoner.isPetStoner()) {
			// ENHANCED: Pets should get FULL combat processing, not minimal
			if (stoner.getClient() != null) {
				stoner.getClient().resetLastPacketReceived();
			}

			// Process all combat systems for pets
			if (stoner.getFollowing() != null) {
				stoner.getFollowing().process();
			}
			processPetCombatWithNpcAnimations();

			// Enhanced aggression check for pets
			doAgressionCheck();

			// CRITICAL: Allow pets to use AutoCombat for better targeting
			if (stoner.getAutoCombat() != null) {
				stoner.getAutoCombat().process();
			}

			return;
		}

		// Regular player processing...
		stoner.getFollowing().process();
		stoner.getCombat().process();
		doAgressionCheck();
		autoCombat.process();
	}

	public void reset() {
		// Combat-specific reset logic if needed
	}

	// Combat interface delegation methods
	public void afterCombatProcess(Entity assault) {
		combatInterface.afterCombatProcess(assault);
	}

	public boolean canAssault() {
		return combatInterface.canAssault();
	}

	public void checkForDeath() {
		combatInterface.checkForDeath();
	}

	public int getCorrectedDamage(int damage) {
		return combatInterface.getCorrectedDamage(damage);
	}

	public int getMaxHit(CombatTypes type) {
		// ENHANCED: Special handling for pets
		if (stoner.isPetStoner()) {
			Integer petBaseDamage = (Integer) stoner.getAttributes().get("PET_BASE_DAMAGE");
			if (petBaseDamage != null) {
				// Calculate pet damage based on their grades and base damage
				long attackGrade = stoner.getGrades()[0]; // Attack
				long strengthGrade = stoner.getGrades()[1]; // Strength

				// Pet damage formula: base damage + grade bonuses
				int maxHit = petBaseDamage + (int)(attackGrade / 50) + (int)(strengthGrade / 50);

				return Math.max(1, maxHit); // Ensure at least 1 damage
			} else {
				// Fallback if no base damage set
				return 50; // Default pet damage
			}
		}

		// Use existing combat interface for regular players
		return combatInterface.getMaxHit(type);
	}

	public void hit(Hit hit) {
		if (!stoner.canTakeDamage()) {
			return;
		}

		if (stoner.isDead()) {
			hit.setDamage(0);
		} else {
			hit.setDamage(getCorrectedDamage((int) hit.getDamage()));
		}

		if (hit.getDamage() > stoner.getGrades()[3]) {
			hit.setDamage(stoner.getGrades()[3]);
		}

		// Apply damage to HP
		stoner.getGrades()[3] = Math.max(0, stoner.getGrades()[3] - hit.getDamage());

		// Update visual hit markers
		if (!stoner.getUpdateFlags().isHitUpdate()) {
			stoner.getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		} else {
			stoner.getUpdateFlags().sendHit2(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		}

		if (hit.getAssaulter() != null) {
			stoner.getCombat().getDamageTracker().addDamage(hit.getAssaulter(), hit.getDamage());

			// Enhanced retaliation for pets and players
			if (stoner.isRetaliate() && stoner.getCombat().getAssaulting() == null) {
				stoner.getCombat().setAssault(hit.getAssaulter());
			}

			// CRITICAL: Call pet combat damage notification
			if (hit.getAssaulter() != null && !hit.getAssaulter().isNpc() &&
				hit.getAssaulter() instanceof Stoner && ((Stoner)hit.getAssaulter()).isPetStoner()) {
				PetCombat.onPetDealDamage((Stoner)hit.getAssaulter(), stoner, (int)hit.getDamage());
			}
		}

		// Check for death after damage is applied
		if (!stoner.isDead()) {
			checkForDeath();
		}

		// Notify the attacker that they hit this target
		if (hit.getAssaulter() != null) {
			hit.getAssaulter().onHit(stoner, hit);
		}

		// CRITICAL: For pets taking damage, trigger combat response
		if (stoner.isPetStoner()) {
			PetCombat.onPetTakeDamage(stoner, (int)hit.getDamage());
		}
	}

	public boolean isIgnoreHitSuccess() {
		return combatInterface.isIgnoreHitSuccess();
	}

	public void onAssault(Entity assault, long hit, CombatTypes type, boolean success) {
		combatInterface.onAssault(assault, hit, type, success);
	}

	public void onCombatProcess(Entity assault) {
		combatInterface.onCombatProcess(assault);
	}

	public void onHit(Entity e, Hit hit) {
		combatInterface.onHit(e, hit);

		if (e.isNpc()) {
			Mob m = World.getNpcs()[e.getIndex()];
			if (m != null) {
				rareDropEP.forHitOnMob(stoner, m, hit);
			}
		}
	}

	public void updateCombatType() {
		CombatTypes type;
		if (stoner.getMage().getSpellCasting().isCastingSpell()) {
			type = CombatTypes.MAGE;
		} else {
			type = EquipmentConstants.getCombatTypeForWeapon(stoner);
		}

		if (type != CombatTypes.MAGE) {
			stoner.send(new SendConfig(333, 0));
		}

		stoner.getCombat().setCombatType(type);

		switch (type) {
			case MELEE:
				stoner.getEquipment().updateMeleeDataForCombat();
				break;
			case SAGITTARIUS:
				stoner.getEquipment().updateSagittariusDataForCombat();
				break;
			default:
				break;
		}
	}

	public void retaliate(Entity assaulted) {
		if (assaulted != null) {
			if (stoner.isRetaliate() && stoner.getCombat().getAssaulting() == null && !stoner.getMovementHandler().moving()) {
				stoner.getCombat().setAssault(assaulted);
			}
		}
	}

	public void doAgressionCheck() {
		if (!stoner.getController().canAssaultNPC()) {
			return;
		}

		// Handle special mob sequences
		short[] override = new short[3];
		if ((stoner.getCombat().inCombat()) && (stoner.getCombat().getLastAssaultedBy().isNpc())) {
			Mob m = World.getNpcs()[stoner.getCombat().getLastAssaultedBy().getIndex()];
			if (m != null) {
				if (m.getId() == 2215) {
					override[0] = 2216; override[1] = 2217; override[2] = 2218;
				} else if (m.getId() == 3162) {
					override[0] = 3163; override[1] = 3164; override[2] = 3165;
				} else if (m.getId() == 2205) {
					override[0] = 2206; override[1] = 2207; override[2] = 2208;
				} else if (m.getId() == 3129) {
					override[0] = 3130; override[1] = 3131; override[2] = 3132;
				}
			}
		}

		// Enhanced NPC detection
		java.util.List<Mob> npcsToCheck = new java.util.ArrayList<>();

		if (World.isDiscordBot(stoner)) {
			// Discord bot: Use world NPCs directly with proper filtering
			Mob[] worldNpcs = World.getNpcs();
			if (worldNpcs != null) {
				for (Mob npc : worldNpcs) {
					if (npc != null && npc.isActive() && npc.getLocation().getZ() == stoner.getLocation().getZ()) {
						int distance = Math.max(
							Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
							Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
						);
						if (distance <= 25) {
							npcsToCheck.add(npc);
						}
					}
				}
			}
		} else {
			// Regular players: Use client NPCs but also check world NPCs as fallback
			if (stoner.getClient().getNpcs() != null && !stoner.getClient().getNpcs().isEmpty()) {
				npcsToCheck.addAll(stoner.getClient().getNpcs());
			} else {
				Mob[] worldNpcs = World.getNpcs();
				if (worldNpcs != null) {
					for (Mob npc : worldNpcs) {
						if (npc != null && npc.isActive() && npc.getLocation().getZ() == stoner.getLocation().getZ()) {
							int distance = Math.max(
								Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
								Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
							);
							if (distance <= 25) {
								npcsToCheck.add(npc);
							}
						}
					}
				}
			}
		}

		// Process each NPC for aggression
		for (Mob npc : npcsToCheck) {
			if ((npc.getCombat().getAssaulting() == null) && (npc.getCombatDefinition() != null)) {
				boolean overrideCheck = false;

				// Check for override NPCs
				for (short overrideId : override) {
					if ((short) npc.getId() == overrideId) {
						overrideCheck = true;
						break;
					}
				}

				if (overrideCheck && npc.inWilderness()) {
					continue;
				}

				// Skip NPCs owned by this player
				if (npc.getOwner() == stoner) {
					continue;
				}

				// Only process actually aggressive NPCs
				if (!MobConstants.isAggressive(npc.getId())) {
					continue;
				}

				if ((npc.getLocation().getZ() == stoner.getLocation().getZ()) && (!npc.isWalkToHome())) {

					// Special handling for God Wars
					if (stoner.getController().equals(ControllerManager.GOD_WARS_CONTROLLER)) {
						GodWarsNpc npcData = GodWarsData.forId(npc.getId());
						if (npcData != null) {
							if (!npc.getCombat().inCombat()) {
								int distance = Math.max(
									Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
									Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
								);
								if (distance <= 25) {
									npc.getCombat().setAssault(stoner);
									npc.getFollowing().setFollow(stoner, Following.FollowType.COMBAT);
								}
							}
						}
						continue;
					}

					// Enhanced aggression logic for aggressive NPCs only
					if (!npc.getCombat().inCombat() || npc.inMultiArea()) {
						// Calculate proper distance
						int distance = Math.max(
							Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
							Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
						);

						// Proper aggression range calculation
						int aggroRange = npc.getSize() * 2 + 2;

						// Check if should attack
						if (overrideCheck || (distance <= aggroRange)) {
							// Only let the NPC attack the player, don't make player target NPC
							npc.getCombat().setAssault(stoner);
						}
					}
				}
			}
		}
	}

	/**
	 * Custom combat processing for pets that uses NPC animations
	 */
	private void processPetCombatWithNpcAnimations() {
		// Handle combat timer countdown
		if (stoner.getCombat().getAssaultTimer() > 0) {
			stoner.getCombat().increaseAssaultTimer(-1);
		}

		// Check if we should execute an attack
		if (stoner.getCombat().getAssaulting() != null && stoner.getCombat().getAssaultTimer() == 0) {
			System.out.println("DEBUG: Pet " + stoner.getUsername() + " executing NPC-style combat");

			// Execute NPC-style combat with proper animations
			executePetNpcCombatDirect();
		}

		// Handle combat state checks
		Entity target = stoner.getCombat().getAssaulting();
		if (target != null) {
			// Check if target is still valid
			if (!target.isActive() || target.isDead() ||
				target.getLocation().getZ() != stoner.getLocation().getZ()) {
				stoner.getCombat().reset();
				return;
			}

			// Check if we're within distance for assault
			if (!stoner.getCombat().withinDistanceForAssault(stoner.getCombat().getCombatType(), false)) {
				// Target is too far, but don't reset - let following catch up
				return;
			}

			// Check if we can assault
			if (!stoner.canAssault()) {
				stoner.getFollowing().reset();
				stoner.getCombat().reset();
				return;
			}
		}

		// Update combat timer if in combat
		if (stoner.getCombat().inCombat()) {
			// Combat timer is handled automatically by the combat system
		}
	}

	/**
	 * Execute NPC-style combat for pets with proper animations
	 */
	private void executePetNpcCombatDirect() {
		Entity target = stoner.getCombat().getAssaulting();
		if (target == null) return;

		// Get stored combat definition
		com.bestbudz.core.definitions.NpcCombatDefinition combatDef =
			(com.bestbudz.core.definitions.NpcCombatDefinition) stoner.getAttributes().get("PET_COMBAT_DEFINITION");

		if (combatDef == null) {
			System.err.println("ERROR: No combat definition stored for pet " + stoner.getUsername());
			// Fallback to basic attack
			executeBasicPetAttack(target);
			return;
		}

		// Determine combat type
		CombatTypes combatType = determinePetCombatType(combatDef, target);

		// Execute the appropriate combat type
		Animation attackAnimation = null;
		com.bestbudz.rs2.content.combat.impl.Assault assault = null;
		int maxHit = 50;
		int hitDelay = 2;
		int attackSpeed = 4;

		switch (combatType) {
			case MELEE:
				attackAnimation = (Animation) stoner.getAttributes().get("PET_MELEE_ANIMATION");
				assault = (com.bestbudz.rs2.content.combat.impl.Assault) stoner.getAttributes().get("PET_MELEE_ASSAULT");
				Integer meleeMax = (Integer) stoner.getAttributes().get("PET_MELEE_MAX_HIT");
				maxHit = meleeMax != null ? meleeMax : 50;
				System.out.println("DEBUG: Pet using MELEE attack");
				break;

			case MAGE:
				attackAnimation = (Animation) stoner.getAttributes().get("PET_MAGE_ANIMATION");
				assault = (com.bestbudz.rs2.content.combat.impl.Assault) stoner.getAttributes().get("PET_MAGE_ASSAULT");
				Integer mageMax = (Integer) stoner.getAttributes().get("PET_MAGE_MAX_HIT");
				maxHit = mageMax != null ? mageMax : 50;

				// Handle mage graphics and projectiles
				com.bestbudz.rs2.entity.Graphic startGraphic =
					(com.bestbudz.rs2.entity.Graphic) stoner.getAttributes().get("PET_MAGE_START");
				com.bestbudz.rs2.entity.Projectile projectile =
					(com.bestbudz.rs2.entity.Projectile) stoner.getAttributes().get("PET_MAGE_PROJECTILE");

				if (startGraphic != null) {
					stoner.getUpdateFlags().sendGraphic(startGraphic);
				}
				if (projectile != null) {
					World.sendProjectile(projectile, stoner, target);
				}

				System.out.println("DEBUG: Pet using MAGE attack with graphics");
				break;

			case SAGITTARIUS:
				attackAnimation = (Animation) stoner.getAttributes().get("PET_SAGITTARIUS_ANIMATION");
				assault = (com.bestbudz.rs2.content.combat.impl.Assault) stoner.getAttributes().get("PET_SAGITTARIUS_ASSAULT");
				Integer sagittariusMax = (Integer) stoner.getAttributes().get("PET_SAGITTARIUS_MAX_HIT");
				maxHit = sagittariusMax != null ? sagittariusMax : 50;

				// Handle sagittarius graphics and projectiles
				com.bestbudz.rs2.entity.Graphic startGraphic2 =
					(com.bestbudz.rs2.entity.Graphic) stoner.getAttributes().get("PET_SAGITTARIUS_START");
				com.bestbudz.rs2.entity.Projectile projectile2 =
					(com.bestbudz.rs2.entity.Projectile) stoner.getAttributes().get("PET_SAGITTARIUS_PROJECTILE");

				if (startGraphic2 != null) {
					stoner.getUpdateFlags().sendGraphic(startGraphic2);
				}
				if (projectile2 != null) {
					World.sendProjectile(projectile2, stoner, target);
				}

				System.out.println("DEBUG: Pet using SAGITTARIUS attack");
				break;

			default:
				System.out.println("DEBUG: Pet using FALLBACK attack");
				executeBasicPetAttack(target);
				return;
		}

		// Get timing from assault data
		if (assault != null) {
			hitDelay = assault.getHitDelay();
			attackSpeed = assault.getAssaultDelay();
		}

		// CRITICAL: Play the NPC animation
		if (attackAnimation != null) {
			System.out.println("DEBUG: Pet playing NPC animation: " + attackAnimation.getId());
			stoner.getUpdateFlags().sendAnimation(attackAnimation);

			// Face the target when attacking
			stoner.face(target);

			// Call combat process methods for proper integration
			stoner.onCombatProcess(target);
		} else {
			System.err.println("ERROR: No attack animation found for pet combat type: " + combatType);
			executeBasicPetAttack(target);
			return;
		}

		// Calculate damage using the NPC's max hit
		int damage = com.bestbudz.core.util.Utility.randomNumber(maxHit + 1);

		// Create and schedule hit
		Hit hit = new Hit(stoner, damage, getHitTypeForCombatType(combatType));

		// Update combat timer for next attack
		stoner.getCombat().updateTimers(attackSpeed);

		// Schedule the hit
		com.bestbudz.core.task.TaskQueue.queue(
			new com.bestbudz.core.task.impl.HitTask(hitDelay, false, hit, target)
		);

		// Call after combat process
		stoner.afterCombatProcess(target);

		System.out.println("DEBUG: Pet " + stoner.getUsername() + " used animation " +
			attackAnimation.getId() + " for " + damage + " damage (max: " + maxHit + ")");
	}

	/**
	 * Fallback basic attack for pets when NPC combat data is missing
	 */
	private void executeBasicPetAttack(Entity target) {
		System.out.println("DEBUG: Pet using fallback basic attack");

		// Use basic melee animation
		stoner.getUpdateFlags().sendAnimation(new Animation(422, 0));
		stoner.face(target);
		stoner.onCombatProcess(target);

		// Basic damage calculation
		int damage = com.bestbudz.core.util.Utility.randomNumber(51); // 0-50 damage

		// Create and schedule hit
		Hit hit = new Hit(stoner, damage, Hit.HitTypes.MELEE);

		// Update combat timer
		stoner.getCombat().updateTimers(4); // 4 tick attack speed

		// Schedule the hit
		com.bestbudz.core.task.TaskQueue.queue(
			new com.bestbudz.core.task.impl.HitTask(2, false, hit, target)
		);

		stoner.afterCombatProcess(target);
	}

	/**
	 * Determine which combat type the pet should use
	 */
	private CombatTypes determinePetCombatType(com.bestbudz.core.definitions.NpcCombatDefinition combatDef, Entity target) {
		double distance = stoner.getCombat().getDistanceFromTarget();

		switch (combatDef.getCombatType()) {
			case MELEE:
				return CombatTypes.MELEE;
			case MAGE:
				return CombatTypes.MAGE;
			case SAGITTARIUS:
				return CombatTypes.SAGITTARIUS;
			case MELEE_AND_MAGE:
				// Use melee if close, mage if far or random choice
				if (distance <= 1 && com.bestbudz.core.util.Utility.randomNumber(2) == 0) {
					return CombatTypes.MELEE;
				} else {
					return CombatTypes.MAGE;
				}
			case MELEE_AND_SAGITTARIUS:
				// Use melee if close, sagittarius if far
				if (distance <= 1 && com.bestbudz.core.util.Utility.randomNumber(2) == 0) {
					return CombatTypes.MELEE;
				} else {
					return CombatTypes.SAGITTARIUS;
				}
			case SAGITTARIUS_AND_MAGE:
				// Random between sagittarius and mage
				return com.bestbudz.core.util.Utility.randomNumber(2) == 0 ?
					CombatTypes.SAGITTARIUS : CombatTypes.MAGE;
			case ALL:
				// Random between all three, but prefer melee if close
				if (distance <= 1) {
					int roll = com.bestbudz.core.util.Utility.randomNumber(2);
					return roll == 0 ? CombatTypes.MELEE :
						(com.bestbudz.core.util.Utility.randomNumber(2) == 0 ? CombatTypes.MAGE : CombatTypes.SAGITTARIUS);
				} else {
					return com.bestbudz.core.util.Utility.randomNumber(2) == 0 ?
						CombatTypes.MAGE : CombatTypes.SAGITTARIUS;
				}
			default:
				return CombatTypes.MELEE;
		}
	}

	/**
	 * Convert combat type to hit type
	 */
	private Hit.HitTypes getHitTypeForCombatType(CombatTypes combatType) {
		switch (combatType) {
			case MELEE:
				return Hit.HitTypes.MELEE;
			case MAGE:
				return Hit.HitTypes.MAGE;
			case SAGITTARIUS:
				return Hit.HitTypes.SAGITTARIUS;
			default:
				return Hit.HitTypes.MELEE;
		}
	}

	public void displayCombatStatus() {
		String status = FormulaData.getCombatStatus(stoner);
		double effectiveness = FormulaData.getCombatEffectiveness(stoner);

		stoner.getClient().queueOutgoingPacket(new SendMessage(
			"@gre@Combat Status: " + status + " (Effectiveness: " +
				String.format("%.1f", effectiveness * 100) + "%)"
		));
	}

	public void resetAggression() {
		aggressionDelay = System.currentTimeMillis();
	}

	// Getters and setters
	public CombatInterface getCombatInterface() { return combatInterface; }
	public SpecialAssault getSpecialAssault() { return specialAssault; }
	public Skulling getSkulling() { return skulling; }
	public RareDropEP getRareDropEP() { return rareDropEP; }
	public AutoCombat getAutoCombat() { return autoCombat; }

	public long getAggressionDelay() { return aggressionDelay; }
	public void setAggressionDelay(long aggressionDelay) { this.aggressionDelay = aggressionDelay; }

	public long getCurrentStunDelay() { return currentStunDelay; }
	public void setCurrentStunDelay(long delay) { currentStunDelay = delay; }

	public long getSetStunDelay() { return setStunDelay; }
	public void setSetStunDelay(long delay) { setStunDelay = delay; }

	public boolean isHitZulrah() { return hitZulrah; }
	public void setHitZulrah(boolean hitZulrah) { this.hitZulrah = hitZulrah; }
}