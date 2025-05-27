package com.mayhem.rs2.content.pets;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles boss pets
 * 
 * @author Daniel
 */
public class BossPets {

	/**
	 * Boss Pet data
	 * @author Daniel
	 *
	 */
	public enum PetData {
		
		//TODO:
		//PENANCE QUEEN
		//CHOMPY CHICK
		GIANT_SQURRIEL(20659, 7334),
		MINI_AHRIM(14012, 2328),
		MINI_GUTHAN(14016, 2329),
		MINI_VERAC(14018, 2332),
		MINI_KARIL(14020, 2330),
		MINI_TORAG(14022, 2331),
		MINI_DHAROK(14014, 2327),
		TEKTON(14010, 2325),
		VESPULA(14006, 2323),
		SLAYERS_PET(433, 2323),
		BLOOD_HOUND(19730, 7232),
		DARK_BEAST(16504, 1234),
		SKOTIZO(17600, 7264),
		WARLOCK(15040, 5000),
		ARCHER(15041, 5001),
		SAMURAI(15042, 5002),
		WHITE_GELATIN(18400, 2336),
		ORANGE_GELATIN(18401, 2341),
		BLUE_GELATIN(18402,  2337),
		RED_GELATIN(18403, 2338),
		GREEN_GELATIN(18404, 2339),
		BROWN_GELATIN(18405, 2340),
		DEMONIC_GORILLA(18406, 2342),
		LAZY_JUBBLY(16500, 4864),
		FLAMBEED(18333, 76),
		KARAMEL(18834, 2333),
		AGRITH_NA_NA(18835, 2334),
		COLUNOMANCER(18837, 4876),
		DESSOURT(18836, 2335),
		JUBBLY(16501, 4863),
		OLMLET(20851, 7519),
		LOVE_CATS(1554, 2644),
		KITTEN1(1555, 5591),
		KITTEN2(1556, 5592),
		KITTEN3(1557, 5593),
		KITTEN4(1558, 5594),
		KITTEN5(1559, 5595),
		KITTEN6(1560, 5596),
		HELLKITTEN(7583, 5597),
		CAT1(1561, 1619),
		CAT2(1562, 1620),
		CAT3(1563, 1621),
		CAT4(1564, 1622),
		CAT5(1565, 1623),
		CAT6(1566, 1624),
		HELLCAT(7582, 1625),
		OVERGROWN_CAT1(1567, 5598),
		OVERGROWN_CAT2(1568, 5599),
		OVERGROWN_CAT3(1569, 5600),
		OVERGROWN_CAT4(1570, 5601),
		OVERGROWN_CAT5(1571, 5602),
		OVERGROWN_CAT6(1572, 5603),
		OVERGROWN_HELLCAT(7581, 5604),
		ORPHAN(13262, 5884),
		KALPHITE_PRINCESS_FLY(12654, 6637), 
		KALPHITE_PRINCESS_BUG(12647, 6638),
		SMOKE_DEVIL(12648, 6655),
		DARK_CORE(12816, 318),
		PRINCE_BLACK_DRAGON(12653, 4000),
		GREEN_SNAKELING(12921, 2130),
		RED_SNAKELING(12939, 2131),
		BLUE_SNAKELING(12940, 2132),
		CHAOS_ELEMENT(11995, 5907),
		KREE_ARRA(12649, 4003),
		CALLISTO(13178, 497),
		SCORPIAS_OFFSPRING(13181, 5547),
		VENENATIS(13177, 495),		
		VETION_PURPLE(13179, 5559),
		VETION_ORANGE(13180, 5560),
		BABY_MOLE(12646, 6635),
		KRAKEN(12655, 6640),
		DAGANNOTH_SUPRIME(12643, 4006),
		DAGANNOTH_RIME(12644, 4007),
		DAGANNOTH_REX(12645, 4008),
		GENERAL_GRAARDOR(12650, 4001), 
		COMMANDER_ZILYANA(12651, 4009),
		KRIL_TSUTSAROTH(12652, 4004),
		HERON(13320, 6715),
		BEAVER(13322, 6717), 
		BABY_RED_CHIN(13323, 6718),
		BABY_GREY_CHIN(13324, 6719),
		BABY_BLACK_CHIN(13325, 6720),
		BABY_YELLOW_CHIN(13326, 6721),
		JAD_PET(13225, 5892),
		ROCK_GOLEM(13321, 6716),
		HELL_PUPPY(13247, 3099),
		SHADOW_HOUND(7916, 3449),
		INADEQUACY(4520, 3473),
		EVIL_CREATURE(10115, 1256),
		CUTE_CREATURE(10117, 1257),
		QUEEN_SPAWN(1, 5576), //TODO
		PENANCE_QUEEN(1, 5575), //TODO
		UNDEAD_OVERLORD(8131, 4496), //undead overlord boss pet
		SPIRITUAL_RANGER(6640, 3160),//plague
		SWAMP_LIZARD(10149, 2906), //hunter
		ORANGE_SALAMANDER(10146, 2903), //hunter
		RED_SALAMANDER(10147, 2904), //hunter
		BLACK_SALAMANDER(10148, 2905), //hunter
		SPIKED_KEBBIT(9957, 1346), //hunter TODO OTHER KEBBITS!!!!
		FALCON(10023, 1342), //hunter
		INFERNAL_MAGE(4140, 446), //members boss pet
		RACCOON(20663, 1419), //thieving
		GOLDFISH(272, 4823), //fishing
		PYREFIEND(4138, 436), //firemaking
		MONKEY_CHILD(4033, 5268), //cooking
		BROOM(4179, 3845), //fletching
		CHOMPY(13071, 1475), //herblore
		TOOL_LEPRECHAUN(12359, 0), //farming
		PHOENIX(20693, 7368), //smithing
		DWARF(7500, 296), //mining
		TREE_SPIRIT(771, 1163), //woodcutting
		SPIRIT(964, 5341), //prayer
		ABYSSAL_LEECH(7986, 2583), //runecrafting
		TOY_MOUSE(7769, 2781), //crafting
		CLOCKWORK_CAT(7771, 2782), //crafting
		TOY_SOLDIER(7761, 2779), //crafting
		TOY_DOLL(7765, 2780), //crafting
		TOY_PENGUIN(5565, 846), //STARTER
		HELL_CAT(7582, 1625), //DONOR SHOP 
		HELL_KITTEN(7583, 5597), //FREE SHOP 
		//VERAC_JR(15570, 4177), 
		RABBIT(9975, 1853), //FREE
		HELL_RAT(11047, 1062), //DONOR
		HELL_GOBLIN(10998, 1065), //DONOR
		MUTANT(10731, 1067), //DONOR
		HAM_ZANIK(8888, 4326), //DONOR
		ZANIK(8887, 4324), //DONOR
		SOULLESS_FOLLOWER(12840, 1072), //DONOR
		BLACK_DOG(8132, 4228), //FREE
		SHEPARD_DOG(7914, 111), //DONOR
		SHARK(7993, 1830), //FREE
		BLACK_SWAN(5076, 1832), //DONOR
		WHITE_SWAN(5077, 1831), //FREE
		//SHEEP_DOG(8132, 2817),
		ROCK_CRAB(1855, 102),
		BABY_RED_DRAGON(8134, 244),
		GHOST(553, 3627), //FREE 
		IMP(9952, 5728), //DONOR
		BUTTERFLY(9970, 1854); //DONOR
		//SPIRITUAL_WARRIOR(6640, 6219), 
		//SPIRITUAL_RANGER(6642, 6220), 
		//SPIRITUAL_MAGE(6644, 6221),

