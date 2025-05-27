package com.bestbudz.rs2.content.profession.mercenary;

public class MercenaryTasks {

	/* Low grade tasks */
	public static enum LowGrade {

		MAN("Man"),
		COW("Cow"),
		ROCK_CRAB("Rock crab"),
		CHAOS_DRUID("Chaos druid"),
		HILL_GIANT("Hill giant"),
		GIANT_BAT("Giant bat"),
		CRAWLING_HAND("Crawling hand"),
		SKELETON("Skeleton"),
		BLACK_KNIGHT("Black knight"),
		POISON_SCORPION("Poison scorpion"),
		POISON_SPIDER("Poison spider"),
		CHAOS_DWARF("Chaos dwarf"),
		MAGE_AXE("Mage axe"),
		BANSHEE("Banshee");

		public final String name;
		public final byte lvl;

		private LowGrade(String name) {
		this.name = name;
		lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
		}
	}

	/* Medium grade tasks */
	public static enum MediumGrade {

		BABY_DRAGON("Baby dragon"),
		RED_DRAGON("Red dragon"),
		LESSER_DEMON("Lesser demon"),
		GREATER_DEMON("Greater demon"),
		GREEN_DRAGON("Green dragon"),
		FIRE_GIANT("Fire giant"),
		MOSS_GIANT("Moss giant");

		public final String name;
		public final byte lvl;

		private MediumGrade(String name) {
		this.name = name;
		lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
		}
	}

	/* High grade tasks */
	public static enum HighGrade {

		BRONZE_DRAGON("Bronze dragon"),
		IRON_DRAGON("Iron dragon"),
		STEEL_DRAGON("Steel dragon"),
		BLACK_DRAGON("Black dragon"),
		LAVA_DRAGON("Lava dragon"),
		HELLHOUND("Hellhound"),
		BLACK_DEMON("Black demon"),
		ABYSSAL_DEMON("Abyssal demon"),
		DARK_BEAST("Dark beast");

		public final String name;
		public final byte lvl;

		private HighGrade(String name) {
		this.name = name;
		lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
		}

	}

	/* Boss tasks */
	public static enum BossGrade {

		ZULRAH("Zulrah"),
		KING_BLACK_DRAGON("King black dragon"),
		KREE_ARRA("Kree'arra"),
		COMMANDER_ZILYANA("Commander Zilyana"),
		CORPOREAL_BEAST("Corporeal Beast"),
		BARRELCHEST("Barrelchest"),
		KRAKEN("Kraken"),
		GIANT_MOLE("Giant mole"),
		CHAOS_ELEMENTAL("Chaos Elemental"),
		CALLISTO("Callisto"),
		VETION("Vet'ion Reborn"),
		CHAOS_FANATIC("Chaos Fanatic"),
		GENERAL_GRAARDOR("General Graardor");

		public final String name;
		public final byte lvl;

		private BossGrade(String name) {
		this.name = name;
		lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
		}
	}

}
