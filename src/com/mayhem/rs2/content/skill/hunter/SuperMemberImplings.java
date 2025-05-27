package com.mayhem.rs2.content.skill.hunter;

import java.util.HashMap;
import java.util.Random;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Manages the functionality of parts of the hunter skill
 * 
 * @author Valiant - http://www.rune-server.org/members/valiant
 * @since April 19th, 2015
 * 
 */

public class SuperMemberImplings {

	/**
	 * The Random Modifier
	 */
	public static final Random random = new Random();

	/** Impling Loot table data */
	public enum ImplingRewards {

		BABY(11238, new int[][] { { 1755, 1 }, { 1734, 1 }, { 1733, 1 }, { 946, 1 }, { 1985, 1 }, { 2347, 1 }, { 1759, 1 }, { 1927, 1 }, { 319, 1 }, { 2007, 1 }, { 1779, 1 }, { 7170, 1 }, { 1438, 1 }, { 2355, 1 }, { 1607, 1 }, { 1743, 1 }, { 379, 1 }, { 1761, 1 } }),

		YOUNG(11240, new int[][] { { 361, 1 }, { 1902, 1 }, { 1539, 5 }, { 1524, 1 }, { 7936, 1 }, { 855, 1 }, { 1353, 1 }, { 2293, 1 }, { 7178, 1 }, { 247, 1 }, { 453, 1 }, { 1777, 1 }, { 231, 1 }, { 1761, 1 }, { 8778, 1 }, { 133, 1 }, { 2359, 1 } }),

		GOURMENT(11242, new int[][] { { 365, 1 }, { 361, 1 }, {13307, 100, 250}, { 2011, 1 }, { 1897, 1 }, { 2327, 1 }, { 2007, 1 }, { 5970, 1 }, { 380, 4 }, { 7179, 1, 5 }, { 386, 3 }, { 1883, 1 }, { 3145, 2 }, { 5755, 1 }, { 5406, 1 }, { 10137, 5 } }),

		EARTH(11244, new int[][] { { 6033, 6 }, { 1440, 1 }, {13307, 100, 250}, { 5535, 1 }, { 557, 32 }, { 1442, 1 }, { 1784, 4 }, { 1273, 1 }, { 447, 1 }, { 1606, 2 } }),

		ESSENCE(11246, new int[][] { { 7936, 20 }, { 555, 30 }, {13307, 100, 250}, { 556, 30 }, { 558, 25 }, { 559, 28 }, { 562, 4 }, { 1448, 1 }, { 564, 4 }, { 563, 13 }, { 565, 7 }, { 566, 11 } }),

		ECLECTIC(11248, new int[][] { { 1273, 1 }, { 5970, 1 }, {13307, 100, 250}, { 231, 1 }, { 556, 30, 47 }, { 8779, 4 }, { 1199, 1 }, { 4527, 1 }, { 444, 1 }, { 2358, 5 }, { 7937, 20, 35 }, { 237, 1 }, { 2493, 1 }, { 10083, 1 }, { 1213, 1 }, { 450, 10 }, { 5760, 2 }, { 7208, 1 }, { 5321, 3 }, { 1391, 1 }, { 1601, 1 } }),
		NATURE(11250, new int[][] { { 5100, 1 }, { 5104, 1 }, {13307, 100, 250}, { 5281, 1 }, { 5294, 1 }, { 6016, 1 }, { 1513, 1 }, { 254, 4 }, { 5313, 1 }, { 5286, 1 }, { 5285, 1 }, { 3000, 1 }, { 5974, 1 }, { 5297, 1 }, { 5299, 1 }, { 5298, 5 }, { 5304, 1 }, { 5295, 1 }, { 270, 2 }, { 5303, 1 } }),
		MAGPIE(11252, new int[][] { { 1701, 3 }, { 1732, 3 }, {13307, 100, 250}, { 2569, 3 }, { 3391, 1 }, { 4097, 1 }, { 5541, 1 }, { 1747, 6 }, { 1347, 1 }, { 2571, 4 }, { 4095, 1 }, { 2364, 2 }, { 1215, 1 }, { 1185, 1 }, { 1602, 4 }, { 5287, 1 }, { 987, 1 }, { 985, 1 }, { 993, 1 }, { 5300, 1 } }),
		NINJA(11254, new int[][] { { 4097, 1 }, { 3385, 1 }, {13307, 100, 250}, { 892, 70 }, { 140, 4 }, { 1748, 10, 16 }, { 1113, 1 }, { 1215, 1 }, { 1333, 1 }, { 1347, 1 }, { 9342, 2 }, { 5938, 4 }, { 6156, 3 }, { 9194, 4 }, { 6313, 1 }, { 805, 50 } }),
		DRAGON(11256, new int[][] { { 11212, 100, 500 }, {13307, 100, 250}, { 9341, 3, 40 }, { 1305, 1 }, { 11232, 105, 350 }, { 11237, 100, 500 }, { 9193, 10, 49 }, { 535, 111, 297 }, { 1216, 3 }, {12789, 1}, { 11230, 105, 350 }, { 5316, 1 }, { 537, 52, 99 }, { 1616, 3, 6 }, { 1705, 2, 4 }, { 5300, 6 }, { 7219, 5, 15 }, { 4093, 1 }, { 5547, 1 }, { 1701, 2, 4 } }),
		LUCKY(19732, new int[][] { {5698, 1}, {989, 1}, {11934, 10}, {537, 5}, {249, 1}, {251, 1}, {253, 1}, {255, 1}, {12789, 1}, {1213, 1}, {1163, 1}, {257, 1}, {259, 1}, {261, 1}, {13307, 100, 350}, {861, 1 }, {859, 1}, {360, 25, 100}, {372, 25, 100}, {384, 25, 80}, {1514, 25, 75}, {3204, 1}, {995, 10000, 75000}, {989, 1}, {12789, 1}, {10476, 5, 25}, {1213, 1}, });

