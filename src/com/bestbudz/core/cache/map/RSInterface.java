package com.bestbudz.core.cache.map;

import com.bestbudz.core.cache.ByteStreamExt;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class RSInterface {

  private static final Map<Integer, Short> songs = new HashMap<Integer, Short>();

  private static final String[] musicNames = {
    "7th Realm",
    "A Familiar Feeling",
    "A New Menace",
    "A Pirate's Life for Me",
    "Adorno I",
    "Adorno II",
    "Adorno III",
    "Adorno IV",
    "Adorno IX",
    "Adorno V",
    "Adorno VI",
    "Adorno VII",
    "Adorno VIII",
    "Adorno X",
    "Adventure",
    "Al Kharid",
    "All's Fairy in Love and War",
    "Alone",
    "Altar Ego",
    "Alternative Root",
    "Ambient Jungle",
    "An Easter United",
    "Ancestral Wisdom",
    "Animal Apogee",
    "Anywhere",
    "Arabian",
    "Arabian 2",
    "Arabian 3",
    "Arabique",
    "Ardougne Ago",
    "Arma Gonna Get You",
    "Armadyl Alliance",
    "Armageddon",
    "Army of Darkness",
    "Arrival",
    "Artistry",
    "Assault and Battery",
    "Assault 1",
    "Assault 2",
    "Assault 3",
    "Assault 4",
    "Assault 5",
    "Assault 6",
    "Attention",
    "Autumn Voyage",
    "Aye Car Rum Ba",
    "Aztec",
    "Back to Life",
    "Background",
    "Bal'lak the Pummeller",
    "Ballad of Enchantment",
    "Bandit Camp",
    "Bandos Battalion",
    "Bane of Summer",
    "Barb Wire",
    "Barbarianism",
    "Barking Mad",
    "Baroque",
    "Battle of Souls",
    "Beetle Juice",
    "Beyond",
    "Big Chords",
    "Bish Bash Bosh",
    "Bittersweet Bunny",
    "Black of Knight",
    "Blistering Barnacles",
    "Bloodbath",
    "Body Parts",
    "Bolrie's Diary",
    "Bone Dance",
    "Bone Dry",
    "Book of Spells",
    "Borderland",
    "Born to Do This",
    "Bounty Hunter Grade 1",
    "Bounty Hunter Grade 2",
    "Bounty Hunter Grade 3",
    "Brain Battle",
    "Breeze",
    "Brew Hoo Hoo",
    "Brimstail's Scales",
    "Bubble and Squeak",
    "Bulwark Beast",
    "But We Can Fight",
    "Cabin Fever",
    "Camelot",
    "CastleWars",
    "Catacombs and Tombs",
    "Catch Me If You Can",
    "Cave Background",
    "Cave of Beasts",
    "Cave of the Goblins",
    "Cavern",
    "Cavernous Mythology",
    "Cellar Song",
    "Chain of Command",
    "Chamber",
    "Charmin' Farmin'",
    "Chef Surprize",
    "Chickened Out",
    "Chompy Hunt",
    "Circus",
    "City of the Dead",
    "Clan Wars",
    "Claustrophobia",
    "Close Quarters",
    "Command Centre",
    "Competition",
    "Complication",
    "Conspiracy: Part 1",
    "Conspiracy: Part 2",
    "Contest",
    "Cool for Ali Cats",
    "Copris Lunaris",
    "Corporal Punishment",
    "Corridors of Power",
    "Courage",
    "Creature Cruelty",
    "Creepy",
    "Crystal Castle",
    "Crystal Cave",
    "Crystal Sword",
    "Cursed",
    "Dagannoth Dawn",
    "Dance of Death",
    "Dance of the Undead",
    "Dangerous",
    "Dangerous Logic",
    "Dangerous Road",
    "Dangerous Way",
    "Dark",
    "Davy Jones's Locker",
    "Dead and Buried",
    "Dead Can Dance",
    "Dead Quiet",
    "Deadlands",
    "Deep Down",
    "Deep Wildy",
    "Demise of the Dorgeshuun",
    "Desert Heat",
    "Desert Island Bear",
    "Desert Smoke",
    "Desert Voyage",
    "Desolate Ruins",
    "Desolo I",
    "Desolo II",
    "Desolo III",
    "Desolo IV",
    "Desolo IX",
    "Desolo V",
    "Desolo VI",
    "Desolo VII",
    "Desolo VIII",
    "Desolo X",
    "Diango's Little Helpers",
    "Dillo-gence is Key",
    "Dimension X",
    "Distant Land",
    "Distillery Hilarity",
    "Divine Skinweaver",
    "Dogs of War",
    "Don't Panic Zanik",
    "Doorways",
    "Dorgeshuun City",
    "Dorgeshuun Deep",
    "Down and Out",
    "Down Below",
    "Down to Earth",
    "Dragontooth Island",
    "Dream",
    "Dream Theatre",
    "Dreamstate",
    "Duel Arena",
    "Dunjun",
    "Dusk in Yu'biusk",
    "Dwarf Theme",
    "Dynasty",
    "Eagle Peak",
    "Easter Jig",
    "Egypt",
    "Elven Mist",
    "Elven Seed",
    "Emotion",
    "Emperor",
    "Eruption",
    "Escape",
    "Espionage",
    "Etcetera",
    "Everlasting",
    "Everlasting Fire",
    "Everywhere",
    "Evil Bob's Island",
    "Exam Conditions",
    "Exhibit 'A'",
    "Expanse",
    "Expecting",
    "Expedition",
    "Exposed",
    "Face Off",
    "Faerie",
    "Faithless",
    "Fanfare",
    "Fanfare 2",
    "Fanfare 3",
    "Fangs for the Memory",
    "Far Away",
    "Fe Fi Fo Fum",
    "Fear and Loathing",
    "Fenkenstrain's Refrain",
    "Fight of the Dwarves",
    "Fight or Flight",
    "Final Destination",
    "Find My Way",
    "Fire and Brimstone",
    "Fisher",
    "Floating Free",
    "Flute Salad",
    "Food for Thought",
    "Forbidden",
    "Forest",
    "Forever",
    "Forgettable Melody",
    "Forgotten",
    "Freshwater",
    "Frogland",
    "Frost Fight",
    "Frostbite",
    "Fruits de Mer",
    "Funny Bunnies",
    "Gaol",
    "Garden",
    "Garden of Autumn",
    "Garden of Spring",
    "Garden of Summer",
    "Garden of Winter",
    "Ghost of Christmas Presents",
    "Glacialis IV",
    "Glacialis VI",
    "Glacialis VIII",
    "Glorious Recallation...",
    "Glorious Recallation...",
    "Glorious Recallation...",
    "Gnome King",
    "Gnome Village",
    "Gnome Village 2",
    "Gnomeball",
    "Goblin Game",
    "Goblin Village",
    "Godmercenary",
    "Golden Touch",
    "Greatness",
    "Grimly Fiendish",
    "Grip of the Talon",
    "Grotto",
    "Ground Scape",
    "Grumpy",
    "Guthix's Hunter",
    "H.A.M. Fisted",
    "Ham and Seek",
    "Ham Assault",
    "Har'Lakk the Riftsplitter",
    "Hare-brained Bitches",
    "Harmony",
    "Harmony 2",
    "Haunted Quarry",
    "Have a Blast",
    "Have an Ice Day",
    "Head to Head",
    "Healin' Feelin'",
    "Heart and Mind",
    "Hell's Bells",
    "Hermit",
    "High Seas",
    "High Spirits",
    "Historic Memories",
    "Hobgoblin Geomancer",
    "Home Sweet Home",
    "Homescape",
    "Honkytonky Harmony",
    "Honkytonky Medieval",
    "Honkytonky Newbie Melody",
    "Honkytonky Parade",
    "Honkytonky Sea Shanty",
    "Horizon",
    "Hot 'n' Bothered",
    "Hypnotized",
    "I'm Counting on You",
    "Iban",
    "Ice Day for Penguins",
    "Ice Melody",
    "Icy a Worried Gnome",
    "Icy Trouble Ahead",
    "Illusive",
    "Impetuous",
    "In Between",
    "In the Brine",
    "In the Clink",
    "In the Manor",
    "In the Pits",
    "Inadequacy",
    "Incantation",
    "Incarceration",
    "Insect Queen",
    "Inspiration",
    "Into the Abyss",
    "Intrepid",
    "Island Life",
    "Island of the Trolls",
    "Isle of Everywhere",
    "Itsy Bitsy...",
    "Jailbird",
    "Jaws of the Dagannoth",
    "Jester Minute",
    "Jolly-R",
    "Joy of the Hunt",
    "Jungle Bells",
    "Jungle Community",
    "Jungle Hunt",
    "Jungle Island",
    "Jungle Island XMAS",
    "Jungle Troubles",
    "Jungly 1",
    "Jungly 2",
    "Jungly 3",
    "Karamja Jam",
    "Kharidian Nights",
    "Kingdom",
    "Knightly",
    "Knightmare",
    "La Mort",
    "Labyrinth",
    "Lair",
    "Lair of Kang Admi",
    "Lament",
    "Lament of Meiyerditch",
    "Lamistard's Labyrinth",
    "Land Down Under",
    "Land of Snow",
    "Land of the Dwarves",
    "Landlubber",
    "Last Stand",
    "Lasting",
    "Lazy Wabbit",
    "Legend",
    "Legion",
    "Lexicus Runewright",
    "Life's a Beach!",
    "Lighthouse",
    "Lightness",
    "Lightwalk",
    "Little Cave of Horrors",
    "Living Rock",
    "Lonesome",
    "Long Ago",
    "Long Way Home",
    "Looking Back",
    "Lore and Order",
    "Lost Soul",
    "Lullaby",
    "Mad Eadgar",
    "Mage Arena",
    "Mage and Mystery",
    "Mage Dance",
    "Mage Mage Mage",
    "Mageal Journey",
    "Maiasaura",
    "Major Quarryr",
    "Making Waves",
    "Malady",
    "March",
    "Marooned",
    "Marzipan",
    "Masquerade",
    "Mastermindless",
    "Mausoleum",
    "Meddling Kids",
    "Medieval",
    "Mellow",
    "Melodrama",
    "Melzar's Maze",
    "Meridian",
    "Method of Madness",
    "Miles Away",
    "Mind Over Matter",
    "Miracle Dance",
    "Mirage",
    "Miscellania",
    "Mobilising Armies",
    "Monarch Waltz",
    "Monkey Madness",
    "Monster Melee",
    "Moody",
    "Morytania",
    "Mouse Trap",
    "Mudskipper Melody",
    "Mutant Medley",
    "My Arm's Journey",
    "Narnode's Theme",
    "Natural",
    "Neverland",
    "Newbie Melody",
    "Nial's Widow",
    "Night of the Vampyre",
    "Night-gazer Khighorahk",
    "Nightfall",
    "No Way Out",
    "Nomad",
    "Norse Code",
    "Null and Void",
    "Ogre the Top",
    "On the Up",
    "On the Wing",
    "Oriental",
    "Out of the Deep",
    "Over To Nardah",
    "Overpass",
    "Overture",
    "Parade",
    "Path of Peril",
    "Pathways",
    "Penguin Possible",
    "Pest Control",
    "Pharaoh's Tomb",
    "Phasmatys",
    "Pheasant Peasant",
    "Pinball Wizard",
    "Pirates of Penance",
    "Pirates of Peril",
    "Poison Dreams",
    "Poles Apart",
    "Prime Time",
    "Principality",
    "Quest",
    "Rammernaut",
    "Rat a Tat Tat",
    "Rat Hunt",
    "Ready for Battle",
    "Regal",
    "Reggae",
    "Reggae 2",
    "Rellekka",
    "Rest for the Weary",
    "Right on Track",
    "Righteousness",
    "Rising Damp",
    "Riverside",
    "Roc and Roll",
    "Roll the Bones",
    "Romancing the Crone",
    "Romper Chomper",
    "Roots and Flutes",
    "Royale",
    "Rune Essence",
    "Sad Meadow",
    "Safety in Numbers",
    "Saga",
    "Sagittare",
    "Saltwater",
    "Sarah's Lullaby",
    "Sarcophagus",
    "Sarim's Vermin",
    "Scape Cave",
    "Scape Hunter",
    "Scape Main",
    "Scape Original",
    "Scape Sad",
    "Scape Santa",
    "Scape Scared",
    "Scape Soft",
    "Scape Summon",
    "Scape Theme",
    "Scape Wild",
    "Scarab",
    "Scarabaeoidea",
    "School's Out",
    "Sea Shanty",
    "Sea Shanty 2",
    "Sea Shanty XMAS",
    "Second Vision",
    "Serenade",
    "Serene",
    "Settlement",
    "Shadow-forger Ihlakhizan",
    "Shadowland",
    "Shaping Up",
    "Shine",
    "Shining",
    "Shining Spirit",
    "Shipwrecked",
    "Showdown",
    "Sigmund's Showdown",
    "Silent Knight",
    "Slain to Waste",
    "Slice of Silent Movie",
    "Slice of Station",
    "Slither and Thither",
    "Slug a Bug Ball",
    "Smorgasbord",
    "Sojourn",
    "Something Fishy",
    "Soundscape",
    "Spa Bizarre",
    "Sphinx",
    "Spirit",
    "Spirits of Elid",
    "Splendour",
    "Spooky",
    "Spooky 2",
    "Spooky Jungle",
    "Stagnant",
    "Starlight",
    "Start",
    "Stealing Creation",
    "Still Night",
    "Stillness",
    "Stillwater",
    "Stomp",
    "Storeroom Shuffle",
    "Storm Brew",
    "Stranded",
    "Strange Place",
    "Stratosphere",
    "Vigour of Saradomin",
    "Subterranea",
    "Sunburn",
    "Superstition",
    "Surok's Theme",
    "Suspicious",
    "Tale of Keldagrim",
    "Talking Forest",
    "Tears of Guthix",
    "Technology",
    "Temple",
    "Temple Desecrated",
    "Temple of Light",
    "Temple of Tribes",
    "Terrorbird Tussle",
    "The stoner",
    "The stoners Re-United!",
    "The Art of Hocus-Pocus",
    "The Cellar Dwellers",
    "The Chosen",
    "The Chosen Commander",
    "The Columbarium",
    "The Dance of the Snow Queen",
    "The Depths",
    "The Desert",
    "The Desolate Isle",
    "The Duke",
    "The Enchanter",
    "The Evil Within",
    "The Fallen Hero",
    "The Far Side",
    "The Galleon",
    "The Genie",
    "The Golem",
    "The Heist",
    "The Horn of Chill",
    "The Last Shanty",
    "The Longramble Scramble",
    "The Lost Melody",
    "The Lost Tribe",
    "The Lunar Isle",
    "The Mad Mole",
    "The Mentor",
    "The Mollusc Menace",
    "The Monsters Below",
    "The Muspah's Tomb",
    "The Navigator",
    "The Noble Rodent",
    "The Other Side",
    "The Pact",
    "The Pengmersible",
    "The Phoenixb",
    "The Plundered Tomb",
    "The Power of Tears",
    "The Quiz Master",
    "The Rogues' Den",
    "The Route of All Evil",
    "The Route of the Problem",
    "The Ruins of Camdozaal",
    "The Shadow",
    "The Mercenary",
    "The Sound of Guthix",
    "The Terrible Caverns",
    "The Terrible Tower",
    "The Terrible Tunnels",
    "The Throne of Bandos",
    "The Tower",
    "The Trade Parade",
    "The Vacant Abyss",
    "The Wrong Path",
    "Theme",
    "These Stones",
    "Throne of the Demon",
    "Time Out",
    "Time to Quarry",
    "Tiptoe",
    "Title Fight",
    "TokTz-Ket-Ek-Mack",
    "Tomb Raider",
    "Tomorrow",
    "Too Many Cooks...",
    "Tournament!",
    "Trawler",
    "Trawler Minor",
    "Tree Spirits",
    "Trees Aren't Your Friends",
    "Tremble",
    "Tribal",
    "Tribal 2",
    "Tribal Background",
    "Trick or Treat?",
    "Trinity",
    "Trouble Brewing",
    "Troubled",
    "Troubled Spirit",
    "Tune from the Dune",
    "Twilight",
    "TzHaar!",
    "Undead Army",
    "Undead Dungeon",
    "Under the Sand",
    "Undercurrent",
    "Underground",
    "Underground Pass",
    "Understanding",
    "Unholy Cursebearer",
    "Unknown Land",
    "Untouchable",
    "Upcoming",
    "Venomous",
    "Venture",
    "Venture 2",
    "Victory is Quarry",
    "Village",
    "Vision",
    "Volcanic Vikings",
    "Voodoo Cult",
    "Voyage",
    "Waiting for Battle",
    "Waiting for the Hunt",
    "Waking Dream",
    "Wander",
    "Warrior",
    "Warriors' Guild",
    "Waste Defaced",
    "Waterfall",
    "Waterlogged",
    "Way of the Enchanter",
    "Wayward",
    "We Are the Fairies",
    "Well of Voyage",
    "Where Eagles Lair",
    "Wild Isle",
    "Wild Side",
    "Wilderness",
    "Wilderness 2",
    "Wilderness 3",
    "Wildwood",
    "Winter Funfare",
    "Witching",
    "Woe of the Wyvern",
    "Wonder",
    "Wonderous",
    "Woodland",
    "Work Work Work",
    "Workshop",
    "Wrath and Ruin",
    "Xenophobe",
    "Yesteryear",
    "Zamorak Zoo",
    "Zanik's Theme",
    "Zaros Stirs",
    "Zealot",
    "Zogre Dance"
  };
  private static final int[] summoningGradeRequirements = {
    1, 4, 10, 13, 16, 17, 18, 19, 22, 23, 25, 28, 29, 31, 32, 33, 34, 34, 34, 34, 36, 40, 41, 42,
    43, 43, 43, 43, 43, 43, 43, 46, 46, 47, 49, 52, 54, 55, 56, 56, 57, 57, 57, 58, 61, 62, 63, 64,
    66, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 76, 77, 78, 79, 79, 79, 80, 83, 83, 85, 86, 88,
    89, 92, 93, 95, 96, 99
  };
  private static final int[] pouchItems = {
    12047, 12043, 12059, 12019, 12009, 12778, 12049, 12055, 12808, 12067, 12063, 12091, 12800,
    12053, 12065, 12021, 12818, 12780, 12798, 12814, 12073, 12087, 12071, 12051, 12095, 12097,
    12099, 12101, 12103, 12105, 12107, 12075, 12816, 12041, 12061, 12007, 12035, 12027, 12077,
    12531, 12810, 12812, 12784, 12023, 12085, 12037, 12015, 12045, 12079, 12123, 12031, 12029,
    12033, 12820, 12057, 14623, 12792, 12069, 12011, 12081, 12782, 12794, 12013, 12802, 12804,
    12806, 12025, 12017, 12788, 12776, 12083, 12039, 12786, 12089, 12796, 12822, 12093, 12790
  };
  private static final int[] scrollItems = {
    12425, 12445, 12428, 12459, 12533, 12838, 12460, 12432, 12839, 12430, 12446, 12440, 12834,
    12447, 12433, 12429, 12443, 12443, 12443, 12443, 12461, 12431, 12422, 12448, 12458, 12458,
    12458, 12458, 12458, 12458, 12458, 12462, 12829, 12426, 12444, 12441, 12454, 12453, 12463,
    12424, 12835, 12836, 12840, 12455, 12468, 12427, 12436, 12467, 12464, 12452, 12439, 12438,
    12423, 12830, 12451, 14622, 12826, 12449, 12450, 12465, 12841, 12831, 12457, 12824, 12824,
    12824, 12442, 12456, 12837, 12832, 12466, 12434, 12833, 12437, 12827, 12828, 12435, 12825
  };
  private static final String[] scrollNames = {
    "Howl",
    "Dreadfowl Strike",
    "Egg Spawn",
    "Slime Spray",
    "Stony Shell",
    "Pester",
    "Electric Lash",
    "Venom Shot",
    "Fireball Assault",
    "Cheese Feast",
    "Sandstorm",
    "Generate Compost",
    "Explode",
    "Vampire Touch",
    "Insane Ferocity",
    "Multichop",
    "Call of Arms",
    "Call of Arms",
    "Call of Arms",
    "Call of Arms",
    "Bronze Bull Rush",
    "Unburden",
    "Weedcall",
    "Evil Flames",
    "Petrifying gaze",
    "Petrifying gaze",
    "Petrifying gaze",
    "Petrifying gaze",
    "Petrifying gaze",
    "Petrifying gaze",
    "Petrifying gaze",
    "Iron Bull Rush",
    "Immense Heat",
    "Starter Fingers",
    "Blood Drain",
    "Tireless Run",
    "Abyssal Drain",
    "Dissolve",
    "Steel Bull Rush",
    "Fish Rain",
    "Goad",
    "Ambush",
    "Rending",
    "Doomsphere Device",
    "Dust Cloud",
    "Abyssal Stealth",
    "Ophidian Incubation",
    "Poisonous Blast",
    "Mithril Bull Rush",
    "Toad Bark",
    "Testudo",
    "Swallow Whole",
    "Fruitfall",
    "Faquarry",
    "Arctic Blast",
    "Rise from the Ashes",
    "Volcanic Vigour",
    "Crushing Claw",
    "Mantis Strike",
    "Adamant Bull Rush",
    "Inferno",
    "Deadly Claw",
    "Acorn Missile",
    "Titan's Consitution",
    "Titan's Consitution",
    "Titan's Consitution",
    "Regrowth",
    "Spike Shot",
    "Ebon Thunder",
    "Swamp Plague",
    "Rune Bull Rush",
    "Healing Aura",
    "Boil",
    "Mage Focus",
    "Essence Shipment",
    "Iron Within",
    "Winter Storage",
    "Steel of Legends"
  };
  private static final String[] pouchNames = {
    "Spirit wolf",
    "Dreadfowl",
    "Spirit spider",
    "Thorny snail",
    "Granite crab",
    "Spirit mosquito",
    "Desert wyrm",
    "Spirit scorpion",
    "Spirit tz-kih",
    "Albino rat",
    "Spirit kalphite",
    "Compost mound",
    "Giant chinchompa",
    "Vampire bat",
    "Honey badger",
    "Beaver",
    "Void ravager",
    "Void spinner",
    "Void torcher",
    "Void shifter",
    "Bronze minotaur",
    "Bull ant",
    "Macaw",
    "Evil turnip",
    "Sp. cockatrice",
    "Sp. guthatrice",
    "Sp. saratrice",
    "Sp. zamatrice",
    "Sp. pengatrice",
    "Sp. coraxatrice",
    "Sp. vulatrice",
    "Iron minotaur",
    "Pyrelord",
    "Magpie",
    "Bloated leech",
    "Spirit terrorbird",
    "Abyssal parasite",
    "Spirit jelly",
    "Steel minotaur",
    "Ibis",
    "Spirit graahk",
    "Spirit kyatt",
    "Spirit larupia",
    "Karam. overlord",
    "Smoke devil",
    "Abyssal lurker",
    "Spirit cobra",
    "Stsagittarius plant",
    "Mithril minotaur",
    "Barker toad",
    "War tortoise",
    "Bunyip",
    "Fruit bat",
    "Ravenous locust",
    "Arctic bear",
    "Phoenix",
    "Obsidian golem",
    "Granite lobster",
    "Praying mantis",
    "Adamant minotaur",
    "Forge regent",
    "Talon beast",
    "Giant ent",
    "Fire titan",
    "Moss titan",
    "Ice titan",
    "Hydra",
    "Spirit dagannoth",
    "Lava titan",
    "Swamp titan",
    "Rune minotaur",
    "Unicorn stallion",
    "Geyser titan",
    "Wolpertinger",
    "Abyssal titan",
    "Iron titan",
    "Pack yak",
    "Steel titan"
  };
  private static final Logger logger = Logger.getLogger(RSInterface.class.getSimpleName());
  public static int[] boxIds = {
    4041, 4077, 4113, 4047, 4083, 4119, 4053, 4089, 4125, 4059, 4095, 4131, 4065, 4101, 4137, 4071,
    4107, 4143, 4154, 12168, 13918
  };
  public static RSInterface[] interfaceCache;
  public String popupString;
  public String hoverText;
  public boolean drawsTransparent;
  public int anInt208;
  public int[] requiredValues;
  public int contentType;
  public int[] spritesX;
  public int textHoverColour;
  public int atActionType;
  public String spellName;
  public int anInt219;
  public int width;
  public String tooltip;
  public String selectedActionName;
  public boolean centerText;
  public int scrollLocation;
  public String[] itemActions;
  public int[][] valueIndexArray;
  public boolean aBoolean227;
  public String disabledText;
  public int mouseOverPopupInterface;
  public int invSpritePadX;
  public int textColor;
  public int anInt233;
  public int mediaID;
  public boolean aBoolean235;
  public int parentID;
  public int spellUsableOn;
  public int anInt239;
  public int[] children;
  public int[] childX;
  public boolean usableItemInterface;
  public int invSpritePadY;
  public int[] valueCompareType;
  public int anInt246;
  public int[] spritesY;
  public String message;
  public boolean isBoxInterface;
  public int id;
  public int[] invStackSizes;
  public int[] inv;
  public byte opacity;
  public int anInt255;
  public int anInt256;
  public int anInt257;
  public int anInt258;
  public boolean aBoolean259;
  public int scrollMax;
  public int type;
  public int anInt263;
  public int anInt265;
  public boolean isMouseoverTriggered;
  public int height;
  public boolean textShadow;
  public int modelZoom;
  public int modelRotation1;
  public int modelRotation2;
  public int[] childY;
  public int itemSpriteId1;
  public int itemSpriteId2;
  public int itemSpriteZoom1;
  public int itemSpriteZoom2;
  public int itemSpriteIndex;
  public boolean greyScale;

  public RSInterface() {
    itemSpriteId1 = -1;
    itemSpriteId2 = -1;
    itemSpriteZoom1 = -1;
    itemSpriteZoom2 = -1;
    itemSpriteIndex = 0;
  }

  public static void addButton(int i, int j, String name, int W, int H, String S, int AT) {
    RSInterface RSInterface = addInterface(i);
    RSInterface.id = i;
    RSInterface.parentID = i;
    RSInterface.type = 5;
    RSInterface.atActionType = AT;
    RSInterface.contentType = 0;
    RSInterface.opacity = 0;
    RSInterface.mouseOverPopupInterface = 52;
    RSInterface.width = W;
    RSInterface.height = H;
    RSInterface.tooltip = S;
  }

  public static void addButton(
      int id,
      int sid,
      String spriteName,
      String tooltip,
      int mOver,
      int atAction,
      int width,
      int height) {
    RSInterface tab = interfaceCache[id] = new RSInterface();
    tab.id = id;
    tab.parentID = id;
    tab.type = 5;
    tab.atActionType = atAction;
    tab.contentType = 0;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = mOver;
    tab.width = width;
    tab.height = height;
    tab.tooltip = tooltip;
  }

  public static void addHover(
      int i,
      int aT,
      int contentType,
      int hoverOver,
      int sId,
      String imageName,
      int width,
      int height,
      String text) {
    addHoverButton(i, imageName, sId, width, height, text, contentType, hoverOver, aT);
  }

  public static void addHovered(int i, int j, String imageName, int w, int h, int IMAGEID) {
    addHoveredButton(i, imageName, j, w, h, IMAGEID);
  }

  public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID) {
    RSInterface tab = addTabInterface(i);
    tab.parentID = i;
    tab.id = i;
    tab.type = 0;
    tab.atActionType = 0;
    tab.width = w;
    tab.height = h;
    tab.isMouseoverTriggered = true;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = -1;
    tab.scrollMax = 0;
    addHoverImage(IMAGEID, j, j, imageName);
    tab.totalChildren(1);
    tab.child(0, IMAGEID, 0, 0);
  }

  public static void addHoverImage(int i, int j, int k, String name) {
    RSInterface tab = addTabInterface(i);
    tab.id = i;
    tab.parentID = i;
    tab.type = 5;
    tab.atActionType = 0;
    tab.contentType = 0;
    tab.width = 512;
    tab.height = 334;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = 52;
  }

  public static void addRuneText(int ID, int runeAmount, int RuneID) {
    RSInterface rsInterface = addTabInterface(ID);
    rsInterface.id = ID;
    rsInterface.parentID = 1151;
    rsInterface.type = 4;
    rsInterface.atActionType = 0;
    rsInterface.contentType = 0;
    rsInterface.width = 0;
    rsInterface.height = 14;
    rsInterface.mouseOverPopupInterface = -1;
    rsInterface.valueCompareType = new int[1];
    rsInterface.requiredValues = new int[1];
    rsInterface.valueCompareType[0] = 3;
    rsInterface.requiredValues[0] = runeAmount - 1;
    rsInterface.valueIndexArray = new int[1][4];
    rsInterface.valueIndexArray[0][0] = 4;
    rsInterface.valueIndexArray[0][1] = 3214;
    rsInterface.valueIndexArray[0][2] = RuneID;
    rsInterface.valueIndexArray[0][3] = 0;
    rsInterface.centerText = true;
    rsInterface.textShadow = true;
    rsInterface.message = "%1/" + runeAmount;
    rsInterface.disabledText = "";
    rsInterface.textColor = 0xc00000;
    rsInterface.anInt219 = 49152;
  }

  public static RSInterface addTabInterface(int id) {
    RSInterface tab = interfaceCache[id] = new RSInterface();
    tab.id = id;
    tab.parentID = id;
    tab.type = 0;
    tab.atActionType = 0;
    tab.contentType = 0;
    tab.width = 512;
    tab.height = 700;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = -1;
    return tab;
  }

  public static void setBounds(int ID, int X, int Y, int frame, RSInterface RSinterface) {
    RSinterface.children[frame] = ID;
    RSinterface.childX[frame] = X;
    RSinterface.childY[frame] = Y;
  }

  public static void addBankHover(
      int interfaceID,
      int actionType,
      int hoverid,
      int spriteId,
      int spriteId2,
      String NAME,
      int Width,
      int Height,
      int configFrame,
      int configId,
      String Tooltip,
      int hoverId2,
      int hoverSpriteId,
      int hoverSpriteId2,
      String hoverSpriteName,
      int hoverId3,
      String hoverDisabledText,
      String hoverEnabledText,
      int X,
      int Y) {
    RSInterface hover = addTabInterface(interfaceID);
    hover.id = interfaceID;
    hover.parentID = interfaceID;
    hover.type = 5;
    hover.atActionType = actionType;
    hover.contentType = 0;
    hover.opacity = 0;
    hover.mouseOverPopupInterface = hoverid;
    hover.width = Width;
    hover.tooltip = Tooltip;
    hover.height = Height;
    hover.valueCompareType = new int[1];
    hover.requiredValues = new int[1];
    hover.valueCompareType[0] = 1;
    hover.requiredValues[0] = configId;
    hover.valueIndexArray = new int[1][3];
    hover.valueIndexArray[0][0] = 5;
    hover.valueIndexArray[0][1] = configFrame;
    hover.valueIndexArray[0][2] = 0;
    hover = addTabInterface(hoverid);
    hover.parentID = hoverid;
    hover.id = hoverid;
    hover.type = 0;
    hover.atActionType = 0;
    hover.width = 550;
    hover.height = 334;
    hover.isMouseoverTriggered = true;
    hover.mouseOverPopupInterface = -1;
    addSprite(hoverId2, hoverSpriteId, hoverSpriteId2, hoverSpriteName, configId, configFrame);
    addHoverBox(hoverId3, interfaceID, hoverDisabledText, hoverEnabledText, configId, configFrame);
    setChildren(2, hover);
    setBounds(hoverId2, 15, 60, 0, hover);
    setBounds(hoverId3, X, Y, 1, hover);
  }

  public static void addBankHover1(
      int interfaceID,
      int actionType,
      int hoverid,
      int spriteId,
      String NAME,
      int Width,
      int Height,
      String Tooltip,
      int hoverId2,
      int hoverSpriteId,
      String hoverSpriteName,
      int hoverId3,
      String hoverDisabledText,
      int X,
      int Y) {
    RSInterface hover = addTabInterface(interfaceID);
    hover.id = interfaceID;
    hover.parentID = interfaceID;
    hover.type = 5;
    hover.atActionType = actionType;
    hover.contentType = 0;
    hover.opacity = 0;
    hover.mouseOverPopupInterface = hoverid;
    hover.width = Width;
    hover.tooltip = Tooltip;
    hover.height = Height;
    hover = addTabInterface(hoverid);
    hover.parentID = hoverid;
    hover.id = hoverid;
    hover.type = 0;
    hover.atActionType = 0;
    hover.width = 550;
    hover.height = 334;
    hover.isMouseoverTriggered = true;
    hover.mouseOverPopupInterface = -1;
    addSprite(hoverId2, hoverSpriteId, hoverSpriteId, hoverSpriteName, 0, 0);
    addHoverBox(hoverId3, interfaceID, hoverDisabledText, hoverDisabledText, 0, 0);
    setChildren(2, hover);
    setBounds(hoverId2, 15, 60, 0, hover);
    setBounds(hoverId3, X, Y, 1, hover);
  }

  private static void addButton(
      int ID,
      int type,
      int hoverID,
      int dS,
      int eS,
      String NAME,
      int W,
      int H,
      String text,
      int configFrame,
      int configId) {
    RSInterface rsinterface = addInterface(ID);
    rsinterface.id = ID;
    rsinterface.parentID = ID;
    rsinterface.type = 5;
    rsinterface.atActionType = type;
    rsinterface.opacity = 0;
    rsinterface.mouseOverPopupInterface = hoverID;
    rsinterface.width = W;
    rsinterface.height = H;
    rsinterface.tooltip = text;
    rsinterface.isMouseoverTriggered = true;
    rsinterface.valueCompareType = new int[1];
    rsinterface.requiredValues = new int[1];
    rsinterface.valueCompareType[0] = 1;
    rsinterface.requiredValues[0] = configId;
    rsinterface.valueIndexArray = new int[1][3];
    rsinterface.valueIndexArray[0][0] = 5;
    rsinterface.valueIndexArray[0][1] = configFrame;
    rsinterface.valueIndexArray[0][2] = 0;
  }

  public static void addButton(
      int i, int j, int hoverId, String name, int W, int H, String S, int AT) {
    RSInterface RSInterface = addInterface(i);
    RSInterface.id = i;
    RSInterface.parentID = i;
    RSInterface.type = 5;
    RSInterface.atActionType = AT;
    RSInterface.opacity = 0;
    RSInterface.mouseOverPopupInterface = hoverId;
    RSInterface.width = W;
    RSInterface.height = H;
    RSInterface.tooltip = S;
  }

  public static void addButton(int id, int sid, String spriteName, String tooltip) {
    RSInterface tab = interfaceCache[id] = new RSInterface();
    tab.id = id;
    tab.parentID = id;
    tab.type = 5;
    tab.atActionType = 1;
    tab.contentType = 0;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = 52;
    tab.tooltip = tooltip;
  }

  public static void addButton(int id, int sid, String spriteName, String tooltip, int w, int h) {
    RSInterface tab = interfaceCache[id] = new RSInterface();
    tab.id = id;
    tab.parentID = id;
    tab.type = 5;
    tab.atActionType = 1;
    tab.contentType = 0;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = 52;
    tab.width = w;
    tab.height = h;
    tab.tooltip = tooltip;
  }

  public static void addClickableText(
      int id,
      String text,
      String tooltip,
      int idx,
      int color,
      boolean center,
      boolean shadow,
      int width) {
    RSInterface tab = addTabInterface(id);
    tab.parentID = id;
    tab.id = id;
    tab.type = 4;
    tab.atActionType = 1;
    tab.width = width;
    tab.height = 11;
    tab.contentType = 0;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = -1;
    tab.centerText = center;
    tab.textShadow = shadow;
    tab.message = text;
    tab.disabledText = "";
    tab.textColor = color;
    tab.anInt219 = 0;
    tab.textHoverColour = 0xffffff;
    tab.anInt239 = 0;
    tab.tooltip = tooltip;
  }

  public static void addClickableText(
      int id,
      String text,
      String tooltip,
      int idx,
      int color,
      int hoverColor,
      int width,
      int height) {
    RSInterface Tab = addTab2(id);
    Tab.parentID = id;
    Tab.id = id;
    Tab.type = 4;
    Tab.atActionType = 1;
    Tab.width = width;
    Tab.height = height;
    Tab.contentType = 0;
    Tab.opacity = 0;
    Tab.mouseOverPopupInterface = -1;
    Tab.centerText = false;
    Tab.textShadow = true;
    Tab.message = text;
    Tab.tooltip = tooltip;
    Tab.disabledText = "";
    Tab.textColor = color;
    Tab.anInt219 = 0;
    Tab.textHoverColour = hoverColor;
    Tab.anInt239 = 0;
  }

  public static void addHoverBox(
      int id, int ParentID, String text, String text2, int configId, int configFrame) {
    RSInterface rsi = addTabInterface(id);
    rsi.id = id;
    rsi.parentID = ParentID;
    rsi.type = 8;
    rsi.disabledText = text;
    rsi.message = text2;
    rsi.valueCompareType = new int[1];
    rsi.requiredValues = new int[1];
    rsi.valueCompareType[0] = 1;
    rsi.requiredValues[0] = configId;
    rsi.valueIndexArray = new int[1][3];
    rsi.valueIndexArray[0][0] = 5;
    rsi.valueIndexArray[0][1] = configFrame;
    rsi.valueIndexArray[0][2] = 0;
  }

  public static void addHoverBox(int id, String text) {
    RSInterface rsi = interfaceCache[id];
    rsi.id = id;
    rsi.parentID = id;
    rsi.isMouseoverTriggered = true;
    rsi.type = 8;
    rsi.hoverText = text;
  }

  public static void addHoverButton(
      int a,
      int i,
      String imageName,
      int j,
      int width,
      int height,
      String text,
      int contentType,
      int hoverOver,
      int aT) {
    RSInterface tab = addTabInterface(i);
    tab.id = i;
    tab.parentID = i;
    tab.type = 5;
    tab.atActionType = aT;
    tab.contentType = contentType;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = hoverOver;

    if (a == 1) {}
    tab.width = width;
    tab.height = height;
    tab.tooltip = text;
  }

  public static void addHoverButton(
      int i,
      String imageName,
      int sId,
      int width,
      int height,
      String text,
      int contentType,
      int hoverOver,
      int aT) {
    RSInterface tab = addTabInterface(i);
    tab.id = i;
    tab.parentID = i;
    tab.type = 5;
    tab.atActionType = aT;
    tab.contentType = contentType;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = hoverOver;
    tab.width = width;
    tab.height = height;
    tab.tooltip = text;
  }

  public static RSInterface addInterface(int id) {
    RSInterface rsi = interfaceCache[id] = new RSInterface();
    rsi.id = id;
    rsi.parentID = id;
    rsi.width = 512;
    rsi.height = 334;
    return rsi;
  }

  public static void addPouch(
      int ID, int[] r1, int ra1, int r2, int lvl, String name, int imageID, int type) {
    RSInterface rsInterface = addTabInterface(ID);
    rsInterface.id = ID;
    rsInterface.parentID = 1151;
    rsInterface.type = 5;
    rsInterface.atActionType = type;
    rsInterface.contentType = 0;
    rsInterface.mouseOverPopupInterface = ID + 1;
    rsInterface.width = 32;
    rsInterface.height = 32;
    rsInterface.tooltip = "Infuse @or1@" + name;
    rsInterface.spellName = name;
    rsInterface.valueCompareType = new int[2];
    rsInterface.requiredValues = new int[2];
    rsInterface.valueCompareType[0] = 3;
    rsInterface.requiredValues[0] = ra1;
    rsInterface.valueCompareType[1] = 3;
    rsInterface.requiredValues[1] = lvl - 1;
    rsInterface.valueIndexArray = new int[2 + r1.length][];
    for (int i = 0; i < r1.length; i++) {
      rsInterface.valueIndexArray[i] = new int[4];
      rsInterface.valueIndexArray[i][0] = 4;
      rsInterface.valueIndexArray[i][1] = 3214;
      rsInterface.valueIndexArray[i][2] = r1[i];
      rsInterface.valueIndexArray[i][3] = 0;
    }

    rsInterface.valueIndexArray[1] = new int[3];
    rsInterface.valueIndexArray[1][0] = 1;
    rsInterface.valueIndexArray[1][1] = 6;
    rsInterface.valueIndexArray[1][2] = 0;
    rsInterface.itemSpriteId1 = r2;
    rsInterface.itemSpriteId2 = r2;
    rsInterface.itemSpriteIndex = imageID;
    rsInterface.greyScale = true;
    RSInterface hover = addTabInterface(ID + 1);
    hover.mouseOverPopupInterface = -1;
    hover.isMouseoverTriggered = true;
    setChildren(5, hover);
    addSprite(ID + 2, 0, "Interfaces/Lunar/BOX");
    addRuneText(ID + 5, ra1, r1[0]);
    addSprite(ID + 6, r1[0], null);
    addSprite(ID + 7, r1[1], null);
    addSprite(ID + 8, r1[2], null);
    setBounds(ID + 2, 0, 0, 0, hover);
    setBounds(ID + 3, 90, 4, 1, hover);
    setBounds(ID + 4, 90, 19, 2, hover);
    setBounds(ID + 5, 87, 66, 3, hover);
    setBounds(ID + 6, 14, 33, 4, hover);
  }

  public static void addSprite(int i, int j, int k) {
    RSInterface rsinterface = interfaceCache[i] = new RSInterface();
    rsinterface.id = i;
    rsinterface.parentID = i;
    rsinterface.type = 5;
    rsinterface.atActionType = 1;
    rsinterface.contentType = 0;
    rsinterface.width = 20;
    rsinterface.height = 20;
    rsinterface.opacity = 0;
    rsinterface.mouseOverPopupInterface = 52;
  }

  public static void addSprite(int a, int id, int spriteId, String spriteName, boolean l) {
    RSInterface tab = interfaceCache[id] = new RSInterface();
    tab.id = id;
    tab.parentID = id;
    tab.type = 5;
    tab.atActionType = 0;
    tab.contentType = 0;
    tab.opacity = (byte) 0;
    tab.mouseOverPopupInterface = 52;
    if (a == 1) {}
    tab.width = 512;
    tab.height = 334;
  }

  public static void addSprite(int ID, int i, int i2, String name, int configId, int configFrame) {
    RSInterface Tab = addTabInterface(ID);
    Tab.id = ID;
    Tab.parentID = ID;
    Tab.type = 5;
    Tab.atActionType = 0;
    Tab.contentType = 0;
    Tab.width = 512;
    Tab.height = 334;
    Tab.opacity = 0;
    Tab.mouseOverPopupInterface = -1;
    Tab.valueCompareType = new int[1];
    Tab.requiredValues = new int[1];
    Tab.valueCompareType[0] = 1;
    Tab.requiredValues[0] = configId;
    Tab.valueIndexArray = new int[1][3];
    Tab.valueIndexArray[0][0] = 5;
    Tab.valueIndexArray[0][1] = configFrame;
    Tab.valueIndexArray[0][2] = 0;
    if (name == null) {
      Tab.itemSpriteZoom1 = -1;
      Tab.itemSpriteId1 = i;
      Tab.itemSpriteZoom2 = 70;
      Tab.itemSpriteId2 = i2;
    } else {
    }
  }

  public static void addSprite(int id, int spriteId, String spriteName) {
    addSprite(id, spriteId, spriteName, -1, -1);
  }

  public static void addSprite(int id, int spriteId, String spriteName, int zoom1, int zoom2) {
    RSInterface tab = interfaceCache[id] = new RSInterface();
    tab.id = id;
    tab.parentID = id;
    tab.type = 5;
    tab.atActionType = 0;
    tab.contentType = 0;
    tab.opacity = 0;
    tab.mouseOverPopupInterface = 52;
    if (spriteName == null) {
      tab.itemSpriteZoom1 = zoom1;
      tab.itemSpriteId1 = spriteId;
      tab.itemSpriteZoom2 = zoom2;
      tab.itemSpriteId2 = spriteId;
    } else {
    }
    tab.width = 512;
    tab.height = 334;
  }

  public static RSInterface addTab2(int id) {
    interfaceCache[id] = new RSInterface();
    RSInterface Tab = interfaceCache[id];
    Tab.id = id;
    Tab.parentID = id;
    Tab.type = 0;
    Tab.atActionType = 0;
    Tab.contentType = 0;
    Tab.width = 512;
    Tab.height = 334;
    Tab.opacity = 0;
    Tab.mouseOverPopupInterface = 0;
    return Tab;
  }

  public static void addTooltip(int id, String text) {
    RSInterface rsinterface = addTabInterface(id);
    rsinterface.parentID = id;
    rsinterface.type = 0;
    rsinterface.isMouseoverTriggered = true;
    rsinterface.mouseOverPopupInterface = -1;
    addTooltipBox(id + 1, text);
    rsinterface.totalChildren(1);
    rsinterface.child(0, id + 1, 0, 0);
  }

  public static void addTooltip(int id, String text, int H, int W) {
    RSInterface rsi = addTabInterface(id);
    rsi.id = id;
    rsi.type = 0;
    rsi.isMouseoverTriggered = true;
    rsi.mouseOverPopupInterface = -1;
    addTooltipBox(id + 1, text);
    rsi.totalChildren(1);
    rsi.child(0, id + 1, 0, 0);
    rsi.height = H;
    rsi.width = W;
  }

  public static void addTooltipBox(int id, String text) {
    RSInterface rsi = addInterface(id);
    rsi.id = id;
    rsi.parentID = id;
    rsi.type = 9;
    rsi.message = text;
  }

  public static void Bank() {
    RSInterface Interface = addTabInterface(5292);
    setChildren(20, Interface);
    addSprite(5293, 0, "Interfaces/Bank/BANK");
    setBounds(5293, 13, 13, 0, Interface);
    addHover(5384, 3, 0, 5380, 1, "Interfaces/Bank/BANK", 17, 17, "Close Window");
    addHovered(5380, 2, "Interfaces/Bank/BANK", 17, 17, 5379);
    setBounds(5384, 476, 16, 3, Interface);
    setBounds(5380, 476, 16, 4, Interface);
    addHover(5294, 4, 0, 5295, 4, "Interfaces/Bank/BANK", 114, 25, "");
    addHovered(5295, 4, "Interfaces/Bank/BANK", 114, 25, 5296);
    setBounds(5294, 444, 38, 5, Interface);
    setBounds(5295, 444, 38, 6, Interface);
    addBankHover(
        21000,
        4,
        21001,
        7,
        8,
        "Interfaces/Bank/BANK",
        35,
        25,
        304,
        1,
        "Swap Withdraw Mode",
        21002,
        7,
        8,
        "Interfaces/Bank/BANK",
        21003,
        "Switch to insert items \nmode",
        "Switch to swap items \nmode.",
        12,
        20);
    setBounds(21000, 25, 285, 7, Interface);
    setBounds(21001, 10, 225, 8, Interface);
    addBankHover(
        21004,
        4,
        21005,
        13,
        15,
        "Interfaces/Bank/BANK",
        35,
        25,
        0,
        1,
        "Search",
        21006,
        14,
        16,
        "Interfaces/Bank/BANK",
        21007,
        "Click here to search your \nbank",
        "Click here to search your \nbank",
        12,
        20);
    setBounds(21004, 65, 285, 9, Interface);
    setBounds(21005, 50, 225, 10, Interface);
    addBankHover(
        21008,
        4,
        21009,
        9,
        11,
        "Interfaces/Bank/BANK",
        35,
        25,
        115,
        1,
        "Search",
        21010,
        10,
        12,
        "Interfaces/Bank/BANK",
        21011,
        "Switch to note withdrawal \nmode",
        "Switch to item withdrawal \nmode",
        12,
        20);
    setBounds(21008, 240, 285, 11, Interface);
    setBounds(21009, 225, 225, 12, Interface);
    addBankHover1(
        21012,
        5,
        21013,
        17,
        "Interfaces/Bank/BANK",
        35,
        25,
        "Deposit carried tems",
        21014,
        18,
        "Interfaces/Bank/BANK",
        21015,
        "Empty your backpack into\nyour bank",
        0,
        20);
    setBounds(21012, 375, 285, 13, Interface);
    setBounds(21013, 360, 225, 14, Interface);
    addBankHover1(
        21016,
        5,
        21017,
        19,
        "Interfaces/Bank/BANK",
        35,
        25,
        "Deposit worn items",
        21018,
        20,
        "Interfaces/Bank/BANK",
        21019,
        "Empty the items your are\nwearing into your bank",
        0,
        20);
    setBounds(21016, 415, 285, 15, Interface);
    setBounds(21017, 400, 225, 16, Interface);
    addBankHover1(
        21020,
        5,
        21021,
        21,
        "Interfaces/Bank/BANK",
        35,
        25,
        "Deposit beast of burden box.",
        21022,
        22,
        "Interfaces/Bank/BANK",
        21023,
        "Empty your BoB's box\ninto your bank",
        0,
        20);
    setBounds(21020, 455, 285, 17, Interface);
    setBounds(21021, 440, 225, 18, Interface);
    setBounds(21022, 455, 42, 19, Interface);
    setBounds(5383, 170, 15, 1, Interface);
    setBounds(5385, -4, 74, 2, Interface);
    Interface = interfaceCache[5385];
    Interface.height = 206;
    Interface.width = 480;
    Interface = interfaceCache[5382];
    Interface.width = 10;
    Interface.invSpritePadX = 12;
    Interface.height = 35;
  }

  public static void boss() {
    RSInterface rsinterface = addTabInterface(45500);
    addHoverButton(45502, "Interfaces/Minigame/Hover", 0, 172, 24, "Nex", -1, 45503, 1);
    addHoveredButton(45503, "Interfaces/Minigame/Hover", 3, 172, 24, 45504);
    addHoverButton(
        45518, "Interfaces/Minigame/Hover", 0, 172, 24, "King Black Dragon", -1, 45519, 1);
    addHoveredButton(45519, "Interfaces/Minigame/Hover", 3, 172, 24, 45520);
    addHoverButton(45521, "Interfaces/Minigame/Hover", 0, 172, 24, "Dagannoth Kings", -1, 45522, 1);
    addHoveredButton(45522, "Interfaces/Minigame/Hover", 3, 172, 24, 45523);
    addHoverButton(
        45524, "Interfaces/Minigame/Hover", 0, 172, 24, "Tormented Demons", -1, 45525, 1);
    addHoveredButton(45525, "Interfaces/Minigame/Hover", 3, 172, 24, 45526);
    addHoverButton(45527, "Interfaces/Minigame/Hover", 0, 172, 24, "Corporal Beast", -1, 45528, 1);
    addHoveredButton(45528, "Interfaces/Minigame/Hover", 3, 172, 24, 45529);
    addHoverButton(45533, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45534, 1);
    addHoveredButton(45534, "Interfaces/Minigame/Back", 1, 16, 16, 45535);
    addSprite(45505, 1, "Interfaces/Minigame/Godwars");
    addSprite(45506, 1, "Interfaces/Minigame/Kbd");
    addSprite(45507, 1, "Interfaces/Minigame/Dagganoths");
    addSprite(45508, 1, "Interfaces/Minigame/Chaos");
    addSprite(45509, 1, "Interfaces/Minigame/Corporeal");
    addSprite(45511, 1, "Interfaces/Minigame/Background");
    byte childAmount = 24;
    int indexChild = 0;
    setChildren(childAmount, rsinterface);
    setBounds(45511, -1, 26, indexChild, rsinterface);
    indexChild++;
    setBounds(45501, 33, 7, indexChild, rsinterface);
    indexChild++;
    setBounds(45502, 8, 35, indexChild, rsinterface);
    indexChild++;
    setBounds(45503, 8, 35, indexChild, rsinterface);
    indexChild++;
    setBounds(45512, 80, 39, indexChild, rsinterface);
    indexChild++;
    setBounds(45518, 8, 72, indexChild, rsinterface);
    indexChild++;
    setBounds(45519, 8, 72, indexChild, rsinterface);
    indexChild++;
    setBounds(45513, 80, 76, indexChild, rsinterface);
    indexChild++;
    setBounds(45521, 8, 109, indexChild, rsinterface);
    indexChild++;
    setBounds(45522, 8, 109, indexChild, rsinterface);
    indexChild++;
    setBounds(45514, 80, 113, indexChild, rsinterface);
    indexChild++;
    setBounds(45524, 8, 146, indexChild, rsinterface);
    indexChild++;
    setBounds(45525, 8, 146, indexChild, rsinterface);
    indexChild++;
    setBounds(45515, 80, 150, indexChild, rsinterface);
    indexChild++;
    setBounds(45527, 8, 183, indexChild, rsinterface);
    indexChild++;
    setBounds(45528, 8, 183, indexChild, rsinterface);
    indexChild++;
    setBounds(45516, 80, 187, indexChild, rsinterface);
    indexChild++;
    setBounds(45505, 148, 33, indexChild, rsinterface);
    indexChild++;
    setBounds(45506, 148, 70, indexChild, rsinterface);
    indexChild++;
    setBounds(45507, 148, 104, indexChild, rsinterface);
    indexChild++;
    setBounds(45508, 148, 144, indexChild, rsinterface);
    indexChild++;
    setBounds(45509, 148, 179, indexChild, rsinterface);
    indexChild++;
    setBounds(45533, 10, 6, indexChild, rsinterface);
    indexChild++;
    setBounds(45534, 10, 6, indexChild, rsinterface);
    indexChild++;
  }

  public static void C(int i, int j, int k, int l, int i1, String s) {
    RSInterface qz = interfaceCache[i] = new RSInterface();
    qz.id = i;
    qz.parentID = i;
    qz.type = 5;
    qz.atActionType = 1;
    qz.contentType = 0;
    qz.width = l;
    qz.height = i1;
    qz.opacity = 0;
    qz.mouseOverPopupInterface = 52;
    qz.tooltip = s;
  }

  public static void Construction() {
    RSInterface Interface = addInterface(31250);
    setChildren(53, Interface);
    addHoverButton(29561, "Interfaces/Construction/BUTTON", 0, 16, 16, "Close", 0, 29562, 1);
    addHoveredButton(29562, "Interfaces/Construction/BUTTON", 1, 16, 16, 29563);
    addSprite(31249, 0, "Interfaces/Construction/CONSTRUCTION");

    addButton(31251, 0, "Interfaces/Construction/CONS", "Build @or1@Fern");
    addTooltip(31252, "Fern (lvl 1):\n1x Kush, 1x Logs");

    addButton(31254, 1, "Interfaces/Construction/CONS", "Build @or1@Tree");
    addTooltip(31255, "Tree (lvl 5):\n3x Logs");

    addButton(31257, 2, "Interfaces/Construction/CONS", "Build @or1@Chair");
    addTooltip(31258, "Chair (lvl 19):\n10x Nails, 2x Oak plank");

    addButton(31260, 3, "Interfaces/Construction/CONS", "Build @or1@Bookcase");
    addTooltip(31261, "Bookcase (lvl 29):\n15x Nails, 3x Oak plank");

    addButton(31263, 4, "Interfaces/Construction/CONS", "Build @or1@Greenman's ale");
    addTooltip(31264, "Greenamn's ale (lvl 26):\n15x Nails, 2x Oak plank");

    addButton(31266, 5, "Interfaces/Construction/CONS", "Build @or1@Small oven");
    addTooltip(31267, "Small oven (lvl 24):\n2x Iron bar");

    addButton(31269, 6, "Interfaces/Construction/CONS", "Build @or1@Carved oak bench");
    addTooltip(31270, "Carved oak bench (lvl 31):\n15x Nails, 3x Oak plank");

    addButton(31272, 7, "Interfaces/Construction/CONS", "Build @or1@Painting stand");
    addTooltip(31273, "Painting stand (lvl 41):\n20x Nails, 2x Oak plank");

    addButton(31275, 8, "Interfaces/Construction/CONS", "Build @or1@Bed");
    addTooltip(31276, "Bed (lvl 40):\n20x Nails, 3x Oak plank");

    addButton(31278, 9, "Interfaces/Construction/CONS", "Build @or1@Teak drawers");
    addTooltip(31279, "Teak drawers (lvl 51):\n20x Nails, 2x Teak plank");

    addButton(31281, 10, "Interfaces/Construction/CONS", "Build @or1@Mithril armour");
    addTooltip(31282, "Mithril armour (lvl 28):\n1x Mithril full helm, platebody, platelegs");

    addButton(31284, 11, "Interfaces/Construction/CONS", "Build @or1@Adamant armour");
    addTooltip(31285, "Adamant armour (lvl 28):\n1x Adamant full helm, platebody, platelegs");

    addButton(31287, 12, "Interfaces/Construction/CONS", "Build @or1@Rune armour");
    addTooltip(31288, "Rune armour (lvl 28):\n1x Rune full helm, platebody, platelegs");

    addButton(31290, 13, "Interfaces/Construction/CONS", "Build @or1@Rune display case");
    addTooltip(
        31291, "Rune display case (lvl 41):\n100x Law rune, 100x Nature rune, 1x Teak plank");

    addButton(31293, 14, "Interfaces/Construction/CONS", "Build @or1@Archery target");
    addTooltip(31294, "Archery target (lvl 81):\n25x Nails, 3x Teak plank");

    addButton(31296, 15, "Interfaces/Construction/CONS", "Build @or1@Combat stone");
    addTooltip(31297, "Combat stone (lvl 59):\n4x Iron bar");

    addButton(31299, 16, "Interfaces/Construction/CONS", "Build @or1@Elemental balance");
    addTooltip(31300, "Elemental balance (lvl 77):\n4x Iron bar");

    addButton(31302, 17, "Interfaces/Construction/CONS", "Build @or1@Mahogany prize chest");
    addTooltip(31303, "Mahogany prize chest (lvl 54):\n20x Nails, 2x Mahogany plank");

    addButton(31305, 18, "Interfaces/Construction/CONS", "Build @or1@Lectern");
    addTooltip(31306, "Lectern (lvl 67):\n40x Nails, 2x Mahogany plank");

    addButton(31308, 19, "Interfaces/Construction/CONS", "Build @or1@Crystal of power");
    addTooltip(31309, "Crystal of power (lvl 66):\n15x Nails, 2x Mahogany plank, 1x Iron bar");

    addButton(31311, 20, "Interfaces/Construction/CONS", "Build @or1@Altar");
    addTooltip(31312, "Altar (lvl 64):\n15x Nails, 2x Mahogany plank, 1x Iron bar");

    addButton(31314, 21, "Interfaces/Construction/CONS", "Build @or1@Intense burners");
    addTooltip(31315, "Intense burners (lvl 61):\n10x Nails, 2x Mahogany plank, 1x Jack Herer");

    addButton(31317, 22, "Interfaces/Construction/CONS", "Build @or1@Hedge");
    addTooltip(31318, "Hedge (lvl 80):\n2x Logs, 2x Jack Herer");

    addButton(31320, 23, "Interfaces/Construction/CONS", "Build @or1@Rocnar");
    addTooltip(31321, "Rocnar (lvl 83):\n2x Adamant bar, 2x Jack Herer");

    addButton(31323, 24, "Interfaces/Construction/CONS", "Build @or1@Bank chest");
    addTooltip(31324, "Bank chest (lvl 92):\n40x Nails, 2x Mahogany plank, 1x Iron bar");

    setBounds(29561, 413, 9, 1, Interface);
    setBounds(29562, 413, 9, 2, Interface);
    setBounds(31249, 69, 3, 0, Interface);

    setBounds(31251, 109, 28, 3, Interface);
    setBounds(31252, 76, 285, 4, Interface);

    setBounds(31254, 172, 28, 5, Interface);
    setBounds(31255, 76, 285, 6, Interface);

    setBounds(31257, 236, 28, 7, Interface);
    setBounds(31258, 76, 285, 8, Interface);

    setBounds(31260, 300, 28, 9, Interface);
    setBounds(31261, 76, 285, 10, Interface);

    setBounds(31263, 364, 28, 11, Interface);
    setBounds(31264, 76, 285, 12, Interface);

    setBounds(31266, 109, 76, 13, Interface);
    setBounds(31267, 76, 285, 14, Interface);

    setBounds(31269, 172, 76, 15, Interface);
    setBounds(31270, 76, 285, 16, Interface);

    setBounds(31272, 236, 76, 17, Interface);
    setBounds(31273, 76, 285, 18, Interface);

    setBounds(31275, 300, 76, 19, Interface);
    setBounds(31276, 76, 285, 20, Interface);

    setBounds(31278, 364, 76, 21, Interface);
    setBounds(31279, 76, 285, 22, Interface);

    setBounds(31281, 109, 124, 23, Interface);
    setBounds(31282, 76, 285, 24, Interface);

    setBounds(31284, 172, 124, 25, Interface);
    setBounds(31285, 76, 285, 26, Interface);

    setBounds(31287, 236, 124, 27, Interface);
    setBounds(31288, 76, 285, 28, Interface);

    setBounds(31290, 300, 124, 29, Interface);
    setBounds(31291, 76, 285, 30, Interface);

    setBounds(31293, 364, 124, 31, Interface);
    setBounds(31294, 76, 285, 32, Interface);

    setBounds(31296, 109, 172, 33, Interface);
    setBounds(31297, 76, 285, 34, Interface);

    setBounds(31299, 172, 172, 35, Interface);
    setBounds(31300, 76, 285, 36, Interface);

    setBounds(31302, 236, 172, 37, Interface);
    setBounds(31303, 76, 285, 38, Interface);

    setBounds(31305, 300, 172, 39, Interface);
    setBounds(31306, 76, 285, 40, Interface);

    setBounds(31308, 364, 172, 41, Interface);
    setBounds(31309, 76, 285, 42, Interface);

    setBounds(31311, 109, 220, 43, Interface);
    setBounds(31312, 76, 285, 44, Interface);

    setBounds(31314, 172, 220, 45, Interface);
    setBounds(31315, 76, 285, 46, Interface);

    setBounds(31317, 236, 220, 47, Interface);
    setBounds(31318, 76, 285, 48, Interface);

    setBounds(31320, 300, 220, 49, Interface);
    setBounds(31321, 76, 285, 50, Interface);

    setBounds(31323, 364, 220, 51, Interface);
    setBounds(31324, 76, 285, 52, Interface);

    Interface = addInterface(31330);
    addSprite(31329, 1, "Interfaces/Construction/CONSTRUCTION");

    addHoverButton(31331, "Interfaces/Construction/BUTTON", 2, 90, 44, "Choose", 0, 31332, 1);
    addHoveredButton(31332, "Interfaces/Construction/BUTTON", 4, 90, 44, 31333);

    addHoverButton(31334, "Interfaces/Construction/BUTTON", 2, 90, 44, "Choose", 0, 31335, 1);
    addHoveredButton(31335, "Interfaces/Construction/BUTTON", 4, 90, 44, 31336);

    addHoverButton(29561, "Interfaces/Construction/BUTTON", 0, 16, 16, "Close", 0, 29562, 1);
    addHoveredButton(29562, "Interfaces/Construction/BUTTON", 1, 16, 16, 29563);

    setChildren(9, Interface);
    setBounds(31329, 169, 79, 0, Interface);

    setBounds(31331, 195, 95, 1, Interface);
    setBounds(31332, 195, 95, 2, Interface);

    setBounds(31334, 195, 157, 3, Interface);
    setBounds(31335, 195, 157, 4, Interface);

    setBounds(31337, 210, 108, 5, Interface);
    setBounds(31338, 210, 170, 6, Interface);

    setBounds(29561, 289, 85, 7, Interface);
    setBounds(29562, 289, 85, 8, Interface);
  }

  public static void GodWars() {
    RSInterface rsinterface = addTabInterface(16220);
    rsinterface.scrollMax = 0;
    rsinterface.children = new int[9];
    rsinterface.childX = new int[9];
    rsinterface.childY = new int[9];
    rsinterface.children[0] = 16211;
    rsinterface.childX[0] = -52 + 375 + 30;
    rsinterface.childY[0] = 7;
    rsinterface.children[1] = 16212;
    rsinterface.childX[1] = -52 + 375 + 30;
    rsinterface.childY[1] = 30;
    rsinterface.children[2] = 16213;
    rsinterface.childX[2] = -52 + 375 + 30;
    rsinterface.childY[2] = 44;
    rsinterface.children[3] = 16214;
    rsinterface.childX[3] = -52 + 375 + 30;
    rsinterface.childY[3] = 58;
    rsinterface.children[4] = 16215;
    rsinterface.childX[4] = -52 + 375 + 30;
    rsinterface.childY[4] = 73;

    rsinterface.children[5] = 16216;
    rsinterface.childX[5] = -52 + 460 + 60;
    rsinterface.childY[5] = 31;
    rsinterface.children[6] = 16217;
    rsinterface.childX[6] = -52 + 460 + 60;
    rsinterface.childY[6] = 45;
    rsinterface.children[7] = 16218;
    rsinterface.childX[7] = -52 + 460 + 60;
    rsinterface.childY[7] = 59;
    rsinterface.children[8] = 16219;
    rsinterface.childX[8] = -52 + 460 + 60;
    rsinterface.childY[8] = 74;
  }

  public static void minigame() {
    RSInterface rsinterface = addTabInterface(45200);
    addHoverButton(45202, "Interfaces/Minigame/Hover", 0, 172, 24, "Duel Arena", -1, 45203, 1);
    addHoveredButton(45203, "Interfaces/Minigame/Hover", 4, 172, 24, 45204);
    addHoverButton(45218, "Interfaces/Minigame/Hover", 0, 172, 24, "Barrows", -1, 45219, 1);
    addHoveredButton(45219, "Interfaces/Minigame/Hover", 4, 172, 24, 45220);
    addHoverButton(45221, "Interfaces/Minigame/Hover", 0, 172, 24, "Pest Control", -1, 45222, 1);
    addHoveredButton(45222, "Interfaces/Minigame/Hover", 4, 172, 24, 45223);
    addHoverButton(45224, "Interfaces/Minigame/Hover", 0, 172, 24, "Tzhaar", -1, 45225, 1);
    addHoveredButton(45225, "Interfaces/Minigame/Hover", 4, 172, 24, 45226);
    addHoverButton(45227, "Interfaces/Minigame/Hover", 0, 172, 24, "Warriors Guild", -1, 45228, 1);
    addHoveredButton(45228, "Interfaces/Minigame/Hover", 4, 172, 24, 45229);
    addHoverButton(45233, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45234, 1);
    addHoveredButton(45234, "Interfaces/Minigame/Back", 1, 16, 16, 45235);
    addSprite(45205, 1, "Interfaces/Minigame/DuelArena");
    addSprite(45206, 1, "Interfaces/Minigame/Barrows");
    addSprite(45207, 1, "Interfaces/Minigame/PestControl");
    addSprite(45208, 1, "Interfaces/Minigame/Tzhaar");
    addSprite(45209, 1, "Interfaces/Minigame/Warriors");
    addSprite(45211, 1, "Interfaces/Minigame/Background");
    byte childAmount = 24;
    int indexChild = 0;
    setChildren(childAmount, rsinterface);
    setBounds(45211, 0, 26, indexChild, rsinterface);
    indexChild++;
    setBounds(45201, 33, 7, indexChild, rsinterface);
    indexChild++;
    setBounds(45202, 8, 35, indexChild, rsinterface);
    indexChild++;
    setBounds(45203, 8, 35, indexChild, rsinterface);
    indexChild++;
    setBounds(45212, 80, 39, indexChild, rsinterface);
    indexChild++;
    setBounds(45218, 8, 72, indexChild, rsinterface);
    indexChild++;
    setBounds(45219, 8, 72, indexChild, rsinterface);
    indexChild++;
    setBounds(45213, 80, 76, indexChild, rsinterface);
    indexChild++;
    setBounds(45221, 8, 109, indexChild, rsinterface);
    indexChild++;
    setBounds(45222, 8, 109, indexChild, rsinterface);
    indexChild++;
    setBounds(45214, 80, 113, indexChild, rsinterface);
    indexChild++;
    setBounds(45224, 8, 146, indexChild, rsinterface);
    indexChild++;
    setBounds(45225, 8, 146, indexChild, rsinterface);
    indexChild++;
    setBounds(45215, 80, 150, indexChild, rsinterface);
    indexChild++;
    setBounds(45227, 8, 183, indexChild, rsinterface);
    indexChild++;
    setBounds(45228, 8, 183, indexChild, rsinterface);
    indexChild++;
    setBounds(45216, 80, 187, indexChild, rsinterface);
    indexChild++;
    setBounds(45205, 148, 33, indexChild, rsinterface);
    indexChild++;
    setBounds(45206, 148, 70, indexChild, rsinterface);
    indexChild++;
    setBounds(45207, 148, 104, indexChild, rsinterface);
    indexChild++;
    setBounds(45208, 148, 140, indexChild, rsinterface);
    indexChild++;
    setBounds(45209, 148, 179, indexChild, rsinterface);
    indexChild++;
    setBounds(45233, 10, 6, indexChild, rsinterface);
    indexChild++;
    setBounds(45234, 10, 6, indexChild, rsinterface);
    indexChild++;
  }

  public static void setChildren(int total, RSInterface i) {
    i.children = new int[total];
    i.childX = new int[total];
    i.childY = new int[total];
  }

  public static void Shop() {
    RSInterface rsinterface = addTabInterface(3824);
    setChildren(8, rsinterface);
    addSprite(3825, 0, "Interfaces/Shop/SHOP");
    addHover(3902, 3, 0, 3826, 1, "Interfaces/Shop/CLOSE", 17, 17, "Close Window");
    addHovered(3826, 2, "Interfaces/Shop/CLOSE", 17, 17, 3827);
    addButton(19681, 2, "Interfaces/Shop/SHOP", 0, 0, "", 1);
    addSprite(19687, 1, "Interfaces/Shop/ITEMBG");
    setBounds(3825, 6, 8, 0, rsinterface);
    setBounds(3902, 478, 10, 1, rsinterface);
    setBounds(3826, 478, 10, 2, rsinterface);
    setBounds(3900, 26, 44, 3, rsinterface);
    setBounds(3901, 240, 11, 4, rsinterface);
    setBounds(19679, 42, 54, 5, rsinterface);
    setBounds(19680, 150, 54, 6, rsinterface);
    setBounds(19681, 129, 50, 7, rsinterface);
    rsinterface = interfaceCache[3900];
    setChildren(1, rsinterface);
    setBounds(19687, 6, 15, 0, rsinterface);
    rsinterface.invSpritePadX = 15;
    rsinterface.width = 10;
    rsinterface.height = 4;
    rsinterface.invSpritePadY = 25;
    rsinterface = addTabInterface(19682);
    addSprite(19683, 1, "Interfaces/Shop/SHOP");
    addButton(19686, 2, "Interfaces/Shop/SHOP", 95, 19, "Main Stock", 1);
    setChildren(7, rsinterface);
    setBounds(19683, 12, 12, 0, rsinterface);
    setBounds(3901, 240, 21, 1, rsinterface);
    setBounds(19684, 42, 54, 2, rsinterface);
    setBounds(19685, 150, 54, 3, rsinterface);
    setBounds(19686, 23, 50, 4, rsinterface);
    setBounds(3902, 471, 22, 5, rsinterface);
    setBounds(3826, 60, 85, 6, rsinterface);
  }

  public static void teleport() {
    RSInterface localRSInterface = addInterface(11650);
    addSprite(11651, 10, "CLICK");
    addHoverButton(11652, "CLICK", 2, 200, 30, "Which Zone?", -1, 11653, 1);
    addHoveredButton(11653, "CLICK", 2, 200, 30, 11654);
    addHoverButton(11655, "CLICK", 3, 200, 30, "Which Zone?", -1, 11656, 1);
    addHoveredButton(11656, "CLICK", 3, 200, 30, 11657);
    addHoverButton(11658, "CLICK", 3, 200, 30, "Which Zone?", -1, 11659, 1);
    addHoveredButton(11659, "CLICK", 3, 200, 30, 11660);
    addHoverButton(11661, "CLICK", 3, 200, 30, "Which Zone?", -1, 11662, 1);
    addHoveredButton(11662, "CLICK", 3, 200, 30, 11663);
    addHoverButton(11664, "CLICK", 3, 200, 30, "Which Zone?", -1, 11665, 1);
    addHoveredButton(11665, "CLICK", 3, 200, 30, 11666);
    addHoverButton(11667, "CLICK", 3, 200, 30, "Which Zone?", -1, 11668, 1);
    addHoveredButton(11668, "CLICK", 3, 200, 30, 11669);
    addHoverButton(11670, "CLICK", 3, 200, 30, "Which Zone?", -1, 11671, 1);
    addHoveredButton(11671, "CLICK", 3, 200, 30, 11672);
    addHoverButton(11673, "CLICK", 1, 200, 30, "Stop Viewing", -1, 11674, 1);
    addHoveredButton(11674, "CLICK", 1, 200, 30, 11675);
    localRSInterface.totalChildren(24);
    localRSInterface.child(0, 11651, 0, 0);
    localRSInterface.child(1, 11652, 12, 40);
    localRSInterface.child(2, 11653, 11, 40);
    localRSInterface.child(3, 11655, 12, 65);
    localRSInterface.child(4, 11656, 11, 65);
    localRSInterface.child(5, 11658, 12, 90);
    localRSInterface.child(6, 11659, 11, 90);
    localRSInterface.child(7, 11661, 12, 115);
    localRSInterface.child(8, 11662, 11, 115);
    localRSInterface.child(9, 11664, 12, 143);
    localRSInterface.child(10, 11665, 11, 143);
    localRSInterface.child(11, 11667, 12, 168);
    localRSInterface.child(12, 11668, 11, 168);
    localRSInterface.child(13, 11670, 12, 193);
    localRSInterface.child(14, 11671, 11, 193);
    localRSInterface.child(15, 11673, 38, 236);
    localRSInterface.child(16, 11674, 38, 236);
    localRSInterface.child(17, 11204, 38, 45);
    localRSInterface.child(18, 11208, 38, 70);
    localRSInterface.child(19, 11212, 38, 95);
    localRSInterface.child(20, 11216, 38, 120);
    localRSInterface.child(21, 11220, 38, 147);
    localRSInterface.child(22, 11224, 38, 174);
    localRSInterface.child(23, 11228, 38, 201);
    localRSInterface = addTabInterface(14000);
    localRSInterface.width = 474;
    localRSInterface.height = 213;
    localRSInterface.scrollMax = 305;
    for (int i = 14001; i <= 14030; ++i) {}
    localRSInterface.totalChildren(30);
    int i = 0;
    int j = 5;
    for (int k = 14001; k <= 14030; ++k) {
      localRSInterface.child(i, k, 248, j);
      ++i;
      j += 13;
    }
  }

  public static void unpack() {
    ByteStreamExt byteVector = null;
    try {
      File f = new File("./data/def/interface");
      byte[] buffer = new byte[(int) f.length()];
      DataInputStream dis = new DataInputStream(new FileInputStream(f));
      dis.readFully(buffer);
      dis.close();
      byteVector = new ByteStreamExt(buffer);
    } catch (Exception e) {
      e.printStackTrace();
    }
    int i = -1;
    byteVector.readUnsignedWord();
    interfaceCache = new RSInterface[60000];
    do {
      if (byteVector.currentOffset >= byteVector.buffer.length) break;
      int k = byteVector.readUnsignedWord();
      if (k == 65535) {
        i = byteVector.readUnsignedWord();
        k = byteVector.readUnsignedWord();
      }
      RSInterface rsInterface = interfaceCache[k] = new RSInterface();
      rsInterface.id = k;
      rsInterface.parentID = i;
      rsInterface.type = byteVector.readUnsignedByte();
      rsInterface.atActionType = byteVector.readUnsignedByte();
      rsInterface.contentType = byteVector.readUnsignedWord();
      rsInterface.width = byteVector.readUnsignedWord();
      rsInterface.height = byteVector.readUnsignedWord();
      rsInterface.opacity = (byte) byteVector.readUnsignedByte();
      rsInterface.mouseOverPopupInterface = byteVector.readUnsignedByte();
      if (rsInterface.mouseOverPopupInterface != 0)
        rsInterface.mouseOverPopupInterface =
            (rsInterface.mouseOverPopupInterface - 1 << 8) + byteVector.readUnsignedByte();
      else rsInterface.mouseOverPopupInterface = -1;
      int i1 = byteVector.readUnsignedByte();
      if (i1 > 0) {
        rsInterface.valueCompareType = new int[i1];
        rsInterface.requiredValues = new int[i1];
        for (int j1 = 0; j1 < i1; j1++) {
          rsInterface.valueCompareType[j1] = byteVector.readUnsignedByte();
          rsInterface.requiredValues[j1] = byteVector.readUnsignedWord();
        }
      }
      int k1 = byteVector.readUnsignedByte();
      if (k1 > 0) {
        rsInterface.valueIndexArray = new int[k1][];
        for (int l1 = 0; l1 < k1; l1++) {
          int i3 = byteVector.readUnsignedWord();
          rsInterface.valueIndexArray[l1] = new int[i3];
          for (int l4 = 0; l4 < i3; l4++)
            rsInterface.valueIndexArray[l1][l4] = byteVector.readUnsignedWord();
        }
      }
      if (rsInterface.type == 0) {
        rsInterface.drawsTransparent = false;
        rsInterface.scrollMax = byteVector.readUnsignedWord();
        rsInterface.isMouseoverTriggered = byteVector.readUnsignedByte() == 1;
        int i2 = byteVector.readUnsignedWord();
        rsInterface.children = new int[i2];
        rsInterface.childX = new int[i2];
        rsInterface.childY = new int[i2];
        for (int j3 = 0; j3 < i2; j3++) {
          rsInterface.children[j3] = byteVector.readUnsignedWord();
          rsInterface.childX[j3] = byteVector.readSignedWord();
          rsInterface.childY[j3] = byteVector.readSignedWord();
        }
      }
      if (rsInterface.type == 1) {
        byteVector.readUnsignedWord();
        byteVector.readUnsignedByte();
      }
      if (rsInterface.type == 2) {
        rsInterface.inv = new int[rsInterface.width * rsInterface.height];
        rsInterface.invStackSizes = new int[rsInterface.width * rsInterface.height];
        rsInterface.aBoolean259 = byteVector.readUnsignedByte() == 1;
        rsInterface.isBoxInterface = byteVector.readUnsignedByte() == 1;
        rsInterface.usableItemInterface = byteVector.readUnsignedByte() == 1;
        rsInterface.aBoolean235 = byteVector.readUnsignedByte() == 1;
        rsInterface.invSpritePadX = byteVector.readUnsignedByte();
        rsInterface.invSpritePadY = byteVector.readUnsignedByte();
        rsInterface.spritesX = new int[20];
        rsInterface.spritesY = new int[20];
        for (int j2 = 0; j2 < 20; j2++) {
          int k3 = byteVector.readUnsignedByte();
          if (k3 != 1) continue;
          rsInterface.spritesX[j2] = byteVector.readSignedWord();
          rsInterface.spritesY[j2] = byteVector.readSignedWord();
          byteVector.readString();
        }

        rsInterface.itemActions = new String[6];
        for (int l3 = 0; l3 < 5; l3++) {
          rsInterface.itemActions[l3] = byteVector.readString();
          if (rsInterface.parentID == 3824) rsInterface.itemActions[4] = "Buy X";
          if (rsInterface.itemActions[l3].length() == 0) rsInterface.itemActions[l3] = null;
          if (rsInterface.parentID == 1644) rsInterface.itemActions[2] = "Operate";
        }
      }
      if (rsInterface.type == 3) rsInterface.aBoolean227 = byteVector.readUnsignedByte() == 1;
      if (rsInterface.type == 4 || rsInterface.type == 1) {
        rsInterface.centerText = byteVector.readUnsignedByte() == 1;
        byteVector.readUnsignedByte();
        rsInterface.textShadow = byteVector.readUnsignedByte() == 1;
      }
      if (rsInterface.type == 4) {
        rsInterface.message = byteVector.readString();
        rsInterface.disabledText = byteVector.readString();
      }
      if (rsInterface.type == 1 || rsInterface.type == 3 || rsInterface.type == 4)
        rsInterface.textColor = byteVector.readDWord();
      if (rsInterface.type == 3 || rsInterface.type == 4) {
        rsInterface.anInt219 = byteVector.readDWord();
        rsInterface.textHoverColour = byteVector.readDWord();
        rsInterface.anInt239 = byteVector.readDWord();
      }
      if (rsInterface.type == 5) {
        rsInterface.drawsTransparent = false;
        byteVector.readString();
        byteVector.readString();
      }
      if (rsInterface.type == 6) {
        int l = byteVector.readUnsignedByte();
        if (l != 0) {
          rsInterface.anInt233 = 1;
          rsInterface.mediaID = (l - 1 << 8) + byteVector.readUnsignedByte();
        }
        l = byteVector.readUnsignedByte();
        if (l != 0) {
          rsInterface.anInt255 = 1;
          rsInterface.anInt256 = (l - 1 << 8) + byteVector.readUnsignedByte();
        }
        l = byteVector.readUnsignedByte();
        if (l != 0) rsInterface.anInt257 = (l - 1 << 8) + byteVector.readUnsignedByte();
        else rsInterface.anInt257 = -1;
        l = byteVector.readUnsignedByte();
        if (l != 0) rsInterface.anInt258 = (l - 1 << 8) + byteVector.readUnsignedByte();
        else rsInterface.anInt258 = -1;
        rsInterface.modelZoom = byteVector.readUnsignedWord();
        rsInterface.modelRotation1 = byteVector.readUnsignedWord();
        rsInterface.modelRotation2 = byteVector.readUnsignedWord();
      }
      if (rsInterface.type == 7) {
        rsInterface.inv = new int[rsInterface.width * rsInterface.height];
        rsInterface.invStackSizes = new int[rsInterface.width * rsInterface.height];
        rsInterface.centerText = byteVector.readUnsignedByte() == 1;
        byteVector.readUnsignedByte();
        rsInterface.textShadow = byteVector.readUnsignedByte() == 1;
        rsInterface.textColor = byteVector.readDWord();
        rsInterface.invSpritePadX = byteVector.readSignedWord();
        rsInterface.invSpritePadY = byteVector.readSignedWord();
        rsInterface.isBoxInterface = byteVector.readUnsignedByte() == 1;
        rsInterface.itemActions = new String[6];
        for (int k4 = 0; k4 < 5; k4++) {
          rsInterface.itemActions[k4] = byteVector.readString();
          if (rsInterface.itemActions[k4].length() == 0) rsInterface.itemActions[k4] = null;
        }
      }
      if (rsInterface.atActionType == 2 || rsInterface.type == 2) {
        rsInterface.selectedActionName = byteVector.readString();
        rsInterface.spellName = byteVector.readString();
        rsInterface.spellUsableOn = byteVector.readUnsignedWord();
      }
      if (rsInterface.type == 8) rsInterface.message = byteVector.readString();
      if (rsInterface.atActionType == 1
          || rsInterface.atActionType == 4
          || rsInterface.atActionType == 5
          || rsInterface.atActionType == 6) {
        rsInterface.tooltip = byteVector.readString();
        if (rsInterface.tooltip.length() == 0) {
          if (rsInterface.atActionType == 1) rsInterface.tooltip = "Ok";
          if (rsInterface.atActionType == 4) rsInterface.tooltip = "Select";
          if (rsInterface.atActionType == 5) rsInterface.tooltip = "Select";
          if (rsInterface.atActionType == 6) rsInterface.tooltip = "Continue";
        }
      }
    } while (true);

    unpackCustom();

    logger.info("All interfaces have been loaded successfully.");
  }

  public static void unpackCustom() {
    RSInterface tab = addTabInterface(962);
    addSprite(27800, 2, "Music/img");
    addSprite(27801, 3, "Music/img");
    addButton(27802, 0, "Music/img", 25, 25, "Loop", 1);
    tab.totalChildren(24);
    for (int i = 0; i < 6; i++) {
      tab.child(2 + i, 27801, 32 * i, 59);
      tab.child(8 + i, 27801, 32 * i, 240);
      tab.child(14 + i, 27801, 32 * i, 35);
    }
    tab.child(0, 27800, 0, 62);
    tab.child(1, 963, 0, 62);
    tab.child(20, 27802, 75, 5);
    tab.child(21, 27803, 122, 5);
    tab.child(22, 27804, 121, 16);
    tab.child(23, 27805, 62, 40);
    RSInterface list = addTabInterface(963);
    list.totalChildren(676);
    for (int i = 27000; i < 27676; i++) {
      addClickableText(
          i, musicNames[(i - 27000)], musicNames[(i - 27000)], 0, 65280, 16777215, 162, 11);
    }
    int id = 27000;
    for (int i = 0; (id < 27676) && (i < 676); i++) {
      list.children[i] = id;
      list.childX[i] = 12;
      int id2 = 27000;
      for (int i2 = 1; (id2 < 27676) && (i2 < 676); i2++) {
        list.childY[0] = 2;
        list.childY[i2] = (list.childY[(i2 - 1)] + 15);

        id2++;
      }
      id++;
    }

    list.height = 178;
    list.width = 174;
    list.scrollMax = 10143;
  }

  public static void wilderness() {

    RSInterface rsinterface = addTabInterface(45600);
    addHoverButton(45602, "Interfaces/Minigame/Hover", 0, 172, 24, "Mage Bank", -1, 45603, 1);
    addHoveredButton(45603, "Interfaces/Minigame/Hover", 2, 172, 24, 45604);
    addHoverButton(
        45618, "Interfaces/Minigame/Hover", 0, 172, 24, "Varrock PK (Multi)", -1, 45619, 1);
    addHoveredButton(45619, "Interfaces/Minigame/Hover", 2, 172, 24, 45620);
    addHoverButton(
        45621, "Interfaces/Minigame/Hover", 0, 172, 24, "GraveYard (Lvl 19)", -1, 45622, 1);
    addHoveredButton(45622, "Interfaces/Minigame/Hover", 2, 172, 24, 45623);
    addHoverButton(45624, "Interfaces/Minigame/Hover", 0, 172, 24, "Edgeville", -1, 45625, 1);
    addHoveredButton(45625, "Interfaces/Minigame/Hover", 2, 172, 24, 45626);
    addHoverButton(45627, "Interfaces/Minigame/Hover", 0, 172, 24, "Green Dragons", -1, 45628, 1);
    addHoveredButton(45628, "Interfaces/Minigame/Hover", 2, 172, 24, 45629);
    addHoverButton(45633, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45634, 1);
    addHoveredButton(45634, "Interfaces/Minigame/Back", 1, 16, 16, 45635);
    addSprite(45605, 1, "Interfaces/Minigame/Pk");
    addSprite(45606, 1, "Interfaces/Minigame/Pk");
    addSprite(45607, 1, "Interfaces/Minigame/Pk");
    addSprite(45608, 1, "Interfaces/Minigame/Pk");
    addSprite(45609, 1, "Interfaces/Minigame/Pk");
    addSprite(45611, 1, "Interfaces/Minigame/Background");
    byte childAmount = 24;
    int indexChild = 0;
    setChildren(childAmount, rsinterface);
    setBounds(45611, -1, 26, indexChild, rsinterface);
    indexChild++;
    setBounds(45601, 33, 7, indexChild, rsinterface);
    indexChild++;
    setBounds(45602, 8, 35, indexChild, rsinterface);
    indexChild++;
    setBounds(45603, 8, 35, indexChild, rsinterface);
    indexChild++;
    setBounds(45612, 80, 39, indexChild, rsinterface);
    indexChild++;
    setBounds(45618, 8, 72, indexChild, rsinterface);
    indexChild++;
    setBounds(45619, 8, 72, indexChild, rsinterface);
    indexChild++;
    setBounds(45613, 80, 76, indexChild, rsinterface);
    indexChild++;
    setBounds(45621, 8, 109, indexChild, rsinterface);
    indexChild++;
    setBounds(45622, 8, 109, indexChild, rsinterface);
    indexChild++;
    setBounds(45614, 80, 113, indexChild, rsinterface);
    indexChild++;
    setBounds(45624, 8, 146, indexChild, rsinterface);
    indexChild++;
    setBounds(45625, 8, 146, indexChild, rsinterface);
    indexChild++;
    setBounds(45615, 80, 150, indexChild, rsinterface);
    indexChild++;
    setBounds(45627, 8, 183, indexChild, rsinterface);
    indexChild++;
    setBounds(45628, 8, 183, indexChild, rsinterface);
    indexChild++;
    setBounds(45616, 80, 187, indexChild, rsinterface);
    indexChild++;
    setBounds(45605, 148, 34, indexChild, rsinterface);
    indexChild++;
    setBounds(45606, 148, 71, indexChild, rsinterface);
    indexChild++;
    setBounds(45607, 148, 108, indexChild, rsinterface);
    indexChild++;
    setBounds(45608, 148, 146, indexChild, rsinterface);
    indexChild++;
    setBounds(45609, 148, 183, indexChild, rsinterface);
    indexChild++;
    setBounds(45633, 10, 6, indexChild, rsinterface);
    indexChild++;
    setBounds(45634, 10, 6, indexChild, rsinterface);
    indexChild++;
  }

  public static void Z(int i, int j, int k, String s) {
    RSInterface qz = interfaceCache[i] = new RSInterface();
    qz.id = i;
    qz.parentID = i;
    qz.type = 5;
    qz.atActionType = 1;
    qz.contentType = 0;
    qz.width = 1;
    qz.height = 1;
    qz.opacity = 0;
    qz.mouseOverPopupInterface = 52;
    qz.tooltip = s;
  }

  public void child(int id, int interID, int x, int y) {
    children[id] = interID;
    childX[id] = x;
    childY[id] = y;
  }

  public void totalChildren(int t) {
    children = new int[t];
    childX = new int[t];
    childY = new int[t];
  }

  public void totalChildren(int id, int x, int y) {
    children = new int[id];
    childX = new int[x];
    childY = new int[y];
  }
}
