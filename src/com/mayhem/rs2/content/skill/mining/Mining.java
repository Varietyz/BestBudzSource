package com.mayhem.rs2.content.skill.mining;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.Task.BreakType;
import com.mayhem.core.task.Task.StackType;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.challenges.DailyChallenges;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.EquipmentConstants;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class Mining {

	private final static Set<Location> DEAD_ORES = new HashSet<>();

	public static void declare() {
		Pickaxe.declare();
		Ore.declare();
	}

	public static enum Pickaxe {
		BRONZE_PICKAXE(1265, 1, 10, new Animation(6753)),
		IRON_PICKAXE(1267, 1, 9, new Animation(6754)),
		STEEL_PICKAXE(1269, 6, 8, new Animation(6755)),
		MITHRIL_PICKAXE(1273, 21, 7, new Animation(6757)),
		ADAMANT_PICKAXE(1271, 31, 6, new Animation(6756)),
		RUNE_PICKAXE(1275, 41, 5, new Animation(6752)),
		DRAGON_PICKAXE(11920, 61, 4, new Animation(6758)),
		INFERNAL(13243, 61, 3, new Animation(4481)),
		THIRD_AGE(20014, 61, 2, new Animation(7282)),
		DRAGON_PICKAXE_OR(12797, 61, 1, new Animation(335));

		private final int item;
		private final int level;
		private final int weight;
		private final Animation animation;

		private static final HashMap<Integer, Pickaxe> PICKAXES = new HashMap<>();

		public static void declare() {
			for (Pickaxe pickaxe : values()) {
				PICKAXES.put(pickaxe.item, pickaxe);
			}
		}

		private Pickaxe(int item, int level, int weight, Animation animation) {
			this.item = item;
			this.level = level;
			this.animation = animation;
			this.weight = weight;
		}

		public int getItem() {
			return item;
		}

		public int getLevel() {
			return level;
		}

		public Animation getAnimation() {
			return animation;
		}

		public int getWeight() {
			return weight;
		}

		public static Pickaxe get(Player player) {
			Pickaxe highest = null;

			Queue<Pickaxe> picks = new PriorityQueue<>((first, second) -> second.getLevel() - first.getLevel());

			if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT] != null) {
				highest = PICKAXES.get(player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId());

				if (highest != null) {
					picks.add(highest);
					highest = null;
				}
			}

			for (Item item : player.getInventory().getItems()) {
				if (item == null) {
					continue;
				}

				Pickaxe pick = PICKAXES.get(item.getId());

				if (pick == null) {
					continue;
				}

				picks.add(pick);
			}

			Pickaxe pick = picks.poll();

			if (pick == null) {
				return null;
			}

			while (player.getLevels()[Skills.MINING] < pick.getLevel()) {
				if (highest == null) {
					highest = pick;
				}

				pick = picks.poll();
			}
			;

			return pick;
		}
	}

	public static enum Ore {
		COPPER("Copper ore", new int[] { 7453, 7484 }, 1, 17.5, new int[] { 436 }, 10081, 6, 4),
		TIN("Tin ore", new int[] { 7485, 7486 }, 1, 17.5, new int[] { 438 }, 10081, 6, 4),
		CLAY("Clay", new int[] { 7487, 7454 }, 1, 10, new int[] { 434 }, 10081, 6, 4),
		IRON("Iron ore", new int[] { 7455, 7488 }, 15, 35, new int[] { 440 }, 10081, 12, 7),
		BLURITE_ORE("Blurite ore", new int[] { 7495 }, 20, 20, new int[] { 668 }, 10081, 8, 6),
		SILVER_ORE("Silver ore", new int[] { 7457, 7490 }, 20, 40, new int[] { 442 }, 10081, 15, 10),
		COAL_ORE("Coal ore", new int[] { 7489, 7456 }, 30, 50, new int[] { 453 }, 10081, 12, 10),
		GOLD_ORE("Gold ore", new int[] { 7458, 7491 }, 40, 65, new int[] { 444 }, 10081, 15, 10),
		MITHRIL_ORE("Mithril ore", new int[] { 7459, 7492 }, 55, 80, new int[] { 447 }, 10081, 15, 13),
		ADAMANT_ORE("Adamant ore", new int[] { 7460, 7493 }, 70, 95, new int[] { 449 }, 10081, 15, 16),
		RUNITE_ORE("Runite ore", new int[] { 7494, 7461 }, 85, 125, new int[] { 451 }, 10081, 15, 18),
		LOVAKITE_ORE("Lovakite ore", new int[] { 28596, 28597 }, 60, 85, new int[] { 13356 }, 10081, 15, 14),
		ESSENCE("Essence", new int[] { 7471, 2491 }, 30, 10, new int[] { 1436 }, -1, -1, -1),
		MOTHERLOAD_("Motherload", new int[] { 26661, 26662, 26663, 26660 }, 70, 55, new int[] { 12011 }, -1, -1, -1),
		//GEM_ROC("Gem Rock", new int[] { 7470 }, 40, 65,
		//		new int[] { 1625, 1627, 1629, 1623, 1621, 1619, 1617 }, 10081, 135, 140),
		GEM_ROCK("Gem Rock", new int[] { 27886 }, 40, 65, new int[] { 1625, 1627, 1629, 1623, 1621, 1619, 1617 }, 10081, 15, 14);

		private final String name;
		private int[] objects;
		private final int level;
		private final double exp;
		private final int[] ore;
		private final int replacement;
		private final int respawn;
		private final int immunity;

		private static final HashMap<Integer, Ore> ORES = new HashMap<>();

		public static void declare() {
			for (Ore ore : values()) {
				for (int object : ore.objects) {
					ORES.put(object, ore);
				}
			}
		}

		private Ore(String name, int[] objects, int level, double exp, int[] ore, int replacement, int respawn,
				int immunity) {
			this.name = name;
			this.objects = objects;
			this.level = level;
			this.exp = exp;
			this.ore = ore;
			this.replacement = replacement;
			this.respawn = respawn;
			this.immunity = immunity;
		}

		public String getName() {
			return name;
		}

		public int getLevel() {
			return level;
		}

		public double getExp() {
			return exp;
		}

		public int[] getOre() {
			return ore;
		}

		public int getReplacement() {
			return replacement;
		}

		public int getRespawn() {
			return respawn;
		}

		public int getImmunity() {
			return immunity;
		}

		public static Ore get(int id) {
			return ORES.get(id);
		}
	}

	public static boolean clickRock(Player player, GameObject object) {
		if (player.getSkill().locked() || object == null) {
			return false;
		}

		Ore ore = Ore.get(object.getId());

		if (ore == null) {
			return false;
		}

		if (player.getLevels()[Skills.MINING] < ore.getLevel()) {
			DialogueManager.sendStatement(player,
					"You need a Mining level of " + ore.getLevel() + " to mine that ore.");
			return false;
		}

		Pickaxe pickaxe = Pickaxe.get(player);

		if (pickaxe == null) {
			DialogueManager.sendStatement(player, "You don't have a pickaxe.");
			return false;
		}

		if (player.getLevels()[Skills.MINING] < pickaxe.getLevel()) {
			player.send(new SendMessage("You need a Mining level of " + pickaxe.getLevel() + " to use that pickaxe."));
			DialogueManager.sendStatement(player,
					"You need a Mining level of " + pickaxe.getLevel() + " to use that pickaxe.");
			return false;
		}

		if (player.getCombat().inCombat() || player.getCombat().getAttacking() != null) {
			player.send(new SendMessage("You can't do that right now!"));
			return false;
		}

		if (player.getInventory().getTakenSlots() == 28) {
			DialogueManager.sendStatement(player, "Your inventory is full!");
			return false;
		}

		player.send(new SendMessage("You swing your pick at the rock."));

		int ticks = ore.immunity == -1 ? 2
				: ore.getImmunity() - (int) ((player.getLevels()[Skills.MINING] - ore.getLevel()) * 2
						/ (double) pickaxe.getWeight());
		int gemTick = ore.getImmunity();

		if (ticks < 1) {
			ticks = 1;
		}

		int time = ore.getName().equalsIgnoreCase("gem rock") ? gemTick : ticks;

		TaskQueue.queue(
				new Task(player, 1, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION) {
					int ticks = 0;

					@Override
					public void execute() {
						if ((ore == Ore.ESSENCE) || (ore == Ore.MOTHERLOAD_)) {
							if (player.getInventory().getFreeSlots() == 0) {
								DialogueManager.sendStatement(player, "Your inventory is full!");
								stop();
								return;
							}
						}
						if (ticks++ == time || DEAD_ORES.contains(object.getLocation())) {
							if ((ore == Ore.ESSENCE) || (ore == Ore.MOTHERLOAD_)) {
								//handleBloodMoney(player);
								player.getInventory().add(ore.getOre()[Utility.random(ore.getOre().length - 1)], 1);
								player.getSkill().addExperience(Skills.MINING, ore.getExp());
								//handleRandom(player);
								//handleDwarf(player);
								player.skillPoints += 10;
								handleRandom1(player);
								handleClueBox(player);
								AchievementHandler.activate(player, AchievementList.MINE_1000_ROCKS, 1);
								ticks = 0;
								if (player.getInventory().getFreeSlots() == 0) {
									DialogueManager.sendStatement(player, "Your inventory is full!");
									stop();
								}
								return;
							} else {
								stop();
								return;
							}
						}

						player.getUpdateFlags().sendAnimation(pickaxe.getAnimation());
					}

					@Override
					public void onStop() {
						player.getUpdateFlags().sendAnimation(new Animation(65535));
						if (time + 1 == ticks) {
							if ((ore != Ore.ESSENCE) || (ore != Ore.MOTHERLOAD_)) {
								handleBloodMoney(player);
								player.getInventory().add(ore.getOre()[Utility.randomNumber(ore.getOre().length)], 1);
								player.getSkill().addExperience(Skills.MINING, ore.getExp());
								if (ore == Ore.ADAMANT_ORE) {
									player.getDailyChallenges().deincrementChallenge(DailyChallenges.ADAMANT_MINING);
								}
								if (ore == Ore.RUNITE_ORE) {
									player.getDailyChallenges().deincrementChallenge(DailyChallenges.RUNITE_MINING);
								}
								handleRandom(player);
								handleDwarf(player);
								handleRandom1(player);
								player.skillPoints += 50;
								handleClueBox(player);
								handleKarGloves1(player);
								handleKarGloves2(player);
								handleKarGloves3(player);
								AchievementHandler.activate(player, AchievementList.MINE_1000_ROCKS, 1);
							}
							if (ore.getReplacement() > 0) {
								ObjectManager.registerObject(new GameObject(ore.getReplacement(), object.getLocation(),
										object.getType(), object.getFace(), false));
								DEAD_ORES.add(object.getLocation());

								TaskQueue.queue(new Task(player, ore.getRespawn(), false, StackType.STACK,
										BreakType.NEVER, TaskIdentifier.MINING_ROCK) {
									@Override
									public void execute() {
										stop();
									}

									@Override
									public void onStop() {
										DEAD_ORES.remove(object.getLocation());
										ObjectManager.registerObject(new GameObject(object.getId(),
												object.getLocation(), object.getType(), object.getFace(), false));
									}
								});
							}
						}
					}
				});

		return true;
	}

	public static void main(String[] args) {
		int pickaxe = Pickaxe.RUNE_PICKAXE.getWeight();
		int ore_req = 1;
		int immunity = 4;

		//System.out.println("Immunity: " + immunity + " [" + (int) (immunity * 5 / 3.0) + "s]");

		for (int i = ore_req; i < 100; i++) {

			int result = immunity - (int) ((i - ore_req) * 2 / (double) pickaxe);

			if (result <= 2) {
				//System.out.println("Level: " + i + " = " + result + " [" + (int) (result * 5 / 3.0) + "s]");
				break;
			}

			//System.out.println("Level: " + i + " = " + result + " [" + (int) (result * 5 / 3.0) + "s]");
		}
	}
	
	public static void handleRandom(Player player) {
		int random = Utility.randomNumber(6000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(13321))) {
				player.getInventory().add(new Item(13321));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Rock golem!"));
			} else if (player.getBank().hasSpaceFor((new Item(13321)))) {
				player.getBank().add((new Item(13321)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a pet Rock golem! It has been sent to your bank."));
			}
				World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Rock golem while mining!");
			}
		}
	
	public static void handleDwarf(Player player) {
		int random = Utility.randomNumber(4000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(7500))) {
				player.getInventory().add(new Item(7500));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Dwarf follower!"));
			} else if (player.getBank().hasSpaceFor((new Item(7500)))) {
				player.getBank().add((new Item(7500)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Dwarf follower! It has been sent to your bank."));
			}
				World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a pet Dwarf follower while mining!");
			}
		}
	
	public static void handleBloodMoney(Player player) {
		if (player.getInventory().hasSpaceFor(new Item(13307))) {
			player.getInventory().add(new Item(13307, Utility.randomNumber(15)));
		} else if (player.getBank().hasSpaceFor((new Item(13307)))) {
			player.getBank().add((new Item(13307, Utility.randomNumber(15))));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive some blood money! It has been sent to your bank."));
		}
			}
	
	
	public static void handleRandom1(Player player) {
		int random = Utility.randomNumber(500);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(995, 100000))) {
				player.getInventory().add(new Item(995, 100000));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You find some gold hidden beside the rocks!"));
			} else if (player.getBank().hasSpaceFor((new Item(995, 100000)))) {
				player.getBank().add((new Item(995, 100000)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You find some gold hidden beside the rocks! It has been sent to your bank."));
			}
		}
	}
	public static void handleClueBox(Player player) {
		int random = Utility.randomNumber(800);
		if (random == 1) {
		if (player.getInventory().hasSpaceFor(new Item(12789))) {
			player.getInventory().add(new Item(12789, 1));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Clue box!"));
		} else if (player.getBank().hasSpaceFor((new Item(12789)))) {
			player.getBank().add((new Item(12789, 1)));
			player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Clue box! It has been sent to your bank."));
		}
			}
	}
	public static void handleKarGloves1(Player player) {

		int random = Utility.randomNumber(600);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(11136, 1)) {
				player.getEquipment().remove(new Item(11136, 1));
				player.getEquipment().equip(new Item(11137), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Karamja gloves has been upgraded!")); }
			}
		}

	public static void handleKarGloves2(Player player) {

		int random = Utility.randomNumber(1000);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(11137, 1)) {
				player.getEquipment().remove(new Item(11137, 1));
				player.getEquipment().equip(new Item(11138), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Karamja gloves has been upgraded!")); }
		}
	}
	public static void handleKarGloves3(Player player) {

		int random = Utility.randomNumber(3000);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(11138, 1)) {
				player.getEquipment().remove(new Item(11138, 1));
				player.getEquipment().equip(new Item(11139), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Karamja gloves has been upgraded!")); }
		}
	}
}