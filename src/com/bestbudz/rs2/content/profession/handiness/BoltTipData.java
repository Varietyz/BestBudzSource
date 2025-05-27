package com.bestbudz.rs2.content.profession.handiness;

public enum BoltTipData {
	RUBY(0, 0, 0, 0.0D);

	public final int gem;
	public final int tips;
	public final int grade;
	public final double exp;

	private BoltTipData(int gem, int tips, int grade, double exp) {
	this.gem = gem;
	this.tips = tips;
	this.grade = grade;
	this.exp = exp;
	}
}
