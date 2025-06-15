package com.bestbudz.rs2.auto.combat;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotPrivileges;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.auto.combat.config.TimingManager;
import com.bestbudz.rs2.auto.combat.equipment.EquipmentManager;
import com.bestbudz.rs2.auto.combat.handlers.CombatHandler;
import com.bestbudz.rs2.auto.combat.handlers.MeleeStyleManager;
import com.bestbudz.rs2.auto.combat.handlers.StyleSelector;
import com.bestbudz.rs2.auto.combat.handlers.TargetFinder;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.List;

/**
 * Redesigned auto-combat system with pure time-based switching and memory for variety
 * Ensures balanced progression across all combat styles without rapid switching
 */
public class AutoCombat {

	private final Stoner stoner;
	private final EquipmentManager equipmentManager;
	private final StyleSelector styleSelector;
	private final CombatHandler combatHandler;
	private final TargetFinder targetFinder;
	private final TimingManager timingManager;
	private final MeleeStyleManager meleeStyleManager;

	public static boolean mageStyle = false;
	private boolean enabled = true;

	public AutoCombat(Stoner stoner) {
		this.stoner = stoner;
		this.equipmentManager = new EquipmentManager(stoner);
		this.styleSelector = new StyleSelector(stoner, equipmentManager);
		this.combatHandler = new CombatHandler(stoner, equipmentManager);
		this.targetFinder = new TargetFinder(stoner);
		this.timingManager = new TimingManager();
		this.meleeStyleManager = new MeleeStyleManager(stoner);
	}

	/**
	 * Main processing method - called from Stoner.process()
	 */
	public void process() {
		if (!enabled || !stoner.getController().canAssaultNPC()) {
			return;
		}

		if (stoner.getMovementHandler().moving() ||
			stoner.isBusy() ||
			stoner.getDialogue() != null) {
			return;
		}

		Mob target = targetFinder.findNearestTarget();
		if (target != null) {
			processTargetEngagement(target);
		}
	}

	private void processTargetEngagement(Mob target) {
		int distance = targetFinder.getDistanceToTarget(target);

		// Check if we should consider gear optimization (every 30 seconds)
		if (timingManager.shouldCheckGearOptimization()) {
			handleGearOptimization(target, distance);
		}

		// Handle melee style rotation when using melee combat
		CombatTypes currentCombatStyle = equipmentManager.getCurrentCombatStyle();
		if (currentCombatStyle == CombatTypes.MELEE) {
			meleeStyleManager.processStyleRotation();
		}

		// Always attack regardless of gear optimization
		engageTarget(target);
	}

	// Add method to get comprehensive timing info including melee styles:
	public String getComprehensiveTimingInfo() {
		StringBuilder info = new StringBuilder();
		info.append(timingManager.getTimingInfo());

		CombatTypes currentStyle = equipmentManager.getCurrentCombatStyle();
		if (currentStyle == CombatTypes.MELEE) {
			info.append("\n").append(meleeStyleManager.getTimingInfo());
		}

		return info.toString();
	}

	// Add method to manually force melee style:
	public void forceMeleeStyle(String styleName) {
		Equipment.AssaultStyles style;
		switch (styleName.toLowerCase()) {
			case "accurate":
				style = Equipment.AssaultStyles.ACCURATE;
				break;
			case "aggressive":
				style = Equipment.AssaultStyles.AGGRESSIVE;
				break;
			case "controlled":
				style = Equipment.AssaultStyles.CONTROLLED;
				break;
			case "defensive":
				style = Equipment.AssaultStyles.DEFENSIVE;
				break;
			default:
				stoner.send(new SendMessage("Invalid style. Use: accurate, aggressive, controlled, or defensive"));
				return;
		}

		meleeStyleManager.forceStyle(style);
	}

	/**
	 * Handle gear optimization based on timing and variety
	 */
	private void handleGearOptimization(Mob target, int distance) {
		CombatTypes currentStyle = equipmentManager.getCurrentCombatStyle();

		// Update timing manager with current style and gear
		String gearFingerprint = timingManager.generateGearFingerprint(equipmentManager.getEquippedItems());
		timingManager.setCurrentStyle(currentStyle, gearFingerprint);

		// Check if we should consider switching styles
		if (shouldConsiderStyleSwitch(target, distance)) {
			CombatTypes newStyle = selectOptimalStyle(target, distance, currentStyle);
			if (newStyle != null && newStyle != currentStyle) {
				executeStyleSwitch(newStyle, target);
			}
		}
	}

