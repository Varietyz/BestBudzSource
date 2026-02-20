package com.bestbudz.rs2.content.profession.petmaster.bond;

public class PetBond {
	private double experience = 0.0;
	private int bondGrade = 1;
	private long activeTime = 0;
	private long firstSummoned = 0;

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

	public boolean meetsRequirements(int requiredGrade, long requiredActiveTime) {
		return this.bondGrade >= requiredGrade && this.activeTime >= requiredActiveTime;
	}

	public double getExperience() { return experience; }
	public void setExperience(double experience) {
		this.experience = experience;
		updateBondGrade();
	}

	public int getBondGrade() { return bondGrade; }
	public void setBondGrade(int bondGrade) { this.bondGrade = bondGrade; }

	public long getActiveTime() { return activeTime; }
	public void setActiveTime(long activeTime) { this.activeTime = activeTime; }

	public long getFirstSummoned() { return firstSummoned; }
	public void setFirstSummoned(long firstSummoned) {
		if (this.firstSummoned == 0) this.firstSummoned = firstSummoned;
	}

}
