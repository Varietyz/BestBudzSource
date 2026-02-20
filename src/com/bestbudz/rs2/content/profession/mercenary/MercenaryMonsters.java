package com.bestbudz.rs2.content.profession.mercenary;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;
import java.util.Map;

public class MercenaryMonsters {

	private static final Map<Integer, Byte> mercenaryRequired = new HashMap<Integer, Byte>();

	public static boolean canAssaultMob(Stoner stoner, Mob mob) {
		return mob != null &&
			mob.getDefinition() != null &&
			mob.getDefinition().isAssaultable();
	}

	public static final void declare() {
		for (int i = 0; i < 18000; i++) {
			NpcDefinition def = Mob.getDefinition(i);

			if ((def != null) && (def.getName() != null)) {
				String check = def.getName().toLowerCase();
				int lvl = getGradeForName(check);
				if (lvl > 0) mercenaryRequired.put(Integer.valueOf(i), Byte.valueOf((byte) lvl));
			}
		}
	}

	public static byte getGradeForName(String check) {
		switch (check) {
			case "crawling hand": return 5;
			case "cave bug": return 7;
			case "cave crawler": return 10;
			case "banshee": return 15;
			case "cave slime": return 17;
			case "rockslug": return 20;
			case "desert lizard": return 22;
			case "cockatrice": return 25;
			case "pyrefiend": return 30;
			case "mogre": return 32;
			case "infernal mage": return 45;
			case "bloodveld": return 50;
			case "jelly": return 62;
			case "cave horror": return 58;
			case "aberrant spectre": return 60;
			case "dust devil": return 65;
			case "spiritual sagittarius": return 63;
			case "spiritual warrior": return 68;
			case "kurask": return 70;
			case "gargoyle": return 75;
			case "aquanite": return 78;
			case "nechryael": return 80;
			case "spiritual mage": return 83;
			case "abyssal demon": return 85;
			case "dark beast": return 90;
			default: return 1;
		}
	}

	public static byte getRequiredGrade(int id) {
		return 1;
	}
}