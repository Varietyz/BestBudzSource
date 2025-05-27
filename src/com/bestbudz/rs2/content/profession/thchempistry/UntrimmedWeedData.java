package com.bestbudz.rs2.content.profession.thchempistry;

import java.util.HashMap;
import java.util.Map;

public enum UntrimmedWeedData {
	KUSH(199, 249, 1, 15),
	HAZE(201, 251, 1, 15),
	OG_KUSH(203, 253, 1, 15),
	POWERPLANT(205, 255, 1, 15),
	CHEESE_HAZE(207, 257, 1, 15),
	BUBBA_KUSH(3049, 2998, 1, 15),
	SPIRITWEED(12174, 12172, 1, 15),
	CHOCOLOPE(209, 259, 1, 15),
	WERGALI(14836, 14854, 1, 15),
	GORILLA_GLUE(211, 261, 1, 15),
	JACK_HERER(213, 263, 1, 15),
	DURBAN_POISON(3051, 3000, 1, 15),
	AMNESIA(215, 265, 1, 15),
	SUPER_SILVER_HAZE(2485, 2481, 1, 15),
	GIRL_SCOUT_COOKIES(217, 267, 1, 15),
	KHALIFA_KUSH(219, 269, 1, 15);

	public static final void declare() {
	for (UntrimmedWeedData data : values())
		weeds.put(Integer.valueOf(data.getUntrimmedWeed()), data);
	}

	private int untrimmedWeed;
	private int cleanWeed;
	private int gradeReq;
	private int cleaningExp;

	private static Map<Integer, UntrimmedWeedData> weeds = new HashMap<Integer, UntrimmedWeedData>();

	public static UntrimmedWeedData forId(int weedId) {
	return weeds.get(Integer.valueOf(weedId));
	}

	private UntrimmedWeedData(int untrimmedWeed, int cleanWeed, int gradeReq, int cleaningExp) {
	this.untrimmedWeed = untrimmedWeed;
	this.cleanWeed = cleanWeed;
	this.gradeReq = gradeReq;
	this.cleaningExp = cleaningExp;
	}

	public int getCleanWeed() {
	return cleanWeed;
	}

	public int getExp() {
	return cleaningExp;
	}

	public int getUntrimmedWeed() {
	return untrimmedWeed;
	}

	public int getGradeReq() {
	return gradeReq;
	}
}
