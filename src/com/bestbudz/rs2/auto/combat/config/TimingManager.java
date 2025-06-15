package com.bestbudz.rs2.auto.combat.config;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced timing manager with memory system for variety and balanced progression
 */
public class TimingManager {

	// Current style timing
	private long currentStyleStartTime = 0;
	private CombatTypes currentStyle = null;
	private long lastGearCheckTime = 0;
	private long lastVarietyForceTime = 0;

	// Memory system for avoiding repetition
	private List<CombatTypes> styleHistory = new ArrayList<>();
	private List<String> gearHistory = new ArrayList<>(); // Store gear "fingerprints"

	// Random duration for current style (set when style changes)
	private long currentStyleDuration = 0;

	public TimingManager() {
		long currentTime = System.currentTimeMillis();
		this.currentStyleStartTime = currentTime;
		this.lastGearCheckTime = currentTime;
		this.lastVarietyForceTime = currentTime;
		generateNewStyleDuration();
	}

	/**
	 * Set the current combat style and reset timing
	 */
	public void setCurrentStyle(CombatTypes style, String gearFingerprint) {
		if (style != currentStyle) {
			// Add old style to history
			if (currentStyle != null) {
				addToStyleHistory(currentStyle);
			}

			// Add current gear to history
			if (gearFingerprint != null && !gearFingerprint.isEmpty()) {
				addToGearHistory(gearFingerprint);
			}

			currentStyle = style;
			currentStyleStartTime = System.currentTimeMillis();
			generateNewStyleDuration();
		}
	}

	/**
	 * Generate a random duration for the current style within configured bounds
	 */
	private void generateNewStyleDuration() {
		long minDuration = AutoCombatConfig.MIN_STYLE_DURATION_MS;
		long maxDuration = AutoCombatConfig.MAX_STYLE_DURATION_MS;
		currentStyleDuration = minDuration + (long)(Math.random() * (maxDuration - minDuration));
	}

	/**
	 * Check if enough time has passed to consider switching styles
	 */
	public boolean canConsiderStyleSwitch() {
		if (currentStyle == null) return true;

		long timeInCurrentStyle = System.currentTimeMillis() - currentStyleStartTime;
		return timeInCurrentStyle >= currentStyleDuration;
	}

	/**
	 * Get the probability of switching styles based on time spent
	 */
	public int getStyleSwitchProbability() {
		if (currentStyle == null) return 100;

		long timeInCurrentStyle = System.currentTimeMillis() - currentStyleStartTime;
		long minimumTime = AutoCombatConfig.MIN_STYLE_DURATION_MS;

		if (timeInCurrentStyle < minimumTime) {
			return 0; // No chance before minimum time
		}

		// Calculate additional minutes beyond minimum
		long extraTime = timeInCurrentStyle - minimumTime;
		long extraMinutes = extraTime / 60000; // Convert to minutes

		int baseProbability = AutoCombatConfig.STYLE_SWITCH_BASE_CHANCE;
		int additionalProbability = (int)(extraMinutes * AutoCombatConfig.STYLE_SWITCH_INCREASE_PER_MINUTE);

		return Math.min(baseProbability + additionalProbability, 95); // Cap at 95%
	}

	/**
	 * Check if gear optimization should be performed
	 */
	public boolean shouldCheckGearOptimization() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastGearCheckTime >= AutoCombatConfig.GEAR_CHECK_INTERVAL_MS) {
			lastGearCheckTime = currentTime;
			return true;
		}
		return false;
	}

	/**
	 * Check if variety should be forced (been too long without switching)
	 */
	public boolean shouldForceVariety() {
		long currentTime = System.currentTimeMillis();
		return (currentTime - lastVarietyForceTime) >= AutoCombatConfig.VARIETY_FORCE_INTERVAL_MS;
	}

	/**
	 * Force variety check was performed
	 */
	public void updateVarietyForceTime() {
		lastVarietyForceTime = System.currentTimeMillis();
	}

	/**
	 * Get variety bonus for a style based on recent history
	 */
	public int getVarietyBonus(CombatTypes style) {
		if (style == null) return 0;

		// Count how recently this style was used
		int bonus = AutoCombatConfig.STYLE_VARIETY_BONUS;

		for (int i = 0; i < styleHistory.size(); i++) {
			if (styleHistory.get(i) == style) {
				// Reduce bonus based on how recently it was used
				// Most recent = largest penalty, oldest = smallest penalty
				int recentnessPenalty = (AutoCombatConfig.STYLE_VARIETY_BONUS * (3 - i)) / 3;
				bonus -= recentnessPenalty;
			}
		}

		return Math.max(bonus, 0); // Don't go negative
	}

	/**
	 * Check if a gear setup was recently used
	 */
	public boolean wasGearRecentlyUsed(String gearFingerprint) {
		return gearHistory.contains(gearFingerprint);
	}

	/**
	 * Add style to history, maintaining size limit
	 */
	private void addToStyleHistory(CombatTypes style) {
		styleHistory.add(0, style); // Add to front
		while (styleHistory.size() > AutoCombatConfig.STYLE_HISTORY_SIZE) {
			styleHistory.remove(styleHistory.size() - 1); // Remove from back
		}
	}

	/**
	 * Add gear fingerprint to history, maintaining size limit
	 */
	private void addToGearHistory(String gearFingerprint) {
		gearHistory.add(0, gearFingerprint); // Add to front
		while (gearHistory.size() > AutoCombatConfig.GEAR_HISTORY_SIZE) {
			gearHistory.remove(gearHistory.size() - 1); // Remove from back
		}
	}

	/**
	 * Get styles that haven't been used recently (good for variety)
	 */
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

	/**
	 * Generate a gear fingerprint for tracking variety
	 */
	public String generateGearFingerprint(com.bestbudz.rs2.entity.item.Item[] equipment) {
		if (equipment == null) return "empty";

		StringBuilder fingerprint = new StringBuilder();

		// Include weapon, shield, and key armor pieces
		int[] keySlots = {
			AutoCombatConfig.WEAPON_SLOT,    // Weapon
			AutoCombatConfig.SHIELD_SLOT,    // Shield
			0, // Head
			4, // Body
			7  // Legs
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

	/**
	 * Get timing information for debugging
	 */
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

	/**
	 * Reset all timings (useful for testing or manual resets)
	 */
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

	// Getters
	public CombatTypes getCurrentStyle() {
		return currentStyle;
	}

	public long getTimeInCurrentStyle() {
		return System.currentTimeMillis() - currentStyleStartTime;
	}
}