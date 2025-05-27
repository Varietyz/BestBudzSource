package com.mayhem.rs2.content.skill.slayer;

public class SlayerTasks {
	
	/* Low level tasks */
	public static enum LowLevel {
		
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
		MAGIC_AXE("Magic axe"),
		BANSHEE("Banshee");

		public final String name;
		public final byte lvl;

		private LowLevel(String name) {
			this.name = name;
			lvl = SlayerMonsters.getLevelForName(name.toLowerCase());
		}
	}

	/* Medium level tasks */
	public static enum MediumLevel {
		
		BABY_DRAGON("Baby dragon"),
		RED_DRAGON("Red dragon"),
		LESSER_DEMON("Lesser demon"),
		//GREATER_DEMON("Greater demon"),
		GREEN_DRAGON("Green dragon"),
		FIRE_GIANT("Fire giant"),
		MOSS_GIANT("Moss giant"),
		CAVE_HORROR("Cave horror"),
		CRUSHING_HAND("Crushing hand"),
		SCREAMING_BANSHEE("Screaming banshee");

		public final String name;
		public final byte lvl;

		private MediumLevel(String name) {
			this.name = name;
			lvl = SlayerMonsters.getLevelForName(name.toLowerCase());
		}
	}

	/* High level tasks */
	public static enum HighLevel {
		
		BRONZE_DRAGON("Bronze dragon"),
		IRON_DRAGON("Iron dragon"),
		STEEL_DRAGON("Steel dragon"),
		BLACK_DRAGON("Black dragon"),
		LAVA_DRAGON("Lava dragon"),
		HELLHOUND("Hellhound"),
		BLACK_DEMON("Black demon"),
		ABYSSAL_DEMON("Abyssal demon"),
		NIGHT_BEAST("Night beast"),
		DARK_BEAST("Dark beast"),
		GREATER_ABYSSAL_DEMON("Greater abyssal demon"),
		DUST_DEVIL("Dust devil"),
		GARGOYLE("Gargoyle"),
		CAVE_ABOMINATION("Cave abomination");
		
		public final String name;
		public final byte lvl;
		
		private HighLevel(String name) {
			this.name = name;
			lvl = SlayerMonsters.getLevelForName(name.toLowerCase());
		}
		
	}
	
	/* Boss tasks */
	public static enum BossLevel {
		
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
		CERBERUS("Cerberus"),
		LIZARD_SHAMAN("Lizardman Shaman"),
		DEMONIC_GORILLAS("Demonic Gorilla"),
		GENERAL_GRAARDOR("General Graardor");
		
		
		public final String name;
		public final byte lvl;
		
		private BossLevel(String name) {
			this.name = name;
			lvl = SlayerMonsters.getLevelForName(name.toLowerCase());
		}		
	}
	
}