		public static HashMap<Integer, ImplingRewards> impReward = new HashMap<>();

		static {
			for (ImplingRewards t : ImplingRewards.values()) {
				impReward.put(t.itemId, t);
			}
		}

		private int itemId;
		private int[][] rewards;
		

		ImplingRewards(int itemId, int[][] rewards) {
			this.itemId = itemId;
			this.rewards = rewards;
		}

		public int[][] getLoot() {
			return rewards;
		}

		/**
		 * The looing of an impling jar
		 * 
		 * @param player
		 *            the player opening the jar
		 * @param itemId
		 *            the itemId of loot recieved
		 */
		public static void lootImpling(Player player, int itemId) {
			if (!player.getInventory().hasSpaceFor(new Item(itemId))) {
				player.getClient().queueOutgoingPacket(new SendMessage("You don't have enough room to loot the impling."));
				return;
			}
			ImplingRewards t = ImplingRewards.impReward.get(itemId);
			player.getInventory().remove(new Item(itemId));
			player.getInventory().getItemSlot(itemId);
			int r = random.nextInt(t.getLoot().length);
			if (t.getLoot()[r].length == 3) {
				int amount = t.getLoot()[r][1] + random.nextInt(t.getLoot()[r][2] - t.getLoot()[r][1]);
				player.getInventory().add(new Item(t.getLoot()[r][0], amount));
			} else {
				player.getInventory().add(new Item(t.getLoot()[r][0], t.getLoot()[r][1]));
			}
			if (Utility.randomNumber(15) == 0) {
				player.getClient().queueOutgoingPacket(new SendMessage("The jar breaks as you open it."));
			} else {
				player.getInventory().add(new Item(11260));
				player.getClient().queueOutgoingPacket(new SendMessage("You loot the impling jar."));
			}
		}

		/** Catachable Impling data */
		public enum SuperImplings {

			BABY_IMPLING(1635, 1, 25, 11238),
			YOUNG_IMPLING(1636, 22, 65, 11240),
			GOURMET_IMPLING(1637, 28, 113, 11242),
			EARTH_IMPLING(1638, 36, 177, 11244),
			ESSENCE_IMPLING(1639, 42, 255, 11246),
			ECLECTIC_IMPLING(1640, 50, 289, 11248),
			NATURE_IMPLING(1641, 58, 353, 11250),
			MAGPIE_IMPLING(1642, 65, 409, 11252),
			NINJA_IMPLING(1643, 74, 481, 11254),
			DRAGON_IMPLING(1644, 83, 553, 11256),
			LUCKY_IMPLING(7233, 90, 604, 19732);

			public static HashMap<Integer, SuperImplings> superimplings = new HashMap<>();

			static {
				for (SuperImplings t : SuperImplings.values()) {
					superimplings.put(t.npc, t);
				}
			}

			private int npc;
			private int levelRequired;
			private int experience;
			private int itemId;

			SuperImplings(final int npc, final int levelRequired, final int experience, final int itemId) {
				this.npc = npc;
				this.levelRequired = levelRequired;
				this.experience = experience;
				this.itemId = itemId;
			}

			public int getImpId() {
				return npc;
			}

			public int getLevelRequired() {
				return levelRequired;
			}

			public int getXp() {
				return experience;
			}

			public int getJar() {
				return itemId;
			}

			public static SuperImplings forId(int id) {
				return superimplings.get(id);
			}

