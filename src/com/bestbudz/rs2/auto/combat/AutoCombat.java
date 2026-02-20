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
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.List;

public class AutoCombat {

	private final Stoner stoner;
	private final EquipmentManager equipmentManager;
	private final StyleSelector styleSelector;
	private final CombatHandler combatHandler;
	private final TargetFinder targetFinder;
	private final TimingManager timingManager;
	private final MeleeStyleManager meleeStyleManager;

	public static boolean mageStyle = false;
	private boolean enabled = false;

	public AutoCombat(Stoner stoner) {
		this.stoner = stoner;
		this.equipmentManager = new EquipmentManager(stoner);
		this.styleSelector = new StyleSelector(stoner, equipmentManager);
		this.combatHandler = new CombatHandler(stoner, equipmentManager);
		this.targetFinder = new TargetFinder(stoner);
		this.timingManager = new TimingManager();
		this.meleeStyleManager = new MeleeStyleManager(stoner);
	}

	public void process() {
		if (!isAutoCombatActive()) {
			return;
		}

		if (!stoner.getController().canAssaultNPC()) {
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

	private boolean isAutoCombatActive() {

		if (stoner.isPetStoner()) {
			return false;
		}

		if (DiscordBotPrivileges.isDiscordBot(stoner)) {
			return true;
		}
		return enabled;
	}

	private void processTargetEngagement(Mob target) {
		int distance = targetFinder.getDistanceToTarget(target);

		if (timingManager.shouldCheckGearOptimization()) {
			handleGearOptimization(target, distance);
		}

		CombatTypes currentCombatStyle = equipmentManager.getCurrentCombatStyle();
		if (currentCombatStyle == CombatTypes.MELEE) {
			meleeStyleManager.processStyleRotation();
		}

		engageTarget(target);
	}

	private void handleGearOptimization(Mob target, int distance) {
		CombatTypes currentStyle = equipmentManager.getCurrentCombatStyle();

		String gearFingerprint = timingManager.generateGearFingerprint(equipmentManager.getEquippedItems());
		timingManager.setCurrentStyle(currentStyle, gearFingerprint);

		if (shouldConsiderStyleSwitch(target, distance)) {
			CombatTypes newStyle = selectOptimalStyle(target, distance, currentStyle);
			if (newStyle != null && newStyle != currentStyle) {
				executeStyleSwitch(newStyle, target);
			}
		}
	}

	private boolean shouldConsiderStyleSwitch(Mob target, int distance) {

		if (equipmentManager.getCurrentCombatStyle() == null) {
			return true;
		}

		if (timingManager.shouldForceVariety()) {
			timingManager.updateVarietyForceTime();
			stoner.send(new SendMessage("Auto-combat: Forcing style variety after extended period"));
			return true;
		}

		if (!timingManager.canConsiderStyleSwitch()) {
			return false;
		}

		CombatTypes currentStyle = equipmentManager.getCurrentCombatStyle();
		if (styleSelector.isStyleSuboptimalForDistance(currentStyle, distance)) {
			return true;
		}

		int switchProbability = timingManager.getStyleSwitchProbability();
		return Utility.randomNumber(100) < switchProbability;
	}

	private CombatTypes selectOptimalStyle(Mob target, int distance, CombatTypes currentStyle) {

		CombatTypes baseOptimal = styleSelector.determineOptimalStyle(target, distance);

		List<CombatTypes> unusedStyles = timingManager.getUnusedStyles();

		CombatTypes bestStyle = null;
		int bestScore = -1;

		CombatTypes[] allStyles = {CombatTypes.MELEE, CombatTypes.SAGITTARIUS, CombatTypes.MAGE};

		for (CombatTypes style : allStyles) {
			if (!equipmentManager.hasGearForStyle(style)) {
				continue;
			}

			int score = calculateStyleScore(style, target, distance, baseOptimal, unusedStyles);

			if (score > bestScore) {
				bestScore = score;
				bestStyle = style;
			}
		}

		return bestStyle;
	}

	private int calculateStyleScore(CombatTypes style, Mob target, int distance,
									CombatTypes baseOptimal, List<CombatTypes> unusedStyles) {
		int score = 0;

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

		score += getWeaknessBonus(style, target);

		score += getDistanceBonus(style, distance);

		if (style == baseOptimal) {
			score += 30;
		}

		score += timingManager.getVarietyBonus(style);

		if (unusedStyles.contains(style)) {
			score += 40;
		}

		return score;
	}

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

	private boolean executeStyleSwitch(CombatTypes targetStyle, Mob target) {

		boolean success = equipmentManager.equipStyleGear(targetStyle);

		if (!success) {

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

			CombatTypes actualStyle = equipmentManager.getCurrentCombatStyle();
			if (actualStyle == targetStyle) {

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

	private void engageTarget(Mob target) {
		if (combatHandler.shouldCastSpells(target)) {
			combatHandler.castSpell(target, AutoCombatConfig.DAMAGE_SPELLS);
			return;
		}

		combatHandler.trySpecialAttack();
		combatHandler.initiateCombat(target);
	}

	public void toggle() {

    if (DiscordBotPrivileges.isDiscordBot(stoner) || stoner.isPetStoner()) {
			stoner.send(new SendMessage("Auto-combat is always enabled for Discord bots & pets!"));
			return;
		}

		enabled = !enabled;
		String status = enabled ? "enabled" : "disabled";
		stoner.send(new SendMessage("Auto combat " + status + "."));

		if (enabled) {

			timingManager.resetTimings();
		}
	}

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