	/**
	 * Determine if we should consider switching styles
	 */
	private boolean shouldConsiderStyleSwitch(Mob target, int distance) {
		// Always switch if no gear equipped
		if (equipmentManager.getCurrentCombatStyle() == null) {
			return true;
		}

		// Force variety if stuck too long
		if (timingManager.shouldForceVariety()) {
			timingManager.updateVarietyForceTime();
			stoner.send(new SendMessage("Auto-combat: Forcing style variety after extended period"));
			return true;
		}

		// Check if minimum time in current style has passed
		if (!timingManager.canConsiderStyleSwitch()) {
			return false;
		}

		// Check distance-based necessity
		CombatTypes currentStyle = equipmentManager.getCurrentCombatStyle();
		if (styleSelector.isStyleSuboptimalForDistance(currentStyle, distance)) {
			return true;
		}

		// Probability-based switching with increasing chance over time
		int switchProbability = timingManager.getStyleSwitchProbability();
		return Utility.randomNumber(100) < switchProbability;
	}

	/**
	 * Select optimal style considering current conditions, distance, and variety
	 */
	private CombatTypes selectOptimalStyle(Mob target, int distance, CombatTypes currentStyle) {
		// Get base optimal style from style selector
		CombatTypes baseOptimal = styleSelector.determineOptimalStyle(target, distance);

		// Get styles that haven't been used recently for variety
		List<CombatTypes> unusedStyles = timingManager.getUnusedStyles();

		// Calculate scores for all available styles
		CombatTypes bestStyle = null;
		int bestScore = -1;

		CombatTypes[] allStyles = {CombatTypes.MELEE, CombatTypes.SAGITTARIUS, CombatTypes.MAGE};

		for (CombatTypes style : allStyles) {
			if (!equipmentManager.hasGearForStyle(style)) {
				continue; // Skip if no gear available
			}

			int score = calculateStyleScore(style, target, distance, baseOptimal, unusedStyles);

			if (score > bestScore) {
				bestScore = score;
				bestStyle = style;
			}
		}

		return bestStyle;
	}

	/**
	 * Calculate comprehensive score for a style considering all factors
	 */
	private int calculateStyleScore(CombatTypes style, Mob target, int distance,
									CombatTypes baseOptimal, List<CombatTypes> unusedStyles) {
		int score = 0;

		// Base preference from config
		switch (style) {
			case MELEE:
				score += AutoCombatConfig.MELEE_BASE_PREFERENCE;
				break;
			case SAGITTARIUS:
				score += AutoCombatConfig.SAGITTARIUS_BASE_PREFERENCE;
				break;
			case MAGE:
				score += AutoCombatConfig.MAGE_BASE_PREFERENCE;
				break;
		}

		// Target weakness bonus
		score += getWeaknessBonus(style, target);

		// Distance appropriateness bonus
		score += getDistanceBonus(style, distance);

		// Optimal style bonus
		if (style == baseOptimal) {
			score += 30;
		}

		// Variety bonus (haven't used recently)
		score += timingManager.getVarietyBonus(style);

		// Extra bonus for completely unused styles
		if (unusedStyles.contains(style)) {
			score += 40;
		}

		return score;
	}

	/**
	 * Get weakness bonus for a style against target
	 */
	private int getWeaknessBonus(CombatTypes style, Mob target) {
		switch (style) {
			case MELEE:
				return (int)(target.getMeleeWeaknessMod() * AutoCombatConfig.WEAKNESS_MODIFIER);
			case SAGITTARIUS:
				return (int)(target.getSagittariusWeaknessMod() * AutoCombatConfig.WEAKNESS_MODIFIER);
			case MAGE:
				return (int)(target.getMageWeaknessMod() * AutoCombatConfig.WEAKNESS_MODIFIER);
			default:
				return 0;
		}
	}

