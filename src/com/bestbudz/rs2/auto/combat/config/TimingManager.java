package com.bestbudz.rs2.auto.combat.config;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import java.util.ArrayList;
import java.util.List;

public class TimingManager {

	private long currentStyleStartTime = 0;
	private CombatTypes currentStyle = null;
	private long lastGearCheckTime = 0;
	private long lastVarietyForceTime = 0;

	private List<CombatTypes> styleHistory = new ArrayList<>();
	private List<String> gearHistory = new ArrayList<>();

	private long currentStyleDuration = 0;

	public TimingManager() {
		long currentTime = System.currentTimeMillis();
		this.currentStyleStartTime = currentTime;
		this.lastGearCheckTime = currentTime;
		this.lastVarietyForceTime = currentTime;
		generateNewStyleDuration();
	}

	public void setCurrentStyle(CombatTypes style, String gearFingerprint) {
		if (style != currentStyle) {

			if (currentStyle != null) {
				addToStyleHistory(currentStyle);
			}

			if (gearFingerprint != null && !gearFingerprint.isEmpty()) {
				addToGearHistory(gearFingerprint);
			}

			currentStyle = style;
			currentStyleStartTime = System.currentTimeMillis();
			generateNewStyleDuration();
		}
	}

	private void generateNewStyleDuration() {
		long minDuration = AutoCombatConfig.MIN_STYLE_DURATION_MS;
		long maxDuration = AutoCombatConfig.MAX_STYLE_DURATION_MS;
		currentStyleDuration = minDuration + (long)(Math.random() * (maxDuration - minDuration));
	}

	public boolean canConsiderStyleSwitch() {
		if (currentStyle == null) return true;

		long timeInCurrentStyle = System.currentTimeMillis() - currentStyleStartTime;
		return timeInCurrentStyle >= currentStyleDuration;
	}

	public int getStyleSwitchProbability() {
		if (currentStyle == null) return 100;

		long timeInCurrentStyle = System.currentTimeMillis() - currentStyleStartTime;
		long minimumTime = AutoCombatConfig.MIN_STYLE_DURATION_MS;

		if (timeInCurrentStyle < minimumTime) {
			return 0;
		}

		long extraTime = timeInCurrentStyle - minimumTime;
		long extraMinutes = extraTime / 60000;

		int baseProbability = AutoCombatConfig.STYLE_SWITCH_BASE_CHANCE;
		int additionalProbability = (int)(extraMinutes * AutoCombatConfig.STYLE_SWITCH_INCREASE_PER_MINUTE);

		return Math.min(baseProbability + additionalProbability, 95);
	}

	public boolean shouldCheckGearOptimization() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastGearCheckTime >= AutoCombatConfig.GEAR_CHECK_INTERVAL_MS) {
			lastGearCheckTime = currentTime;
			return true;
		}
		return false;
	}

	public boolean shouldForceVariety() {
		long currentTime = System.currentTimeMillis();
		return (currentTime - lastVarietyForceTime) >= AutoCombatConfig.VARIETY_FORCE_INTERVAL_MS;
	}

	public void updateVarietyForceTime() {
		lastVarietyForceTime = System.currentTimeMillis();
	}

	public int getVarietyBonus(CombatTypes style) {
		if (style == null) return 0;

		int bonus = AutoCombatConfig.STYLE_VARIETY_BONUS;

		for (int i = 0; i < styleHistory.size(); i++) {
			if (styleHistory.get(i) == style) {

				int recentnessPenalty = (AutoCombatConfig.STYLE_VARIETY_BONUS * (3 - i)) / 3;
				bonus -= recentnessPenalty;
			}
		}

		return Math.max(bonus, 0);
	}

	public boolean wasGearRecentlyUsed(String gearFingerprint) {
		return gearHistory.contains(gearFingerprint);
	}

	private void addToStyleHistory(CombatTypes style) {
		styleHistory.add(0, style);
		while (styleHistory.size() > AutoCombatConfig.STYLE_HISTORY_SIZE) {
			styleHistory.remove(styleHistory.size() - 1);
		}
	}

	private void addToGearHistory(String gearFingerprint) {
		gearHistory.add(0, gearFingerprint);
		while (gearHistory.size() > AutoCombatConfig.GEAR_HISTORY_SIZE) {
			gearHistory.remove(gearHistory.size() - 1);
		}
	}

	public List<CombatTypes> getUnusedStyles() {
		List<CombatTypes> unused = new ArrayList<>();
		CombatTypes[] allStyles = {CombatTypes.MELEE, CombatTypes.SAGITTARIUS, CombatTypes.MAGE};

		for (CombatTypes style : allStyles) {
			if (!styleHistory.contains(style)) {
				unused.add(style);
			}
		}

		return unused;
	}

	public String generateGearFingerprint(com.bestbudz.rs2.entity.item.Item[] equipment) {
		if (equipment == null) return "empty";

		StringBuilder fingerprint = new StringBuilder();

		int[] keySlots = {
			AutoCombatConfig.WEAPON_SLOT,
			AutoCombatConfig.SHIELD_SLOT,
			0,
			4,
			7
		};

		for (int slot : keySlots) {
			if (slot < equipment.length && equipment[slot] != null) {
				fingerprint.append(equipment[slot].getId()).append("-");
			} else {
				fingerprint.append("0-");
			}
		}

		return fingerprint.toString();
	}

	public String getTimingInfo() {
		long timeInCurrentStyle = System.currentTimeMillis() - currentStyleStartTime;
		long timeUntilCanSwitch = Math.max(0, currentStyleDuration - timeInCurrentStyle);

		return String.format(
			"Style: %s, Time in style: %ds, Can switch in: %ds, Switch probability: %d%%, Style history: %s",
			currentStyle != null ? currentStyle.name() : "NONE",
			timeInCurrentStyle / 1000,
			timeUntilCanSwitch / 1000,
			getStyleSwitchProbability(),
			styleHistory.toString()
		);
	}

	public void resetTimings() {
		long currentTime = System.currentTimeMillis();
		this.currentStyleStartTime = currentTime;
		this.lastGearCheckTime = currentTime;
		this.lastVarietyForceTime = currentTime;
		this.currentStyle = null;
		this.styleHistory.clear();
		this.gearHistory.clear();
		generateNewStyleDuration();
	}

	public CombatTypes getCurrentStyle() {
		return currentStyle;
	}

	public long getTimeInCurrentStyle() {
		return System.currentTimeMillis() - currentStyleStartTime;
	}
}