		private final int itemID;
		private final int npcID;

		private PetData(int itemID, int npcID) {
			this.itemID = itemID;
			this.npcID = npcID;
		}

		public int getItem() {
			return itemID;
		}

		public int getNPC() {
			return getNpcID();
		}

		public static PetData forItem(int id) {
			for (PetData data : PetData.values())
				if (data.itemID == id)
					return data;
			return null;
		}

		public static PetData forNPC(int id) {
			for (PetData data : PetData.values())
				if (data.getNpcID() == id)
					return data;
			return null;
		}

		public int getNpcID() {
			return npcID;
		}
	}

	/**
	 * Handles spawning the pet
	 * @param player
	 * @param itemID
	 */
	public static boolean spawnPet(Player player, int itemID, boolean loot) {
		PetData data = PetData.forItem(itemID);

		if (data == null) {
			return false;
		}

		if (player.getBossPet() != null) {
			player.send(new SendMessage("You already have a pet summoned!"));
			return true;
		}

		player.getInventory().remove(new Item(itemID, 1));

		final Mob mob = new Mob(player, data.getNpcID(), false, false, true, player.getLocation());
		mob.getFollowing().setIgnoreDistance(true);
		mob.getFollowing().setFollow(player);

		player.setBossPet(mob);
		player.setBossID(data.getNpcID());
		player.getUpdateFlags().sendAnimation(new Animation(827));
		player.face(player.getBossPet());
		

		if (loot) {
			AchievementHandler.activate(player, AchievementList.OBTAIN_1_BOSS_PET, 1);
			AchievementHandler.activate(player, AchievementList.OBTAIN_10_BOSS_PET, 1);
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a Pet while bossing!");
		} else {
			player.send(new SendMessage("You summoned your " + mob.getDefinition().getName() + "."));
		}
		return true;
	}

