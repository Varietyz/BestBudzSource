package com.mayhem.rs2.content;

import java.util.HashMap;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.content.dialogue.OptionDialogue;
import com.mayhem.rs2.content.minigames.clanwars.ClanWarsConstants;
import com.mayhem.rs2.content.minigames.weapongame.WeaponGameConstants;
import com.mayhem.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.impl.Cerberus;
import com.mayhem.rs2.entity.mob.impl.GelatinnothMother;
import com.mayhem.rs2.entity.mob.impl.Shaman;
import com.mayhem.rs2.entity.mob.impl.Zulrah;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles teleporting around with interface
 * @author Daniel
 *
 */
public class TeleportHandler {

	/**
	 * Teleportation data
	 * @author Daniel
	 *
	 */
	public enum TeleportationData {

		/** Training */
		ROCK_CRABS(238123, "None", "None", "None", 0, new Location(2674, 3710, 0), false, false),
		SAND_CRABS(238124, "None", "None", "None", 0, new Location(1757, 3480, 0), false, false),
		AL_KAHID(238125, "None", "None", "None", 0, new Location(3293, 3182, 0), false, false),
		COWS(238126, "None", "None", "None", 0, new Location(3257, 3263, 0), false, false),
		YAKS(238127, "None", "None", "None", 0, new Location(2327, 3796, 0), false, false),
		BRIMHAVEN_DUNG(238128, "None", "None", "None", 0, new Location(2710, 9466, 0), false, false),
		TAVERLY_DUNG(238129, "None", "None", "None", 0, new Location(2884, 9798, 0), false, false),
		SLAYER_TOWER(238130, "None", "None", "None", 0, new Location(3428, 3538, 0), false, false),
		LAVA_DRAGONS(238131, "None", "40+ Wild!", "None", 0, new Location(3202, 3860, 0), false, false),
		MITHRIL_DRAGONS(238132, "None", "None", "None", 0, new Location(1747, 5324, 0), false, false),
		HILL_GIANTS(238133, "None", "None", "None", 0, new Location(3117, 9856, 0), false, false),
		CRYSTAL_MONSTERS(238134, "None", "Wilderness!", "None", 0, new Location(3117, 9856, 0), false, true),
		CAVE_HORRORS(238135, "Drop: Black mask", "58 slayer", "None", 0, new Location(3761, 9454, 0), false, false),
		NIEVES_SLAYER_DUNGEON(238136, "Cave Krakens", "87 slayer", "None", 0, new Location(2443, 9808, 0), false, false),
		SUPERIOR_SLAYER_DUNGEON(238137, "Superior tasks", "High-Level", "None", 0, new Location(1650, 9988, 0), false, false),
		REVENANT_CAVE(238138, "Drop: PvP Sets", "WILDERNESS", "None", 0, new Location(3201, 3876, 0), false, false),

		/** Skilling */
		WILD_RESOURCE(242099, "None", "@mag@54 Wild!", "None", 0, new Location(3184, 3947, 0), false, false),
		THIEVING(242100, "None", "None", "None", 0, new Location(3047, 4976, 1), false, false),
		CRAFTING(242101, "None", "None", "None", 0, new Location(2747, 3444, 0), false, false),
		AGILITY(242102, "None", "None", "None", 0, new Location(2480, 3437, 0), false, true),
		MINING(242103, "None", "None", "None", 0, new Location(3044, 9785, 0), false, true),
		SMITHING(242104, "None", "None", "None", 0, new Location(3186, 3425, 0), false, false),
		FISHING(242105, "None", "None", "None", 0, new Location(1846, 3786, 0), false, false),
		WOODCUTTING(242106, "None", "None", "None", 0, new Location(1590, 3481, 0), false, false),
		FARMING(242107, "None", "None", "None", 0, new Location(2806, 3463, 0), false, true),
		HUNTER(242108, "None", "Puro points", "None", 0, new Location(2525, 2914, 0), false, false),

