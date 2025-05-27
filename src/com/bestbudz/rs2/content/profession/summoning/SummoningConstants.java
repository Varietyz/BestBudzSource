package com.bestbudz.rs2.content.profession.summoning;

import java.util.HashMap;
import java.util.Map;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.rs2.content.profession.summoning.impl.Minotaur;
import com.bestbudz.rs2.content.profession.summoning.impl.SpiritJelly;
import com.bestbudz.rs2.content.profession.summoning.impl.SpiritSpider;
import com.bestbudz.rs2.content.profession.summoning.impl.SpiritTerrorBird;
import com.bestbudz.rs2.content.profession.summoning.impl.SpiritWolf;
import com.bestbudz.rs2.content.profession.summoning.impl.UnicornStallion;
import com.bestbudz.rs2.entity.item.Item;

public class SummoningConstants {
	private static final Map<Integer, Familiar> itemsForFamiliar = new HashMap<Integer, Familiar>();

	private static final Map<Familiar, Integer> scrollsForFamiliar = new HashMap<Familiar, Integer>();

	private static final Map<Familiar, FamiliarSpecial> specials = new HashMap<Familiar, FamiliarSpecial>();
	public static final int SHARD_ID = 18016;
	public static final int POUCH_ID = 12525;
	public static final String SUMMONING_FAMILIAR_LOGIN_KEY = "summoningfamsave";
	public static final String SUMMONING_BOB_BOX_KEY = "summoningbobbox";
	public static final String FAMILIAR_NEXT_MAX_HIT = "summonfammax";

	public static void declare() {
	specials.put(Familiar.a, new SpiritWolf());
	specials.put(Familiar.cc, new SpiritTerrorBird());
	specials.put(Familiar.c, new SpiritSpider());
	specials.put(Familiar.ee, new SpiritJelly());
	specials.put(Familiar.u, new Minotaur());
	specials.put(Familiar.y, new Minotaur());
	specials.put(Familiar.u, new Minotaur());
	specials.put(Familiar.ff, new Minotaur());
	specials.put(Familiar.pp, new Minotaur());
	specials.put(Familiar.aaa, new Minotaur());
	specials.put(Familiar.lll, new Minotaur());
	specials.put(Familiar.mmm, new UnicornStallion());

	for (Pouch i : Pouch.values()) {
		ItemDefinition def = Item.getDefinition(i.pouchId);

		if (def != null) {
			def.setUntradable();
		}
	}

	for (Scroll i : Scroll.values()) {
		ItemDefinition def = Item.getDefinition(i.itemId);

		if (def != null) {
			def.setUntradable();
		}
	}

	main: for (Familiar i : Familiar.values()) {
		int pouch = i.pouch;

		if (pouch >= 0) {
			for (Scroll k : Scroll.values()) {
				if ((k.pouch != null) && (k.pouch.getId() == pouch)) {
					scrollsForFamiliar.put(i, Integer.valueOf(k.itemId));
					continue main;
				}
			}

			// System.out.println("no scroll for: " + i.name);
		}
	}
	for (Familiar f : Familiar.values())
		itemsForFamiliar.put(Integer.valueOf(f.pouch), f);
	}

	public static Familiar getFamiliarForPouch(int id) {
	return itemsForFamiliar.get(Integer.valueOf(id));
	}

	public static int getScrollForFamiliar(FamiliarMob mob) {
	return !scrollsForFamiliar.containsKey(mob.getData()) ? -1 : scrollsForFamiliar.get(mob.getData()).intValue();
	}

	public static FamiliarSpecial getSpecial(Familiar f) {
	return specials.get(f);
	}

	public static boolean isAllowed(Familiar f) {
	if (f.assault <= 0) {
		return false;
	}

	return true;
	}
}