			/**
			 * The catching of implings around the game
			 * 
			 * @param player
			 *            the player catching the impling
			 * @param impling
			 *            the impling mob being caught
			 */
			public static void catchImp(Player player, Mob impling) {
				if (player.getEquipment().getItems()[3] == null) {
					return;
				}
				SuperImplings t = SuperImplings.superimplings.get(impling.getId());

				if (!player.getInventory().hasSpaceFor(new Item(t.getJar()))) {
					player.getClient().queueOutgoingPacket(new SendMessage("You don't have enough room to do this."));
					return;
				}

				if (player.getEquipment().getItems()[3].getId() != 10010) {
				if (player.getEquipment().getItems()[3].getId() != 11259) {
					player.getClient().queueOutgoingPacket(new SendMessage("You need a butterfly net to catch this impling."));
					return;
				}
				}

				if (player.getSkill().getLevels()[22] < t.getLevelRequired()) {
					player.getClient().queueOutgoingPacket(new SendMessage("You need a hunter level of " + t.getLevelRequired() + "to catch this impling."));
					return;
				}

				if (!player.getInventory().hasItemId(new Item(11260))) {
					player.getClient().queueOutgoingPacket(new SendMessage("You need an impling jar to catch and store the impling."));
				}
				if (Utility.randomNumber(25) == 0) {
					player.getUpdateFlags().sendAnimation(new Animation(2067));
					player.getClient().queueOutgoingPacket(new SendMessage("You fail to catch the impling."));
				} else {
					player.getUpdateFlags().sendAnimation(new Animation(2067));
					player.getClient().queueOutgoingPacket(new SendMessage("You catch the impling. Puro Points [" + player.puroPoints + "]. Skill Points [" + player.skillPoints + "]."));
					handleBloodMoney(player);
					player.getInventory().remove(11260, 1);
					player.getInventory().add(t.getJar(), 1);
					handlePetGreyChin(player);
					handlePetRedChin(player);
					handlePetBlackChin(player);
					handlePetYellowChin(player);
					player.getSkill().addExperience(22, t.getXp());
					player.skillPoints += 50;
					player.puroPoints += 1;
					teleportSuperImpling(impling);
				}
			}
		}
	}

	/**
	 * Teleports a caught impling to another location
	 * 
	 * @param impling
	 *            the impling being teleported
	 */
	private static void teleportSuperImpling(Mob impling) {
		int location[][] = { {2104, 3911}, {2105, 3914}, {2098, 3913}, {2094, 3915}, {2093, 3912}, {2089, 3915}, {2088, 3912},
				  {2085, 3916}, {2083, 3914}, {2084, 3911}, {2086, 3914}, {2112, 3914}, {2107, 3919}, };
		int random_location = Utility.randomNumber(location.length - 1);
		impling.teleport(new Location(location[random_location][0], location[random_location][1]));
	}

	/**
	 * Manages the spawning of implings upon server startup
	 * 
	 * @param impling
	 *            the impling being created
	 */
	public static void appendSuperImpling() {
		int implings[] = { 1644, 1644, };
		int location[][] = { {2104, 3911}, {2105, 3914}, {2098, 3913}, {2094, 3915}, {2093, 3912}, {2089, 3915}, {2088, 3912},
							  {2085, 3916}, {2083, 3914}, {2084, 3911}, {2086, 3914}, {2112, 3914}, {2107, 3919}, };
		for (int i = 0; i < 5; i++) {
			int npc = Utility.randomNumber(implings.length - 1);
			int random_location = Utility.randomNumber(location.length - 1);
			new Mob(implings[npc], true, new Location(location[random_location][0], location[random_location][1]));
			
		}
	}
	
	public static void handleBloodMoney(Player player) {
		if (player.getInventory().hasSpaceFor(new Item(13307))) {
			player.getInventory().add(new Item(13307, Utility.randomNumber(30)));
		} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
			player.getBank().add((new Item(13307, Utility.randomNumber(30))));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood money! It has been sent to your bank."));
		}
			}
	public static void handlePetGreyChin(Player player) {
		int random = Utility.randomNumber(4000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(13324))) {
				player.getInventory().add(new Item(13324));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa!"));
			} else if (player.getBank().hasSpaceFor((new Item(13324)))) {
				player.getBank().add((new Item(13324)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Baby chinchompa while hunting!");
		}
			}
	public static void handlePetRedChin(Player player) {
		int random = Utility.randomNumber(4000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(13323))) {
				player.getInventory().add(new Item(13323));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa!"));
			} else if (player.getBank().hasSpaceFor((new Item(13323)))) {
				player.getBank().add((new Item(13323)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Baby chinchompa while hunting!");
		}
			}
	public static void handlePetBlackChin(Player player) {
		int random = Utility.randomNumber(4000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(13325))) {
				player.getInventory().add(new Item(13325));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa!"));
			} else if (player.getBank().hasSpaceFor((new Item(13325)))) {
				player.getBank().add((new Item(13325)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Baby chinchompa while hunting!");
		}
			}
	public static void handlePetYellowChin(Player player) {
		int random = Utility.randomNumber(3500);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(13326))) {
				player.getInventory().add(new Item(13326));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa!"));
			} else if (player.getBank().hasSpaceFor((new Item(13326)))) {
				player.getBank().add((new Item(13326)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Baby chinchompa! It has been sent to your bank."));
			}
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Baby chinchompa while hunting!");
		}
			}
	
}