		/** Player Vs Player */
		EDGEVILLE(246075, "1v1 pk", "Pk gear", "1", 0, new Location(3087, 3515, 0), false, false),
		VARROCK(246076, "Multi pk", "Pk gear", "1", 0, new Location(3244, 3512, 0), false, false),
		EAST_DRAGONS(246077, "Hybrid pk", "Pk gear", "19", 0, new Location(3333, 3666, 0), false, false),
		CASTLE(246078, "1v1 pk", "Pk gear", "14", 0, new Location(3002, 3626, 0), false, false),
		MAGE_BANK(246079, "Hybrid pk", "Pk gear", "@mag@54 Wild", 0, new Location(2540, 4717, 0), false, false),
		DEMONIC_RUINS(246080, "Multi pk", "Pk gear", "@mag@46 Wild", 0, new Location(3287, 3884, 0), false, false),

		/** Player Vs Monster */
		KING_BLACK_DRAGON(250051, "None", "High combat", "@or1@40+ Wild", 276, new Location(2997, 3849, 0), false, false),
		SEA_TROLL_QUEEN(250052, "None", "High combat", "Multi", 170, new Location(2336, 3692, 0), false, false),
		BARRELCHEST(250053, "None", "High combat", "Instanced", 190, new Location(2638, 9107, 0), true, true),
		CORPOREAL_BEAST(250054, "None", "High combat", "Team Based", 785, new Location(2968, 4384, 2), false, false),
		DAGGANNOTH_KINGS(250055, "None", "High combat", "", 303, new Location(1909, 4367, 0), false, false),
		GOD_WARS(250056, "None", "High combat", "Team Based", 0,  new Location(2882, 5308, 2), false, false),
		ZULRAH(250057, "None", "High combat", "Hard", 725, new Location(2268, 3070, 0), true, true),
		KRAKEN(250058, "None", "High combat", "", 291, new Location(3696, 5807, 0), true, true),
		GIANT_MOLE(250059, "None", "High combat", "", 230, new Location(1760, 5163, 0), false, false),
		CHAOS_ELEMENTAL(250060, "None", "High combat", "50+ Wild", 305, new Location(3284, 3913, 0), false, false),
		CALLISTO(250061, "None", "High combat", "40+ Wild", 470, new Location(3283, 3853, 0), false, false),
		SCORPIA(250062, "None", "High combat", "50+ Wild", 225, new Location(3233, 3943, 0), false, false),
		VETION(250063, "None", "High combat", "30+ Wild", 454, new Location(3210, 3780, 0), false, false),
		VENENATIS(250064, "None", "High combat", "20+ Wild", 330, new Location(3317, 3745, 0), false, false),
		CHAOS_FANATIC(250065, "None", "High combat", "40+ Wild", 202, new Location(2981, 3837, 0), false, false),
		CRAZY_ARCHAEOLOGIST(250066, "None", "High combat", "20+ Wild", 204, new Location(2975, 3715, 0), false, false),
		CERBERUS(250067, "None", "High combat", "Instanced", 318, new Location(1240, 1239, 0), true, true),
		THERMO(250068, "@whi@Prepare To Die", "@yel@High combat", "@mag@40+ Wild", 318, new Location(2951, 3819, 0), false, false),
		SHAMAN(250069, "None", "High combat", "Instanced", 150, new Location(1444, 3703, 0), true, true),
		DEMONIC_GORILLAS(250070, "None", "Medium combat", "None", 204, new Location(2142, 5678, 0), false, false),
		GELATINNOTH_MOTHER(250071, "6 Phases", "Medium", "Instanced", 318, new Location(3748, 5761, 0), true, true),
		RECEIPE_FOR_DISASTER(250072, "None", "Low Combat", "None", 318, new Location(3748, 5761, 0), false, true),
		ANCIENT_WARRIORS(250073, "None", "Wilderness", "None", 318, new Location(3748, 5761, 0), false, true),
		KALPHITE_QUEEN(250074, "2 Phases", "Medium combat", "", 333, new Location(3485, 9509, 0), false, false),
		THE_EVERLASTING(250075, "Pray Range", "MAGIC ONLY", "Instanced", 223, new Location(2275, 4705, 0), true, true),
		

