package com.bestbudz.rs2.content.profession.petmaster.growth;

public class GrowthRequirement {
	private final int stonerGrade;
	private final int bondGrade;
	private final long minActiveTime;

	public GrowthRequirement(int stonerGrade, int bondGrade, long minActiveTime) {
		this.stonerGrade = stonerGrade;
		this.bondGrade = bondGrade;
		this.minActiveTime = minActiveTime;
	}

	public int getStonerGrade() {
		return stonerGrade;
	}

	public int getBondGrade() {
		return bondGrade;
	}

	public long getMinActiveTime() {
		return minActiveTime;
	}
}
