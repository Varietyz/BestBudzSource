package com.bestbudz.rs2.content;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;

public class FountainOfRune {

	private final static int OBJECT_IDENTIFICATION = 26782;

	private static final HashMap<Integer, Integer> USABLE = new HashMap<>();

	public static void declare() {
	USABLE.put(1704, 11978);
	USABLE.put(1706, 11978);
	USABLE.put(1708, 11978);
	USABLE.put(1710, 11978);
	USABLE.put(11976, 11978);
	USABLE.put(11118, 11972);
	USABLE.put(11120, 11972);
	USABLE.put(11122, 11972);
	USABLE.put(11124, 11972);
	USABLE.put(11126, 11972);
	USABLE.put(11972, 11972);
	USABLE.put(11105, 11968);
	USABLE.put(11107, 11968);
	USABLE.put(11109, 11968);
	USABLE.put(11111, 11968);
	USABLE.put(11113, 11968);
	USABLE.put(11970, 11968);
	}

	public static boolean itemOnObject(Stoner stoner, int object, int item) {

	if (object != OBJECT_IDENTIFICATION) {
		return false;
	}

	if (USABLE.get(item) != null) {
		Item replacement = new Item(USABLE.get(item));
		stoner.getBox().remove(item, 1);
		stoner.getBox().add(replacement);
		return true;
	}

	return false;
	}

	public static void recharge(Stoner stoner, int item) {

	}

}