		/** Minigames */
		BARROWS(254027, "None", "None", "None", 0, new Location(3565, 3315, 0), false, false),
		WARRIORS_GUILD(254028, "None", "None", "None", 0, new Location(2869, 3544, 0), false, false),
		DUEL_ARENA(254029, "None", "None", "None", 0, new Location(3365, 3265, 0), false, false),
		PEST_CONTROL(254030, "None", "None", "None", 0, new Location(2662, 2655, 0), false, false),
		FIGHT_CAVES(254031, "None", "None", "None", 0, new Location(2439, 5171, 0), false, false),
		WEAPON_GAME(254032, "None", "None", "None", 0, WeaponGameConstants.LOBBY_COODINATES, false, false),
		CLAN_WARS(254033, "None", "None", "None", 0, ClanWarsConstants.CLAN_WARS_ARENA, false, true),

		/** Cities */
		MEMBERSHIP(240111, "None", "Donators only", "None", 0, PlayerConstants.MEMEBER_AREA, false, true),
		//STAFFZONE(240112, "None", "Staff only", "None", 0, PlayerConstants.STAFF_AREA, false, true);
		SHOPS(240112, "None", "All Shops", "None", 0, PlayerConstants.SHOPS, false, true),
		VARROCK_CENTER(240113, "None", "None", "None", 0, PlayerConstants.VARROCK_CENTER, false, true),
		FALADOR(240114, "None", "None", "None", 0, PlayerConstants.FALADOR, false, true),
		CAMELOT(240115, "None", "None", "None", 0, PlayerConstants.CAMELOT, false, true),
		ZEAH(240116, "None", "None", "None", 0, PlayerConstants.ZEAH, false, true),
		LUMBRIDGE(240117, "None", "None", "None", 0, PlayerConstants.LUMBRIDGE, false, false),
		DRAYNOR(240118, "None", "None", "None", 0, PlayerConstants.DRAYNOR, false, false),
		REMMINGTON(240119, "None", "None", "None", 0, PlayerConstants.REMMINGTON, false, false),
		YANILLE(240120, "None", "None", "None", 0, PlayerConstants.YANILLE, false, false),
		TALVERY(240121, "None", "None", "None", 0, PlayerConstants.TALVERY, false, false),
		ARDOUNGE(240122, "None", "None", "None", 0, PlayerConstants.ARDOUNGE, false, false),
		KARAMJA(240123, "None", "None", "None", 0, PlayerConstants.KARAMJA, false, false);

		private int button;
		private String description;
		private String requirement;
		private String other;
		private int level;
		private Location position;
		private boolean instanced;
		private boolean specialCase;

		private TeleportationData(int button, String description, String requirement, String other, int level, Location position, boolean instanced, boolean specialCase) {
			this.button = button;
			this.description = description;
			this.requirement = requirement;
			this.other = other;
			this.level = level;
			this.position = position;
			this.instanced = instanced;
			this.specialCase = specialCase;
		}

		public int getButton() {
			return button;
		}

		public String getDescription() {
			return description;
		}

		public String getRequirement() {
			return requirement;
		}

		public String getOther() {
			return other;
		}
		
		public int getLevel() {
			return level;
		}

		public Location getPosition() {
			return position;
		}
		
		public boolean isInstanced() {
			return instanced;
		}
		
		public boolean getSpecialCase() {
			return specialCase;
		}

		public static HashMap<Integer, TeleportationData> teleportation = new HashMap<Integer, TeleportationData>();

		static {
			for (final TeleportationData teleportation : TeleportationData.values()) {
				TeleportationData.teleportation.put(teleportation.button, teleportation);
			}
		}
	}

	/**
	 * Handles player teleporting
	 * @param player
	 */
	public static void teleport(Player player) {
		int buttonId = player.getTeleportTo();

		TeleportationData teleportation = TeleportationData.teleportation.get(buttonId);

		if (teleportation == null) {
			return;
		}
		
		if (teleportation.getSpecialCase()) {
			if (!specialCase(player, teleportation.getButton())) {
				return;
			}
		}
		
		/*if (!player.isCreditUnlocked(CreditPurchase.FREE_TELEPORTS)) {
			if (!player.payment(teleportation.getDescription())) {
				if (teleportation.getDescription() != 0) {
					return;					
				}
			}
			player.send(new SendMessage(teleportation.getCost() == 0 ? "You paid @red@nothing</col> and were teleported!" : "You have paid @red@" + Utility.format(teleportation.getCost()) + "</col> coins and were teleported!"));				
		} else {
			player.send(new SendMessage("@red@You have been teleported for free."));
		}*/
		
		
		int height = teleportation.isInstanced() ? player.getIndex() << 2 : teleportation.getPosition().getZ();
		
		player.getMagic().teleport(teleportation.getPosition().getX(), teleportation.getPosition().getY(), height, TeleportTypes.SPELL_BOOK);
		
		player.setTeleportTo(0);
	}
	
