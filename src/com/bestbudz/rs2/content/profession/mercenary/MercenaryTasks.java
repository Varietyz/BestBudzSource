package com.bestbudz.rs2.content.profession.mercenary;

public class MercenaryTasks {

  public enum LowGrade {
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

    LowGrade(String name) {
      this.name = name;
      lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
    }
  }

  public enum MediumGrade {
    BABY_DRAGON("Baby dragon"),
    RED_DRAGON("Red dragon"),
    LESSER_DEMON("Lesser demon"),
    GREATER_DEMON("Greater demon"),
    GREEN_DRAGON("Green dragon"),
    FIRE_GIANT("Fire giant"),
    MOSS_GIANT("Moss giant");

    public final String name;
    public final byte lvl;

    MediumGrade(String name) {
      this.name = name;
      lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
    }
  }

  public enum HighGrade {
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

    HighGrade(String name) {
      this.name = name;
      lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
    }
  }

  public enum BossGrade {
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

    BossGrade(String name) {
      this.name = name;
      lvl = MercenaryMonsters.getGradeForName(name.toLowerCase());
    }
  }
}