	/**
	 * Get distance appropriateness bonus for a style
	 */
	private int getDistanceBonus(CombatTypes style, int distance) {
		switch (style) {
			case MELEE:
				return distance <= AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD ?
					AutoCombatConfig.DISTANCE_PREFERENCE_BONUS : 0;
			case SAGITTARIUS:
				return (distance > 1 && distance <= AutoCombatConfig.SAGITTARIUS_MAX_EFFECTIVE_DISTANCE) ?
					AutoCombatConfig.DISTANCE_PREFERENCE_BONUS : 0;
			case MAGE:
				return distance > AutoCombatConfig.LONG_DISTANCE_THRESHOLD ?
					AutoCombatConfig.DISTANCE_PREFERENCE_BONUS : 0;
			default:
				return 0;
		}
	}

	/**
	 * Execute style switch with proper validation and timing
	 */
	private boolean executeStyleSwitch(CombatTypes targetStyle, Mob target) {
		// Check if this gear setup was recently used
		boolean success = equipmentManager.equipStyleGear(targetStyle);

		if (!success) {
			// Try fallback styles
			CombatTypes[] fallbackOrder = styleSelector.getFallbackStyleOrder(target,
				targetFinder.getDistanceToTarget(target));

			for (CombatTypes fallback : fallbackOrder) {
				if (fallback != targetStyle && equipmentManager.hasGearForStyle(fallback)) {
					success = equipmentManager.equipStyleGear(fallback);
					if (success) {
						targetStyle = fallback;
						stoner.send(new SendMessage("Auto-combat: Switched to fallback style " +
							targetStyle.name().toLowerCase()));
						break;
					}
				}
			}
		}

		if (success) {
			// Validate the switch
			CombatTypes actualStyle = equipmentManager.getCurrentCombatStyle();
			if (actualStyle == targetStyle) {
				// Update timing manager with new style
				String gearFingerprint = timingManager.generateGearFingerprint(equipmentManager.getEquippedItems());
				timingManager.setCurrentStyle(actualStyle, gearFingerprint);

				mageStyle = actualStyle.name().equalsIgnoreCase("mage");

				stoner.send(new SendMessage("Auto-combat: Switched to " + actualStyle.name().toLowerCase() +
					" style for balanced progression"));
				return true;
			}
		}

		return false;
	}

	/**
	 * Engage target with current equipped gear
	 */
	private void engageTarget(Mob target) {
		if (combatHandler.shouldCastSpells(target)) {
			combatHandler.castSpell(target, AutoCombatConfig.DAMAGE_SPELLS);
			return;
		}

		combatHandler.trySpecialAttack();
		combatHandler.initiateCombat(target);
	}

	/**
	 * Get comprehensive timing and status information
	 */
	public String getTimingInfo() {
		return timingManager.getTimingInfo();
	}

	/**
	 * Toggle auto combat on/off
	 */
	public void toggle() {
		// Discord bot cannot disable auto-combat
		if (DiscordBotPrivileges.isDiscordBot(stoner)) {
			stoner.send(new SendMessage("Auto-combat is always enabled for Discord bots!"));
			return;
		}

		// Normal toggle logic
		enabled = !enabled;
		String status = enabled ? "enabled" : "disabled";
		stoner.send(new SendMessage("Auto combat " + status + "."));

		if (enabled) {
			// Reset timings when re-enabling
			timingManager.resetTimings();
		}
	}

	/**
	 * Manual style switch (for testing or user preference)
	 */
	public void forceStyleSwitch(CombatTypes style) {
		if (equipmentManager.hasGearForStyle(style)) {
			boolean success = equipmentManager.equipStyleGear(style);
			if (success) {
				String gearFingerprint = timingManager.generateGearFingerprint(equipmentManager.getEquippedItems());
				timingManager.setCurrentStyle(style, gearFingerprint);
				stoner.send(new SendMessage("Manually switched to " + style.name().toLowerCase() + " style"));
			}
		} else {
			stoner.send(new SendMessage("No gear available for " + style.name().toLowerCase() + " style"));
		}
	}

	// Getters
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public EquipmentManager getEquipmentManager() {
		return equipmentManager;
	}

	public StyleSelector getStyleSelector() {
		return styleSelector;
	}

	public CombatHandler getCombatHandler() {
		return combatHandler;
	}

	public TargetFinder getTargetFinder() {
		return targetFinder;
	}

	public TimingManager getTimingManager() {
		return timingManager;
	}

	public MeleeStyleManager getMeleeStyleManager() {
		return meleeStyleManager;
	}
}