	/**
	 * Handles special case teleporting
	 * @param player
	 * @param buttonId
	 */
	public static boolean specialCase(Player player, int buttonId) {
		switch(buttonId) {
		
		case 254033://Clan Wars
			player.send(new SendMessage("@dre@Walk west to enter the FFA arena."));
			return true;
		
		case 250053://Barrelchest
			TaskQueue.queue(new Task(player, 4, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					new Mob(player, 6342, true, false, false, new Location(2638, 9115, player.getIndex() << 2));
					stop();
				}
				@Override
				public void onStop() {
				}
			});
			return true;
			
		case 242108:
			player.send(new SendMessage("@dre@You have teleported to Puro puro. You'll need a butterfly net and impling jars."));
			return true;
			
		case 242107://Farming
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Catherby", p -> {
				p.teleport(new Location(2805, 3464, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Catherby farming area."));
			}, "Ardougne", p -> {
				p.teleport(new Location(2662, 3375, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Ardougne farming area."));
			}, "Falador", p -> {
				p.teleport(new Location(3056, 3310, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Falador farming area."));
			}, "Phasmatys", p -> {
				p.teleport(new Location(3600, 3524, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Phasmatys farming area."));
			}));
			}
			return false;
			
		case 242102://Agility
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Gnome agility course", p -> {
				p.teleport(new Location(2480, 3437, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Gnome agility course."));
			}, "Barbarian agility course", p -> {
				p.teleport(new Location(2546, 3551, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Barbarian agility course."));
			}, "Wilderness agility course (50+ wild)", p -> {
				p.teleport(new Location(2998, 3932, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Wilderness agility course."));
			}, "Rooftop courses", p -> {	
				player.start(new OptionDialogue("Seer's course", p2 -> {
					p.teleport(new Location(2729, 3488, 0));
					p.send(new SendMessage("<col=482CB8>You have teleported to the Seer's Village rooftop agility course."));
				}, "Ardougne course", p2 -> {
					p.teleport(new Location(2674, 3297, 0));
					p.send(new SendMessage("<col=482CB8>You have teleported to the Ardougne rooftop agility course."));
				}));		
			}));}
			return false;
			
		case 242103://Mining
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Falador Dwarven Mine", p -> {
				p.teleport(new Location(3044, 9785, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Dwarven mining area."));
			}, "Lovakite Mine", p -> {
				p.teleport(new Location(1437, 3823, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Lovakite mining area."));
			}, "Shilo Village Gem Mine", p -> {
				p.teleport(new Location(2825, 2999, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Shilo Village Gem mining area."));
			}, "Essence Mine", p -> {
				p.teleport(new Location(2925, 4819, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Essence Mining area."));
			}, "Motherload Mine", p -> {
				p.teleport(new Location(3728, 5692, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Motherload Mining area."));
			}));
			}
			return false;

		case 238134://Crystal Monsters
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Crystal Dragons", p -> {
				p.teleport(new Location(2979, 3949, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Crystal Dragons area."));
			}, "Crystal Demons", p -> {
				p.teleport(new Location(3288, 3886, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Crystal Demons area."));
			}, "Crystal Avansies", p -> {
				p.teleport(new Location(3315, 3839, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Crystal Avansies area."));
			}, "@red@Coming soon!", p -> {
				//p.teleport(new Location(2925, 4819, 0));
				p.send(new SendMessage("<col=482CB8>This Monster will be coming soon..*hint* Crystal Abyssal Demons"));
			}));
			}
			return false;
		case 250073://skeleton warriors
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Ancient Samurai", p -> {
				p.teleport(new Location(3037, 3674, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Ancient Samurai."));
			}, "Ancient Archer", p -> {
				p.teleport(new Location(3353, 3804, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Ancient Archer."));
			}, "Ancient Warlock", p -> {
				p.teleport(new Location(2980, 3763, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Ancient Warlock."));
			}
			));
			}
			return false;
			//culinaromancer, agrith-na-na, flambeed, karamel, dessourt
		case 250072://RFD
			if (!player.inWilderness()) {
			player.start(new OptionDialogue("Culinaromancer", p -> {
				p.teleport(new Location(2792, 9328, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to the Culinaromancer."));
			}, "Flambeed", p -> {
				p.teleport(new Location(1898, 5349, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to Flambeed."));
			}, "Agrith-Na-Na", p -> {
				p.teleport(new Location(1555, 3596, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to Agrith-Na-Na."));
			}, "Dessourt", p -> {
				p.teleport(new Location(2582, 9447, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to Dessourt."));
			},"Karamel", p -> {
				p.teleport(new Location(2609, 9485, 0));
				p.send(new SendMessage("<col=482CB8>You have teleported to Karamel."));
			}));
			}
			return false;
			
		case 250058://Kraken
			TaskQueue.queue(new Task(5) {
				@Override
				public void execute() {
					int[][] DATA = { { 493, 3691, 5810 }, { 493, 3691, 5814 }, { 493, 3700, 5810 }, { 493, 3700, 5814 }, { 496, 3694, 5811 } };
					for (int i = 0; i < DATA.length; i++) {
						Mob mob = new Mob(player, DATA[i][0], false, false, false, new Location(DATA[i][1], DATA[i][2], player.getZ()));		
						mob.setCanAttack(false);
						player.face(mob);
					}
					stop();
				}
	
				@Override
				public void onStop() {
					player.whirlpoolsHit = 0;
					player.send(new SendMessage("Welcome to Kraken's cave."));
				}
			});
			return true;
			
		case 250057://Zulrah
			TaskQueue.queue(new Task(5) {
				@Override
				public void execute() {
					Zulrah mob = new Zulrah(player, new Location(2266, 3073, player.getIndex() << 2));
					mob.face(player);
					mob.getUpdateFlags().sendAnimation(new Animation(5071));
					player.face(mob);
					player.send(new SendMessage("Welcome to Zulrah's shrine."));
					DialogueManager.sendStatement(player, "Welcome to Zulrah's shrine.");
					stop();
				}

				@Override
				public void onStop() {
				}
			});
			return true;
			
		case 250067://Cerb
			TaskQueue.queue(new Task(5) {
			@Override
			public void execute() {
			Cerberus mob = new Cerberus(player, new Location(1241, 1254, player.getIndex() << 2));
			mob.face(player);
			player.face(mob);
				player.send(new SendMessage("Welcome to Cerberus"));
				DialogueManager.sendStatement(player, "Welcome to Cerberus");
				stop();
			}
			
			@Override
			public void onStop() {
			}
		});
		return true;
		
		case 250071://Gelatin
			TaskQueue.queue(new Task(5) {
			@Override
			public void execute() {
			GelatinnothMother mob = new GelatinnothMother(player, new Location(3751, 5775, player.getIndex() << 2));
			mob.face(player);
			player.face(mob);
				player.send(new SendMessage("Welcome to the Gelatinnoth Mother. She has 6 phases!"));
				DialogueManager.sendStatement(player, "Welcome to Gelatinnoth Mother. She has 6 Phases! Are you lucky?");
				stop();
			}
			
			@Override
			public void onStop() {
			}
		});
		return true;
			
		case 250068://Thermo
			TaskQueue.queue(new Task(player, 4, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
				new Mob(player, 499, true, false, false, new Location(3663, 5771, player.getIndex() << 2));
				player.send(new SendMessage("Welcome to Thermonuclear Smoke Devils"));
				DialogueManager.sendStatement(player, "Welcome to Thermonuclear Smoke Devils");
				stop();
			}
			@Override
			public void onStop() {
			}
		});
		return true;
		
		case 250075://Mage only boss
			TaskQueue.queue(new Task(player, 4, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
				new Mob(player, 3474, true, false, false, new Location(2272, 4686, player.getIndex() << 2));
				player.send(new SendMessage("Welcome to The Everlasting! The magic ONLY boss!"));
				DialogueManager.sendStatement(player, "Welcome to The Everlasting! The magic ONLY boss!");
				stop();
			}
			@Override
			public void onStop() {
			}
		});
		return true;
		
		case 250069://Shaman
			TaskQueue.queue(new Task(5) {
			@Override
			public void execute() {
			Shaman mob = new Shaman(player, new Location(1424, 3703, player.getIndex() << 2), player);
			mob.face(player);
			player.face(mob);
				player.send(new SendMessage("Welcome to Shamans Canyon"));
				DialogueManager.sendStatement(player, "Welcome to Shamans Canyon");
				stop();
			}
			
			@Override
			public void onStop() {
			}
		});
		return true;
			
		case 240111://Membership
			if (PlayerConstants.isPlayer(player)) {
				if (player.getMoneySpent() < 5) {
				DialogueManager.sendNpcChat(player, 7042, Emotion.DEFAULT, "You need to be a <img=4>@red@Donator </col>to do this!");
				return false;
			}
			}
			return true;
			
						
		
		case 121:
			
		
		case 240112://Lumbridge
			return true;
		case 240113://Varrock Center
			return true;
		case 240114://Falador
			return true;
		case 240115://Camelot
			return true;
		case 240116://Ardounge
			return true;
		case 240117://Yanille
			return true;
		}
		return false;
	
	}

	/**
	 * Handles selecting teleport
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean selection(Player player, int buttonId) {
		TeleportationData teleportation = TeleportationData.teleportation.get(buttonId);

		if (teleportation == null) {
			return false;
		}

		player.setTeleportTo(buttonId);
		player.send(new SendString("Level: @red@0", 64039));
		switch (player.getInterfaceManager().getMain()) {
		case 61000:// Training
			player.send(new SendString("Selected: @red@" + Utility.formatPlayerName(teleportation.name().replaceAll("_", " ").toLowerCase()), 61031));
			player.send(new SendString("Description: @red@" + teleportation.getDescription(), 61032));
			player.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 61033));
			player.send(new SendString("Other: @red@" + teleportation.getOther(), 61034));
			break;
		case 62000:// Skilling
			player.send(new SendString("Selected: @red@" + Utility.formatPlayerName(teleportation.name().replaceAll("_", " ").toLowerCase()), 62031));
			player.send(new SendString("Description: @red@" + teleportation.getDescription(), 62032));
			player.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 62033));
			player.send(new SendString("Other: @red@" + teleportation.getOther(), 62034));
			break;
		case 63000:// Player Vs Player
			player.send(new SendString("Selected: @red@" + Utility.formatPlayerName(teleportation.name().replaceAll("_", " ").toLowerCase()), 63031));
			player.send(new SendString("Description: @red@" + teleportation.getDescription(), 63032));
			player.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 63033));
			player.send(new SendString("Wildy Level: @red@" + teleportation.getOther(), 63034));
			break;
		case 64000:// Player Vs Monster
			player.send(new SendString("Selected: @red@" + Utility.formatPlayerName(teleportation.name().replaceAll("_", " ").toLowerCase()), 64031));
			player.send(new SendString("Description: @red@" + teleportation.getDescription(), 64032));
			player.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 64033));
			player.send(new SendString("Other: @red@" + teleportation.getOther(), 64034));
			player.send(new SendString("Level: @red@" + teleportation.getLevel(), 64039));
			break;
		case 65000:// Minigame
			player.send(new SendString("Selected: @red@" + Utility.formatPlayerName(teleportation.name().replaceAll("_", " ").toLowerCase()), 65031));
			player.send(new SendString("Description: @red@" + teleportation.getDescription(), 65032));
			player.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 65033));
			player.send(new SendString("Other: @red@" + teleportation.getOther(), 65034));
			break;
		case 61500:// Other
			player.send(new SendString("Selected: @red@" + Utility.formatPlayerName(teleportation.name().replaceAll("_", " ").toLowerCase()), 61531));
			player.send(new SendString("Description: @red@" + teleportation.getDescription(), 61532));
			player.send(new SendString("Requirement: @red@" + teleportation.getRequirement(), 61533));
			player.send(new SendString("Other: @red@" + teleportation.getOther(), 61534));
			break;
		}
		return true;
	}
	
	/*public static String format(long num) {
		if (num == 0) {
			return "Free";
		}
		return NumberFormat.getInstance().format(num);
}*/
	
}
