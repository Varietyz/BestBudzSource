package com.bestbudz.rs2.content.profession.petmaster.bond;

/**
 * Enhanced pet bond tracking with growth integration
 */
public class PetBond {
	private double experience = 0.0;
	private int bondGrade = 1;
	private long activeTime = 0;
	private boolean growthReady = false;
	private long firstSummoned = 0;

	// Bond grade thresholds
	private static final double[] THRESHOLDS = {
		0, 100, 300, 600, 1000, 1500, 2200, 3000, 4000, 5500, 7500
	};

	public void addExperience(double exp) {
		this.experience += exp;
		updateBondGrade();
	}

	public void addActiveTime(long time) {
		this.activeTime += time;
	}

	private void updateBondGrade() {
		for (int i = THRESHOLDS.length - 1; i >= 0; i--) {
			if (experience >= THRESHOLDS[i]) {
				bondGrade = i + 1;
				break;
			}
		}
	}

	/**
	 * Transfer experience and data from another bond (for growth)
	 */
	public void transferFrom(PetBond otherBond, double retentionRate) {
		this.experience = otherBond.experience * retentionRate;
		this.activeTime = otherBond.activeTime;
		this.firstSummoned = otherBond.firstSummoned;
		updateBondGrade();
	}

	/**
	 * Check if bond meets minimum requirements
	 */
	public boolean meetsRequirements(int requiredGrade, long requiredActiveTime) {
		return this.bondGrade >= requiredGrade && this.activeTime >= requiredActiveTime;
	}

	/**
	 * Get progress to next grade as percentage
	 */
	public double getProgressToNextGrade() {
		if (bondGrade >= THRESHOLDS.length) return 100.0;

		double currentThreshold = THRESHOLDS[bondGrade - 1];
		double nextThreshold = THRESHOLDS[bondGrade];

		if (nextThreshold == currentThreshold) return 100.0;

		return ((experience - currentThreshold) / (nextThreshold - currentThreshold)) * 100.0;
	}

	/**
	 * Get experience required for next grade
	 */
	public double getExperienceToNextGrade() {
		if (bondGrade >= THRESHOLDS.length) return 0.0;
		return THRESHOLDS[bondGrade] - experience;
	}

	// Getters and setters
	public double getExperience() { return experience; }
	public void setExperience(double experience) {
		this.experience = experience;
		updateBondGrade();
	}

	public int getBondGrade() { return bondGrade; }
	public void setBondGrade(int bondGrade) { this.bondGrade = bondGrade; }

	public long getActiveTime() { return activeTime; }
	public void setActiveTime(long activeTime) { this.activeTime = activeTime; }

	public boolean isGrowthReady() { return growthReady; }
	public void setGrowthReady(boolean growthReady) { this.growthReady = growthReady; }

	public long getFirstSummoned() { return firstSummoned; }
	public void setFirstSummoned(long firstSummoned) {
		if (this.firstSummoned == 0) this.firstSummoned = firstSummoned;
	}

	/**
	 * Get active time in human-readable format
	 */
	public String getActiveTimeFormatted() {
		long hours = activeTime / (60 * 60 * 1000);
		long minutes = (activeTime % (60 * 60 * 1000)) / (60 * 1000);
		return hours + "h " + minutes + "m";
	}

	/**
	 * Check if this is a new bond (never been summoned)
	 */
	public boolean isNewBond() {
		return firstSummoned == 0 && experience == 0.0;
	}
}