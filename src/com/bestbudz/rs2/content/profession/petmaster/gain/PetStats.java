package com.bestbudz.rs2.content.profession.petmaster.gain;

/**
 * Tracks comprehensive statistics for each pet type
 */
public class PetStats {
	private long totalActiveTime = 0;
	private double totalExperience = 0.0;
	private int combatParticipations = 0;
	private int growthsWitnessed = 0;
	private long distanceTraveled = 0;
	private int daysActive = 0;
	private long firstSummoned = 0;

	public void addActiveTime(long time) {
		this.totalActiveTime += time;
	}

	public void addTotalExperience(double exp) {
		this.totalExperience += exp;
	}

	public void incrementCombatParticipation() {
		this.combatParticipations++;
	}

	public void incrementGrowths() {
		this.growthsWitnessed++;
	}

	public void addDistance(long distance) {
		this.distanceTraveled += distance;
	}

	public void incrementDaysActive() {
		this.daysActive++;
	}

	/**
	 * Transfer stats from another PetStats (for growth)
	 */
	public void transferFrom(PetStats other) {
		this.totalActiveTime += other.totalActiveTime;
		this.totalExperience += other.totalExperience;
		this.combatParticipations += other.combatParticipations;
		this.growthsWitnessed = other.growthsWitnessed + 1; // +1 for this growth
		this.distanceTraveled += other.distanceTraveled;
		this.daysActive += other.daysActive;
		if (this.firstSummoned == 0) this.firstSummoned = other.firstSummoned;
	}

	// Getters and setters for database integration
	public long getTotalActiveTime() { return totalActiveTime; }
	public void setTotalActiveTime(long totalActiveTime) { this.totalActiveTime = totalActiveTime; }

	public double getTotalExperience() { return totalExperience; }
	public void setTotalExperience(double totalExperience) { this.totalExperience = totalExperience; }

	public int getCombatParticipations() { return combatParticipations; }
	public void setCombatParticipations(int combatParticipations) { this.combatParticipations = combatParticipations; }

	public int getGrowthsWitnessed() { return growthsWitnessed; }
	public void setGrowthsWitnessed(int growthsWitnessed) { this.growthsWitnessed = growthsWitnessed; }

	public long getDistanceTraveled() { return distanceTraveled; }
	public void setDistanceTraveled(long distanceTraveled) { this.distanceTraveled = distanceTraveled; }

	public int getDaysActive() { return daysActive; }
	public void setDaysActive(int daysActive) { this.daysActive = daysActive; }

	public long getFirstSummoned() { return firstSummoned; }
	public void setFirstSummoned(long time) {
		if (this.firstSummoned == 0) this.firstSummoned = time;
	}
}