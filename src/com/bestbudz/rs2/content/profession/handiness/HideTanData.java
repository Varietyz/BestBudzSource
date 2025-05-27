package com.bestbudz.rs2.content.profession.handiness;

import java.util.HashMap;
import java.util.Map;

public enum HideTanData {
	COWHIDE_LEATHER(1739, new int[] { 0, 0 }, 1741, 1, new int[0]),
	COWHIDE_HARDLEATHER(1739, new int[] { 0, 0 }, 1743, 1, new int[0]),
	SNAKEHIDE(6287, new int[] { 0, 0 }, 6289, 1, new int[0]),
	SNAKEHIDE2(7801, new int[] { 0, 0 }, 6289, 1, new int[0]),
	GREEN_LEATHER(1753, new int[] { 0, 0 }, 1745, 1, new int[] { 1136, 1066, 1100 }),

	BLUE_LEATHER(1751, new int[] { 0, 0 }, 2505, 1, new int[] { 2500, 2488, 2494 }),

	RED_LEATHER(1749, new int[] { 0, 0 }, 2507, 1, new int[] { 2502, 2490, 2496 }),

	BLACK_LEATHER(1747, new int[] { 0, 0 }, 2509, 1, new int[] { 2504, 2492, 2498 });

	private short itemId;
	private int[] bestbucks;
	private short outcome;
	private short requiredGrade;
	private int[] craftableOutcomes;
	private static Map<Short, HideTanData> hideRewards = new HashMap<Short, HideTanData>();

	public static final void declare() {
	for (HideTanData hide : values())
		hideRewards.put(Short.valueOf(hide.getOutcome()), hide);
	}

	public static HideTanData forReward(short id) {
	return hideRewards.get(Short.valueOf(id));
	}

	private HideTanData(int itemId, int[] bestbucks, int outcome, int requiredGrade, int[] craftableOutcomes) {
	this.itemId = ((short) itemId);
	this.bestbucks = bestbucks;
	this.outcome = ((short) outcome);
	this.requiredGrade = ((short) requiredGrade);
	this.craftableOutcomes = craftableOutcomes;
	}

	public int[] getBestBucks() {
	return bestbucks;
	}

	public int[] getCraftableOutcomes() {
	return craftableOutcomes;
	}

	public short getItemId() {
	return itemId;
	}

	public short getOutcome() {
	return outcome;
	}

	public short getRequiredGrade() {
	return requiredGrade;
	}
}