	/**
	 * Handles picking up the pet
	 * @param player
	 * @param npcID
	 * @return
	 */
	public static boolean pickupPet(Player player, Mob mob) {
		if (mob == null || World.getNpcs()[mob.getIndex()] == null) {
			return false;
		}
		PetData data = PetData.forNPC(mob.getId());

		if (data == null) {
			return false;
		}
		
		if (player.getBossPet() == null || player.getBossPet().isDead()) {
			return false;
		}
		
		if (player.getBossPet() != mob || mob.getOwner() != player) {
			DialogueManager.sendStatement(player, "This is not your pet!");
			return true;
		}
		
		if (player.getInventory().hasSpaceFor(new Item(data.getItem(), 1))) {
			player.getInventory().add(new Item(data.getItem(), 1));
		} else if (player.getBank().hasSpaceFor((new Item(data.getItem(), 1)))) {
			player.getBank().add((new Item(data.getItem(), 1)));
			player.getClient().queueOutgoingPacket(new SendMessage("Your pet has been added to your bank."));
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("You must free some inventory space to pick up your pet."));
			return false;
		}
		
		player.getUpdateFlags().sendAnimation(new Animation(827));
		player.face(player.getBossPet());
		
		TaskQueue.queue(new Task(player, 1, true) {
			@Override
			public void execute() {
				player.getBossPet().remove();
				player.setBossPet(null);
				stop();
			}

			@Override
			public void onStop() {
				player.send(new SendMessage("You have picked up your pet."));
			}
		});		
		
		return true;
	}
	
	/**
	 * Handles pets on logout
	 * @param player
	 * @return
	 */
	public static void onLogout(Player player) {
		if (player.getBossPet() != null) {
			
			PetData data = PetData.forNPC(player.getBossPet().getId());
			
			if (player.getInventory().hasSpaceFor(new Item(data.getItem(), 1))) {
				player.getInventory().add(new Item(data.getItem(), 1));
			} else if (player.getBank().hasSpaceFor(new Item(data.getItem(), 1))) {
				player.getBank().add(new Item(data.getItem(), 1));
			}
		}
	}
	
	/**
	 * Handles what happens on death
	 * @param player
	 */
	public static void onDeath(Player player) {
		if (player.getBossPet() != null) {
			player.getBossPet().remove();
			player.setBossPet(null);
			player.send(new SendMessage("You have died with your pet, losing it forever."));
		}
	}
}